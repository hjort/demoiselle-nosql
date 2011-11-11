package br.gov.frameworkdemoiselle.cassandra.example.persistence;

import br.gov.frameworkdemoiselle.cassandra.example.domain.User;
import br.gov.frameworkdemoiselle.cassandra.persistence.CassandraEntityDAO;
import br.gov.frameworkdemoiselle.stereotype.PersistenceController;

@PersistenceController
public class UserDAO extends CassandraEntityDAO<User> {

}
