package kuncwlr;

import java.util.HashSet;
import java.util.Set;

//as the download goal
//including host, file..
public class Item {
	private Host host;
	private String file;
	private int portNo = 80;
	private int deepth = 0;
//	private int type = Constants.UriTypesCons.URI_TYPE_ABSOLUTE_WS;
	private static Set<String> itemSet = new HashSet<String>();
	
	public static Item getNewInstance(Host host, String file){
		if(!check(host, file, 80)){
			return new Item(host, file);
		}
		return null;
	}
	public static Item getNewInstance(Host host, String file, int deepth){
		if(!check(host, file, 80)){
			return new Item(host, file, deepth);
		}
		return null;
	}
	
	public static Item getNewInstance(Host host, int portNo, String file, int deepth){
		if(!check(host, file, portNo)){
			return new Item(host, file, portNo, deepth);
		}
		return null;
	}
	
	public static synchronized boolean check(Host host,String file,int port){
		String itemStr = host.toString()+":"+port+file;
		if(itemSet.contains(itemStr)){
			return true;
		}
		itemSet.add(itemStr);
		return false;
	}
	
	private Item(Host host, String file){
		this.setHost(host);
		this.setFile(file);
	}
	
	private Item(Host host, String file, int deepth){
		this.setHost(host);
		this.setFile(file);
		this.deepth = deepth;
	}
	
	private Item(Host host, String file, int portNo, int deepth){
		this.setHost(host);
		this.setFile(file);
		this.setPortNo(portNo);
		this.deepth = deepth;
	}
	
	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	public String toString(){
		return host.toString()+file;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}
	
	public int getDeepth(){
		return deepth;
	}
	
}

class Host {
	//host should't with http or '/' and should be clean
	public Host(String host){
		this.host = host;
	}
	private String host;
	
	public void setHost(String host){
		this.host = host;
	}
	
	public String toString(){
		return this.host;
	}
}
