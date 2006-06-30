/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package basic.JNDITest;

import java.io.*;
import java.awt.datatransfer.*;

import org.openide.nodes.*;
import org.openide.util.Lookup;
import org.openide.util.actions.*;
import org.openide.util.datatransfer.*;


public class Helper {

    public static void printActions (Node node, final PrintStream out) {
        out.println ("Actions of node: " + node.getName ());
        SystemAction[] sa = node.getActions ();
        for (int a = 0; a < sa.length; a ++)
            if (sa [a] != null) {
                String ss = sa [a].getName ();
                if (ss.indexOf("Add") >= 0)
                    ss = "Add";
                out.println(ss);
            }
        out.println ();
    }
    
    public static void printProperties (Node node, final PrintStream out) {
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
    
    public static void performAction (Node node, Class action) {
        final SystemAction act = SystemAction.get (action);
        final Node fnode = node;
        javax.swing.SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                act.actionPerformed (new java.awt.event.ActionEvent (fnode, 0, ""));
            }
        });
    }
    
    public static void printClipboardAsString (final PrintStream out) throws IOException, UnsupportedFlavorException {
        ExClipboard clip = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
        Transferable str = (Transferable) clip.getContents (null);
        out.println (str.getTransferData (DataFlavor.stringFlavor).toString ());
    }

    public static String getStringFromClipboard() throws IOException, UnsupportedFlavorException {
        ExClipboard clip = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
        Transferable str = (Transferable) clip.getContents(null);
        return str.getTransferData(DataFlavor.stringFlavor).toString ();
    }
    
    public static void printClipboardAsStringWithReplace (final PrintStream out, String src, String dst, String lsrc, String ldst) throws IOException, UnsupportedFlavorException {
        ExClipboard clip = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
        Transferable strsel = (Transferable) clip.getContents (null);
        String str = strsel.getTransferData (DataFlavor.stringFlavor).toString ();
        String strout = "";
        int i = str.indexOf (src);
        if (i >= 0) {
            if (i > 0)
                strout = str.substring (0, i);
            strout += dst;
            if (i + src.length () < str.length ())
                strout += str.substring (i + src.length());
            i += dst.length ();
        } else {
            strout = str;
            i = 0;
        }
        do {
            i = strout.indexOf (lsrc, i);
            if (i < 0)
                break;
            str = strout;
            if (i > 0)
                strout = str.substring (0, i);
            strout += ldst;
            if (i + lsrc.length () < str.length ())
                strout += str.substring (i + lsrc.length ());
            i += ldst.length ();
        } while (true);
        out.println (strout);
    }
    
    public static Node findSubNode (Node node, String name) {
        Node[] nodes = node.getChildren ().getNodes (true);
        for (int a = 0; a < nodes.length; a ++)
            if (nodes [a].getName ().equals (name))
                return nodes [a];
        return null;
    }
    
     public static Node waitSubNode(Node node, String name) {
        for (int a = 0; a < 10; a ++) {
            try { Thread.sleep(1000); } catch (Exception e) { }
            Node n = findSubNode(node, name);
            if (n != null)
                return n;
        }
        return null;
    }
    
   public static void sleep (int delay) {
        try { Thread.sleep(delay); } catch (Exception e) {}
    }
}