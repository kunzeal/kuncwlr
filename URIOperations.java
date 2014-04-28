package kuncwlr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URIOperations {
	public static Item getItem(Item srcItem, String uri){
		int type = getURIType(uri);
		if(type==Constants.UriTypesCons.URI_TYPE_WRONG) return null;
		String [] sa;
		//absolute uri with scheme
		if(type == Constants.UriTypesCons.URI_TYPE_ABSOLUTE_WS){
			sa = Constants.RegexSet.PATTERN_EXT_ABS_WS_HOST.split(uri);
			String host = sa[1];
			sa = Constants.RegexSet.PATTERN_EXT_ABS_WS_FILE.split(uri);
			String file = "/"+sa[1];
			sa = Constants.RegexSet.PATTERN_EXT_ABS_WS_PORT.split(uri);
			int port = Integer.parseInt(sa[1]);
			if(host.equals(srcItem.getHost().toString()))
				return new Item(srcItem.getHost(), file, port);
			return new Item(new Host(host), file, port);
		}
		//absolute uri with no scheme
		else if(type == Constants.UriTypesCons.URI_TYPE_ABSOLUTE_NS){
			return new Item(srcItem.getHost(), uri);
		}
		//relative uri
		else if(type == Constants.UriTypesCons.URI_TYPE_RELATIVE){
			String currentDir = srcItem.getFile();
			if(currentDir.endsWith("/")){
				//end with slash
				return new Item(srcItem.getHost(), srcItem.getFile()+uri, srcItem.getPortNo());
			}else
			{
				//end not end with slash
				Matcher m = Constants.RegexSet.PATTERN_EXT_REL_NENDS_DIR.matcher(srcItem.getFile());
				m.find();
				currentDir = m.group();
				return new Item(srcItem.getHost(),currentDir+uri, srcItem.getPortNo());
			}
		} 
//		return new Item(new Host("221.130.120.178"), "/info/cm/ah/"+uri, 8080);
		return null;
	}
	public static int getURIType(String uri){
		Matcher m;
		m = Constants.RegexSet.PATTERN_ABSOLUTE_WS.matcher(uri);
		if(m.matches()){
//			System.out.println("match the absolute ws model");
			return Constants.UriTypesCons.URI_TYPE_ABSOLUTE_WS;
		}
		m = Constants.RegexSet.PATTERN_ABSOLUTE_NS.matcher(uri);
		if(m.matches()){
//			System.out.println("match the absolute ns model");
			return Constants.UriTypesCons.URI_TYPE_ABSOLUTE_NS;
		}
		m = Constants.RegexSet.PATTERN_RELATIVE.matcher(uri);
		if(m.matches()){
//			System.out.println("match the relative model");
//			System.out.println("relative module "+uri);;
			return Constants.UriTypesCons.URI_TYPE_RELATIVE;
		}
		return Constants.UriTypesCons.URI_TYPE_WRONG;
	}
}