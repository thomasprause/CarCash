/*
 * Created on Oct 12, 2006
 *
 */
package thp.carcash;

import java.io.*;
import java.util.*;
import java.text.*;

/**
 * @author prause
 *
 * 
 */

public class Car {
	
	class CarEntry implements Comparable<CarEntry>
	{
		Date date;
		
		public int compareTo(CarEntry ce)
		{
			 if(date.getTime() == ce.date.getTime()) return 0;
			 if(date.getTime() > ce.date.getTime())
				 return 1;
			 else
				 return -1;
		}
	}
	class Fuel extends CarEntry
	{
		int km;
		float amount;
		float price;
		
		public final static String SEPARATOR = "#fuel";

		public Fuel(Date d, int k, float am, float pr)
		{
			date = d;
			km = k;
			amount = am;
			price = pr;
		}
		public void print(PrintWriter pw)
		{
			pw.println(SEPARATOR);
			pw.println(sdf.format(date));
			pw.println(km);
			pw.println(amount);
			pw.println(price);
		}
	}
	class Tyres extends CarEntry
	{
		int id;
		String descr;
		float price;
		int dist;  // transient
		public final static String SEPARATOR = "#tyres";
		
		public Tyres(int i, Date d, String des, float pr)
		{
			date = d;
			descr = des;
			price = pr;
			id = i;
			dist = 0;  // transient
		}
		public Tyres(Date d, String des, float pr)
		{
			date = d;
			descr = des;
			price = pr;
			id = 0;
			dist = 0;  // transient
		}
		
		public String toString()
		{
			return descr;
		}
		
		public void print(PrintWriter pw)
		{
			pw.println(SEPARATOR);
			pw.println(id);
			pw.println(sdf.format(date));
			pw.println(descr);
			pw.println(price);
		}
	}
	class TyreChange extends CarEntry
	{
		int oldId;
		int id;
		int dist;
		public final static String SEPARATOR = "#tyrechange";

		public TyreChange(int old,int i, Date d, int di)
		{
			oldId = old;
			id = i;
			date = d;
			dist = di;
		}
		public void print(PrintWriter pw)
		{
			pw.println(SEPARATOR);
			pw.println(oldId);
			pw.println(id);
			pw.println(sdf.format(date));
			pw.println(dist);
		}
	}
	class Garage extends CarEntry
	{
		int km;
		String descr;
		float price;
		boolean HU, service, breakService;
		public final static String SEPARATOR = "#garage";

		public Garage(Date d, int k, String des, boolean hu, boolean srvc, boolean brServ, float pr)
		{
			date = d;
			km = k;
			descr = des;
			price = pr;
			HU = hu;
			service = srvc;
			breakService = brServ;
		}
		public void print(PrintWriter pw)
		{
			pw.println(SEPARATOR);
			pw.println(sdf.format(date));
			pw.println(km);
			pw.println(descr);
			pw.println(HU);
			pw.println(service);
			pw.println(breakService);
			pw.println(price);
		}
		boolean wasHU()
		{ return HU; }
		boolean wasService()
		{ return service; }
		boolean wasBreakService()
		{ return breakService; }
	}
	class Insurance extends CarEntry
	{
		String descr;
		float price;
		public final static String SEPARATOR = "#insurance";

		public Insurance(Date d, String des, float p)
		{
			date = d;
			descr= des;
			price = p;
		}
		public void print(PrintWriter pw)
		{
			pw.println(SEPARATOR);
			pw.println(sdf.format(date));
			pw.println(descr);
			pw.println(price);
		}
	}
	class Tax extends CarEntry
	{
		String descr;
		float price;
		public final static String SEPARATOR = "#tax";

		public Tax(Date d, String des, float p)
		{
			date = d;
			descr= des;
			price = p;
		}
		public void print(PrintWriter pw)
		{
			pw.println(SEPARATOR);
			pw.println(sdf.format(date));
			pw.println(descr);
			pw.println(price);
		}
	}
	class OtherExpense extends CarEntry
	{
		String descr;
		float price;
		public final static String SEPARATOR = "#other";

		public OtherExpense(Date d, String des, float p)
		{
			date = d;
			descr= des;
			price = p;
		}
		public void print(PrintWriter pw)
		{
			pw.println(SEPARATOR);
			pw.println(sdf.format(date));
			pw.println(descr);
			pw.println(price);
		}
	}
	
