package kuncwlr;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Queue;

public class Download {
	private Socket so;
	private StringBuilder sb = new StringBuilder();
	private PrintWriter out;
	private BufferedReader in;
	private Queue<Item> lq;
	private Item currentItem;
	
//	public static void main(String [] args){
//		Item item = new Item(new Link("221.130.120.178"), "/info/cm/ah/serviceInfo.html", 8080);
//		Download down = new Download(item);
//		Page page = down.downOps();
//		System.out.println(page.toString());
//	}
	
	
	public Download(Queue<Item> lq){
		this.lq = lq;
	}
	
	public Page downOps(){
		String str = "";
		try {
			getItemfromQueue();
			conn();
			str = download();
			disconn();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		so = new Socket(currentItem.getLink().toString(), currentItem.getPortNo());
		out = new PrintWriter(new BufferedOutputStream(so.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(so.getInputStream()));
		
		return so;
	}
	
	//download page
	//get method and return String
	public String download(){
		out.println("GET "+ currentItem.getFile() + " HTTP/1.0\r\n");
		out.println("Accept:text/plaint, text/html, text/*, */*\r\n:w");
		out.println("\r\n");
		out.flush();
		
		String str;
		try {
			while((str = in.readLine())!=null){
				sb.append(str+"\n");
//				System.out.println(str);
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
	

}