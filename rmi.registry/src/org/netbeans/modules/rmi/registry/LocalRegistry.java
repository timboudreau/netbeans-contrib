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

package org.netbeans.modules.rmi.registry;

import java.io.IOException;
import java.lang.reflect.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import org.netbeans.modules.rmi.registry.settings.RMIRegistrySettings;

import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 *
 * @author  mryzl
 * @version
 */
public final class LocalRegistry {

    public static final int RUNNING = 1;
    public static final int STOPPED = 2;
    public static final int ERROR = 3;

    /** Registry port. */
    private int runningOnPort = -1;
    
    /** Registry. */
    private Registry registry = null;
    
    /** Singleton instance. */
    private static LocalRegistry instance;
    
    /** Creates new LocalRegistry */
    private LocalRegistry() {
    }
    
    /** Test whether the registry is running.
     */
    private boolean isRunning() {
        Remote r = getImpl(ObjID.REGISTRY_ID);
        //  registry   r    result
        //     0        0    false
        //     0        1   true
        //     1        0   true
        //     1        1   true
        return (r != null) || (registry != null);
    }
    
    /** Start registry. Don't stop the current version, throw an exception instead.
    * @param internalRegistryPort port value. 
    */
    public synchronized Status startRegistry() {
        if (isRunning()) return new Status(RUNNING, runningOnPort);

        try {
            runningOnPort = RMIRegistrySettings.getInstance().getInternalRegistryPort();
            registry = java.rmi.registry.LocateRegistry.createRegistry(runningOnPort, RMISocketFactory.getDefaultSocketFactory(), new RMIRegistrySF());
            System.out.println(java.text.MessageFormat.format(
                NbBundle.getBundle(LocalRegistry.class).getString("FMT_RegistryStarted"), // NOI18N
                new Object[] {new Integer(runningOnPort)}
            ));
            return new Status(RUNNING, runningOnPort);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            return new Status(ERROR, -1);
        }
    }

    /** Stop registry.
    */
    public synchronized Status stopRegistry() {
        try {
            if (registry != null) {
                unexportObject(registry, true);
                registry = null;
                if (runningOnPort != -1) {
                    RMIRegistrySF.cancelSocket(runningOnPort, 1000);
                }
            }
            runningOnPort = -1;
            return new Status(STOPPED, -1);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            return new Status(ERROR, -1);
        }
    }
    
    /** Get instance of local registry.
     * @return the instance
     */
    public static final LocalRegistry getDefault() {
        if (instance == null) {
            instance = new LocalRegistry();
        }
        return instance;
    }
    
    /** This method is a wrapper for operations on ObjectTable. Some methods
     * are called by reflection even though the might be called directly,
     * but the class may not be present in another implementations of JDK.
     * This wrapper has been tested with SUN JDK 1.2, JDK 1.3
     */
    private static Remote getImpl(int id) {
        return getImpl(new ObjID(id));
    }

    /** This method is a wrapper for operations on ObjectTable. Some methods
     * are called by reflection even though the might be called directly,
     * but the class may not be present in another implementations of JDK.
     * This wrapper has been tested with SUN JDK 1.2, JDK 1.3
     */
    private static Remote getImpl(ObjID objID) {

        try {
            Class clazz = Class.forName("sun.rmi.transport.ObjectTable"); // NOI18N
            Field f = clazz.getDeclaredField("objTable"); // NOI18N
            f.setAccessible(true);
            Object obj = f.get(null);

            Map map = (Map) obj;     // map of Targets
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                Object target = me.getValue();

                f = target.getClass().getDeclaredField("id"); // NOI18N
                f.setAccessible(true);
                ObjID id = (ObjID) f.get(target);
                if ((id != null) && (id.equals(objID))) {

                    f = target.getClass().getDeclaredField("weakImpl"); // NOI18N
                    f.setAccessible(true);
                    obj = f.get(target);

                    java.lang.ref.WeakReference wr = (java.lang.ref.WeakReference) obj;
                    Object ro = wr.get();   // remote object
                    return (Remote) ro;
                }
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        return null;
    }

    /** This method is a wrapper for operations on ObjectTable. Some methods
     * are called by reflection even though the might be called directly,
     * but the class may not be present in another implementations of JDK.
     * This wrapper has been tested with SUN JDK 1.2, JDK 1.3
     */
    private static void unexportObject(Remote obj, boolean force) {
        try {
            Class clazz = Class.forName("sun.rmi.transport.ObjectTable"); // NOI18N
            Method m = clazz.getMethod("unexportObject", new Class[] { java.rmi.Remote.class, Boolean.TYPE }); // NOI18N
            if (m != null) {
                m.invoke(null, new Object[] { obj, force ? Boolean.TRUE : Boolean.FALSE });
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
    }
    
    /** This method is a wrapper for operations on reference. Some methods
     * are called by reflection even though the might be called directly,
     * but the class may not be present in another implementations of JDK.
     * This wrapper has been tested with SUN JDK 1.2, JDK 1.3
     */
    private static int getPort(Remote remote) {
        
        try {
            if (remote instanceof RemoteObject) {
                Object ref = ((RemoteObject) remote).getRef();
                System.err.println("remote ref: " + ref); // NOI18N

                Class clazz = Class.forName("sun.rmi.server.UnicastRef"); // NOI18N
                System.err.println(clazz);
                Field[] fs = clazz.getDeclaredFields();
                for(int i = 0; i < fs.length; i++) {
                    System.err.println("\t" + fs[i]); // NOI18N
                }
                
                
                if (clazz.isAssignableFrom(ref.getClass())) {
                
                    Field f = clazz.getDeclaredField("ref"); // NOI18N
                    f.setAccessible(true);
                    Object lref = f.get(ref);
                    System.err.println("live ref: " + lref); // NOI18N
                    // check for local????
                    
                    try {
                        f = lref.getClass().getDeclaredField("isLocal"); // NOI18N
                        f.setAccessible(true);
                        Boolean b = (Boolean) f.get(lref);
                        if ((b != null) && !b.booleanValue()) return -1;
                    } catch (Exception ex) {
                        // ignore
                    }
                    
                    Method m = lref.getClass().getMethod("getPort", new Class[0]); // NOI18N

                    Integer i = (Integer) m.invoke(lref, new Object[0]);
                    return i.intValue();
                }
            }
        } catch (Exception ex) {
//            debug("LocalRegistry:getPort", ex); // NOI18N
        }
            
        return -1;
    }
    
    /** Get status of local registry.
     * @returns status object
     */
    public Status getStatus() {
        return new Status(isRunning() ? RUNNING: STOPPED, runningOnPort);
    }
    
    
    /** Immutable status.
     */
    public static class Status {
        private int status, port;
        
        private Status(int status, int port) {
            this.status = status;
            this.port = port;
        }
        
        public int getStatus() {
            return status;
        }
        public int getPort() {
            return port;
        }
    }
}
