package br.gov.frameworkdemoiselle.cassandra.internal.implementation;

public interface CassandraDAO<T> {

	/**
	 * Saves the given object into the data store.
	 * 
	 * @param object
	 */
	void save(T object);
	
	/**
	 * Removes the given object from the data store.
	 * 
	 * @param object
	 */
	void delete(T object);

}
