package br.gov.frameworkdemoiselle.cassandra.internal.mapping;

public class ShortTypeMapping extends AbstractStringBasedTypeMapping<Short> {

	protected String asString(final Short value) {
		return value.toString();
	}

	protected Short fromString(final String string) {
		return Short.valueOf(string);
	}

}
