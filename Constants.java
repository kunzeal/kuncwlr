package kuncwlr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {
	public static class UriTypesCons{
		//absolute with scheme uri type
		public static final int URI_TYPE_ABSOLUTE_WS = 1;
		//absolute with no scheme uri type
		public static final int URI_TYPE_ABSOLUTE_NS = 2;
		//relative uri type
		public static final int URI_TYPE_RELATIVE = 3;
		//wrong type
		public static final int URI_TYPE_WRONG = -1;
	}
	
	public static class RegexSet{
		public static final Pattern PATTERN_RELATIVE =
				Pattern.compile("\\b[-A-Za-z0-9+&@#/%=~_|!:,.;]*(/[-A-Za-z0-9+&@#/%=~_|!:,.;]*)?(\\?[-A-Za-z0-9+&@#/%=~_|!:,.;]*)?");
		//absolute uri with scheme
		public static final Pattern PATTERN_ABSOLUTE_WS =
				Pattern.compile("\\bhttps?://([-A-Za-z0-9.:]+)(/[-A-Za-z0-9+&@#/%=~_|!:,.;]*)?(\\?[A-Za-z0-9+&@#/%=~_|!:,.;]*)?");
		//absolute uri with no scheme and no host, or just absolute directory from host root
		//here is a problem such as /////
		public static final Pattern PATTERN_ABSOLUTE_NS =
				Pattern.compile("(/[-A-Za-z0-9+&@#/%=~_|!:,.;]*)+(\\?[A-Za-z0-9+&@#/%=~_|!:,.;]*)?");
		
		//split 1
		public static final Pattern PATTERN_EXT_ABS_WS_HOST = Pattern.compile("https?://|/|:");
		//split 1
		public static final Pattern PATTERN_EXT_ABS_WS_PORT = Pattern.compile("https?://[-A-Za-z0-9.]+:|/");
		//split 1
		public static final Pattern PATTERN_EXT_ABS_WS_FILE = Pattern.compile("https?://[-A-Za-z0-9.:]+/");
		//matcher find and group
		public static final Pattern PATTERN_EXT_REL_NENDS_DIR = Pattern.compile("(/[-A-Za-z0-9+&@#/%=~_|!:,.;]*)?/");
		public static final Pattern PATTERN_EXT_REL = Pattern.compile("");
	}
	
	//for test
	public static void main(String [] args){
		Matcher m;
		/*
		 * test 1
		m = RegexSet.PATTERN_RELATIVE.matcher("setprefs?sig=0_T5EPPqnKeXKRNBpSyMSYQJYmxoU%3D&hl=zh-TW&source=homepage");
		if(m.matches())System.out.println("it matches!!");
		*/
		
		/*
		 * test PATTERN_EXT_ABS_WS_HOST and PATTERN_EXT_ABS_WS_FILE
//		String tmpuri = "http://www.baidu.com/link?url=DdOdpOaVYsCFZNWF_vqYKXrQiyGW-9U2BKHhXE9Kr9NcFC0KkVHnn9st2MfqdLzg5Vp22FrB7Nn-8bgndt6-xTUtLx3Hua9al6Btb3b007_&wd=windows%20dos%E5%91%BD%E4%BB%A4&ie=utf-8&tn=baiduad&f=12&oq=windows%20doc%E5%91%BD%E4%BB%A4&rsp=0&inputT=21337&bs=mv%20windows";
//		String tmpuri = "http://sports.sina.com.cn/g/pl/2014-04-15/09447119919.shtml";
		String tmpuri = "http://221.130.120.178:8088/self/self_logon.jsp";
		m = Constants.RegexSet.PATTERN_ABSOLUTE_WS.matcher(tmpuri);
		if(m.matches()) System.out.println("it matches!");
		String [] sa = Constants.RegexSet.PATTERN_EXT_ABS_WS_HOST.split(tmpuri);
		System.out.println("split host:"+sa[1]);
		sa = Constants.RegexSet.PATTERN_EXT_ABS_WS_FILE.split(tmpuri);
		System.out.println("split:"+sa[1]);
		*/
		
		/*
		 * test PATTERN_EXT_REL_NENDS_DIR
//		String tmpuri = "/link?url=DdOdpOaVYsCFZNWF_vqYKXrQiyGW-9U2BKHhXE9Kr9NcFC0KkVHnn9st2MfqdLzg5Vp22FrB7Nn-8bgndt6-xTUtLx3Hua9al6Btb3b007_&wd=windows%20dos%E5%91%BD%E4%BB%A4&ie=utf-8&tn=baiduad&f=12&oq=windows%20doc%E5%91%BD%E4%BB%A4&rsp=0&inputT=21337&bs=mv%20windows";
		String tmpuri = "/g/pl/2014-04-15/09447119919.shtml";
		m = Constants.RegexSet.PATTERN_EXT_REL_NENDS_DIR.matcher(tmpuri);
		if(m.find())
			System.out.println("ext dir:"+m.group());
		*/
		
		/*
		 * test PATTER_EXT_ABS_WS_PORT
		String tmpuri = "http://221.130.120.178:8088/self/self_logon.jsp";
		String [] sa = Constants.RegexSet.PATTERN_EXT_ABS_WS_PORT.split(tmpuri);
		System.out.println(sa[1]);
		*/
		
		/*
		 * test abs with scheme
		 */
	}
}
