/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * AbstractWrapper.java
 *
 * Created on 12. øíjen 2000, 10:30
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
