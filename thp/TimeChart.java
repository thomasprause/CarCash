/*
 * Created on Oct 12, 2006
 *
 */
package thp;

import java.awt.*;
import java.util.*;
import java.text.*;

import javax.swing.*;



/**
 * A panel which shows a line chart/diagram with the appropriate methods to set
 * the unit (including scale factor) and maximum for each axis. Values are added
 * in form of instances of TimeChartValue.
 *
 * @see  TimeChartValue
 */
@SuppressWarnings("serial")
public class TimeChart extends JPanel
{
	public final static int RES_DAY   = 1;
	public final static int RES_WEEK  = 2;
	public final static int RES_MONTH = 3;
	public final static int RES_YEAR  = 4;

	int resolution = 1;
	private int width			= -1;
	int height			= -1;
	long steps_x			= 1;
	private int steps_y			= 1;
	float step_width_x		= 1;
	private float step_width_y		= 1;
	float unit_x			= 1;
	float unit_y			= 1;

	Vector<TimeChartValue> values			= null;
	//private final static String CN 		= "Chart";
	final static int SPACE_Y 	= 20;
	final static int SPACE_X 	= 35;

		// to scale the values
	private float scaleFactor 		= 1;
	private String unitString 		= "";
	private String timeString 		= "time";

		// where the chart ends
		// use a !! FLOAT !! here to avoid rounding errors and numbers like 0.99999
	float max_value 		= 2;
	float mLimit	 		= 0;
	long min_time 			= 11474836480000L;
	private long max_time 			= 10000;

	private DecimalFormat decForm 	        = null;
	DateFormat    timeFormat        = null;
	
