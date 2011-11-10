package br.gov.frameworkdemoiselle.cassandra.example.domain;

import br.gov.frameworkdemoiselle.cassandra.annotation.CassandraEntity;
import br.gov.frameworkdemoiselle.cassandra.annotation.Key;

@CassandraEntity(columnFamily = "users")
public class User {

	@Key
	private String login;

	private String name;

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
