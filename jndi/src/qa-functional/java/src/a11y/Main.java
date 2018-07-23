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

package a11y;

import org.netbeans.jemmy.*;
import org.netbeans.jemmy.operators.*;
import org.netbeans.test.oo.gui.jam.*;
import org.netbeans.test.oo.gui.jelly.*;
import org.netbeans.jellytools.*;
import org.netbeans.a11y.*;
import java.io.PrintWriter;
import java.awt.Component;
import javax.swing.JDialog;
import java.io.OutputStreamWriter;
import org.netbeans.jellytools.actions.ActionNoBlock;
import org.netbeans.jellytools.nodes.Node;

public class Main extends JellyTestCase {

    public Main (String name) {
        super (name);
    }

    public static junit.framework.Test suite () {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest (new Main ("testAll"));
        return test;
    }
    
    public void testA11Y (Component comp, boolean close) throws Exception {
        new org.netbeans.jemmy.EventTool().waitNoEvent(1000);
        TestSettings ts = new TestSettings ();
        ts.setDefaultSettings();
		ts.tabTraversal = false; //
        if (close)
            ts.setCancelLabel("Close");
        AccessibilityTester at = new AccessibilityTester (comp, ts);
		at.startTests ();
//        at.testProperties();
//        at.testTraversal();
        TextReport treport = new TextReport (at, ts);
        if (comp instanceof JDialog)
            getRef ().println ("---> Testing Dialog: " + ((JDialog) comp).getTitle ());
        else
            getRef ().println ("---> Testing Component");
        OutputStreamWriter osw = new OutputStreamWriter (getRef ());
        treport.getReport(osw);
        osw.flush ();
    }

    /** Use for execution inside IDE */ 
    public static void main(java.lang.String[] args) { 
        junit.textui.TestRunner.run(suite ()); 
    }

