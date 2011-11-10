package br.gov.frameworkdemoiselle.cassandra.example.domain;

import br.gov.frameworkdemoiselle.cassandra.annotation.Column;
import br.gov.frameworkdemoiselle.cassandra.annotation.ColumnFamily;
import br.gov.frameworkdemoiselle.cassandra.annotation.Key;
import br.gov.frameworkdemoiselle.cassandra.annotation.SecondaryColumnFamily;

@ColumnFamily("subscribes_to")
@SecondaryColumnFamily("subscribers_of")
//@CassandraColumn(columnFamily = "subscribes_to", secondaryColumnFamily = "subscribers_of")
public class Subscription {

	@Key
	private String subscriber;

	@Column
	private String subscribed;

	public String getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}

	public String getSubscribed() {
		return subscribed;
	}

	public void setSubscribed(String subscribed) {
		this.subscribed = subscribed;
	}

}
