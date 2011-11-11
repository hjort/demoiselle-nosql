package br.gov.frameworkdemoiselle.cassandra.persistence;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

//import me.prettyprint.cassandra.dao.Command;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.KeyspaceService;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.cassandra.annotation.ColumnFamily;
import br.gov.frameworkdemoiselle.cassandra.annotation.Consistency;
import br.gov.frameworkdemoiselle.cassandra.annotation.Key;
import br.gov.frameworkdemoiselle.cassandra.annotation.Keyspace;
import br.gov.frameworkdemoiselle.cassandra.exception.CassandraException;
import br.gov.frameworkdemoiselle.cassandra.internal.EntityDAO;
import br.gov.frameworkdemoiselle.cassandra.internal.implementation.AbstractCassandraDAO;
import br.gov.frameworkdemoiselle.cassandra.internal.implementation.MarshalledObject;
import br.gov.frameworkdemoiselle.cassandra.internal.implementation.TypeConverter;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.TypeMapping;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public abstract class CassandraEntityDAO<T> extends AbstractCassandraDAO<T> implements EntityDAO<T> {

	@Inject
	private Logger logger;

	private final StringSerializer serializer = StringSerializer.get();
	private me.prettyprint.hector.api.Keyspace keyspace;	
	
	protected CassandraEntityDAO() {
	}
	
	@PostConstruct
	protected void init() {
		
		final Class<T> clz = getClazz();
		
        logger.debug("Instantiating CassandraEntityDAO<" + clz.getSimpleName() + ">");
        
        if (!clz.isAnnotationPresent(ColumnFamily.class)) {
            throw new CassandraException(
            		"Target class must be annotated with @ColumnFamily: " + clz.getName());
        }

		final Keyspace keyspaceAnnotation = clz.getAnnotation(Keyspace.class);
		if (keyspaceAnnotation != null && !"".equals(keyspaceAnnotation.value())) {
			this.keyspaceName = keyspaceAnnotation.value();
		} else {
			this.keyspaceName = config.getDefaultKeyspace();
		}
		if (this.keyspaceName == null) {
			throw new CassandraException("Could not find keyspace for "
					+ clz.getName() + ", annotate it with @Keyspace or define in the properties file");
		}

        final ColumnFamily columnFamilyAnnotation = clz.getAnnotation(ColumnFamily.class);
		if (!"".equals(columnFamilyAnnotation.value())) {
			this.columnFamilyName = columnFamilyAnnotation.value();
		} else if (this.columnFamilyName == null) {
			this.columnFamilyName = clz.getSimpleName();
		}
        
		final Consistency consistencyAnnotation = clz.getAnnotation(Consistency.class);
        if (consistencyAnnotation != null) {
        	this.consistencyLevel = consistencyAnnotation.value();
        } else {
        	this.consistencyLevel = config.getDefaultConsistency();
        }
        if (this.consistencyLevel == null) {
        	this.consistencyLevel = ConsistencyLevel.QUORUM;
        }

        logger.trace("Using column family name [" + this.keyspaceName + "][" + this.columnFamilyName + "]");
        
        // TODO: implementar pool de conexões com múltiplos servidores
        
        Cluster cluster = HFactory.getOrCreateCluster("MyCluster", "localhost:9160");
        this.keyspace = HFactory.createKeyspace(keyspaceName, cluster);
        
        // ...
        
		typeConverter = new TypeConverter(typeMappings, serializeUnknownClasses);
		propertyDescriptors = PropertyUtils.getPropertyDescriptors(clz);

        fields = new HashMap<String, Field>();
        for (Field field : clz.getDeclaredFields()) {
        	fields.put(field.getName(), field);
        	if (field.isAnnotationPresent(Key.class)) {
        		keyField = field;
        	}
		}
        
		final Builder<byte[]> setBuilder = ImmutableSet.<byte[]> builder();
		for (final PropertyDescriptor descriptor : propertyDescriptors) {
			setBuilder.add(typeConverter.stringToBytes(descriptor.getName()));
			if (isKeyProperty(descriptor)) {
				keyDescriptor = descriptor;
			}
			if (isSuperColumnProperty(descriptor)) {
				superColumnDescriptor = descriptor;
			}
		}
		columnNames = ImmutableList.copyOf(setBuilder.build());

		if (keyField == null && keyDescriptor == null) {
			throw new CassandraException("Could not find key for class "
					+ clz.getName() + ", annotate a property with @Key");
		}
	}

	@Override
	public void delete(T object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> get(Iterable<String> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> get(List<String> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> getRange(String keyStart, String keyEnd, int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> get(String key, Iterable<String> columns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(final T object) {
		
		Object rowKey = null;
		Map<String, Object> columns = new HashMap<String, Object>();
		
		for (PropertyDescriptor d : propertyDescriptors) {
			if (isReadWrite(d)) {
				try {
					final String name = d.getName();
					final Object value = PropertyUtils.getProperty(object, name);
					
					if (isKeyProperty(d)) {
						rowKey = value;
					} else if (!isTransient(d)) {
						columns.put(name, value);
					}
				} catch (final NoSuchMethodException e) {
					throw new CassandraException(e);
				} catch (final IllegalAccessException e) {
					throw new CassandraException(e);
				} catch (final InvocationTargetException e) {
					throw new CassandraException(e);
				}
			}
		}
		
		Mutator<String> m = HFactory.createMutator(keyspace, serializer);
//		for (Map.Entry<String, Object> value : columns.entrySet()) {
//			m.addInsertion(rowKey, columnFamilyName,
//					HFactory.createColumn(value.getKey(), value.getValue(), keyspace.createClock(), serializer, serializer));
//		}
		
//		<N, V> HColumn<N, V> createColumn(N name, V value, long clock,
//			      Serializer<N> nameSerializer, Serializer<V> valueSerializer) {
		
//	  <N, V> Mutator<K> addInsertion(K key, String cf, HColumn<N, V> c);

		
		try {
			m.execute();
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
	}

	/*
	@Override
	public void save(final T object) {

        final MarshalledObject marshalledObject = MarshalledObject.create();

		for (final PropertyDescriptor d : propertyDescriptors) {
			if (isReadWrite(d)) {
				try {
					
					final String name = d.getName();
					final byte[] value = typeConverter.convertValueObjectToByteArray(
							PropertyUtils.getProperty(object, name));

					if (isKeyProperty(d)) {
						marshalledObject.setKey(value);
					} else if (isSuperColumnProperty(d)) {
						marshalledObject.setSuperColumn(value);
					} else if (!isTransient(d)) {
						marshalledObject.addValue(name, value);
					}

				} catch (final NoSuchMethodException e) {
					throw new CassandraException(e);
				} catch (final IllegalAccessException e) {
					throw new CassandraException(e);
				} catch (final InvocationTargetException e) {
					throw new CassandraException(e);
				}
			}
        }

		if (marshalledObject.getKey() == null || marshalledObject.getKey().length == 0) {
			throw new CassandraException("Key is null, can't store object");
		}

		store(marshalledObject);
	}

	private void store(final MarshalledObject marshalledObject) {

		final byte[] idColumn = marshalledObject.getKey();
		final List<Column> columnList = Lists.newLinkedList();
		final long timestamp = System.currentTimeMillis() * 1000;

		for (final Map.Entry<String, byte[]> property : marshalledObject.getEntries()) {
			columnList.add(toColumn(property, timestamp));
		}

		final Map<String, List<Column>> columnMap;
		final Map<String, List<SuperColumn>> superColumnMap;

		if (marshalledObject.isSuperColumnPresent()) {
			final SuperColumn superColumn = new SuperColumn(marshalledObject.getSuperColumn(), columnList);
			superColumnMap = ImmutableMap.<String, List<SuperColumn>> of(columnFamily, ImmutableList.of(superColumn));
			columnMap = null;
		} else {
			columnMap = ImmutableMap.<String, List<Column>> of(columnFamilyName, columnList);
			superColumnMap = null;
		}

		try {
			execute(new Command<Void>() {

				@Override
				public Void execute(KeyspaceService ks) throws HectorException {
					ks.batchInsert(typeConverter.bytesToString(idColumn),
							columnMap, superColumnMap);
					return null;
				}
			});
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
	}

	private Column toColumn(final Entry<String, byte[]> property, final long timestamp) {
		return new Column(typeConverter.stringToBytes(property.getKey()), property.getValue(), timestamp);
	}
    
	public void delete(final T object) {
		delete(getKeyFrom(object));
	}

	private String getKeyFrom(final T object) {
		try {
			return typeConverter.bytesToString(typeConverter.convertValueObjectToByteArray(
					PropertyUtils.getProperty(object, keyDescriptor.getName())));
		} catch (final IllegalAccessException e) {
			throw new CassandraException(e);
		} catch (final InvocationTargetException e) {
			throw new CassandraException(e);
		} catch (final NoSuchMethodException e) {
			throw new CassandraException(e);
		}
	}

	public void delete(final String key) {
		try {
			execute(new Command<Void>() {

				@Override
				public Void execute(KeyspaceService ks) throws HectorException {
					ks.remove(key, new ColumnPath(columnFamily));
					return null;
				}
			});
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
	}

	public T get(final String key) {

		final ColumnParent parent = makeColumnParent();
		final SlicePredicate predicate = makeSlicePredicateWithAllPropertyColumns();

		try {
			return execute(new Command<T>() {

				@Override
				public T execute(KeyspaceService ks) throws HectorException {
					try {
						final List<Column> slice = ks.getSlice(key, parent, predicate);
						if (Iterables.isEmpty(slice)) {
							return null;
						}
						return applyColumns(key, slice);
					} catch (final HectorException e) {
						return null;
					}
				}
			});
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
	}

	private T applyColumns(final String key, final Iterable<Column> slice) {
		try {
			final T newInstance = clz.newInstance();

			PropertyUtils.setProperty(newInstance, keyDescriptor.getName(),
					typeConverter.convertByteArrayToValueObject(
							keyDescriptor.getReadMethod().getReturnType(),
							typeConverter.stringToBytes(key)));

			for (final Column c : slice) {
				final String name = typeConverter.bytesToString(c.name);
				if (PropertyUtils.isWriteable(newInstance, name)) {
					final PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(newInstance, name);
					final Class<?> returnType = propertyDescriptor.getReadMethod().getReturnType();
					PropertyUtils.setProperty(newInstance, name,
							typeConverter.convertByteArrayToValueObject(returnType, c.value));
				}
			}

			return newInstance;

		} catch (final InstantiationException e) {
			throw new CassandraException("Could not instantiate " + clz.getName(), e);
		} catch (final IllegalAccessException e) {
			throw new CassandraException("Could not instantiate " + clz.getName(), e);
		} catch (final InvocationTargetException e) {
			throw new CassandraException(e);
		} catch (final NoSuchMethodException e) {
			throw new CassandraException(e);
		}
	}

	private List<T> applyColumns(final String key, final List<SuperColumn> slice) {
		final ImmutableList.Builder<T> listBuilder = ImmutableList.builder();
		for (final SuperColumn superColumn : slice) {
			final T object = applyColumns(key, superColumn.getColumns());
			applySuperColumnName(object, superColumn.getName());
			listBuilder.add(object);
		}
		return listBuilder.build();
	}

	private void applySuperColumnName(final T object, final byte[] value) {
		final Class<?> returnType = superColumnDescriptor.getReadMethod().getReturnType();
		try {
			PropertyUtils.setProperty(object, superColumnDescriptor.getName(),
					typeConverter.convertByteArrayToValueObject(returnType, value));
		} catch (final IllegalAccessException e) {
			throw new CassandraException(e);
		} catch (final InvocationTargetException e) {
			throw new CassandraException(e);
		} catch (final NoSuchMethodException e) {
			throw new CassandraException(e);
		}
	}

	public List<T> get(final Iterable<String> keys) {
		final ColumnParent parent = makeColumnParent();
		final SlicePredicate predicate = makeSlicePredicateWithAllPropertyColumns();
		try {
			return execute(new Command<List<T>>() {

				@Override
				public List<T> execute(KeyspaceService ks) throws HectorException {
					final Map<String, List<Column>> slice = ks.multigetSlice(
							ImmutableList.copyOf(keys), parent, predicate);
					return convertToList(slice);
				}
			});
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
	}

	public List<T> get(final List<String> keys) {
		return get(Iterables.transform(keys, Functions.toStringFunction()));
	}

	public List<T> getRange(final String keyStart, final String keyEnd, final int amount) {
		final ColumnParent parent = makeColumnParent();
		final SlicePredicate predicate = makeSlicePredicateWithAllPropertyColumns();
		try {
			return execute(new Command<List<T>>() {

				@Override
				@SuppressWarnings("deprecation")
				public List<T> execute(KeyspaceService ks) throws HectorException {
					final Map<String, List<Column>> slice = ks.getRangeSlice(
							parent, predicate, keyStart, keyEnd, amount);
					return convertToList(slice);
				}
			});
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
	}

	private SlicePredicate makeSlicePredicateWithAllPropertyColumns() {
		final SlicePredicate predicate = new SlicePredicate();
		predicate.setColumn_names(columnNames);
		return predicate;
	}

	private ColumnParent makeColumnParent() {
		final ColumnParent parent = new ColumnParent();
		parent.setColumn_family(columnFamily);
		return parent;
	}

	private List<T> convertToList(final Map<String, List<Column>> slice) {
		final ImmutableList.Builder<T> listBuilder = ImmutableList.<T> builder();
		for (final Map.Entry<String, List<Column>> entry : slice.entrySet()) {
			if (!Iterables.isEmpty(entry.getValue())) {
				listBuilder.add(applyColumns(entry.getKey(), entry.getValue()));
			}
		}
		return listBuilder.build();
	}

	public List<T> get(final String key, final Iterable<String> columns) {

		final ColumnParent parent = makeColumnParent();
		final SlicePredicate predicate = makeSlicePredicateWithColumns(columns);

		try {
			return execute(new Command<List<T>>() {

				@Override
				public List<T> execute(KeyspaceService ks) throws HectorException {
					try {
						final List<SuperColumn> slice = ks.getSuperSlice(key, parent, predicate);
						return applyColumns(key, slice);
					} catch (final HectorException e) {
						return null;
					}
				}
			});
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
	}
	
	private SlicePredicate makeSlicePredicateWithColumns(final Iterable<String> columns) {
		final SlicePredicate predicate = new SlicePredicate();
		predicate.setColumn_names(ImmutableList.copyOf(Iterables.transform(
				columns, typeConverter.toByteArrayFunction())));
		return predicate;
	}
	*/

}
