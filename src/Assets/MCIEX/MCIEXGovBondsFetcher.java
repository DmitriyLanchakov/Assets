package Assets.MCIEX;

public class MCIEXGovBondsFetcher extends MCIEXFetcher {

	public MCIEXGovBondsFetcher() {
		super(_assets, _type);
	}
	@Override
	protected String getURLPattern() {
		return "http://www.moex.com/iss/history/engines/stock/markets/bonds/boards/TQOB/securities/";
	}

	private final static String _assets = "MCIEX";
	private final static String _type = "GovBonds";
	@Override
	public String GetSource() {
		return _assets;
	}
	@Override
	public String GetType() {
		return _type;
	}
}
