package cat.altimiras.dynamic.integration.db;

import cat.altimiras.dynamic.ClassDef;
import cat.altimiras.dynamic.Generator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

import javax.persistence.Index;
import javax.persistence.UniqueConstraint;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Integration test. Tests than generated class can be used to store a complex bean (Multiple tables) into a DB
 */
public class HibernateTest {

	@Test
	public void singleTable() throws Exception {

		ClassDef personDef = new ClassDef("cat.altimiras.Person")
				.addAnnotation("javax.persistence.Entity")
					.addAnnotationField("name", "")
					.build()
				.addAnnotation("javax.persistence.Table")
					.addAnnotationField("name", "p")
					.addAnnotationField("catalog", "")
					.addAnnotationField("uniqueConstraints", new UniqueConstraint[]{})
					.addAnnotationField("schema", "")
					.addAnnotationField("indexes", new Index[]{})

					.build()
				.addField("name", "String")
					.addAnnotation("javax.persistence.Id").build()
				.build()
				.addField("surname", "String").build()
				.addField("height", "integer").build();

		Generator generator = new Generator();
		Class personClass = generator.create(personDef, this.getClass().getClassLoader());

		Object personInstance = personClass.getConstructor().newInstance();
		setValue(personInstance, "name", "Leo");
		setValue(personInstance, "surname", "Messi");
		setValue(personInstance, "height", 170);

		SessionFactory sessionFactory = setUpHibernate(personClass);

		store(sessionFactory, personInstance);

		List personsNames = sessionFactory.openSession().createQuery("Select pe.name from Person pe").getResultList();

		assertEquals("Leo", personsNames.get(0));
	}

	private SessionFactory setUpHibernate(Class... classes) throws Exception {
		Configuration configuration = new Configuration();
		for (Class c : classes) {
			configuration.addAnnotatedClass(c);
		}

		SessionFactory sessionFactory = configuration
				.configure()
				.buildSessionFactory();

		return sessionFactory;
	}

	private void store(SessionFactory sessionFactory, Object... objects) {
		try (Session session = sessionFactory.openSession()) {
			session.beginTransaction();
			for (Object o : objects) {
				session.save(o);
			}
			session.getTransaction().commit();
		}
	}

	private void setValue(Object o, String field, Object value) throws Exception {
		Field f = o.getClass().getDeclaredField(field);
		f.setAccessible(true);
		f.set(o, value);
	}

}
