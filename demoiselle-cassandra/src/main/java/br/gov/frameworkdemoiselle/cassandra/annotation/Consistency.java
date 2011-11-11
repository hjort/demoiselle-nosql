package br.gov.frameworkdemoiselle.cassandra.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.cassandra.thrift.ConsistencyLevel;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Consistency {

	ConsistencyLevel value() default ConsistencyLevel.QUORUM;
	
}
