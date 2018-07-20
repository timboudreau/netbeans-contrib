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
