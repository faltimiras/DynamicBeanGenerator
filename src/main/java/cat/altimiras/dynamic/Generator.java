package cat.altimiras.dynamic;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Collectors;

public class Generator {

	public Class create(ClassDef definition) throws Exception {
		return this.create(definition, this.getClass().getClassLoader());
	}

	public Class create(ClassDef definition, ClassLoader classLoader) throws Exception {

		//create the class
		DynamicType.Builder builder = new ByteBuddy()
				.subclass(Object.class, ConstructorStrategy.Default.NO_CONSTRUCTORS)
				.name(definition.getFullname());

		//add class annotations
		for (AnnotationDef annotation : definition.getAnnotations()) {
			builder = builder.annotateType(generateAnnotationClass(annotation, classLoader));
		}

		//add fields
		for (FieldDef fdef : definition.getFields()) {

			Class type;
			if (fdef.isSimple()) {
				type = Class.forName(fdef.getType());
			} else {
				type = create(fdef.getNested(), classLoader);
			}

			//define the fields
			String capitalized = capitalize(fdef.getName());
			DynamicType.Builder.FieldDefinition builderField = builder
					.defineField(fdef.getName(), type, Visibility.PRIVATE);

			//add field annotations
			for (AnnotationDef annotation : fdef.getAnnotations()) {
				builderField = builderField.annotateField(generateAnnotationClass(annotation, classLoader));
			}

			//add the getters and the setters following java standard
			builder = (DynamicType.Builder) builderField;
			builder = builder
					.defineMethod("get" + capitalized, type, Visibility.PUBLIC)
					.intercept(FieldAccessor.ofBeanProperty())
					.defineMethod("set" + capitalized, void.class, Visibility.PUBLIC)
					.withParameters(type)
					.intercept(FieldAccessor.ofBeanProperty());
		}

		MapToVar mapToVar = new MapToVar();

		//add empty constructors
		builder = builder
				//empty constructor
				.defineConstructor(Visibility.PUBLIC)
				.intercept(MethodCall.invoke(Object.class.getDeclaredConstructor()))
				//constructor that accept a Map and delegates it to mapToVar
				.defineConstructor(Visibility.PUBLIC)
				.withParameter(Map.class)
				.intercept(MethodCall.invoke(Object.class.getDeclaredConstructor()).andThen(MethodDelegation.to(mapToVar)));

		//build the class
		Class theClass = builder.make()
				.load(classLoader, ClassLoadingStrategy.Default.INJECTION)
				.getLoaded();

		//initialize the constructor implementation once we have the class built
		mapToVar.init(theClass, definition.getFields().stream().map(FieldDef::getName).collect(Collectors.toList()));
		return theClass;
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
