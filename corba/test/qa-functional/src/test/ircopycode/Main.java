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

package test.ircopycode;

import util.*;

import java.io.*;
import org.openide.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.netbeans.modules.corba.*;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jellytools.modules.corba.dialogs.AddInterfaceRepositoryDialog;
import org.netbeans.jellytools.modules.corba.nodes.CORBAInterfaceRepositoryNode;
import org.netbeans.jellytools.modules.corba.nodes.IDLNode;
import org.netbeans.jellytools.modules.corba.nodes.InterfaceRepositoryNode;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Timeouts;
import util.Environment;

public class Main extends JellyTestCase {
    
    public Main (String name) {
        super (name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testIR_CopyCode"));
        return test;
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    ExplorerOperator exp = null;
    EventTool ev = null;
    PrintStream out = null;
    Timeouts time;
    
    public void setUp () {
        time = JemmyProperties.getCurrentTimeouts ();
        Timeouts t = new Timeouts ();
        try { t.loadDebugTimeouts (); } catch (IOException e) {}
        JemmyProperties.setCurrentTimeouts (t);
        exp = new ExplorerOperator ();
        ev = new EventTool ();
        out = getRef ();
        closeAllModal = true;
//        dumpScreen = true;
//        css = (CORBASupportSettings) CORBASupportSettings.findObject(CORBASupportSettings.class, true);
//        assertNotNull ("Cannot find CORBASupportSettings class", css);
    }
    
    public void tearDown () {
        JemmyProperties.setCurrentTimeouts (time);
    }
	
    public void comparePropertySets (Node.PropertySet[] p1, Node.PropertySet[] p2) {
        if (p1.length != p2.length) {
            out.println ("ERROR: Property sets have not equal children count");
            return;
        }
        for (int a = 0; a < p1.length; a ++) {
            out.println ("  " + p1[a].getName () + " -- " + p2[a].getName ());
            if (!p1[a].getName ().equals (p2[a].getName ())) {
                out.println ("ERROR: " + p1[a].getName () + " and " + p2[a].getName () + " property sets have not equal names");
                continue;
            }
            Node.Property[] np1 = p1[a].getProperties ();
            Node.Property[] np2 = p2[a].getProperties ();
            if (np1.length != np2.length) {
                out.println ("ERROR: " + p1[a].getName () + " and " + p2[a].getName () + " property sets have not equal children count");
                continue;
            }
            for (int b = 0; b < np1.length; b ++) {
                out.println("    " + np1[b].getName () + " vs. " + np2[b].getName ());
                if (!np1[b].getName ().equals (np2[b].getName ()))
                    out.println ("ERROR: " + np1[b].getName () + " and " + np2[b].getName () + " property have not equal names");
                try {
                    if (!np1[b].getValue ().toString ().equals (np2[b].getValue ().toString ()))
                        out.println ("ERROR: " + np1[b].getValue ().toString () + "(" + np1[b].getName () + ")" + " and " + np2[b].getValue ().toString () + "(" + np2[b].getName () + ")" + " property have not equal values");
                } catch (IllegalAccessException e) {
                    e.printStackTrace (out);
                } catch (InvocationTargetException e) {
                    e.printStackTrace (out);
                }
            }
        }
    }
    
    public String getNameProperty (Node node) {
        Node.PropertySet ps[] = node.getPropertySets ();
        if (ps == null)
            return null;
        for (int a = 0; a < ps.length; a ++) {
            Node.Property[] p = ps[a].getProperties ();
            if (p == null)
                continue;
            for (int b = 0; b < p.length; b ++)
                if ("name".equalsIgnoreCase(p[b].getName ())) {
                    try {
                        if (p[b].getValue () == null)
                            return "<NULL>";
                        return p[b].getValue ().toString ();
                    } catch (Exception e) {
                        e.printStackTrace(out);
                        return "<NULL>";
                    }
                }
        }
        return null;
    }
    
    public void compareProperties (Node n1, Node n2) {
        Node[] an1 = n1.getChildren ().getNodes ();
        java.util.Arrays.sort(an1, new java.util.Comparator () {
            public int compare (Object o1, Object o2) {
                Node n1 = (Node) o1;
                Node n2 = (Node) o2;
                return getNameProperty (n1).compareTo (getNameProperty (n2));
            }
        });
        Node[] an2 = n2.getChildren ().getNodes ();
        java.util.Arrays.sort(an2, new java.util.Comparator () {
            public int compare (Object o1, Object o2) {
                Node n1 = (Node) o1;
                Node n2 = (Node) o2;
                return getNameProperty (n1).compareTo (getNameProperty (n2));
            }
        });
        out.println(getNameProperty (n1) + " vs. " + getNameProperty (n2));
        out.println("======");
        for (int a = 0; a < an1.length; a ++)
            out.println(getNameProperty (an1[a]) + " !!! " + getNameProperty (an2[a]));
        out.println("------");
        if (an1.length != an2.length) {
            out.println ("ERROR: " + n1.getName () + " and " + n2.getName () + " nodes have not equal children count");
            return;
        }
        for (int a = 0; a < an1.length; a ++) {
            if (!an1[a].getName ().equals (an2[a].getName ())) {
                out.println ("ERROR: " + n1.getName () + " and " + n2.getName () + " nodes have not equal names");
                continue;
            }
            comparePropertySets (an1[a].getPropertySets (), an2[a].getPropertySets ());
            compareProperties (an1[a], an2[a]);
        }
    }
    
    String idlpackage = "data/ir";
    String idlpackagenode = "|data|ir";
    
    public void perform (String idlname, String ior) {
        
        assertTrue ("Invalid IOR: " + ior, ior != null  &&  ior.startsWith ("IOR:"));
        
        String irname = "IRCopyCodeTest_" + new SimpleDateFormat ("yyyyMMddHHmmss").format (new Date (System.currentTimeMillis()));
        out.println ("Adding IR");
        new CORBAInterfaceRepositoryNode (exp.runtimeTab().tree ()).addInterfaceRepository();
        AddInterfaceRepositoryDialog addir = new AddInterfaceRepositoryDialog ();
        addir.setName (irname);
        addir.loadIOR(ior);
        addir.oK ();
        new InterfaceRepositoryNode (exp.runtimeTab().tree (), "|" + irname);
        out.println ("Done");
        Helper.sleep (1000);
        new InterfaceRepositoryNode (exp.runtimeTab().tree (), "|" + irname).copyCode ();
        Helper.sleep (1000);
        MainWindowOperator.getDefault().waitStatusText("Code generated to Clipboard.");
        Helper.sleep (1000);
        ev.waitNoEvent(1000);
        
//        FileObject foIDL = Environment.findFileObject (idlpackage + "/" + idlname + "_SOURCE.idl");
//        assertNotNull ("File not found: File: " + idlpackage + "/" + idlname + "_SOURCE.idl", foIDL);

        String strIDL = Helper.getStringFromClipboard();
        assertNotNull ("Cannot get string from clipboard", strIDL);
        
        NewWizardOperator.create ("CORBA|Empty", idlpackagenode, idlname + "_COPYCODE");
        new EditorWindowOperator ().getEditor (idlname + "_COPYCODE");
        Helper.sleep (1000);
        ev.waitNoEvent(1000);
        new IDLNode (exp.repositoryTab().tree (), idlpackagenode + "|" + idlname + "_COPYCODE");
        Helper.sleep (1000);
        new IDLNode (exp.repositoryTab().tree (), idlpackagenode + "|" + idlname + "_COPYCODE").open ();
        Helper.sleep (1000);
        ev.waitNoEvent(1000);
        EditorOperator eo = new EditorWindowOperator ().getEditor (idlname + "_COPYCODE");
        Helper.sleep (1000);
        ev.waitNoEvent(1000);
        eo.txtEditorPane().setText(strIDL);
        eo.waitModified(true);
        eo.close (true);
        
        FileObject foOriginal = Environment.findFileObject (idlpackage + "/" + idlname + "_SOURCE.idl");
        assertNotNull ("File not found: File: " + idlpackage + "/" + idlname + "_SOURCE.idl", foOriginal);
        IDLDataObject doOriginal = (IDLDataObject) Environment.getDataObject (foOriginal);

        doOriginal.parse();
        if (doOriginal.getStatus() != 0)
            assertTrue("Parser error on doOriginal: Status: " + doOriginal.getStatus (), false);
        
        FileObject foNew = Environment.findFileObject (idlpackage + "/" + idlname + "_COPYCODE.idl");
        assertNotNull ("File not found: File: " + idlpackage + "/" + idlname + "_COPYCODE.idl", foNew);
        IDLDataObject doNew = (IDLDataObject) Environment.getDataObject (foNew);

        doNew.parse();
        if (doNew.getStatus() != 0)
            assertTrue("Parser error on doNew: Status: " + doNew.getStatus (), false);
        
        compareProperties (doOriginal.getNodeDelegate (), doNew.getNodeDelegate ());
        Helper.sleep (1000);
        ev.waitNoEvent(1000);

        out.println ("Removing IR");
        new InterfaceRepositoryNode (exp.runtimeTab().tree (), "|" + irname).removeInterfaceRepository ();
        out.println ("Done");
    }
    
    public void testIR_CopyCode () {
        perform ("01", System.getProperty ("IR_IOR"));
        compareReferenceFiles();
    }
    
}
