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

package gui.RefFSTest;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.modules.jndi.AddPropertyDialog;
import org.netbeans.jellytools.modules.jndi.ChangePropertyDialog;
import org.netbeans.jellytools.modules.jndi.ConfirmObjectDeletionDialog;
import org.netbeans.jellytools.modules.jndi.nodes.JNDIRootNode;
import org.netbeans.jellytools.modules.jndi.NewContextDialog;
import org.netbeans.jellytools.modules.jndi.NewJNDIContextDialog;
import org.netbeans.jellytools.modules.jndi.nodes.ContextNode;
import org.netbeans.jellytools.modules.jndi.nodes.ObjectNode;
import org.netbeans.jellytools.modules.jndi.nodes.RootContextNode;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jellytools.properties.PropertySheetTabOperator;
import org.netbeans.jellytools.properties.StringProperty;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.junit.AssertionFailedErrorException;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExClipboard;

public class RefFSTest extends JellyTestCase {
    
    public RefFSTest (String name) {
        super (name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new RefFSTest("testGUI_RefFS"));
        return test;
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    ExplorerOperator exp = null;
//    EventTool ev = null;
    PrintStream out = null;
    PrintStream log = null;
    Timeouts time;
    
    public void setUp () {
        time = JemmyProperties.getCurrentTimeouts ();
        Timeouts t = new Timeouts ();
        try { t.loadDebugTimeouts (); } catch (IOException e) {}
        JemmyProperties.setCurrentTimeouts (t);
        exp = new ExplorerOperator ();
//        ev = new EventTool ();
        out = getRef ();
        log = getLog ();
        closeAllModal = true;
//        dumpScreen = true;
    }
    
    public void tearDown () {
        JemmyProperties.setCurrentTimeouts (time);
    }
    
    public static void sleep (int delay) {
        try { Thread.sleep (delay); } catch (InterruptedException e) {}
    }

    public static String getStringFromClipboard() {
        try {
            ExClipboard clip = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
            Transferable str = (Transferable) clip.getContents(null);
            Object o = str.getTransferData(DataFlavor.stringFlavor);
            return o.toString ();
        } catch (NullPointerException e) {
            throw new AssertionFailedErrorException ("NPE while getting string from clipboard", e);
        } catch (IOException e) {
            throw new AssertionFailedErrorException ("IOException while getting string from clipboard", e);
        } catch (UnsupportedFlavorException e) {
            throw new AssertionFailedErrorException ("UnsupportedFlavorException while getting string from clipboard", e);
        }
    }
    
    public static String replaceAll(String str, String from, String to) {
        for (;;) {
            int index = str.indexOf(from);
            if (index < 0)
                break;
            str = str.substring(0, index) + to + str.substring(index + from.length());
        }
        return str;
    }
    
    public void printClipboard () {
        String str = getStringFromClipboard();
        str = replaceAll(str, "\\\\", "/");
        try {
        str = replaceAll(str, replaceAll (getWorkDirPath(), "\\", "/"), "");
        } catch (IOException e) {
            throw new AssertionFailedErrorException ("IOException during getWorkDirPath()", e);
        }
        str = replaceAll(str, "file:///", "file://");
        out.println ("---- Clipboard ----");
        out.println(str);
        out.println ("---- End ----");
    }
    
    public static void closeAllProperties () {
        for (;;) {
            javax.swing.JComponent co = TopComponentOperator.findTopComponent ("Propert", 0);
            if (co == null)
                break;
            new TopComponentOperator(co).close ();
            sleep (1000);
        }
    }
    
    String repo = "RefFS";
    String rootpart;
    String root;
    String init = "init";
    String bindname = "Object";
    String filename = "file";
    String directory = "directory";
    String newdirectory = "New";
    
    public void testGUI_RefFS () {
        boolean winOS = System.getProperty("os.name").startsWith ("Windows");
        repo += new SimpleDateFormat ("yyyyMMddHHmmss").format (new Date (System.currentTimeMillis()));
        try {
            rootpart = getWorkDirPath();
        } catch (Exception e) {
            e.printStackTrace(log);
            assertTrue ("IOException during getWorkDirPath", false);
        }
        root = rootpart + "/" + init;
        log.println ("RootPart: " + rootpart);
        
        out.println ("Preparing");
        assertTrue ("Error while creating Root directory", new File (root).mkdirs ());
        assertTrue ("Error while creating Root/directory directory", new File (root + "/" + directory).mkdirs ());
        try {
            new File (root + "/" + directory + "/" + filename).createNewFile();
        } catch (IOException e) {
           throw new AssertionFailedErrorException ("Error while creating Root/directory/file file", e);
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter (root + "/.bindings");
            fw.write(bindname + "/ClassName=java.lang.String\n");
            fw.close ();
        } catch (Exception e) {
            e.printStackTrace (log);
            assertTrue ("Could not write .bindings file", false);
        }
        out.println ("Done");
        
        out.println ("Adding JNDI Context");
        new JNDIRootNode (exp.runtimeTab ().tree ()).addContext ();
        NewJNDIContextDialog nj = new NewJNDIContextDialog ();
        nj.typeContextLabel (repo);
        nj.selectJNDIContextFactory (NewJNDIContextDialog.ITEM_COMSUNJNDIFSCONTEXTREFFSCONTEXTFACTORY);
        nj.txtJNDIInitialContext ().clearText();
        nj.typeJNDIInitialContext (((winOS) ? "file:///" : "file://") + rootpart);
        nj.typeContextRoot (init);
        nj.add();
       
        out.println ("Adding Invalid Property");
        AddPropertyDialog add = new AddPropertyDialog ();
        add.typePropertyName("InvalidPropertyName");
        add.typePropertyValue("InvalidPropertyValue");
        add.ok();
        nj.lstOtherProperties ().selectItem ("InvalidPropertyName=InvalidPropertyValue");
        out.println ("Done");
        out.println ("Adding Property To Delete");
        nj.add();
        add = new AddPropertyDialog ();
        add.typePropertyName("PropertyToDelete");
        add.typePropertyValue("PropertyToDeleteValue");
        add.ok();
        nj.lstOtherProperties ().selectItem ("PropertyToDelete=PropertyToDeleteValue");
        out.println ("Done");
        out.println ("Editing Invalid Property");
        nj.lstOtherProperties ().selectItem ("InvalidPropertyName=InvalidPropertyValue");
        nj.edit ();
        ChangePropertyDialog ch = new ChangePropertyDialog ();
        ch.txtPropertyName ().clearText();
        ch.typePropertyName("PropertyName");
        ch.txtPropertyValue ().clearText();
        ch.typePropertyValue("PropertyValue");
        ch.ok();
        nj.lstOtherProperties ().selectItem ("PropertyName=PropertyValue");
        out.println ("Done");
        out.println ("Removing Property To Delete");
        nj.lstOtherProperties ().selectItem ("PropertyToDelete=PropertyToDeleteValue");
        nj.remove();
        out.println ("Done");
        nj.ok ();

        new RootContextNode (exp.runtimeTab ().tree (), "|" + repo);
        out.println ("Done");

        out.println ("Exploring property");
        closeAllProperties ();
        new RootContextNode (exp.runtimeTab ().tree (), "|" + repo);
        sleep (1000);
        new RootContextNode (exp.runtimeTab ().tree (), "|" + repo).properties();
        PropertySheetOperator pso = new PropertySheetOperator (PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, repo);
        PropertySheetTabOperator psto = new PropertySheetTabOperator (pso, "Properties");
        StringProperty sp = new StringProperty (psto, "PropertyName");
        out.println ("Value of PropertyName: " + sp.getStringValue());
        out.println ("Done");
        pso.close ();

        repo = "|" + repo;
        
        out.println ("Adding context");
        new RootContextNode (exp.runtimeTab ().tree (), repo).addDirectory();
        NewContextDialog nc = new NewContextDialog ();
        nc.typeContextName(newdirectory);
        nc.ok ();
        sleep (5000);
        new ContextNode (exp.runtimeTab ().tree (), repo + "|" + directory);
        out.println ("Done");
        
        out.println ("Deleting empty directory");
        sleep (5000);
        new ContextNode (exp.runtimeTab ().tree (), repo + "|" + newdirectory).delete ();
        new ConfirmObjectDeletionDialog ().yes();
        out.println ("Done");
        
        out.println ("Deleting non-empty directory");
        sleep (5000);
        new ObjectNode (exp.runtimeTab ().tree (), repo + "|" + directory + "|" + filename);
        sleep (1000);
        new ContextNode (exp.runtimeTab().tree (), repo + "|" + directory);
        sleep (1000);
        new ContextNode (exp.runtimeTab().tree (), repo + "|" + directory).delete ();
        new ConfirmObjectDeletionDialog ().yes();
        new NbDialogOperator ("Exception").ok();
        out.println ("Done");
        
        out.println ("Exploring bind object");
        sleep (5000);
        new ObjectNode (exp.runtimeTab ().tree (), repo + "|" + bindname);
        sleep (1000);
        new ObjectNode (exp.runtimeTab ().tree (), repo + "|" + bindname).copyLookupCode();
        sleep (5000);
        printClipboard();
        out.println ("Done");
        
        out.println ("Exploring file object");
        sleep (5000);
        new ObjectNode (exp.runtimeTab ().tree (), repo + "|" + directory + "|" + filename);
        sleep (1000);
        new ObjectNode (exp.runtimeTab ().tree (), repo + "|" + directory + "|" + filename).copyLookupCode();
        sleep (5000);
        printClipboard();
        out.println ("Done");
        
        out.println ("Exploring directory context");
        sleep (5000);
        new ContextNode (exp.runtimeTab ().tree (), repo + "|" + directory);
        sleep (1000);
        new ContextNode (exp.runtimeTab ().tree (), repo + "|" + directory).copyBindingCode();
        sleep (5000);
        printClipboard();
        new ContextNode (exp.runtimeTab ().tree (), repo + "|" + directory).copyLookupCode();
        sleep (5000);
        printClipboard();
        out.println ("Done");
        
        out.println ("Deleting bind object");
        sleep (5000);
        new ObjectNode (exp.runtimeTab ().tree (), repo + "|" + bindname).delete();
        new ConfirmObjectDeletionDialog ().yes();
        out.println ("Done");
        
        out.println ("Deleting file object");
        sleep (5000);
        new ObjectNode (exp.runtimeTab ().tree (), repo + "|" + directory + "|" + filename);
        sleep (1000);
        new ObjectNode (exp.runtimeTab ().tree (), repo + "|" + directory + "|" + filename).delete();
        new ConfirmObjectDeletionDialog ().yes();
        out.println ("Done");
        
        out.println ("Disconnecting");
        sleep (5000);
        new RootContextNode (exp.runtimeTab ().tree (), repo).disconnectContext();
        out.println ("Done");
        
        compareReferenceFiles();
    }
    
}
