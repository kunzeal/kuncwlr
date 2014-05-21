package kuncwlr.util;

import org.apache.http.impl.execchain.RetryExec;

public class BaseConfig {
	private static final int retryNum = 10;
	public static int getRetryNum(){
		return retryNum;
	}
}
