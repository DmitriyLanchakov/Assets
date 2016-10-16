package Assets;

import java.util.Date;
import java.util.TreeMap;

public final class Asset implements Comparable<Asset> {
	/* Class to store and operate on an asset */
	
	/** 
	 * Create an asset
	 * @param name name represents the asset for user
	 * @param secid ID of the asset on market
	 */
	public Asset(String name, String secid) {
		Name = name;
		SECID = secid;
	}
	/**
	 * @return name of the asset
	 */
	public final String name() {return Name;}
	/**
	 * @return SECID of the asset
	 */
	public final String secid() {return SECID;}
	/**
	 * @return name of the market where the asset changes
	 */
	public final String source() {return Source_;}
	/**
	 * @return type of the asset (share, ETF, bond, etc)
	 */
	public final String type() {return Type_;}
	/**
	 * @return string represents the asset for DataBase
	 */
	public final String DBType() {return Source_+Type_;}
	/**
	 * @return array of {name, secid, source, type, count}
	 */
	public final Object[] getAll() {
		Object[] all = {Name, SECID, Source_, Type_, count_};
		return all;
	}
	/**
	 * @return values of the asset, values mean the TreeMap<Date, Double> represents the
	 * price of the asset for the Date  
	 */
	public final TreeMap<Date, Double> values() {return getSetValues(null);}
	/**
	 * @return count of the asset that you have
	 */
	public int count() {return count_;}
	/**
	 * Sets source of the market where the asset being changed
	 * @param s source of the market
	 */
	public void setSource(String s) {Source_=s;}
	/**
	 * Sets the type of the asset (share, ETF, bond, etc)
	 * @param t type of the asset
	 */
	public void setType_(String t) {Type_=t;}
	/**
	 * Sets count of the asset
	 * @param c count
	 */
	public void setCount(int c) {count_=c; syncWithDB_=false;}
	/**
	 * Sets values for the asset
	 * @param val prices of the asset for given Dates
	 * @see values()
	 */
	public void setValues(TreeMap<Date, Double> val) {getSetValues(val); syncWithDB_=false;}
	/**
	 * Adds values to the asset's values.
	 * @param val prices for the asset to be added
	 * @see values()
	 */
	public void addValues(TreeMap<Date, Double> val) {
		if(values_ == null)
			values_ = new TreeMap<>();
		values_.putAll(val); 
		syncWithDB_=false;
	}
	private synchronized TreeMap<Date, Double> getSetValues(TreeMap<Date, Double> values) {
		if (values == null) //get condition
			return values_;
		else  //set condition
			values_ = values;
		return values_;
	}
	/**
	 * @return true if asset is deleted by user and may be deleted from DataBase
	 */
	public final boolean isDeleted() {return deleted_;}
	/**
	 * @return true if asset's values are synchronized with DB, return false
	 * if asset has changes that haven't been stored in DB yet
	 */
	public final boolean isSynced() {return syncWithDB_;}
	/**
	 * Marks the asset as deleted. Real deletion is done during synchronization process
	 */
	public void delete() {deleted_ = true;}
	/**
	 * Sets the asset as synchronized (all changes are dumped to DB)
	 */
	public void setSynced() {syncWithDB_ = true;}
	private String Name;
	private String SECID;
	private String Source_ = null;
	private String Type_ = null;
	private int count_ = 1;
	private boolean deleted_ = false;
	private boolean syncWithDB_ = false;
	private TreeMap<Date, Double> values_ = null;
	@Override
	/**
	 * Compares with another asset
	 */
	public int compareTo(Asset o) {
		int c = this.source().compareToIgnoreCase(o.source());
		if (c != 0)
			return c;
		c = this.type().compareToIgnoreCase(o.type());
		if (c!=0)
			return c;
		return this.secid().compareToIgnoreCase(o.secid());
	}
	@Override
	/**
	 * Returns string object represents the asset in readable format
	 */
	public String toString() {
		return DBType()+secid();
	}
}