	String sVendor, sModel;
	float price;
	float mSellPrice;
	int mSellMileAge = 0;
	boolean mWasNew;
	Date dBuy, dEZ, mSellDate, dAU, dHU;
	int mServiceDist; //service alle xx km
	int mServiceTime; // service alle xx Monate
	Vector<Fuel> fuelList;
	Vector<TyreChange> tyreChangeList;
	Vector<Tyres> tyreList;
	Vector<Garage> garageList;
	Vector<Insurance> insuranceList;
	Vector<Tax> taxList;
	Vector<OtherExpense> otherList;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat MMYYformat = new SimpleDateFormat("MM/yy");
	final static String CAR_IDENTIFIER_V2 = "car:v2";
	final static String CAR_IDENTIFIER_V3 = "car:v3";
	final static String CAR_IDENTIFIER = "car:v4";
	final static String FINAL_ENTRY = "#EOF";
	
	Car()
	{
		fuelList = new Vector<Fuel>();
		tyreList = new Vector<Tyres>();
		tyreChangeList = new Vector<TyreChange>();
		garageList = new Vector<Garage>();
		insuranceList = new Vector<Insurance>();
		taxList = new Vector<Tax>();
		otherList = new Vector<OtherExpense>();
		dBuy = new Date(5);
	}
	Car(String vend, String mod)
	{
		sVendor = vend;
		sModel = mod;
		fuelList = new Vector<Fuel>();
		tyreList = new Vector<Tyres>();
		tyreChangeList = new Vector<TyreChange>();
		garageList = new Vector<Garage>();
		insuranceList = new Vector<Insurance>();
		taxList = new Vector<Tax>();
		otherList = new Vector<OtherExpense>();
		dBuy = new Date(5);
	}
	void setPrice(float p) { price = p; }
	void setEZ(Date d) { dEZ = d; }
	void setHU(Date d) { dHU = d; }
	void setAU(Date d) { dAU = d; }
	String getEZString() { return MMYYformat.format(dEZ);}
	Date getEZ() { return dEZ;}
	String getBuyDate() { return MMYYformat.format(dBuy);}
	void setServiceDist(int dist) { mServiceDist = dist; }
	void setServiceTime(int time) { mServiceTime = time; }
	int getNextServiceDist()
	{
		int lastService = 0;
		for(Enumeration<Garage> en = garageList.elements(); en.hasMoreElements();)
		{
			Garage g = (Garage) en.nextElement();
			if(g.wasService()) lastService = g.km;
		}
		return lastService + mServiceDist;
	}
	Date getNextServiceDate()
	{
		GregorianCalendar lastService = new GregorianCalendar();
		lastService.setTime(getLastServiceDate());
		lastService.add(Calendar.MONTH, mServiceTime);
		return lastService.getTime(); // is now next service ;-)
	}
	Date getLastServiceDate()
	{
		GregorianCalendar lastService = new GregorianCalendar();
		lastService.setTime(new Date(0));
		for(Enumeration<Garage> en = garageList.elements(); en.hasMoreElements();)
		{
			Garage g = (Garage) en.nextElement();
			if(g.wasService()) lastService.setTime(g.date);
		}
		return lastService.getTime();
	}
	int getMileAge()
	{
		int mileAge = 0;
		for(Enumeration<Fuel> en = fuelList.elements(); en.hasMoreElements();)
		{
			Fuel f = en.nextElement();
			if(f.km > mileAge) mileAge = f.km;
		}
		return mileAge;
	}
	String getNextHU()
	{
		print_log("getNextHU");
		Date lastHU = null;
		for(Enumeration<Garage> en = garageList.elements(); en.hasMoreElements();)
		{
			Garage g = (Garage) en.nextElement();
			if(g.wasHU()) lastHU = g.date;
		}
		if(null == lastHU) lastHU = dHU;
		Calendar cal = new GregorianCalendar();
		cal.setTime(lastHU);
		cal.add(Calendar.YEAR, 2);
		return MMYYformat.format(cal.getTime());
	}
	String getNextAU()
	{
		print_log("get next AU");
		Date lastAU = null;
		for(Enumeration<Garage> en = garageList.elements(); en.hasMoreElements();)
		{
			Garage g = (Garage) en.nextElement();
			if(g.wasHU()) lastAU = g.date;
		}
		if(null == lastAU) lastAU = dAU;
		Calendar cal = new GregorianCalendar();
		cal.setTime(lastAU);
		cal.add(Calendar.YEAR, 2);
		return MMYYformat.format(cal.getTime());
	}
	boolean wasNew() { return mWasNew; }
	
