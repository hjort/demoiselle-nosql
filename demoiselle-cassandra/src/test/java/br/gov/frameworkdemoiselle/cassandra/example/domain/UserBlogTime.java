package br.gov.frameworkdemoiselle.cassandra.example.domain;

import java.util.UUID;

import br.gov.frameworkdemoiselle.cassandra.annotation.Column;
import br.gov.frameworkdemoiselle.cassandra.annotation.ColumnFamily;
import br.gov.frameworkdemoiselle.cassandra.annotation.Key;
import br.gov.frameworkdemoiselle.cassandra.annotation.Value;

@ColumnFamily("time_ordered_blogs_by_user")
public class UserBlogTime {

	@Key
	private String user;

	@Column
	private Long time;

	@Value
	private UUID blog;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public UUID getBlog() {
		return blog;
	}

	public void setBlog(UUID blog) {
		this.blog = blog;
	}

}
