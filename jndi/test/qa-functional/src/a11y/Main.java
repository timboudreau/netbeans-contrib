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

package a11y;

import org.netbeans.jemmy.*;
import org.netbeans.jemmy.operators.*;
import org.netbeans.test.oo.gui.jam.*;
import org.netbeans.test.oo.gui.jelly.*;
import org.netbeans.a11y.*;
import java.io.PrintWriter;
import java.awt.Component;
import javax.swing.JDialog;
import java.io.OutputStreamWriter;

public class Main extends org.netbeans.junit.NbTestCase {

    public Main (String name) {
        super (name);
    }
    
    public static junit.framework.Test suite () {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest (new Main ("testAll"));
        return test;
    }
    
    public void testA11Y (Component comp, boolean close) throws Exception {
        TestSettings ts = new TestSettings ();
        ts.setDefaultSettings();
        if (close)
            ts.setCancelLabel("Close");
        AccessibilityTester at = new AccessibilityTester (comp, ts);
        at.testProperties();
        at.testTraversal();
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
        JellyProperties.setDefaults();
        JemmyProperties.getProperties().setOutput(TestOut.getNullOutput ());
//        JemmyProperties.getProperties().setCurrentDispatchingModel (JemmyProperties.QUEUE_MODEL_MASK | JemmyProperties.ROBOT_MODEL_MASK);
//        JellyProperties.setJemmyDebugTimeouts();
        JDialog testDialog = null;
        JDialog testDialog2 = null;
        Explorer exp = Explorer.find ();

        new ComponentOperator (exp.getJFrame ()).activateWindow ();
        exp.switchToRuntimeTab();
        exp.pushPopupMenuNoBlock("Add Context...", "JNDI");
        testDialog = JDialogOperator.waitJDialog ("New JNDI Context", true, true);
        try {
            testA11Y (testDialog, false);
        } finally {
            new JButtonOperator (JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        }

        new ComponentOperator (exp.getJFrame ()).activateWindow ();
        exp.switchToRuntimeTab();
        exp.pushPopupMenuNoBlock("Add Provider...", "JNDI|Providers");
        testDialog = JDialogOperator.waitJDialog ("New Provider", true, true);
        try {
            testA11Y (testDialog, false);
        } finally {
            new JButtonOperator (JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        }

        new ComponentOperator (exp.getJFrame ()).activateWindow ();
        exp.pushPopupMenuNoBlock("Customize", "JNDI|Providers|CNCtxFactory");
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

        new ComponentOperator (exp.getJFrame ()).activateWindow ();
        exp.pushPopupMenuNoBlock("Connect Using...", "JNDI|Providers|LdapCtxFactory");
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
            new ComponentOperator (exp.getJFrame ()).activateWindow ();
            exp.pushPopupMenuNoBlock("Customize", "JNDI|CONTEXT");
            testDialog = JDialogOperator.waitJDialog ("Customizer Dialog", true, true);
            try {
                testA11Y (testDialog, true);
            } finally {
                new JButtonOperator (JButtonOperator.findJButton (testDialog, "Close", true, true)).push ();
            }

            new ComponentOperator (exp.getJFrame ()).activateWindow ();
            exp.pushPopupMenuNoBlock("Add Directory...", "JNDI|CONTEXT");
            testDialog = JDialogOperator.waitJDialog ("New Context", true, true);
            try {
                testA11Y (testDialog, false);
            } finally {
                new JButtonOperator (JButtonOperator.findJButton (testDialog, "Cancel", true, true)).push ();
            }
        } finally {
            exp.pushPopupMenuNoBlock("Disconnect Context", "JNDI|CONTEXT");
        }
        compareReferenceFiles();
    }

}
