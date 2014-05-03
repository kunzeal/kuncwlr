package kuncwlr;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Mana {
	
//	public static void main(String [] args){
//		Item item = Item.getNewInstance(new Host("221.130.120.178"), 8080, "/info/cm/ah/serviceInfo.html", 0);
////		Item item = new Item(new Host("www.baidu.com"), "/");
//		HTMLExtractor xmlextractor = new htmlparserExtractor();
//		Mana manager = new Mana(item, xmlextractor);
//		manager.startDownload();
//		Map<Item, Page> map = manager.getMap();
//		Iterator<Entry<Item, Page>> it = map.entrySet().iterator();
//		while(it.hasNext()){
//			Entry<Item, Page> entry = it.next();
//			Page page = entry.getValue();
//			System.out.println(page.toString());
//		}
//		manager.startExtract();
//		Iterator<Item> itit = manager.getLinkQueue().iterator();
//		System.out.println("size:"+manager.getLinkQueue().size());
//		int i = 0;
//		while(itit.hasNext()){
//			i++;
//			item = itit.next();
//			System.out.println(item.toString()+":"+item.getPortNo()+" deepth:"+item.getDeepth());
//		}
//		System.out.println("size:"+i);
//	}
	
	public static void main(String [] args){
		Item item = Item.getNewInstance(new Host("221.130.120.178"), 8080, "/info/cm/ah/serviceInfo.html", 0);
//		Item item = new Item(new Host("www.baidu.com"), "/");
		HTMLExtractor xmlextractor = new htmlparserExtractor();
		Mana manager = new Mana(item, xmlextractor);
		
		ExecutorService es = Executors.newFixedThreadPool(3);
		
		Future<String> df = es.submit(manager.getDownload());
		Future<String> ef = es.submit(manager.getExtractor());
		
		while(!df.isDone()||!ef.isDone()){
		}
		
		es.shutdown();
		
		Iterator<Item> itit = manager.getLinkQueue().iterator();
		System.out.println("size:"+manager.getLinkQueue().size());
		int i = 0;
		while(itit.hasNext()){
			i++;
			Item it = itit.next();
			System.out.println(it.toString()+":"+it.getPortNo()+" deepth:"+it.getDeepth());
		}
		System.out.println("size:"+i);
	}
	
	private Queue<Item> lq = new LinkedList<Item>();
	private Map<Item, Page> map = new ConcurrentHashMap<Item, Page>();
	private Map<Item, Page> extractedMap = new ConcurrentHashMap<Item, Page>();
	private Download down;
	private Item currentItem;
	private Extractor extractor;
	private EndingStatus es;
	
	public Mana(Item item, HTMLExtractor xmlextractor){
		this.es = new EndingStatus();
		this.down = new Download(lq, map, es);
		lq.add(item);
		this.extractor = new Extractor(map, extractedMap, lq, xmlextractor, es);
	}
	
	
	public void startDownload(){
		currentItem = lq.peek();
		Page p = down.downOps();
		map.put(this.currentItem, p);
	}
	
	public Map<Item, Page> getMap(){
		return map;
	}
	
	public void startExtract(){
		this.extractor.extractingOps();
	}
	
	public Download getDownload(){
		return this.down;
	}
	
	public Extractor getExtractor(){
		return extractor;
	}
	
	public Queue<Item> getLinkQueue(){
		return lq;
	}
	
}
