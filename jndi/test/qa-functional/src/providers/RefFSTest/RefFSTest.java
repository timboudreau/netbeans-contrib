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

package providers.RefFSTest;

import java.io.*;
import java.util.ArrayList;

import java.awt.datatransfer.*;

import org.openide.TopManager;
import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.util.datatransfer.*;
import org.openide.util.actions.*;
import org.netbeans.modules.jndi.*;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RefFSTest extends org.netbeans.junit.NbTestCase {
    
    public static String name = "RefFSCtx";
    public static String dirname = "RefFSTestDir";
    public static String bindname = "RefFSTestBinding";
    
    public RefFSTest (String name) {
        super (name);
    }
    
    public static Node findSubNode(Node node, String name) {
        Node[] nodes = node.getChildren().getNodes();
        for (int a = 0; a < nodes.length; a ++)
            if (nodes [a].getName().startsWith(name))
                return nodes [a];
        return null;
    }
    
    public static void performAction(Node node, Class action) {
        SystemAction act = SystemAction.get(action);
        act.actionPerformed(new java.awt.event.ActionEvent(node, 0, ""));
    }
    
    public static String getStringFromClipboard() throws IOException, UnsupportedFlavorException {
        ExClipboard clip = TopManager.getDefault().getClipboard();
        Transferable str = (Transferable) clip.getContents(null);
        return str.getTransferData(DataFlavor.stringFlavor).toString ();
    }
    
    public void printClipboardToRef () {
        try {
            String str = getStringFromClipboard();
            str = replaceAll(str, "c:\\\\", "");
            str = replaceAll(str, "\\", "/");
//            str = replaceAll(str, "\"temp\"", "\"tmp\"");
            str = replaceAll(str, getWorkDirPath(), "");
            ref.println(str);
        } catch (IOException e) {
            ref.println("Clipboard error!");
            e.printStackTrace(ref);
        } catch (UnsupportedFlavorException e) {
            ref.println("Clipboard error!");
            e.printStackTrace(ref);
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
    
    public Node waitSubNode(Node node, String name) {
        for (int a = 0; a < 20; a ++) {
            try { Thread.sleep(1000); } catch (Exception e) { }
            Node n = findSubNode(node, name);
            if (n != null)
                return n;
        }
        return null;
    }
    
    public boolean waitNoSubNode(Node node, String name) {
        for (int a = 0; a < 10; a ++) {
            try { Thread.sleep(1000); } catch (Exception e) { }
            if (findSubNode(node, name) == null)
                return true;
        }
        return false;
    }
    
    public boolean waitNoPleaseWait(Node node) {
        return waitNoSubNode (node, "Please wait");
    }

    public static boolean deleteDirCore (File dir) {
        File[] files = dir.listFiles();
        if (files == null)
            return true;
        for (int a = 0; a < files.length; a ++)
            if (!".".equals (files[a])  &&  !"..".equals (files[a])) {
                if (files[a].isDirectory())
                    if (!deleteDirCore (files[a]))
                        return false;
                if (!files[a].delete ())
                    return false;
            }
        return false;
    }
    
    public static boolean deleteDir (String dir) {
        return deleteDirCore (new File (dir));
    }
    
    PrintStream ref;
    PrintStream log;
    
    public void testAll () throws Exception {
        name += new SimpleDateFormat ("yyyyMMddHHmmss").format (new Date (System.currentTimeMillis()));
        
        try {
        ref = getRef ();
        log = getLog ();
        
        Node runtimeNode = TopManager.getDefault().getPlaces().nodes().environment();
        Node jndiNode = findSubNode(runtimeNode, "JNDI");
        if (jndiNode == null)
            throw new RuntimeException ("JNDI node does not exists!");
        Node jndiRootNode = jndiNode;
        Node providersNode = findSubNode(jndiNode, "Providers");
        if (providersNode == null)
            throw new RuntimeException ("Providers node does not exists!");
/*
        for (;;) {
            Node node = findSubNode(jndiRootNode, name);
            if (node == null)
                break;
            if (node.getCookie(DisconnectCtxCookie.class) != null)
                performAction(node, DisconnectAction.class);
            else
                performAction(node, DeleteAction.class);
        }
*/
        String osName = System.getProperty("os.name");
        String context = ((osName.startsWith("Windows")) ? "file:///" : "file://") + getWorkDirPath();
        String root = "tmp";
        String tempDir = getWorkDirPath () + "/tmp";
        getLog ().println(context);
        getLog ().println(tempDir);
/*        String context = (osName.startsWith("Windows")) ? "file:///c:\\" : "file:///";
        String root = (osName.startsWith("Windows")) ? "temp" : "opt/tmp";
        String tempDir = (osName.startsWith("Windows")) ? "c:/temp" : "/opt/tmp";*/

        deleteDir (tempDir + "/" + dirname);

        /* Add new context */
        JndiRootNode.getDefault ().addContext(name, "com.sun.jndi.fscontext.RefFSContextFactory", context, root, "", "", "", new java.util.Vector());
        Node testNode = findSubNode(jndiRootNode, name);
        if (testNode == null)
            throw new RuntimeException ("Cannot found context: " + name);

        performAction (testNode, RefreshAction.class);
        if (!waitNoPleaseWait(testNode))
            throw new RuntimeException ("Under testNode there is \"Please Wait...\" node shown forever. Pass 1");
        new File (tempDir, dirname).mkdirs();
	testNode = waitSubNode(jndiRootNode, name);
        if (testNode == null)
            throw new RuntimeException ("Cannot found context 2: " + name);
        performAction (testNode, RefreshAction.class);
        if (!waitNoPleaseWait(testNode))
            throw new RuntimeException ("Under testNode there is \"Please Wait...\" node shown forever. Pass 2");
	testNode = waitSubNode(jndiRootNode, name);
        if (testNode == null)
            throw new RuntimeException ("Cannot found context 3: " + name);
        Node dirNode = waitSubNode(testNode, dirname);
        if (dirNode == null)
            throw new RuntimeException ("Could not find testdir node");

        /* Print lookup and binding code */
        performAction(dirNode, LookupCopyAction.class);
        ref.println("Lookup copy code on node: " + dirname);
        printClipboardToRef();

        performAction(dirNode, BindingCopyAction.class);
        ref.println("Binding copy code on node: " + dirname);
        printClipboardToRef();

        FileWriter fw = null;
        try {
            fw = new FileWriter (tempDir + "/" + dirname + "/.bindings");
            fw.write(bindname + "/ClassName=java.lang.String\n");
            fw.close ();
        } catch (Exception e) {
            ref.println ("Could not write .binding file");
            e.printStackTrace (ref);
            throw e;
        }
        performAction (dirNode, RefreshAction.class);
        if (!waitNoPleaseWait(dirNode))
            throw new RuntimeException ("Under dirNode there is \"Please Wait...\" node shown forever");
        Node bindNode = waitSubNode(dirNode, bindname);
        if (bindNode == null)
            throw new RuntimeException ("Could not find testbinding node");

        /* Print lookup and binding code */
        performAction(bindNode, LookupCopyAction.class);
        ref.println("Lookup copy code on node: " + bindname);
        printClipboardToRef();

        Component com = null;
        ref.println ("dirNode.hasCustomizer (): " + dirNode.hasCustomizer ());
        com = (dirNode.hasCustomizer ()) ? dirNode.getCustomizer () : null;
        if (com != null)
            ref.println ("dirNode.getCustomizer (): " + com.getName());
        else
            ref.println ("dirNode.getCustomizer (): null");
        ref.println ("bindNode.hasCustomizer (): " + bindNode.hasCustomizer ());
        com = (bindNode.hasCustomizer ()) ? bindNode.getCustomizer () : null;
        if (com != null)
            ref.println ("bindNode.getCustomizer (): " + com.getName());
        else
            ref.println ("bindNode.getCustomizer (): null");

/* !!! problem with DeleteAction - bindNode needs to be selected
        performAction (bindNode, DeleteAction.class);
        performAction (dirNode, RefreshAction.class);
        if (!waitNoPleaseWait(dirNode))
            throw new RuntimeException ("Under dirNode there is \"Please Wait...\" node shown forever");
        if (!waitNoSubNode(dirNode, bindname))
            throw new RuntimeException ("bindNode is still shown");
*/
        // do it !!! - check behaviour (add, delete) of strange name of binded directory

        } finally {
            ref.close();
            log.close();
        }
        compareReferenceFiles();
    }
    
    public static void main(String[] s) {
        junit.textui.TestRunner.run (new org.netbeans.junit.NbTestSuite (RefFSTest.class));
    }

}
