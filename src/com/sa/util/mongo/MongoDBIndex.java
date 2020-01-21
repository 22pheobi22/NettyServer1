package com.sa.util.mongo;

/**
 * 
 * Mongo索引 封装
 * @author bugao.gong
 * @version 创建时间：2019年1月8日 下午5:49:22
 * Pursuing Excelsior!
 */
public class MongoDBIndex {
	private String indexName;
	private boolean unique;
	
	public MongoDBIndex() {
		super();
	}
	public MongoDBIndex(String indexName, boolean unique) {
		super();
		this.indexName = indexName;
		this.unique = unique;
	}
	public MongoDBIndex(String indexName) {
		super();
		this.indexName = indexName;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public boolean getUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
}
