package com.sa.util.mongo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.Document;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;
import com.sa.util.DateUtil;

/**
 * Mongo操作工具类
 * 
 * @author bugao.gong
 * @version 创建时间：2019年1月8日 下午4:51:51 Pursuing Excelsior!
 */
public final class MongoDBUtil {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MongoDBUtil.class);
	private static volatile MongoDBUtil mongoDBUtil;
	private MongoClient mongoClient;// mongoClient
	private MongoDBConfig config;
	private List<MongoDatabase> mongoDatabases = new ArrayList<MongoDatabase>();// mongoDatabases
	private Map<String, MongoCollection<Document>> mongoCollectionMap = new HashMap<String, MongoCollection<Document>>();// mongoCollection
	private static MongoClientOptions mongoOptions;

	// 采用单例模式
	private MongoDBUtil(MongoDBConfig config) {
		MongoClientOptions.Builder build = new MongoClientOptions.Builder();
		build.connectionsPerHost(50); // 与目标数据库能够建立的最大connection数量为50
		build.threadsAllowedToBlockForConnectionMultiplier(50); // 如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
		/*
		 * 一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为2分钟
		 * 这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception
		 * 故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败
		 */
		build.maxWaitTime(1000 * 60 * 2);
		build.connectTimeout(1000 * 60 * 1); // 与数据库建立连接的timeout设置为1分钟
		mongoOptions = build.build();

		if (config.getNeedAuth()) {
			this.authentication(config);
		} else {
			this.noAuthentication(config);
		}
	}

	/**
	 * 获取MongoDBUtil的实例
	 * 
	 * @param @param config
	 * @param @return
	 * @return: MongoDBUtil
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午7:27:34 Pursuing Excelsior!
	 */
	public static MongoDBUtil getInstance(MongoDBConfig config) {
		if (mongoDBUtil == null) {
			// 同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
			synchronized (MongoDBUtil.class) {
				// 未初始化，则初始instance变量
				if (mongoDBUtil == null) {
					mongoDBUtil = new MongoDBUtil(config);
				}
			}
		}
		mongoDBUtil.config = config;
		return mongoDBUtil;
	}

	/**
	 * 有密码的授权
	 * 
	 * @param @param config
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午7:27:56 Pursuing Excelsior!
	 */
	private void authentication(final MongoDBConfig config) {
		if (config.getUsername()==null || config.getUsername().length() == 0 || config.getPassword()==null || config.getPassword().length() == 0) {
			throw new RuntimeException("用户名或密码为空！");
		}
		String ip = config.getIp();
		Integer port = config.getPort();
		String userName = config.getUsername();
		String dbName = config.getDb();
		char[] password = config.getPassword().toCharArray();
		ServerAddress serverAddress = new ServerAddress(ip, port);
		MongoCredential credential = MongoCredential.createScramSha1Credential(userName, dbName, password);
		List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		credentials.add(credential);
		List<ServerAddress> addrs = new ArrayList<ServerAddress>();
		addrs.add(serverAddress);
		mongoClient = new MongoClient(addrs, credentials,mongoOptions);
	}

	/**
	 * 无账号密码的授权
	 * 
	 * @param:
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:13:24 Pursuing Excelsior!
	 */
	public void noAuthentication(final MongoDBConfig config) {
		String ip = config.getIp();
		Integer port = config.getPort();
		ServerAddress serverAddress = new ServerAddress(ip, port);
		List<ServerAddress> addrs = new ArrayList<ServerAddress>();
		addrs.add(serverAddress);
		mongoClient = new MongoClient(addrs,mongoOptions);
	}

	/**
	 * mongo授权
	 * 
	 * @param: IP
	 * @param port     端口
	 * @param userName 用户名
	 * @param password 密码
	 * @param dbName   数据库名
	 * @param: @return
	 * @return: MongoClient
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:11:44 Pursuing Excelsior!
	 */
	public MongoClient authenticationAndGetClient(final String ip,final Integer port,final  String userName,final  String password,
			final String dbName) {
		ServerAddress serverAddress = new ServerAddress(ip, port);
		List<ServerAddress> addrs = new ArrayList<ServerAddress>();
		addrs.add(serverAddress);
		if (userName!=null && userName.length()>0 && password!=null && password.length()>0 ) {
			MongoClient mongoClient = new MongoClient(addrs,mongoOptions);
			return mongoClient;
		}
		MongoCredential credential = MongoCredential.createScramSha1Credential(userName, dbName,
				password.toCharArray());
		List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		credentials.add(credential);
		// mongoClient = new MongoClient(addrs, credentials,myOptions);
		MongoClient mongoClient = new MongoClient(addrs, credentials,mongoOptions);
		return mongoClient;
	}

	/**
	 * 添加索引
	 * 
	 * @param collection 集合名
	 * @param field      字段名
	 * @param isUnique   是否唯一 true:是 , false:否
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:11:33 Pursuing Excelsior!
	 */
	public void addIndex(MongoCollection<org.bson.Document> collection, String field, boolean isUnique) {
		addIndex(collection, field, 1, isUnique);
	}
	public void addTextIndex(MongoCollection<org.bson.Document> collection, String field, boolean isUnique) {
		addIndex(collection, field, "text", isUnique);
	}
	public void addIndex(MongoCollection<org.bson.Document> collection, String field,Object type, boolean isUnique) {
		BasicDBObject fieldDBObj = new BasicDBObject();
		fieldDBObj.put(field, type);
		IndexOptions indexOptions = new IndexOptions();
		indexOptions.unique(isUnique);
		ListIndexesIterable<Document> indexs = collection.listIndexes();
		for (Document document : indexs) {
			if(document.get("key")==null){
				continue;
			}
			boolean containsIndex = ((Document)document.get("key")).get(field)!=null;
			if(containsIndex){
				return ;
			}
		}
		collection.createIndex(fieldDBObj, indexOptions);
	}
	/**
	 * 添加索引
	 * 
	 * @param collection  集合实例
	 * @param mongoIndexs mongo索引
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:11:26 Pursuing Excelsior!
	 */
	public void addIndexs(MongoCollection<org.bson.Document> collection, MongoDBIndex... mongoIndexs) {
		List<IndexModel> models = new ArrayList<IndexModel>();
		for (MongoDBIndex mongoDBIndex : mongoIndexs) {
			BasicDBObject fieldDBObj = new BasicDBObject();
			fieldDBObj.put(mongoDBIndex.getIndexName(), 1);
			IndexOptions indexOptions = new IndexOptions();
			indexOptions.unique(mongoDBIndex.getUnique());
			IndexModel indexModel = new IndexModel(fieldDBObj, indexOptions);
			models.add(indexModel);
		}
		collection.createIndexes(models);
	}

	/**
	 * 添加索引
	 * 
	 * @param collectionName 集合名
	 * @param mongoIndexs    mongo索引
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:11:54 Pursuing Excelsior!
	 */
	public void addIndexs(String collectionName, MongoDBIndex... mongoIndexs) {
		MongoCollection<Document> collection = mongoDBUtil.getCollection(collectionName);
		addIndexs(collection, mongoIndexs);
	}

	/**
	 * 添加组合索引
	 * 
	 * @param collection  集合
	 * @param mongoDBIndexs mongo索引
	 * @param isUnique    是否唯一 true:是 , false:否
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:12:05 Pursuing Excelsior!
	 */
	public void addCombinationIndex(MongoCollection<org.bson.Document> collection, List<MongoDBIndex> mongoDBIndexs,
			boolean isUnique) {
		BasicDBObject fieldDBObjs = new BasicDBObject();
		for (MongoDBIndex mongoDBIndex : mongoDBIndexs) {
			fieldDBObjs.put(mongoDBIndex.getIndexName(), 1);
		}
		IndexOptions indexOptions = new IndexOptions();
		indexOptions.unique(isUnique);
		collection.createIndex(fieldDBObjs, indexOptions);
	}

	/**
	 * 添加组合索引
	 * 
	 * @param collectionName 集合名
	 * @param mongoDBIndexs    mongo索引
	 * @param isUnique       是否唯一 true:是 , false:否
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:12:26 Pursuing Excelsior!
	 */
	public void addCombinationIndex(String collectionName, List<MongoDBIndex> mongoDBIndexs, boolean isUnique) {
		addCombinationIndex(getCollection(collectionName), mongoDBIndexs, isUnique);
	}

	/**
	 * 添加组合索引
	 * 
	 * @param collection  集合
	 * @param isUnique    是否唯一 true:是 , false:否
	 * @param mongoIndexs mongo索引
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:12:40 Pursuing Excelsior!
	 */
	public void addCombinationIndex(MongoCollection<org.bson.Document> collection, boolean isUnique,
			MongoDBIndex... mongoIndexs) {
		BasicDBObject fieldDBObjs = new BasicDBObject();
		for (MongoDBIndex mongoDBIndex : mongoIndexs) {
			fieldDBObjs.put(mongoDBIndex.getIndexName(), 1);
		}
		IndexOptions indexOptions = new IndexOptions();
		indexOptions.unique(isUnique);
		collection.createIndex(fieldDBObjs, indexOptions);
	}

	/**
	 * 添加组合索引
	 * 
	 * @param collectionName 集合名
	 * @param isUnique       是否唯一 true:是 , false:否
	 * @param mongoIndexs    mongo索引
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:12:50 Pursuing Excelsior!
	 */
	public void addCombinationIndex(String collectionName, boolean isUnique, MongoDBIndex... mongoIndexs) {
		addCombinationIndex(getCollection(collectionName), isUnique, mongoIndexs);
	}

	/**
	 * 多个集合添加索引
	 * 
	 * @param collections 集合列表
	 * @param field       字段名
	 * @param isUnique    是否唯一
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:13:02 Pursuing Excelsior!
	 */
	public void addIndex(List<MongoCollection<org.bson.Document>> collections, String field, boolean isUnique) {
		for (MongoCollection<Document> mongoCollection : collections) {
			addIndex(mongoCollection, field, isUnique);
		}
	}

	/**
	 * 添加索引
	 * 
	 * @param: collectionName 集合名
	 * @param field    字段名
	 * @param isUnique 是否唯一
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:13:11 Pursuing Excelsior!
	 */
	public void addIndex(String collectionName, String field, boolean isUnique) {
		MongoCollection<Document> mongoCollection = mongoDBUtil.getCollection(config.getDb(), collectionName);
		addIndex(mongoCollection, field, isUnique);
	}
	
	public void addTextIndex(String collectionName, String field, boolean isUnique) {
		MongoCollection<Document> mongoCollection = mongoDBUtil.getCollection(config.getDb(), collectionName);
		addTextIndex(mongoCollection, field, isUnique);
	}

	/**
	 * 无账号密码的授权
	 * 
	 * @param ip
	 * @param port
	 * @param: @return
	 * @return: MongoClient
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:13:47 Pursuing Excelsior!
	 */
	public MongoClient noAuthentication(String ip, Integer port) {
		ServerAddress serverAddress = new ServerAddress(ip, port);
		List<ServerAddress> addrs = new ArrayList<ServerAddress>();
		addrs.add(serverAddress);
		// mongoClient = new MongoClient(addrs,myOptions);
		MongoClient mongoClient = new MongoClient(addrs,mongoOptions);
		return mongoClient;
	}

	/**
	 * 获取数据库实例
	 * 
	 * @param databaseName 数据库名
	 * @param: @return
	 * @return: MongoDatabase
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:13:59 Pursuing Excelsior!
	 */
	public MongoDatabase getDB(String databaseName) {
		if (mongoClient == null) {
			noAuthentication(config);
		}
		MongoDatabase returnMongoDatabase = null;
		for (MongoDatabase mongoDatabase : mongoDatabases) {
			if (mongoDatabase.getName().equals(databaseName)) {
				returnMongoDatabase = mongoDatabase;
				break;
			}
		}
		if (returnMongoDatabase == null) {
			returnMongoDatabase = mongoClient.getDatabase(databaseName);
			mongoDatabases.add(returnMongoDatabase);
		}
		return returnMongoDatabase;
	}

	/**
	 * 获取集合
	 * 
	 * @param collectionName 集合名
	 * @param: @return
	 * @return: MongoCollection<org.bson.Document>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:14:16 Pursuing Excelsior!
	 */
	public MongoCollection<org.bson.Document> getCollection(String collectionName) {
		return getCollection(config.getDb(), collectionName);
	}

	/**
	 * 删除集合
	 * 
	 * @param collectionName 集合名
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:14:31 Pursuing Excelsior!
	 */
	public void dropCollection(String collectionName) {
		getCollection(collectionName).drop();
	}

	/**
	 * 模糊查询
	 * 
	 * @param str
	 * @param: @return
	 * @return: Pattern
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:14:42 Pursuing Excelsior!
	 */
	public Pattern like(String str) {
		Pattern ptn = Pattern.compile("^.*" + str + ".*$", Pattern.CASE_INSENSITIVE);
		return ptn;
	}

	/**
	 * 查询集合
	 * 
	 * @param dbName         数据库名
	 * @param collectionName 集合名
	 * @param: @return
	 * @return: MongoCollection<org.bson.Document>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:15:09 Pursuing Excelsior!
	 */
	public MongoCollection<org.bson.Document> getCollection(String dbName, String collectionName) {
		MongoCollection<Document> cacheCollection = mongoCollectionMap.get(dbName + "_" + collectionName);
		if (cacheCollection != null) {
			return cacheCollection;
		}
		cacheCollection = getDB(dbName).getCollection(collectionName);
		if (cacheCollection != null) {
			mongoCollectionMap.put(dbName + "_" + collectionName, cacheCollection);
		}
		return cacheCollection;
	}

	/**
	 * 查询集合
	 * 
	 * @param dbName          数据库名
	 * @param collectionNames 集合名
	 * @param: @return
	 * @return: List<MongoCollection<org.bson.Document>>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:15:43 Pursuing Excelsior!
	 */
	public List<MongoCollection<org.bson.Document>> getCollections(String dbName, String... collectionNames) {
		List<MongoCollection<org.bson.Document>> collections = new ArrayList<MongoCollection<Document>>();
		for (String cName : collectionNames) {
			MongoCollection<Document> collection = getDB(dbName).getCollection(cName);
			collections.add(collection);
		}
		return collections;
	}

	/**
	 * 查询集合
	 * 
	 * @param collectionNames 集合名
	 * @param: @return
	 * @return: List<MongoCollection<org.bson.Document>>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:16:01 Pursuing Excelsior!
	 */
	public List<MongoCollection<org.bson.Document>> getCollectionsFromConfigDB(String... collectionNames) {
		List<MongoCollection<org.bson.Document>> collections = new ArrayList<MongoCollection<Document>>();
		for (String cName : collectionNames) {
			MongoCollection<Document> collection = getDB(config.getDb()).getCollection(cName);
			collections.add(collection);
		}
		return collections;
	}

	/**
	 * 保存一个文档
	 * 
	 * @param collectionName
	 * @param map
	 * @param: @return
	 * @return: boolean
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:16:10 Pursuing Excelsior!
	 */
	public boolean saveDoc(String collectionName, Map<String, Object> map) {
		/*
		 * if (map instanceof JSONObject) { String json =
		 * map.toString().replaceAll(":null", ":''"); map = JSONObject.fromObject(json);
		 * }
		 */
		map.put("createTime", new SimpleDateFormat(DateUtil.DATE_FORMAT_01).format(new Date()));
		MongoCollection<Document> collection = mongoDBUtil.getCollection(config.getDb(), collectionName);
		return saveDoc(collection, map);
	}

	/**
	 * 保存一个文档
	 * 
	 * @param collection 集合
	 * @param map        文档的map
	 * @param: @return
	 * @return: boolean
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:16:46 Pursuing Excelsior!
	 */
	public boolean saveDoc(MongoCollection<org.bson.Document> collection, Map<String, Object> map) {
		if (map == null)
			throw new NullPointerException("Map can't be null");
		Document document = new Document(map);
		try {
			collection.insertOne(document);
		} catch (Exception e) {
			// e.printStackTrace();
			 System.out.println(e.getMessage());
			logger.error("saveDoc error!",e);
			return false;
		}
		return true;
	}
	
	/**
	 * 保存多个文档
	 * 
	 * @param collection 集合
	 * @param map        文档的map
	 * @param: @return
	 * @return: boolean
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:16:46 Pursuing Excelsior!
	 */
	public boolean saveDocs(String collectionName, List<Map<String, Object>> maps) {
		if (maps == null)
			throw new NullPointerException("Map can't be null");
		List<Document> documents = new ArrayList<>();
		for (Map<String, Object> map : maps) {
			map.put("createTime", new SimpleDateFormat(DateUtil.DATE_FORMAT_01).format(new Date()));
			documents.add(new Document(map));
		}
		MongoCollection<Document> collection = mongoDBUtil.getCollection(config.getDb(), collectionName);
		try {
			collection.insertMany(documents);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.error("saveDoc error!",e);
			return false;
		}
		return true;
	}
	
	/**
	 * 获取集合的数量
	 * 
	 * @param collectionName 集合名
	 * @param conditionMap   条件
	 * @param: @return
	 * @return: int
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:16:51 Pursuing Excelsior!
	 */
	public int getCount(String collectionName, Map<String, Object> conditionMap) {
		MongoCollection<Document> collection = mongoDBUtil.getCollection(config.getDb(), collectionName);
		return getCount(collection, conditionMap);
	}

	/**
	 * 获取集合数量
	 * 
	 * @param collectionName 集合名
	 * @param: @return
	 * @return: int
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:17:18 Pursuing Excelsior!
	 */
	public int getCount(String collectionName) {
		MongoCollection<Document> collection = mongoDBUtil.getCollection(config.getDb(), collectionName);
		return getCount(collection, null);
	}

	/**
	 * 获取集合数量
	 * 
	 * @param collection   集合
	 * @param conditionMap 条件
	 * @param: @return
	 * @return: int
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:17:26 Pursuing Excelsior!
	 */
	public int getCount(MongoCollection<org.bson.Document> collection, Map<String, Object> conditionMap) {
		BasicDBObject basicDBObject = null;
		if (conditionMap == null) {
			basicDBObject = new BasicDBObject();
		} else {
			basicDBObject = new BasicDBObject(conditionMap);
		}
		try {
			return Integer.parseInt(String.valueOf(collection.count(basicDBObject)));
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			logger.error("getCount error!",e);
			return 0;
		}
	}

	/**
	 * 获取集合数量
	 * 
	 * @param collection          集合
	 * @param conditionFieldName  字段名
	 * @param conditionFieldValue 字段值
	 * @param: @return
	 * @return: int
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:17:36 Pursuing Excelsior!
	 */
	public int getCount(MongoCollection<org.bson.Document> collection, String conditionFieldName,
			Object conditionFieldValue) {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put(conditionFieldName, conditionFieldValue);
		BasicDBObject basicDBObject = new BasicDBObject(conditionMap);
		try {
			return Integer.parseInt(String.valueOf(collection.count(basicDBObject)));
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			logger.error("getCount error!",e);
			return 0;
		}
	}

	public boolean has(MongoCollection<org.bson.Document> collection, String conditionFieldName,
			Object conditionFieldValue) {
		return getCount(collection, conditionFieldName, conditionFieldValue) > 0;
	}

	public boolean has(String collectionName, String conditionFieldName, Object conditionFieldValue) {
		return getCount(getCollection(collectionName), conditionFieldName, conditionFieldValue) > 0;
	}

	public boolean has(String collectionName, Map<String, Object> query) {
		return getCount(getCollection(collectionName), query) > 0;
	}

	/**
	 * 根据条件更新文档
	 * 
	 * @param collection 集合
	 * @param queryMap   查询条件
	 * @param updateMap  更新的Map 注意：这里只更新部分字段(即传入updateMap的字段)
	 * @param: @return
	 * @return: boolean
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:17:46 Pursuing Excelsior!
	 */
	public boolean updateDoc(String collection, Map<String, Object> queryMap, Map<String, Object> updateMap) {
		return updateDoc(getCollection(collection), queryMap, updateMap);
	}

	/**
	 * 根据条件更新文档
	 * 
	 * @param collection 集合
	 * @param queryMap   查询条件
	 * @param updateMap  更新的Map 注意：这里只更新部分字段(即传入updateMap的字段)
	 * @param: @return
	 * @return: boolean
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:18:00 Pursuing Excelsior!
	 */
	public boolean updateDoc(MongoCollection<org.bson.Document> collection, Map<String, Object> queryMap,
			Map<String, Object> updateMap) {
		BasicDBObject queryDBObject = new BasicDBObject(queryMap);
		BasicDBObject updateDBObject = new BasicDBObject(updateMap);
		BasicDBObject updateSetValue = new BasicDBObject("$set", updateDBObject);
		UpdateOptions options = new UpdateOptions();
		// options.upsert(true);
		try {
			collection.updateOne(queryDBObject, updateSetValue, options);
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			logger.error("updateDoc error!",e);
			return false;
		}
		return true;
	}

	/**
	 * 更新一个集合
	 * 
	 * @param collectionName 集合名
	 * @param queryMap       查询条件
	 * @param updateMap      更新的Map 注意：这里只更新部分字段(即传入updateMap的字段)
	 * @param: @return
	 * @return: Document
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:18:16 Pursuing Excelsior!
	 */
	public Document findOneAndUpdate(String collectionName, Map<String, Object> queryMap,
			Map<String, Object> updateMap) {
		return findOneAndUpdate(getCollection(collectionName), queryMap, updateMap);
	}

	/**
	 * 更新一个集合
	 * 
	 * @param collection 集
	 * @param queryMap   查询条件
	 * @param updateMap  更新的Map 注意：这里只更新部分字段(即传入updateMap的字段)
	 * @param: @return
	 * @return: Document
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:18:32 Pursuing Excelsior!
	 */
	public Document findOneAndUpdate(MongoCollection<org.bson.Document> collection, Map<String, Object> queryMap,
			Map<String, Object> updateMap) {
		return findOneAndUpdate(collection, queryMap, updateMap, null);
	}

	/**
	 * 更新一个集合
	 * 
	 * @param collection              集合
	 * @param queryMap                查询条件
	 * @param updateMap               更新的Map 注意：这里只更新部分字段(即传入updateMap的字段)
	 * @param findOneAndUpdateOptions
	 * @param: @return
	 * @return: Document
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:18:44 Pursuing Excelsior!
	 */
	public Document findOneAndUpdate(MongoCollection<org.bson.Document> collection, Map<String, Object> queryMap,
			Map<String, Object> updateMap, FindOneAndUpdateOptions findOneAndUpdateOptions) {
		BasicDBObject queryDBObject = new BasicDBObject(queryMap);
		BasicDBObject updateDBObject = new BasicDBObject(updateMap);
		BasicDBObject updateSetValue = new BasicDBObject("$set", updateDBObject);
		Document document = null;
		try {
			if (findOneAndUpdateOptions != null) {
				document = collection.findOneAndUpdate(queryDBObject, updateSetValue, findOneAndUpdateOptions);
			} else {
				document = collection.findOneAndUpdate(queryDBObject, updateSetValue);
			}
			return document;
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			logger.error("findOneAndUpdate error!",e);
			return null;
		}
	}

	/**
	 * 拷贝集合
	 * 
	 * @param srcCollectionName 源集合
	 * @param tarCollectionName 目标集合
	 * @param: @return
	 * @return: boolean
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:18:57 Pursuing Excelsior!
	 */
	public boolean cpCollection(String srcCollectionName, String tarCollectionName) {
		return cpCollection(srcCollectionName, tarCollectionName, config.getIp(), config.getIp(),
				Integer.toString(config.getPort()), config.getUsername(), config.getPassword(),
				Integer.toString(config.getPort()), config.getUsername(), config.getPassword(), config.getDb(),
				config.getDb());
	}

	/**
	 * 拷贝集合
	 * 
	 * @param srcCollectionName 源集合
	 * @param tarCollectionName 目标集合
	 * @param srcIp
	 * @param tarIp
	 * @param srcPort
	 * @param srcUserName
	 * @param srcPassword
	 * @param tarPort
	 * @param tarUserName
	 * @param tarPassword
	 * @param srcDBName
	 * @param tarDBName
	 * @param: @return
	 * @return: boolean
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:19:10 Pursuing Excelsior!
	 */
	public boolean cpCollection(String srcCollectionName, String tarCollectionName, String srcIp, String tarIp,
			String srcPort, String srcUserName, String srcPassword, String tarPort, String tarUserName,
			String tarPassword, String srcDBName, String tarDBName) {
		MongoClient srcClient = null;
		MongoClient tarClient = null;
		try {
			srcClient = authenticationAndGetClient(srcIp, Integer.parseInt(srcPort), srcUserName, srcPassword,
					srcDBName);
			tarClient = authenticationAndGetClient(tarIp, Integer.parseInt(tarPort), tarUserName, tarPassword,
					tarDBName);
			MongoDatabase srcDatabase = srcClient.getDatabase(srcDBName);
			MongoDatabase tarDatabase = tarClient.getDatabase(tarDBName);
			MongoCollection<Document> srcCollection = srcDatabase.getCollection(srcCollectionName);
			MongoCollection<Document> tarCollection = tarDatabase.getCollection(tarCollectionName);
			long srcCollectionCount = mongoDBUtil.getCount(srcCollection, null);
			int currCp = 0;
			FindIterable<Document> srcItr = srcCollection.find();
			MongoCursor<Document> srcCursor = srcItr.iterator();
			while (srcCursor.hasNext()) {
				Document doc = srcCursor.next();
				mongoDBUtil.saveDoc(tarCollection, doc);
				System.out.println(currCp++ + "/" + srcCollectionCount);
			}

		} catch (Exception e) {
			logger.error("cpCollection error!",e);
			return false;
		} finally {
			if (srcClient != null)
				srcClient.close();
			if (tarClient != null)
				tarClient.close();
		}
		return true;
	}

	/**
	 * 获取索引列表，只支持单索引
	 * 
	 * @param collectionName
	 * @return
	 */
	public List<MongoDBIndex> getIndexList(String collectionName) {
		List<MongoDBIndex> mongoDBIndexs = new ArrayList<MongoDBIndex>();
		ListIndexesIterable<Document> listIndexs = getCollection(collectionName).listIndexes();
		MongoCursor<Document> itr = listIndexs.iterator();
		while (itr.hasNext()) {
			Document doc = itr.next();
			Object key = doc.get("key");
			Boolean unique = doc.getBoolean("unique");
			if (key instanceof Document) {
				Document keyDocument = (Document) key;
				Iterator<String> keysItr = keyDocument.keySet().iterator();
				if (keysItr.hasNext()) {// 只支持单索引
					String k = keysItr.next();
					if (unique == null) {
						unique = false;
					}
					MongoDBIndex mongoDBIndex = new MongoDBIndex(k, unique);
					mongoDBIndexs.add(mongoDBIndex);
				}
			}
		}
		return mongoDBIndexs;
	}

	/**
	 * 添加索引
	 * 
	 * @param collectionName 集合名
	 * @param mongoDBIndexs    索引
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:19:25 Pursuing Excelsior!
	 */
	public void addIndexs(String collectionName, List<MongoDBIndex> mongoDBIndexs) {
		List<MongoDBIndex> midxs = getIndexList(collectionName);
		List<String> existsIdxNames = new ArrayList<String>();
		for (MongoDBIndex midx : midxs) {
			existsIdxNames.add(midx.getIndexName());
		}
		List<IndexModel> models = new ArrayList<IndexModel>();
		for (MongoDBIndex mongoDBIndex : mongoDBIndexs) {
			if (existsIdxNames.contains(mongoDBIndex.getIndexName())) {
				continue;
			}
			BasicDBObject fieldDBObj = new BasicDBObject();
			fieldDBObj.put(mongoDBIndex.getIndexName(), 1);
			IndexOptions indexOptions = new IndexOptions();
			indexOptions.unique(mongoDBIndex.getUnique());
			IndexModel indexModel = new IndexModel(fieldDBObj, indexOptions);
			models.add(indexModel);
		}
		getCollection(collectionName).createIndexes(models);
	}

	/**
	 * 查询集合
	 * 
	 * @param collectionName 集合名
	 * @param: @return
	 * @return: FindIterable<Document>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:19:37 Pursuing Excelsior!
	 */
	public FindIterable<Document> find(String collectionName) {
		return find(collectionName, null, null, false, null);
	}

	/**
	 * 查询集合
	 * 
	 * @param collectionName 集合名
	 * @param projection     查哪些字段
	 * @param: @return
	 * @return: FindIterable<Document>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:19:47 Pursuing Excelsior!
	 */
	public FindIterable<Document> find(String collectionName, Map<String, Object> projection) {
		return find(collectionName, null, null, false, projection);
	}

	/**
	 * 查询集合
	 * 
	 * @param collectionName 集合名
	 * @param limit          限制几条
	 * @param: @return
	 * @return: FindIterable<Document>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:20:17 Pursuing Excelsior!
	 */
	public FindIterable<Document> find(String collectionName, Integer limit) {
		return find(collectionName, null, limit, false, null);
	}

	/**
	 * 查询集合
	 * 
	 * @param collectionName  集合名
	 * @param filter          过滤条件
	 * @param limit           限制几条
	 * @param noCursorTimeout true:索引不超时 false:索引超时
	 * @param projection      查哪些字段
	 * @param: @return
	 * @return: FindIterable<Document>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:20:37 Pursuing Excelsior!
	 */
	public FindIterable<Document> find(String collectionName, Map<String, Object> filter, Integer limit,
			boolean noCursorTimeout, Map<String, Object> projection) {
		return find(collectionName, filter, limit, null, noCursorTimeout, projection);
	}

	/**
	 * 查询集合
	 * 
	 * @param collectionName  集合名
	 * @param filter          过滤条件
	 * @param limit           限制几条
	 * @param skip            跳过多少条
	 * @param noCursorTimeout true:索引不超时 false:索引超时
	 * @param projection      查哪些字段
	 * @param: @return
	 * @return: FindIterable<Document>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:21:56 Pursuing Excelsior!
	 */
	public FindIterable<Document> find(String collectionName, Map<String, Object> filter, Integer limit, Integer skip,
			boolean noCursorTimeout, Map<String, Object> projection) {
		BasicDBObject find = null;
		BasicDBObject projectionBson = null;
		if (filter != null) {
			find = new BasicDBObject(filter);
		} else {
			find = new BasicDBObject();
		}
		if (projection != null) {
			projectionBson = new BasicDBObject(projection);
		} else {
			projectionBson = new BasicDBObject();
		}
		FindIterable<Document> findIterable = getCollection(collectionName).find(find).projection(projectionBson);
		if (limit != null) {
			if (limit <= 0) {
				throw new RuntimeException("Find limit must > 0");
			}
			findIterable = findIterable.limit(limit);
		}
		if (skip != null && skip > 0) {
			findIterable = findIterable.skip(skip);
		}
		return findIterable.noCursorTimeout(noCursorTimeout);
	}

	/**
	 * 遍历
	 * 
	 * @param 集合名
	 * @param filter          过滤条件
	 * @param limit           限制几条
	 * @param noCursorTimeout true:索引不超时 false:索引超时
	 * @param: @return
	 * @return: MongoCursor<Document>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:22:36 Pursuing Excelsior!
	 */
	public MongoCursor<Document> iterator(String collectionName, Map<String, Object> filter, Integer limit,
			boolean noCursorTimeout) {
		return find(collectionName, filter, limit, noCursorTimeout, null).iterator();
	}

	public MongoCursor<Document> iterator(String collectionName, Map<String, Object> filter, Integer limit,
			Integer skip, boolean noCursorTimeout) {
		return find(collectionName, filter, limit, skip, noCursorTimeout, null).iterator();
	}

	/**
	 * 遍历
	 * 
	 * @param collectionName
	 * @return
	 */
	public MongoCursor<Document> iterator(String collectionName) {
		return iterator(collectionName, null, null, false);
	}

	/**
	 * 根据集合名查询
	 * 
	 * @param collectionName
	 * @return
	 */
	public List<Document> findDocList(String collectionName) {
		return findDocList(collectionName, null, null, false);
	}

	/**
	 * 
	 * @param collectionName
	 * @return
	 */
	public List<Document> findDocList(String collectionName, Map<String, Object> filter) {
		return findDocList(collectionName, filter, null, false);
	}

	public List<Document> findDocList(String collectionName, Map<String, Object> filter, Integer limit, Integer skip,
			boolean noCursorTimeout) {
		MongoCursor<Document> cursor = mongoDBUtil.iterator(collectionName, filter, limit, skip, noCursorTimeout);
		List<Document> docs = new ArrayList<Document>();
		/*int currCursor = 0;
		int totalCount = mongoDBUtil.getCount(collectionName);*/
		while (cursor.hasNext()) {
			/*currCursor++;
			if (currCursor % 100 == 0 || currCursor == totalCount) {
				logger.info(String.format("[collectionName] %s progress : %s", collectionName,
						currCursor + "/" + totalCount));
			}*/
			Document doc = cursor.next();
			docs.add(doc);
		}
		return docs;
	}

	/**
	 * 查询
	 * 
	 * @param collectionName  集合名
	 * @param filter          过滤条件
	 * @param limit           限制几条
	 * @param noCursorTimeout true:索引不超时 false:索引超时
	 * @param: @return
	 * @return: List<Document>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:24:57 Pursuing Excelsior!
	 */
	public List<Document> findDocList(String collectionName, Map<String, Object> filter, Integer limit,
			boolean noCursorTimeout) {
		return findDocList(collectionName, filter, limit, null, noCursorTimeout);
	}

	/**
	 * 获取集合的cursor
	 * 
	 * @param collectionName 集合名称
	 * @param limit          限制返回的数量
	 * @return
	 */
	public MongoCursor<Document> iterator(String collectionName, Integer limit) {
		return iterator(collectionName, null, limit, false);
	}

	/**
	 * 获取集合的cursor
	 * 
	 * @param collectionName  集合名称
	 * @param noCursorTimeout 游标不过期 true（不过期） false（自动过期）
	 * @return 游标
	 */
	public MongoCursor<Document> iterator(String collectionName, boolean noCursorTimeout) {
		return iterator(collectionName, null, null, noCursorTimeout);
	}

	/**
	 * 查一个集合
	 * 
	 * @param collectionName 集合名
	 * @param queryMap       查询条件
	 * @return
	 */
	public Document findOne(String collectionName, Map<String, Object> queryMap) {
		Document resultDoc = null;
		MongoCursor<Document> findResult = iterator(collectionName, queryMap, 1, false);
		boolean hasNext = findResult.hasNext();
		if (hasNext) {
			resultDoc = findResult.next();
		}
		return resultDoc;
	}

	/**
	 * Find one from collectionName
	 * 
	 * @param collectionName 集合名
	 * @param queryMap       查询条件
	 * @return
	 */
	public Document findOne(String collectionName, String key, Object value) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put(key, value);
		return findOne(collectionName, queryMap);
	}

	/**
	 * 查询
	 * 
	 * @param collectionName 集合名
	 * @param projection     查哪些字段
	 * @param: @return
	 * @return: MongoCursor<Document>
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:24:03 Pursuing Excelsior!
	 */
	public MongoCursor<Document> iterator(String collectionName, Map<String, Object> projection) {
		return find(collectionName, projection).iterator();
	}

	/**
	 * 根据过滤条件删除
	 * 
	 * @param collectionName 集合名
	 * @param filter         过滤条件
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:23:56 Pursuing Excelsior!
	 */
	public void delete(String collectionName, Map<String, Object> filter) {
		if (filter == null || filter.size() == 0) {
			throw new RuntimeException("Filter can't be empty!");
		}
		BasicDBObject find = new BasicDBObject(filter);
		getCollection(collectionName).deleteMany(find);
	}

	/**
	 * 根据字段名 字段值 删除
	 * 
	 * @param collectionName 集合名
	 * @param fieldName      字段名
	 * @param fieldValue     字段值
	 * @return: void
	 * @throws:
	 * @author: bugao.gong
	 * @date: 2019年1月8日 下午5:23:39 Pursuing Excelsior!
	 */
	public void delete(String collectionName, String fieldName, String fieldValue) {
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(fieldName, fieldValue);
		delete(collectionName, filter);
	}
}
