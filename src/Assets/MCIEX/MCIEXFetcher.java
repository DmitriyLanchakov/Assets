package Assets.MCIEX;


import java.io.*;
import java.net.*;
import java.util.*;

import Assets.*;
import Assets.DB.DBConnector;
import Assets.DB.InsertAssetCb;
import Assets.DB.ReadAssetValueCb;

class DBReaderHelper extends Thread {
	/**
	 * Read assets from DB in parallel
	 * @param a asset to be road
	 */
	public DBReaderHelper(Asset a) {
		super.start();
		a_ = a;
	}
	public void run() {
		final TreeMap<Date, Double> val = new TreeMap<>();
		DBConnector db = DBConnector.Get();
		db.readAssetValues(a_.secid(), a_.DBType(), new ReadAssetValueCb() {
			public void setValue(Date date, double value,
					double currency, int count) {
				val.put(date, value);
			}
		});
		a_.setValues(val);
		a_.setSynced();
	}
	Asset a_;
}

public abstract class MCIEXFetcher implements Fetcher {
	/**
	 * Parent class to manage (read, delete, update...) assets which Source is MCIEX 
	 * Some methods are specified  in child
	 */
	
	protected MCIEXFetcher(String assets, String asset) {
		DBConnector db = DBConnector.Get();
		db.createAssetsTable(assets);
		db.createAssetTable(assets + asset);
	}
	/**
	 * Fills fetcher by assets
	 * @param assets asset's array to fill this fetcher
	 */
	public MCIEXFetcher(SortedSet<Asset> assets) {
		set__assets(assets);
	}
	/**
	 * Gets assets from this fetcher
	 * @return the assets
	 */
	@Override
	public SortedSet<Asset> GetAssets() {
		return __assets;
	}
	/**
	 * Replaces (or creates) fetcher's assets
	 * @param assets assets that fills this fetcher
	 */
	public void set__assets(SortedSet<Asset> assets) {
		this.__assets = assets;
	}
	/** Add asset into this fetcher
	 * @param asset the assets
	 */
	@Override
	public void add(Asset asset) {
		asset.setSource(GetSource());
		asset.setType_(GetType());
		__assets.add(asset);
	}
	/**
	 * Updates volumes of all assets in this fetcher
	 */
	@Override
	public void update() { 
		for(Asset asset : __assets) {
			Thread th = new Thread(new ThreadFetch(this, asset));
			th.start();
		}
	}
	/**
	 * Updates volumes for the asset
	 * @param asset the asset to be updated
	 * @return true - updated successfully, false - otherwise
	 */
	@Override
	public boolean update(Asset asset) throws Exception {
		boolean res = false;
		String url = getURLPattern() + asset.secid() + ".xml";
		Date begin = GetLastDate(asset), end = Calendar.getInstance().getTime();
		url = appendDate(url, begin, end);
		System.out.println("URL to fetch: " + url);
		URL u;
		try {
			u = new URL(url);
			InputStream is = u.openStream();
			MCIEXXMLParser xmlParser = new MCIEXXMLParser(new InputStreamReader(is));
			TreeMap<Date,Double> map = xmlParser.get();
			if (map==null)
				throw new IOException("Empty data for " + asset);
			if (map.size() > 1)
				res = false;
			else
				res = true;
			if (!asset.isSynced())
				addAsset2DB(asset);
			updateAsset(asset, map);	
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new IOException("Bad URL " + e);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			asset.delete();
			throw e;
		}
		return res;
	}
	protected String appendDate(String pattern, String begin, String end) {
		return pattern+"?from="+begin+"&till"+end;
	}
	protected String appendDate(String pattern, Date begin, Date end) {
		return appendDate(pattern, DBConnector.Date2DB(begin), DBConnector.Date2DB(end));
	}
	/**
	 * Gets the date of the latest record for the asset
	 * @param name name of the asset
	 * @return The date of the latest record, 01.01.2000 if no record
	 */
	public Date GetLastDate(Asset name) {
		return GetLastDate(name.DBType(), name.secid());
	}
	protected final Date GetLastDate(String table, String name) {
		return DBConnector.Get().GetLastDateForAsset(table, name);
	}
	protected final void updateAsset(Asset name, TreeMap<Date, Double> datas) {
		Set<Date> dates = datas.keySet();
		DBConnector db = DBConnector.Get();
		for (Date d : dates)
				db.insert2Table(name.DBType(), name.secid(), d, datas.get(d), name.count());
		name.addValues(datas);
	}
	private void del(Asset asset) {
		if (__assets != null)
		__assets.remove(asset);
	}
	/**
	 * Deletes the asset
	 * @param name the asset to be deleted
	 */
	@Override
	public final void DeleteAsset(Asset name) {
		this.del(name);
		DBConnector db = DBConnector.Get();
		db.deleteAsset(name.DBType(), name.secid());
	}
	protected void addAsset2DB(Asset asset) throws Exception {
		DBConnector db = DBConnector.Get();
		String source = asset.source();
		if (source == null)
			source = this.GetSource();
		String type = asset.type();
		if (source == null)
			type = this.GetSource() + this.GetType();
		else 
			type = asset.DBType();
		db.addAsset(asset.name(), asset.secid(), source, type); 
	}
	protected void removeAssetFromDB(Asset asset) {
		DBConnector db = DBConnector.Get();
		db.deleteAsset(GetSource(), asset.secid());
	}
	/**
	 * Synchronizes asset's tree with DB records
	 */
	@Override
	public final void sync() throws Exception {
		if (__assets.size() == 0) {
			class ReadThread extends Thread {
				ReadThread() {
					super();
					start();
				}
			public void run() {
				readAssetsFromDB();
				}
			};
			new ReadThread();
		}
		else
			for (Asset a : __assets) {
				if (a.isDeleted())
					removeAssetFromDB(a);
				else
					if (!a.isSynced())
						addAsset2DB(a);
			}
	}
	private final void readAssetsFromDB() {
		DBConnector db = DBConnector.Get();
		db.readAssets(GetSource(), GetSource()+GetType(), new InsertAssetCb() {
			public void setAsset(String name, String secid, int count) {
				Asset a = new Asset(name, secid);
				a.setCount(count);
				new DBReaderHelper(a);
				MCIEXFetcher.this.add(a);
			}
		});
	}
	/**
	 * Returns the Source of the fetcher's instance
	 * This method should be implemented by child
	 * @return string contains the source name
	 */
	@Override
	abstract public String GetSource();
	@Override
	/**
	 * Return the type of the fetchers's instance
	 * This method should be implemented by child
	 * @return string contains the type name
	 */
	abstract public String GetType();
	abstract protected String getURLPattern();
	private SortedSet<Asset> __assets = new TreeSet<>();
}
