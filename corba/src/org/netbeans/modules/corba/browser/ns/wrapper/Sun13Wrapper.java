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
import com.sun.corba.se.internal.iiop.ORB;
import com.sun.corba.se.internal.CosNaming.*;
import java.util.Properties;

/**
 *
 * @author  tzezula
 * @version 
 */
public class Sun13Wrapper extends AbstractWrapper {

    /** Creates new Sun13Wrapper */
    public Sun13Wrapper() {
    }
    
    public void run () {
        
        Properties properties = new Properties();
        try {
            Class.forName ("com.sun.corba.se.internal.POA.POAORB");
            properties.put ("org.omg.CORBA.ORBClass","com.sun.corba.se.internal.POA.POAORB");
        }catch (ClassNotFoundException cnfe) {
            properties.put ("org.omg.CORBA.ORBClass","com.sun.corba.se.internal.iiop.ORB");
        }
        
        ORB orb = (ORB) org.omg.CORBA.ORB.init(new String[]{}, properties);
        
        TransientNameService transientnameservice = new TransientNameService(orb);
        org.omg.CosNaming.NamingContext namingcontext = transientnameservice.initialNamingContext(); 
        this.ior = orb.object_to_string(namingcontext);
        properties.put("NameService", this.ior);
        BootstrapServer bootstrapserver = new BootstrapServer(orb, (int)this.port, null, properties);
        
        try {
            bootstrapserver.start();
    	    synchronized (this) {
		this.state = INITIALIZED;
		this.notify();
	    }
	} catch(org.omg.CORBA.SystemException systemexception)
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
