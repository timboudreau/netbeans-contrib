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
 * IBMWrapper.java
 *
 * Created on 12. øíjen 2000, 10:39
 */

package org.netbeans.modules.corba.browser.ns.wrapper;

import java.util.Properties;
import com.ibm.CosNaming.*;
import org.omg.CORBA.*;
/**
 *
 * @author  root
 * @version 
 */
public class IBMWrapper extends AbstractWrapper {
    
    /** Creates new IBMWrapper */
    public IBMWrapper() {
    }

    public void run() {
        Properties properties = new Properties();
        properties.put("org.omg.CORBA.ORBClass", "com.ibm.rmi.iiop.ORB");
        com.ibm.rmi.iiop.ORB orb = (com.ibm.rmi.iiop.ORB)org.omg.CORBA.ORB.init(new String[]{}, properties);
        
        TransientNameService transientnameservice = new TransientNameService(orb);
        org.omg.CosNaming.NamingContext namingcontext = transientnameservice.initialNamingContext(); 
        this.ior = orb.object_to_string(namingcontext);
        properties.put("NameService", this.ior);
        com.ibm.CosNaming.BootstrapServer bootstrapserver = new com.ibm.CosNaming.BootstrapServer(orb, (int)this.port, null, properties);
        
        try {
            bootstrapserver.start();
    	    synchronized (this) {
		this.state = INITIALIZED;
		this.notify();
	    }
	} catch(SystemException systemexception)
        {
	    synchronized (this) {
    		this.state = ERROR;
		this.notify();
	    }
        }
        
        java.lang.Object sync = new java.lang.Object();
        synchronized (sync) {
            try {
                sync.wait();
            }catch (InterruptedException ie) {}
        }
    }
    
}
