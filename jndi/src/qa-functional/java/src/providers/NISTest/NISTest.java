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

package providers.NISTest;

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
import org.openide.util.Lookup;

public class NISTest extends org.netbeans.junit.NbTestCase {
    
    public static String name = "NISCtx";
    
    public NISTest (String name) {
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
        final SystemAction act = SystemAction.get (action);
        final Node fnode = node;
        javax.swing.SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                act.actionPerformed (new java.awt.event.ActionEvent (fnode, 0, ""));
            }
        });
    }
    
    public static String getStringFromClipboard() throws IOException, UnsupportedFlavorException {
        ExClipboard clip = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
        Transferable str = (Transferable) clip.getContents(null);
        return str.getTransferData(DataFlavor.stringFlavor).toString ();
    }
    
    public void printClipboardToRef () {
        try {
            String str = getStringFromClipboard();
            str = replaceAll (str, System.getProperty ("NIS_SERVER"), "<NIS_SERVER>");
            str = replaceAll (str, System.getProperty ("NIS_CONTEXT"), "<NIS_CONTEXT>");
            str = replaceAll (str, System.getProperty ("NIS_SUB_CONTEXT"), "<NIS_SUB_CONTEXT>");
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
    
    public void testAll_NIS () throws Exception {
        name += new SimpleDateFormat ("yyyyMMddHHmmss").format (new Date (System.currentTimeMillis()));

        String bindname = System.getProperty ("NIS_SUB_CONTEXT");
        
        try {
        ref = getRef ();
        log = getLog ();
        
        Node jndiNode = JndiRootNode.getDefault();
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
        /* Add new context */
        JndiRootNode.getDefault ().addContext(name, "com.sun.jndi.nis.NISCtxFactory", System.getProperty ("NIS_SERVER"), System.getProperty ("NIS_CONTEXT"), "", "", "", new java.util.Vector());
        Node testNode = waitSubNode(jndiRootNode, name);
        if (testNode == null)
            throw new RuntimeException ("Cannot found context: " + name);

        performAction (testNode, RefreshAction.class);
        if (!waitNoPleaseWait(testNode))
            throw new RuntimeException ("Under testNode there is \"Please Wait...\" node shown forever. Pass 1");

        Node bindNode = waitSubNode(testNode, bindname);
        if (bindNode == null)
            throw new RuntimeException ("Could not find testbinding node");

        /* Print lookup and binding code */
        performAction(bindNode, LookupCopyAction.class);
        ref.println("Lookup copy code on node: " + "NISCtx");
        printClipboardToRef();

        performAction(bindNode, BindingCopyAction.class);
        ref.println("Binding copy code on node: " + "NISCtx");
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

        // do it !!! - check behaviour (add, delete) of strange name of binded directory

        } finally {
            ref.close();
            log.close();
        }
        compareReferenceFiles();
    }
    
    public static void main(String[] s) {
        junit.textui.TestRunner.run (new org.netbeans.junit.NbTestSuite (NISTest.class));
    }

}
