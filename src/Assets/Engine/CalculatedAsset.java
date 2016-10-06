package Assets.Engine;

import java.util.Date;
import java.util.TreeMap;

import Assets.Asset;

public class CalculatedAsset {

	public CalculatedAsset(double average, double sigma, Asset assets[], Double percentages[], 
			Double averages[], Double sigmas[], TreeMap<Date, Double> values) {
		// TODO Auto-generated constructor stub
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
	
	public Asset[] getAssets() {return assets_;}
	public double[] getAverages() {return averages_;}
	public double[] getSigmas() {return sigmas_;}
	public double getResultAverageReturn() {return resAverage_;}
	public double getResultSigma() {return resSigma_;}
	public double getValueForNumber(final int num) {
		int n = num;
		if (n >= values_.length)
			n = values_.length-1;
		return values_[n];
	}
	public Date getDateForNumber(final int num) {
		int n = num;
		if (n >= dates_.length)
			n = dates_.length-1;
		return dates_[n];
	}
	public double getMinValue() {return min_;}
	public double getMaxValue() {return max_;}
	public int getCount() {return values_.length;}
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