	void addTyres(Tyres newT)
	{
		print_log("add tyres: " +newT);
		int curId = 0;
		for(Enumeration<Tyres> en = tyreList.elements(); en.hasMoreElements();)
		{
			Tyres t = (Tyres) en.nextElement();
			if(t.id > curId) curId = t.id;
		}
		newT.id = curId + 1;
		tyreList.add(newT);
	}
	void addDistForId(int id, int dist)
	{
		print_log("addDist:" +dist +" km for id="+id);
		for(Enumeration<Tyres> en = tyreList.elements(); en.hasMoreElements();)
		{
			Tyres t = (Tyres) en.nextElement();
			if(t.id == id) 
			{
				t.dist+=dist;
				print_log("dist is now "+t.dist);
			}
		}
	}
	TyreChange getLastTyreChange()
	{
		print_log("getLastChange:");
		if(tyreChangeList.isEmpty()) return null;
		TyreChange lastTc = (TyreChange) tyreChangeList.elementAt(0);
		for(Enumeration<TyreChange> en = tyreChangeList.elements(); en.hasMoreElements();)
		{
			TyreChange tc = (TyreChange) en.nextElement();
			if(tc.dist > lastTc.dist) lastTc = tc;
		}
		return lastTc;
	}
	TyreChange getLastTyreChange(int id)
	{
		print_log("getLastChange: "+id);
		if(tyreChangeList.isEmpty()) return null;
		TyreChange lastTc = null;
		for(Enumeration<TyreChange> en = tyreChangeList.elements(); en.hasMoreElements();)
		{
			TyreChange tc = (TyreChange) en.nextElement();
			if(tc.id == id)
			{
				if(null == lastTc) lastTc = tc;
				if(tc.dist > lastTc.dist) lastTc = tc;
			}
		}
		return lastTc;
	}
	Tyres getTyresForId(int id)
	{
		for(Enumeration<Tyres> en = tyreList.elements(); en.hasMoreElements();)
		{
			Tyres t = (Tyres) en.nextElement();
			if(t.id == id) return t;
		}
		print_log("tyres w/ id " +id +" not found");
		return null;
	}
	void read(BufferedReader br) throws Exception
	{
		int cnt = 0, cntG = 0, cntT = 0, cntC = 0, cntI = 0, cntTa = 0;
		try
		{
			String fileVer = br.readLine();
			if(!CAR_IDENTIFIER.equals(fileVer)
				&& !(CAR_IDENTIFIER_V3.equals(fileVer))
				&& !(CAR_IDENTIFIER_V2.equals(fileVer))) throw new IOException("wrong file version identifier: '" +fileVer +"'");
			print_log("read file header: " +fileVer);
			
			sVendor = br.readLine();
			sModel = br.readLine();
			dEZ = MMYYformat.parse(br.readLine());
			dBuy = MMYYformat.parse(br.readLine());
			price = Float.parseFloat(br.readLine());
			String was_new = br.readLine();
			if(was_new.equals("new"))
			{
				print_log("new car");
				mWasNew = true;
			}
			else
			{
				print_log("old car");
				mWasNew = false;
			}
			dHU = MMYYformat.parse(br.readLine());
			dAU = MMYYformat.parse(br.readLine());
			
			if(CAR_IDENTIFIER_V3.equals(fileVer)
			 || CAR_IDENTIFIER.equals(fileVer))
			{
				mServiceDist = Integer.parseInt(br.readLine());
				mServiceTime = Integer.parseInt(br.readLine());
			}
			if(CAR_IDENTIFIER.equals(fileVer))
			{
				mSellDate = MMYYformat.parse(br.readLine());
				mSellMileAge = Integer.parseInt(br.readLine());
				mSellPrice = Float.parseFloat(br.readLine());
			}
			
			String id = br.readLine();
			while(true)
			{
				//print_log("first ID=" +id);
				if(id.startsWith(Tyres.SEPARATOR))
				{
					tyreList.add(new Tyres(Integer.parseInt(br.readLine()), sdf.parse(br.readLine()), 
							br.readLine(), Float.parseFloat(br.readLine())));
					cntT++;
				}
				if(id.startsWith(TyreChange.SEPARATOR))
				{
					TyreChange tc = new TyreChange(Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()),
							sdf.parse(br.readLine()), Integer.parseInt(br.readLine()) );
					tyreChangeList.add(tc);
					TyreChange last = getLastTyreChange(tc.oldId);
					if(null != last) addDistForId(tc.oldId, tc.dist-last.dist);
					cntC++;
				}
				if(id.startsWith(Fuel.SEPARATOR))
				{
					fuelList.add(new Fuel(sdf.parse(br.readLine()), Integer.parseInt(br.readLine()), 
							Float.parseFloat(br.readLine()), Float.parseFloat(br.readLine())));
					cnt++;
				}
				if(id.startsWith(Garage.SEPARATOR))
				{
					if(fileVer.equals(CAR_IDENTIFIER_V2))
					{
						garageList.add(new Garage(sdf.parse(br.readLine()), Integer.parseInt(br.readLine()), 
							br.readLine(), false, false, false, Float.parseFloat(br.readLine())));
					}
					else {
						garageList.add(new Garage(sdf.parse(br.readLine()), Integer.parseInt(br.readLine()), 
							br.readLine(), Boolean.parseBoolean(br.readLine()), Boolean.parseBoolean(br.readLine()),
							Boolean.parseBoolean(br.readLine()), Float.parseFloat(br.readLine())));
					}
					cntG++;
				}
				if(id.startsWith(Insurance.SEPARATOR))
				{
					insuranceList.add(new Insurance(sdf.parse(br.readLine()), br.readLine(), Float.parseFloat(br.readLine())));
					cntI++;
				}
				if(id.startsWith(Tax.SEPARATOR))
				{
					taxList.add(new Tax(sdf.parse(br.readLine()), br.readLine(), Float.parseFloat(br.readLine())));
					cntTa++;
				}
				if(id.startsWith(FINAL_ENTRY))
				{
					print_log("read last entry");
					break;
				}
				id = br.readLine();
			}
		}catch(Exception e)
		{
			print_log(e +": " +e.getMessage());
			throw new Exception("error while parsing file: " +e.getMessage());
		}
		print_log("read " +cnt +" fuel entries");
		print_log("read " +cntG +" garage entries");
		print_log("read " +cntT +" tyre entries");
		print_log("read " +cntC +" tyre change entries");
		print_log("read " +cntI +" ins. entries");
		print_log("read " +cntTa +" tax entries");
		
