package br.gov.frameworkdemoiselle.cassandra.annotation;

public @interface ColumnFamily {

	String value() default "";
	
}
