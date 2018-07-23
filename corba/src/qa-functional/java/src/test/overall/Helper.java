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

package test.overall;

import java.io.*;
import java.awt.datatransfer.*;

import org.openide.nodes.*;
import org.openide.util.Lookup;
import org.openide.util.actions.*;
import org.openide.util.datatransfer.*;

/** Helper class for Basic CORBA Test
 */
public class Helper {

/** Prints node's actions to output stream
 * @param node Node
 * @param out Output stream
 */
    public static void printActions (Node node, PrintStream out) {
        out.println ("Actions of node: " + node.getName ());
        SystemAction[] sa = node.getActions ();
        for (int a = 0; a < sa.length; a ++)
            if (sa [a] != null)
                out.println(sa [a].getName ());
        out.println ();
    }
    
/** Prints node's properties
 * @param node Node
 * @param out Output stream
 */    
    public static void printProperties (Node node, PrintStream out) {
        out.println ("Properties of node: " + node.getName ());
        Node.PropertySet[] nps = node.getPropertySets ();
        for (int a = 0; a < nps.length; a ++) {
            if (nps [a] == null)
                continue;
            out.println (" PropertySet: " + nps [a].getName ());
            Node.Property[] np = nps [a].getProperties ();
            for (int b = 0; b < np.length; b ++) {
                String s = np [b].getName () + ": ";
                try {
                    if (np [b].getValue () != null)
                        s += np [b].getValue ().toString ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
                out.println (s);
            }
        }
        out.println ();
    }
    
/** Performs node's action
 * @param node Node
 * @param action Action for performing
 */    
    public static void performAction (Node node, Class action) {
        SystemAction act = SystemAction.get (action);
        act.actionPerformed (new java.awt.event.ActionEvent (node, 0, ""));
    }
    
/** Prints clipboard content to output stream
 * @param out Output stream
 * @throws IOException
 * @throws UnsupportedFlavorException
 */    
    public static void printClipboardAsString (PrintStream out) throws IOException, UnsupportedFlavorException {
        ExClipboard clip = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
        Transferable str = (Transferable) clip.getContents (null);
        out.println (str.getTransferData (DataFlavor.stringFlavor).toString ());
    }
    
/** Finds node's subnode by subnode's name
 * @param node Parent node
 * @param name Subnode's name
 * @return Subnode
 */    
    public static Node findSubNode (Node node, String name) {
        Node[] nodes = node.getChildren ().getNodes (true);
        for (int a = 0; a < nodes.length; a ++)
            if (nodes [a].getName ().equals (name))
                return nodes [a];
        return null;
    }

/** Finds node's subnode by subnode's name with delayed look
 * @param node Parent node
 * @param name Subnode's name
 * @return Subnode
 */    
    public static Node findSubNode (Node node, String name, int delay) {
        node.getChildren ().getNodes (true);
        try {
            Thread.currentThread().sleep (delay);
        } catch (Exception r) {}
        return findSubNode (node, name);
    }
    
    public static Node waitSubNode(Node node, String name) {
        for (int a = 0; a < 20; a ++) {
            try { Thread.sleep(1000); } catch (Exception e) { }
            Node n = findSubNode(node, name);
            if (n != null)
                return n;
        }
        return null;
    }
    
    public static Node waitSubNode (Node node, String name, int delay) {
        node.getChildren ().getNodes ();
        try {
            Thread.currentThread().sleep (delay);
        } catch (Exception r) {}
        return waitSubNode (node, name);
    }
    
/** Waits for readLine
 * @throws Exception
 */    
    public static void pressKey () throws Exception {
        new BufferedReader (new InputStreamReader (System.in)).readLine();
    }

/** Sleeps for specified delay
 * @param delay Delay
 * @throws Exception
 */    
    public static void sleep (int delay) throws Exception {
        Thread.currentThread ().sleep (delay);
    }

}
