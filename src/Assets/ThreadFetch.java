package Assets;

public final class ThreadFetch implements Runnable {
	public ThreadFetch(Fetcher fetcher, Asset asset) {
		this.fetcher = fetcher;
		this.asset = asset;
	}
	@Override
	public void run() {
		boolean done = false;
		while(!done)
			try {
				done = this.fetcher.update(asset);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
	}
private Fetcher fetcher;
private Asset asset;	
}