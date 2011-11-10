package br.gov.frameworkdemoiselle.mongo.example;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.mongo.internal.implementation.MongoDB;
import br.gov.frameworkdemoiselle.stereotype.PersistenceController;
import br.gov.frameworkdemoiselle.template.Crud;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@PersistenceController
//@SessionScoped
public class BookmarkMongoDAO implements Crud<Bookmark, Long> {

	private static final long serialVersionUID = -2507296781936867852L;

	@Inject
	private MongoDB db;
	
//	@Inject
//	@Name("mydb")
//	private DB db;
	
	@Inject
	private Logger logger;

	@PostConstruct
	public void init() {
//		coll = db.getCollection("bookmarks");
//		logger.info(this.toString() + ".init(): " + coll.toString());
		logger.info(this.toString() + ".init()");
	}

	@PreDestroy
	public void finish() {
//		logger.info(this.toString() + ".finish(): " + coll.toString());
		logger.info(this.toString() + ".finish()");
	}
	
	private DBCollection getCollection() {
		DBCollection coll = db.getCollection("bookmarks");
		logger.info(coll.toString());
		return coll;
	}
	
	@Override
	public List<Bookmark> findAll() {
		DBCursor cur = getCollection().find();
		List<Bookmark> list = new ArrayList<Bookmark>();
		while (cur.hasNext()) {
			DBObject obj = cur.next();
			Bookmark bean = new Bookmark();
			bean.setId((Long) obj.get("id"));
			bean.setDescription((String) obj.get("description"));
			bean.setLink((String) obj.get("link"));
			list.add(bean);
		}
		return list;
	}

	@Override
	public Bookmark load(Long id) {
		BasicDBObject query = new BasicDBObject();
		query.put("id", id);
		
		DBCursor cur = getCollection().find(query);
		if (!cur.hasNext())
			return null;
		DBObject obj = cur.next();
		
		Bookmark bean = new Bookmark();
		bean.setId((Long) obj.get("id"));
		bean.setDescription((String) obj.get("description"));
		bean.setLink((String) obj.get("link"));
		return bean;
	}

	@Override
	public void insert(Bookmark bean) {
		BasicDBObject doc = new BasicDBObject();
		doc.put("id", new Long((long) (Math.random() * 1E5)));
		doc.put("description", bean.getDescription());
		doc.put("link", bean.getLink());
		getCollection().insert(doc);
	}

	@Override
	public void update(Bookmark bean) {
		BasicDBObject query = new BasicDBObject();
		query.put("id", bean.getId());
		BasicDBObject doc = new BasicDBObject();
		doc.put("id", bean.getId());
		doc.put("description", bean.getDescription());
		doc.put("link", bean.getLink());
		getCollection().update(query, doc);
	}

	@Override
	public void delete(Long id) {
		BasicDBObject query = new BasicDBObject();
		query.put("id", id);
		getCollection().remove(query);
	}

}
