package kuncwlr;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

public class Extractor implements Callable<String>{
	//<Item,Page> without extraction
	private Map<Item, Page> map;
	//<Item, Page> has been extracted
	private Map<Item, Page> extractedMap;
	private Queue<Item> lq;
	private HTMLExtractor xmlextractor;
	private EndingStatus es;
	private Logger exLog;
	
	public Extractor(Map<Item, Page> map, Map<Item, Page> extractedMap, Queue<Item> lq, HTMLExtractor xmlextractor, EndingStatus es){
		this.es = es;
		this.map = map;
		this.extractedMap = extractedMap;
		this.lq = lq;
		this.xmlextractor = xmlextractor;
		exLog = Logger.getLogger("exLog");
		exLog.setLevel(Level.INFO);
		FileHandler fh;
		try {
//			fh = new FileHandler("E:/searchEngine/log/exlog.log");
			fh = new FileHandler("/var/mylog/exlog.log");
			fh.setFormatter(new MyLogFormatter());
			exLog.addHandler(fh);
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void extractingOps(){
		if(map.isEmpty()) return;
		
		Iterator<Entry<Item, Page>> it = map.entrySet().iterator();
		Entry<Item, Page> entry;
		Set<String> set = null;
		//每次只处理一个page
		if(it.hasNext()){
			entry = it.next();
			if(entry.getKey().getDeepth()==2) {
				exLog.info("deepth is 1");
                map.remove(entry.getKey());
                extractedMap.put(entry.getKey(), entry.getValue());
				return;
			}
			try {
				set = xmlextractor.XMLextract(entry.getValue());
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			map.remove(entry.getKey());
			extractedMap.put(entry.getKey(), entry.getValue());
			
			Iterator<String> strit = set.iterator();
			while(strit.hasNext()){
				String uri = strit.next();
//				System.out.println("the uri in page is " +uri+" in item "+entry.getKey().toString());
				//create items and enqueue
				Item i = URIOperations.getItem(entry.getKey(), uri);
				if(i==null) continue;
				lq.add(i);
			}
			
			exLog.info("lq added!!");
			
//			Iterator<Item> itit = lq.iterator();
//			System.out.println("this is the queue");
//			while(itit.hasNext()){
//				System.out.println(itit.next().toString());
//			}
		}
	}

	@Override
	public String call() throws Exception {
//		while(!map.isEmpty()){
//			extractingOps();
//		}
		int i = 0;
		while(true){
			//最多抽取次数
//			if(i == 120) {
//				exLog.info("map size is "+map.size());
//				exLog.info("i extract finished");
//				return "finished";
//			}
			i++;
			exLog.info("extract "+i);

			if(map.isEmpty()&&lq.isEmpty()&&es.isDownloadPaused()){
				synchronized (map) {
					synchronized (lq) {
						if(map.isEmpty()&&lq.isEmpty()&&es.isDownloadPaused()){
							es.extractPause();
						    lq.notifyAll();
						    exLog.info("lq notifyall");
						    exLog.info("extract finished");
						    return "finished!";
						}
					}
				}
			}
			
			if(map.isEmpty()){
				synchronized (map) {
					if(map.isEmpty()){
						if(i==9) exLog.info("extractor in the map empty");
						//if download pause and lq is empty then end
						//if not end, then pause wait for next entry
						es.extractPause();
						exLog.info("map wait d:"+(es.isDownloadPaused()?1:0)+" e:"+(es.isExtractPaused()?1:0)+" lq size:"+lq.size()+" map size:"+map.size());
						
						exLog.info("extractor map wait");
						map.wait();
						
						//check if download is finished after wait
						//check is notify or notifyAll
						if(map.isEmpty()){
						    exLog.info("extract finished");
						    return "finished!";
						}
						es.extractResume();
						exLog.info("map notify d:"+(es.isDownloadPaused()?1:0)+"e:"+(es.isExtractPaused()?1:0));
					}
				}
			}
			
			extractingOps();
			
			synchronized (lq) {
				if(!lq.isEmpty()){
					lq.notify();
				}
			}
		}
	}
	
}
