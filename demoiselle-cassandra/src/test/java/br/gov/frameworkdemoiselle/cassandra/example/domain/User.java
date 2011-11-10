package br.gov.frameworkdemoiselle.cassandra.example.domain;

import br.gov.frameworkdemoiselle.cassandra.annotation.ColumnFamily;
import br.gov.frameworkdemoiselle.cassandra.annotation.Indexed;
import br.gov.frameworkdemoiselle.cassandra.annotation.Key;

@ColumnFamily("users")
//@CassandraEntity(columnFamily = "users")
public class User {

	@Key
	private String login;

	@Indexed
	private String name;

	@Indexed
	private String state;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
