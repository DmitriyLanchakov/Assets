package Assets.MCIEX;

public class MCIEXSharesFetcher extends MCIEXFetcher {
	/**
	 * Fetches data for assets in Stocks section on MCIEX
	 */
	
	public MCIEXSharesFetcher() {
		super(_assets, _type);
	}
	@Override
	protected String getURLPattern()  {
		return "http://www.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/";
	}
	private final static String _assets = "MCIEX";
	private final static String _type = "Shares";
	
	/**
	 * @return "MCIEX"
	 */
	@Override
	public String GetSource() {
		return _assets;
	}
	/**
	 * @return "Shares"
	 */
	@Override
	public String GetType() {
		return _type;
	}
}
