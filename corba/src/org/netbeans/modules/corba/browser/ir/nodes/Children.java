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

/*
 * Children.java
 *
 * Created on September 5, 2000, 5:06 PM
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import org.openide.nodes.*;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.util.AsyncTarget;

/**
 *
 * @author  tzezula
 * @version 
 */
public abstract class Children extends org.openide.nodes.Children.Keys implements Refreshable, AsyncTarget {
    
    //private static final boolean DEBUG = true;
    private static final boolean DEBUG = false;

    /** Creates new Children */
    public static final int NOT_INITIALIZED = 0;
    public static final int TRANSIENT = 1;
    public static final int INITIALIZED = 2;
    public static final int SYNCHRONOUS = 3;
    
    protected int state;
    protected Node waitNode;
    
    public Children() {
        super ();
        synchronized (this) {
            this.state = NOT_INITIALIZED;
        }
    }
    
    public abstract void createKeys();
    
    public synchronized int getState () {
        return this.state;
    }
    
    public void preinvoke () {
    }
    
    public void invoke () {
        if (DEBUG)
            System.out.println("Invoke");
        createKeys ();
    }
    
    public void postinvoke () {
        if (this.waitNode != null) {
            remove ( new Node[] {this.waitNode});
            this.waitNode = null;
        }
        synchronized (this) {
            this.state = INITIALIZED;
        }
    }

}
