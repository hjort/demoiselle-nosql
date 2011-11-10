package br.gov.frameworkdemoiselle.mongo.internal.producer;

import java.net.UnknownHostException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.mongo.internal.configuration.MongoConfig;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

//@SessionScoped
public class MongoConnectorProducer /*implements Serializable*/ {

	private static final long serialVersionUID = -4797709537316929035L;

	@Inject
	private MongoConfig config;

	@Inject
	private Logger logger;

	@Produces
	@ApplicationScoped
	public Mongo create() throws UnknownHostException, MongoException {
		logger.info("Creating connection for MongoDB...");

		// connect to the database server
		Mongo mongo = new Mongo(config.getServerHost(), config.getServerPort());

		logger.info("Connection created [" + mongo + "]");
		return mongo;
	}

	void destroy(@Disposes Mongo mongo) {
		logger.info("Releasing connection [" + mongo + "]");
		mongo.close();
	}

}
