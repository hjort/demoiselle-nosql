package br.gov.frameworkdemoiselle.cassandra.internal.mapping;

public class ByteTypeMapping extends AbstractStringBasedTypeMapping<Byte> {

	protected String asString(final Byte value) {
		return value.toString();
	}

	protected Byte fromString(final String string) {
		return Byte.valueOf(string);
	}

}
