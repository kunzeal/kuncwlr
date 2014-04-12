package kuncwlr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

public class Mana {
	
	public static void main(String [] args){
		Item item = new Item(new Link("221.130.120.178"), "/info/cm/ah/serviceInfo.html", 8080);
		Mana manager = new Mana(item);
		manager.startDownload();
		Map<Item, Page> map = manager.getMap();
		Iterator<Entry<Item, Page>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<Item, Page> entry = it.next();
			Page page = entry.getValue();
			System.out.println(page.toString());
		}
	}
	
	private Queue<Item> lq = new LinkedList<Item>();
	private Map<Item, Page> map = new HashMap<Item, Page>();
	private Download down;
	private Item currentItem;
	
	public Mana(Item item){
		this.down = new Download(lq);
		lq.add(item);
	}
	
	
	public void startDownload(){
		currentItem = lq.peek();
		Page p = down.downOps();
		map.put(this.currentItem, p);
	}
	
	public Map<Item, Page> getMap(){
		return map;
	}
	
}
