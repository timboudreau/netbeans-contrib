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

package basic.JNDITest;

import java.io.*;
import java.util.ArrayList;

import java.awt.datatransfer.*;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.netbeans.modules.jndi.*;
import org.netbeans.modules.properties.BundleStructure;
import org.netbeans.modules.properties.PropertiesDataObject;
import org.openide.util.Lookup;

public class JNDITest extends org.netbeans.junit.NbTestCase {

    public JNDITest(String testName) {
        super(testName);
    } 

    /** Use for execution inside IDE */ 
    public static void main(java.lang.String[] args) { 
        junit.textui.TestRunner.run(new org.netbeans.junit.NbTestSuite(JNDITest.class)); 
    }

    public void testJNDI() throws Exception {
        PrintStream log = getLog();
        PrintStream ref = getRef();
        
        try {
        /* Test JNDI System Option */
            SystemOptionTest sot = new SystemOptionTest ();
            sot.setIO (ref);
            sot.testSystemOption ("org.netbeans.modules.jndi.settings.JndiSystemOption");
            ref.println ();

        /* Get and test JNDI and Providers node */
            Node jndiNode = null, providersNode = null, n = null;
            jndiNode = JndiRootNode.getDefault ();
            assertNotNull("JNDI node does not exists!", jndiNode);
            Node jndiRootNode = jndiNode;
            providersNode = Helper.findSubNode (jndiNode, "Providers");
            assertNotNull("Providers node does not exists!", providersNode);

        /* Print actions and properties of JNDI and Providers node */
            Helper.printActions (jndiNode, ref);
            Helper.printProperties (jndiNode, ref);
            Helper.printActions (providersNode, ref);
            Helper.printProperties (providersNode, ref);

        /* Get JNDI System DataFolder */
            FileSystem dfs = Repository.getDefault ().getDefaultFileSystem ();
            FileObject[] fos = dfs.getRoot ().getChildren ();
            DataFolder jndiDataFolder = null;
            for (int i = 0; i < fos.length; i ++)
                if (fos [i].getName ().equals ("JNDI")) {
                    try {
                        jndiDataFolder = (DataFolder) DataObject.find (fos [i]);
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                    break;
                }
            assertNotNull("JNDI system data folder does not exists!", jndiDataFolder);

        /* Get JNDI System loaded properties */
            Node[] providersNodes = providersNode.getChildren ().getNodes (true); // under runtime node
            for (int a = 0; a < providersNodes.length; a ++) {
                boolean changed = false;
                for (int b = 0; b < providersNodes.length - 1; b ++) {
                    if (providersNodes[b].getDisplayName ().compareTo (providersNodes[b+1].getDisplayName ()) > 0) {
                        Node helpnode = providersNodes[b];
                        providersNodes[b] = providersNodes[b+1];
                        providersNodes[b+1] = helpnode;
                        changed = true;
                    }
                }
                if (!changed)
                    break;
            }
            DataObject[] jndiDataObjects = jndiDataFolder.getChildren (); // in system directory
            if (jndiDataObjects.length != providersNodes.length)
                ref.println ("Incorrectly loaded providers: Loaded: " + providersNodes.length + ". Should be: " + jndiDataObjects.length + ".");

            for (int i = 0; i < providersNodes.length; i ++) {
                PropertiesDataObject activeDataObject = null;
                Node activeProvider = providersNodes [i];
                BundleStructure bs = null;
                int iKey = 0, iEntry = 0;
                String value = null;

            /* Find PropertiesDataObject */
                for (int b = 0; b < jndiDataObjects.length; b ++) {
                    bs = ((PropertiesDataObject) jndiDataObjects [b]).getBundleStructure ();
                    iKey = bs.getKeyIndexByName ("java.naming.factory.initial");
                    iEntry = bs.getEntryIndexByFileName (jndiDataObjects [b].getName());
                    value = bs.getItem (iEntry, iKey).getValue ();
                    if (value.endsWith (providersNodes [i].getName ())) {
                        activeDataObject = (PropertiesDataObject) jndiDataObjects [b];
                        break;
                    }
                }
                if (activeDataObject == null) {
                    ref.println ("Missing system property file for provider: " + activeProvider.getName ());
                    continue;
                }

            /* Try to find provider class */
                String result;
                try {
                    Class.forName (value);
                    result = "Ok";
                } catch (ClassNotFoundException e) {
                    result = "failed";
                }
                ref.println ("Provider " + value + " initialization " + result + ".");

            /* Print actions */
                Helper.printActions (activeProvider, ref);

            /* Compare properties */
                ref.println ("Properties of node: " + activeProvider.getName ());
                Node.PropertySet[] nps = activeProvider.getPropertySets ();
                for (int a = 0; a < nps.length; a ++) {
                    if (nps [a] == null)
                        continue;
                    ref.println (" PropertySet: " + nps [a].getName ());
                    Node.Property[] np = nps [a].getProperties ();
                    for (int b = 0; b < np.length; b ++) {
                        String propName = np [b].getName ();
                        String propValue;
                        try {
                            propValue = (String) np [b].getValue ();
                        } catch (Exception e) {
                            e.printStackTrace ();
                            propValue = "";
                        }
                        ref.print (propName + " = " + propValue);
                        if (nps [a].getName ().equals ("properties")) {
                            iKey = bs.getKeyIndexByName (propName);
                            String dataValue;
                            try {
                                dataValue = bs.getItem (iEntry, iKey).getValue ();
                            } catch (Exception e) {
                                dataValue = "";
                            }
                            if (dataValue == null)
                                dataValue = "";
                            if (! propValue.equals(dataValue))
                                ref.println (" <- (loaded) - ERROR - (expected) -> " + dataValue);
                            else
                                ref.println ();
                        } else
                            ref.println ();
                    }
                }
                ref.println ();
            }

        /* Add new FS context */
            String osName = System.getProperty("os.name");
            String context = (osName.startsWith("Windows")) ? "file:///c:\\" : "file:///";
            ref.println ("Adding new context");
            JndiRootNode.getDefault ().addContext ("TestNode", "com.sun.jndi.fscontext.RefFSContextFactory", context, "", "", "", "", new java.util.Vector ());
            Node testNode = Helper.waitSubNode (jndiRootNode, "TestNode");
            assertNotNull("Cannot found coxntext: TestNode", testNode);

       /* Print lookup and binding code */
            Helper.sleep (1000);
            Helper.performAction (testNode, LookupCopyAction.class);
            Helper.sleep (1000);
            ref.println ("Lookup copy code on node: TestNode");
            try {
    //                Helper.printClipboardAsString (ref);
                Helper.printClipboardAsStringWithReplace (ref, "c:\\\\", "", "\\", "/");
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace ();
                ref.println ("Clipboard error!");
            }
            Helper.sleep (1000);
            Helper.performAction (testNode, BindingCopyAction.class);
            Helper.sleep (1000);
            ref.println ("Binding copy code on node: TestNode");
            try {
    //                Helper.printClipboardAsString (ref);
                Helper.printClipboardAsStringWithReplace (ref, "c:\\\\", "", "\\", "/");
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace ();
                ref.println ("Clipboard error!");
            }
        } finally {
            ref.close();
            log.close();
        }
        compareReferenceFiles();
    }
}