    public void testAll() throws Exception {
//        JellyProperties.setDefaults();
//        JemmyProperties.getProperties().setOutput(TestOut.getNullOutput ());
//        JemmyProperties.getProperties().setCurrentDispatchingModel (JemmyProperties.QUEUE_MODEL_MASK | JemmyProperties.ROBOT_MODEL_MASK);
//        JellyProperties.setJemmyDebugTimeouts();
        JDialog testDialog = null;
        JDialog testDialog2 = null;
//        Explorer exp = Explorer.find ();
        ExplorerOperator expl = new ExplorerOperator ();

//        new ComponentOperator (exp.getJFrame ()).activateWindow ();
//        exp.switchToRuntimeTab();
        new ActionNoBlock (null, "Add Context...").perform (new Node (expl.runtimeTab().tree (), "JNDI"));
//        exp.pushPopupMenuNoBlock("Add Context...", "JNDI");
        testDialog = JDialogOperator.waitJDialog ("New JNDI Context", true, true);
        try {
            testA11Y (testDialog, false);
        } finally {
            new JButtonOperator (JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        }

//        new ComponentOperator (exp.getJFrame ()).activateWindow ();
//        exp.switchToRuntimeTab();
        new ActionNoBlock (null, "Add Provider...").perform (new Node (expl.runtimeTab().tree (), "JNDI|Providers"));
//        exp.pushPopupMenuNoBlock("Add Provider...", "JNDI|Providers");
        testDialog = JDialogOperator.waitJDialog ("New Provider", true, true);
        try {
            testA11Y (testDialog, false);
        } finally {
            new JButtonOperator (JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        }

//        new ComponentOperator (exp.getJFrame ()).activateWindow ();
        new ActionNoBlock (null, "Customize").perform (new Node (expl.runtimeTab().tree (), "JNDI|Providers|CNCtxFactory"));
//        exp.pushPopupMenuNoBlock("Customize", "JNDI|Providers|CNCtxFactory");
        testDialog = JDialogOperator.waitJDialog ("Customizer Dialog", true, true);
        try {
            testA11Y (testDialog, true);

            new JamButton (new JamDialog (testDialog), "Add...").doClickNoBlock();
            testDialog2 = JDialogOperator.waitJDialog ("Add Property", true, true);
            try {
                testA11Y (testDialog2, false);
                new JTextFieldOperator (JTextFieldOperator.findJTextField(testDialog2, "", false, false, 0)).typeText("Name");
                new JTextFieldOperator (JTextFieldOperator.findJTextField(testDialog2, "", false, false, 1)).typeText("Value");
                new JButtonOperator (JButtonOperator.findJButton (testDialog2, "OK", true, true)).push ();
            } catch (Exception e) {
                new JButtonOperator (JButtonOperator.findJButton (testDialog2, "Cancel", true, true)).push ();
                throw e;
            }

            new JListOperator (JListOperator.findJList(testDialog, "", false, false, 0)).clickOnItem("Name=Value", true, true);
            testA11Y (testDialog, true);
            new JButtonOperator (JButtonOperator.findJButton (testDialog, "Remove", true, true)).push ();
        } finally {
            new JButtonOperator (JButtonOperator.findJButton (testDialog, "Close", true, true)).push ();
        }

//        new ComponentOperator (exp.getJFrame ()).activateWindow ();
        new ActionNoBlock (null, "Connect Using...").perform (new Node (expl.runtimeTab().tree (), "JNDI|Providers|LdapCtxFactory"));
//        exp.pushPopupMenuNoBlock("Connect Using...", "JNDI|Providers|LdapCtxFactory");
        testDialog = JDialogOperator.waitJDialog ("New JNDI Context", true, true);
        try {
            testA11Y (testDialog, false);

            new JTextFieldOperator (JTextFieldOperator.findJTextField(testDialog, "", false, false, 0)).typeText("CONTEXT");
            new JTextFieldOperator (JTextFieldOperator.findJTextField(testDialog, "", false, false, 2)).clearText();
            new JTextFieldOperator (JTextFieldOperator.findJTextField(testDialog, "", false, false, 2)).typeText(System.getProperty ("LDAP_SERVER"));
            new JTextFieldOperator (JTextFieldOperator.findJTextField(testDialog, "", false, false, 3)).typeText(System.getProperty ("LDAP_CONTEXT"));
            new JamButton (new JamDialog (testDialog), "Add...").doClickNoBlock();

            testDialog2 = JDialogOperator.waitJDialog ("Add Property", true, true);
            try {
                testA11Y (testDialog2, false);
                new JTextFieldOperator (JTextFieldOperator.findJTextField(testDialog2, "", false, false, 0)).typeText("Name");
                new JTextFieldOperator (JTextFieldOperator.findJTextField(testDialog2, "", false, false, 1)).typeText("Value");
                new JButtonOperator (JButtonOperator.findJButton (testDialog2, "OK", true, true)).push ();
            } catch (Exception e) {
                new JButtonOperator (JButtonOperator.findJButton (testDialog2, "Cancel", true, true)).push ();
                throw e;
            }

            new JListOperator (JListOperator.findJList(testDialog, "", false, false, 0)).clickOnItem("Name=Value", true, true);
            testA11Y (testDialog, false);
            new JButtonOperator (JButtonOperator.findJButton (testDialog, "Remove", true, true)).push ();
	    JamUtilities.waitEventQueueEmpty (1000);
        } finally {
            new JButtonOperator (JButtonOperator.findJButton (testDialog, "OK", true, true)).push ();
        }

        try {
//            new ComponentOperator (exp.getJFrame ()).activateWindow ();
              new ActionNoBlock (null, "Customize").perform (new Node (expl.runtimeTab().tree (), "JNDI|CONTEXT"));
//            exp.pushPopupMenuNoBlock("Customize", "JNDI|CONTEXT");
            testDialog = JDialogOperator.waitJDialog ("Customizer Dialog", true, true);
            try {
                testA11Y (testDialog, true);
            } finally {
                new JButtonOperator (JButtonOperator.findJButton (testDialog, "Close", true, true)).push ();
            }

//            new ComponentOperator (exp.getJFrame ()).activateWindow ();
              new ActionNoBlock (null, "Add Directory...").perform (new Node (expl.runtimeTab().tree (), "JNDI|CONTEXT"));
//            exp.pushPopupMenuNoBlock("Add Directory...", "JNDI|CONTEXT");
            testDialog = JDialogOperator.waitJDialog ("New Context", true, true);
            try {
                testA11Y (testDialog, false);
            } finally {
                new JButtonOperator (JButtonOperator.findJButton (testDialog, "Cancel", true, true)).push ();
            }
        } finally {
              new ActionNoBlock (null, "Disconnect Context").perform (new Node (expl.runtimeTab().tree (), "JNDI|CONTEXT"));
//            exp.pushPopupMenuNoBlock("Disconnect Context", "JNDI|CONTEXT");
        }
        compareReferenceFiles();
    }

}
