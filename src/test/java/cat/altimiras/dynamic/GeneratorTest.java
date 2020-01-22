package cat.altimiras.dynamic;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GeneratorTest {

	@Test
	public void basic() throws Exception {

		ClassDef classDef = new ClassDef("cat.altimiras.Test")
				.addField("value1", "String").build();

		Generator generator = new Generator();
		Class testClass = generator.create(classDef, this.getClass().getClassLoader());

		Object o = testClass.getConstructor().newInstance();

		Method getter = testClass.getMethod("getValue1");
		Method setter = testClass.getMethod("setValue1", String.class);

		setter.invoke(o, "lala");
		String result = (String) getter.invoke(o);

		assertEquals("lala", result);
		assertEquals("cat.altimiras.Test", testClass.getName());
	}

	@Test
	public void nested() throws Exception {

		/**
		 * Test {
		 * 	 val : Nested {
		 * 	 		value1 : String
		 *        }
		 *     }
		 */

		//Prepare class definition
		ClassDef classDef = new ClassDef("cat.altimiras.Parent")
				.addField("val", new ClassDef("cat.altimiras.Child")
						.addField("value1", "String").build()).build();

		//generate the class dynamically
		Generator generator = new Generator();
		Class testClass = generator.create(classDef, this.getClass().getClassLoader());

		//validate
		Class nestedClass = Class.forName("cat.altimiras.Child");

		Object o = testClass.getConstructor().newInstance(); //create instance of Test
		Method getter = testClass.getMethod("getVal");
		Method setter = testClass.getMethod("setVal", nestedClass);

		Object nestedObj = nestedClass.getConstructor().newInstance(); //create instance of Nested
		Method getterNested = nestedClass.getMethod("getValue1");
		Method setterNested = nestedClass.getMethod("setValue1", String.class);

		//set values to Test and Nested instance
		setterNested.invoke(nestedObj, "lala"); //set value to value1 propety in the Nested instance
		setter.invoke(o, nestedObj); //set Nested instance into Test

		//get the values
		Object result = getter.invoke(o); //get the Nested instance from Test instance
		String nestedFieldValue = (String) getterNested.invoke(result); //get the value1 value from Nested instance

		assertEquals(nestedObj, result);
		assertEquals("lala", nestedFieldValue);
	}

	@Test
	public void annotations() throws Exception {

		ClassDef classDef = new ClassDef("cat.altimiras.AnnotatedClass")
				.addAnnotation("cat.altimiras.dynamic.GeneratorTest$TestAnnotation")
					.addAnnotationField("val", "annotation-value").build()
				.addField("value1", "String").build();

		Generator generator = new Generator();
		Class testClass = generator.create(classDef, this.getClass().getClassLoader());


		assertEquals("cat.altimiras.AnnotatedClass", testClass.getName());

		assertEquals(1, testClass.getAnnotations().length);
		Annotation annotation = testClass.getAnnotations()[0];
		Class annotationClass = annotation.annotationType();
		assertEquals("cat.altimiras.dynamic.GeneratorTest$TestAnnotation", annotationClass.getName());
		Method valMethod = annotationClass.getMethod("val");

		assertEquals("annotation-value", valMethod.invoke(annotation));
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface TestAnnotation {
		String val() default "";
	}

	@Test
	public void mapConstructor() throws Exception {

		ClassDef classDef = new ClassDef("cat.altimiras.TestWithMap")
				.addField("value1", "integer").build();

		Generator generator = new Generator();
		Class testClass = generator.create(classDef, this.getClass().getClassLoader());

		Map<String, Object> param = new HashMap<>(1);
		param.put("value1", 3);

		Object o = testClass.getConstructor(Map.class).newInstance(param);


		Method getter = testClass.getMethod("getValue1");
		Integer result = (Integer) getter.invoke(o);

		assertEquals(Integer.valueOf(3), result);
		assertEquals("cat.altimiras.TestWithMap", testClass.getName());
	}
}