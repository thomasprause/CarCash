/*
 * Created on Oct 15, 2006
 *
 */
package thp;


import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author prause
 *
 */
@SuppressWarnings("serial")
public class TimeBarChart extends TimeChart {
	protected int barWidth = 18;
	/**
	 * 
	 */
	public TimeBarChart() {
		super();
	}
	public TimeBarChart(int res)
	{
		super();
		resolution = res;
		switch(res)
		{
			case RES_DAY:
				break;
			case RES_WEEK:
				timeFormat = new SimpleDateFormat("ww/yy", Locale.GERMANY);
				break;
			case RES_MONTH:
				timeFormat = new SimpleDateFormat("MM/yy", Locale.GERMANY);
				break;
			case RES_YEAR:
				timeFormat = new SimpleDateFormat("yyyy", Locale.GERMANY);
				break;
		}
	}
	public void addValue(float val, long tm)
	{
		System.out.println("add "+val+" date=" +tm);
		TimeChartValue curTc = getValue(tm);
		float tmp = val;
		if(curTc == null)
		{
			addValue(new TimeChartValue(val, tm));
		}
		else
		{
			tmp += curTc.getValue();
			curTc.setValue(tmp);

			if (tmp > max_value) {
				int incr = 50;
				if (tmp > 500) {
					max_value = 500;
					incr = 100;
				}
				if (tmp > 5000) {
					max_value = 5000;
					incr = 1000;
				}
				while (tmp > max_value) {
					max_value += incr;
					System.out.println("val=" + tmp + " incr=" + incr
							+ " maxVal=" + max_value);
				}
			}
		}
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
		else barWidth = 4;
		if(barWidth < 2) barWidth = 2;
		//System.out.println("barWidth=" +barWidth);
		
		// draw the values
		g.setColor(chartColor);
		TimeChartValue cv = (TimeChartValue) values.elementAt(0);//, cvLast;
		for(java.util.Enumeration<TimeChartValue> en = values.elements();en.hasMoreElements();)
		{
			//cvLast = cv;
			cv = (TimeChartValue) en.nextElement();
			//g.drawRect(SPACE_X + (int) ((cv.getTimeStamp() - min_time) * unit_x) - 1, 
			//		(int) (height-SPACE_Y- cv.getValue()*unit_y)-1, 20, 5);
			g.fillRect(SPACE_X + (int) ((float)(cv.getTimeStamp() - tmp_min_time) * unit_x)-barWidth/2, 
					(int) (height-SPACE_Y- cv.getValue()*unit_y), barWidth, 
					height-SPACE_Y-(int)(height-SPACE_Y- cv.getValue()*unit_y));
		}
	}
}
