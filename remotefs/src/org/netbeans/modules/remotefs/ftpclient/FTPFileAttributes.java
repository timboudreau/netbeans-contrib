/*                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version 
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is RemoteFS. The Initial Developer of the Original
 * Code is Libor Martinek. Portions created by Libor Martinek are 
 * Copyright (C) 2000. All Rights Reserved.
 * 
 * Contributor(s): Libor Martinek. 
 */

package org.netbeans.modules.remotefs.ftpclient;

import java.util.*;
import org.netbeans.modules.remotefs.core.RemoteFileAttributes;


/** Class for storing file attributes.
 *
 * @author  Libor Martinek
 * @version 1.0
 */ 
public class FTPFileAttributes extends RemoteFileAttributes  {

    private String rights = null;
    private int links = 0;
    private String user = null;
    private String group = null;
    
    /** Create new FTPFileAttributes */
    public FTPFileAttributes(String name,boolean isdirectory,String rights,int links,
                            String user,String group,long size,java.util.Date date) {
        super(name, isdirectory, size, date);
        this.rights=rights;
        this.links=links;
        this.user=user;
        this.group=group;
    }
    
    /** Create new FTPFileAttributes */
    public FTPFileAttributes(String name,boolean isdirectory,long size,java.util.Date date) {
        super(name,isdirectory,size,date);
    }
   
    /** Create empty FTPFileAttributes */
    public FTPFileAttributes() {
       super();
    }
    
    /** Create FTPFileAttributes only with name and isdirectory flag */
    public FTPFileAttributes(String name, boolean isdirectory) {
    	super(name,isdirectory);
    }	
    
    /** Set rights string */
    public void setRights(String rights) {  this.rights=rights; }
    /** Set number of links */
    public void setLinks(int links) { this.links=links;}
    /** Set user name */
    public void setUser(String user ) {  this.user=user;}
    /** Set group name */
    public void setGroup(String group) { this.group=group;}
    
    /** Get rights string */
    public String getRights() { return rights; }
    /** Get number of links */
    public int getLinks() { return links; }
    /** Get user name */
    public String getUser() { return user; }
    /** Get group name */
    public String getGroup() { return group; }
    
    //*****************************************************************
    /** Set date in format got from FTP server
     * @param month first three letter from month
     * @param day number of day
     * @param timeoryear year or time
     * @return true if format of date is correct
     */
    public boolean setDate(String month, int day, String timeoryear) {
	    int nmonth = 0;
	    
	    if (month.equalsIgnoreCase("JAN")) nmonth = 1;
	    else if (month.equalsIgnoreCase("FEB")) nmonth = 2;
	    else if (month.equalsIgnoreCase("MAR")) nmonth = 3;
	    else if (month.equalsIgnoreCase("APR")) nmonth = 4;
	    else if (month.equalsIgnoreCase("MAY")) nmonth = 5;
	    else if (month.equalsIgnoreCase("JUN")) nmonth = 6;
	    else if (month.equalsIgnoreCase("JUL")) nmonth = 7;
	    else if (month.equalsIgnoreCase("AUG")) nmonth = 8;
	    else if (month.equalsIgnoreCase("SEP")) nmonth = 9;
	    else if (month.equalsIgnoreCase("OCT")) nmonth = 10;
	    else if (month.equalsIgnoreCase("NOV")) nmonth = 11;
	    else if (month.equalsIgnoreCase("DEC")) nmonth = 12;
	    else return false;
	    nmonth--;
	    
	    Calendar cal = Calendar.getInstance();
	    Date date = cal.getTime();
	    int curryear = cal.get(Calendar.YEAR);
	    cal.clear();
	    
	    int colon = timeoryear.indexOf(':');
	    if (colon == -1) {
	        int nyear=0;
	        try {  nyear=Integer.parseInt(timeoryear); }
	        catch (NumberFormatException e) { return false; }
	        cal.set(nyear,nmonth,day,0,0,0);
	    }    
	    else  {
	      StringTokenizer stime = new StringTokenizer(timeoryear,":");
	      String hour="0", min="0", sec="0";
	      int nhour=0, nmin=0, nsec=0;
	      if (stime.hasMoreTokens())  hour=stime.nextToken();
	      if (stime.hasMoreTokens())  min=stime.nextToken();
	      if (stime.hasMoreTokens())  sec=stime.nextToken();
	      try {
	        nhour=Integer.parseInt(hour);
	        nmin=Integer.parseInt(min);
	        nsec=Integer.parseInt(sec);
	      }
	      catch (NumberFormatException e) {
	         //nhour=0; nmin=0; nsec=0;
                 return false;
	      }
	      cal.set(Calendar.HOUR,nhour);
	      cal.set(Calendar.MINUTE,nmin);
	      cal.set(Calendar.SECOND,nsec);
	      cal.set(Calendar.YEAR,curryear);
	      cal.set(Calendar.MONTH,nmonth);
	      cal.set(Calendar.DAY_OF_MONTH,day);
	      if (date.compareTo(cal.getTime()) < 0 ) cal.set(Calendar.YEAR,curryear-1);
	    }  
	    date = cal.getTime();
	    this.setDate(date); 
            return true;
    }  
 
}