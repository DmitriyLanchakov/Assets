package Assets.DB;

import java.util.Date;

public interface ReadAssetValueCb {
	void setValue(Date date, double value, double currency, int count);
}
