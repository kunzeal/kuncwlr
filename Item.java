package kuncwlr;
import java.net.URL;

//as the download goal
//including link, file..
public class Item {
	private Link link;
	private String file;
	
	public Item(Link link, String file){
		this.setLink(link);
		this.setFile(file);
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	public String toString(){
		return link.toString()+file;
	}

}

class Link {
	public Link(String host){
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
