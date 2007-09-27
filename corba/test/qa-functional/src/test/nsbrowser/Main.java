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

package test.nsbrowser;

import java.io.IOException;
import javax.swing.JDialog;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.modules.corba.nodes.CORBANamingServiceNode;
import org.netbeans.jellytools.modules.corba.nodes.NamingContextNode;
import org.netbeans.jellytools.modules.corba.nodes.NamingObjectNode;
import org.netbeans.jellytools.modules.corba.dialogs.BindNewDialog;
import org.netbeans.jellytools.modules.corba.dialogs.CreateContextDialog;
import org.netbeans.jellytools.modules.corba.dialogs.StartLocalDialog;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jellytools.properties.PropertySheetTabOperator;
import org.netbeans.jellytools.properties.StringProperty;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyException;
//import org.netbeans.jemmy.drivers.DriverManager;
//import org.netbeans.jemmy.drivers.TreeDriver;
//import org.netbeans.jemmy.drivers.trees.JTreeAPIDriver;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.openide.execution.Executor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import util.Environment;
import util.Helper;
import util.JHelper;
import org.netbeans.modules.corba.settings.CORBASupportSettings;

public class Main extends JellyTestCase {

    public Main(String name) {
        super(name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testNS_Error"));
        test.addTest(new Main("testNS_Local"));
        test.addTest(new Main("testNS_Create"));
        test.addTest(new Main("testNS_GetIOR"));
        test.addTest(new Main("testNS_BindServer"));
        test.addTest(new Main("testNS_CopyServer"));
        test.addTest(new Main("testNS_CopyClient"));
        test.addTest(new Main("testNS_Second"));
        test.addTest(new Main("testNS_Unbind"));
        return test;
    }
    
    ExplorerOperator exp = null;
    JDialog dialog = null;

    static String ior = null;
    
    static boolean itemLocalTest = false;
    static boolean itemLocalTest2 = false;
    static boolean itemLocalTest_NSName = false;
    static boolean itemLocalTest_NSName2 = false;
    static boolean itemLocalTest_NSName2_NSNameSub2 = false;
    static boolean itemLocalTest_NSName_ServerName2 = false;
    
    public void setUp () {
        exp = new ExplorerOperator ();
        closeAllModal = true;
    }
    
    public void testNS_Error () {
        CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject (CORBASupportSettings.class, true); // CORBA bug workaround
        css.getORB ();
		
        new CORBANamingServiceNode (exp.runtimeTab().tree ()).bindNewContext();
        BindNewDialog di = new BindNewDialog ();
        di.cancel ();
        di.waitClosed ();
        
        new CORBANamingServiceNode (exp.runtimeTab().tree ()).bindNewContext();
        di = new BindNewDialog ();
        di.setName ("Test");
        di.oK();
        di.waitClosed ();
        dialog = JDialogOperator.waitJDialog("Error", true, true);
        new JButtonOperator (JButtonOperator.waitJButton (dialog, "OK", true, true)).push ();
        new EventTool().waitNoEvent(1000);
    }
    
    public void testNS_Local () {
        new CORBANamingServiceNode (exp.runtimeTab ().tree ()).startLocal ();
        StartLocalDialog di = new StartLocalDialog ();
        di.setName ("LocalTest");
        di.setNameServicePort("11903");
        di.oK ();
        di.waitClosed ();
        new EventTool().waitNoEvent(1000);
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest"); // CORBA bug workaround
        itemLocalTest = true;
//        dialog = JDialogOperator.waitJDialog("Information", true, true);
//        if (dialog != null)
//            new JButtonOperator (JButtonOperator.waitJButton (dialog, "OK", true, true)).push ();
        
        new CORBANamingServiceNode (exp.runtimeTab ().tree ()).startLocal ();
        di = new StartLocalDialog ();
        di.setName ("LocalTest_2");
        di.setNameServicePort("11903");
        di.oK ();
        di.waitClosed ();
        new EventTool().waitNoEvent(1000);
        dialog = JDialogOperator.waitJDialog("Information", true, true);
        new JButtonOperator (JButtonOperator.waitJButton (dialog, "OK", true, true)).push ();
        new EventTool().waitNoEvent(1000);
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest_2");
        itemLocalTest2 = true;
    }
    
    public void testNS_Create () {
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest").createNewContext();
        CreateContextDialog di = new CreateContextDialog ();
        di.setName ("NSName");
        di.setKind ("NSKind");
        di.oK ();
        di.waitClosed ();

        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest").createNewContext();
        di = new CreateContextDialog ();
        di.setName ("NSName2");
        di.setKind ("NSKind2");
        di.oK ();
        di.waitClosed ();

        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName");
        itemLocalTest_NSName = true;
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2");
        itemLocalTest_NSName2 = true;
    }
    
    public void testNS_GetIOR () {
        JHelper.closeAllProperties ();
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest").properties();
        PropertySheetOperator pso = new PropertySheetOperator (PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, "LocalTest");
        PropertySheetTabOperator pst = pso.getPropertySheetTabOperator("Properties");
        ior = new StringProperty (pst, "IOR").getStringValue();
        assertTrue ("Invalid IOR: " + ior, ior != null  &&  ior.startsWith("IOR:"));
        pso.close();
    }
    
    public void testNS_BindServer () {
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2").bindNewContext ();
        BindNewDialog di = new BindNewDialog ();
        di.setName ("NSNameSub2");
        di.setKind ("NSKindSub2");
        di.loadIOR (ior);
        di.oK ();
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2|NSNameSub2");
        itemLocalTest_NSName2_NSNameSub2 = true;
    }
    
    public void testNS_CopyServer () {
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName").copyServerBindingCode();
        MainWindowOperator.getDefault().waitStatusText("Code was sucessfully generated into clipboard.");
        getRef ().println (Helper.getStringFromClipboard());
        compareReferenceFiles ();
    }
    
    public void testNS_CopyClient () {
        Executor exec = Executor.find ("External Execution");
        FileObject fo = Environment.findFileObject ("test/nsbrowser/bind/HelloSNS.class");
        try {
            exec.execute(DataObject.find (fo)).waitFinished();
        } catch (DataObjectNotFoundException e) {
            assertTrue ("Not found: HelloSNS", false);
        } catch (IOException e) {
            assertTrue ("IOException", false);
        }

//        TreeDriver dm = DriverManager.getTreeDriver (JTreeOperator.class);
//        DriverManager.setTreeDriver(new JTreeAPIDriver ());
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName").refresh();
        new NamingObjectNode (exp.runtimeTab ().tree (), "|LocalTest|NSName|ServerName");
        Helper.sleep (1000);
        new NamingObjectNode (exp.runtimeTab ().tree (), "|LocalTest|NSName|ServerName").copyClientBindingCode ();
//        DriverManager.setTreeDriver (dm);

        MainWindowOperator.getDefault().waitStatusText("Code was sucessfully generated into clipboard.");
        getRef ().println (Helper.getStringFromClipboard());
        compareReferenceFiles ();
    }
    
    public void testNS_Second () {
        JHelper.closeAllProperties ();
//        TreeDriver dm = DriverManager.getTreeDriver (JTreeOperator.class);
//        DriverManager.setTreeDriver(new JTreeAPIDriver ());
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2|NSNameSub2").refresh ();
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2|NSNameSub2|NSName");
        Helper.sleep (1000);
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2|NSNameSub2|NSName").refresh ();
        new NamingObjectNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2|NSNameSub2|NSName|ServerName");
        Helper.sleep (1000);
        new NamingObjectNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2|NSNameSub2|NSName|ServerName").properties ();
//        DriverManager.setTreeDriver (dm);

        PropertySheetOperator pso = new PropertySheetOperator (PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, "ServerName");
        PropertySheetTabOperator pst = pso.getPropertySheetTabOperator("Properties");
        ior = new StringProperty (pst, "IOR").getStringValue();
        assertTrue ("Invalid IOR: " + ior, ior != null  &&  ior.startsWith("IOR:"));
        pso.close();

        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName").bindNewObject();
        BindNewDialog di = new BindNewDialog ();
        di.setName ("ServerName2");
        di.setKind ("ServerKind2");
        di.loadIOR (ior);
        di.oK ();
        di.waitClosed();
        new NamingObjectNode (exp.runtimeTab ().tree (), "|LocalTest|NSName|ServerName2");
        itemLocalTest_NSName_ServerName2 = true;

//        TreeDriver dm = DriverManager.getTreeDriver (JTreeOperator.class);
//        DriverManager.setTreeDriver(new JTreeAPIDriver ());
        new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2|NSNameSub2|NSName").refresh ();
        new NamingObjectNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2|NSNameSub2|NSName|ServerName2");
        Helper.sleep (1000);
        new NamingObjectNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2|NSNameSub2|NSName|ServerName2").copyClientBindingCode ();
//        DriverManager.setTreeDriver (dm);

        MainWindowOperator.getDefault().waitStatusText("Code was sucessfully generated into clipboard.");
        getRef ().println (Helper.getStringFromClipboard());
        compareReferenceFiles ();
    }
    
    public void testNS_Unbind () {
//        TreeDriver dm = DriverManager.getTreeDriver (JTreeOperator.class);
//        DriverManager.setTreeDriver(new JTreeAPIDriver ());
        if (itemLocalTest_NSName_ServerName2) {
            new NamingObjectNode (exp.runtimeTab ().tree (), "|LocalTest|NSName|ServerName2");
            Helper.sleep (1000);
            new NamingObjectNode (exp.runtimeTab ().tree (), "|LocalTest|NSName|ServerName2").unbindObject();
            getLog ().println ("|LocalTest|NSName|ServerName2");
        }
        if (itemLocalTest_NSName2_NSNameSub2) {
            new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2|NSNameSub2");
            Helper.sleep (1000);
            new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2|NSNameSub2").unbindContext();
            getLog ().println ("|LocalTest|NSName2|NSNameSub2");
        }
        if (itemLocalTest_NSName2) {
            new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2");
            Helper.sleep (1000);
            new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName2").unbindContext();
            getLog ().println ("|LocalTest|NSName2");
        }
        if (itemLocalTest_NSName) {
            new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName");
            Helper.sleep (1000);
            new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest|NSName").unbindContext();
            getLog ().println ("|LocalTest|NSName");
        }
        if (itemLocalTest2) {
            new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest_2");
            Helper.sleep (1000);
            new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest_2").unbindContext();
            getLog ().println ("|LocalTest_2");
        }
        if (itemLocalTest) {
            new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest");
            Helper.sleep (1000);
            new NamingContextNode (exp.runtimeTab ().tree (), "|LocalTest").unbindContext();
            getLog ().println ("|LocalTest");
        }
//        DriverManager.setTreeDriver (dm);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