		Collections.sort(fuelList);
		Collections.sort(tyreList);
		Collections.sort(tyreChangeList);
		Collections.sort(garageList);
		Collections.sort(insuranceList);
		
		TyreChange lastTC = getLastTyreChange();
		Fuel lastFuel = fuelList.lastElement();
		if(null != lastTC) addDistForId(lastTC.id, lastFuel.km - lastTC.dist);
	}
	
	void write(PrintWriter pw) throws java.io.IOException
	{
		pw.println(CAR_IDENTIFIER);
		pw.println(sVendor);
		pw.println(sModel);
		pw.println(MMYYformat.format(dEZ));
		pw.println(MMYYformat.format(dBuy));
		pw.println(price);
		if(mWasNew)
		{
			pw.println("new");
		}
		else
		{
			pw.println("old");
		}
		pw.println(MMYYformat.format(dHU));
		pw.println(MMYYformat.format(dAU));
		// added in V3
		pw.println(mServiceDist);
		pw.println(mServiceTime);
		// added in V4
		pw.println(MMYYformat.format(mSellDate));
		pw.println(mSellMileAge);
		pw.println(mSellPrice);

		for(Enumeration<Tyres> en = tyreList.elements(); en.hasMoreElements();)
		{
			((Tyres) en.nextElement()).print(pw);
		}
		for(Enumeration<TyreChange> en = tyreChangeList.elements(); en.hasMoreElements();)
		{
			((TyreChange) en.nextElement()).print(pw);
		}
		for(Enumeration<Fuel> en = fuelList.elements(); en.hasMoreElements();)
		{
			((Fuel) en.nextElement()).print(pw);
		}
		for(Enumeration<Garage> en = garageList.elements(); en.hasMoreElements();)
		{
			((Garage) en.nextElement()).print(pw);
		}
		for(Enumeration<Insurance> en = insuranceList.elements(); en.hasMoreElements();)
		{
			( (Insurance) en.nextElement()).print(pw);			
		}
		for(Enumeration<Tax> en = taxList.elements(); en.hasMoreElements();)
		{
			( (Tax) en.nextElement()).print(pw);			
		}
		pw.println(FINAL_ENTRY); // mark the end of file
	}
	void print_log(String s)
	{
		System.out.println(s);
	}
	public void sell(Date sold, int km, int price)
	{
		mSellDate = sold;
		mSellPrice = price;
		mSellMileAge = km;
	}
}
