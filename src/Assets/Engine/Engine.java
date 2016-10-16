package Assets.Engine;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
//import java.lang.

import Assets.Asset;

class MathHelper {
	/**
	 * Calculates the average sum
	 * @param values to be calculated
	 * @return average sum
	 */
	static final Double CalcAverageSum(Collection<? extends Double> values) {
		double avgSum = 0.0;
		for (Double cur : values)
			avgSum += cur;
		return avgSum / values.size();
	}
	/**
	 * Calculates the geometric average
	 * @param values to be calculated
	 * @return geometric average
	 */
	static final Double CalcAverageMult(Collection<? extends Double> values) {
		double avgMult = 1.0;
		for (Double cur : values)
			avgMult *= cur;
		return Math.pow(avgMult, 1.0 / values.size());
	}
	/**
	 * Calculates the sigma (Standard deviation)
	 * @param values values to be calculated for the sigma 
	 * @param average average
	 * @return the sigma
	 */
	static final Double CalcSigma(Collection<? extends Double> values, Double average) {
		double sigma = 0.0, sum = 0.0;
		for (Double cur : values) {
			sum += Math.pow(cur-average, 2);
		}
		sigma = sum / (values.size()-1);
		return Math.sqrt(sigma);
	}
	/**
	 * Calculates the sigma (Standard deviation)
	 * @param values values to be calculated for the sigma
	 * @return the sigma
	 */
	static final Double CalcSigma(Collection<? extends Double> values) {
		return CalcSigma(values, CalcAverageSum(values));
	}
}	

class CalcAssets {
	/**
	 * Class to calculate the sigma and average return for 2 assets
	 */
	/**
	 * 
	 * @param asset1 values of asset #1
	 * @param percentage1 percentage of the asset #1
	 * @param asset2 values of ssset #2
	 * @param percentage2 percentage of the asset #2
	 */
	public CalcAssets (TreeMap<Date, Double> asset1, double percentage1,
			TreeMap<Date, Double> asset2, double percentage2) {
		
		if (percentage1 > 1.0)
			percentage1 = percentage1 / 100;
		if (percentage2 > 1.0)
			percentage2 = percentage2 / 100;
		percent_ = percentage1 + percentage2;
		for (Date d : asset1.keySet()) {
			Double v1 = asset1.get(d);
			Double v2 = asset2.get(d);
			if (v1 == null || v2 == null)
				continue;
			result_.put(d, 1 + ((v1-1) * percentage1) + ((v2-1) * percentage2));
		}
		calculate();
	}
	private void calculate() {
		avgSum_ = MathHelper.CalcAverageSum(result_.values());
		avgMult_ = MathHelper.CalcAverageMult(result_.values());
		Sigma_ = MathHelper.CalcSigma(result_.values(), avgSum_);
	}
	/**
	 * Returns sum of percentages of 2 assets
	 * @return sum of percentages
	 */
	public double getPercent() {return percent_;}
	/**
	 * Returns average sum for 2 assets
	 * @return average sum
	 */
	public double getAvgSum() {return avgSum_;}
	/**
	 * Returns geometric average for 2 asset
	 * @return geometric average
	 */
	public double getAvgMult() {return avgMult_;}
	/**
	 * Returns the sigma of 2 assets
	 * @return the sigma
	 */
	public double getSigma() {return Sigma_;}
	/**
	 * Returns the map contains the result of 2 assets
	 * @return the map of result
	 */
	public TreeMap<Date, Double> get() {return result_;}
	private TreeMap<Date, Double> result_ = new TreeMap<>();
	private double percent_;
	private Double avgSum_ = null, avgMult_ = null, Sigma_ = null;
}

