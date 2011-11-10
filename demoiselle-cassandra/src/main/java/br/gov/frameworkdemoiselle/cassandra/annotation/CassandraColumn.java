package br.gov.frameworkdemoiselle.cassandra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.cassandra.thrift.ConsistencyLevel;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CassandraColumn {

	String keyspace() default "";

	String columnFamily();

	String secondaryColumnFamily() default "";

	ConsistencyLevel consistency() default ConsistencyLevel.QUORUM;

}
