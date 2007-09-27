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

package providers.RegistryTest;

import java.io.*;
import java.util.ArrayList;

import java.awt.datatransfer.*;
import java.rmi.*;
import java.rmi.registry.*;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
//import org.openide.nodes.*;
import org.openide.actions.*;
//import org.openide.execution.*;
import org.openide.util.datatransfer.*;
import org.openide.util.actions.*;
import org.netbeans.modules.jndi.*;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.AssertionFailedError;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.modules.jndi.actions.RefreshAction;
import org.netbeans.jellytools.modules.jndi.nodes.ContextNode;
import org.netbeans.jellytools.modules.jndi.nodes.JNDIRootNode;
import org.netbeans.jellytools.modules.jndi.nodes.ObjectNode;
import org.netbeans.jellytools.nodes.Node;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;

public class RegistryTest extends JellyTestCase {
    
    public static String name = "RegistryCtx";
    public static String bindname = "RegistryBinding";
    
    public RegistryTest (String name) {
        super (name);
    }
    
    public void failNotify (Throwable th) {
        log.println ("Status Text Tracer history:");
        MainWindowOperator.getDefault().getStatusTextTracer ().printStatusTextHistory (log);
    }
    
    public static org.openide.nodes.Node findSubNode(org.openide.nodes.Node node, String name) {
        org.openide.nodes.Node[] nodes = node.getChildren().getNodes(true);
        for (int a = 0; a < nodes.length; a ++)
            if (nodes [a].getName().startsWith(name))
                return nodes [a];
        return null;
    }
/*    
    public static void performAction(org.openide.nodes.Node node, Class action) {
        SystemAction act = SystemAction.get(action);
        act.actionPerformed(new java.awt.event.ActionEvent(node, 0, ""));
    }
*/    
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
    
    public org.openide.nodes.Node waitSubNode(org.openide.nodes.Node node, String name) {
        for (int a = 0; a < 30; a ++) {
            try { Thread.sleep(1000); } catch (Exception e) { }
            org.openide.nodes.Node n = findSubNode(node, name);
            if (n != null)
                return n;
        }
        return null;
    }
/*    
    public boolean waitNoSubNode(org.openide.nodes.Node node, String name) {
        for (int a = 0; a < 30; a ++) {
            try { Thread.sleep(1000); } catch (Exception e) { }
            if (findSubNode(node, name) == null)
                return true;
        }
        return false;
    }
    
    public boolean waitNoPleaseWait(org.openide.nodes.Node node) {
        return waitNoSubNode (node, "Please wait");
    }
*/
    PrintStream ref;
    PrintStream log;
    ExplorerOperator exp;
    
