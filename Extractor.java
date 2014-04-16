package kuncwlr;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.xml.sax.SAXException;

public class Extractor {
	//<Item,Page> without extraction
	private Map<Item, Page> map;
	//<Item, Page> has been extracted
	private Map<Item, Page> extractedMap;
	private Queue<Item> lq;
	private HTMLExtractor xmlextractor;
	
	public Extractor(Map<Item, Page> map, Map<Item, Page> extractedMap, Queue<Item> lq, HTMLExtractor xmlextractor){
		this.map = map;
		this.extractedMap = extractedMap;
		this.lq = lq;
		this.xmlextractor = xmlextractor;
	}
	
	public void extractingOps(){
		if(map.isEmpty()) return;
		
		Iterator<Entry<Item, Page>> it = map.entrySet().iterator();
		Entry<Item, Page> entry;
		Set<String> set = null;
		while(it.hasNext()){
			entry = it.next();
			try {
				set = xmlextractor.XMLextract(entry.getValue());
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.remove(entry.getKey());
			extractedMap.put(entry.getKey(), entry.getValue());
			
			Iterator<String> strit = set.iterator();
			while(strit.hasNext()){
				String uri = strit.next();
				System.out.println("the uri in page is " +uri+" in item "+entry.getKey().toString());
				//create items and enqueue
				Item i = URIOperations.getItem(entry.getKey(), uri);
				lq.add(i);
			}
			
			Iterator<Item> itit= lq.iterator();
			System.out.println("this is the queue");
			while(itit.hasNext()){
				System.out.println(itit.next().toString());
			}
		}
	}
}
