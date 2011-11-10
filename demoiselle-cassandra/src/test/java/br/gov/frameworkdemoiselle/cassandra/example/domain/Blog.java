package br.gov.frameworkdemoiselle.cassandra.example.domain;

import java.util.UUID;

import org.apache.cassandra.thrift.ConsistencyLevel;

import br.gov.frameworkdemoiselle.cassandra.annotation.ColumnFamily;
import br.gov.frameworkdemoiselle.cassandra.annotation.Consistency;
import br.gov.frameworkdemoiselle.cassandra.annotation.Key;
import br.gov.frameworkdemoiselle.cassandra.annotation.Keyspace;

@Keyspace("Keyspace1")
@ColumnFamily("blog_entries")
@Consistency(ConsistencyLevel.ALL)
//@CassandraEntity(columnFamily = "blog_entries")
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
