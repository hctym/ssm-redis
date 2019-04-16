package com.hctym.utils;

import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component("redisCache")
public class RedisCacheUtil {
	
	@Resource
	private RedisTemplate<String, Object>  redisTemplate;
	
	public boolean EXISTS(String key){
		if(key == null || "".equals(key)){
            return false;
        }
        return redisTemplate.hasKey(key);
	}
	
	public boolean EXPIRE(String key,long timeout){
		if(key == null || "".equals(key)){
            return false;
        }
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}
	
	/**
     * 向Hash中添加值
     * @param key      可以对应数据库中的表名
      * @param field    可以对应数据库表中的唯一索引
     * @param value    存入redis中的值
     */
    public void hset(String key, String field, String value) {
        if(key == null || "".equals(key)){
            return ;
        }
        redisTemplate.opsForHash().put(key, field, value);
    }
    
    /**
     * 从redis中取出值
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field){
        if(key == null || "".equals(key)){
            return null;
        }
        return (String) redisTemplate.opsForHash().get(key, field);
    }
    
    /**
     * 判断 是否存在 key 以及 hash key
     * @param key
     * @param field
     * @return
     */
    public boolean hexists(String key, String field){
        if(key == null || "".equals(key)){
            return false;
        }
        return redisTemplate.opsForHash().hasKey(key, field);
    }
    
    /**
     * 查询 key中对应多少条数据
     * @param key
     * @return
     */
    public long hsize(String key) {
        if(key == null || "".equals(key)){
            return 0L;
        }
        return redisTemplate.opsForHash().size(key);
    }
    
    /**
     * 删除
     * @param key
     * @param field
     */
    public void hdel(String key, String field) {
        if(key == null || "".equals(key)){
            return;
        }
        redisTemplate.opsForHash().delete(key, field);
    }
    
    /**
     * 左进
     * @param key
     * @param value
     * @return
     */
    public long lpush(String key,String value){
    	 if(key == null || "".equals(key)){
             return 0L;
         }
    	 return redisTemplate.opsForList().leftPush(key, value);
    }
    
    /**
     * 右出
     * @param key
     * @return
     */
    public String lpop(String key){
    	if(key == null || "".equals(key)){
            return "";
        }
   	 	return (String) redisTemplate.opsForList().rightPop(key);
   }
    
    /**
     * hash 锁
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean hsetnx(String key, String field, String value){
    	if(StringUtils.isBlank(key)){
    		return false;
    	}
    	return redisTemplate.opsForHash().putIfAbsent(key, field, value);
    }
    
    /**
     * string key
     * @param key
     * @param value
     * @return
     */
    public boolean setnx(String key, String value){
    	if(StringUtils.isBlank(key)){
    		return false;
    	}
    	return redisTemplate.opsForValue().setIfAbsent(key, value);
    }
    
    /**
     * 
     * @param key
     */
    public void del(String key){
    	if(StringUtils.isBlank(key)){
    		return;
    	}
    	redisTemplate.delete(key);
    }
    
    
    
    
    public static void main(String[] args) {
		new RedisCacheUtil().EXISTS("POS");
	}
}