class AssetParam implements Comparable<AssetParam> {
	/**
	 * Class to calculate the difference of the values of the asset between the closest dates 
	 * @param a the asset
	 * @param start start date
	 * @param minP minimum Percentage 
	 * @param maxP maximum Percentage
	 */
	public AssetParam(Asset a, Date start, double  minP, double maxP) {
		asset_ = a;
		if (start == null)
			startDate_ = asset_.values().firstKey();
		else
			startDate_ = start;
		setMinPercent(minP);
		setMaxPercent(maxP);
		calcDelta();
		calcAverages();
	}
	/**
	 * Returns the asset which the object calculated for
	 * @return the asset
	 */
	public Asset get() {
		return asset_;
	}
	/**
	 * Returns the values of delta (delta means the difference between the closest values)
	 * @return the delta
	 */
	public TreeMap<Date, Double> getDelta() {return delta_;}
	private void calcDelta() {
		SortedMap<Date, Double> val = asset_.values();
		val = val.tailMap(startDate_);
		Iterator<Date> KeyIter = val.keySet().iterator();
		Iterator<Double> ValIter = val.values().iterator();		
	
		Date prevD = (Date)KeyIter.next();
		Double prevV = (Double)ValIter.next();
		for( ;KeyIter.hasNext() && ValIter.hasNext();) {
			Date curD = (Date)KeyIter.next();
			Double curV = (Double)ValIter.next();
			delta_.put(curD, curV / prevV );
			prevD = curD;
			prevV = curV;
		}
	}
	private void calcAverages() {
		avgSum_ = MathHelper.CalcAverageSum(delta_.values());
		avgMult_ = MathHelper.CalcAverageMult(delta_.values());
	}
	private double calcSigma() {
		if (avgSum_ == null || avgMult_ == null)
			calcAverages();
		return MathHelper.CalcSigma(delta_.values(), avgSum_);
	}
	private Asset asset_;
	private TreeMap<Date, Double> delta_ = new TreeMap<>();
	private Date startDate_;
	private double minPercent_, maxPercent_;
	private Double avgSum_ = null, avgMult_ = null;
	/**
	 * Returns average sum of the asset
	 * @return average sum
	 */
	public double getAvgSum() {return avgSum_;}
	/**
	 * Returns the geometric average of the asset
	 * @return geometric average
	 */
	public double getAvgMult() {return avgMult_;}
	/**
	 * Returns the sigma (risk) of the asset
	 * @return
	 */
	public double getSigma() {return calcSigma();}
	/**
	 * Sets the minimum percentage
	 * @param p minimum percentage
	 */
	public void setMinPercent(double p) {minPercent_ = p;}
	/**
	 * Returns the minimum percentage
	 * @return the minimum percentage
	 */
	public double getMinPercent() {return minPercent_;}
	/**
	 * Sets the maximum percentage
	 * @param p maximum percentage
	 */
	public void setMaxPercent(double p) {maxPercent_ = p;}
	/**
	 * Returns the maximum percentage
	 * @return the maximum percentage
	 */
	public double getMaxPercent() {return maxPercent_;}
	/**
	 * Compares to the other asset
	 * @return -1,0,1 if this asset is less,the same, more than the another asset
	 */
	@Override
	public int compareTo(AssetParam o) {
		return asset_.compareTo(o.get());
	}
}

