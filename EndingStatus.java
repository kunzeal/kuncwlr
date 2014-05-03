package kuncwlr;

public class EndingStatus {
	private boolean downloadPaused = false;
	private boolean extractPaused = false;
	
	public boolean isDownloadPaused(){
		return downloadPaused;
	}
	
	public boolean isExtractPaused(){
		return downloadPaused;
	}
	
	public void downloadPause(){
		synchronized (this) {
			this.downloadPaused = true;
		}
	}
	
	public void downloadResume(){
		synchronized (this) {
			this.downloadPaused = false;
		}
	}
	
	public void extractPause(){
		synchronized (this) {
			this.extractPaused = true;
		}
	}
	
	public void extractResume(){
		synchronized (this) {
			this.extractPaused = false;
		}
	}
	
}
