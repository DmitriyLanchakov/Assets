package Assets;

import java.util.ArrayList;
import java.util.HashMap;

import Assets.MCIEX.MCIEXCorpBondsFetcher;
import Assets.MCIEX.MCIEXETFFetcher;
import Assets.MCIEX.MCIEXGovBondsFetcher;
import Assets.MCIEX.MCIEXSharesFetcher;

public final class FetcherFabric {
	public static Fetcher get(String source, String type) {
		if (fetchers_ == null)
			init();
		HashMap<String, String> hm = new HashMap<>(1,1);
		hm.put(source.toUpperCase(), type.toUpperCase());
		return fetchers_.get(hm);
	}
	private static void set(Fetcher f) {
		HashMap<String,String> hm = new HashMap<>(1,1);
		hm.put(f.GetSource().toUpperCase(),
				f.GetType().toUpperCase());
		fetchers_.put(hm, f);
	}
	private static synchronized void init() {
		if (fetchers_ != null)
			return;
		fetchers_ = new HashMap<>();
		set(new MCIEXSharesFetcher());
		set(new MCIEXETFFetcher());
		set(new MCIEXCorpBondsFetcher());
		set(new MCIEXGovBondsFetcher());
	}
	public static ArrayList<Fetcher> Fetchers() {
		if (fetchers_ == null)
			init();
		return ((fetchers_ != null) ? new ArrayList<Fetcher>(fetchers_.values()) : null);
	}
	private static HashMap< HashMap<String,String>, Fetcher > fetchers_ = null;
}