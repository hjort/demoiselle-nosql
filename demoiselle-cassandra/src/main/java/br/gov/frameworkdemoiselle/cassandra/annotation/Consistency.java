package br.gov.frameworkdemoiselle.cassandra.annotation;

import org.apache.cassandra.thrift.ConsistencyLevel;

public @interface Consistency {

	ConsistencyLevel value() default ConsistencyLevel.QUORUM;
	
}
