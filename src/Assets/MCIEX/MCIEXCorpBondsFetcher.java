package Assets.MCIEX;

public class MCIEXCorpBondsFetcher extends MCIEXFetcher {

	public MCIEXCorpBondsFetcher() {
		super(_assets, _type);
	}
	@Override
	protected String getURLPattern() {
		return "http://www.moex.com/iss/history/engines/stock/markets/bonds/boards/EQOB/securities/";
	}

	private final static String _assets = "MCIEX";
	private final static String _type = "CorpBonds";
	@Override
	public String GetSource() {
		return _assets;
	}
	@Override
	public String GetType() {
		return _type;
	}
}
