/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.jndi;

/*JNDI*/
import javax.naming.*;
import javax.naming.directory.*;

/*JDK*/
import java.util.*;






public class JndiDirContext extends InitialDirContext
{
    public final static int SLEEP_TIME=100;
    public final static int TIME_OUT=10000;
    
    protected boolean inwork;		// Flag context in work  
    protected int iExpecterCount;	// Operation called for ... time    
    protected Hashtable env_table;


    //Constuctor takes environment with proper properties 
    public JndiDirContext(Hashtable env) throws NamingException
    {
	super(env);
	this.env_table=env;
	this.inwork=false;
	iExpecterCount=0;
    }
    
    //Timer
    public void waitForFinish()
    {
	iExpecterCount++;
	try
	{
	    int timer=0;
	    while (this.inwork && timer< (JndiDirContext.TIME_OUT*iExpecterCount))
	    {
		Thread.sleep(JndiDirContext.SLEEP_TIME);
		timer+=JndiDirContext.SLEEP_TIME;
	    }  
	    
	}catch(InterruptedException ie){}
	if (!this.inwork) iExpecterCount--;
    }
    
    //Is in progress?
    public boolean getInWork()
    {
	return this.inwork;
    }
    
    //Set/Clear flag
    public synchronized void setInWotk(boolean inwork)
    {
	this.inwork=inwork;
    }
    
    public Hashtable getEnvironment()
    {
	return this.env_table;
    }
    
    
    public void setEnvironment(Hashtable env)
    {
	this.env_table=env;
    }
    
    
}
