package br.gov.frameworkdemoiselle.cassandra.internal.mapping;

public class CharacterTypeMapping extends AbstractStringBasedTypeMapping<Character> {

	protected String asString(final Character value) {
		return value.toString();
	}

	protected Character fromString(final String string) {
		return Character.valueOf(string != null ? string.charAt(0) : null);
	}

}
