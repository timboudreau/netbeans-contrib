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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javafx.debug;

import com.sun.source.util.TreePath;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Max Sauer
 */
public class ErrorNavigatorProviderImpl implements NavigatorPanel {

    private JComponent panel;
    private final ExplorerManager manager = new ExplorerManager();
    
    /**
     * Default constructor for layer instance.
     */
    public ErrorNavigatorProviderImpl() {
        manager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    TreeNavigatorProviderImpl.setHighlights(ElementNavigatorJavaSourceFactory.getInstance().getFile(), manager);
                }
            }
        });
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(ErrorNavigatorProviderImpl.class, "NM_Errors");
    }
    
    public String getDisplayHint() {
        return NbBundle.getMessage(ErrorNavigatorProviderImpl.class, "SD_Errors");
    }
    
    public JComponent getComponent() {
        if (panel == null) {
            final BeanTreeView view = new BeanTreeView();
            view.setRootVisible(false);
            view.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            class Panel extends JPanel implements ExplorerManager.Provider, Lookup.Provider {
                // Make sure action context works correctly:
                private final Lookup lookup = ExplorerUtils.createLookup(manager, new ActionMap());
                {
                    setLayout(new BorderLayout());
                    add(view, BorderLayout.CENTER);
                }
                public ExplorerManager getExplorerManager() {
                    return manager;
                }
                public Lookup getLookup() {
                    return lookup;
                }
            }
            panel = new Panel();
        }
        return panel;
    }

    public Lookup getLookup() {
        return null;
    }

    public void panelActivated(Lookup context) {
//        ElementNavigatorJavaSourceFactory.getInstance().setLookup(context, new TaskImpl());
    }

    public void panelDeactivated() {
//        ElementNavigatorJavaSourceFactory.getInstance().setLookup(Lookup.EMPTY, null);
    }
    
//    private final class TaskImpl implements CancellableTask<CompilationInfo> {
//        
//        public void cancel() {
//        }
//
//        public void run(CompilationInfo info) {
//            //XXX set proper root
//            manager.setRootContext(ErrorNode.getTree(info));
//        }
//        
//    }
    
}