		// all the colors
	Color chartColor 		= Color.red;
	private Color gridColor 		= Color.gray;
	private Color textColor 		= Color.black;

public TimeChart()
{
	super();
	setDefaults();
}
public TimeChart(float limit)
{
	super();
	setDefaults();
	mLimit = limit;
	max_value = limit;
}

private void setDefaults()
{
	setDoubleBuffered(true);	
	values = new Vector<TimeChartValue>();
	decForm = new DecimalFormat("#0.00");
	timeFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
}
/**
 * Converts a long representing a time into a timeformat.
 *
 * @param l String
 */
String makeTimeString(long i)
{
	//return timeFormat.format(new Date(i + ((TimeChartValue)values.elementAt(1)).getTimeStamp()));
	//return timeFormat.format(new Date(((TimeChartValue)values.elementAt(i)).getTimeStamp()));
	//System.out.println(i +" is " +timeFormat.format(new Date(i))); 
	return timeFormat.format(new Date(i)); 
}
public void addValue(TimeChartValue cv)
{
	values.addElement(cv);
	//System.out.println("add: " +cv.getTimeStamp());

	if(0 == mLimit)
	{
	    while(cv.getValue() > max_value)
	    {
	 	    	float tmp = cv.getValue();
				int incr = 10;
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

	while (cv.getTimeStamp() > max_time)
	{
		max_time += 1000000;
	}
	while (cv.getTimeStamp() < min_time)
	{
		min_time -= 1000000;
	}
	//System.out.println("max Time = " +max_time);
	//System.out.println("min Time = " +min_time +"\n");
}
/*
private void drawNumbers(Graphics g, int thistime)
{
	TimeChartValue tcv = getValue(thistime);
		
	if(tcv == null)
		return;
			
	String info = decForm.format( tcv.getValue() * scaleFactor) 
					+" " +unitString +" - " +makeTimeString(thistime);

	g.setColor(getBackground());
	g.setPaintMode();
	g.fillRect(0, 0, 180, SPACE_Y/2+2);

	g.setColor(textColor);
	g.drawString(info, 1, SPACE_Y/2+2);
}
*/
public void drawValue(TimeChartValue cv)
{
	boolean repa = false;
	
	if ((cv.getValue() > max_value) || (cv.getTimeStamp() > max_time))
	{
		repa = true;
	}
	addValue(cv);

	if(repa)
	{
		repaint();
	}
		
	Graphics g = getGraphics();
	
	if(g != null)
	{
		g.setColor(chartColor);
		g.drawOval(SPACE_X + (int) (cv.getTimeStamp()*unit_x) - 1, 
			   (int) (height-SPACE_Y- cv.getValue()*unit_y)-1, 2, 2);
	}
}
		
public Color getChartColor()
{
	return chartColor;
}
public Color getGridColor()
{
	return gridColor;
}
public Color getTextColor()
{
	return textColor;
}
TimeChartValue getValue(long tm)
{
	TimeChartValue tcv;

	for(Enumeration<TimeChartValue> e = values.elements(); e.hasMoreElements(); )
	{
		 tcv = e.nextElement();
		 if (tcv.getTimeStamp() == tm)
		 	return tcv;
	}
	
	return null;
}

void _paint(Graphics g)
{
	long tmp_min_time = min_time;
	width = getSize().width;
	height = getSize().height;

	steps_y = 5;
	step_width_y = (float) (height-2*SPACE_Y-25)/(float)steps_y;
	while(step_width_y > 60)
	{
		steps_y*=2;
		step_width_y = (float)(height-2*SPACE_Y-25)/(float)steps_y;
	}

	unit_y = (float) (height-2*SPACE_Y-25)/(float) max_value;

	if(resolution == RES_YEAR)
	{
		tmp_min_time -= 31104000000L;
		steps_x = ((max_time - tmp_min_time)/31104000000L);
		step_width_x = (float) (width-2*SPACE_X-30)/(float)steps_x;
		System.out.println("res=YEAR steps="+steps_x);
	}
	else if(resolution == RES_MONTH)
	{
		tmp_min_time -= 2592000000L;
		steps_x = ((max_time - tmp_min_time)/2592000000L);
		step_width_x = (float) (width-2*SPACE_X-30)/(float)steps_x;
		while(step_width_x < 30)
		{
			steps_x/=2;
			step_width_x = (float) (width-2*SPACE_X-30)/(float)steps_x;
		}
		System.out.println("res=MONTH steps="+steps_x);
	}
	else
	{
		steps_x = 5;
		step_width_x = (float) (width-2*SPACE_X-30)/(float)steps_x;
		while(step_width_x > 140)
		{
			steps_x*=2;
			step_width_x = (float) (width-2*SPACE_X-30)/(float)steps_x;
		}
	}
	unit_x = (float) (width-2*SPACE_X-30)/(float) (max_time - tmp_min_time);
	
	// erase the background
	g.setColor(getBackground());
	g.fillRect(0, 0, width, height);

	g.setColor(gridColor);
	g.setPaintMode();
	
	//Y_scale
	for(int l = 1; l <= steps_y; l++){
		g.drawLine(SPACE_X, height-SPACE_Y-(int)(step_width_y * l), width-SPACE_X, height-SPACE_Y-(int)(step_width_y * l) );
	}
	// X-scale
	for(int l = 1; l <= steps_x; l++){
		g.drawLine(SPACE_X+ (int) (step_width_x * l ), SPACE_Y, SPACE_X+ (int) (step_width_x * l), height-SPACE_Y);
	}
	
	g.setColor(textColor);
	g.setPaintMode();
	// Y-axis
	g.drawLine(SPACE_X, SPACE_Y, SPACE_X, height - SPACE_Y + 5);
	// arrow
	g.drawLine(SPACE_X, SPACE_Y, SPACE_X+5, SPACE_Y+5);
	g.drawLine(SPACE_X, SPACE_Y, SPACE_X-5, SPACE_Y+5);
	g.drawString(unitString, SPACE_X+6, 10);
	//Y_scale
	for(int l = 1; l <= steps_y; l++)
	{
		g.drawLine(SPACE_X-5, height-SPACE_Y-(int)(step_width_y * l), 
			   SPACE_X, height-SPACE_Y-(int)(step_width_y * l));
		g.drawString(decForm.format((double) (l*max_value/(float)steps_y) * scaleFactor), 2, 
			     height-SPACE_Y- (int) (step_width_y * l));
	}
	// X-axis
	g.drawLine(width-SPACE_X, height-SPACE_Y, SPACE_X-5, height-SPACE_Y);
	// arrow
	g.drawLine(width-SPACE_X, height-SPACE_Y, width - SPACE_X-5, height-SPACE_Y+5);
	g.drawLine(width-SPACE_X, height-SPACE_Y, width - SPACE_X-5, height-SPACE_Y-5);
	g.drawString(timeString, width-timeString.length()*7, height-SPACE_Y-2);
	// X-scale
	for(long l = 1; l <= steps_x; l++)
	{
		g.drawLine(SPACE_X +(int)(step_width_x * l ), height-SPACE_Y+5, 
			   SPACE_X +(int)(step_width_x * l), height-SPACE_Y);
		g.drawString(""+makeTimeString(tmp_min_time + (l*(max_time-tmp_min_time)/steps_x)), 
			     SPACE_X+ (int) (step_width_x * l)-10, height - 2);
	}
}
public void paint(Graphics g)
{
	_paint(g);
	// draw the values
	g.setColor(chartColor);
	TimeChartValue cv = values.elementAt(0), cvLast;
	for(java.util.Enumeration<TimeChartValue> en = values.elements();en.hasMoreElements();)
	{
		cvLast = cv;
		cv = en.nextElement();
		g.drawOval(SPACE_X + (int) ((cv.getTimeStamp() - min_time) * unit_x) - 1, 
			   (int) (height-SPACE_Y- cv.getValue()*unit_y)-1, 2, 2);
		if(0 != mLimit)
		{
		    g.drawLine(SPACE_X + (int) ((cvLast.getTimeStamp() - min_time) * unit_x) - 1, 
			   (int) (height-SPACE_Y- cvLast.getAvg()*unit_y)-1, 
			   SPACE_X + (int) ((cv.getTimeStamp() - min_time) * unit_x) - 1, 
			   (int) (height-SPACE_Y- cv.getAvg()*unit_y)-1);
		}
	}
}
/**
 * This method was created in VisualAge.
 */
public void reset()
{
		// clear the list
	values.removeAllElements();

		// defaults
	max_value = 1;
	max_time = 600;
	
	repaint();
}
public void setChartColor(Color newValue)
{
	this.chartColor = newValue;
}
public void setGridColor(Color newValue)
{
	this.gridColor = newValue;
}
public void setMaxTime(int tm) {
	max_time = tm;
}
public void setMaxValue(float mx) {
	max_value = mx;
}
public void setTextColor(Color newValue) {
	this.textColor = newValue;
}
public void setUnit(float aFactor, String aUnit)
{
	scaleFactor = aFactor;
	unitString = aUnit;

		// make max_value non fractional
	max_value = ((int) (max_value * scaleFactor +0.9)/scaleFactor);
	
	repaint();
}
public void update(Graphics g)
{
	paint(g);
}
}
