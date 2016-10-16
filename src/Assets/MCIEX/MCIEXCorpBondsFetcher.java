package Assets.MCIEX;

public class MCIEXCorpBondsFetcher extends MCIEXFetcher {
	/**
	* Fetches data for assets in Corporative Bonds section on MCIEX
	*/
	
	public MCIEXCorpBondsFetcher() {
		super(_assets, _type);
	}
	@Override
	protected String getURLPattern() {
		return "http://www.moex.com/iss/history/engines/stock/markets/bonds/boards/EQOB/securities/";
	}

	private final static String _assets = "MCIEX";
	private final static String _type = "CorpBonds";
	/**
	 * @return "MCIEX"
	 */
	@Override
	public String GetSource() {
		return _assets;
	}
	/**
	 * @return "CorpBonds" 
	 */
	@Override
	public String GetType() {
		return _type;
	}
}
