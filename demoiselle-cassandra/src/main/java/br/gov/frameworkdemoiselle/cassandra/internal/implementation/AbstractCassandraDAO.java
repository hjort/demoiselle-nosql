package br.gov.frameworkdemoiselle.cassandra.internal.implementation;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import me.prettyprint.cassandra.dao.Command;

import org.apache.cassandra.thrift.ConsistencyLevel;

import br.gov.frameworkdemoiselle.cassandra.annotation.ColumnProperty;
import br.gov.frameworkdemoiselle.cassandra.annotation.KeyProperty;
import br.gov.frameworkdemoiselle.cassandra.annotation.SuperColumnProperty;
import br.gov.frameworkdemoiselle.cassandra.annotation.Transient;
import br.gov.frameworkdemoiselle.cassandra.annotation.ValueProperty;
import br.gov.frameworkdemoiselle.cassandra.exception.CassandraException;
import br.gov.frameworkdemoiselle.cassandra.internal.configuration.CassandraConfig;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.BooleanTypeMapping;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.ByteTypeMapping;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.CharacterTypeMapping;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.DoubleTypeMapping;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.FloatTypeMapping;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.IntegerTypeMapping;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.LongTypeMapping;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.ShortTypeMapping;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.StringTypeMapping;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.TypeMapping;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.URITypeMapping;
import br.gov.frameworkdemoiselle.cassandra.internal.mapping.UUIDTypeMapping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public abstract class AbstractCassandraDAO<T> {

	@Inject
	private CassandraConfig config;
	
	protected Class<T> clz;
	
	protected boolean serializeUnknownClasses;
	protected ImmutableMap<Class<?>, TypeMapping<?>> typeMappings;

    private String hostname;
    private int port;
	private String[] nodes;

    protected String keyspace;
    protected String columnFamily;

    protected ConsistencyLevel consistencyLevel;

    protected PropertyDescriptor[] propertyDescriptors;
    protected ImmutableList<byte[]> columnNames;
    protected PropertyDescriptor keyDescriptor;
    protected PropertyDescriptor superColumnDescriptor;

    protected Map<String, Field> fields;
    protected Field keyField;

    protected TypeConverter typeConverter;
    
    private static final ImmutableMap<Class<?>, TypeMapping<?>> DEFAULT_TYPES =
    	new ImmutableMap.Builder<Class<?>, TypeMapping<?>>()
    		.put(String.class, new StringTypeMapping())
	        .put(Character.class, new CharacterTypeMapping())
	        .put(Byte.class, new ByteTypeMapping())
	        .put(Short.class, new ShortTypeMapping())
	        .put(Integer.class, new IntegerTypeMapping())
	        .put(Long.class, new LongTypeMapping())
	        .put(Float.class, new FloatTypeMapping())
	        .put(Double.class, new DoubleTypeMapping())
	        .put(Boolean.class, new BooleanTypeMapping())
	        .put(UUID.class, new UUIDTypeMapping())
	        .put(URI.class, new URITypeMapping())
    		.build();
    /*
    private static final Map<Class<?>, TypeMapping<?>> DEFAULT_TYPES =
    	ImmutableMap.<Class<?>, TypeMapping<?>>of(
	        String.class, new StringTypeMapping(),
	        UUID.class, new UUIDTypeMapping(),
	        Long.class, new LongTypeMapping(),
	        Integer.class, new IntegerTypeMapping(),
	        URI.class, new URITypeMapping()
    	);
    */

	@SuppressWarnings("unchecked")
	public AbstractCassandraDAO() {
		
        clz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        
        // TODO: read additional type mappings from somewhere (i.e., type-mappings.properties) 
        typeMappings = ImmutableMap.<Class<?>, TypeMapping<?>>builder().putAll(DEFAULT_TYPES)./*putAll(mappings).*/build();

        initialize();
	}

	private void initialize() {
		
		// read settings from properties file
		nodes = config.getServerNodes();
		keyspace = config.getDefaultKeyspace();
		consistencyLevel = config.getDefaultConsistency();
		serializeUnknownClasses = config.isSerializeUnknown();
		
		// TODO: implement the pooleable clients approach
//		CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
//		CassandraClient client = pool.addCassandraHost(new CassandraHost("localhost", 9160));
	}
	
	protected boolean isKeyProperty(final PropertyDescriptor d) {
		return safeIsAnnotationPresent(d, KeyProperty.class);
	}

    protected boolean isColumnProperty(final PropertyDescriptor d) {
		return safeIsAnnotationPresent(d, ColumnProperty.class);
	}

    protected boolean isValueProperty(final PropertyDescriptor d) {
		return safeIsAnnotationPresent(d, ValueProperty.class);
	}

	protected boolean isTransient(final PropertyDescriptor d) {
		return safeIsAnnotationPresent(d, Transient.class);
	}

	protected boolean isSuperColumnProperty(final PropertyDescriptor descriptor) {
		return safeIsAnnotationPresent(descriptor, SuperColumnProperty.class);
	}

	private boolean safeIsAnnotationPresent(final PropertyDescriptor d, final Class<? extends Annotation> annotation) {
		return nullSafeAnnotationPresent(annotation, fields.get(d.getName()))
				|| nullSafeAnnotationPresent(annotation, d.getReadMethod())
				|| nullSafeAnnotationPresent(annotation, d.getWriteMethod());
	}

	private boolean nullSafeAnnotationPresent(final Class<? extends Annotation> annotation, final Method method) {
		return (method != null && method.isAnnotationPresent(annotation));
	}

	private boolean nullSafeAnnotationPresent(final Class<? extends Annotation> annotation, final Field field) {
		return (field != null && field.isAnnotationPresent(annotation));
	}

	protected boolean isReadWrite(final PropertyDescriptor d) {
		return (d.getReadMethod() != null && d.getWriteMethod() != null);
	}

	protected <V> V execute(final Command<V> command) throws Exception {
    	if (hostname != null) {
    		return command.execute(hostname, port, keyspace);
    	} else if (nodes != null) {
    		return command.execute(nodes, keyspace, consistencyLevel);
    	} else {
    		throw new CassandraException("One of these must be set: hostname and port or an array of nodes.");
    	}
    }

	// TODO: implement this in the future, by using a pool of clients
//	protected <T> T execute(final CassandraCommand<T> command) throws CassandraException {
//		return command.execute(keyspace, consistencyLevel);
//	}

}
