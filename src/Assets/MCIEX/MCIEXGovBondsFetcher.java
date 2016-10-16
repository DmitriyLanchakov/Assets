package Assets.MCIEX;

public class MCIEXGovBondsFetcher extends MCIEXFetcher {
	/**
	 * Fetches data for assets in Government Bonds section on MCIEX
	 */
	
	public MCIEXGovBondsFetcher() {
		super(_assets, _type);
	}
	@Override
	protected String getURLPattern() {
		return "http://www.moex.com/iss/history/engines/stock/markets/bonds/boards/TQOB/securities/";
	}

	private final static String _assets = "MCIEX";
	private final static String _type = "GovBonds";
	/**
	 * @return "MCIEX"
	 */
	@Override
	public String GetSource() {
		return _assets;
	}
	/**
	 * @return "GovBonds"
	 */
	@Override
	public String GetType() {
		return _type;
	}
}
