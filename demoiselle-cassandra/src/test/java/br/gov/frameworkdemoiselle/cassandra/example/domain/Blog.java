package br.gov.frameworkdemoiselle.cassandra.example.domain;

import java.util.UUID;

import br.gov.frameworkdemoiselle.cassandra.annotation.CassandraEntity;
import br.gov.frameworkdemoiselle.cassandra.annotation.Key;

@CassandraEntity(columnFamily = "blog_entries")
public class Blog {

	@Key
	private UUID id;

	private String body;

	// @Indexed
	private String user;

	// @Indexed
	private String category;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
