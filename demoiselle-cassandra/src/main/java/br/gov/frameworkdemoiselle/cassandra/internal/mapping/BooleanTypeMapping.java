package br.gov.frameworkdemoiselle.cassandra.internal.mapping;

public class BooleanTypeMapping extends AbstractStringBasedTypeMapping<Boolean> {

	protected String asString(final Boolean value) {
		return value.toString();
	}

	protected Boolean fromString(final String string) {
		return Boolean.valueOf(string);
	}

}
