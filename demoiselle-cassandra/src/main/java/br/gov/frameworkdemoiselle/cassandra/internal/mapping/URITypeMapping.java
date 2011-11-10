package br.gov.frameworkdemoiselle.cassandra.internal.mapping;

import java.net.URI;

public class URITypeMapping extends AbstractStringBasedTypeMapping<URI> {

	protected String asString(final URI value) {
		return value.toString();
	}

	protected URI fromString(final String string) {
		return URI.create(string);
	}

}
