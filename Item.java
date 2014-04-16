package kuncwlr;

//as the download goal
//including host, file..
public class Item {
	private Host host;
	private String file;
	private int portNo = 80;
	private int type = Constants.UriTypesCons.URI_TYPE_ABSOLUTE_WS;
	
	public Item(Host host, String file){
		this.setHost(host);
		this.setFile(file);
	}
	
	public Item(Host host, String file, int portNo){
		this.setHost(host);
		this.setFile(file);
		this.setPortNo(portNo);
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
