package kuncwlr1_0;

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

public class Download {
	private Socket so;
	private int portNo = 80;
	private StringBuilder sb = new StringBuilder();
	private PrintWriter out;
	private BufferedReader in;
	private Item item;
	
	public static void main(String [] args){
		Item item = new Item(new Link("www.baidu.com"), "/");
		Download down = new Download(item);
		Page page = down.downOps();
		System.out.println(page.toString());
	}
	
	
	public Download(Item item){
		this.item = item;
	}
	
	public Page downOps(){
		String str = "";
		try {
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
	
	
	//socket connect
	public Socket conn() throws UnknownHostException, IOException{
		//create Socket
		so = new Socket(item.getLink().toString(), portNo);
		out = new PrintWriter(new BufferedOutputStream(so.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(so.getInputStream()));
		
		return so;
	}
	
	//download page
	//get method and return String
	public String download(){
		out.println("GET "+ item.getFile() + " HTTP/1.0\r\n");
		out.println("Accept:text/plaint, text/html, text/*, */*\r\n:w");
		out.println("\r\n");
		out.flush();
		
		String str;
		try {
			while((str = in.readLine())!=null){
				sb.append(str);
				System.out.println(str);
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