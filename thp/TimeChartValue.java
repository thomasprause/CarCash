/*
 * Created on Oct 12, 2006
 *
 */
package thp;


/**
 * @author prause
 *
	 * Represents a value at a specific point in time.
	 */
	public class TimeChartValue implements Comparable<TimeChartValue>
	{
		private long timeStamp = -1;
		private float value = -1F;
		private float mAvg = -1F;

	/**
	 *
	 * @param v float
	 * @param t int
	 */
	public TimeChartValue(float v, long t)
	{
		super();
		
		value = v;
		mAvg = 0;
		timeStamp = t;
	}

	public TimeChartValue(float v, float avg, long t)
	{
		super();
		
		value = v;
		mAvg = avg;
		timeStamp = t;
	}
	/**
	 *
	 * @return long
	 */
	public long getTimeStamp() 
	{
		return timeStamp;
	}
	/**
	 *
	 * @return float
	 */
	public float getValue() 
	{
		return value;
	}
	public float getAvg() 
	{
		return mAvg;
	}
	/**
	 *
	 * @param newValue long
	 *
	private void setTimeStamp(long newValue) 
	{
		this.timeStamp = newValue;
	}*/
	/**
	 *
	 * @param newValue float
	 */
	void setValue(float newValue) 
	{
		this.value = newValue;
	}
	
	public int compareTo(TimeChartValue tcv)
	{
		if(timeStamp == tcv.timeStamp) return 0;
		if(timeStamp > tcv.timeStamp)
			return 1;
		else
			return -1;
	}
}
