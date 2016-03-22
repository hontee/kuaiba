package com.kuaiba.site.core.cache;

import java.io.IOException;

import com.kuaiba.site.core.exception.CacheException;
import com.kuaiba.site.core.exception.SecurityException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

public class MemcachedUtil {

	final static String host = "127.0.0.1";
	final static String port = "11211";
	
	/**
	 * 获取连接
	 * @return
	 * @throws IOException
	 */
	private static MemcachedClient getMemcachedClient() throws IOException {
		return new MemcachedClient(new BinaryConnectionFactory(), 
				AddrUtil.getAddresses(host + ":" + port));
	}
	
	/**
	 * 销毁连接
	 * @param client
	 */
	private static void destory(MemcachedClient client) {
		if (client != null) {
			client.shutdown();
		}
	}
	
	/**
	 * 设置缓存对象
	 * @param key
	 * @param expire
	 * @param value
	 */
	public static void set(CacheIDs key, int expire, Object value) throws SecurityException {
		MemcachedClient client = null;
		try {
			client = getMemcachedClient();
			OperationFuture<Boolean> future = null;
			
			if (exists(key)) {
				client.replace(key.name(), expire, value);
			} else {
				future = client.set(key.name(), expire, value);
			}
			
			future.get(); // 确保之前(mc.set())操作已经结束
		} catch (Exception e) {
			throw new CacheException("设置缓存失败: " + key.name(), e);
		} finally {
			destory(client);
		}
	}
	
	/**
	 * 获取缓存对象
	 * @param key
	 * @param expire
	 * @param value
	 */
	public static Object get(CacheIDs key) throws SecurityException {
		MemcachedClient client = null;
		try {
			client = getMemcachedClient();
			return client.get(key.name());
		} catch (IOException e) {
			throw new CacheException("获取缓存失败: " + key.name(), e);
		} finally {
			destory(client);
		}
	}
	
	/**
	 * 删除缓存对象
	 * @param key
	 * @param expire
	 * @param value
	 */
	public static void delete(CacheIDs key) throws SecurityException {
		MemcachedClient client = null;
		try {
			client = getMemcachedClient();
			OperationFuture<Boolean> future =  client.delete(key.name());
			future.get();
		} catch (Exception e) {
			throw new CacheException("删除缓存失败: " + key.name(), e);
		} finally {
			destory(client);
		}
	}
	
	/**
	 * 检测是否存在缓存对象
	 * @param key
	 * @param expire
	 * @param value
	 * @throws SecurityException 
	 */
	public static boolean exists(CacheIDs key) throws SecurityException {
		return get(key) != null;
	}
	
	/**
	 * 刷新所有缓存
	 * @throws CacheException
	 */
	public static void flush() throws CacheException {
		MemcachedClient client = null;
		try {
			client = getMemcachedClient();
			OperationFuture<Boolean> future =  client.flush();
			future.get();
		} catch (Exception e) {
			throw new CacheException("刷新缓存失败: ", e);
		} finally {
			destory(client);
		}
	}

}