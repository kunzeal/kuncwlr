package kuncwlr;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.FactoryConfigurationError;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParamConfig;

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
	private CloseableHttpClient hc;
	private RequestConfig reqConfig;
	private final int ConnTimeout = 1000;
	private final int SocTimeout = 1000;
	
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
		reqConfig = RequestConfig.custom()
				.setSocketTimeout(SocTimeout)
				.setConnectTimeout(ConnTimeout)
				.build();
		hc = HttpClients.createDefault();
	}
	
	public Page downOps(){
		String str = "";
		if(lq.isEmpty())return null;
		getItemfromQueue();
		try {
			str = download();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			downLog.info("downOps clientP... exception");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			downLog.info("downOps IOException");
			return null;
		}
		return new Page(str);
	}
	
	//get item from queue and remove it
	public Item getItemfromQueue(){
		this.currentItem =  lq.remove();
		return currentItem;
	}
	
	
	//download page
	//get method and return String
	public String download() throws ClientProtocolException, IOException{
		URI uri;
		try {
			uri = new URIBuilder().setScheme("http")
				.setPath(currentItem.getFile())
				.setHost(currentItem.getHost().toString())
				.setPort(currentItem.getPortNo())
				.build();
		} catch (URISyntaxException e) {
			//error in Item
			return null;
		}
		HttpGet hg = new HttpGet(uri);
		hg.setConfig(reqConfig);
		CloseableHttpResponse response = hc.execute(hg);
		StringBuilder sb = new StringBuilder();
		String str;
		try {
			HttpEntity entity = response.getEntity();
			BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
			while((str=in.readLine())!=null){
				sb.append(str);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			response.close();
		}
		downLog.info("sb"+sb.toString());
		return sb.toString();
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
			downLog.info("Page"+p.toString());
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