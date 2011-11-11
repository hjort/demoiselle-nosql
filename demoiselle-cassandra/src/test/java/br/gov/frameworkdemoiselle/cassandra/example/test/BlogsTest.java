package br.gov.frameworkdemoiselle.cassandra.example.test;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.gov.frameworkdemoiselle.cassandra.example.domain.User;
import br.gov.frameworkdemoiselle.cassandra.example.persistence.UserDAO;
import br.gov.frameworkdemoiselle.junit.DemoiselleRunner;

@RunWith(DemoiselleRunner.class)
public class BlogsTest {

//	1) what users subscribe to my blog?
//	2) show me all of the blog entries about fashion
//	3) show me the most recent entries for the blogs I subscribe to

	@Inject
	private UserDAO userDAO;
	
	@Before
	public void setUp() {
//		userDAO = new UserDAO();
	}
	
	@After
	public void tearDown() {
//		userDAO = null;
	}
	
	@Test
	public void testPopulateUsers() {
		System.out.println(userDAO);
		
		User user1 = new User("john", "John Doe", "FL"); 
		userDAO.save(user1);
		User user2 = new User("mary", "Mary Jane", "KS"); 
		userDAO.save(user2);
		User user3 = new User("kevin", "Kevin Bacon", "WA");
		userDAO.save(user3);
	}
	
}
