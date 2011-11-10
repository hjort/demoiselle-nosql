package br.gov.frameworkdemoiselle.cassandra.internal.mapping;

public abstract class AbstractStringBasedTypeMapping<T> implements TypeMapping<T> {
	
	private final static StringTypeMapping STRING_MAPPING = new StringTypeMapping();

	public T fromBytes(final byte[] value) {
		if (value.length == 0) {
			return null;
		}
		return fromString(STRING_MAPPING.fromBytes(value));
	}

	@SuppressWarnings("unchecked")
	public byte[] toBytes(final Object value) {
		if (value == null) {
			return new byte[0];
		}
		return STRING_MAPPING.toBytes(asString((T) value));
	}

	protected abstract T fromString(String string);

	protected abstract String asString(T value);

}