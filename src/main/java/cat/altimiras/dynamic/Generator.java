package cat.altimiras.dynamic;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.annotation.Annotation;

public class Generator {


	public Class create(ClassDef definition, ClassLoader classLoader) throws Exception {

		DynamicType.Builder builder = new ByteBuddy()
				.subclass(Object.class)
				.name(definition.getFullname());

		for (AnnotationDef annotation : definition.getAnnotations()) {
			builder = builder.annotateType(generateAnnotationClass(annotation, classLoader));
		}

		for (FieldDef fdef : definition.getFields()) {

			Class type;
			if (fdef.isSimple()) {
				type = Class.forName(fdef.getType());
			} else {
				type = create(fdef.getNested(), classLoader);
			}

			String capitalized = capitalize(fdef.getName());
			DynamicType.Builder.FieldDefinition builderField = builder
					.defineField(fdef.getName(), type, Visibility.PRIVATE);

			for (AnnotationDef annotation : fdef.getAnnotations()) {
				builderField = builderField.annotateField(generateAnnotationClass(annotation, classLoader));
			}
			builder = (DynamicType.Builder)builderField;
			builder = builder.defineMethod("get" + capitalized, type, Visibility.PUBLIC)
					.intercept(FieldAccessor.ofBeanProperty())
					.defineMethod("set" + capitalized, void.class, Visibility.PUBLIC)
					.withParameters(type)
					.intercept(FieldAccessor.ofBeanProperty());
		}

		return builder.make()
				.load(classLoader, ClassLoadingStrategy.Default.INJECTION)
				.getLoaded();
	}

	private Annotation generateAnnotationClass(AnnotationDef<?> annotationDef, ClassLoader classLoader) throws Exception {

		Class annotationClass = Class.forName(annotationDef.getFullName());
		DynamicType.Builder builder = new ByteBuddy()
				.subclass(annotationClass)
				.name(annotationDef.getFullName() + "Impl")
				.method(ElementMatchers.named("annotationType")).intercept(FixedValue.value(annotationClass));

		for (AnnotationFieldsDef fdef : annotationDef.getFields()) {
			builder = builder.method(ElementMatchers.named(fdef.getName()))
					.intercept(FixedValue.value(fdef.getValue()));
		}

		Class annotationImplClass = builder.make()
				.load(classLoader, ClassLoadingStrategy.Default.INJECTION)
				.getLoaded();
		return (Annotation) annotationImplClass.getConstructor().newInstance();

	}

	private String capitalize(String name) {
		if (name == null || name.isEmpty()) {
			return name;
		}
		final int firstCodepoint = name.codePointAt(0);
		final int newCodePoint = Character.toTitleCase(firstCodepoint);
		if (firstCodepoint == newCodePoint) {
			return name;
		}

		final int newCodePoints[] = new int[name.length()]; // cannot be longer than the char array
		int outOffset = 0;
		newCodePoints[outOffset++] = newCodePoint; // copy the first codepoint
		for (int inOffset = Character.charCount(firstCodepoint); inOffset < name.length(); ) {
			final int codepoint = name.codePointAt(inOffset);
			newCodePoints[outOffset++] = codepoint; // copy the remaining ones
			inOffset += Character.charCount(codepoint);
		}
		return new String(newCodePoints, 0, outOffset);

	}
}
