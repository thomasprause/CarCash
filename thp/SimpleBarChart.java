/*
 * Created on Nov 14, 2009
 *
 */
package thp;

import java.awt.*;
import java.util.*;
import java.text.*;

import javax.swing.*;

/**
 * A panel which shows a bar chart/diagram with the appropriate methods to set
 * the unit (including scale factor) and maximum for the y axis. Values are added
 * as SimpleValues.
 */
@SuppressWarnings("serial")
public class SimpleBarChart extends JPanel
{
	public class SimpleValue
	{
		public SimpleValue(float val, String des)
		{ 
			value=val;
		    descr=des;
		}
		String getDescr()
		{ return descr; }
		float getValue()
		{ return value; }
	    private float value = -1;
	    private String descr = "**undef**";
	};
	int resolution = 1;
	private int width			= -1;
	int height			= -1;
	long steps_x			= 1;
	private int steps_y			= 1;
	float step_width_x		= 1;
	private float step_width_y		= 1;
	float unit_x			= 1;
	float unit_y			= 1;

	Vector<SimpleValue> values			= null;
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

	private DecimalFormat decForm 	= null;
	DateFormat    timeFormat        = null;
	
		// all the colors
	Color chartColor 		= Color.red;
	private Color gridColor 		= Color.gray;
	private Color textColor 		= Color.black;

public SimpleBarChart()
{
	super();
	setDefaults();
}
public SimpleBarChart(float limit)
{
	super();
	setDefaults();
	mLimit = limit;
	max_value = limit;
}

private void setDefaults()
{
	setDoubleBuffered(true);	
	values = new Vector<SimpleValue>();
	decForm = new DecimalFormat("#0.00");
	timeFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
}
public void addValue(SimpleValue cv)
{
	values.addElement(cv);
	//System.out.println("add: " +cv.getTimeStamp());

	if ((0 == mLimit) && (cv.getValue() > max_value)) 
	{
		int tmp = (int) cv.getValue();
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
			System.out.println("val=" + tmp + " incr=" + incr + " maxVal=" + max_value);
		}
	}

	//System.out.println("max Time = " +max_time);
	//System.out.println("min Time = " +min_time +"\n");
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

void _paint(Graphics g)
{
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
	unit_x = (float) (width-2*SPACE_X-30)/values.size();
	
	// erase the background
	g.setColor(getBackground());
	g.fillRect(0, 0, width, height);

	g.setColor(gridColor);
	g.setPaintMode();
	
	//Y_scale
	for(int l = 1; l <= steps_y; l++){
		g.drawLine(SPACE_X, height-SPACE_Y-(int)(step_width_y * l), width-SPACE_X, height-SPACE_Y-(int)(step_width_y * l) );
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
}
public void paint(Graphics g)
{
	_paint(g);
	// draw the values
	g.setColor(chartColor);
	SimpleValue cv = values.elementAt(0);
	int idx = 0;
	for(java.util.Enumeration<SimpleValue> en = values.elements();en.hasMoreElements();)
	{
		cv = en.nextElement();
		g.fillRect(SPACE_X + idx * (int)unit_x, 
				(int) (height-SPACE_Y- cv.getValue()*unit_y),
				(int)unit_x - 1,
				(int) (cv.getValue()*unit_y)+1);
		idx++;
	}
}
/**
 * This method was created in VisualAge.
 */
public void reset()
{		// clear the list
	values.removeAllElements();

		// defaults
	max_value = 1;
	
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
