/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.versioning.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.openide.explorer.*;
import org.openide.explorer.view.*;
import org.openide.explorer.propertysheet.*;
import org.openide.nodes.*;
import org.openide.windows.Workspace;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;

import org.netbeans.modules.vcscore.util.TopComponentCloseListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author  Martin Entlicher
 */
public class VersioningExplorer {

    /**
     * Map of explorer panels for individual nodes.
     */
    private static Map explorersForNodes = new WeakHashMap();

    private Panel panel;
    private Node root = null;
    
    /** Creates new VersioningExplorer */
    private VersioningExplorer(Node root) {
        this.root = root;
        panel = new Panel();
        //panel.setName(org.openide.util.NbBundle.getBundle(RevisionExplorer.class).getString("CTL_Explorer.title"));
        panel.setName(root.getDisplayName());
        panel.getExplorerManager().setRootContext(root);
        panel.setIcon (org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/versioning/impl/versioningExplorer.gif"));
        initComponents();
    }

    public static VersioningExplorer.Panel getRevisionExplorer() {
        return getRevisionExplorer(VersioningDataSystem.getVersioningDataSystem());
    }
    
    /**
     * Get the Revision Explorer for that node.
     */
    private static VersioningExplorer.Panel getRevisionExplorer(final Node node) {
        synchronized (explorersForNodes) {
            VersioningExplorer explorer;
            explorer = (VersioningExplorer) explorersForNodes.get(node);
            if (explorer == null) {
                explorer = new VersioningExplorer(node);
                explorersForNodes.put(node, explorer);
            }
            return explorer.panel;
        }
    }

    private void initComponentsSplitted() {
        PropertySheetView propertySheetView = new PropertySheetView();
        try {
            propertySheetView.setSortingMode(org.openide.explorer.propertysheet.PropertySheet.UNSORTED);
        } catch (java.beans.PropertyVetoException exc) {
            // The change was vetoed
        }
        org.openide.awt.SplittedPanel split = new org.openide.awt.SplittedPanel();
        split.setSplitType(org.openide.awt.SplittedPanel.HORIZONTAL);
        split.add(new BeanTreeView(), org.openide.awt.SplittedPanel.ADD_LEFT);
        split.add(propertySheetView, org.openide.awt.SplittedPanel.ADD_RIGHT);
        //panel.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new RevisionTreeView(), propertySheetView));
        panel.add(split);
        //panel.add(new BeanTreeView(), java.awt.BorderLayout.WEST);
        //panel.add(new PropertySheetView(), java.awt.BorderLayout.EAST);
    }
    
    private void initComponents() {
        BeanTreeView VersioningBTV = new BeanTreeView();
        VersioningBTV.getAccessibleContext().setAccessibleName(
            org.openide.util.NbBundle.getMessage(VersioningExplorer.class, "ACSN_versioningSystemName.BTV"));
        VersioningBTV.getAccessibleContext().setAccessibleDescription(
            org.openide.util.NbBundle.getMessage(VersioningExplorer.class, "ACSD_versioningSystemName.BTV"));
        panel.add(VersioningBTV);
        panel.getAccessibleContext().setAccessibleDescription(
            org.openide.util.NbBundle.getMessage(VersioningExplorer.class, "ACSD_versioningSystemName.dialog"));
    }
    
    public static class Panel extends ExplorerPanel {

        private transient ArrayList closeListeners = new ArrayList();
    
        static final long serialVersionUID =-264310566346550916L;

        public void open(Workspace workspace) {
            Mode mode = WindowManager.getDefault().findMode(this);
            if (mode == null) {
                mode = WindowManager.getDefault().findMode("explorer"); // NOI18N
                if (mode != null)
                    mode.dockInto(this);
            }
            super.open(workspace);
        }

        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_ALWAYS;
        }
        /*
         * Override for clean up reasons.
         * Will be moved to the appropriate method when will be made.
         *
        public boolean canClose(Workspace workspace, boolean last) {
            boolean can = super.canClose(workspace, last);
            if (last && can) {
                closing();
            }
            return can;
        }
         */
        
        public void addCloseListener(TopComponentCloseListener listener) {
            closeListeners.add(listener);
        }
        
        protected void closeNotify() {
            if (closeListeners != null) {
                for(Iterator it = closeListeners.iterator(); it.hasNext(); ) {
                    ((TopComponentCloseListener) it.next()).closing();
                }
                closeListeners = new ArrayList(); // to free all listeners, closing will be called only once
            }
        }
    
        /** Called when the explored context changes.
         * Overriden - we don't want the title to chnage. */
        protected void updateTitle() {
            // empty to keep the title unchanged
            //setName(getExplorerManager().getRootContext().getDisplayName());
        }

        /** Writes a resolvable */
        protected Object writeReplace() {
            return new Resolvable();
        }

        static class Resolvable implements java.io.Serializable {
            
            static final long serialVersionUID =2445268234090632539L;
            private Object readResolve() {
                return getRevisionExplorer();
            }
        }
    }
}
