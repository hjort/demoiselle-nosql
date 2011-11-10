package br.gov.frameworkdemoiselle.cassandra.internal.mapping;

public class LongTypeMapping extends AbstractStringBasedTypeMapping<Long> {

	protected String asString(final Long value) {
		return value.toString();
	}

	protected Long fromString(final String string) {
		return Long.valueOf(string);
	}

}
