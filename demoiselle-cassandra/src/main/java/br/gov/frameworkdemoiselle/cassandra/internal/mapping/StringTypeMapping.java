package br.gov.frameworkdemoiselle.cassandra.internal.mapping;

import java.nio.charset.Charset;

public class StringTypeMapping implements TypeMapping<String> {

	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	public String fromBytes(final byte[] value) {
		if (value.length == 0) {
			return null;
		}
		return new String(value, DEFAULT_CHARSET);
	}

	public byte[] toBytes(final Object value) {
		if (value == null) {
			return new byte[0];
		}
		return ((String) value).getBytes(DEFAULT_CHARSET);
	}

}
