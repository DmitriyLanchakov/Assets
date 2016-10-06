package Assets.MCIEX;

public class MCIEXETFFetcher extends MCIEXFetcher {
	public MCIEXETFFetcher() {
		super(_assets, _type);
	}
	@Override
	protected String getURLPattern() {
		return "http://www.moex.com/iss/history/engines/stock/markets/shares/boards/TQTF/securities/";
	}

	private final static String _assets = "MCIEX";
	private final static String _type = "ETF";
	@Override
	public String GetSource() {
		return _assets;
	}
	@Override
	public String GetType() {
		return _type;
	}
}
