/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * SNS.java  --  server side implementation
 *
 * Created on November 28, 2000, 11:27 AM for JDK 1.3 ORB
 * with Naming Service binding.
 */

package test.nsbrowser.bind;

import org.omg.CORBA.*;
import java.io.*;
import java.util.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;

/**
 *
 * @author  dkaspar
 * @version 
 */
public class HelloSNS {

    /** Creates new SNS */
    public HelloSNS() {
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {

        args = new String[] {"-ORBInitialPort", "11903"};
        
        // create ORB
        ORB orb = null;
        try {
            orb = ORB.init (args, null);
            // add code to instantiate your object implementation here
HelloImpl hi = new HelloImpl ();


            orb.connect (hi/* add your object implementation here */);

            //this server will use Naming Service
            org.omg.CORBA.Object o1 = null;
            try {
                o1 = orb.resolve_initial_references("NameService");
            } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
                System.out.println ("Can't binding to NameService");
                System.exit (1);
            }
            NamingContext nc = NamingContextHelper.narrow(o1);
            
            if (nc == null) {
                System.out.println ("Naming Context is null!!!");
                System.exit (1);
            }
            
            try {
                //
                // Create and bind Naming Contexts
                //
String[] hierarchy_of_contexts = new String [] {"NSName", "NSKind"};
String[] name_of_server = new String [] {"ServerName", "ServerKind"};
                // paste code retrieved using the Copy Server Code action (on a context node in the Naming Service Browser) here
                boolean already_bound = false;
                NameComponent[] nc1Name = new NameComponent[1];
                NamingContext nc1 = null;
                for (int i=0; i<hierarchy_of_contexts.length / 2; i++) {
                    nc1Name[0] = new NameComponent();
                    nc1Name[0].id = hierarchy_of_contexts[i*2];
                    nc1Name[0].kind = hierarchy_of_contexts[i*2+1];
                    try {
                        nc1 = nc.bind_new_context (nc1Name);
                        nc = nc1;
                    } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound e) {
                        already_bound = true;
                    }
                    if (already_bound) {
                        try {
                            org.omg.CORBA.Object o = nc.resolve (nc1Name);
                            nc1 = NamingContextHelper.narrow (o);
                            if (nc1 != null)
                                nc = nc1;
                            already_bound = false;
                        } catch (Exception e) {
                            e.printStackTrace ();
                        }
                    }
                }
                
                //
                // Bind names with the Naming Service
                //
                NameComponent[] aName = new NameComponent[1];
                aName[0] = new NameComponent ();
                aName[0].id = name_of_server[0];
                aName[0].kind = name_of_server[1];
                nc.rebind(aName, hi/* place here name of servant variable */);
            } catch (Exception e) {
                e.printStackTrace ();
            }
/*
            java.lang.Object sync = new java.lang.Object ();
            synchronized (sync) {
                sync.wait ();
            }*/
        } catch (Exception ex) {
            ex.printStackTrace ();
        }

    }

}