class ReturnHelper implements Iterable<Integer> {
	/**
	 * Class to make storage of assets in the portfolio
	 * @param ap Asset transformed to AssetParam
	 * @param p percentage of the asset 
	 */
	ReturnHelper(AssetParam ap, Double p) {
		aps_.add(ap);
		percents_.add(p);
	}
	/**
	 * Adds the asset (transformed to AssetParam) and the calculation of sigma/average return
	 * of the previous asset and the current.  
	 * @param as Calculated parameters (sigma/average return) for the previous and current assets 
	 * @param ap Current asset (transformed to AssetParam)
	 * @param p percentage of the asset in result portfolio
	 */
	public void put(CalcAssets as, AssetParam ap, Double p) {
		as_.add(as);
		aps_.add(ap);
		percents_.add(p);
	}
	/**
	 * Returns the latest calculated assets
	 * @return the latest calculated assets
	 */
	public CalcAssets getLastCalcAssets() {
		return as_.get(as_.size()-1);
	}
	/**
	 * Returns the asset (transformed to AssetParam) for index
	 * @param index the index of the asset
	 * @return the asset
	 */
	public AssetParam getParamFor(int index) {return aps_.get(index);}
	/**
	 * Returns the percentage of the asset at index
	 * @param index the index in the assets array 
	 * @return the percentage
	 */
	public double getPercentageFor(int index) {return percents_.get(index);}
	private ArrayList<CalcAssets> as_ = new ArrayList<>();
	private ArrayList<AssetParam> aps_ = new ArrayList<>();
	private ArrayList<Double> percents_ = new ArrayList<>();
	/**
	 * Returns an iterator
	 * @return the iterator
	 */
	@Override
	public Iterator<Integer> iterator() {
		return new InnerIterator();
	}
	private class InnerIterator implements Iterator<Integer> {
		/**
		 * Class to support an iteration operation on outer class
		 * @see Iterator
		 */
		@Override
		public boolean hasNext() {
			if (ReturnHelper.this.aps_.size() > cursor &&
					ReturnHelper.this.percents_.size() > cursor)
				return true;
			return false;
		}
		@Override
		public Integer next() {
			if(this.hasNext()) {
				int cur = cursor;
				cursor++;
				return cur;
			}
			throw new NoSuchElementException();
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		private int cursor = 0;
	}
}

public class Engine {
	/**
	 * Class to calculate the best percentages for each assets in portfolio
	 * The best percentages are calculated by recursive calculation of the previous 
	 * calculated assets and the current asset
	 * 
	 * @param assets array of assets existed in portfolio
	 * @param mins array of minimum percentages available for the asset in assets array
	 * @param maxs array of maximum percentages available for the asset in assets array
	 * @throws NumberFormatException
	 */
	public Engine(Asset[] assets, int[] mins, int[] maxs) throws NumberFormatException  {
		if(assets == null || assets.length < 2)
			throw new NumberFormatException("No data");
		minDate_ = calculateMinCommonDate(assets);
		this.assets = new ArrayList<>();
		for (int n = 0; n < assets.length; n++) {
			Asset a = assets[n];
			double minP = (double)mins[n] / 100.0; 
			double maxP = (double)maxs[n] / 100.0;
			this.assets.add(new AssetParam(a, minDate_, minP, maxP));
		}
		ReturnHelper res = null;
			try {
				res = calcBestPercent(this.assets, 1.0, 0.05);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		makeReturnedAsset(res);
	}
	/**
	 * Returns the count of assets in the portfolio
	 * @return count of assets
	 */
	public int getAssetsCount() {return assets.size();}
	/**
	 * Finds the minimum date exists in all assets
	 * @param assets array of assets in the portfolio
	 * @return the minimum date 
	 */
	private Date calculateMinCommonDate(Asset[] assets) {
		@SuppressWarnings("deprecation")
		Date minD = new Date(100, Calendar.JANUARY, 1);
		for (Asset a : assets ) {
			TreeMap<Date, Double> val = a.values();
			Date d = val.firstKey();
			if (d.after(minD))
				minD = d;
		}
		return minD;
	}
	private void makeReturnedAsset(ReturnHelper res) {
		ArrayList<Asset> as = new ArrayList<>();
		ArrayList<Double> ps = new ArrayList<>();
		ArrayList<Double> averages = new ArrayList<>();
		ArrayList<Double> sigmas = new ArrayList<>();
		for (int n : res) {
			AssetParam ap = res.getParamFor(n);
			double percent = res.getPercentageFor(n);
			as.add(ap.get());
			ps.add(percent);
			averages.add(ap.getAvgSum());
			sigmas.add(ap.getSigma());
			/*System.out.println("For " + ap.get().toString() + " Amount in portfolio=" + percent);
			System.out.println("Average return for this asset=" + ap.getAvgSum() + 
					" Sigma=" + ap.getSigma() );*/
		}
		/*System.out.println("Average return=" + res.getLastCalcAssets().getAvgSum() + 
				" Sigma=" + res.getLastCalcAssets().getSigma());*/
		retValues = new CalculatedAsset(res.getLastCalcAssets().getAvgSum(),res.getLastCalcAssets().getSigma(), 
				as.toArray(new Asset[0]), ps.toArray(new Double[0]),
				averages.toArray(new Double[0]),sigmas.toArray(new Double[0]), 
				res.getLastCalcAssets().get());
	}
	/**
	 * Recursive calculation of the best percentage for each asset in portfolio
	 * O(n) = n**n
	 * @param apl tail of the list of assets
	 * @param availP max percentage available for the tail
	 * @param step step of percentage calculation
	 * @return ReturnHelper contains the list of asset with the best percentage  
	 * @throws Exception
	 */
	protected ReturnHelper calcBestPercent(List<AssetParam> apl, double availP, double step) throws Exception {
		double maxR = Double.NEGATIVE_INFINITY;//, minS = Double.POSITIVE_INFINITY;
		List<AssetParam> tail = apl.subList(1, apl.size());
		ReturnHelper ret = null;
		AssetParam ap = apl.get(0);
		if (availP < ap.getMinPercent())
			return null;
		/*System.out.println("calcBestPercent ap: " + ap.get().name() + " MinPercent=" +  ap.getMinPercent() + 
					" MaxPercent=" + ap.getMaxPercent() + " step=" + step);*/
		double max = Math.min(availP, ap.getMaxPercent());
		if (tail.size() == 1) {
			AssetParam ap1 = apl.get(1);
			for (double p = ap.getMinPercent(); p < max; p += step) {
				ReturnHelper cur = new ReturnHelper(ap1,availP-p); 
				cur.put(new CalcAssets(ap.getDelta(), p, ap1.getDelta(), availP - p),
						ap, p); 
				//double r = cur.getLastCalcAssets().getAvgSum();
				//double r = 1 - cur.getLastCalcAssets().getSigma();
				double r = cur.getLastCalcAssets().getAvgSum() - cur.getLastCalcAssets().getSigma();
				if (r > maxR) {
					ret = cur;
					maxR = r;
				}
			}
		} else { 
			for (double p = ap.getMinPercent(); p < max; p += step ) {
				ReturnHelper tmp = calcBestPercent(tail, availP - p, step);
				if (tmp == null)
					continue;
				CalcAssets cur = new CalcAssets(ap.getDelta(), p, tmp.getLastCalcAssets().get(), availP - p);
				//double r = cur.getAvgSum();
				//double r = 1 - cur.getSigma();
				double r = cur.getAvgSum() - cur.getSigma(); 
				if (r > maxR) {
					tmp.put(cur, ap, p);
					ret = tmp;
					maxR = r;
				}
			}
		}
		return ret;
	}
	/**
	 * Returns the result
	 * @return the result
	 */
	public CalculatedAsset getResult() {return retValues;}
	private ArrayList<AssetParam> assets = new ArrayList<>();
	private CalculatedAsset retValues = null;
	private Date minDate_;
}
