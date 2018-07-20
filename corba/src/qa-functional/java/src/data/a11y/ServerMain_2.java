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
 * ServerMain_2.java  --  server side implementation
 *
 * Created on November 9, 2001, 10:54 AM for VisiBroker 4.x for Java
 * with Naming Service binding.
 */

package data.a11y;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
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
/**
 *
 * @author  dk102396
 * @version 
 */
public class ServerMain_2 {

    /** Creates new ServerMain_2 */
    public ServerMain_2() {
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {

	try {
            // setting system properties is necessary in order to use this ORB in JDK
            Properties props = System.getProperties();
            props.put("org.omg.CORBA.ORBClass", "com.inprise.vbroker.orb.ORB");
            props.put("org.omg.CORBA.ORBSingletonClass", "com.inprise.vbroker.orb.ORB");
            System.setProperties(props);

            ORB orb = ORB.init(args, props);

            POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));//GEN-BEGIN:poa_section_vb4
            
            Policy[] _policies;
            
            _policies = new Policy[] {
            };
            POA myPOA1 = poa.create_POA("MyPOA1", null, _policies);//GEN-END:poa_section_vb4

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
            myPOA1.the_POAManager().activate();//GEN-END:poa_activate_section

            orb.run();
	} catch (Exception ex) {
	     ex.printStackTrace ();
	}
    }
}
