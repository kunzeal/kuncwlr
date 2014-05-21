package kuncwlr;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Mana {
	
//	public static void main(String [] args){
//		Item item = Item.getNewInstance(new Host("221.130.120.178"), 8080, "/info/cm/ah/serviceInfo.html", 0);
////		Item item = Item.getNewInstance(new Host("www.baidu.com"), "/", 0);
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
//		Item item = Item.getNewInstance(new Host("www.baidu.com"), 80, "/", 0);
		HTMLExtractor xmlextractor = new htmlparserExtractor();
		Mana manager = new Mana(item, xmlextractor);
		
		ExecutorService es = Executors.newFixedThreadPool(3);
		
		Future<String> df = es.submit(manager.getDownload());
		Future<String> ef = es.submit(manager.getExtractor());
		
//		while(!df.isDone()||!ef.isDone()){
//		}
//		try{
//			Thread.sleep(10000);
//		} catch(InterruptedException e){
//			
//		}
		
		try {
			df.get();
			ef.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		es.shutdown();
		
		System.out.println("~~~~~~~~~~~~~~~result~~~~~~~~~~~~~~~");
		System.out.println("lq size:"+manager.getLinkQueue().size());
		System.out.println("map size:"+manager.getMap().size());
		//print lq
		Iterator<Item> itit = manager.getLinkQueue().iterator();
		System.out.println("lq contents:");
		while(itit.hasNext()){
			Item it = itit.next();
			System.out.println(it.toString()+":"+it.getPortNo()+" deepth:"+it.getDeepth());
		}
		//print map
		System.out.println("map contents:");
		Iterator<Entry<Item, Page>> itmap = manager.getMap().entrySet().iterator();
		while(itmap.hasNext()){
			Entry<Item, Page> en = itmap.next();
			System.out.println("Item:"+en.getKey().toString());
		}
		//print map
		System.out.println("map contents:");
		itmap = manager.getExMap().entrySet().iterator();
		while(itmap.hasNext()){
			Entry<Item, Page> en = itmap.next();
			System.out.println("Item:"+en.getKey().toString());
		}
	}
	
	private Queue<Item> lq = new LinkedList<Item>();
	private Map<Item, Page> map = new ConcurrentHashMap<Item, Page>();
	private Map<Item, Page> extractedMap = new ConcurrentHashMap<Item, Page>();
	private Download down;
	private Item currentItem;
	private Extractor extractor;
	private EndingStatus es;
	private Logger logger;
	
	public Mana(Item item, HTMLExtractor xmlextractor){
		this.es = new EndingStatus();
		this.down = new Download(lq, map, es);
		lq.add(item);
		this.extractor = new Extractor(map, extractedMap, lq, xmlextractor, es);
		this.logger = Logger.getLogger("kunlog");
		try {
			FileHandler fh = new FileHandler("/var/mylog/kunlog.log");
			fh.setFormatter(new MyLogFormatter());
			logger.addHandler(fh);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void startDownload(){
		currentItem = lq.peek();
		Page p = down.downOps();
		map.put(this.currentItem, p);
	}
	
	public Map<Item, Page> getMap(){
		return map;
	}
	
	public Map<Item, Page> getExMap(){
		return extractedMap;
	}
	
	public void startExtract(){
		while(!map.isEmpty()){
			this.extractor.extractingOps();
		}
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
