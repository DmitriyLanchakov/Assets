package Assets.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;

public class DBConnector {
	/**
	 * Converts Date to DataBase String format
	 * @param date Date object
	 * @return String represents the Date
	 */
	public static final String Date2DB(java.util.Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	/**
	 * Configures the connection to DB
	 * @param user User name
	 * @param pass Password
	 * @param host Host
	 * @param port Port
	 */
	public static final void ConfigConnector(String user, String pass, String host, Integer port) {
		_USER = user;
		_PASSWORD = pass;
		_HOST = host;
		_PORT = port;
		that = null;
	}
	/**
	 * Gets the instance of DB connection, before doing anything with DB 
	 * you need to call this method 
	 * @return Instance of DB connection
	 */
	public static synchronized final DBConnector Get() {
		if (that == null)
			that = new DBConnector();
		return that;
	}
	private static DBConnector that = null;
	private Connection con = null;
	private String _DATABASE = "portfolio1";
	private static String _USER = "dmitry";
	private static String _PASSWORD= "123";
	private static String _HOST = "localhost";
	private static Integer _PORT = 3306;
	
	protected DBConnector() {
		String url = "jdbc:mysql://"+ _HOST +":" + _PORT + "/" + _DATABASE;
		connect2DB(url, _USER, _PASSWORD);
	};
	/**
	 * Creates a table contains volume of the asset (Date, Price for the Date, count) 
	 * @param name name of the asset
	 */
	public void createAssetTable (String name) {
		String query = "CREATE TABLE IF NOT EXISTS " + name +
				" (SECID VARCHAR(16) NOT NULL," +
				" DATE DATE," +
				" PRICE DOUBLE," +
				" CURRENCY DOUBLE DEFAULT 1.0," +
				" AMOUNT INT UNSIGNED DEFAULT 1," +
				" INDEX (SECID,DATE)," +
				" UNIQUE (SECID,DATE))";
		execute(query);
	} 
	/**
	 * Creates asset's definition table (SECID, Name, Source(Type))
	 * @param name name of the asset
	 */
	public void createAssetsTable (String name) {		
		String query = "CREATE TABLE IF NOT EXISTS " + name +
				" (SECID VARCHAR(16) NOT NULL," +
				" NAME VARCHAR(189) NOT NULL," +
				" TYPE VARCHAR(16) NOT NULL," +
				" UNIQUE (SECID, TYPE)," +
				" INDEX (SECID))";
		execute(query);
	}
	/**
	 * Remove the table
	 * @param name
	 */
	public void dropTable(String name) {
		String query = "DROP TABLE IF EXISTS " + name;
		execute(query);
	}
	/**
	 * Inserts asset's volume  
	 * @param table table name
	 * @param name name of the asset
	 * @param d date
	 * @param price price for the date
	 * @param amount amount for the date
	 */
	public void insert2Table(String table, String name, java.util.Date d, Double price, int amount) {
		String query = "INSERT IGNORE INTO " + table +
				" (SECID, DATE, PRICE, AMOUNT)" +
				" VALUES(\"" + name + "\",\"" +
				Date2DB(d) +"\"," + 
				price + "," +  amount + " )";
		execute(query);
	}
	@Deprecated
	public void insert2Table(String table, String name, java.util.Date d, Double price) {
		insert2Table(table, name, d, price, 1);
	}
	/**
	 * Reads volumes for the assets
	 * @param secid SECID of the asset (i.e. GAZP)
	 * @param type type of the asset
	 * @param cb callback to be called on each record in DB
	 */
	public void readAssetValues(String secid, String type, ReadAssetValueCb cb) {
		String query = "SELECT DATE, PRICE, CURRENCY, AMOUNT FROM " + type +
				" WHERE SECID=\"" + secid + "\"";
		try (Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next())
				cb.setValue(rs.getDate(1), rs.getDouble(2), rs.getDouble(3), rs.getInt(4));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Returns the date of the latest record in DB according to the asset defined by name
	 * @param table table name
	 * @param name name of the asset
	 * @return  The date of the latest record or January.01.2000
	 */
	public java.util.Date GetLastDateForAsset(String table, String name) { 
		@SuppressWarnings("deprecation")
		java.util.Date date = new java.util.Date(100, Calendar.JANUARY, 1);
		String query = "SELECT MAX(DATE) FROM " + table +
				" WHERE SECID=\"" + name +"\"";
		try (Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			if (rs.first()) {
				java.util.Date tmpDate = rs.getDate(1);
				if( tmpDate != null)
					date = tmpDate;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return date;
	}
	/**
	 * Deletes asset info from the DB
	 * @param Source asset's source (i.e. MCIEX)
	 * @param Name asset's SECID (i.e. GAZP)
	 */
	public void deleteAsset(String Source, String Name) {
		String query = "SELECT TYPE FROM " + Source + " WHERE SECID=\"" + Name + "\"";
		System.out.println("Delete> " + query);
		try (Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			String Type = null;
			if (rs.first()) {
				Type =  rs.getString(1);
				if (Type == null)
					throw new SQLException("Wrong return null");	
			} else
				throw new SQLException("Wrong return " + Type);
			Formatter fmt = new Formatter();
			fmt.format("DELETE %s,%s  FROM %s,%s WHERE %s.SECID=\"%s\" AND %s.SECID=\"%s\"",
					Source, Type, Source, Type, Source, Name, Type, Name);
			query = fmt.toString();
			fmt.close();
			System.out.println("DELETE> " + query);
			stmt.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Reads available assets from asset's definition table according by Source and Type  
	 * @param Source Source of assets (i.e. MCIEX)
	 * @param Type type of assets (i.e. Shares)
	 * @param Cb CallBack to be called on each record in Db
	 */
	public void readAssets(String Source, String Type, InsertAssetCb Cb) {
		Formatter fmt = new Formatter();
		fmt.format("SELECT %s.SECID, %s.NAME, %s.AMOUNT FROM %s  JOIN %s ON TYPE=\"%s\" AND %s.SECID=%s.SECID AND DATE IN (SELECT MAX(DATE) FROM %s)", 
					Source, Source, Type, Source, Type, Type, Type, Source, Type);
		String query = fmt.toString(); fmt.close();
		System.out.println("readAssets> " + query);
		try (Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next())
				Cb.setAsset(rs.getString(2), rs.getString(1), rs.getInt(3)); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	/**
	 * Adds an asset to DB
	 * @param Name name of the asset (i.e. GazProm)
	 * @param SECID SECID of the asset (i.e. GAZP)
	 * @param AssetSource Source of the asset (i.e. MCIEX)
	 * @param AssetType Type of the asset (i.e. Shares)
	 * @throws Exception
	 */
	public void addAsset(String Name, String SECID, String AssetSource, String AssetType) throws Exception {
		String query = "INSERT IGNORE INTO " + AssetSource +
				" (SECID, NAME, TYPE)" +
				" VALUES(\"" + SECID + "\",\"" +
				Name + "\",\"" + AssetType + "\")";
		if (execute(query)!= 0)
			throw new Exception("Insert Error");
	}
	private void connect2DB(String host, String user, String pass) {
		try {
			con = DriverManager.getConnection(host, user, pass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void close() {
		if(con!=null)  {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		con = null;
	}
	private int execute (String query) {
		//System.out.println("Execute query>" + query);
		try (Statement stmt = con.createStatement()) {
			stmt.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Got exeption " + e + " On SQL request: " + query);
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
}
	