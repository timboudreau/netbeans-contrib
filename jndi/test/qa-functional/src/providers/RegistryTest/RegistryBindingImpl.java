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

package providers.RegistryTest;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

public class RegistryBindingImpl extends java.rmi.server.UnicastRemoteObject implements RegistryBinding {

    public RegistryBindingImpl() throws RemoteException {
        super();
    }

    public RegistryBindingImpl(int port) throws RemoteException {
        super(port);
    }

    public static void main(String[] args) throws Exception {
        System.setSecurityManager(new RMISecurityManager());

        RegistryBindingImpl obj = new RegistryBindingImpl();
        LocateRegistry.getRegistry(11199).rebind("RegistryBinding", obj);
    }
    
    public String hello() throws java.rmi.RemoteException {
        return "Hello!";
    }
    
}
