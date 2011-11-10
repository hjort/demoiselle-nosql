package br.gov.frameworkdemoiselle.cassandra.internal.mapping;

public class IntegerTypeMapping extends AbstractStringBasedTypeMapping<Integer> {

	protected String asString(final Integer value) {
		return value.toString();
	}

	protected Integer fromString(final String string) {
		return Integer.valueOf(string);
	}

}
