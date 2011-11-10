package br.gov.frameworkdemoiselle.mongo.internal.implementation;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.mongo.internal.configuration.MongoConfig;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class MongoDB /*implements Serializable*/ {

//	private static final long serialVersionUID = 4074740540668280022L;
	
	private DB db;
	
	@Inject
	private Mongo mongo;

	@Inject
	private MongoConfig config;

	@Inject
	private Logger logger;

	public MongoDB() {
		System.out.println("MongoDB.MongoDB()");
	}
	
	public MongoDB(DB db) {
		this.db = db;
		System.out.println("MongoDB.MongoDB()");
	}

	@PostConstruct
	public void init() {
		logger.info(this + ".init()");
		
		if (db != null)
			return;
		
		db = mongo.getDB(config.getDefaultDatabase());
	}
	
	public DBCollection getCollection(String name) {
		return db.getCollection(name);
	}
	
	public void requestStart() {
		db.requestStart();
		logger.info(db.toString());
	}

	public void requestDone() {
		db.requestDone();
		logger.info(db.toString());
	}

	public String toString() {
		return "MongoDB [db=" + db + "]";
	}

}
