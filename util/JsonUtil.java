package jsonlibTest;

import net.sf.json.JSONObject;

public class JsonUtil {
	public static String toJson(Object obj){
		return JSONObject.fromObject(obj).toString();
	}
	
	public static Object toObj(String jsonStr, Class c){
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		return JSONObject.toBean(jsonObject, c);
	}
}
