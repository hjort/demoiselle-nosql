package br.gov.frameworkdemoiselle.cassandra.internal.implementation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import br.gov.frameworkdemoiselle.cassandra.exception.CassandraException;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.TypeMapping;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

public class TypeConverter {

	private static final byte[] EMPTY_BYTES = new byte[0];

	private final ImmutableMap<Class<?>, TypeMapping<?>> mappings;
	private final boolean serializeUnknown;

	private final static Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
	static {
	    map.put(boolean.class, Boolean.class);
	    map.put(byte.class, Byte.class);
	    map.put(short.class, Short.class);
	    map.put(char.class, Character.class);
	    map.put(int.class, Integer.class);
	    map.put(long.class, Long.class);
	    map.put(float.class, Float.class);
	    map.put(double.class, Double.class);
	}

	public TypeConverter(final ImmutableMap<Class<?>, TypeMapping<?>> typeMappings, final boolean serializeUnknown) {
		this.serializeUnknown = serializeUnknown;
		this.mappings = typeMappings;
	}

	public byte[] convertValueObjectToByteArray(final Object propertyValue) {
		if (propertyValue == null) {
			return EMPTY_BYTES;
		}
		if (mappings.containsKey(propertyValue.getClass())) {
			return mappings.get(propertyValue.getClass()).toBytes(propertyValue);
		}
		if (Enum.class.isAssignableFrom(propertyValue.getClass())) {
			return stringToBytes(((Enum<?>) propertyValue).name());
		}
		if (propertyValue instanceof Serializable && serializeUnknown) {
			return serialize(propertyValue);
		}
		throw new CassandraException("Cannot map " + propertyValue.getClass() + 
				" instance to byte array, either implement serializable or create a custom type mapping!");
	}

	private byte[] serialize(final Object propertyValue) {
		try {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(propertyValue);
			oout.close();
			return out.toByteArray();
		} catch (final IOException e) {
			throw new CassandraException("Unable to serialize object of type " + propertyValue.getClass(), e);
		}
	}

	public String bytesToString(final byte[] bytes) {
		return (String) mappings.get(String.class).fromBytes(bytes);
	}

	public byte[] stringToBytes(final String string) {
		return mappings.get(String.class).toBytes(string);
	}

	public Object convertByteArrayToValueObject(Class<?> returnType, final byte[] value) {
		if (returnType.isPrimitive() && map.containsKey(returnType)) {
			returnType = map.get(returnType);
		}
		if (mappings.containsKey(returnType)) {
			return returnType.cast(mappings.get(returnType).fromBytes(value));
		}
		if (returnType.isEnum()) {
			return makeEnumInstance(returnType, value);
		}
		if (Serializable.class.isAssignableFrom(returnType)) {
			return returnType.cast(deserialize(value));
		}
		throw new CassandraException("Cannot handle type " + returnType.getClass() + 
				", maybe you have getters and setters with different types? Otherwise, add a type mapping.");
	}

	private Enum<?> makeEnumInstance(final Class<?> returnType, final byte[] value) {
		try {
			final Method method = returnType.getMethod("valueOf", String.class);
			return (Enum<?>) method.invoke(returnType, bytesToString(value));
		} catch (final SecurityException e) {
			throw new CassandraException(e);
		} catch (final NoSuchMethodException e) {
			throw new CassandraException(e);
		} catch (final IllegalArgumentException e) {
			throw new CassandraException(e);
		} catch (final IllegalAccessException e) {
			throw new CassandraException(e);
		} catch (final InvocationTargetException e) {
			throw new CassandraException(e);
		}
	}

	private Object deserialize(final byte[] value) {
		final ByteArrayInputStream in = new ByteArrayInputStream(value);
		try {
			final ObjectInputStream oin = new ObjectInputStream(in);
			final Object retval = oin.readObject();
			oin.close();
			return retval;
		} catch (final IOException e) {
			throw new CassandraException(e);
		} catch (final ClassNotFoundException e) {
			throw new CassandraException(e);
		}
	}

	public Function<String, byte[]> toByteArrayFunction() {
		return new Function<String, byte[]>() {
			public byte[] apply(final String arg0) {
				return stringToBytes(arg0);
			}
		};
	}

}