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

package org.netbeans.modules.corba.browser.ns.wrapper;

/**
 *
 * @author  root
 * @version 
 */
public abstract class AbstractWrapper extends Object implements Runnable {

    public static final short NOT_INITIALIZED = 0;
    public static final short INITIALIZED = 1;
    public static final short ERROR = 2;

    protected short port;
    protected Thread thread;
    protected String ior;
    protected short state;
    
    /** Creates new AbstractWrapper */
    public AbstractWrapper() {
	this.state = NOT_INITIALIZED;
    }
    
    public void start (short port) {
        this.port = port;
        this.thread = new Thread (this);
	this.thread.setName ("Local CORBA Name Service");
        this.thread.start();
    }
    public void stop () {
        if (thread != null && thread.isAlive())
            thread.interrupt();
    }
    
    public String getIOR() {
	synchronized (this) {
	    while (this.state == NOT_INITIALIZED) {
		try {
		    this.wait();
                }catch (InterruptedException ie) {}
	    }
	    switch (this.state) {
		case INITIALIZED:
		    return ior;
		case ERROR:
		    return null;
	    }
	}
        return null;
    }

}
