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

package org.netbeans.modules.vcscore.versioning.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.openide.explorer.*;
import org.openide.explorer.view.*;
import org.openide.nodes.*;
import org.openide.windows.Workspace;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import org.netbeans.modules.vcscore.util.TopComponentCloseListener;

/**
 * Creates UI displaing content of VersioningRepository.
 *
 * @author  Martin Entlicher
 */
public final class VersioningExplorer {

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
        panel.setToolTipText(root.getDisplayName());
        panel.getExplorerManager().setRootContext(root);
        panel.setIcon (org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/versioning/impl/versioning.png"));
        initComponents();
    }

    public static VersioningExplorer.Panel getRevisionExplorer() {
        return getRevisionExplorer(RepositoryNode.getExplorerNode());
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

    private void initComponents() {
        BeanTreeView VersioningBTV = new BeanTreeView();
        VersioningBTV.getAccessibleContext().setAccessibleName(
            org.openide.util.NbBundle.getMessage(VersioningExplorer.class, "ACSN_versioningSystemName.BTV"));
        VersioningBTV.getAccessibleContext().setAccessibleDescription(
            org.openide.util.NbBundle.getMessage(VersioningExplorer.class, "ACSD_versioningSystemName.BTV"));
        panel.add(VersioningBTV);
        panel.getAccessibleContext().setAccessibleDescription(
            org.openide.util.NbBundle.getMessage(VersioningExplorer.class, "ACSD_versioningSystemName.dialog"));
        panel.setView(VersioningBTV);
        VersioningBTV.setRootVisible(false);
    }
    
    public static class Panel extends ExplorerPanel {

        private transient ArrayList closeListeners = new ArrayList();
        private transient TreeView view;
    
        static final long serialVersionUID =-264310566346550916L;

        public Panel() {
            setToolTipText(org.openide.util.NbBundle.getMessage(VersioningExplorer.class, "VcsExplorer_tltp"));
        }

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
        
        protected String preferredID(){
            return "VersioningExplorer_Panel";// NOI18N
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
        
        void setView(TreeView view) {
            this.view = view;
        }

        public boolean requestFocusInWindow() {
            super.requestFocusInWindow();
            return view.requestFocusInWindow();
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
