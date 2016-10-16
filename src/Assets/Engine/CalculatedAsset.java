package Assets.Engine;

import java.util.Date;
import java.util.TreeMap;

import Assets.Asset;

public class CalculatedAsset {
	/**
	 * Class to describe the result portfolio
	 * 
	 * @param average average return per day of the portfolio  
	 * @param sigma Sigma (Standard deviation, risk metric) of the portfolio
	 * @param assets array of assets are in the portfolio
	 * @param percentages array of percentage value for each asset in the portfoliov
	 * @param averages array of average return volume for each asset in the portfolio
	 * @param sigmas array of sigma (risk metric) for each asset in the portfolio
	 * @param values map of values of the portfolio for dates (
	 *   value for the date means changes(in times) after the previous date)
	 */
	public CalculatedAsset(double average, double sigma, Asset assets[], Double percentages[], 
			Double averages[], Double sigmas[], TreeMap<Date, Double> values) {
		resAverage_ = Math.rint(average * ScaleFactor) / ScaleFactor;
		resSigma_ = Math.rint(sigma * ScaleFactor) / ScaleFactor;
		int len = assets.length; 
		if (len != percentages.length || len != averages.length || len != sigmas.length)
			throw new IndexOutOfBoundsException("assets and percentages arrays must have the same size");
		assets_ = new Asset[len];
		percentages_ = new double[len];
		averages_ = new double[len];
		sigmas_ = new double[len];
		for (int i=0; i<len; i++) {
			assets_[i] = assets[i];
			percentages_[i] = Math.rint(percentages[i] * ScaleFactor) / ScaleFactor;
			averages_[i] = Math.rint(averages[i] * ScaleFactor) / ScaleFactor;
			sigmas_[i] = Math.rint(sigmas[i] * ScaleFactor) / ScaleFactor ;
		}
		len = values.size();
		dates_ = new Date[len];
		values_ = new double[len];
		int i = 0;
		for (Date d : values.keySet()) {
			if (i!= 0) {
				values_[i] = values_[i-1] * values.get(d); 
			} else {
				values_[0] = values.get(d);
			}
			if (values_[i] < min_)
				min_ = values_[i];
			if (values_[i] > max_)
				max_= values_[i];
			dates_[i] = d;
			i++;	
		}
		//System.out.println("Values[0]=" + values_[0] + " Values[" + (i-1) + "]=" + values_[i-1]);
	}
	
	/**
	 * Returns array of the assets contained in the portfolio 
	 * @return array of the assets in portfolio
	 */
	public Asset[] getAssets() {return assets_;}
	/**
	 * Returns array of average return for the assets in portfolio
	 * @return array of average returns
	 */
	public double[] getAverages() {return averages_;}
	/**
	 * Returns array of sigmas for the assets in portfolio
	 * @return arrays of sigmas
	 */
	public double[] getSigmas() {return sigmas_;}
	/**
	 * Returns average return of the portfolio
	 * @return average return
	 */
	public double getResultAverageReturn() {return resAverage_;}
	/**
	 * Returns sigma of the portfolio
	 * @return
	 */
	public double getResultSigma() {return resSigma_;}
	/**
	 * Returns value for number num in values tree 
	 * @param num the number
	 * @return the value for the number num
	 */
	public double getValueForNumber(final int num) {
		int n = num;
		if (n >= values_.length)
			n = values_.length-1;
		return values_[n];
	}
	/**
	 * Returns date for number num in values tree
	 * @param num the number
	 * @return the date for the number num
	 */
	public Date getDateForNumber(final int num) {
		int n = num;
		if (n >= dates_.length)
			n = dates_.length-1;
		return dates_[n];
	}
	/**
	 * Returns the minimum value of the portfolio
	 * @return the minimum value
	 */
	public double getMinValue() {return min_;}
	/**
	 * Returns the maximum value of the portfolio
	 * @return the maximum value
	 */
	public double getMaxValue() {return max_;}
	/**
	 * Returns the count of the values of the portfolio
	 * @return the count of the values
	 */
	public int getCount() {return values_.length;}
	/**
	 * Returns array of the percentages of each asset in the portfolio 
	 * @return array of the percentages 
	 */
	public double[] getPercents() {return  percentages_;}
	private Date dates_[];
	private double values_[];
	private double averages_[];
	private double sigmas_[];
	private Asset assets_[];
	private double percentages_[];
	private double resAverage_;
	private double resSigma_;
	private double min_ = Double.MAX_VALUE;
	private double max_ = Double.MIN_VALUE;
	private static final int ScaleFactor = 10000; 
}
