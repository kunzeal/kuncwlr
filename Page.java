package kuncwlr;

public class Page {
	private String page;
	private boolean extracted = false;
	
	public Page(String page){
		this.setPage(page);
	}
	
	public String toString() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
	
	public void extracted(){
		this.extracted = true;
	}
	
	public boolean isExtracted(){
		return extracted;
	}

}
