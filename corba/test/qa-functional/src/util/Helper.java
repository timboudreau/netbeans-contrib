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
            return str.getTransferData(DataFlavor.stringFlavor).toString ();
        } catch (IOException e) {
        } catch (UnsupportedFlavorException e) {
        }
        return null;
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
