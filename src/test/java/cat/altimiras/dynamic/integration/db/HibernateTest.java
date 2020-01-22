package cat.altimiras.dynamic.integration.db;

import cat.altimiras.dynamic.ClassDef;
import cat.altimiras.dynamic.Generator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

import javax.persistence.Index;
import javax.persistence.UniqueConstraint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


		Map<String, Object> messi = new HashMap<>(3);
		messi.put("name", "Leo");
		messi.put("surname", "Messi");
		messi.put("height", 170);

		Object personInstance = personClass.getConstructor(Map.class).newInstance(messi);

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
}
