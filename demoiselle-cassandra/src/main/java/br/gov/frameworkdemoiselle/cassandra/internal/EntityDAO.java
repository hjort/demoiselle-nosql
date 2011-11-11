package br.gov.frameworkdemoiselle.cassandra.internal;

import java.util.List;

public interface EntityDAO<T> extends AbstractDAO<T> {

	void delete(final String key);
	
	T get(final String key);

	List<T> get(final Iterable<String> keys);

	List<T> get(final List<String> keys);

	List<T> getRange(final String keyStart, final String keyEnd, final int amount);

	List<T> get(final String key, final Iterable<String> columns);

}
