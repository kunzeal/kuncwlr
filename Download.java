package kuncwlr;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.FactoryConfigurationError;

public class Download implements Callable<String>{
	private Socket so;
	private StringBuilder sb = new StringBuilder();
	private PrintWriter out;
	private BufferedReader in;
	private Queue<Item> lq;
	private Item currentItem;
	private EndingStatus es;
	private Map<Item, Page> map;
	private Logger downLog;
	
	public static void main(String [] args){
//		Item item = Item.getNewInstance(new Host("221.130.120.178"), 8088, "/self/self_logon.jsp", 0);
//		Download down = new Download(item);
//		Page page = down.downOps();
//		System.out.println(page.toString());
	}
	
	
	public Download(Queue<Item> lq, Map<Item, Page> map, EndingStatus es){
		this.lq = lq;
		this.map = map;
		this.es = es;
		downLog = Logger.getLogger("downLog");
		try {
//			FileHandler fh = new FileHandler("E:/searchEngine/log/downlog.log");
			FileHandler fh = new FileHandler("/var/mylog/downlog.log");
			fh.setLevel(Level.INFO);
			fh.setFormatter(new MyLogFormatter());
			downLog.addHandler(fh);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Page downOps(){
		String str = "";
		if(lq.isEmpty())return null;
		try {
			getItemfromQueue();
			conn();
			str = download();
			disconn();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new Page(str);
	}
	
	public Item getItemfromQueue(){
		this.currentItem =  lq.remove();
		return currentItem;
	}
	
	//socket connect
	public Socket conn() throws UnknownHostException, IOException{
		//create Socket
		so = new Socket();
		SocketAddress saddr = new InetSocketAddress(currentItem.getHost().toString(), currentItem.getPortNo());
		so.connect(saddr, 1000);
		out = new PrintWriter(new BufferedOutputStream(so.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(so.getInputStream()));
		
		return so;
	}
	
	//download page
	//get method and return String
	public String download(){
		out.println("GET "+ currentItem.getFile() + " HTTP/1.0\r\n");
		out.println("Accept:text/plaint, text/html\r\n:w");
		out.println("\r\n");
		out.flush();
		
		String str;
		try {
			while((str = in.readLine())!=null){
				sb.append(str);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return sb.toString();
	}
	
	public boolean disconn(){
		try {
			so.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public String call() throws InterruptedException{
		
//		while(!lq.isEmpty()){
//			downOps();
//		}
		int i = 0;
		while(true){
//			if(i == 30){
//				downLog.info("i down finished");
//				es.downloadPause();
//				return "finished";
//			}
			i++;
			downLog.info("down "+i);
			
			//end judge
			if(lq.isEmpty()&&map.isEmpty()&&es.isExtractPaused()){
				synchronized (lq) {
					synchronized (map) {
						if(lq.isEmpty()&&map.isEmpty()&&es.isExtractPaused()){
							es.downloadPause();
							map.notifyAll();
							downLog.info("map notifyall");
							downLog.info("down finished");
							return "finished!";
						}
					}
				}
			}
			
			if(lq.isEmpty()){
				synchronized (lq) {
					if(lq.isEmpty()){
						es.downloadPause();
						downLog.info("lq wait d:"+(es.isDownloadPaused()?1:0)+" e:"+(es.isExtractPaused()?1:0)+" lq size:"+lq.size()+" map size:"+map.size());
						if(es.isExtractPaused()&&lq.isEmpty()&&map.isEmpty()){
							synchronized (map) {
								if(map.isEmpty()&&es.isExtractPaused()){
									es.downloadPause();
									map.notifyAll();
									downLog.info("map notifyall");
								}
							}
							downLog.info("down finished");
							return "finished!";
						}
						downLog.info("download lq wait");
						lq.wait();
						//检查extractor是否停止，若停止则跳出
						if(lq.isEmpty()){
							downLog.info("down finished");
							return "finished!";
						}
						es.downloadResume();
						downLog.info("lq notify d:"+(es.isDownloadPaused()?1:0)+"e:"+(es.isExtractPaused()?1:0));

					}
				}
			}
			
			//termination condition
			currentItem = lq.peek();
			//之抓取到深度为1的连接
			if(currentItem.getDeepth()>=3){
				downLog.info("deepth >= 2");
				lq.poll();
				continue;
			}
			
			downLog.info("downloading:"+currentItem.toString()+" port:"+currentItem.getPortNo()+" deepth "+currentItem.getDeepth());
			Page p = downOps();
			downLog.info("downloaded");
			if(p == null) continue;
			map.put(this.currentItem, p);
			synchronized(map){
				if(!map.isEmpty()){
					map.notify();
					downLog.info("map notify");
				}
			}
		}
		
		
	}

}