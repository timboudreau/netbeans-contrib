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

package providers.CNSTest.CNSBinding;

import org.omg.CORBA.ORB;

import java.util.Properties;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NameComponent;

public class CNSBindingServerMain {
    
    public static void main(String args[]) {
        
        try {
            Properties props = System.getProperties();
            ORB orb = ORB.init(new String[] {"-ORBInitialPort", "11198"}, props);
            
            
            // add your creating of object implementation here
            CNSBindingImpl impl = new CNSBindingImpl();
            orb.connect(impl);
            
            // the server will use Naming Service
            org.omg.CORBA.Object ns = orb.resolve_initial_references("NameService");
            if (ns == null)
                throw new RuntimeException();
            NamingContext nc = NamingContextHelper.narrow(ns);
            if (nc == null)
                throw new RuntimeException();
            
            // create and bind Naming Contexts
            String[] hierarchy_of_contexts = new String [] {"CNSTestRoot", "CNSTestRootKind", "CNSTestContext", "CNSTestContextKind"};
            String[] name_of_server = new String [] {"CNSBinding", "CNSBindingKind"};
            NameComponent[] nc1Name = new NameComponent[1];
            NamingContext nc1 = null;
            for (int i=0; i<hierarchy_of_contexts.length / 2; i++) {
                nc1Name[0] = new NameComponent();
                nc1Name[0].id = hierarchy_of_contexts[i*2];
                nc1Name[0].kind = hierarchy_of_contexts[i*2+1];
                try {
                    nc1 = nc.bind_new_context(nc1Name);
                    nc = nc1;
                } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound e) {
                    org.omg.CORBA.Object o = nc.resolve(nc1Name);
                    if (o == null)
                        throw new RuntimeException();
                    nc1 = NamingContextHelper.narrow(o);
                    if (nc1 == null)
                        throw new RuntimeException();
                    nc = nc1;
                }
            }
            
            // bind names with the Naming Service
            NameComponent[] aName = new NameComponent[1];
            aName[0] = new NameComponent();
            aName[0].id = name_of_server[0];
            aName[0].kind = name_of_server[1];
            nc.rebind(aName, impl);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
