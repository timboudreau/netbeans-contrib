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

package data.poasearch.main;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ThreadPolicyValue;

import java.util.Properties;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NameComponent;

public class ServerMain {
    
    /** Creates new ServerMain */
    public ServerMain() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        try {
            

            ORB orb = ORB.init(args, null);

            POA poa = (POA)orb.resolve_initial_references("RootPOA");//GEN-BEGIN:poa_section_JDK1.4
            
            Policy[] _policies;
            
            _policies = new Policy[] {
                poa.create_id_uniqueness_policy(IdUniquenessPolicyValue.MULTIPLE_ID),
                poa.create_request_processing_policy(RequestProcessingPolicyValue.USE_DEFAULT_SERVANT)
            };
            POA myPOA1 = poa.create_POA("Default", null, _policies);
            
            _policies = new Policy[] {
                poa.create_request_processing_policy(RequestProcessingPolicyValue.USE_SERVANT_MANAGER)
            };
            POA myPOA2 = poa.create_POA("Manager", null, _policies);
            
            _policies = new Policy[] {
            };
            POA myPOA3 = poa.create_POA("Activator", null, _policies);
            
            _policies = new Policy[] {
            };
            POA myPOA4 = poa.create_POA("Servant", null, _policies);//GEN-END:poa_section_JDK1.4

            // add your creating of object implementation here
            //GEN-LINE:servant_section

            // the server will use Naming Service
            org.omg.CORBA.Object ns = orb.resolve_initial_references("NameService");
            if (ns == null)
                throw new RuntimeException();
            NamingContext nc = NamingContextHelper.narrow(ns);
            if (nc == null)
                throw new RuntimeException();
            
            // create and bind Naming Contexts
            // paste code retrieved using the Copy Client/Server Code action
            // (on the corresponding node in the Naming Service Browser) here
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
            nc.bind(aName, poa.servant_to_reference(/*servant_variable*/));

            poa.the_POAManager().activate();//GEN-BEGIN:poa_activate_section
            myPOA1.the_POAManager().activate();
            myPOA2.the_POAManager().activate();
            myPOA3.the_POAManager().activate();
            myPOA4.the_POAManager().activate();//GEN-END:poa_activate_section

            orb.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
