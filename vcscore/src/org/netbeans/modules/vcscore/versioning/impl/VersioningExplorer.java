/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.versioning.impl;

import java.util.ArrayList;
import java.util.Iterator;

import org.openide.TopManager;
import org.openide.explorer.*;
import org.openide.explorer.view.*;
import org.openide.explorer.propertysheet.*;
import org.openide.nodes.*;
import org.openide.windows.Workspace;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;

import org.netbeans.modules.vcscore.util.TopComponentCloseListener;

/**
 *
 * @author  Martin Entlicher
 */
public class VersioningExplorer {

    private static final String MODE_NAME = "VersioningExplorer";

    private Panel panel;
    private Node root = null;
    
    /** Creates new VersioningExplorer */
    private VersioningExplorer(Node root) {
        this.root = root;
        panel = new Panel();
        //panel.setName(org.openide.util.NbBundle.getBundle(RevisionExplorer.class).getString("CTL_Explorer.title"));
        panel.setName(root.getDisplayName());
        panel.getExplorerManager().setRootContext(root);
        panel.setIcon (org.openide.util.Utilities.loadImage("/org/netbeans/modules/vcscore/versioning/impl/versioningExplorer.gif"));
        initComponents();
    }

    public static VersioningExplorer.Panel getRevisionExplorer() {
        return getRevisionExplorer(VersioningDataSystem.getVersioningDataSystem());
    }
    
    /**
     * Get the Revision Explorer for that node.
     */
    public static VersioningExplorer.Panel getRevisionExplorer(final Node node) {
        Workspace curr = TopManager.getDefault().getWindowManager().getCurrentWorkspace();
//        Mode reMode = curr.findMode(MODE_NAME);
        Iterator it = curr.getModes().iterator();
//        if (reMode != null) {
        while (it.hasNext()) {
            Mode reMode = (Mode)it.next();
            TopComponent[] tcs = reMode.getTopComponents();
            //System.out.println("No. of components in Explorer Mode = "+tcs.length);
            for(int i = 0; i < tcs.length; i++) {
                if (tcs[i] instanceof VersioningExplorer.Panel) {
                    VersioningExplorer.Panel explorer = (VersioningExplorer.Panel) tcs[i];
                    Node root = explorer.getExplorerManager().getRootContext();
                    if (root.equals(node)) return explorer;
                }
                /*
                DataObject dobjRoot = null;
                if (root instanceof DataNode) dobjRoot = ((DataNode) root).getDataObject();
                //String displayName = root.getDisplayName();
                if (dobj != null && dobj.equals(dobjRoot)) {
                    Children.Array ch = (Children.Array) root.getChildren();
                    ch.remove(new Node[] { ch.findChild(node.getName()) });
                    ch.add(new Node[] { node });
                    return explorer;
                }
                 */
            }
        }
        /*
        Children.Array ch = new Children.Array();
        ch.add(new Node[] { node });
        //AbstractNode root = new RootNode(ch, node);
        AbstractNode root = null;
        if (dobj != null) {
            root = new DataNode(dobj, ch);
        }
        if (root == null) {
            root = new AbstractNode(ch);
            root.setDisplayName(rootDisplayName);
        }
        root.setIconBase(ICON_ROOT);
         */
        VersioningExplorer ve = new VersioningExplorer(node);
        return ve.panel;
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
        panel.add(new BeanTreeView());
    }
    
    public static class Panel extends ExplorerPanel {

        private transient ArrayList closeListeners = new ArrayList();
    
        static final long serialVersionUID =-264310566346550916L;
        Panel() {
        }

        public void open(Workspace workspace) {
            Mode myMode = workspace.findMode(this);
            if (myMode == null) {
                // create new mode for CI and set the bounds properly
                myMode = workspace.createMode(MODE_NAME, getName(), null); //NOI18N
                /*
                Rectangle workingSpace = workspace.getBounds();
                myMode.setBounds(new Rectangle(workingSpace.x +(workingSpace.width * 3 / 10), workingSpace.y,
                                               workingSpace.width * 2 / 10, workingSpace.height / 2));
                 */
                myMode.dockInto(this);
            }
            super.open(workspace);
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
