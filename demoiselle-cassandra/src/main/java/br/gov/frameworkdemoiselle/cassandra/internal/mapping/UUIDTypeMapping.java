package br.gov.frameworkdemoiselle.cassandra.internal.mapping;

import java.util.UUID;

public class UUIDTypeMapping extends AbstractStringBasedTypeMapping<UUID> {

	protected String asString(final UUID value) {
		return value.toString();
	}

	protected UUID fromString(final String string) {
		return UUID.fromString(string);
	}

}
