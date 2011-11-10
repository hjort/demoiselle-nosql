package br.gov.frameworkdemoiselle.cassandra.example.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.gov.frameworkdemoiselle.cassandra.example.domain.SimpleEntity;
import br.gov.frameworkdemoiselle.cassandra.example.persistence.BeanDAO;

import com.google.common.collect.ImmutableList;

public class CassandraEntityDAOExecution extends EmbeddedServerExecution {

	private static Logger log = Logger.getLogger(CassandraEntityDAOExecution.class);
	
	private BeanDAO dao;

	@Before
	public void setUp() throws Exception {
		dao = new BeanDAO();
	}

	@After
	public void tearDown() throws Exception {
		dao = null;
	}
	
	@Test
	public void testSave() {
		SimpleEntity bean = createBean();
		
		log.debug("Saving bean: " + bean);
		dao.save(bean);
		assertTrue(true);
		
		SimpleEntity bean2 = dao.get(bean.getId().toString());
		assertEquals(bean, bean2);
		
		dao.delete(bean2);
	}

	@Test
	public void testDeleteObject() {
		SimpleEntity bean = createBean();
		dao.save(bean);
		assertTrue(true);
		
		log.debug("Deleting bean: " + bean);
		dao.delete(bean);
		assertTrue(true);
		
		SimpleEntity bean2 = dao.get(bean.getId().toString());
		assertNull(bean2);
	}

	@Test
	public void testDeleteString() {
		SimpleEntity bean = createBean();
		dao.save(bean);
		assertTrue(true);
		
		log.debug("Deleting bean: " + bean);
		dao.delete(bean.getId().toString());
		assertTrue(true);
		
		SimpleEntity bean2 = dao.get(bean.getId().toString());
		assertNull(bean2);
	}

	@Test
	public void testGetString() {
		SimpleEntity bean = createBean();
		
		dao.save(bean);
		assertTrue(true);
		
		SimpleEntity bean2 = dao.get(bean.getId().toString());
		assertEquals(bean, bean2);
		dao.delete(bean2);
		
		SimpleEntity bean3 = dao.get("invalid-id");
		assertNull(bean3);
	}

	@Test
	public void testGetIterableOfString() {
		saveBeans();

		final List<String> keys = ImmutableList.of("100001", "100005", "100010");
		final Iterable<String> ite = new Iterable<String>() {
			@Override
			public Iterator<String> iterator() {
				return keys.iterator();
			}
		};
		
		log.debug("Retrieving iterable list: " + keys);
		final List<SimpleEntity> list = dao.get(ite);
		log.debug(list);
		assertEquals(2, list.size());
		assertTrue(list.contains(createBean(100001l)));
		assertTrue(list.contains(createBean(100005l)));
		assertFalse(list.contains(createBean(100010l)));
		
		removeBeans();
	}

	@Test
	public void testGetListOfString() {
		saveBeans();

		final List<String> keys = ImmutableList.of("100001", "100005", "100010");
		log.debug("Retrieving list: " + keys);
		final List<SimpleEntity> list = dao.get(keys);
		log.debug(list);
		assertEquals(2, list.size());
		assertTrue(list.contains(createBean(100001l)));
		assertTrue(list.contains(createBean(100005l)));
		assertFalse(list.contains(createBean(100010l)));
		
		removeBeans();
	}

	@Test
	public void testGetRange() {
		saveBeans();
		
		log.debug("Retrieving ranges:");
		List<SimpleEntity> list = dao.getRange("100002", "100004", 100);
		log.debug(list);
		assertEquals(3, list.size());
		assertTrue(list.contains(createBean(100003l)));
		
		list = dao.getRange("100008", "100010", 100);
		log.debug(list);
		assertEquals(2, list.size());
		assertTrue(list.contains(createBean(100009l)));
		
		list = dao.getRange("100001", "100010", 5);
		log.debug(list);
		assertEquals(5, list.size());
		assertTrue(list.contains(createBean(100002l)));
		
		removeBeans();
	}

	private SimpleEntity createBean() {
		long id = (long) (Math.random() * 1E4);
		return createBean(id);
	}
	
	private SimpleEntity createBean(final long id) {
		SimpleEntity bean = new SimpleEntity();
		bean.setId(id);
		bean.setName("Bean Named " + id);
		bean.setActive(true);
		return bean;
	}

	private void saveBeans() {
		log.debug("Saving beans:");
		for (int i = 1; i < 10; i++) {
			SimpleEntity bean = createBean((long) (1E5 + i));
			log.debug(bean);
			dao.save(bean);
		}
	}

	private void removeBeans() {
		for (int i = 1; i < 10; i++) {
			long id = (long) (1E5 + i);
			dao.delete(String.valueOf(id));
		}
	}

}
