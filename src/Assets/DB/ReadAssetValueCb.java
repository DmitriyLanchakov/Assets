package Assets.DB;

import java.util.Date;

public interface ReadAssetValueCb {
	/**
	 * CallBack to be called when a record is road from DB
	 * @param date Date
	 * @param value Value for the date
	 * @param currency Currency value for the date (to convert to Rub)
	 * @param count Asset count for the date
	 */
	void setValue(Date date, double value, double currency, int count);
}
