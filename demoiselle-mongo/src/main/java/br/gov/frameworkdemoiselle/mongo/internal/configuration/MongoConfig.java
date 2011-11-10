package br.gov.frameworkdemoiselle.mongo.internal.configuration;

import java.io.Serializable;

import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration // TODO: (prefix = "mongodb")
public class MongoConfig implements Serializable {

	private static final long serialVersionUID = -3857708873956980150L;

	@Name("mongodb.server.host") // TODO: remover
	private String serverHost = "localhost";

	@Name("mongodb.server.port") // TODO: remover
	private int serverPort = 27017;

	@Name("mongodb.default.database") // TODO: remover
	private String defaultDatabase = "mydb";
	
	public String getServerHost() {
		return serverHost;
	}

	public int getServerPort() {
		return serverPort;
	}
	
	public String getDefaultDatabase() {
		return defaultDatabase;
	}

}
