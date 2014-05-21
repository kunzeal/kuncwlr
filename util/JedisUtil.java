package kuncwlr.util;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

	public class JedisUtil  {
		
		public static void main(String [] args){
			Jedis jedis = JedisUtil.getInstance().getJedis();
			jedis.setnx("aa","{'name':'zhoujie', 'age':'20'}");
			jedis.setnx("bb", "{'name':'yilin', 'age':'28'}");
			String str = jedis.get("aa name");
			System.out.println(str);
		}
		
		
//	    protected Logger log = LoggerFactory.getLogger(getClass());
		
		//默认只使用localhost的redis
		private static final String ip = "127.0.0.1";
		private static final int port = 6379;
	    
		/**
		 * 私有构造器.
		 */
		private JedisUtil() {

		}
		private static Map<String,JedisPool> maps  = new HashMap<String,JedisPool>();
		
	    
	    /**
	     * 获取连接池.
	     * @return 连接池实例
	     */
	    private static JedisPool getPool() {
	    	String key = ip+":" +port;
	    	JedisPool pool = null;
	        if(!maps.containsKey(key)) {
	            JedisPoolConfig config = new JedisPoolConfig();
	            config.setMaxActive(RedisConfig.getMaxactive());
	            config.setMaxIdle(RedisConfig.getMaxidle());
	            config.setMaxWait(2);
	            config.setTestOnBorrow(true);
	            config.setTestOnReturn(true);
	            try{  
	                /**
	                 *如果你遇到 java.net.SocketTimeoutException: Read timed out exception的异常信息
	                 *请尝试在构造JedisPool的时候设置自己的超时值. JedisPool默认的超时时间是2秒(单位毫秒)
	                 */
	                pool = new JedisPool(config, ip, port,RedisConfig.getTimeout());
	                maps.put(key, pool);
	            } catch(Exception e) {
	                e.printStackTrace();
	            }
	        }else{
	        	pool = maps.get(key);
	        }
	        return pool;
	    }

	    /**
	     *类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
	     *没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载。
	     */
	    private static class RedisUtilHolder{
	        /**
	         * 静态初始化器，由JVM来保证线程安全
	         */
	        private static JedisUtil instance = new JedisUtil();
	    }

	    /**
	     *当getInstance方法第一次被调用的时候，它第一次读取
	     *RedisUtilHolder.instance，导致RedisUtilHolder类得到初始化；而这个类在装载并被初始化的时候，会初始化它的静
	     *态域，从而创建RedisUtil的实例，由于是静态的域，因此只会在虚拟机装载类的时候初始化一次，并由虚拟机来保证它的线程安全性。
	     *这个模式的优势在于，getInstance方法并没有被同步，并且只是执行一个域的访问，因此延迟初始化并没有增加任何访问成本。
	     */
		public static JedisUtil getInstance() {
			return RedisUtilHolder.instance;
		}
		
		/**
		 * 获取Redis实例.
		 * @return Redis工具类实例
		 */
		public Jedis getJedis() {
			Jedis jedis  = null;
			int count =0;
			do{
	    		try{ 
	    			jedis = getPool().getResource();
	    			//log.info("get redis master1!");
	    		} catch (Exception e) {
//	    			log.error("get redis master1 failed!", e);
	    			 // 销毁对象  
	    			getPool().returnBrokenResource(jedis);  
	    		}
	    		count++;
			}while(jedis==null&&count<BaseConfig.getRetryNum());
			return jedis;
		}

		/**
		 * 释放redis实例到连接池.
	     * @param jedis redis实例
	     */
		public void closeJedis(Jedis jedis) {
			if(jedis != null) {
			    getPool().returnResource(jedis);
			}
		}
	}
