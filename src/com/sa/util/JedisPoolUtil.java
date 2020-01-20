package com.sa.util;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Pipeline;

public class JedisPoolUtil {
    protected static Logger logger = LoggerFactory.getLogger(JedisPoolUtil.class);

    public static JedisSentinelPool jedisPool = null;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private boolean blockWhenExhausted;
    private int maxWaitMillis;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private String sentinel1;
    private String sentinel2;
    private String sentinel3;

    public static Lock lock = new ReentrantLock();

    private void initialConfig() {
        try {
    		String fileName = System.getProperty("user.dir")+"/bin/conf/setting.conf";
            InputStream stream = JedisPoolUtil.class.getResourceAsStream("/conf/redis.properties");
            Properties prop = new Properties();
            prop.load(stream);

            maxTotal = Integer.parseInt(prop.getProperty("redis.maxTotal"));
            maxIdle = Integer.parseInt(prop.getProperty("redis.maxIdle"));
            minIdle = Integer.parseInt(prop.getProperty("redis.minIdle"));
            blockWhenExhausted = Boolean.parseBoolean(prop.getProperty("redis.blockWhenExhausted"));
            maxWaitMillis = Integer.parseInt(prop.getProperty("redis.maxWaitMillis"));
            testOnBorrow = Boolean.parseBoolean(prop.getProperty("redis.testOnBorrow"));
            testOnReturn = Boolean.parseBoolean(prop.getProperty("redis.testOnReturn"));
            sentinel1 = prop.getProperty("redis.sentinel1");
            sentinel2 = prop.getProperty("redis.sentinel2");
            sentinel3 = prop.getProperty("redis.sentinel3");

//            boolean testWhileIdle = Boolean.parseBoolean(prop.getProperty("redis.testWhileIdle"));
//            int timeBetweenEvictionRunsMillis = Integer.parseInt(prop.getProperty("redis.timeBetweenEvictionRunsMillis"));
//            int minEvictableIdleTimeMillis = Integer.parseInt(prop.getProperty("redis.minEvictableIdleTimeMillis"));
//            int numTestsPerEvictionRun = Integer.parseInt(prop.getProperty("redis.numTestsPerEvictionRun"));
        } catch (Exception e) {
            logger.debug("parse configure file error.");
        }
    }

    /**
     * initial redis pool
     */
    private void initialPool() {
        if (lock.tryLock()) {
            lock.lock();
            initialConfig();
            try {
                JedisPoolConfig config = new JedisPoolConfig();
                //config.setMaxTotal(maxTotal);
                //config.setMaxIdle(maxIdle);
                //config.setMaxWaitMillis(maxWaitMillis);
                //conf
                
                //声明一个set 存放烧饼集群的地址和端口
                Set<String> sentinels = new HashSet<String>();
                sentinels.add(sentinel1);
                sentinels.add(sentinel2);
                sentinels.add(sentinel3);
                // 名称 sentinel pool timeout
                jedisPool = new JedisSentinelPool("mymaster", sentinels, config,2000,null,0);
                //config.setTestOnBorrow(testOnBorrow);
                //jedisPool = new JedisPool(config, host, port);
            } catch (Exception e) {
                logger.debug("init redis pool failed : {}", e.getMessage());
            } finally {
                lock.unlock();
            }
        } else {
            logger.debug("some other is init pool, just wait 1 second.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public Jedis getJedis() {

        if (jedisPool == null) {
            initialPool();
        }
        try {
            return jedisPool.getResource();
        } catch (Exception e) {
            logger.debug("getJedis() throws : {}" + e.getMessage());
        }
        return null;
    }

}