package br.gov.frameworkdemoiselle.cassandra.internal.implementation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.prettyprint.cassandra.dao.Command;
import me.prettyprint.cassandra.service.KeyspaceService;
import me.prettyprint.hector.api.exceptions.HectorException;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import br.gov.frameworkdemoiselle.cassandra.annotation.CassandraColumn;
import br.gov.frameworkdemoiselle.cassandra.exception.CassandraException;

public abstract class CassandraColumnDAO<T> extends AbstractCassandraDAO<T> {

	private static Logger log = Logger.getLogger(CassandraColumnDAO.class);

	private final String secondaryColumnFamily;

	private PropertyDescriptor columnDescriptor;
	private PropertyDescriptor valueDescriptor;

	public CassandraColumnDAO() {
		
        log.debug("Instantiating CassandraColumnDAO with " + clz.getSimpleName());

		if (!clz.isAnnotationPresent(CassandraColumn.class)) {
			throw new IllegalArgumentException(
					"Trying to create a CassandraColumnDAO for a class that is not mapped with @CassandraColumn");
		}

		final CassandraColumn annotation = clz.getAnnotation(CassandraColumn.class);
		
		this.typeConverter = new TypeConverter(typeMappings, serializeUnknownClasses);

		if (!"".equals(annotation.keyspace())) {
			this.keyspace = annotation.keyspace();
		} else if (this.keyspace == null) {
			throw new CassandraException("Could not find keyspace for "
					+ clz.getName() + ", annotate it on @CassandraColumn or define in the properties file");
		}
		
		this.columnFamily = annotation.columnFamily();
		this.secondaryColumnFamily = annotation.secondaryColumnFamily();

		if (annotation.consistency() != null) {
			consistencyLevel = annotation.consistency();
		}

		this.fields = new HashMap<String, Field>();
		for (Field field : clz.getDeclaredFields()) {
			this.fields.put(field.getName(), field);
		}

		this.propertyDescriptors = PropertyUtils.getPropertyDescriptors(clz);
		for (final PropertyDescriptor descriptor : propertyDescriptors) {
			if (isKeyProperty(descriptor)) {
				this.keyDescriptor = descriptor;
			} else if (isColumnProperty(descriptor)) {
				this.columnDescriptor = descriptor;
			} else if (isValueProperty(descriptor)) {
				this.valueDescriptor = descriptor;
			}
		}

		if (keyDescriptor == null) {
			throw new CassandraException("Could not find key of class "
					+ clz.getName() + ", did you annotate with @KeyProperty");
		}

		if (columnDescriptor == null) {
			throw new CassandraException("Could not find column of class "
					+ clz.getName() + ", did you annotate with @ColumnProperty");
		}
	}

	public void save(final T object) {
		try {
			final String keyName = keyDescriptor.getName();
			final Object keyValue = PropertyUtils.getProperty(object, keyName);
			final byte[] keyBytes = typeConverter.convertValueObjectToByteArray(keyValue);

			final String columnName = columnDescriptor.getName();
			final Object columnValue = PropertyUtils.getProperty(object, columnName);
			final byte[] columnBytes = typeConverter.convertValueObjectToByteArray(columnValue);

			byte[] tempValue = null;
			if (valueDescriptor != null) {
				final String valueName = valueDescriptor.getName();
				final Object valueValue = PropertyUtils.getProperty(object, valueName);
				tempValue = typeConverter.convertValueObjectToByteArray(valueValue);
			} else {
				final long timestamp = System.currentTimeMillis();
				tempValue = typeConverter.convertValueObjectToByteArray(timestamp);
			}
			final byte[] value = tempValue;

			execute(new Command<Void>() {
				
				@Override
				public Void execute(KeyspaceService ks) throws HectorException {
					ColumnPath columnPath = new ColumnPath();
					columnPath.setColumn_family(columnFamily);
					columnPath.setColumn(columnBytes);
					ks.insert(keyValue.toString(), columnPath, value);
					
					if (secondaryColumnFamily != null && !secondaryColumnFamily.isEmpty()) {
						columnPath = new ColumnPath();
						columnPath.setColumn_family(secondaryColumnFamily);
						columnPath.setColumn(keyBytes);
						ks.insert(columnValue.toString(), columnPath, value);
					}
					
					return null;
				}
			});

		} catch (final Exception e) {
			throw new CassandraException(e);
		}
	}

