package br.gov.frameworkdemoiselle.mongo.internal.configuration;

import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(prefix = "frameworkdemoiselle.mongo")
public class MongoConfig {

	private String serverHost = "localhost";

	private int serverPort = 27017;

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
