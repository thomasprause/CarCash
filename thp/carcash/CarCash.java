/*
 * Created on Oct 12, 2006
 *
 */
package thp.carcash;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.*;

import thp.carcash.Car;
import thp.carcash.Car.Fuel;
import thp.carcash.Car.Garage;
import thp.carcash.Car.Insurance;
import thp.carcash.Car.OtherExpense;
import thp.carcash.Car.Tax;
import thp.carcash.Car.TyreChange;
import thp.carcash.Car.Tyres;

import thp.*;


/**
 * @author prause
 *
 */
@SuppressWarnings("serial")
public class CarCash extends JFrame  implements ActionListener, MouseListener, WindowListener, MouseMotionListener
{
	class InsuranceTable extends JTable
	{
		public InsuranceTable(TableModel arg0) {
			super(arg0);
		}
		public void editingStopped(ChangeEvent e)
		{
			switch(getEditingColumn())
			{
			case 1:
				print_log("row="+getEditingRow() +" res=" +getCellEditor().getCellEditorValue());
				CarCash.this.curCar.insuranceList.elementAt(getEditingRow()).descr = getCellEditor().getCellEditorValue().toString();
				CarCash.this.modified = true;
				break;
				
			}
			removeEditor();
		}
	}
	private boolean	 modified	    = false;
	private JMenuItem    menuItem_close     = null;
	private JMenuItem    menuItem_save      = null;
	private JMenuItem    menuItem_load      = null;
	private JMenuItem    menuItem_new       = null;
	private JMenuItem    menuItem_sold      = null;
	private JMenuItem    menuItem_fuel      = null;
	private JMenuItem    menuItem_newWheel  = null;
	private JMenuItem    menuItem_cngWheel  = null;
	private JMenuItem    menuItem_oil       = null;
	private JMenuItem    menuItem_garage    = null;
	private JMenuItem    menuItem_insurance = null;
	private JMenuItem    menuItem_tax       = null;
	private JMenuItem    menuItem_other     = null;
	private JMenuItem    menuItem_fuelcons  = null;
	private JMenuItem    menuItem_fuelprice = null;
	private JMenuItem    menuItem_distYear  = null;
	private JMenuItem    menuItem_distMonth = null;
	private JMenuItem    menuItem_statYear  = null;
	private JMenuItem    menuItem_statYear2 = null;
	private JMenuItem    menuItem_statMonth = null;
	private JMenuItem    menuItem_lastUsed1 = null;
	private JMenuItem    menuItem_lastUsed2 = null;
	private JMenuItem    menuItem_lastUsed3 = null;
	private JMenuItem    menuItem_lastUsed4 = null;
	private JMenuItem    menuItem_about     = null;
	private JPanel       contentsPane       = null;
	private JPanel       carOverviewPane    = null;
	private JPanel       tyrePane	        = null;
	private JScrollPane  fuelPane           = null;
	private JTable       fuelTable          = null;
	private JTable       tyreTable          = null;
	private JTable       tyreChangeTable    = null;
	private JTable       garageTable        = null;
	private InsuranceTable insuranceTable   = null;
	private JTable		 taxTable			= null;
	private JTable 		 otherTable			= null;
	private JLabel       lbVendor           = null;
	private JLabel       lbModel            = null;
	private JLabel       lbEZ               = null;
	private JLabel       lbBuy              = null;
	private JLabel       lbPrice            = null;
	private JLabel       lbHU               = null;
	private JLabel       lbAU               = null;
	private JLabel       lbVerbr            = null;
	private JLabel       lbEmiss            = null;
	private JLabel       lbDist             = null;
	private JLabel       lbCostKM           = null;
	private JLabel       lbCostKM2          = null;
	private JLabel 		 lbObsolescenceKM 	= null;
	private JLabel       lbCostYear         = null;
	private JLabel       lbCostMonth        = null;
	private JLabel       lbCostYear2        = null;
	private JLabel       lbCostMonth2       = null;
	private JLabel 		 lbObsolescenceYear	= null;
	private JLabel 		 lbObsolescenceMonth= null;
	private JLabel       lbCostAll          = null;
	private JLabel       lbCostTyre         = null;
	private JLabel       lbCostFuel         = null;
	private JLabel       lbCostGarage       = null;
	private JLabel       lbCostIns          = null;
	private JLabel       lbCostTax          = null;
	private JLabel       lbCostOther        = null;
	private JLabel       lbDistYear         = null;
	private JLabel       lbServiceDist      = null;
	private JLabel       lbServiceTime      = null;
	private JLabel       lbStatus           = null;
	//private JLabel 		 lbCurrentTyreSet   = null;
	private String       fileName           = null;
	private String[]     lastUsed			= {"-empty-","-empty-","-empty-","-empty-"};
	private Car 	 	 curCar 	    	= new Car();
	private DecimalFormat _2digFormat       = new DecimalFormat("##0.00");
	private DateFormat    dateFormat	    = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
	private DateFormat    longDateFormat	= DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == menuItem_close)
		{
			if(checkModified()) System.exit(0);
			return;
		}
		if (e.getSource() == menuItem_new)
		{
			if(checkModified())
			{
				modified = false;
				fileName = "";
				NewCarDlg ncd = new NewCarDlg(this);
				ncd.setVisible(true);
			}
			return;
		}
		if (e.getSource() == menuItem_sold)
		{
			SellCarDlg scd = new SellCarDlg(this);
			scd.setVisible(true);
			print_log("sell the car ...");
			return;
		}
		if (e.getSource() == menuItem_save)
		{
			if(fileName == "")
			{
				JFileChooser chooser = new JFileChooser();
				int ret = chooser.showSaveDialog(this);  
				
				if(ret == JFileChooser.APPROVE_OPTION) 
				{
					save(chooser.getSelectedFile().getAbsolutePath());
				}
			} else
			{
				save(fileName);
			}
			return; 
		}
		if (e.getSource() == menuItem_load)
		{
			String path = "";
			if(fileName != null) path = new File(fileName).getParent();
			print_log("path='"+path+"'");
			JFileChooser chooser = new JFileChooser(path); 
			// Note: source for ExtensionFileFilter can be found in the SwingSet demo 
			//ExtensionFileFilter filter = new ExtensionFileFilter(); 
			//filter.addExtension("room"); 
			//filter.setDescription("room definition files"); 
			//chooser.setFileFilter(filter); 
			int ret = chooser.showOpenDialog(this); 
			
			if(ret == JFileChooser.APPROVE_OPTION) 
			{
				load(chooser.getSelectedFile().getAbsolutePath());
			}
			return;
		}
		if (e.getSource() == menuItem_fuel)
		{
			FuelDlg fd = new FuelDlg(this);
			fd.setLocationByPlatform(true);
			fd.setVisible(true);
			return;
		}
		if (e.getSource() == menuItem_newWheel)
		{
			NewWheelDlg nwd = new NewWheelDlg(this);
			nwd.setLocationByPlatform(true);
			nwd.setVisible(true);
			return;
		}
		if (e.getSource() == menuItem_cngWheel)
		{
			if(curCar.tyreList.isEmpty())
			{
				JOptionPane.showMessageDialog(this, 
						"Bevor Sie Reifen wechseln können, müssen Sie erste welche kaufen!",
						"Keine Reifen vorhanden", JOptionPane.WARNING_MESSAGE);
				return;
			}
			NewWheelChangeDlg nwd = new NewWheelChangeDlg(this);
			nwd.setLocationByPlatform(true);
			nwd.setVisible(true);
			return;
		}
		if (e.getSource() == menuItem_garage)
		{
			NewGarageDlg ngd = new NewGarageDlg(this);
			ngd.setLocationByPlatform(true);
			ngd.setVisible(true);
			return;
		}
		if (e.getSource() == menuItem_insurance)
		{
			NewInsuranceDlg nid = new NewInsuranceDlg(this);
			nid.setLocationByPlatform(true);
			nid.setVisible(true);
			return;
		}
		if (e.getSource() == menuItem_tax)
		{
			NewTaxDlg ntd = new NewTaxDlg(this);
			ntd.setLocationByPlatform(true);
			ntd.setVisible(true);
			return;
		}
		if (e.getSource() == menuItem_other)
		{
			NewOtherDlg nod = new NewOtherDlg(this);
			nod.setLocationByPlatform(true);
			nod.setVisible(true);
			return;
		}
		if (e.getSource() == menuItem_fuelcons)
		{
			JFrame frame = new JFrame();
			frame.setTitle(curCar.sVendor +"-" +curCar.sModel +" - Verbrauchsdiagramm");
			TimeChart tc = new TimeChart(12);
			Car.Fuel f1 = null; // , f2 = null;
			float /*litres = 0,*/ avg, avg1=0, avg2=0, avg3=0, avg4=0,avg5=0,avg6;
			int /*km_start = -1, km_end = -1,*/ km1=0, km2=0, km3=0, km4=0, km5=0, km6=0,km7;
			
			for(Enumeration<?> en=curCar.fuelList.elements(); en.hasMoreElements();)
			{
				f1 = (Fuel) en.nextElement();
				avg6 = avg5; avg5 = avg4; avg4 = avg3; avg3 = avg2; avg2 = avg1;
				avg1 = f1.amount;
				km7 = km6; km6 = km5; km5 = km4; km4 = km3;	km3 = km2; km2 = km1;
				if(0 == km1)
				{
					km6 = f1.km; km5 = f1.km;	km4 = f1.km;	km3 = f1.km;
					km2 = f1.km;
					km7 = f1.km;
					avg1=0;
				}
				km1 = f1.km;
				
				if(km1==km2)
					avg = 0;
				else
				{
					avg = (avg1+avg2+avg3+avg4+avg5+avg6)/(km1-km7)*100;
				print_log("liter="+(avg1+avg2+avg3+avg4+avg5+avg6) +" dist="+(km1-km7));
				tc.addValue(new TimeChartValue( f1.amount/(km1-km2)*100,
							avg, f1.date.getTime()));
				}
				/*
				 * f1 = f2;
				f2 = (Fuel) en.nextElement();
				avg6 = avg5;	avg5 = avg4;	avg4 = avg3;	avg3 = avg2;	avg2 = avg1;
				avg1 = f2.amount;
				km7 = km6;	km6 = km5;	km5 = km4;	km4 = km3;	km3 = km2;			km2 = km1;
				km1 = f2.km;
				if(null == f1)
				{
					km_start = f2.km;
					km_end   = f2.km;
					km6 = f2.km;	km5 = f2.km;	km4 = f2.km;	km3 = f2.km;	km2 = f2.km;
				}
				if(f2.km < km_start) km_start = f2.km;
				if(f2.km > km_end) km_end = f2.km;
				
				if(f1 != null)
				{
					print_log("liter="+(avg1+avg2+avg3+avg4+avg5+avg6) +" dist="+(km1-km7));
					litres+=f2.amount;
					//print_log("liter=" +litres +" dist="+(km_end-km_start) +" verbr=" +(litres/(km_end-km_start)*100));
					tc.addValue(new TimeChartValue( f2.amount/(f2.km-f1.km)*100,
						//	(litres/(km_end-km_start)*100), f2.date.getTime()));
							(avg1+avg2+avg3+avg4+avg5+avg6)/(km1-km7)*100, f2.date.getTime()));
				}
				 */
			}
			
			frame.getContentPane().add(tc);
			frame.setBounds(30, 20, 600, 400);
			frame.setVisible(true);
			return;
		}
		if(e.getSource() == menuItem_fuelprice)
		{
			JFrame frame = new JFrame();
			frame.setTitle(curCar.sVendor +"-" +curCar.sModel +" - Benzinpreisdiagramm");
			TimeChart tc = new TimeChart();
			
			for(Enumeration<?> en=curCar.fuelList.elements(); en.hasMoreElements();)
			{
				Fuel f = (Fuel) en.nextElement();
				tc.addValue(new TimeChartValue((float) f.price/f.amount, f.date.getTime()));
			}
			
			frame.getContentPane().add(tc);
			frame.setBounds(30, 20, 600, 400);
			frame.setVisible(true);
			return;
		}
		if(e.getSource() == menuItem_distYear)
		{
			JFrame frame = new JFrame();
			frame.setTitle(curCar.sVendor +"-" +curCar.sModel +" - Fahrstrecke pro Jahr");
			TimeBarChart tc = new TimeBarChart(TimeChart.RES_YEAR);
			tc.setMaxValue(2000);
			
			Fuel lastF = (Fuel) curCar.fuelList.firstElement();
			Fuel f = lastF;
			GregorianCalendar lastC = new GregorianCalendar();
			GregorianCalendar cal = new GregorianCalendar();
			
			lastC.setTime(lastF.date);
			for(Enumeration<Fuel> en=curCar.fuelList.elements(); en.hasMoreElements();)
			{
				f = (Fuel) en.nextElement();
				cal.setTime(f.date);
				if(cal.get(GregorianCalendar.YEAR) > lastC.get(GregorianCalendar.YEAR))
				{
					lastC.set(Calendar.HOUR, 1);
					lastC.set(Calendar.DAY_OF_MONTH, 1);
					lastC.set(Calendar.MONTH, Calendar.JANUARY);
					tc.addValue(new TimeChartValue((float) (f.km-lastF.km), lastC.getTimeInMillis()));
					print_log("add dist: "+(f.km-lastF.km) +" km bei " +lastC.getTime());
					lastC.setTime(f.date);
					lastF = f;
				}
			}
			// dont miss the last year
			lastC.set(Calendar.HOUR, 1);
			lastC.set(Calendar.DAY_OF_MONTH, 1);
			lastC.set(Calendar.MONTH, Calendar.JANUARY);
			tc.addValue(new TimeChartValue((float) (f.km-lastF.km), lastC.getTimeInMillis()));
			print_log("add dist: "+(f.km-lastF.km) +" km bei " +lastC.getTime());
			
			frame.getContentPane().add(tc);
			frame.setBounds(30, 20, 600, 400);
			frame.setVisible(true);
			return;
		}
		if(e.getSource() == menuItem_distMonth)
		{
			JFrame frame = new JFrame();
			frame.setTitle(curCar.sVendor +"-" +curCar.sModel +" - Fahrstrecke pro Monat");
			TimeBarChart tc = new TimeBarChart(TimeChart.RES_MONTH);
			tc.setMaxValue(200);
			
			Fuel lastF = (Fuel) curCar.fuelList.firstElement();
			Fuel f = lastF;
			GregorianCalendar lastC = new GregorianCalendar();
			GregorianCalendar cal = new GregorianCalendar();
			
			lastC.setTime(lastF.date);
			for(Enumeration<Fuel> en=curCar.fuelList.elements(); en.hasMoreElements();)
			{
				f = (Fuel) en.nextElement();
				cal.setTime(f.date);
				if( (cal.get(GregorianCalendar.MONTH) > lastC.get(GregorianCalendar.MONTH))
				 || (cal.get(GregorianCalendar.YEAR) > lastC.get(GregorianCalendar.YEAR)) )
				{
					tc.addValue(new TimeChartValue((float) (f.km-lastF.km), lastC.getTimeInMillis()));
					print_log("add dist: "+(f.km-lastF.km) +" km bei " +lastC.getTime());
					lastC.setTime(f.date);
					lastC.set(Calendar.HOUR, 1);
					lastC.set(Calendar.DAY_OF_MONTH, 15);
					lastF = f;
				}
			}
			// don't miss the last month
			tc.addValue(new TimeChartValue((float) (f.km-lastF.km), lastC.getTimeInMillis()));
			print_log("add dist: "+(f.km-lastF.km) +" km at " +lastC.getTime());
			
			frame.getContentPane().add(tc);
			frame.setBounds(30, 20, 600, 400);
			frame.setVisible(true);
			return;
		}
		if(e.getSource() == menuItem_statYear)
		{
			JFrame frame = new JFrame();
			frame.setTitle(curCar.sVendor +"-" +curCar.sModel +" - Kosten pro Jahr");
			TimeBarChart tc = new TimeBarChart(TimeChart.RES_YEAR);
			tc.setMaxValue(100);
			
			for(Enumeration<Tyres> en=curCar.tyreList.elements(); en.hasMoreElements();)
			{
				Tyres t = (Tyres) en.nextElement();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(t.date);
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				tc.addValue(t.price, cal.getTimeInMillis());
			}
			for(Enumeration<Garage> en=curCar.garageList.elements(); en.hasMoreElements();)
			{
				Garage t = (Garage) en.nextElement();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(t.date);
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				tc.addValue(t.price, cal.getTimeInMillis());
			}
			for(Enumeration<Fuel> en=curCar.fuelList.elements(); en.hasMoreElements();)
			{
				Fuel f = (Fuel) en.nextElement();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(f.date);
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				tc.addValue(f.price, cal.getTimeInMillis());
			}
			frame.getContentPane().add(tc);
			frame.setBounds(30, 20, 600, 400);
			frame.setVisible(true);
			return;
		}
		if(e.getSource() == menuItem_statYear2)
		{
			JFrame frame = new JFrame();
			frame.setTitle(curCar.sVendor +"-" +curCar.sModel +" - Kosten pro Jahr");
			StackedBarChart sbc = new StackedBarChart(TimeChart.RES_YEAR);
			sbc.setMaxValue(100);
			
			for(Enumeration<Tyres> en=curCar.tyreList.elements(); en.hasMoreElements();)
			{
				Tyres t = (Tyres) en.nextElement();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(t.date);
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				sbc.addValue(t.price, cal.getTimeInMillis(), 1);
			}
			for(Enumeration<Garage> en=curCar.garageList.elements(); en.hasMoreElements();)
			{
				Garage t = (Garage) en.nextElement();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(t.date);
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				sbc.addValue(t.price, cal.getTimeInMillis(), 2);
			}
			for(Enumeration<Fuel> en=curCar.fuelList.elements(); en.hasMoreElements();)
			{
				Fuel f = (Fuel) en.nextElement();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(f.date);
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				sbc.addValue(f.price, cal.getTimeInMillis(), 3);
			}
			frame.getContentPane().add(sbc);
			frame.setBounds(30, 20, 600, 400);
			frame.setVisible(true);
			return;
		}
		if(e.getSource() == menuItem_statMonth)
		{
			JFrame frame = new JFrame();
			frame.setTitle(curCar.sVendor +"-" +curCar.sModel +" - Kosten pro Monat");
			TimeBarChart tc = new TimeBarChart(TimeChart.RES_MONTH);
			tc.setMaxValue(100);
			tc.setChartColor(Color.GREEN);
			
			for(Enumeration<Tyres> en=curCar.tyreList.elements(); en.hasMoreElements();)
			{
				Tyres t = (Tyres) en.nextElement();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(t.date);
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				tc.addValue(t.price, cal.getTimeInMillis());
			}
			for(Enumeration<Garage> en=curCar.garageList.elements(); en.hasMoreElements();)
			{
				Garage t = (Garage) en.nextElement();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(t.date);
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				tc.addValue(t.price, cal.getTimeInMillis());
			}
			for(Enumeration<Fuel> en=curCar.fuelList.elements(); en.hasMoreElements();)
			{
				Fuel f = (Fuel) en.nextElement();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(f.date);
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				tc.addValue(f.price, cal.getTimeInMillis());
			}
			for(Enumeration<Insurance> en=curCar.insuranceList.elements(); en.hasMoreElements();)
			{
				Insurance i = (Insurance) en.nextElement();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(i.date);
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				tc.addValue(i.price, cal.getTimeInMillis());
			}
			for(Enumeration<Tax> en=curCar.taxList.elements(); en.hasMoreElements();)
			{
				Tax t = (Tax) en.nextElement();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(t.date);
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				tc.addValue(t.price, cal.getTimeInMillis());
			}
			frame.getContentPane().add(tc);
			frame.setBounds(30, 20, 600, 400);
			frame.setVisible(true);
			return;
		}
		if( (e.getSource() == menuItem_lastUsed1)
		 || (e.getSource() == menuItem_lastUsed2)
		 || (e.getSource() == menuItem_lastUsed3)
		 || (e.getSource() == menuItem_lastUsed4) )
		{
			if(!checkModified()) return;
			load(((JMenuItem)(e.getSource())).getText());
			return;
		}
		if(e.getSource() == menuItem_about)
		{
			print_log("show about");
			JOptionPane.showMessageDialog(this , "CarCash Version 0.3 beta\nBenutzung erfolgt auf eigene Gefahr\n\n"
					+"(C) 2006,2010 by Thomas Prause", "Über ...", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		print_error("Unhandled event: " +e.getSource());
	}
	public void mouseClicked(MouseEvent e){};
	public void mouseEntered(MouseEvent e) {};
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { };
	public void mouseReleased(MouseEvent e) {};
	public void mouseDragged(MouseEvent e) {};
	public void mouseMoved(MouseEvent e) { };
	public void windowActivated(WindowEvent e) {};
	public void windowClosed(WindowEvent e) {};
	public void windowClosing(WindowEvent e)
	{
		checkModified();  // there is no way to stop here
		System.exit(0);
	};
	public void windowDeactivated(java.awt.event.WindowEvent e) {};
	public void windowDeiconified(java.awt.event.WindowEvent e) {};
	public void windowIconified(java.awt.event.WindowEvent e) {};
	public void windowOpened(java.awt.event.WindowEvent e) {};
	private boolean checkModified()
	{
		if(modified)
		{
			print_log("data is modified!");
			Object[] options = {"Ja", "Nein", "Abbrechen"};
			int n = JOptionPane.showOptionDialog(this, "Sollen die Änderungen in " +fileName +" gespeichert werden?",
					"Daten wurden geändert", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
			if(0 == n) save(fileName);
			if(2 == n) return false;
		}
		try
		{
			PrintWriter lUsed = new PrintWriter(new BufferedWriter(
								new FileWriter(System.getProperty("user.home")+"/.CarCash_last_used")));
			lUsed.println(lastUsed[0]);
			lUsed.println(lastUsed[1]);
			lUsed.println(lastUsed[2]);
			lUsed.println(lastUsed[3]);
			lUsed.close();
			print_log("last used saved");
		}
		catch(Throwable t){print_log("save last used failed: "+t +": "+t.getMessage());}
		return true;
	}
	
	class FuelDlg extends JDialog implements java.awt.event.ActionListener
	{
		JButton exitBtn, okBtn ;
		JTextField tfDate, tfKM, tfAmount, tfPrice;
		
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if (e.getSource() == okBtn)
			{
				try
				{
					Fuel f = curCar.new Fuel(dateFormat.parse(tfDate.getText()), Integer.parseInt(tfKM.getText()),
							Float.parseFloat(tfAmount.getText()), Float.parseFloat(tfPrice.getText()));
					curCar.fuelList.add(f);
					modified = true;
				}
				catch(Exception ex)
				{
					print_error(ex.getMessage());
					return;
				}
			}
			dispose();
			fuelTable.doLayout();
			fuelPane.validate();
			updateOverviewPane();
		}
		
		public FuelDlg(Frame f)
		{
			super (f, "Tanken", true);
			SpringLayout layout = new SpringLayout();
			Container contentPane = getContentPane();
			contentPane.setLayout(layout);
			setSize(240, 170);
			
			JLabel lb = new JLabel("Datum");
			JLabel lbMoney = new JLabel("Betrag (EUR)");
			tfDate = new JTextField(7);
			tfDate.setText(dateFormat.format(new Date()));
			contentPane.add(lb);
			contentPane.add(tfDate);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.NORTH, contentPane);
			layout.putConstraint(SpringLayout.WEST, tfDate, 5, SpringLayout.EAST, lbMoney);
			layout.putConstraint(SpringLayout.NORTH, tfDate, 5, SpringLayout.NORTH, contentPane);
			
			lb = new JLabel("km-Stand");
			tfKM = new JTextField(7);
			getContentPane().add(tfKM);
			contentPane.add(lb);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfKM);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfDate);
			layout.putConstraint(SpringLayout.WEST, tfKM, 5, SpringLayout.EAST, lbMoney);
			layout.putConstraint(SpringLayout.NORTH, tfKM, 5, SpringLayout.SOUTH, tfDate);
			
			lb = new JLabel("Menge (l)");
			tfAmount = new JTextField(7);
			getContentPane().add(tfAmount);
			contentPane.add(lb);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfKM);
			layout.putConstraint(SpringLayout.WEST, tfAmount, 5, SpringLayout.EAST, lbMoney);
			layout.putConstraint(SpringLayout.NORTH, tfAmount, 5, SpringLayout.SOUTH, tfKM);
			
			tfPrice = new JTextField(7);
			getContentPane().add(tfPrice);
			contentPane.add(lbMoney);
			layout.putConstraint(SpringLayout.WEST, lbMoney, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.NORTH, lbMoney, 5, SpringLayout.SOUTH, tfAmount);
			layout.putConstraint(SpringLayout.WEST, tfPrice, 5, SpringLayout.EAST, lbMoney);
			layout.putConstraint(SpringLayout.NORTH, tfPrice, 5, SpringLayout.SOUTH, tfAmount);
			
			okBtn = new JButton("Ok");
			getContentPane().add(okBtn);
			layout.putConstraint(SpringLayout.WEST, okBtn, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, okBtn, -5, SpringLayout.SOUTH, contentPane);
			
			exitBtn = new JButton("Cancel");
			getContentPane().add(exitBtn);
			layout.putConstraint(SpringLayout.EAST, exitBtn, -5, SpringLayout.EAST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, exitBtn, -5, SpringLayout.SOUTH, contentPane);
			
			// init connections     
			exitBtn.addActionListener(this);
			okBtn.addActionListener(this);
		}
	}
	
	class NewCarDlg extends JDialog implements java.awt.event.ActionListener, EventListener
	{
		JButton exitBtn = null, okBtn = null;
		JTextField tfVendor, tfModel, tfPrice, tfBuy, tfEZ, tfAU, tfHU, tfServiceDist, tfServiceTime;
		JCheckBox cbNew;
		JRadioButton rbGasoline, rbDiesel;
		SimpleDateFormat MMYYformat = new SimpleDateFormat("MM/yy");
		
		
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if (e.getSource() == okBtn)
			{
				try
				{
					curCar = new Car(tfVendor.getText(), tfModel.getText());
					curCar.setPrice(Float.parseFloat(tfPrice.getText()));
					curCar.setServiceDist(Integer.parseInt(tfServiceDist.getText()));
					curCar.setServiceTime(Integer.parseInt(tfServiceTime.getText()));
					if(cbNew.isSelected())
					{ // a new car
						curCar.dBuy = MMYYformat.parse(tfEZ.getText());
						curCar.setEZ(curCar.dBuy);
						curCar.setHU(curCar.dBuy);
						curCar.setAU(curCar.dBuy);
					}
					else {
						curCar.setEZ(MMYYformat.parse(tfEZ.getText()));
						curCar.setHU(MMYYformat.parse(tfHU.getText()));
						curCar.setAU(MMYYformat.parse(tfAU.getText()));
						curCar.dBuy = MMYYformat.parse(tfBuy.getText());
					}
					curCar.mWasNew = cbNew.isSelected();
					print_log("date is " +curCar.dBuy);
					print_log("ez is " +MMYYformat.parse(tfEZ.getText()));
					updateOverviewPane();
					modified = true;
					enableMenus();
					//fileName = tfModel.getText()+".car";
				}
				catch(Exception ex)
				{
					JOptionPane.showMessageDialog(this, ex.getClass()+"\nProblem: "+ex.getMessage(),
							"Ungültige Werte", JOptionPane.WARNING_MESSAGE);
					 
					print_error(ex.getMessage());
					return;
				}
			}
			if(e.getSource() == cbNew)
			{
				print_log("CB event");
				tfBuy.setEditable(!tfBuy.isEditable());
				tfHU.setEditable(!tfHU.isEditable());
				tfAU.setEditable(!tfAU.isEditable());
				return;
			}
			dispose(); // this saves the check for the cancel btn
		}
		
		public NewCarDlg(Frame f)
		{
			super (f, "Neues Fahrzeug anlegen", true);
			
			SpringLayout layout = new SpringLayout();
			Container contentPane = getContentPane();
			contentPane.setLayout(layout);
			setSize(420, 290);
			
			JLabel lb = new JLabel("Hersteller");
			JLabel lbEZ = new JLabel("Erstzulassung (mm/jj)");
			contentPane.add(lb);
			tfVendor = new JTextField(20);
			contentPane.add(tfVendor);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfVendor);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.NORTH, contentPane);
			layout.putConstraint(SpringLayout.WEST, tfVendor, 5, SpringLayout.EAST, lbEZ);
			layout.putConstraint(SpringLayout.NORTH, tfVendor, 5, SpringLayout.NORTH, contentPane);
			
			lb = new JLabel("Model");
			contentPane.add(lb);
			tfModel = new JTextField(20);
			contentPane.add(tfModel);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfModel);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfVendor);
			layout.putConstraint(SpringLayout.WEST, tfModel, 0, SpringLayout.WEST, tfVendor);
			layout.putConstraint(SpringLayout.NORTH, tfModel, 5, SpringLayout.SOUTH, tfVendor);

			rbGasoline = new JRadioButton("Benziner");
			rbDiesel = new JRadioButton("Diesel");
			ButtonGroup btnGroup = new ButtonGroup();
			btnGroup.add(rbGasoline);
			btnGroup.add(rbDiesel);
			rbGasoline.setSelected(true);
			contentPane.add(rbGasoline);
			contentPane.add(rbDiesel);
			layout.putConstraint(SpringLayout.WEST, rbGasoline, 0, SpringLayout.WEST, tfModel);
			layout.putConstraint(SpringLayout.NORTH, rbGasoline, 5, SpringLayout.SOUTH, tfModel);
			
			layout.putConstraint(SpringLayout.WEST, rbDiesel, 10, SpringLayout.EAST, rbGasoline);
			layout.putConstraint(SpringLayout.NORTH, rbDiesel, 5, SpringLayout.SOUTH, tfModel);
			
			lb = new JLabel("Preis");
			contentPane.add(lb);
			tfPrice = new JTextField(7);
			contentPane.add(tfPrice);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfPrice);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, rbGasoline);
			layout.putConstraint(SpringLayout.WEST, tfPrice, 0, SpringLayout.WEST, tfModel);
			layout.putConstraint(SpringLayout.NORTH, tfPrice, 5, SpringLayout.SOUTH, rbGasoline);
			
			lb = new JLabel("EUR");
			contentPane.add(lb);
			layout.putConstraint(SpringLayout.WEST, lb, 5, SpringLayout.EAST, tfPrice);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, rbGasoline);
			
			contentPane.add(lbEZ);
			tfEZ = new JTextField(5);
			contentPane.add(tfEZ);
			layout.putConstraint(SpringLayout.WEST, lbEZ, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.NORTH, lbEZ, 5, SpringLayout.SOUTH, tfPrice);
			layout.putConstraint(SpringLayout.WEST, tfEZ, 0, SpringLayout.WEST, tfPrice);
			layout.putConstraint(SpringLayout.NORTH, tfEZ, 5, SpringLayout.SOUTH, tfPrice);
			
			lb = new JLabel("Kaufdatum");
			contentPane.add(lb);
			tfBuy = new JTextField(5);
			getContentPane().add(tfBuy);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfBuy);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfEZ);
			layout.putConstraint(SpringLayout.WEST, tfBuy, 0, SpringLayout.WEST, tfVendor);
			layout.putConstraint(SpringLayout.NORTH, tfBuy, 5, SpringLayout.SOUTH, tfEZ);
			
			cbNew = new JCheckBox("Neufahrzeug");
			contentPane.add(cbNew);
			layout.putConstraint(SpringLayout.WEST, cbNew, 5, SpringLayout.EAST, tfBuy);
			layout.putConstraint(SpringLayout.NORTH, cbNew, 5, SpringLayout.SOUTH, tfPrice);
			
			lb = new JLabel("Letzte HU (mm/jj)");
			contentPane.add(lb);
			tfHU = new JTextField(5);
			getContentPane().add(tfHU);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfHU);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfBuy);
			layout.putConstraint(SpringLayout.WEST, tfHU, 0, SpringLayout.WEST, tfVendor);
			layout.putConstraint(SpringLayout.NORTH, tfHU, 5, SpringLayout.SOUTH, tfBuy);
			
			lb = new JLabel("Letzte AU (mm/jj)");
			contentPane.add(lb);
			tfAU = new JTextField(5);
			getContentPane().add(tfAU);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfAU);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfHU);
			layout.putConstraint(SpringLayout.WEST, tfAU, 0, SpringLayout.WEST, tfVendor);
			layout.putConstraint(SpringLayout.NORTH, tfAU, 5, SpringLayout.SOUTH, tfHU);
			
			lb = new JLabel("Inspektionsintervall");
			contentPane.add(lb);
			layout.putConstraint(SpringLayout.WEST, lb, 20, SpringLayout.EAST, tfBuy);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfEZ);
			
			lb = new JLabel("alle");
			contentPane.add(lb);
			tfServiceDist = new JTextField(5);
			getContentPane().add(tfServiceDist);
			layout.putConstraint(SpringLayout.WEST, lb, 30, SpringLayout.EAST, tfBuy);
			layout.putConstraint(SpringLayout.NORTH, lb, 0, SpringLayout.NORTH, tfHU);
			layout.putConstraint(SpringLayout.WEST, tfServiceDist, 5, SpringLayout.EAST, lb);
			layout.putConstraint(SpringLayout.NORTH, tfServiceDist, 0, SpringLayout.NORTH, tfHU);
			JLabel lb2 = new JLabel("KM");
			contentPane.add(lb2);
			layout.putConstraint(SpringLayout.WEST, lb2, 5, SpringLayout.EAST, tfServiceDist);
			layout.putConstraint(SpringLayout.NORTH, lb2, 0, SpringLayout.NORTH, tfHU);
			
			lb = new JLabel("alle");
			contentPane.add(lb);
			tfServiceTime = new JTextField(5);
			getContentPane().add(tfServiceTime);
			layout.putConstraint(SpringLayout.WEST, lb, 30, SpringLayout.EAST, tfBuy);
			layout.putConstraint(SpringLayout.NORTH, lb, 0, SpringLayout.NORTH, tfAU);
			layout.putConstraint(SpringLayout.WEST, tfServiceTime, 5, SpringLayout.EAST, lb);
			layout.putConstraint(SpringLayout.NORTH, tfServiceTime, 0, SpringLayout.NORTH, tfAU);
			lb2 = new JLabel("Monate");
			contentPane.add(lb2);
			layout.putConstraint(SpringLayout.WEST, lb2, 5, SpringLayout.EAST, tfServiceTime);
			layout.putConstraint(SpringLayout.NORTH, lb2, 0, SpringLayout.NORTH, tfAU);
			
			okBtn = new JButton("Ok");
			getContentPane().add(okBtn);
			layout.putConstraint(SpringLayout.WEST, okBtn, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, okBtn, -5, SpringLayout.SOUTH, contentPane);
			
			exitBtn = new JButton("Cancel");
			getContentPane().add(exitBtn);
			layout.putConstraint(SpringLayout.EAST, exitBtn, -5, SpringLayout.EAST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, exitBtn, -5, SpringLayout.SOUTH, contentPane);
			
			// init connections     
			exitBtn.addActionListener(this);
			okBtn.addActionListener(this);
			cbNew.addActionListener(this);
		}
	}
	class SellCarDlg extends JDialog implements java.awt.event.ActionListener
	{
		JButton okBtn, exitBtn;
		JTextField tfDate, tfKM, tfPrice;
		public SellCarDlg(Frame f) {
			super(f, "Fahrzeug verkauft");
			SpringLayout layout = new SpringLayout();
			Container contentPane = getContentPane();
			contentPane.setLayout(layout);
			setSize(380, 160);
			
			JLabel lb = new JLabel("Datum"), lbKM = new JLabel("KM-Stand"), lbPrice = new JLabel("Preis");
			contentPane.add(lb);
			tfDate = new JTextField(10);
			tfDate.setText(dateFormat.format(new Date()));
			contentPane.add(tfDate);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.NORTH, contentPane);
			layout.putConstraint(SpringLayout.WEST, tfDate, 5, SpringLayout.EAST, lbKM);
			layout.putConstraint(SpringLayout.NORTH, tfDate, 5, SpringLayout.NORTH, contentPane);
			
			contentPane.add(lbKM);
			tfKM = new JTextField(10);
			contentPane.add(tfKM);
			layout.putConstraint(SpringLayout.WEST, lbKM, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.NORTH, lbKM, 5, SpringLayout.SOUTH, tfDate);
			layout.putConstraint(SpringLayout.WEST, tfKM, 0, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, tfKM, 5, SpringLayout.SOUTH, tfDate);
			
			contentPane.add(lbPrice);
			tfPrice = new JTextField(10);
			contentPane.add(tfPrice);
			layout.putConstraint(SpringLayout.WEST, lbPrice, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.NORTH, lbPrice, 5, SpringLayout.SOUTH, tfKM);
			layout.putConstraint(SpringLayout.WEST, tfPrice, 0, SpringLayout.WEST, tfKM);
			layout.putConstraint(SpringLayout.NORTH, tfPrice, 5, SpringLayout.SOUTH, tfKM);
			
			okBtn = new JButton("Ok");
			getContentPane().add(okBtn);
			layout.putConstraint(SpringLayout.WEST, okBtn, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, okBtn, -5, SpringLayout.SOUTH, contentPane);
			
			exitBtn = new JButton("Cancel");
			getContentPane().add(exitBtn);
			layout.putConstraint(SpringLayout.EAST, exitBtn, -5, SpringLayout.EAST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, exitBtn, -5, SpringLayout.SOUTH, contentPane);
			
			// init connections     
			exitBtn.addActionListener(this);
			okBtn.addActionListener(this);
		}
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if (e.getSource() == okBtn)
			{
				try
				{
					curCar.sell(dateFormat.parse(tfDate.getText()), Integer.parseInt(tfKM.getText()), Integer.parseInt(tfPrice.getText()));
					updateOverviewPane();
					modified = true;
				}
				catch(Exception ex)
				{
					print_error(ex.getMessage());
					return;
				}
			}
			dispose();
		}
	}
	
	class NewWheelDlg extends JDialog implements java.awt.event.ActionListener
	{
		JButton exitBtn = null, okBtn = null;
		JTextField tfWheels, tfPrice, tfDate, tfKM;
		JCheckBox cb;
		
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if (e.getSource() == okBtn)
			{
				try
				{
					Tyres ty = curCar.new Tyres(dateFormat.parse(tfDate.getText()), 
							tfWheels.getText(), Float.parseFloat(tfPrice.getText()));
					curCar.addTyres(ty);
					if(cb.isSelected())
					{
						TyreChange lastChange = curCar.getLastTyreChange();
						int lastId = lastChange == null ? 0 : lastChange.id;
						curCar.tyreChangeList.add(curCar.new TyreChange(lastId, ty.id, 
									dateFormat.parse(tfDate.getText()), Integer.parseInt(tfKM.getText())));
						tyreChangeTable.revalidate();
					}
					tyreTable.revalidate();
					modified = true;
				}
				catch(Exception ex)
				{
					print_error(ex);
					return;
				}
			}
			dispose();
		}
		
		public NewWheelDlg(Frame f)
		{
			super (f, "Neue Reifen kaufen", true);
			
			SpringLayout layout = new SpringLayout();
			Container contentPane = getContentPane();
			contentPane.setLayout(layout);
			setSize(380, 180);
			
			JLabel lb = new JLabel("Datum"), lbWheel = new JLabel("Bezeichnung");
			contentPane.add(lb);
			tfDate = new JTextField(10);
			tfDate.setText(dateFormat.format(new Date()));
			contentPane.add(tfDate);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.NORTH, contentPane);
			layout.putConstraint(SpringLayout.WEST, tfDate, 5, SpringLayout.EAST, lbWheel);
			layout.putConstraint(SpringLayout.NORTH, tfDate, 5, SpringLayout.NORTH, contentPane);
			
			contentPane.add(lbWheel);
			tfWheels = new JTextField(20);
			contentPane.add(tfWheels);
			layout.putConstraint(SpringLayout.WEST, lbWheel, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.NORTH, lbWheel, 5, SpringLayout.SOUTH, tfDate);
			layout.putConstraint(SpringLayout.WEST, tfWheels, 0, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, tfWheels, 5, SpringLayout.SOUTH, tfDate);
			
			lb = new JLabel("Preis");
			contentPane.add(lb);
			tfPrice = new JTextField(5);
			contentPane.add(tfPrice);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfWheels);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfWheels);
			layout.putConstraint(SpringLayout.WEST, tfPrice, 0, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, tfPrice, 5, SpringLayout.SOUTH, tfWheels);
			lb = new JLabel("EUR");
			contentPane.add(lb);
			layout.putConstraint(SpringLayout.WEST, lb, 5, SpringLayout.EAST, tfPrice);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfWheels);
			
			cb = new JCheckBox("gleich montiert bei KM");
			contentPane.add(cb);
			layout.putConstraint(SpringLayout.WEST, cb, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.NORTH, cb, 5, SpringLayout.SOUTH, lb);
			
			tfKM = new JTextField(6);
			contentPane.add(tfKM);
			layout.putConstraint(SpringLayout.WEST, tfKM, 0, SpringLayout.EAST, cb);
			layout.putConstraint(SpringLayout.NORTH, tfKM, 5, SpringLayout.SOUTH, tfPrice);
			
			okBtn = new JButton("Ok");
			getContentPane().add(okBtn);
			layout.putConstraint(SpringLayout.WEST, okBtn, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, okBtn, -5, SpringLayout.SOUTH, contentPane);
			
			exitBtn = new JButton("Cancel");
			getContentPane().add(exitBtn);
			layout.putConstraint(SpringLayout.EAST, exitBtn, -5, SpringLayout.EAST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, exitBtn, -5, SpringLayout.SOUTH, contentPane);
			
			// init connections     
			exitBtn.addActionListener(this);
			okBtn.addActionListener(this);
		}
	}
	
	class NewWheelChangeDlg extends JDialog implements ActionListener //, MouseListener
	{
		JButton exitBtn, okBtn;
		JTextField tfDate, tfKM;
		JComboBox cbTyres;
		
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if (e.getSource() == cbTyres) 
			{
				print_log("action: " +e);
				return;
			}
			if (e.getSource() == okBtn)
			{
				try
				{
					print_log(""+cbTyres.getSelectedItem());
					print_log("id=" +((Tyres)cbTyres.getSelectedItem()).id);
					TyreChange lastChange = curCar.getLastTyreChange();
					print_log("last="+lastChange);
					int lastId=0;
					if(lastChange != null) lastId = lastChange.id;
					TyreChange newChange = curCar.new TyreChange(lastId, ((Tyres)cbTyres.getSelectedItem()).id,
							dateFormat.parse(tfDate.getText()), Integer.parseInt(tfKM.getText()));
					if(null != lastChange)
					{
						curCar.addDistForId(lastChange.id, newChange.dist-lastChange.dist);
					}
					curCar.tyreChangeList.add(newChange);
					tyreTable.revalidate();
					tyreChangeTable.revalidate();
					modified = true;
				}
				catch(Exception ex)
				{
					print_error(ex.getMessage());
					return;
				}
			}
			dispose();
		}
		
		public NewWheelChangeDlg(Frame f)
		{
			super (f, "Reifen wechseln", true);
			
			SpringLayout layout = new SpringLayout();
			Container contentPane = getContentPane();
			contentPane.setLayout(layout);
			setSize(380, 160);
			
			JLabel lb = new JLabel("Datum"), lbKM = new JLabel("KM-Stand");
			contentPane.add(lb);
			tfDate = new JTextField(10);
			tfDate.setText(dateFormat.format(new Date()));
			contentPane.add(tfDate);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.NORTH, contentPane);
			layout.putConstraint(SpringLayout.WEST, tfDate, 5, SpringLayout.EAST, lbKM);
			layout.putConstraint(SpringLayout.NORTH, tfDate, 5, SpringLayout.NORTH, contentPane);
			
			contentPane.add(lbKM);
			tfKM = new JTextField(10);
			contentPane.add(tfKM);
			layout.putConstraint(SpringLayout.WEST, lbKM, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.NORTH, lbKM, 5, SpringLayout.SOUTH, tfDate);
			layout.putConstraint(SpringLayout.WEST, tfKM, 0, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, tfKM, 5, SpringLayout.SOUTH, tfDate);
			
			lb = new JLabel("Reifensatz");
			contentPane.add(lb);
			cbTyres = new JComboBox(curCar.tyreList);
			cbTyres.setSelectedIndex(0);
			contentPane.add(cbTyres);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfKM);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfKM);
			layout.putConstraint(SpringLayout.WEST, cbTyres, 0, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, cbTyres, 5, SpringLayout.SOUTH, tfKM);
			
			okBtn = new JButton("Ok");
			getContentPane().add(okBtn);
			layout.putConstraint(SpringLayout.WEST, okBtn, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, okBtn, -5, SpringLayout.SOUTH, contentPane);
			
			exitBtn = new JButton("Cancel");
			getContentPane().add(exitBtn);
			layout.putConstraint(SpringLayout.EAST, exitBtn, -5, SpringLayout.EAST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, exitBtn, -5, SpringLayout.SOUTH, contentPane);
			
			// init connections     
			exitBtn.addActionListener(this);
			okBtn.addActionListener(this);
			cbTyres.addActionListener(this);
		}
	}
	
	class NewGarageDlg extends JDialog implements java.awt.event.ActionListener
	{
		JButton exitBtn = null, okBtn = null;
		JTextField tfKm, tfDescr, tfPrice, tfDate;
		JCheckBox cbHU, cbSrv, cbBr;
		
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if (e.getSource() == okBtn)
			{
				try
				{
					curCar.garageList.add(curCar.new Garage(dateFormat.parse(tfDate.getText()), 
							Integer.parseInt(tfKm.getText()), tfDescr.getText(), 
							cbHU.isSelected(), cbSrv.isSelected(), cbBr.isSelected(), 
							Float.parseFloat(tfPrice.getText())));
					garageTable.revalidate();
					updateOverviewPane();
					modified = true;
				}
				catch(Exception ex)
				{
					print_error(ex.getMessage());
					JOptionPane.showMessageDialog(this, "Fehler: "+ex,
							"Ungültige Eingabe", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			dispose();
		}
		
		public NewGarageDlg(Frame f)
		{
			super (f, "Werkstatt", true);
			
			SpringLayout layout = new SpringLayout();
			Container contentPane = getContentPane();
			contentPane.setLayout(layout);
			setSize(440, 160);
			
			JLabel lb = new JLabel("Datum"), lbDescr = new JLabel("durchgef. Arbeiten");
			contentPane.add(lb);
			tfDate = new JTextField(10);
			tfDate.setText(dateFormat.format(new Date()));
			contentPane.add(tfDate);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.NORTH, contentPane);
			layout.putConstraint(SpringLayout.WEST, tfDate, 5, SpringLayout.EAST, lbDescr);
			layout.putConstraint(SpringLayout.NORTH, tfDate, 5, SpringLayout.NORTH, contentPane);
			
			lb = new JLabel("KM-Stand");
			contentPane.add(lb);
			tfKm = new JTextField(5);
			contentPane.add(tfKm);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfDate);
			layout.putConstraint(SpringLayout.WEST, tfKm, 0, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, tfKm, 5, SpringLayout.SOUTH, tfDate);
			
			contentPane.add(lbDescr);
			tfDescr = new JTextField(25);
			contentPane.add(tfDescr);
			layout.putConstraint(SpringLayout.WEST, lbDescr, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.NORTH, lbDescr, 5, SpringLayout.SOUTH, tfKm);
			layout.putConstraint(SpringLayout.WEST, tfDescr, 0, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, tfDescr, 5, SpringLayout.SOUTH, tfKm);
			
			lb = new JLabel("Kosten");
			contentPane.add(lb);
			tfPrice = new JTextField(5);
			contentPane.add(tfPrice);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfDescr);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfDescr);
			layout.putConstraint(SpringLayout.WEST, tfPrice, 0, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, tfPrice, 5, SpringLayout.SOUTH, tfDescr);
			lb = new JLabel("EUR");
			contentPane.add(lb);
			layout.putConstraint(SpringLayout.WEST, lb, 5, SpringLayout.EAST, tfPrice);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfDescr);
			
			cbHU = new JCheckBox("HU");
			contentPane.add(cbHU);
			layout.putConstraint(SpringLayout.WEST, cbHU, 10, SpringLayout.EAST, tfKm);
			layout.putConstraint(SpringLayout.NORTH, cbHU, 5, SpringLayout.SOUTH, tfDate);
			
			cbSrv = new JCheckBox("Service");
			contentPane.add(cbSrv);
			layout.putConstraint(SpringLayout.WEST, cbSrv, 10, SpringLayout.EAST, cbHU);
			layout.putConstraint(SpringLayout.NORTH, cbSrv, 5, SpringLayout.SOUTH, tfDate);
			
			cbBr = new JCheckBox("Bremse");
			contentPane.add(cbBr);
			layout.putConstraint(SpringLayout.WEST, cbBr, 10, SpringLayout.EAST, cbSrv);
			layout.putConstraint(SpringLayout.NORTH, cbBr, 5, SpringLayout.SOUTH, tfDate);
			
			okBtn = new JButton("Ok");
			getContentPane().add(okBtn);
			layout.putConstraint(SpringLayout.WEST, okBtn, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, okBtn, -5, SpringLayout.SOUTH, contentPane);
			
			exitBtn = new JButton("Cancel");
			getContentPane().add(exitBtn);
			layout.putConstraint(SpringLayout.EAST, exitBtn, -5, SpringLayout.EAST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, exitBtn, -5, SpringLayout.SOUTH, contentPane);
			
			// init connections     
			exitBtn.addActionListener(this);
			okBtn.addActionListener(this);
		}
	}

	class NewInsuranceDlg extends NewXXXDlg implements java.awt.event.ActionListener
	{
		public NewInsuranceDlg(Frame f) {
			super(f, "Versicherungsbeitrag");
		}
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if (e.getSource() == okBtn)
			{
				try
				{
					curCar.insuranceList.add(curCar.new Insurance(dateFormat.parse(tfDate.getText()), 
							tfDescr.getText(), Float.parseFloat(tfPrice.getText())));
					insuranceTable.revalidate();
					updateOverviewPane();
					modified = true;
				}
				catch(Exception ex)
				{
					print_error(ex.getMessage());
					return;
				}
			}
			dispose();
		}
	}
	class NewTaxDlg extends NewXXXDlg implements java.awt.event.ActionListener
	{
		public NewTaxDlg(Frame f) {
			super(f, "Kfz-Steuer");
		}
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if (e.getSource() == okBtn)
			{
				try
				{
					curCar.taxList.add(curCar.new Tax(dateFormat.parse(tfDate.getText()), 
							tfDescr.getText(), Float.parseFloat(tfPrice.getText())));
					taxTable.revalidate();
					updateOverviewPane();
					modified = true;
				}
				catch(Exception ex)
				{
					print_error(ex.getMessage());
					return;
				}
			}
			dispose();
		}
	}
	class NewOtherDlg extends NewXXXDlg implements java.awt.event.ActionListener
	{
		public NewOtherDlg(Frame f) {
			super(f, "Andere Ausgaben");
		}
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if (e.getSource() == okBtn)
			{
				try
				{
					curCar.otherList.add(curCar.new OtherExpense(dateFormat.parse(tfDate.getText()), 
							tfDescr.getText(), Float.parseFloat(tfPrice.getText())));
					otherTable.revalidate();
					updateOverviewPane();
					modified = true;
				}
				catch(Exception ex)
				{
					print_error(ex.getMessage());
					return;
				}
			}
			dispose();
		}
	}

	class NewXXXDlg extends JDialog implements java.awt.event.ActionListener
	{
		JButton exitBtn = null, okBtn = null;
		JTextField tfDescr, tfPrice, tfDate;
		public void actionPerformed(ActionEvent e) {}	
		public NewXXXDlg(Frame f, String title)
		{
			super (f, title, true);
			
			SpringLayout layout = new SpringLayout();
			Container contentPane = getContentPane();
			contentPane.setLayout(layout);
			setSize(380, 160);
			
			JLabel lb = new JLabel("Datum"), lbDescr = new JLabel("Bezeichnung");
			contentPane.add(lb);
			tfDate = new JTextField(10);
			tfDate.setText(dateFormat.format(new Date()));
			contentPane.add(tfDate);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.NORTH, contentPane);
			layout.putConstraint(SpringLayout.WEST, tfDate, 5, SpringLayout.EAST, lbDescr);
			layout.putConstraint(SpringLayout.NORTH, tfDate, 5, SpringLayout.NORTH, contentPane);
			
			contentPane.add(lbDescr);
			tfDescr = new JTextField(20);
			contentPane.add(tfDescr);
			layout.putConstraint(SpringLayout.WEST, lbDescr, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.NORTH, lbDescr, 5, SpringLayout.SOUTH, tfDate);
			layout.putConstraint(SpringLayout.WEST, tfDescr, 0, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, tfDescr, 5, SpringLayout.SOUTH, tfDate);
			
			lb = new JLabel("Preis");
			contentPane.add(lb);
			tfPrice = new JTextField(5);
			contentPane.add(tfPrice);
			layout.putConstraint(SpringLayout.EAST, lb, -5, SpringLayout.WEST, tfDescr);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfDescr);
			layout.putConstraint(SpringLayout.WEST, tfPrice, 0, SpringLayout.WEST, tfDate);
			layout.putConstraint(SpringLayout.NORTH, tfPrice, 5, SpringLayout.SOUTH, tfDescr);
			lb = new JLabel("EUR");
			contentPane.add(lb);
			layout.putConstraint(SpringLayout.WEST, lb, 5, SpringLayout.EAST, tfPrice);
			layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, tfDescr);
			
			okBtn = new JButton("Ok");
			getContentPane().add(okBtn);
			layout.putConstraint(SpringLayout.WEST, okBtn, 5, SpringLayout.WEST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, okBtn, -5, SpringLayout.SOUTH, contentPane);
			
			exitBtn = new JButton("Cancel");
			getContentPane().add(exitBtn);
			layout.putConstraint(SpringLayout.EAST, exitBtn, -5, SpringLayout.EAST, contentPane);
			layout.putConstraint(SpringLayout.SOUTH, exitBtn, -5, SpringLayout.SOUTH, contentPane);
			
			// init connections     
			exitBtn.addActionListener(this);
			okBtn.addActionListener(this);
		}
	}
	static class DateRenderer extends DefaultTableCellRenderer {
		DateFormat formatter;
		public DateRenderer() { super(); }
		
		public void setValue(Object value) {
			if (formatter==null) {
				formatter = DateFormat.getDateInstance();
			}
			setText((value == null) ? "" : formatter.format(value));
		}
	}
	static class ThreeDigitRenderer extends DefaultTableCellRenderer {
		DecimalFormat f;
		public ThreeDigitRenderer() { super(); }
		public void setValue(Object value) {
			if (f==null) {
				f = new DecimalFormat("##0.000");
			}
			setText((value == null) ? "" : f.format(value));
			setHorizontalAlignment(RIGHT);
		}
	}

	static class TwoDigitRenderer extends DefaultTableCellRenderer {
		DecimalFormat f;
		public TwoDigitRenderer() { super(); }
		public void setValue(Object value) {
			if (f==null) {
				f = new DecimalFormat("##0.00");
			}
			setText((value == null) ? "" : f.format(value));
			setHorizontalAlignment(RIGHT);
		}
	}
	
	class FuelTableModel extends AbstractTableModel
	{
		private final String[] FUEL_TABLE_HEADER = {"Datum", "km Stand", "Menge (L)", "Kosten (EUR)", "Literpreis", "Verbrauch", "Distanz" };
		public String getColumnName(int c) { return FUEL_TABLE_HEADER[c]; }
		public int getColumnCount() { return FUEL_TABLE_HEADER.length; }
		public int getRowCount() { return CarCash.this.curCar.fuelList.size(); }
		public boolean isCellEditable(int row, int col) { return false; }
		public Object getValueAt(int row, int col) 
		{
			switch(col)
			{
			case 0:
				return ((Fuel) CarCash.this.curCar.fuelList.elementAt(row)).date;
			case 1:
				return new Integer(((Fuel) CarCash.this.curCar.fuelList.elementAt(row)).km);
			case 2:
				return _2digFormat.format(((Fuel) CarCash.this.curCar.fuelList.elementAt(row)).amount);
			case 3:
				return _2digFormat.format(((Fuel) curCar.fuelList.elementAt(row)).price);
			case 4:
				Fuel f= (Fuel) curCar.fuelList.elementAt(row);
				return new Float(f.price/f.amount);
			case 5:
				if(row>0){
					Fuel f1= (Fuel) curCar.fuelList.elementAt(row);
					Fuel f2= (Fuel) curCar.fuelList.elementAt(row-1);
					return new Float(f1.amount/(f1.km-f2.km)*100);
				}else return new Float(0);
			case 6:
			if(row>0){
				Fuel f1= (Fuel) curCar.fuelList.elementAt(row);
				Fuel f2= (Fuel) curCar.fuelList.elementAt(row-1);
				return new Integer(f1.km-f2.km);
			}else return new Integer(0);
			}
			return null;
		}
		public Class<? extends Object> getColumnClass(int c) { return getValueAt(0, c).getClass(); }
	}
	class TyreTableModel extends AbstractTableModel
	{
		private final String[] TYRE_TABLE_HEADER = {"Nr", "Datum", "Bezeichnung", "Preis (EUR)", "Laufleistung" };
		public String getColumnName(int c) { return TYRE_TABLE_HEADER[c]; }
		public int getColumnCount() { return TYRE_TABLE_HEADER.length; }
		public int getRowCount() { return curCar.tyreList.size(); }
		public boolean isCellEditable(int row, int col)
		{
			if (col==5) return true;
			else return false;
		}
		public Object getValueAt(int row, int col) 
		{
			switch(col)
			{
			case 0:
				return ((Tyres) curCar.tyreList.elementAt(row)).id +"";
			case 1:
				return ((Tyres) curCar.tyreList.elementAt(row)).date;
			case 2:
				return ((Tyres) curCar.tyreList.elementAt(row)).descr;
			case 3:
				return new Float(((Tyres) curCar.tyreList.elementAt(row)).price);
			case 4:
				return new Integer(((Tyres) curCar.tyreList.elementAt(row)).dist);
			}
			return null;
		}
		public Class<? extends Object> getColumnClass(int c) { return getValueAt(0, c).getClass(); }
	}
	class TyreChangeTableModel extends AbstractTableModel
	{
		private final String[] TYRECHANGE_TABLE_HEADER = {"Datum", "km Stand", "Reifensatz"};
		public String getColumnName(int c) { return TYRECHANGE_TABLE_HEADER[c]; }
		public int getColumnCount() { return TYRECHANGE_TABLE_HEADER.length; }
		public int getRowCount() { return curCar.tyreChangeList.size(); }
		public boolean isCellEditable(int row, int col) { return false; }
		public Object getValueAt(int row, int col) 
		{
			switch(col)
			{
			case 0:
				return ((TyreChange) curCar.tyreChangeList.elementAt(row)).date;
			case 1:
				return new Integer(((TyreChange) curCar.tyreChangeList.elementAt(row)).dist);
			case 2:
				int id = ((TyreChange) curCar.tyreChangeList.elementAt(row)).id;
				return "["+id+"] " +curCar.getTyresForId(id).descr;
			}
			return null;
		}
		public Class<?> getColumnClass(int c) { return getValueAt(0, c).getClass(); }
	}

	class GarageTableModel extends AbstractTableModel
	{
		private final String[] GARAGE_TABLE_HEADER = {"Datum", "km Stand", "durchgeführte Arbeiten", "HU/AU", "Service", "BS", "Kosten (EUR)" };
		public String getColumnName(int c) { return GARAGE_TABLE_HEADER[c]; }
		public int getColumnCount() { return GARAGE_TABLE_HEADER.length; }
		public int getRowCount() { return curCar.garageList.size(); }
		public boolean isCellEditable(int row, int col) { return false; }
		public Object getValueAt(int row, int col) 
		{
			switch(col)
			{
			case 0:
				return dateFormat.format(((Garage) curCar.garageList.elementAt(row)).date);
			case 1:
				return new Integer(((Garage) curCar.garageList.elementAt(row)).km);
			case 2:
				return ((Garage) curCar.garageList.elementAt(row)).descr;
			case 3:
				return new Boolean(((Garage) curCar.garageList.elementAt(row)).wasHU());
			case 4:
				return new Boolean(((Garage) curCar.garageList.elementAt(row)).wasService());
			case 5:
				return new Boolean(((Garage) curCar.garageList.elementAt(row)).wasBreakService());
			case 6:
				return new Float(((Garage) curCar.garageList.elementAt(row)).price);
			}
			return null;
		}
		public Class<?> getColumnClass(int c) { return getValueAt(0, c).getClass(); }
	}

	class InsuranceTableModel extends AbstractTableModel
	{
		private final String[] INSURANCE_TABLE_HEADER = {"Datum", "Art und Zeitraum", "Kosten (EUR)" };
		public String getColumnName(int c) { return INSURANCE_TABLE_HEADER[c]; }
		public int getColumnCount() { return INSURANCE_TABLE_HEADER.length; }
		public int getRowCount() { return curCar.insuranceList.size(); }
		public boolean isCellEditable(int row, int col)
		{
			if(col == 1)
				return true;
			else
				return false;
		}
		public Object getValueAt(int row, int col) 
		{
			switch(col)
			{
			case 0:
				return dateFormat.format(((Insurance) curCar.insuranceList.elementAt(row)).date);
			case 1:
				return ((Insurance) curCar.insuranceList.elementAt(row)).descr;
			case 2:
				return new Float(((Insurance) curCar.insuranceList.elementAt(row)).price);
			}
			return null;
		}
		public Class<?> getColumnClass(int c) { return getValueAt(0, c).getClass(); }
	}

	class TaxTableModel extends AbstractTableModel
	{
		private final String[] TAX_TABLE_HEADER = {"Datum", "Bezeichnung", "Betrag (EUR)" };
		public String getColumnName(int c) { return TAX_TABLE_HEADER[c]; }
		public int getColumnCount() { return TAX_TABLE_HEADER.length; }
		public int getRowCount() { return curCar.taxList.size(); }
		public boolean isCellEditable(int row, int col) { return false; }
		public Object getValueAt(int row, int col) 
		{
			switch(col)
			{
			case 0:
				return ((Tax) curCar.taxList.elementAt(row)).date;
			case 1:
				return ((Tax) curCar.taxList.elementAt(row)).descr;
			case 2:
				return new Float(((Tax) curCar.taxList.elementAt(row)).price);
			}
			return null;
		}
		public Class<?> getColumnClass(int c) { return getValueAt(0, c).getClass(); }
	}
	class OtherTableModel extends AbstractTableModel
	{
		private final String[] OTHER_TABLE_HEADER = {"Datum", "Bezeichnung", "Betrag (EUR)" };
		public String getColumnName(int c) { return OTHER_TABLE_HEADER[c]; }
		public int getColumnCount() { return OTHER_TABLE_HEADER.length; }
		public int getRowCount() { return curCar.otherList.size(); }
		public boolean isCellEditable(int row, int col) { return false; }
		public Object getValueAt(int row, int col) 
		{
			switch(col)
			{
			case 0:
				return ((OtherExpense) curCar.otherList.elementAt(row)).date;
			case 1:
				return ((OtherExpense) curCar.otherList.elementAt(row)).descr;
			case 2:
				return new Float(((OtherExpense) curCar.otherList.elementAt(row)).price);
			}
			return "";
		}
		public Class<?> getColumnClass(int c) { return getValueAt(0, c).getClass(); }
	}
	public CarCash()
	{
		super();
		print_log("main");
		
		try
		{
			BufferedReader lUsed = new BufferedReader(
								new FileReader(System.getProperty("user.home")+"/.CarCash_last_used"));
			lastUsed[0] = lUsed.readLine();
			lastUsed[1] = lUsed.readLine();
			lastUsed[2] = lUsed.readLine();
			lastUsed[3] = lUsed.readLine();
		}
		catch(Throwable t){print_log("load used: "+t +": "+t.getMessage());}
		try
		{
			setSize(680, 480);
			setTitle("CarCash");
			setContentPane(getContentsPane());
			initConnections();
		}
		catch(Throwable t)
		{
			print_error(t + ": " +t.getMessage());
		}
	}
	private JPanel getCarOverviewPane()
	{
		if (carOverviewPane == null)
		{
			try 
			{
				//String[] f = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
				//for(int i=0; i<f.length; i++) print_log(f[i]);
				print_log("create overview panel");
				carOverviewPane = new JPanel();
				carOverviewPane.setName("carOverviewPane");
				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints gbc = new GridBagConstraints();
				carOverviewPane.setLayout(gridbag);
				
				//setFont(new Font("Helvetica", Font.PLAIN, 14));
				//Font nf = new Font("SansSerif", Font.PLAIN, 12);
				Font bf = new Font("Lucida Serif", Font.BOLD, 13);
				//Font bf = new Font("Arial Black", Font.PLAIN, 13);
				
				gbc.fill = GridBagConstraints.BOTH;
				gbc.weightx = 1.0;
				gbc.gridwidth = 1;
				gbc.gridheight = 1;
				JLabel tmp = new JLabel("Hersteller");
				tmp.setHorizontalAlignment(JLabel.CENTER);
				//tmp.setFont(nf);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbVendor = new JLabel("-");
				lbVendor.setFont(bf);
				lbVendor.setHorizontalAlignment(JLabel.CENTER);
				gridbag.setConstraints(lbVendor, gbc); 
				carOverviewPane.add(lbVendor);
				
				tmp = new JLabel("Model");
				tmp.setHorizontalAlignment(JLabel.CENTER);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER; //end row
				lbModel = new JLabel("-");
				lbModel.setFont(bf);
				gridbag.setConstraints(lbModel, gbc); 
				carOverviewPane.add(lbModel);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("Erstzulassung ");
				tmp.setHorizontalAlignment(JLabel.CENTER);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbEZ = new JLabel("xx/XX");
				lbEZ.setFont(bf);
				lbEZ.setHorizontalAlignment(JLabel.CENTER);
				gridbag.setConstraints(lbEZ, gbc); 
				carOverviewPane.add(lbEZ);
				
				tmp = new JLabel("Nächste HU ");
				tmp.setHorizontalAlignment(JLabel.CENTER);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				lbHU = new JLabel("xx/XX");
				lbHU.setFont(bf);
				gridbag.setConstraints(lbHU, gbc); 
				carOverviewPane.add(lbHU);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("Kaufdatum");
				tmp.setHorizontalAlignment(JLabel.CENTER);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbBuy = new JLabel("xx/XX");
				lbBuy.setHorizontalAlignment(JLabel.CENTER);
				lbBuy.setFont(bf);
				gridbag.setConstraints(lbBuy, gbc); 
				carOverviewPane.add(lbBuy);
				
				tmp = new JLabel("Nächste AU");
				tmp.setHorizontalAlignment(JLabel.CENTER);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				lbAU = new JLabel("xx/XX");
				lbAU.setFont(bf);
				gridbag.setConstraints(lbAU, gbc); 
				carOverviewPane.add(lbAU);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("Kaufpreis");
				tmp.setHorizontalAlignment(JLabel.CENTER);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbPrice = new JLabel("-- EUR");
				lbPrice.setFont(bf);
				lbPrice.setHorizontalAlignment(JLabel.CENTER);
				gridbag.setConstraints(lbPrice, gbc); 
				carOverviewPane.add(lbPrice);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("Kilometerstand ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbDist = new JLabel("-- km");
				lbDist.setFont(bf);
				lbDist.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(lbDist, gbc); 
				carOverviewPane.add(lbDist);
				
				tmp = new JLabel("Durchschnittsverbrauch ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				lbVerbr = new JLabel("-- l/100km");
				gridbag.setConstraints(lbVerbr, gbc); 
				carOverviewPane.add(lbVerbr);
								
				gbc.gridwidth = 1;
				tmp = new JLabel("Jahresfahrleistung");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbDistYear = new JLabel("-- km");
				lbDistYear.setFont(bf);
				lbDistYear.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(lbDistYear, gbc); 
				carOverviewPane.add(lbDistYear);
				
				tmp = new JLabel("CO2-Emission ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				lbEmiss = new JLabel("-- g/km");
				gridbag.setConstraints(lbEmiss, gbc); 
				carOverviewPane.add(lbEmiss);
				
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 2;
				tmp = new JLabel("total");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("pro Jahr ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel(" pro Monat");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("Kosten insgesamt ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbCostAll = new JLabel("-- EUR");
				lbCostAll.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(lbCostAll, gbc); 
				carOverviewPane.add(lbCostAll);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("Kraftstoff ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbCostFuel = new JLabel("-- EUR");
				lbCostFuel.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(lbCostFuel, gbc); 
				carOverviewPane.add(lbCostFuel);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("Reifen ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbCostTyre = new JLabel("-- EUR");
				lbCostTyre.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(lbCostTyre, gbc); 
				carOverviewPane.add(lbCostTyre);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("Werkstatt ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbCostGarage = new JLabel("-- EUR");
				lbCostGarage.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(lbCostGarage, gbc); 
				carOverviewPane.add(lbCostGarage);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("Versicherung ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbCostIns = new JLabel("-- EUR");
				lbCostIns.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(lbCostIns, gbc); 
				carOverviewPane.add(lbCostIns);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("Steuern ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbCostTax = new JLabel("-- EUR");
				lbCostTax.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(lbCostTax, gbc); 
				carOverviewPane.add(lbCostTax);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("Andere ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbCostOther = new JLabel("-- EUR");
				lbCostOther.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(lbCostOther, gbc); 
				carOverviewPane.add(lbCostOther);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 1;
				gbc.gridx = 1;
				tmp = new JLabel("Kosten pro km");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel("Kosten pro Monat");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				tmp = new JLabel("Kosten pro Jahr");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridwidth = 1;
				tmp = new JLabel("  ohne Wertverlust ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbCostKM = new JLabel("-- Cent/km");
				gridbag.setConstraints(lbCostKM, gbc); 
				carOverviewPane.add(lbCostKM);
				
				gbc.gridx = GridBagConstraints.RELATIVE;
				lbCostMonth = new JLabel("-- EUR/Monat");
				gridbag.setConstraints(lbCostMonth, gbc); 
				carOverviewPane.add(lbCostMonth);

				gbc.gridwidth = GridBagConstraints.REMAINDER;
				lbCostYear = new JLabel("-- EUR/Jahr");
				gridbag.setConstraints(lbCostYear, gbc); 
				carOverviewPane.add(lbCostYear);
				
				gbc.gridwidth = 1;
				tmp = new JLabel(" Wertverlust ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbObsolescenceKM  = new JLabel("-- Cent/km");
				gridbag.setConstraints(lbObsolescenceKM, gbc); 
				carOverviewPane.add(lbObsolescenceKM);
				
				gbc.gridx = GridBagConstraints.RELATIVE;
				lbObsolescenceMonth = new JLabel("-- EUR/Monat");
				gridbag.setConstraints(lbObsolescenceMonth, gbc); 
				carOverviewPane.add(lbObsolescenceMonth);

				gbc.gridwidth = GridBagConstraints.REMAINDER;
				lbObsolescenceYear = new JLabel("-- EUR/Jahr");
				gridbag.setConstraints(lbObsolescenceYear, gbc); 
				carOverviewPane.add(lbObsolescenceYear);
				
				//gbc.gridheight = GridBagConstraints.REMAINDER;
				gbc.gridwidth = 1;
				tmp = new JLabel("  mit Wertverlust ");
				tmp.setHorizontalAlignment(JLabel.TRAILING);
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				lbCostKM2 = new JLabel("-- Cent/km");
				gridbag.setConstraints(lbCostKM2, gbc); 
				carOverviewPane.add(lbCostKM2);
				
				gbc.gridx = GridBagConstraints.RELATIVE;
				lbCostMonth2 = new JLabel("-- EUR/Monat");
				gridbag.setConstraints(lbCostMonth2, gbc); 
				carOverviewPane.add(lbCostMonth2);

				gbc.gridwidth = GridBagConstraints.REMAINDER;
				lbCostYear2 = new JLabel("-- EUR/Jahr");
				gridbag.setConstraints(lbCostYear2, gbc); 
				carOverviewPane.add(lbCostYear2);
		
					// just some space
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel("   ");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
								
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = 1;
				lbServiceTime = new JLabel("Nächste Durchsicht in 08/10 (7 Monaten)");
				gridbag.setConstraints(lbServiceTime, gbc); 
				carOverviewPane.add(lbServiceTime);
				
				gbc.gridheight = GridBagConstraints.REMAINDER;
				lbServiceDist = new JLabel("oder bei KM-Stand 0815 (noch 327 KM)");
				gridbag.setConstraints(lbServiceDist, gbc); 
				carOverviewPane.add(lbServiceDist);
				
				/*
				gbc.gridheight = 2;
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				tmp = new JLabel("Nächste events:");
				gridbag.setConstraints(tmp, gbc); 
				carOverviewPane.add(tmp);
				
				gbc.gridheight = GridBagConstraints.REMAINDER;
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = GridBagConstraints.RELATIVE;
				gbc.gridy = GridBagConstraints.RELATIVE;
				JTextArea tf = new JTextArea("Durchsicht\nHU/AU\nund anderes\nwas sonst noch anfällt");
				JScrollPane sp = new JScrollPane(tf);
				gridbag.setConstraints(sp, gbc); 
				carOverviewPane.add(sp);
*/
			}
			catch (java.lang.Throwable Exc) 
			{
				print_error("Ex: " +Exc);
			}
		}
		return carOverviewPane;
	}
	private void updateOverviewPane()
	{
		lbVendor.setText(curCar.sVendor);
		lbModel.setText(curCar.sModel);
		lbEZ.setText(curCar.getEZString());
		lbBuy.setText(curCar.getBuyDate());
		lbPrice.setText(curCar.price +" EUR");
		lbHU.setText(curCar.getNextHU());
		lbAU.setText(curCar.getNextAU());
		Calendar today = Calendar.getInstance();
		Calendar nextService = new GregorianCalendar();
		nextService.setTime(curCar.getNextServiceDate());
		Calendar timeToNextService = new GregorianCalendar();
		timeToNextService.setTime(new Date(nextService.getTimeInMillis() - today.getTimeInMillis()));
		print_log("time is " +longDateFormat.format(timeToNextService.getTime()));
		int serviceMonths = (timeToNextService.get(Calendar.YEAR)-1970)*12 + timeToNextService.get(Calendar.MONTH);
		int distToNextService = curCar.getNextServiceDist() - curCar.getMileAge();
			
		Fuel f;
		float litres = 0, money = 0, money_tyres = 0, money_fuel = 0, money_garage = 0;
		int km_start = -1, km_end = -1;
		Date first = new Date(9999999999990L), last = new Date(0);
		boolean firstEntry = true;
		for(Enumeration<Fuel> en = curCar.fuelList.elements(); en.hasMoreElements();)
		{
			f = (Fuel) en.nextElement();
			if(firstEntry)
			{
				km_start = f.km;
				km_end   = f.km;
				firstEntry = false;
			}
			else
			{  // skip the very first fuel amount
				litres+=f.amount;
			}
			money_fuel +=f.price;
			if(f.km < km_start) km_start = f.km;
			if(f.km > km_end) km_end = f.km;
			if(f.date.before(first)) first = f.date;
			if(f.date.after(last)) last = f.date;
		}
		money += money_fuel;
		for(Enumeration<Tyres> en = curCar.tyreList.elements(); en.hasMoreElements();)
		{
			Tyres t = (Tyres) en.nextElement();
			money_tyres += t.price;
		}
		money += money_tyres;
		for(Enumeration<Garage> en = curCar.garageList.elements(); en.hasMoreElements();)
		{
			Garage g = (Garage) en.nextElement();
			money_garage += g.price;
		}
		money += money_garage;
		float money_ins = 0;
		for(Enumeration<Insurance> en = curCar.insuranceList.elements(); en.hasMoreElements();)
		{
			Insurance i = (Insurance) en.nextElement();
			money_ins += i.price;
		}
		money += money_ins;
		float money_tax = 0;
		for(Enumeration<Tax> en = curCar.taxList.elements(); en.hasMoreElements();)
		{
			Tax t = (Tax) en.nextElement();
			money_tax += t.price;
		}
		money += money_tax;
		float money_other = 0;
		for(Enumeration<OtherExpense> en = curCar.otherList.elements(); en.hasMoreElements();)
		{
			OtherExpense o = (OtherExpense) en.nextElement();
			money_other += o.price;
		}
		money += money_other;
		print_log("km_start="+km_start+" km_end="+km_end+" liter="+litres);
		print_log("first="+first+" last="+last);
		lbDist.setText(km_end+" km");
		double diffY= (last.getTime() - first.getTime())/(1000*3600*24*365.0);
		long dist = km_end-km_start;
		print_log("diffY="+diffY);
		lbDistYear.setText((int)(dist/diffY)+" km");
		lbVerbr.setText(_2digFormat.format(litres/dist*100)+" l/100km");
		lbEmiss.setText(_2digFormat.format(litres/dist*2320)+" g/km");  // 1 l BENZIN entspr. 2320, TODO: Diesel -> 2620 g CO2
		lbCostKM.setText(_2digFormat.format(money/dist*100)+" Cent/km");
		lbCostYear.setText(_2digFormat.format(money/diffY)+" EUR/Jahr");
		lbCostMonth.setText(_2digFormat.format(money/diffY/12)+" EUR/Monat");
		//Calendar today = new GregorianCalendar();
		Calendar d_buy = new GregorianCalendar();
		d_buy.setTime(curCar.dBuy);
		int months = (today.get(Calendar.YEAR)*12+today.get(Calendar.MONTH)) - 
					 (d_buy.get(Calendar.YEAR)*12+d_buy.get(Calendar.MONTH));
		print_log("months="+months);
		long distAdj = dist;
		int monthsAdj = months;
		if(months < 96) // 8 years
		{
			monthsAdj = 96;
			distAdj = (long)(dist/diffY)*8;
			print_log("months adj="+monthsAdj);
			print_log("dist adj="+distAdj);
		}
		if(curCar.wasNew() && distAdj < km_end)
		{
			distAdj = km_end;
		}
		float carValue = curCar.price;
		if(curCar.mSellMileAge == 0)
		{
			if (serviceMonths < 0) lbServiceTime.setForeground(Color.RED);
			lbServiceTime.setText("Nächster Service am " +longDateFormat.format(curCar.getNextServiceDate()) 
								+" (noch " +serviceMonths +" Monate)");
			if (distToNextService < 0) lbServiceDist.setForeground(Color.RED);
			lbServiceDist.setText("oder bei KM-Stand " +curCar.getNextServiceDist() +" (noch " +distToNextService +" KM)");
		}
		else
		{
			lbServiceTime.setText("Fahrzeug verkauft am " +longDateFormat.format(curCar.mSellDate) 
		                        +" nach " +(curCar.mSellMileAge - km_start) +" KM.");
			carValue -= curCar.mSellPrice;
		}
		print_log("car value="+carValue);
		lbObsolescenceKM.setText(_2digFormat.format((carValue/distAdj)*100)+" Cent/km");
		lbObsolescenceMonth.setText(_2digFormat.format((carValue/monthsAdj))+" EUR/Monat");
		lbObsolescenceYear.setText(_2digFormat.format((carValue/monthsAdj)*12)+" EUR/Jahr");
		lbCostKM2.setText(_2digFormat.format((money/dist+(carValue/distAdj))*100)+" Cent/km");
		lbCostMonth2.setText(_2digFormat.format((money/diffY/12)+(carValue/monthsAdj)) +" EUR/Monat");
		lbCostYear2.setText(_2digFormat.format((money/diffY)+(carValue/monthsAdj*12)) +" EUR/Jahr");
		lbCostAll.setText(_2digFormat.format(money)+" EUR");
		lbCostFuel.setText(_2digFormat.format(money_fuel)+" EUR");
		lbCostTyre.setText(_2digFormat.format(money_tyres)+" EUR");
		lbCostGarage.setText(_2digFormat.format(money_garage)+" EUR");
		lbCostTax.setText(_2digFormat.format(money_tax)+" EUR");
		lbCostIns.setText(_2digFormat.format(money_ins)+" EUR");
		lbCostOther.setText(_2digFormat.format(money_other)+" EUR");

		getCarOverviewPane().revalidate();
	}
	private JScrollPane getFuelPane()
	{
		if(fuelPane == null)
		{
			fuelTable = new JTable(new FuelTableModel());
			fuelTable.setDefaultRenderer(Float.class, new ThreeDigitRenderer());
			fuelPane = new JScrollPane(fuelTable);
		}
		return fuelPane;
	}
	private JPanel getTyrePane()
	{
		if(tyrePane == null)
		{
			tyrePane = new JPanel();
			tyreTable = new JTable(new TyreTableModel());
			tyreTable.setDefaultRenderer(Float.class, new TwoDigitRenderer());
			tyreChangeTable = new JTable(new TyreChangeTableModel());
			//lbCurrentTyreSet = new JLabel("some tyres");
			JScrollPane sc = new JScrollPane(tyreTable);
			JScrollPane sc2 = new JScrollPane(tyreChangeTable);
			//JLabel lbH = new JLabel("Derzeit montierter Reifensatz: ");
			JLabel lb1 = new JLabel("Reifensätze");
			JLabel lb2 = new JLabel("Reifenwechsel");
			//lbH.setFont(new Font("Helvetica", Font.BOLD, 14));
			//lbCurrentTyreSet.setFont(new Font("Helvetica", Font.BOLD, 14));
			lb1.setFont(new Font("Helvetica", Font.PLAIN, 18));
			lb2.setFont(new Font("Helvetica", Font.PLAIN, 18));
			JPanel pnHead = new JPanel();
			//pnHead.add(lbH);
			//pnHead.add(lbCurrentTyreSet);
			tyrePane.add(pnHead);
			tyrePane.add(lb1);
			tyrePane.add(sc);
			tyrePane.add(lb2);
			tyrePane.add(sc2);
			BoxLayout layout = new BoxLayout(tyrePane, BoxLayout.Y_AXIS);
			tyrePane.setLayout(layout);
			/*
			 SpringLayout layout = new SpringLayout();
			 tyrePane.setLayout(layout);
			 layout.putConstraint(SpringLayout.WEST, lb1, 5, SpringLayout.WEST, tyrePane);
			 layout.putConstraint(SpringLayout.NORTH, lb1, 5, SpringLayout.NORTH, tyrePane);
			 layout.putConstraint(SpringLayout.WEST, sc, 5, SpringLayout.WEST, tyrePane);
			 layout.putConstraint(SpringLayout.NORTH, sc, 5, SpringLayout.SOUTH, lb1);
			 layout.putConstraint(SpringLayout.WEST, lb2, 5, SpringLayout.WEST, tyrePane);
			 layout.putConstraint(SpringLayout.NORTH, lb2, 5, SpringLayout.SOUTH, sc);
			 layout.putConstraint(SpringLayout.WEST, sc2, 5, SpringLayout.WEST, tyrePane);
			 layout.putConstraint(SpringLayout.NORTH, sc2, 5, SpringLayout.SOUTH, lb2);
			 */
			
			TableColumn column = null;
			for (int i = 0; i < 4; i++) {
				column = tyreTable.getColumnModel().getColumn(i);
				if (i == 0) {
					column.setPreferredWidth(30);
				}else if (i == 1) {
					column.setPreferredWidth(80);
				}else if (i == 2) {
					column.setPreferredWidth(280);
				} else {
					column.setPreferredWidth(60);
				}
			}
		}
		return tyrePane;
	}
	
	private JPanel getContentsPane()
	{
		if (contentsPane == null)
		{
			try 
			{
				print_log("create main panel");
				contentsPane = new JPanel();
				contentsPane.setName("ContentsPane");
				contentsPane.setLayout(new java.awt.BorderLayout());
				
				JMenu menuF = new JMenu("Datei");
				menuItem_close = new JMenuItem("Ende");
				menuItem_save  = new JMenuItem("Speichern");
				menuItem_load  = new JMenuItem("Laden...");
				menuItem_lastUsed1 = new JMenuItem(lastUsed[0]);
				menuItem_lastUsed2 = new JMenuItem(lastUsed[1]);
				menuItem_lastUsed3 = new JMenuItem(lastUsed[2]);
				menuItem_lastUsed4 = new JMenuItem(lastUsed[3]);
				menuItem_new   = new JMenuItem("Neues Fahrzeug...");
				menuItem_sold  = new JMenuItem("Fahrzeug verkauft ...");
				menuF.add(menuItem_new);
				menuF.add(menuItem_load);
				menuF.add(menuItem_save);
				menuF.add(menuItem_sold);
				menuF.add(new JSeparator());
				menuF.add(menuItem_lastUsed1);
				menuF.add(menuItem_lastUsed2);
				menuF.add(menuItem_lastUsed3);
				menuF.add(menuItem_lastUsed4);
				menuF.add(new JSeparator());
				menuF.add(menuItem_close);
				
				JMenu menuE =new JMenu("Eintragen");
				menuItem_fuel = new JMenuItem("Tanken...");
				menuItem_oil = new JMenuItem("Öl nachfüllen...");
				menuItem_newWheel = new JMenuItem("Reifen kaufen...");
				menuItem_cngWheel = new JMenuItem("Reifen wechseln...");
				menuItem_garage = new JMenuItem("Werkstatt...");
				menuItem_insurance = new JMenuItem("Versicherungsbeitrag...");
				menuItem_tax  = new JMenuItem("Steuern...");
				menuItem_other = new JMenuItem("Andere Ausgaben...");
				menuE.add(menuItem_fuel);
				//menuE.add(menuItem_oil);
				menuE.add(menuItem_newWheel);
				menuE.add(menuItem_cngWheel);
				menuE.add(menuItem_garage);
				menuE.add(menuItem_insurance);
				menuE.add(menuItem_tax);
				menuE.add(menuItem_other);
				
				JMenu menuA =new JMenu("Auswerten");
				menuItem_fuelcons  = new JMenuItem("Verbrauch");
				menuItem_fuelprice = new JMenuItem("Benzinpreis");
				menuItem_distYear  = new JMenuItem("Fahrstrecke pro Jahr");
				menuItem_distMonth = new JMenuItem("Fahrstrecke pro Monat");
				JMenu menuStat = new JMenu("Kostenstatistik");
				menuItem_statYear  = new JMenuItem("jährlich");
				menuItem_statYear2 = new JMenuItem("jährlich (test)");
				menuItem_statMonth = new JMenuItem("monatlich");
				menuStat.add(menuItem_statYear);
				menuStat.add(menuItem_statYear2);
				menuStat.add(menuItem_statMonth);
				
				menuA.add(menuItem_fuelcons);
				menuA.add(menuItem_fuelprice);
				menuA.add(menuItem_distYear);
				menuA.add(menuItem_distMonth);
				menuA.add(menuStat);
				
				JMenu menuHelp =new JMenu("Hilfe");
				menuItem_about = new JMenuItem("Über CarCash");
				menuHelp.add("... gibts keine :-(");
				menuHelp.add(menuItem_about);
				
				JMenuBar bar = new JMenuBar();   
				bar.add(menuF);
				bar.add(menuE);
				bar.add(menuA);
				bar.add(Box.createHorizontalGlue());
				bar.add(menuHelp);
				contentsPane.add(bar, "North");
				
				JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP);
				tabPane.add("Überblick", getCarOverviewPane());
				tabPane.add("Benzin", getFuelPane());
				//tabPane.add("Öl", new JPanel());
				tabPane.add("Reifen", getTyrePane());
				
				garageTable = new JTable(new GarageTableModel());
				garageTable.setDefaultRenderer(Float.class, new TwoDigitRenderer());
				tabPane.add("Werkstatt", new JScrollPane(garageTable));
				
				insuranceTable = new InsuranceTable(new InsuranceTableModel());
				insuranceTable.setDefaultRenderer(Float.class, new TwoDigitRenderer());
				tabPane.add("Versicherung", new JScrollPane(insuranceTable));
				
				taxTable = new JTable(new TaxTableModel());
				taxTable.setDefaultRenderer(Float.class, new TwoDigitRenderer());
				tabPane.add("Steuern", new JScrollPane(taxTable));
				
				otherTable = new JTable(new OtherTableModel());
				tabPane.add("andere Ausgaben", new JScrollPane(otherTable));
				contentsPane.add(tabPane, "Center");
				
				lbStatus = new JLabel("initializing ...");
				contentsPane.add(lbStatus, "South");
				
				TableColumn column = null;
				for (int i = 0; i < 7; i++) {
					column = garageTable.getColumnModel().getColumn(i);
					if (i < 2) {
						column.setPreferredWidth(45);
					} else if (i == 2) {
						column.setPreferredWidth(280);
					} else if ( (i > 2) && (i < 6) )  {
						column.setPreferredWidth(30);
					} else {
						column.setPreferredWidth(65);
					}
				}
				for (int i = 0; i < 2; i++) {
					column = insuranceTable.getColumnModel().getColumn(i);
					if (i == 2) {
						column.setPreferredWidth(330);
					} else {
						column.setPreferredWidth(60);
					}
				}
				for (int i = 0; i < 2; i++) {
					column = otherTable.getColumnModel().getColumn(i);
					if (i == 2) {
						column.setPreferredWidth(330);
					} else {
						column.setPreferredWidth(60);
					}
				}
			} 
			catch (java.lang.Throwable Exc) 
			{
				print_error(Exc +":" +Exc.getMessage());
			}
		}
		
		return contentsPane;
	}
	private void initConnections() throws Exception
	{
		print_log("init connections");
		try
		{
			this.addWindowListener(this);
			this.addMouseListener(this);
			menuItem_close.addActionListener(this);
			menuItem_save.addActionListener(this);
			menuItem_load.addActionListener(this);
			menuItem_new.addActionListener(this);
			menuItem_sold.addActionListener(this);
			menuItem_fuel.addActionListener(this);
			menuItem_oil.addActionListener(this);
			menuItem_newWheel.addActionListener(this);
			menuItem_cngWheel.addActionListener(this);
			menuItem_garage.addActionListener(this);
			menuItem_insurance.addActionListener(this);
			menuItem_tax.addActionListener(this);
			menuItem_other.addActionListener(this);
			menuItem_fuelcons.addActionListener(this);
			menuItem_fuelprice.addActionListener(this);
			menuItem_distYear.addActionListener(this);
			menuItem_distMonth.addActionListener(this);
			menuItem_statYear.addActionListener(this);
			menuItem_statYear2.addActionListener(this);
			menuItem_statMonth.addActionListener(this);
			menuItem_lastUsed1.addActionListener(this);
			menuItem_lastUsed2.addActionListener(this);
			menuItem_lastUsed3.addActionListener(this);
			menuItem_lastUsed4.addActionListener(this);
			menuItem_about.addActionListener(this);
			disableMenus();
		} 
		catch (java.lang.Throwable Exc) 
		{
			print_error("failed to init connections\n  Ex: " +Exc);
		}
	}
	private void disableMenus()
	{
		menuItem_save.setEnabled(false);
		menuItem_sold.setEnabled(false);
		menuItem_fuel.setEnabled(false);
		menuItem_newWheel.setEnabled(false);
		menuItem_cngWheel.setEnabled(false);
		menuItem_oil.setEnabled(false);
		menuItem_garage.setEnabled(false);
		menuItem_insurance.setEnabled(false);
		menuItem_tax.setEnabled(false);
		menuItem_other.setEnabled(false);
		menuItem_fuelcons.setEnabled(false);
		menuItem_fuelprice.setEnabled(false);
		menuItem_distYear.setEnabled(false);
		menuItem_distMonth.setEnabled(false);
		menuItem_statYear.setEnabled(false);
		menuItem_statMonth.setEnabled(false);
	}
	private void enableMenus()
	{
		menuItem_save.setEnabled(true);
		menuItem_sold.setEnabled(true);
		menuItem_fuel.setEnabled(true);
		menuItem_newWheel.setEnabled(true);
		menuItem_cngWheel.setEnabled(true);
		menuItem_oil.setEnabled(true);
		menuItem_garage.setEnabled(true);
		menuItem_insurance.setEnabled(true);
		menuItem_tax.setEnabled(true);
		menuItem_other.setEnabled(true);
		menuItem_fuelcons.setEnabled(true);
		menuItem_fuelprice.setEnabled(true);
		menuItem_distYear.setEnabled(true);
		menuItem_distMonth.setEnabled(true);
		menuItem_statYear.setEnabled(true);
		menuItem_statMonth.setEnabled(true);
	}
	private static void print_log(String s)
	{
		System.out.println(s);
	}
	private void print_error(String s)
	{
		System.out.println(s);
		JOptionPane.showMessageDialog(this, s, "Error", JOptionPane.ERROR_MESSAGE);
	}
	private void print_error(Exception ex)
	{
		String s = "caught exception "+ex.getClass().getSimpleName()+"\n"+ex.getMessage();
		System.out.println(s);
		JOptionPane.showMessageDialog(this, s, "Error", JOptionPane.ERROR_MESSAGE);
	}
	private void updateLastUsed(String fName)
	{
		print_log("upd last used");
		boolean found = false;
		if(lastUsed[3] == fName)
		{
			print_log("found at 3");
			lastUsed[3]=lastUsed[2];
			found = true;
		}
		if(lastUsed[2] == fName) found = true;
		if(found) lastUsed[2]=lastUsed[1];
		if(lastUsed[1] == fName) found = true;
		if(found) lastUsed[1]=lastUsed[0];
		if(lastUsed[0] == fName) found = true;
		if(!found)
		{
			print_log("not found");	
			lastUsed[3]=lastUsed[2];
			lastUsed[2]=lastUsed[1];
			lastUsed[1]=lastUsed[0];
		}
		lastUsed[0]=fName;
		
		menuItem_lastUsed1.setText(lastUsed[0]);
		menuItem_lastUsed2.setText(lastUsed[1]);
		menuItem_lastUsed3.setText(lastUsed[2]);
		menuItem_lastUsed4.setText(lastUsed[3]);
	}
	private void load(String fName)
	{
		print_log("loading file "+fName +"...");
		try
		{
			FileInputStream istream = new FileInputStream(fName);
			InputStreamReader isr = new InputStreamReader(istream);
			BufferedReader br = new BufferedReader(isr);
			curCar = new Car();	
			curCar.read(br);
			istream.close();
			
			fileName = fName;
			
			updateOverviewPane();
			fuelTable.revalidate();
			tyreTable.revalidate();
			tyreChangeTable.revalidate();
			garageTable.revalidate();
			insuranceTable.revalidate();
			taxTable.revalidate();
			garageTable.revalidate();
			setTitle("CarCash - " +curCar.sVendor +" - " +curCar.sModel);
			lbStatus.setText("file "+fName +" loaded.");
			enableMenus();
			updateLastUsed(fName);
		}
		catch(Throwable eofe)
		{
			print_error(eofe +": " +eofe.getMessage());
			JOptionPane.showMessageDialog(this, "Kann Datei "+fName +" nich laden: "+eofe.getMessage(),
							"Fehler", JOptionPane.WARNING_MESSAGE);
		}
	}
	private void save(String fName)
	{
		print_log("saving to file "+fName +"...");
		try
		{
			fileName = fName;
			FileOutputStream ostream = new FileOutputStream(fName);
			OutputStreamWriter osr = new OutputStreamWriter(ostream);
			PrintWriter pw = new PrintWriter(osr);
			curCar.write(pw);
			pw.close();
			modified = false;
			lbStatus.setText("file "+fileName +" saved.");
		}
		catch(IOException ioe)
		{
			print_error(ioe + ": " +ioe.getMessage());
		}
	}
	public static void main(String[] args)
	{
		try
		{
			CarCash cc = new CarCash();
			cc.setVisible(true);
		}
		catch (Throwable exception)
		{
			System.out.println("Exeption in main():" +exception.getMessage());
			exception.printStackTrace(System.out);
		}
	}
}
