package br.gov.frameworkdemoiselle.cassandra.internal;

import java.util.List;

public interface ColumnDAO<T> extends AbstractDAO<T> {

	List<String> getColumns(final String key);

	List<String> getColumnsBySecondary(final String key);

	List<String> getValues(final String key);

	List<T> getByPrimaryKey(final String key);

	List<T> getBySecondaryKey(final String key);

}