	public void delete(final T object) {
		try {
			final String keyName = keyDescriptor.getName();
			final Object keyValue = PropertyUtils.getProperty(object, keyName);
			final byte[] keyBytes = typeConverter.convertValueObjectToByteArray(keyValue);

			final String columnName = columnDescriptor.getName();
			final Object columnValue = PropertyUtils.getProperty(object, columnName);
			final byte[] columnBytes = typeConverter.convertValueObjectToByteArray(columnValue);

			execute(new Command<Void>() {

				@Override
				public Void execute(KeyspaceService ks) throws HectorException {
					ColumnPath columnPath = new ColumnPath();
					columnPath.setColumn_family(columnFamily);
					columnPath.setColumn(columnBytes);
					ks.remove(keyValue.toString(), columnPath);
					
					if (secondaryColumnFamily != null && !secondaryColumnFamily.isEmpty()) {
						columnPath = new ColumnPath();
						columnPath.setColumn_family(secondaryColumnFamily);
						columnPath.setColumn(keyBytes);
						ks.remove(columnValue.toString(), columnPath);
					}
					
					return null;
				}
			});

		} catch (final Exception e) {
			throw new CassandraException(e);
		}
	}
	
	public List<String> getColumns(final String key) {
		final List<String> result = new ArrayList<String>();
		try {
			execute(new Command<Void>() {

				@Override
				public Void execute(KeyspaceService ks) throws HectorException {
					SlicePredicate predicate = new SlicePredicate();
					SliceRange sliceRange = new SliceRange();
			        sliceRange.setStart(new byte[] {});
			        sliceRange.setFinish(new byte[] {});
			        predicate.setSlice_range(sliceRange);
			        
					List<Column> list = ks.getSlice(
							key, new ColumnParent(columnFamily), predicate);
					
					if (list != null && !list.isEmpty()) {
						String name = null;
						for (Column column : list) {
							name = typeConverter.bytesToString(column.getName());
							result.add(name);
						}
					}
					
					return null;
				}
			});
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
		return result;
	}
	
	public List<String> getColumnsBySecondary(final String key) {
		final List<String> result = new ArrayList<String>();
		if ("".equals(secondaryColumnFamily)) {
			throw new CassandraException("Could not find secondary column family for "
					+ clz.getName() + ", annotate it on @CassandraColumn");
		}
		try {
			execute(new Command<Void>() {

				@Override
				public Void execute(KeyspaceService ks) throws HectorException {
					SlicePredicate predicate = new SlicePredicate();
					SliceRange sliceRange = new SliceRange();
			        sliceRange.setStart(new byte[] {});
			        sliceRange.setFinish(new byte[] {});
			        predicate.setSlice_range(sliceRange);
			        
					List<Column> list = ks.getSlice(
							key, new ColumnParent(secondaryColumnFamily), predicate);
					
					if (list != null && !list.isEmpty()) {
						String name = null;
						for (Column column : list) {
							name = typeConverter.bytesToString(column.getName());
							result.add(name);
						}
					}
					
					return null;
				}
			});
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
		return result;
	}
	
	public List<String> getValues(final String key) {
		final List<String> result = new ArrayList<String>();
		try {
			execute(new Command<Void>() {

				@Override
				public Void execute(KeyspaceService ks) throws HectorException {
					SlicePredicate predicate = new SlicePredicate();
					SliceRange sliceRange = new SliceRange();
			        sliceRange.setStart(new byte[] {});
			        sliceRange.setFinish(new byte[] {});
			        predicate.setSlice_range(sliceRange);
			        
					List<Column> list = ks.getSlice(
							key, new ColumnParent(columnFamily), predicate);
					
					if (list != null && !list.isEmpty()) {
						String value = null;
						for (Column column : list) {
							value = typeConverter.bytesToString(column.getValue());
							result.add(value);
						}
					}
					
					return null;
				}
			});
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
		return result;
	}

	public List<T> getByPrimaryKey(final String key) {
		final List<T> result = new ArrayList<T>();
		try {
			execute(new Command<Void>() {

				@Override
				public Void execute(KeyspaceService ks) throws HectorException {
					SlicePredicate predicate = new SlicePredicate();
					SliceRange sliceRange = new SliceRange();
			        sliceRange.setStart(new byte[] {});
			        sliceRange.setFinish(new byte[] {});
			        predicate.setSlice_range(sliceRange);
			        
					List<Column> list = ks.getSlice(
							key, new ColumnParent(columnFamily), predicate);
					
					if (list != null && !list.isEmpty()) {
						T newInstance = null;
						try {
							for (final Column column : list) {
								newInstance = clz.newInstance();
								
								if (keyDescriptor != null) {
									byte[] keyBytes = typeConverter.stringToBytes(key);
									final Class<?> returnType = keyDescriptor.getReadMethod().getReturnType();
									PropertyUtils.setProperty(newInstance, keyDescriptor.getName(),
											typeConverter.convertByteArrayToValueObject(returnType, keyBytes));
								}
								if (columnDescriptor != null) {
									final Class<?> returnType = columnDescriptor.getReadMethod().getReturnType();
									PropertyUtils.setProperty(newInstance, columnDescriptor.getName(),
											typeConverter.convertByteArrayToValueObject(returnType, column.name));
								}
								if (valueDescriptor != null) {
									final Class<?> returnType = valueDescriptor.getReadMethod().getReturnType();
									PropertyUtils.setProperty(newInstance, valueDescriptor.getName(),
											typeConverter.convertByteArrayToValueObject(returnType, column.value));
								}
								
								result.add(newInstance);
							}
						} catch (Exception e) {
							throw new CassandraException("Could not instantiate " + clz.getName(), e);
						}
					}
					
					return null;
				}
			});
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
		return result;
	}

	public List<T> getBySecondaryKey(final String key) {
		final List<T> result = new ArrayList<T>();
		if ("".equals(secondaryColumnFamily)) {
			throw new CassandraException("Could not find secondary column family for "
					+ clz.getName() + ", annotate it on @CassandraColumn");
		}
		try {
			execute(new Command<Void>() {

				@Override
				public Void execute(KeyspaceService ks) throws HectorException {
					SlicePredicate predicate = new SlicePredicate();
					SliceRange sliceRange = new SliceRange();
			        sliceRange.setStart(new byte[] {});
			        sliceRange.setFinish(new byte[] {});
			        predicate.setSlice_range(sliceRange);
			        
					List<Column> list = ks.getSlice(
							key, new ColumnParent(secondaryColumnFamily), predicate);
					
					if (list != null && !list.isEmpty()) {
						T newInstance = null;
						try {
							for (final Column column : list) {
								newInstance = clz.newInstance();
								
								if (keyDescriptor != null) {
									final Class<?> returnType = keyDescriptor.getReadMethod().getReturnType();
									PropertyUtils.setProperty(newInstance, keyDescriptor.getName(),
											typeConverter.convertByteArrayToValueObject(returnType, column.name));
								}
								if (columnDescriptor != null) {
									byte[] keyBytes = typeConverter.stringToBytes(key);
									final Class<?> returnType = columnDescriptor.getReadMethod().getReturnType();
									PropertyUtils.setProperty(newInstance, columnDescriptor.getName(),
											typeConverter.convertByteArrayToValueObject(returnType, keyBytes));
								}
								if (valueDescriptor != null) {
									final Class<?> returnType = valueDescriptor.getReadMethod().getReturnType();
									PropertyUtils.setProperty(newInstance, valueDescriptor.getName(),
											typeConverter.convertByteArrayToValueObject(returnType, column.value));
								}
								
								result.add(newInstance);
							}
						} catch (Exception e) {
							throw new CassandraException("Could not instantiate " + clz.getName(), e);
						}
					}
					
					return null;
				}
			});
		} catch (final Exception e) {
			throw new CassandraException(e);
		}
		return result;
	}

}
