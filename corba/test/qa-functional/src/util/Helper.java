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

package util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.openide.TopManager;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.datatransfer.ExClipboard;

public class Helper {

    public static final String PLEASE_WAIT = "Please Wait...";
    
    public static String filter (ArrayList filter, String str) {
        if (filter != null) {
            for (int filterindex = 0; filterindex < filter.size (); filterindex ++) {
                int index = str.indexOf ((String) filter.get (filterindex));
                if (index >= 0)
                    str = str.substring (0, index + ((String) filter.get (filterindex)).length());
            }
        }
        return str;
    }

    public static void sleep (int delay) {
        try {
            Thread.currentThread().sleep (delay);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public static String getStringFromClipboard() {
        try {
            ExClipboard clip = TopManager.getDefault().getClipboard();
            Transferable str = (Transferable) clip.getContents(null);
            Object o = str.getTransferData(DataFlavor.stringFlavor);
            return o.toString ();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean isPleaseWait (Node[] nodes) {
        if (nodes.length != 1)
            return false;
        for (int a = 0; a < nodes.length; a ++)
            if (PLEASE_WAIT.equals (nodes[a].getName ()))
                return true;
        return false;
    }

    public static Node[] waitSubNodes (Node node) {
        Node[] nodes = null;
        int a = 0;
        do {
            nodes = node.getChildren ().getNodes (true);
            if (++ a >= 60)
                return null;
            sleep (1000);
        } while (isPleaseWait (nodes));
        return nodes;
    }
    
    public static Node getSubNode (Node[] nodes, String name) {
        for (int a = 0; a < nodes.length; a ++)
            if (name.equals (nodes[a].getName()))
                return nodes[a];
        return null;
    }
    
    public static Node waitSubNode (Node node, String name) {
        return getSubNode (waitSubNodes(node), name);
    }
	
    public static Node[] filterNodes (Node[] nodes) {
		if (nodes == null)
			return null;
        ArrayList al = new ArrayList ();
		int count = 0;
		for (int a = 0; a < nodes.length; a ++) {
			if (!PLEASE_WAIT.equals (nodes[a].getName ())) {
				al.add (nodes[a]);
				count ++;
			}
		}
		return (Node[]) al.toArray (new Node[count]);
    }
    
/*
    public static Node getOriginalNode (Node node) {
        Method m;
        try {
            m = FilterNode.class.getDeclaredMethod("getOriginal", null);
            m.setAccessible(true);
            while (node instanceof FilterNode)
                node = (Node) m.invoke(node, null);
        } catch (IllegalAccessException e) {
        } catch (IllegalArgumentException e) {
        } catch (InvocationTargetException e) {
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
        }
        return node;
    }
*/
}
