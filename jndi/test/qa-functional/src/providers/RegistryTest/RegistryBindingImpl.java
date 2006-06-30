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