    public void testAll_Reg () throws Exception {
        name += new SimpleDateFormat ("yyyyMMddHHmmss").format (new Date (System.currentTimeMillis()));
        try {
        ref = getRef ();
        log = getLog ();
        
        exp = new ExplorerOperator ();
        Node jndiRootNode = new JNDIRootNode (exp.runtimeTab().tree ());
        if (JndiRootNode.getDefault () == null)
            throw new AssertionFailedError ("JNDI node does not exists!");
        Node providersNode = new Node (jndiRootNode, "Providers");
/*        org.openide.nodes.Node providersNode = waitSubNode(jndiNode, "Providers");
        if (providersNode == null)
            throw new AssertionFailedError ("Providers node does not exists!");*/
/*
        for (;;) {
            org.openide.nodes.Node node = findSubNode(jndiRootNode, name);
            if (node == null)
                break;
            if (node.getCookie(DisconnectCtxCookie.class) != null)
                performAction(node, DisconnectAction.class);
            else
                performAction(node, DeleteAction.class);
        }
*/
        Registry reg = null;

        try {
            reg = LocateRegistry.getRegistry(11199);
            reg.rebind (bindname, new RegistryBindingImpl ());
        } catch (RemoteException e) {
            log.println ("Starting Registry on port 11199");
            try {
                reg = LocateRegistry.createRegistry(11199);
                reg.rebind (bindname, new RegistryBindingImpl ());
            } catch (RemoteException ee) {
                ee.printStackTrace (ref);
                throw new AssertionFailedError ("Cannot rebind into registry");
            }
        }

        java.util.Properties jndiProperties = new java.util.Properties();
        jndiProperties.put("java.naming.provider.url","rmi://localhost:11199");
        jndiProperties.put("java.naming.factory.initial","com.sun.jndi.rmi.registry.RegistryContextFactory");
        try {
            javax.naming.directory.DirContext jndiCtx = new javax.naming.directory.InitialDirContext(jndiProperties);
            javax.naming.Context jndiObject = (javax.naming.Context)jndiCtx.lookup("");
            jndiObject.rebind(bindname, new RegistryBindingImpl ());
        } catch (javax.naming.NamingException ne) {
            ref.println ("Exception reached while rebinding impl");
            ne.printStackTrace(ref);
            throw new AssertionFailedError ("Cannot rebind impl");
        }

        /* Add new context */
        JndiRootNode.getDefault ().addContext(name, "com.sun.jndi.rmi.registry.RegistryContextFactory", "rmi://localhost:11199", "", "", "", "", new java.util.Vector());
        ContextNode JtestNode = new ContextNode (jndiRootNode, name);
        org.openide.nodes.Node testNode = waitSubNode(JndiRootNode.getDefault(), name);
/*        org.openide.nodes.Node testNode = findSubNode(jndiRootNode, name);
        if (testNode == null)
            throw new AssertionFailedError ("Cannot found context: " + name);*/

        JtestNode.select ();
        new RefreshAction ().perform (JtestNode);
/*        performAction (testNode, RefreshAction.class);
        if (!waitNoPleaseWait(testNode))
            throw new AssertionFailedError ("Under testNode there is \"Please Wait...\" node shown forever. Pass 1");*/

        /* Print lookup and binding code */
//        MainWindowOperator.StatusTextTracer stt = MainWindowOperator.getDefault().getStatusTextTracer();
//        stt.start ();
        StatusDisplayer.getDefault().setStatusText("<Dummy>");
        JtestNode.copyLookupCode();
        MainWindowOperator.getDefault().waitStatusText("Lookup code generated to clipboard.");
//        stt.waitText("Lookup code generated to clipboard.", true);
//        stt.stop ();
//        performAction(testNode, LookupCopyAction.class);
        ref.println("Lookup copy code on node: " + "RegistryCtx");
        printClipboardToRef();

//        stt.start ();
        StatusDisplayer.getDefault().setStatusText("<Dummy>");
        JtestNode.copyBindingCode();
        MainWindowOperator.getDefault().waitStatusText("Binding code generated to clipboard.");
//        stt.waitText("Binding code generated to clipboard.", true);
//        stt.stop ();
//        performAction(testNode, BindingCopyAction.class);
        ref.println("Binding copy code on node: " + "RegistryCtx");
        printClipboardToRef();

        ObjectNode JbindNode = new ObjectNode (JtestNode, bindname);
        org.openide.nodes.Node bindNode = waitSubNode(testNode, bindname);
        if (bindNode == null)
            throw new AssertionFailedError ("Could not find testbinding node");

        /* Print lookup and binding code */
        StatusDisplayer.getDefault().setStatusText("<Dummy>");
//        stt.start ();
        JbindNode.copyLookupCode();
        MainWindowOperator.getDefault().waitStatusText("Lookup code generated to clipboard.");
//        stt.waitText("Lookup code generated to clipboard.", true);
//        stt.stop ();
//        performAction(bindNode, LookupCopyAction.class);
        ref.println("Lookup copy code on node: " + bindname);
        printClipboardToRef();

        Component com = null;
        ref.println ("testNode.hasCustomizer (): " + testNode.hasCustomizer ());
        com = (testNode.hasCustomizer ()) ? testNode.getCustomizer () : null;
        if (com != null)
            ref.println ("testNode.getCustomizer (): " + com.getName());
        else
            ref.println ("testNode.getCustomizer (): null");
        ref.println ("bindNode.hasCustomizer (): " + bindNode.hasCustomizer ());
        com = (bindNode.hasCustomizer ()) ? bindNode.getCustomizer () : null;
        if (com != null)
            ref.println ("bindNode.getCustomizer (): " + com.getName());
        else
            ref.println ("bindNode.getCustomizer (): null");

/* !!! problem with DeleteAction - bindNode needs to be selected
        performAction (bindNode, DeleteAction.class);
        performAction (testNode, RefreshAction.class);
        if (!waitNoPleaseWait(testNode))
            throw new AssertionFailedError ("Under testNode there is \"Please Wait...\" node shown forever");
        if (!waitNoSubNode(testNode, bindname))
            throw new AssertionFailedError ("bindNode is still shown");
*/
        // do it !!! - check behaviour (add, delete) of strange name of binded directory

        } finally {
            ref.close();
            log.close();
        }
        compareReferenceFiles();
    }
    
    public static void main(String[] s) {
        junit.textui.TestRunner.run (new org.netbeans.junit.NbTestSuite (RegistryTest.class));
    }

}
