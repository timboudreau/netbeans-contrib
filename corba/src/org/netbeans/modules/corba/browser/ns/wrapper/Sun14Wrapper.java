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

package org.netbeans.modules.corba.browser.ns.wrapper;


import java.util.Properties;
/**
 *
 * @author  xtom
 * @version
 */
public class Sun14Wrapper extends AbstractWrapper {

    /** Creates new Sun14Wrapper */
    public Sun14Wrapper() {
    }

    public void run() {
        Properties properties = new Properties();
        properties.put ("org.omg.CORBA.ORBClass","com.sun.corba.se.internal.CosNaming.NSORB");

        try {
            Class orbClass = Class.forName ("org.omg.CORBA.ORB");
            java.lang.Object[] params = new Object[]{new String[0],properties};
            java.lang.reflect.Method m = orbClass.getMethod ("init", new Class[]{params[0].getClass(),params[1].getClass()});
            Object orb = m.invoke (null,params);        
//           ORB orb = (ORB) org.omg.CORBA.ORB.init(new String[]{}, properties);
        
            Class transientNameServiceClass = Class.forName ("com.sun.corba.se.internal.CosNaming.TransientNameService");
            java.lang.reflect.Constructor c = transientNameServiceClass.getConstructor (new Class[] {Class.forName("com.sun.corba.se.internal.POA.POAORB")});
            Object transientNameService = c.newInstance (new java.lang.Object[]{orb});
        
//           TransientNameService transientnameservice = new TransientNameService(orb);
        
            m = transientNameServiceClass.getMethod ("initialNamingContext",new Class[0]);
            java.lang.Object namingContext = m.invoke (transientNameService, new Object[0]);
             
//           org.omg.CosNaming.NamingContext namingcontext = transientnameservice.initialNamingContext(); 
            
            params = new java.lang.Object[]{namingContext};
            m = orbClass.getMethod ("object_to_string", new Class[]{org.omg.CORBA.Object.class});
            this.ior = (String) m.invoke (orb,params);
            
//           this.ior = orb.object_to_string(namingcontext);
            
            properties.put("NameService", this.ior);
            
            Class bootstrapServerClass = Class.forName ("com.sun.corba.se.internal.CosNaming.BootstrapServer");
            params = new Object[] {orb,new Integer (this.port),null,properties};
            c = bootstrapServerClass.getConstructor (new Class[]{Class.forName("com.sun.corba.se.internal.iiop.ORB"),Integer.TYPE,java.io.File.class,params[3].getClass()});
            java.lang.Object bootstrapServer = c.newInstance (params);
            
//           BootstrapServer bootstrapserver = new BootstrapServer(orb, (int)this.port, null, properties);
        
            try {
                m = bootstrapServerClass.getMethod ("start", new Class[0]);
                m.invoke (bootstrapServer, new java.lang.Object[0]);
//                bootstrapserver.start();
                synchronized (this) {
                    this.state = INITIALIZED;
                    this.notify();
                }
                
                java.lang.Object sync = new java.lang.Object();
                synchronized (sync) {
                    try {
                        sync.wait();
                    }catch (InterruptedException ie) {}
                }
                
            } catch(org.omg.CORBA.SystemException systemexception) {
                synchronized (this) {
                    this.state = ERROR;
                    this.notify();
                }
            }
        }catch (Exception e) {
            synchronized (this) {
                this.state = ERROR;
                this.notify();
            }
        }
        
    }
    
}
