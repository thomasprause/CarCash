/*
 * Created on Nov 24, 2006
 *
 */
package thp;


import java.awt.Graphics;
import java.util.Collections;
import java.util.Enumeration;
import java.awt.Color;

/**
 * @author prause
 *
 */
@SuppressWarnings("serial")
public class StackedBarChart extends TimeBarChart {

	int mMaxCat = 0;
		
	/**
	 * 
	 */
	public StackedBarChart() {
		super();
	}

	/**
	 * @param res
	 */
	public StackedBarChart(int res) {
		super(res);
	}
	StackedChartValue getValue(long tm,int cat)
	{
		StackedChartValue tcv;

		for(Enumeration<TimeChartValue> e = values.elements(); e.hasMoreElements(); )
		{
			 tcv = (StackedChartValue) e.nextElement();
			 if ((tcv.getTimeStamp() == tm) && (tcv.getCategory() == cat))
			 	return tcv;
		}
		
		return null;
	}
	public void addValue(float val, long tm, int cat)
	{
		System.out.println("add "+val+" date=" +tm);
		TimeChartValue curTc = getValue(tm, cat);
		float tmp = val;
		if (cat>mMaxCat) mMaxCat=cat;
		if(curTc == null)
		{
			addValue(new StackedChartValue(val, tm, cat));
		}
		else
		{
			tmp += curTc.getValue();
			curTc.setValue(tmp);

			if (tmp > max_value) {
				int incr = 50;
				if (tmp > 500) {
					max_value = 1000;
					incr = 200;
				}
				if (tmp > 2000) {
					max_value = 5000;
					incr = 1000;
				}
				if (tmp > 5000) {
					max_value = 10000;
					incr = 5000;
				}
				while (tmp > max_value) {
					max_value += incr;
					System.out.println("val=" + tmp + " incr=" + incr
							+ " maxVal=" + max_value);
				}
			}
		}
		Collections.sort(values);
	}
	
	public void paint(Graphics g)
	{
		_paint(g);
		long tmp_min_time = min_time;

		if(resolution == RES_YEAR)
		{
			tmp_min_time -= 31104000000L;
			barWidth = (int)(unit_x*31104000000L)/2 - 2;
		}
		else if(resolution == RES_MONTH)
		{
			tmp_min_time -= 2592000000L;
			barWidth = (int)(unit_x*2592000000F)/2 - 2;
		}
		if(barWidth < 2) barWidth = 2;

		// draw the values
		Color colArr[] = {Color.red, Color.blue, Color.green, Color.yellow}; 
		StackedChartValue cv;

		int y_start = height-SPACE_Y;
		long lastTimeStamp = 0;
		for(java.util.Enumeration<TimeChartValue> en = values.elements();en.hasMoreElements();)
		{
			cv = (StackedChartValue) en.nextElement();
			if(cv.getTimeStamp() != lastTimeStamp)
			{
				y_start = height-SPACE_Y;
			}
			System.out.println("val="+cv.getValue() +" cat" +cv.getCategory());
			g.setColor(colArr[cv.getCategory()]);
			g.fillRect(SPACE_X + (int) ((float)(cv.getTimeStamp() - tmp_min_time) * unit_x)-barWidth/2, 
					(int)(y_start - cv.getValue() * unit_y),
					barWidth, 
					y_start-(int)(y_start - cv.getValue()*unit_y));
			y_start -= cv.getValue()*unit_y;
			lastTimeStamp = cv.getTimeStamp();
		}

	}
}
