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
