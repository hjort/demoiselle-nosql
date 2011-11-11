package br.gov.frameworkdemoiselle.cassandra.example.domain;

import br.gov.frameworkdemoiselle.cassandra.annotation.ColumnFamily;
import br.gov.frameworkdemoiselle.cassandra.annotation.Key;

@ColumnFamily("users")
public class User {

	@Key
	private String login;

	private String name;

	private String state;

	public User(String login, String name, String state) {
		this.login = login;
		this.name = name;
		this.state = state;
	}

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
