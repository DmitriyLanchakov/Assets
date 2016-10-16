package Assets.MCIEX;

public class MCIEXETFFetcher extends MCIEXFetcher {
	/**
	 * Fetches data for assets in ETF section on MCIEX
	 */
	
	public MCIEXETFFetcher() {
		super(_assets, _type);
	}
	@Override
	protected String getURLPattern() {
		return "http://www.moex.com/iss/history/engines/stock/markets/shares/boards/TQTF/securities/";
	}

	private final static String _assets = "MCIEX";
	private final static String _type = "ETF";
	/**
	 * @return "MCIEX"
	 */
	@Override
	public String GetSource() {
		return _assets;
	}
	/**
	 * @return "ETF"
	 */
	@Override
	public String GetType() {
		return _type;
	}
}
