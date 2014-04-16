package kuncwlr;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;
import org.htmlparser.*;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class htmlparserExtractor implements HTMLExtractor{
	private Parser htmlparser;
	public htmlparserExtractor(){
		this.htmlparser = new Parser();
	}

	@Override
	public Set<String> XMLextract(Page page) throws SAXException,
			IOException {
		// TODO Auto-generated method stub
		try {
			htmlparser.setEncoding("utf-8");
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("set encoding error!!");
		}
		System.out.println(page.toString());
		Pattern ptn = Pattern.compile("<html[\\s\\S]*>[\\s\\S]*</html>");
		Matcher m = ptn.matcher(page.toString());
//		if(!m.matches()){
//			System.out.println("no matches!!!");
//			return;
//		}
		m.find();
		String str = m.group();
		try {
			htmlparser.setInputHTML(str);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("set input html error!!");
		}
		
		NodeFilter filter = new TagNameFilter("a");
		NodeList nodelist = null;
		try {
			nodelist = htmlparser.extractAllNodesThatMatch(filter);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(nodelist == null){
			System.out.println("nodelist is null!!");
			return null;
		}
		int length = nodelist.size();
		System.out.println("%%%%%%%%%%%%%extraction results%%%%%%%%%%%%%%%%");
		Set<String> uriset = new HashSet<String>();
		System.out.println("adding!!");
		for(int i = 0;i<length;i++){
			System.out.println(nodelist.elementAt(i).toHtml());
			LinkTag n = (LinkTag)nodelist.elementAt(i);
			uriset.add(n.getAttribute("href"));
			if(n.getAttribute("href") == ""){
				continue;
			}
			System.out.println("href is:"+n.getAttribute("href"));
		}
		System.out.println("added!!");
		
		return uriset;
	}

}
