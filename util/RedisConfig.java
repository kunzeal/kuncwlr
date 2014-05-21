package kuncwlr.util;

public class RedisConfig {
	private static final int MAX_ACTIVE = 500;
	private static final int MAX_IDLE = 5;
	private static final int MAX_WAIT = 10000;
	private static final int TIME_OUT = 2000;

	public static int getMaxactive(){
		return MAX_ACTIVE;
	}
	
	public static int getMaxidle(){
		return MAX_IDLE;
	}
	
	public static int getMaxwait(){
		return MAX_WAIT;
	}
	
	public static int getTimeout(){
		return TIME_OUT;
	}
}