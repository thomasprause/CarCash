/*
 * Created on Nov 24, 2006
 *
 */
package thp;


/**
 * @author prause
 *
 */
public class StackedChartValue extends TimeChartValue {

	int mCategory;
	/**
	 * @param v
	 * @param t
	 */
	public StackedChartValue(float v, long t) {
		super(v, t);
		mCategory = 1;
	}

	/**
	 * @param v
	 * @param avg
	 * @param t
	 */
	public StackedChartValue(float v, long t, int cat) {
		super(v, t);
		mCategory = cat;
	}
	
	int getCategory() { return mCategory; }

}
