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

package providers.LDAPTest;

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
import org.netbeans.jellytools.nodes.Node;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;

public class LDAPTest extends JellyTestCase {
    
    public static String name = "LDAPCtx";
    
    public LDAPTest (String name) {
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
    public static void performAction(Node node, Class action) {
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
            str = replaceAll (str, System.getProperty ("LDAP_SERVER"), "<LDAP_SERVER>");
            str = replaceAll (str, System.getProperty ("LDAP_CONTEXT"), "<LDAP_CONTEXT>");
            str = replaceAll (str, System.getProperty ("LDAP_SUB_CONTEXT"), "<LDAP_SUB_CONTEXT>");
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
*/
    PrintStream ref;
    PrintStream log;
    ExplorerOperator exp;
    
    public void testAll_LDAP () throws Exception {
        String dirname = System.getProperty("LDAP_SUB_CONTEXT");
        name += new SimpleDateFormat ("yyyyMMddHHmmss").format (new Date (System.currentTimeMillis()));
        
        try {
        ref = getRef ();
        log = getLog ();
        
        exp = new ExplorerOperator ();
        Node jndiRootNode = new JNDIRootNode (exp.runtimeTab().tree ());
        if (JndiRootNode.getDefault () == null)
            throw new AssertionFailedError ("JNDI node does not exists!");
        Node providersNode = new Node (jndiRootNode, "Providers");
/*        Node providersNode = waitSubNode(jndiNode, "Providers");
        if (providersNode == null)
            throw new AssertionFailedError ("Providers node does not exists!");*/
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
        /* Add new context */
        JndiRootNode.getDefault ().addContext(name, "com.sun.jndi.ldap.LdapCtxFactory", System.getProperty ("LDAP_SERVER"), System.getProperty ("LDAP_CONTEXT"), "", "", "", new java.util.Vector());
        Node JtestNode = new Node (jndiRootNode, name);
        org.openide.nodes.Node testNode = waitSubNode(JndiRootNode.getDefault(), name);
//        Node testNode = findSubNode(jndiRootNode, name);
        if (testNode == null)
            throw new AssertionFailedError ("Cannot find context: " + name);

        JtestNode.select ();
        new RefreshAction ().perform (JtestNode);
/*        performAction (testNode, RefreshAction.class);
        if (!waitNoPleaseWait(testNode))
            throw new AssertionFailedError ("Under testNode there is \"Please Wait...\" node shown forever. Pass 1");*/

        ContextNode JdirNode = new ContextNode (JtestNode, dirname);
        org.openide.nodes.Node dirNode = waitSubNode(testNode, dirname);
//        Node dirNode = findSubNode(testNode, dirname);
        if (dirNode == null)
            throw new AssertionFailedError ("Cannot find context: " + dirname);

        /* Print lookup and binding code */
//        MainWindowOperator.StatusTextTracer stt = MainWindowOperator.getDefault().getStatusTextTracer();
//        stt.start ();
        StatusDisplayer.getDefault().setStatusText("<Dummy>");
        JdirNode.copyLookupCode();
        MainWindowOperator.getDefault().waitStatusText("Lookup code generated to clipboard.");
//        stt.waitText("Lookup code generated to clipboard.", true);
//        stt.stop ();
//        performAction(dirNode, LookupCopyAction.class);
        ref.println("Lookup copy code on node: " + "<LDAP_SUB_CONTEXT>");
        printClipboardToRef();

//        stt.start ();
        StatusDisplayer.getDefault().setStatusText("<Dummy>");
        JdirNode.copyBindingCode();
        MainWindowOperator.getDefault().waitStatusText("Binding code generated to clipboard.");
//        stt.waitText("Binding code generated to clipboard.", true);
//        stt.stop ();
//        performAction(dirNode, BindingCopyAction.class);
        ref.println("Binding copy code on node: " + "<LDAP_SUB_CONTEXT>");
        printClipboardToRef();

        Component com = null;
        ref.println ("dirNode.hasCustomizer (): " + testNode.hasCustomizer ());
        com = (dirNode.hasCustomizer ()) ? dirNode.getCustomizer () : null;
        if (com != null)
            ref.println ("dirNode.getCustomizer (): " + com.getName());
        else
            ref.println ("dirNode.getCustomizer (): null");

        // do it !!! - check behaviour (add, delete) of strange name of binded directory

        } finally {
            ref.close();
            log.close();
        }
        compareReferenceFiles();
    }
    
    public static void main(String[] s) {
        junit.textui.TestRunner.run (new org.netbeans.junit.NbTestSuite (LDAPTest.class));
    }

}
