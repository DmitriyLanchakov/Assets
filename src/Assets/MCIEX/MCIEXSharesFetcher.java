package Assets.MCIEX;

public class MCIEXSharesFetcher extends MCIEXFetcher {
	public MCIEXSharesFetcher() {
		super(_assets, _type);
	}
	@Override
	protected String getURLPattern()  {
		return "http://www.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/";
	}
	private final static String _assets = "MCIEX";
	private final static String _type = "Shares";
	
	@Override
	public String GetSource() {
		return _assets;
	}
	@Override
	public String GetType() {
		return _type;
	}
}
