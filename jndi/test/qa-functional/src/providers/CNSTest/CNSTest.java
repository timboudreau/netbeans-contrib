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

package providers.CNSTest;

import java.io.*;
import java.util.ArrayList;

import java.awt.datatransfer.*;
import java.rmi.*;
import java.rmi.registry.*;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.execution.*;
import org.openide.util.datatransfer.*;
import org.openide.util.actions.*;
import org.netbeans.modules.jndi.*;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.netbeans.modules.jndi.utils.DisconnectCtxCookie;
import org.openide.actions.DeleteAction;
import org.openide.filesystems.Repository;
import org.openide.util.Lookup;

public class CNSTest extends org.netbeans.junit.NbTestCase {
    
    public static String name = "CNSCtx";
    public static String dirname = "CNSTestContext.CNSTestContextKind";
    public static String bindname = "CNSBinding.CNSBindingKind";
    
    public CNSTest (String name) {
        super (name);
    }
    
    public static Node findSubNode(Node node, String name) {
        Node[] nodes = node.getChildren().getNodes(true);
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
        ExClipboard clip = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
        Transferable str = (Transferable) clip.getContents(null);
        return str.getTransferData(DataFlavor.stringFlavor).toString ();
    }
    
    public void printClipboardToRef () {
        try {
            String str = getStringFromClipboard();
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
        for (int a = 0; a < 30; a ++) {
            try { Thread.sleep(1000); } catch (Exception e) { }
            Node n = findSubNode(node, name);
            if (n != null)
                return n;
        }
        return null;
    }
    
    public boolean waitNoSubNode(Node node, String name) {
        for (int a = 0; a < 30; a ++) {
            try { Thread.sleep(1000); } catch (Exception e) { }
            if (findSubNode(node, name) == null)
                return true;
        }
        return false;
    }
    
    public boolean waitNoPleaseWait(Node node) {
        return waitNoSubNode (node, "Please wait");
    }

    PrintStream ref;
    PrintStream log;
    
    public void testAll_CNS () throws Exception {
        
        name += new SimpleDateFormat ("yyyyMMddHHmmss").format (new Date (System.currentTimeMillis()));
        
        ref = getRef ();
        log = getLog ();
        
        Process process = null;
    
        try {
            Node jndiNode = JndiRootNode.getDefault();
            if (jndiNode == null)
                throw new RuntimeException ("JNDI node does not exists!");
            Node jndiRootNode = jndiNode;
            Node providersNode = waitSubNode(jndiNode, "Providers");
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
            process = Runtime.getRuntime().exec (System.getProperty("java.home") + "/bin/tnameserv -ORBInitialPort 11198");
            ProcessExecutor mp = new ProcessExecutor ();
            FileObject fo = Repository.getDefault ().find ("providers.CNSTest.CNSBinding", "CNSBindingServerMain", "class");
            if (fo == null)
                throw new RuntimeException ("Cannot find servermain fileobject");
            DataObject dao = null;
            try {
                dao = DataObject.find (fo);
            } catch (DataObjectNotFoundException e) {
                e.printStackTrace (ref);
                throw new RuntimeException ("Cannot find servermain dataobject");
            }
            if (mp.execute (dao).result () != 0)
                throw new RuntimeException ("Error during binding CNSBindingImpl");
            
            /* Add new context */
            JndiRootNode.getDefault ().addContext(name, "com.sun.jndi.cosnaming.CNCtxFactory", "iiop://localhost:11198", "CNSTestRoot.CNSTestRootKind", "", "", "", new java.util.Vector());
            Node testNode = findSubNode(jndiRootNode, name);
            if (testNode == null)
                throw new RuntimeException ("Cannot find context: " + name);
            
            performAction (testNode, RefreshAction.class);
            if (!waitNoPleaseWait(testNode))
                throw new RuntimeException ("Under testNode there is \"Please Wait...\" node shown forever. Pass 1");

            Node dirNode = waitSubNode(testNode, dirname);
            if (dirNode == null)
                throw new RuntimeException ("Cannot find context: " + dirname);
            
            /* Print lookup and binding code */
            performAction(dirNode, LookupCopyAction.class);
            ref.println("Lookup copy code on node: " + dirname);
            printClipboardToRef();
            
            performAction(dirNode, BindingCopyAction.class);
            ref.println("Binding copy code on node: " + dirname);
            printClipboardToRef();

            performAction (dirNode, RefreshAction.class);
            Node bindNode = waitSubNode(dirNode, bindname);
            if (bindNode == null)
                throw new RuntimeException ("Could not find testbinding node");

            /* Print lookup and binding code */
            performAction(bindNode, LookupCopyAction.class);
            ref.println("Lookup copy code on node: " + bindname);
            ref.println (replaceAll (getStringFromClipboard(), "CDRInputStream$1", "CDRInputStream_1_0$1"));
            
            Component com = null;
            ref.println ("dirNode.hasCustomizer (): " + testNode.hasCustomizer ());
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

            performAction (bindNode, DeleteAction.class);
            performAction (testNode, RefreshAction.class);
            if (!waitNoPleaseWait(testNode))
                throw new RuntimeException ("Under testNode there is \"Please Wait...\" node shown forever");
            if (!waitNoSubNode(testNode, bindname))
                throw new RuntimeException ("bindNode is still shown");

            // do it !!! - check behaviour (add, delete) of strange name of binded directory
            
        } finally {
//	    try { Thread.sleep (100000); } catch (Exception e) {}
            if (process != null)
                process.destroy();
            ref.close();
            log.close();
        }
        compareReferenceFiles();
    }
    
    public static void main(String[] s) {
        junit.textui.TestRunner.run (new org.netbeans.junit.NbTestSuite (CNSTest.class));
    }

}
