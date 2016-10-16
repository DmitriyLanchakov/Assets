package Assets;

import java.util.ArrayList;

public class Updater extends Thread {
	/**
	 * Update fetchers data from Internet
	 */
	
	public Updater() {
		super.start();
	}
		
	public void run() {
		set();
		for(Fetcher f : fetchers) 
			f.update();
	}
	private void set() {
		Fetcher shares_fetcher = FetcherFabric.get("MCIEX", "Shares");
		Fetcher etf_fetcher = FetcherFabric.get("MCIEX", "ETF");
		fetchers = new ArrayList<>();
		fetchers.add(shares_fetcher);
		fetchers.add(etf_fetcher);
		for (Fetcher f : fetchers)
			try {
				f.sync();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	ArrayList<Fetcher> fetchers;
}