package Assets;

import java.util.Date;
import java.util.TreeMap;

public final class Asset implements Comparable<Asset> {
	public Asset(String name, String secid) {
		Name = name;
		SECID = secid;
	}
	public final String name() {return Name;}
	public final String secid() {return SECID;}
	public final String source() {return Source_;}
	public final String type() {return Type_;}
	public final String DBType() {return Source_+Type_;}
	public final Object[] getAll() {
		Object[] all = {Name, SECID, Source_, Type_, count_};
		return all;
	}
	public final TreeMap<Date, Double> values() {return getSetValues(null);}
	public int count() {return count_;}
	public void setSource(String s) {Source_=s;}
	public void setType_(String t) {Type_=t;}
	public void setCount(int c) {count_=c; syncWithDB_=false;}
	public void setValues(TreeMap<Date, Double> val) {getSetValues(val); syncWithDB_=false;}
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
	public final boolean isDeleted() {return deleted_;}
	public final boolean isSynced() {return syncWithDB_;}
	public void delete() {deleted_ = true;}
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
	public String toString() {
		return DBType()+secid();
	}
}