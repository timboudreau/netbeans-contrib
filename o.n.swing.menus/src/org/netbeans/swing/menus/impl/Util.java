/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * Util.java
 *
 * Created on May 21, 2004, 6:51 PM
 */

package org.netbeans.swing.menus.impl;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

/**
 * Some utility methods with logic used by components that listen on trees
 *
 * @author  Tim Boudreau
 */
public class Util {
    public static final int CHANGED = 0;
    public static final int INSERTED = 1;
    public static final int REMOVED = 2;
    public static final int STRUCTURE = 3;
    
    /** Creates a new instance of Util */
    private Util() {
    }
    
    public static boolean isInteresting (TreeModelEvent e, int id, Object root) {
        TreePath path = e.getTreePath();
        boolean result = false;
        switch (id) {
            case CHANGED :
            case INSERTED :
            case REMOVED :
                result = inImmediateSubtree(path, root);
                break;
            case STRUCTURE :
                result = true;
                break;
            default :
                throw new IllegalArgumentException();
        }
        return result;
    }
    
    
    private static boolean inImmediateSubtree (TreePath path, Object node) {
        return path.getLastPathComponent() == node;
    }    
    
    
    
}
