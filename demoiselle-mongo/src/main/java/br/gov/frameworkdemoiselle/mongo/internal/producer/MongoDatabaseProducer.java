package br.gov.frameworkdemoiselle.mongo.internal.producer;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.mongo.internal.implementation.MongoDB;

import com.mongodb.Mongo;

//@SessionScoped
public class MongoDatabaseProducer /*implements Serializable*/ {

	private static final long serialVersionUID = -68512646248934348L;

	@Inject
	private Mongo mongo;

//	@Inject
//	private MongoConfig config;

	@Inject
	private Logger logger;

	@Produces
	@Alternative
	@Default
//	@RequestScoped
	public MongoDB create() {
		
		final String dbname = "mydb";
		
//		DB db = mongo.getDB(dbname);
		MongoDB db = new MongoDB(mongo.getDB(dbname));
		logger.info("Session established on database [" + db + "]");

		db.requestStart();
		return db;
	}

	void destroy(@Disposes MongoDB db) {
		logger.info("Releasing session on database [" + db + "]");
		db.requestDone();
	}
	
	/*
	@Produces
	public DB create(InjectionPoint point) {
		
		String dbname;
		Annotated annotated = point.getAnnotated();
		if (annotated.isAnnotationPresent(Name.class)) {
			dbname = annotated.getAnnotation(Name.class).value();
		} else {
			dbname = config.getDefaultDatabase();
		}
		
		DB db = mongo.getDB(dbname);
		logger.info("Session established on database [" + db + "]");

		db.requestStart();
		return db;
	}

	void destroy(@Disposes DB db) {
		logger.info("Releasing session on database [" + db + "]");
		db.requestDone();
	}
	*/
	
}
