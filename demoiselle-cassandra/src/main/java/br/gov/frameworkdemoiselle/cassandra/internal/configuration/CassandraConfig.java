package br.gov.frameworkdemoiselle.cassandra.internal.configuration;

import org.apache.cassandra.thrift.ConsistencyLevel;

import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(prefix = "frameworkdemoiselle.cassandra")
public class CassandraConfig {

	private String[] serverNodes;

	private String defaultKeyspace;

	private String defaultConsistency = ConsistencyLevel.QUORUM.name();

	private boolean serializeUnknown;

	public String[] getServerNodes() {
		return serverNodes;
	}

	public String getDefaultKeyspace() {
		return defaultKeyspace;
	}

	public ConsistencyLevel getDefaultConsistency() {
		return ConsistencyLevel.valueOf(defaultConsistency);
	}

	public boolean isSerializeUnknown() {
		return serializeUnknown;
	}

}
