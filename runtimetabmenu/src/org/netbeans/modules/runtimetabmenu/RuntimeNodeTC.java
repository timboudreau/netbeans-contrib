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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.runtimetabmenu;

import java.awt.BorderLayout;
import java.beans.BeanInfo;
import java.io.ObjectStreamException;
import java.io.Serializable;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tim
 */
public class RuntimeNodeTC extends TopComponent implements ExplorerManager.Provider {
    private final ExplorerManager mgr = new ExplorerManager();
    RuntimeNodeTC(String path) {
        this (nodeFor(path));
    }
    
    RuntimeNodeTC(Node node) {
        this();
        setNode (node);
    }
    
    RuntimeNodeTC() {
        setLayout (new BorderLayout());
        BeanTreeView view = new BeanTreeView();
        add (view, BorderLayout.CENTER);
//        view.setRootVisible(false);
    }
    
    private void setNode (Node n) {
        mgr.setRootContext(n);
        setDisplayName (n.getDisplayName());
        setIcon(n.getIcon (BeanInfo.ICON_COLOR_16x16));
        setToolTipText(n.getShortDescription());
    }
    
    private static Node nodeFor (String path) {
        FileObject base = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(path);
        Node result = null;
        if (base != null) {
            try {
                DataObject dob = DataObject.find (base);
                result = dob.getNodeDelegate();
            } catch (DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
            }
        }
        if (result == null) {
            result = new AbstractNode (Children.LEAF);
            result.setDisplayName (NbBundle.getMessage(RuntimeNodeTC.class, 
                    "LBL_MISSING", path)); //NOI18N                    
        }
        return result;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }
    
    private String getPath() {
        Node root = getExplorerManager().getRootContext();
        if (root != null) {
            DataObject dob = root.getLookup().lookup (DataObject.class);
            if (dob != null) {
                return dob.getPrimaryFile().getPath();
            }
        }
        return "unknown"; //NOI18N        
    }
    
    @Override
    public void open() {
        String mode = NbPreferences.forModule(RuntimeNodeTC.class).get(getPath(), "explorer"); //NOI18N        
        Mode m = WindowManager.getDefault().findMode (mode);
        if (m != null) {
            m.dockInto(this);
        }
        super.open();
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ONLY_OPENED;
    }

    @Override
    protected Object writeReplace() throws ObjectStreamException {
        saveModeInfo();
        return new Stub (getPath());
    }

    @Override
    protected void componentHidden() {
        super.componentHidden();
    }
    
    private void saveModeInfo() {
        Mode m = WindowManager.getDefault().findMode(this);
        if (m != null && !"explorer".equals(m.getName())) { //NOI18N        
            String mode = m.getName();
            String path = getPath();
            NbPreferences.forModule(RuntimeNodeTC.class).put(path, mode);
        }
    }
    
    protected String preferredID() {
        return getPath();
    }
    
    private static final class Stub implements Serializable {
        private static long serialVersionUID = 1234L;
        private String path;
        Stub (String path) {
            this.path = path;
        }
        
        public Object readResolve() {
            RuntimeNodeTC result = new RuntimeNodeTC (path);
            return result;
        }
    }
}
