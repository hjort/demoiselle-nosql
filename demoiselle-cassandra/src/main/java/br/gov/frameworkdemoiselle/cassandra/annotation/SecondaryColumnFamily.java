package br.gov.frameworkdemoiselle.cassandra.annotation;

public @interface SecondaryColumnFamily {

	String value() default "";

}
