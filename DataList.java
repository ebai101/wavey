package ebai.javamusic.wavey;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("serial")
public class DataList<T> extends ArrayList<T> {
	
	public void interpolate(Function<Integer, Double> get, BiConsumer<Integer, Double> set, double smoothingFactor) {
		interpolateNew(get, set, smoothingFactor);
	}

	public void interpolate(Function<Integer, Double> get, BiConsumer<Integer, Double> set) {
		// default factor is 0.2, actual factor 0.36
		interpolateNew(get, set, 0.2);
	}
	
	private void interpolateNew(Function<Integer, Double> get, BiConsumer<Integer, Double> set, double smoothingFactor)
	{
		// this method creates a separate list of the data without
		// repeats and interpolates new values from there
		
		double y0, y1, y2, y3, mu;
		
		// pad list
		this.ensureCapacity(this.size() + 3);
		
		// first pass: create truncated list to interpolate from
		ArrayList<DoublePair> truncList = new ArrayList<DoublePair>();
		int repeatCount = 0;
		for (int i = 0; i < this.size() - 3; i++)
		{
			if (get.apply(i).equals(get.apply(i+1)))
			{
				// in a repeating section - increment the counter
				repeatCount++;
			}
			else if (!get.apply(i).equals(get.apply(i+1)) && repeatCount > 0)
			{
				// repeating section is over - add midpoint of section to truncList
				int midIndex = i - (int)(repeatCount / 2);
				truncList.add(new DoublePair(midIndex, get.apply(midIndex)));
				repeatCount = 0;
			}
			else
			{
				// non-repeating point, add normally
				truncList.add(new DoublePair(i, get.apply(i)));
			}
		}
		
		//System.out.println("truncList: "+truncList);
		
		// second pass: interpolate new list
		truncList.ensureCapacity(truncList.size());
		for (int i = 0; i < truncList.size() - 1; i++)
		{
			int empty = truncList.get(i+1).key - truncList.get(i).key - 1;
			if (empty > 0)
			{
				// gap to fill
				int index = truncList.get(i).key + 1;
				y0 = i == 0 ? truncList.get(i).value : get.apply(truncList.get(i).key - 1);
				y1 = truncList.get(i).value;
				y2 = truncList.get(i+1).value;
				y3 = get.apply(truncList.get(i+1).key + 1);
				for (int j = 0; j < empty; j++)
				{
					mu = (double)(j+1) / (double)(empty+1);
					set.accept(index++, cubic(y0, y1, y2, y3, mu));
				}
			}
		}
		
		// third pass: weighted moving average, smooths the data by a given factor
		double factor = (-1 * Math.pow(smoothingFactor, 2)) + (2 * smoothingFactor); // weight lower factors more with transform: y = -x^2 + 2x
		double sum = 0, meanDm = 0, mean;
		for (int i = 0; i < this.size(); i++)
		{
			sum += get.apply(i) * (i + 1);
			meanDm += i + 1;
			mean = sum / meanDm;
			set.accept(i, linear(get.apply(i), mean, factor));
		}
	}

	@SuppressWarnings("unused")
	private void interpolateOriginal(Function<Integer, Double> get, BiConsumer<Integer, Double> set) {
		// original interpolation method. doesn't work because
		// it doesn't use Double.equals() where it should - the Function
		// objects return a Double object, not a primitive double
		
		double y0, y1, y2, y3, mu;

		// pad list
		this.ensureCapacity(this.size() + 3);

		// first pass, standard cubic
		for (int i = 0; i < this.size() - 3; i++) {
			y0 = get.apply(i);
			y1 = get.apply(i + 1);
			y2 = get.apply(i + 2);
			y3 = get.apply(i + 3);

			set.accept(i, cubic(y0, y1, y2, y3, 0.5));
		}

		// second pass, cosine over long repeat sections
		double lastVal = 0.0;
		int startIndex = 0, endIndex = 0, midpoint = 0, distance = 0;
		for (int i = 0; i < this.size() - 3; i++) {
			if (get.apply(i) == lastVal) {
				if (distance == 0)
					startIndex = i;
				distance++;
			} else if (distance != 0) {
				endIndex = i;
				midpoint = (int) ((endIndex + startIndex) / 2);

				// first half - between startIndex value and midpoint value
				y0 = get.apply(startIndex);
				y1 = get.apply(midpoint);
				for (int j = 0; j < midpoint - startIndex; j++) {
					mu = j / (midpoint - startIndex);
					set.accept(j, cosine(y0, y1, mu));
				}

				// second half - between midpoint value and endIndex value
				y0 = get.apply(midpoint);
				y1 = get.apply(endIndex);
				for (int j = 0; j < endIndex - midpoint; j++) {
					mu = j / (endIndex - midpoint);
					set.accept(j, cosine(y0, y1, mu));
				}

				// reset
				distance = 0;
			}

			lastVal = get.apply(i);
		}
	}

	public void normalize(Function<Integer, Double> get, BiConsumer<Integer, Double> set) {
		normalize(get, set, 0.0, 1.0);
	}

	public void normalizePositive(Function<Integer, Double> get, BiConsumer<Integer, Double> set) {
		// Normalize to 0.0, MAX

		double inMin = Double.MAX_VALUE;
		double inMax = Double.MIN_VALUE;

		for (int i = 0; i < this.size(); i++) {

			inMax = Math.max(get.apply(i), inMax);
			inMin = Math.min(get.apply(i), inMin);
		}

		for (int j = 0; j < this.size(); j++) {
			double temp = (get.apply(j) - inMin) * (inMax) / (inMax - inMin);
			set.accept(j, temp);
		}
	}

	public void normalize(Function<Integer, Double> get, BiConsumer<Integer, Double> set, double outMin,
			double outMax) {
		// Very inefficient array normalization, with custom min and max

		if (outMax <= outMin)
			throw new IllegalArgumentException();

		double inMin = Double.MAX_VALUE;
		double inMax = Double.MIN_VALUE;

		for (int i = 0; i < this.size(); i++) {

			inMax = Math.max(get.apply(i), inMax);
			inMin = Math.min(get.apply(i), inMin);
		}

		for (int j = 0; j < this.size(); j++) {
			double temp = (get.apply(j) - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
			set.accept(j, temp);
		}
	}

	private double cubic(double y0, double y1, double y2, double y3, double mu) {
		// cubic interpolation
		
		double a0, a1, a2, a3, mu2;

		mu2 = mu * mu;
		a0 = y3 - y2 - y0 + y1;
		a1 = y0 - y1 - a0;
		a2 = y2 - y0;
		a3 = y1;

		return (a0 * mu * mu2 + a1 * mu2 + a2 * mu + a3);
	}

	private double cosine(double y0, double y1, double mu) {
		// cosine interpolation
		
		double mu2;

		mu2 = (1 - Math.cos(mu * Math.PI)) / 2;
		return (y0 * (1 - mu2) + y1 * mu2);
	}
	
	private double linear(double y1,double y2, double mu)
	{
		// linear interpolation
		
	   return(y1*(1-mu)+y2*mu);
	}
}

class DoublePair {
	// simple key/value pair object for interpolation purposes
	
	public int key;
	public double value;

	public DoublePair(int key, double value) {
		this.key = key;
		this.value = value;
	}

	public String toString() {
		return String.format("[%d, %.1f]", key, value);
	}
}
