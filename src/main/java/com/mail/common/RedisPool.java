package com.mail.common;

import com.mail.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {

    private static JedisPool pool;  //jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idel","10")); //在jedispool中的最大idle状态的jedis实例个数
    private static Integer minIdle =Integer.parseInt(PropertiesUtil.getProperty("redis.min.idel","2")) ; //在jedispool中的最小idle状态的jedis实例个数
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true")); //在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值为true，那么每次获取都是肯定会用的
    private static Boolean testOnReturn =Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true")) ; //在return一个jedis实例的时候，是否要进行验证操作，如果赋值为true，那么每次放回都是肯定会用的

    private static Integer port = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));
    private static String ip = PropertiesUtil.getProperty("redis.ip");

    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        pool = new JedisPool(config,ip,port,1000*2);
    }

    static {
        initPool();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(Jedis jedis){
            pool.getResource();
    }
    public static void returnBrokenResource(Jedis jedis){
            pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args){
        Jedis jedis =getJedis();
        jedis.sadd("zjl","zjl");
        returnResource(jedis);
        System.out.println("end~~~~~~~~~~~~~~~~~~~~~~~~");

    }
}
