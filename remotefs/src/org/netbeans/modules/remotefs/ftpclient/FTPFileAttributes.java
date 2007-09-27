/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s):
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
/*
/* If you wish your version of this file to be governed by only the CDDL
/* or only the GPL Version 2, indicate your decision by adding
/* "[Contributor] elects to include this software in this distribution
/* under the [CDDL or GPL Version 2] license." If you do not indicate a
/* single choice of license, a recipient has the option to distribute
/* your version of this file under either the CDDL, the GPL Version 2 or
/* to extend the choice of license to its licensees as provided above.
/* However, if you add GPL Version 2 code and therefore, elected the GPL
/* Version 2 license, then the option applies only if the new code is
/* made subject to such option by the copyright holder.
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
    public FTPFileAttributes(FTPFileName name,boolean isdirectory,String rights,int links,
                            String user,String group,long size,java.util.Date date) {
        super(name, isdirectory, size, date);
        this.rights=rights;
        this.links=links;
        this.user=user;
        this.group=group;
    }
    
    /** Create new FTPFileAttributes */
    public FTPFileAttributes(FTPFileName name,boolean isdirectory,long size,java.util.Date date) {
        super(name,isdirectory,size,date);
    }
   
    /** Create empty FTPFileAttributes */
    public FTPFileAttributes() {
       super();
    }
    
    /** Create FTPFileAttributes only with name and isdirectory flag */
    public FTPFileAttributes(FTPFileName name, boolean isdirectory) {
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
            // [PENDING] temporary workaround for localized filesystems
            // nevertheless, the solution may be very difficult.
            else nmonth = 1;
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