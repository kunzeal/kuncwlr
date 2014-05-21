package kuncwlr;

import java.util.regex.Matcher;

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
			String file;
			if(sa.length>1){
				file = "/"+sa[1];
			}else file = "/";
			sa = Constants.RegexSet.PATTERN_EXT_ABS_WS_PORT.split(uri);
			int port;
			if(sa.length>1&&sa[1].matches("[0-9]+")){
				port = Integer.parseInt(sa[1]);
			}
			else port = 80;
			if(host.equals(srcItem.getHost().toString()))
				return Item.getNewInstance(srcItem.getHost(), port, file, srcItem.getDeepth()+1);
			return Item.getNewInstance(new Host(host), port, file, srcItem.getDeepth()+1);
		}
		//absolute uri with no scheme
		else if(type == Constants.UriTypesCons.URI_TYPE_ABSOLUTE_NS){
			return Item.getNewInstance(srcItem.getHost(), srcItem.getPortNo(), uri, srcItem.getDeepth()+1);
		}
		//relative uri
		else if(type == Constants.UriTypesCons.URI_TYPE_RELATIVE){
			String currentDir = srcItem.getFile();
			if(currentDir.endsWith("/")){
				//end with slash
				return Item.getNewInstance(srcItem.getHost(), srcItem.getPortNo(), srcItem.getFile()+uri, srcItem.getDeepth()+1);
			}else
			{
				//end not end with slash
				Matcher m = Constants.RegexSet.PATTERN_EXT_REL_NENDS_DIR.matcher(srcItem.getFile());
				m.find();
				currentDir = m.group();
				return Item.getNewInstance(srcItem.getHost(), srcItem.getPortNo(), currentDir+uri, srcItem.getDeepth()+1);
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