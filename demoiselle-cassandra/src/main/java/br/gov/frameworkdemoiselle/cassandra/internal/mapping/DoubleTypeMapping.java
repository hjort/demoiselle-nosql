package br.gov.frameworkdemoiselle.cassandra.internal.mapping;

public class DoubleTypeMapping extends AbstractStringBasedTypeMapping<Double> {

	protected String asString(final Double value) {
		return value.toString();
	}

	protected Double fromString(final String string) {
		return Double.valueOf(string);
	}

}
