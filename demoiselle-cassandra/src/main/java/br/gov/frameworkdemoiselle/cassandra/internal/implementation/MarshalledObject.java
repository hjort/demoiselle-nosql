package br.gov.frameworkdemoiselle.cassandra.internal.implementation;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import br.gov.frameworkdemoiselle.cassandra.exception.CassandraException;

import com.google.common.collect.Maps;

public class MarshalledObject {

	private byte[] key;
	private byte[] superColumn;
	private final Map<String, byte[]> values = Maps.newHashMap();

	public static MarshalledObject create() {
		return new MarshalledObject();
	}

	public void setKey(final byte[] value) {
		key = value;
	}

	public void addValue(final String name, final byte[] value) {
		if (values.put(name, value) != null) {
			throw new CassandraException("Property with name " + name
					+ " had already" + " a value, overwriting is illegal");
		}
	}

	public byte[] getKey() {
		return key;
	}

	public Set<Map.Entry<String, byte[]>> getEntries() {
		return Collections.unmodifiableSet(values.entrySet());
	}

	public void setSuperColumn(final byte[] superColumn) {
		this.superColumn = superColumn;
	}

	public byte[] getSuperColumn() {
		return superColumn;
	}

	public boolean isSuperColumnPresent() {
		return superColumn != null;
	}

}
