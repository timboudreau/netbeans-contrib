/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.rmi;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.activation.*;
import java.rmi.server.UnicastRemoteObject;
import java.lang.reflect.Field;
import java.util.*;

/**
 * The Wrapper provides control over an activation system implementation.
 * Exports own service for retrieving activation identifiers.
 * @author  Jan Pokorsky
 * @version
 */
public class RMIDWrapperImpl extends UnicastRemoteObject implements RMIDWrapper, Runnable {

    private static final boolean debug = Boolean.getBoolean("org.netbeans.modules.rmi.test"); // NOI18N
    private static final boolean exception = Boolean.getBoolean("netbeans.debug.exceptions"); // NOI18N
    
    private static final String LOG_ARG = "-log";
    
    /** Activator's registry. */
    private Registry registry = null;
    /** Activation system. */
    private ActivationSystem system = null;
    
    /**
     * max timeout for looking up the local registry
     */
    public static final long MAX_REGISTRY_TIMEOUT = 1000 << 10; // ~ 17 min.
    
    /** Creates new RMIDWrapper */
    private RMIDWrapperImpl() throws RemoteException {
    }
    
    static void argsToString(String[] args) {
        for (int i = 0; args != null && i < args.length; i++)
            System.out.print('['+args[i]+"] ");
        System.out.println("\nEnd of argument list.");
    }
    
    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        // argsToString(args);
        try {
            System.setSecurityManager(new RMISecurityManager());
            Thread wrapper = new Thread(new RMIDWrapperImpl());
            wrapper.setDaemon(true);
            wrapper.start();
        } catch (RemoteException re) {
            System.err.println(ResourceBundle.getBundle("org/netbeans/rmi/Bundle").getString("MSG_RMIDWrapperImpl.UnableStart")); // NOI18N
            if (exception) re.printStackTrace();
            System.exit(1);
        }
        sun.rmi.server.Activation.main(processArgs (args));
    }
    
    public void run() {
        initService();
    }
    
    /** Initialize a remote service for retrieving activation identifiers.
     * Remote service can be look up by
     * <CODE>Registry.lookup("rmi://hostname:rmidport/org.netbeans.rmi.RMIDWrapper")
     * </CODE>.
     */    
    private void initService() {
        long timeout = 1000;    // 1 s
        
        do {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ie) {}
            
            try {
                registry = (Registry) findObjectTableTarget(java.rmi.registry.Registry.class);
                registry.bind("org.netbeans.rmi.RMIDWrapper", this); // NOI18N
                return;
            } catch (AlreadyBoundException ex) {
                // servis is already installed
                return;
            } catch (AccessException ex) {
                // forbidden operation (non-local host, etc)
                if (exception) ex.printStackTrace();
                return;
            } catch (RemoteException ex) {
                // if registry could not be contacted; try again
                if (exception) ex.printStackTrace();
            } catch (UnknownRMIDException ex) {
                // probably wrong implementation or activation system not ready yet
                if (exception) ex.printStackTrace();
            }
        } while ((timeout <<= 1) <= MAX_REGISTRY_TIMEOUT);
    }
    
    /** Get Activation System.
     * @throws ActivationException when system is not running.
     */
    private ActivationSystem getSystem() throws ActivationException {
        if (system == null) {
            try {
                system = (ActivationSystem) registry.lookup("java.rmi.activation.ActivationSystem"); // NOI18N
            } catch (Exception ex) {
                throw new ActivationException("ActivationSystem not running", ex); // NOI18N
            }
        }
        return system;
    }

    /** Returns an array of activation identifiers.
     * @return the array of activation ids
     * @throws RemoteException
     * @throws UnknownRMIDException if a unknown rmid implementation is used.
     */  
    public java.rmi.activation.ActivationID[] getActivationIDs() throws java.rmi.RemoteException, UnknownRMIDException {
        try {
            Object obj = findObjectTableTarget(java.rmi.activation.ActivationSystem.class);
            Field f = obj.getClass().getDeclaredField("this$0"); // NOI18N
            f.setAccessible(true);
            obj = f.get(obj);
            f = obj.getClass().getDeclaredField("idTable"); // NOI18N
            f.setAccessible(true);
            Hashtable ht = (Hashtable) f.get(obj);
            Set set = ht.keySet();
            return (ActivationID[]) set.toArray(new ActivationID[set.size()]);
        } catch (IllegalAccessException ex) {
            if (exception) ex.printStackTrace();
            throw new UnknownRMIDException(ex);
        } catch (NoSuchFieldException ex) {
            // an activation system does not exist
            if (exception) ex.printStackTrace();
            throw new UnknownRMIDException(ex);
        } catch (NullPointerException ex) {
            if (exception) ex.printStackTrace();
            throw new UnknownRMIDException(ex);
        }
    }
    
    /** Returns an array of activation group identifiers.
     * @return the array of activation group ids
     * @throws RemoteException
     * @throws UnknownRMIDException if a unknown rmid implementation is used.
     */  
    public java.rmi.activation.ActivationGroupID[] getActivationGroupIDs() throws java.rmi.RemoteException, UnknownRMIDException {
        try {
            Object obj = findObjectTableTarget(java.rmi.activation.ActivationSystem.class);
            Field f = obj.getClass().getDeclaredField("this$0"); // NOI18N
            f.setAccessible(true);
            obj = f.get(obj);
            f = obj.getClass().getDeclaredField("groupTable"); // NOI18N
            f.setAccessible(true);
            Hashtable ht = (Hashtable) f.get(obj);
            Set set = ht.keySet();
            return (ActivationGroupID[]) set.toArray(new ActivationGroupID[set.size()]);
        } catch (IllegalAccessException ex) {
            if (exception) ex.printStackTrace();
            throw new UnknownRMIDException(ex);
        } catch (NoSuchFieldException ex) {
            // an activation system does not exist
            if (exception) ex.printStackTrace();
            throw new UnknownRMIDException(ex);
        } catch (NullPointerException ex) {
            if (exception) ex.printStackTrace();
            throw new UnknownRMIDException(ex);
        }
    }
    
    /** Finds a weak reference to a remote object implementation in ObjectTable.
     * @return weak reference to remote object implementation
     * @param find remote object class to find
     * @throws UnknownRMIDException if a unknown rmid implementation is used.
     */    
    private Object findObjectTableTarget(Class find) throws UnknownRMIDException {
        try {
            Field f = sun.rmi.transport.ObjectTable.class.getDeclaredField("objTable"); // NOI18N
            f.setAccessible(true);
            Object obj = f.get(null);
            if (!(obj instanceof java.util.Map))
                throw new UnknownRMIDException();
            
            Map map = (Map) obj;     // map of Targets
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                Object target = me.getValue();
                if (!(target instanceof sun.rmi.transport.Target))
                    throw new UnknownRMIDException();
                
                f = sun.rmi.transport.Target.class.getDeclaredField("weakImpl"); // NOI18N
                f.setAccessible(true);
                obj = f.get(target);
                if (!(obj instanceof java.lang.ref.WeakReference))
                    throw new UnknownRMIDException();
                
                java.lang.ref.WeakReference wr = (java.lang.ref.WeakReference) obj;
                Object ro = wr.get();   // remote object
                if (find.isInstance(ro))
                    return ro;
            }
        } catch (NullPointerException ex) {
            if (exception) ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            // attempt access inaccessible field
            if (exception) ex.printStackTrace();
        } catch (NoSuchFieldException ex) {
            // wrong implementation
            if (exception) ex.printStackTrace();
        }
        
        throw new UnknownRMIDException();
    }
    
    // methods hereinafter are ActivationSystem implementation
    
    public ActivationDesc setActivationDesc(ActivationID p1, ActivationDesc p2)
                                                throws ActivationException,
                                                       UnknownObjectException,
                                                       UnknownGroupException,
                                                       RemoteException {
        return getSystem().setActivationDesc(p1, p2);
    }
    
    public ActivationID registerObject(ActivationDesc p1)
                                                throws ActivationException,
                                                       UnknownGroupException,
                                                       RemoteException {
        return getSystem().registerObject(p1);
    }
    
    public ActivationMonitor activeGroup(ActivationGroupID p1,
                                         ActivationInstantiator p2,
                                         long p3)
                                         throws UnknownGroupException,
                                                ActivationException,
                                                RemoteException {
        return getSystem().activeGroup(p1, p2, p3);
    }
    
    public void shutdown() throws java.rmi.RemoteException {
        try {
            final ActivationSystem as = getSystem();
            final RMIDWrapperImpl wrapper = this;
            Thread t = new Thread() {
                public synchronized void run() {
                    try {
                        unexport(wrapper);
                        as.shutdown();
                    } catch (RemoteException ex) {
                    }
                }
            };
            synchronized (t) {
                t.start();
                return;
            }
        } catch (ActivationException ex) {
            throw new RemoteException(null, ex);
        }
        
    }
    
    public ActivationGroupDesc getActivationGroupDesc(ActivationGroupID p1)
                                               throws ActivationException,
                                                      UnknownGroupException,
                                                      RemoteException {
        return getSystem().getActivationGroupDesc(p1);
    }
    
    public ActivationGroupDesc setActivationGroupDesc(ActivationGroupID p1,
                                                      ActivationGroupDesc p2)
                                               throws ActivationException,
                                                      UnknownGroupException,
                                                      RemoteException {
        return getSystem().setActivationGroupDesc(p1, p2);
    }
    
    public ActivationDesc getActivationDesc(ActivationID p1)
                                               throws ActivationException,
                                                      UnknownObjectException,
                                                      RemoteException {
        return getSystem().getActivationDesc(p1);
    }
    
    public ActivationGroupID registerGroup(ActivationGroupDesc p1)
                                               throws ActivationException,
                                                      RemoteException {
        return getSystem().registerGroup(p1);
    }
    
    public void unregisterObject(ActivationID p1) throws ActivationException,
                                                         UnknownObjectException,
                                                         RemoteException {
        getSystem().unregisterObject(p1);
    }
    
    public void unregisterGroup(ActivationGroupID p1) throws ActivationException,
                                                             UnknownGroupException,
                                                             RemoteException {
        getSystem().unregisterGroup(p1);
    }
    
    // methods above are ActivationSystem implementation
    
    /** Method tries to unexport object until it is possible. */
    private void unexport(Remote obj) {
	for (;;) {
	    try {
		if (UnicastRemoteObject.unexportObject(obj, false) == true) {
		    break;
		} else {
		    Thread.sleep(100);
		}
	    } catch (Exception e) {
		continue;
	    }
	}
        if (debug) System.err.println("RMIDWrapperImpl unexported"); // NOI18N
    }
    
    /** Returns the remote reference to the activatable object.
     * @param desc activatable object descriptor
     * @param id activation id
     * @return a remote reference.
     * @throws RemoteException - if remote call fails
     * @throws ActivationException - if activation system is not running or for general failure
     * @throws UnknownObjectException - if id is not registered
     * @throws StubNotFoundException
     */
    public java.rmi.Remote getStub(java.rmi.activation.ActivationID id)
    throws java.rmi.RemoteException,
           java.rmi.activation.ActivationException,
           java.rmi.activation.UnknownObjectException,
           java.rmi.StubNotFoundException {
        ActivationDesc desc = getSystem().getActivationDesc(id);
        return sun.rmi.server.ActivatableRef.getStub(desc, id);
    }
    
    /** Registers the remote reference of the activatable object to 
     * a local registry.
     * @param id activation id
     * @param name name for the remote object
     * @param port port on which the registry accepts requests
     * @throws RemoteException - if remote call fails
     * @throws AccessException  if Registry.rebind operation is not permitted.
     * @throws MalformedURLException - if the name is not an appropriately formatted URL
     * @throws ActivationException - if activation system is not running or for general failure
     * @throws UnknownObjectException - if id is not registered
     * @throws StubNotFoundException
     */
    public void rebind(java.rmi.activation.ActivationID id,
                       String name,
                       int port)
    throws java.rmi.RemoteException,
           java.rmi.AccessException,
           java.net.MalformedURLException,
           java.rmi.activation.ActivationException,
           java.rmi.activation.UnknownObjectException,
           java.rmi.StubNotFoundException {
        Registry reg = LocateRegistry.getRegistry(port);
        reg.rebind(name, getStub(id));
    }
    
    /** Inactivate activatable object.
     * @param id activation id
     * @throws RemoteException - if remote call fails
     * @throws ServerException - detail = RemoteException - if call informing monitor fails
     * @throws UnknownObjectException - if id is not registered
     * @throws UnknownRMIDException if a unknown rmid implementation is used.
     */
    public void inactivateObject(java.rmi.activation.ActivationID id)
    throws java.rmi.RemoteException,
           java.rmi.activation.UnknownObjectException,
           UnknownRMIDException
    {
        try {
            ActivationMonitor monitor = (ActivationMonitor) findObjectTableTarget(ActivationMonitor.class);
            
            // RemoteObject.toStub is called due to security check for getClientHost == localhost
            // in ActivationMonitor.inactiveObject method.
            // Method findObjectTableTarget(ActivationMonitor.class) does not return stub
            // and so getClientHost may be nonlocal.
            monitor = (ActivationMonitor) java.rmi.server.RemoteObject.toStub(monitor);
            
            monitor.inactiveObject(id);
        } catch (NullPointerException ex) { // monitor not found
            if (exception) ex.printStackTrace();
            throw new UnknownRMIDException(ex);
        } catch (NoSuchObjectException ex) { // stub not found
            if (exception) ex.printStackTrace();
            throw new UnknownRMIDException(ex);
        }
            
    }
    
    /** Inactivate group.
     * @param gid activation group id
     * @throws RemoteException - if remote call fails
     * @throws ServerException - detail = RemoteException - if call informing monitor fails
     * @throws UnknownGroupException - unknown group id
     * @throws UnknownRMIDException if a unknown rmid implementation is used.
     */
    public void inactivateGroup(java.rmi.activation.ActivationGroupID gid)
    throws java.rmi.RemoteException,
           java.rmi.activation.UnknownGroupException,
           UnknownRMIDException
    {
        try {
            ActivationMonitor monitor = (ActivationMonitor) findObjectTableTarget(ActivationMonitor.class);
            
            Field f = monitor.getClass().getDeclaredField("this$0"); // NOI18N
            f.setAccessible(true);
            Object obj = f.get(monitor);
            f = obj.getClass().getDeclaredField("groupTable"); // NOI18N
            f.setAccessible(true);
            Hashtable groups = (Hashtable) f.get(obj);
            obj = groups.get(gid);
            if (obj == null) throw new UnknownGroupException("unknown group: " + gid); // NOI18N
            f = obj.getClass().getDeclaredField("incarnation"); // NOI18N
            f.setAccessible(true);
            long incarnation = f.getLong(obj);
            
            monitor.inactiveGroup(gid, incarnation);
        } catch (NoSuchFieldException ex) {
            if (exception) ex.printStackTrace();
            throw new UnknownRMIDException(ex);
        } catch (NullPointerException ex) {
            if (exception) ex.printStackTrace();
            throw new UnknownRMIDException(ex);
        } catch (IllegalAccessException ex) {
            if (exception) ex.printStackTrace();
            throw new UnknownRMIDException(ex);
        }
    }
    
    private void findObjectTableTargets() throws UnknownRMIDException {
        try {
            Field f = sun.rmi.transport.ObjectTable.class.getDeclaredField("objTable"); // NOI18N
            f.setAccessible(true);
            Object obj = f.get(null);
            if (!(obj instanceof java.util.Map))
                throw new UnknownRMIDException();
            
            Map map = (Map) obj;     // map of Targets
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                Object target = me.getValue();
                if (!(target instanceof sun.rmi.transport.Target))
                    throw new UnknownRMIDException();
                
                f = sun.rmi.transport.Target.class.getDeclaredField("weakImpl"); // NOI18N
                f.setAccessible(true);
                obj = f.get(target);
                if (!(obj instanceof java.lang.ref.WeakReference))
                    throw new UnknownRMIDException();
                
                java.lang.ref.WeakReference wr = (java.lang.ref.WeakReference) obj;
                Object ro = wr.get();   // remote object
                System.err.println(ro.getClass()+" : "+ro.toString()); // NOI18N
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (IllegalAccessException iae) {
            // attempt access inaccessible field
            iae.printStackTrace();
        } catch (NoSuchFieldException nfe) {} // wrong implementation
        
        throw new UnknownRMIDException();
    }
    
    
    private static String[] processArgs (String[] args) {
	String cwd = System.getProperty ("user.dir");	// NOI18N
	boolean specialChar = false;
	for (int i=0; i< cwd.length(); i++)
	    if (cwd.charAt(i) <= 0x20) {
		specialChar = true;
		break;
	    }
	if (specialChar) {
	    for (int i=0; i< args.length; i++)
		if (args[i].equals (LOG_ARG))
		    return args;
	    String[] newArgs = new String[args.length + 2];
	    System.arraycopy (newArgs,0,args,0,args.length);
	    newArgs[newArgs.length-2] = LOG_ARG;
	    newArgs[newArgs.length-1] = cwd;
	    args = newArgs;
	}
	return args;
    }
}
