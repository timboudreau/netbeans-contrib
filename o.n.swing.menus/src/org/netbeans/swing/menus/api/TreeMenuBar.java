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
/*
 * TreeMenuBar.java
 *
 * Created on May 21, 2004, 5:15 PM
 */

package org.netbeans.swing.menus.api;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import org.netbeans.swing.menus.impl.Util;
import org.netbeans.swing.menus.spi.*;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.netbeans.swing.menus.impl.*;

/**
 * A menu bar whose contents are driven by a tree model.  Constructs its model
 * from the system property <code>org.netbeans.swing.menus.MenuTreeModel</code>.
 *
 * @author  Tim Boudreau
 */
public class TreeMenuBar extends JMenuBar {
    private MenuTreeModel mdl;
    public Lis lis; //XXX make package private for tests
    /** Creates a new instance of TreeMenuBar */
    public TreeMenuBar() {
        this (findDefaultModel());
    }
    
    public TreeMenuBar (MenuTreeModel mdl) { //XXX for unit tests
        this.mdl = mdl;
        lis = new Lis();
        StateListener state = new StateListener();
        addHierarchyListener (state);
//        System.err.println("Created a TreeMenuBar");
    }
    
    public static JMenu createMenu (MenuTreeModel model, Object menuNode) {
        return new TreeNodeMenu(model, menuNode);
    }
    
    private static MenuTreeModel findDefaultModel() {
//        System.err.println("Finding default model");
        MenuTreeModel result = null;
        
        String modelClass = System.getProperty (
            "org.netbeans.swing.menus.MenuTreeModel"); //NOI18N
        
//        System.err.println("System property is " + modelClass);
        boolean failed = modelClass == null;
        if (!failed) {
            try {
                Class c = Class.forName (modelClass);
                result = ((MenuTreeModel) c.newInstance());
                failed = false;
            } catch (Exception e) {
//                e.printStackTrace();
                failed = true;
            }
        }
        if (failed) {
            result = (MenuTreeModel) 
                Lookup.getDefault().lookup(MenuTreeModel.class);
            failed = result == null;
        }
        if (failed) {
            throw new Error ("Menu model not supplied - " + //NOI18N
                "no main menu can be created"); //NOI18N
        }
        return result;
    }
    
    private void setModel (MenuTreeModel mdl) {
        this.mdl = mdl;
    }
    
    private class StateListener implements HierarchyListener {
        public void hierarchyChanged(HierarchyEvent e) {
            if (e.getChanged() == TreeMenuBar.this) {
                if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
//                    System.out.println("Got displayability change on menu bar - " + isDisplayable());
                    setListeningToModel (isDisplayable());
                    if (isDisplayable() && getComponentCount() == 0) {
//                        System.err.println("Update from model for showing changed");
                        updateFromModel();
                    }
                }
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
//                    System.err.println("Got showing change on menu bar - " + isShowing());
                    setListeningToModel (isShowing());
                    if (isShowing() && getComponentCount() == 0) {
//                        System.err.println("Update from model for showing changed");
                        updateFromModel();
                    }
                }
            }
        }
    }

    public boolean listening = false; //XXX make package private for unit tests
    private void setListeningToModel (boolean val) {
        if (listening != val) {
            if (val) {
                mdl.addTreeModelListener (lis);
            } else {
                mdl.removeTreeModelListener(lis);
            }
            listening = val;
        }
    }
    
    private boolean dirty = true;
    private void markDirty() {
        if (!dirty) {
            dirty = true;
            if (isShowing()) {
                updateFromModel();
            }
        }
    }
    
    
    private boolean dirty() {
        boolean wasDirty = dirty;
        dirty = false;
        return wasDirty;
    }
    
    private void updateFromModel() {
        Runnable run = new Runnable() {
            public void run() {
                if (_updateFromModel()) {
                    revalidate();
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            if (!Thread.holdsLock(this)) {
                run.run();
            }
            return;
        }
        SwingUtilities.invokeLater (run);
    }
    
    public JComponent componentForItem (Object item) { //XXX should be non public
        return (JComponent) itemsToComponents.get (item);
    }
    
    public Object itemForComponent (JComponent comp) { //XXX should be non public
        return componentsToItems.get(comp);
    }
    
    private HashMap itemsToComponents = new HashMap();
    private HashMap componentsToItems = new HashMap();
    private boolean _updateFromModel() {
        boolean result = false;
        MenuTreeModel.ComponentProvider provider = mdl.getComponentProvider();
        Object node = mdl.getRoot();
        
        int max = mdl.getChildCount(node);
        ArrayList nodes = new ArrayList(itemsToComponents.keySet());
        System.err.println("Update from model, " + max);
        for (int i=0; i < max; i++) {
            Object childNode = mdl.getChild(node, i);
            JComponent comp = (JComponent) itemsToComponents.get(childNode);
            nodes.remove(childNode);
            if (comp == null) {
                result = true;
                comp = provider.createItemFor(childNode);
                install (comp, childNode);
            } else {
                JComponent newComp = provider.syncStateOf(childNode, comp);
                if (newComp != comp) {
                    result = true;
                    replace (comp, newComp, childNode);
                }
            }
        }
        if (!nodes.isEmpty()) {
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                Object missing = i.next();
                JComponent jc = (JComponent) itemsToComponents.get(missing);
                uninstall (jc, missing);
                result = true;
            }
        }
        dirty = false;
        return result;
    }
    
    private void install (JComponent comp, Object child) {
//        System.err.println("Installing " + child + " as " + comp);
        itemsToComponents.put (child, comp);
        componentsToItems.put (comp, child);
        add(comp);
    }
    
    private void uninstall (JComponent comp, Object child) {
//        System.err.println("Uninstalling " + child + " as " + comp);
        itemsToComponents.remove (child);
        componentsToItems.remove(comp);
        remove (comp);
        mdl.getComponentProvider().dispose (comp, child);
    }
    
    private void replace (JComponent old, JComponent nue, Object child) {
        int idx = Arrays.asList(getComponents()).indexOf(old);
        remove (idx);
        componentsToItems.remove(old);
        mdl.getComponentProvider().dispose (old, child);
        add (nue, idx);
        itemsToComponents.put(child, nue);
        componentsToItems.put(nue, child);
    }
    
    private class Lis implements TreeModelListener {
        
        public void treeNodesChanged(TreeModelEvent e) {
            if (Util.isInteresting(e, Util.CHANGED, mdl.getRoot())) {
                markDirty();
            }
        }
        
        public void treeNodesInserted(TreeModelEvent e) {
            if (Util.isInteresting(e, Util.INSERTED, mdl.getRoot())) {
                markDirty();
            }
        }
        
        public void treeNodesRemoved(TreeModelEvent e) {
            if (Util.isInteresting(e, Util.REMOVED, mdl.getRoot())) {
                markDirty();
            }
        }
        
        public void treeStructureChanged(TreeModelEvent e) {
            if (Util.isInteresting(e, Util.STRUCTURE, mdl.getRoot())) {
                markDirty();
            }
        }
    }
}
