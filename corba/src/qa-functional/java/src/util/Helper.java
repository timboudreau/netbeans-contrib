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

package util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
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
            ExClipboard clip = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
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
    
    public static String replaceAll(String str, String from, String to) {
        if ("".equals (from)  ||  to.startsWith(from))
            return str;
        for (;;) {
            int index = str.indexOf(from);
            if (index < 0)
                break;
            str = str.substring(0, index) + to + str.substring(index + from.length());
        }
        return str;
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
