package br.gov.frameworkdemoiselle.cassandra.exception;

public class CassandraException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CassandraException() {
		super();
	}

	public CassandraException(String message, Throwable cause) {
		super(message, cause);
	}

	public CassandraException(String message) {
		super(message);
	}

	public CassandraException(Throwable cause) {
		super(cause);
	}

}
