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

package org.netbeans.modules.latex.bibtex.navigator;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Jan Lahoda
 */
public class BiBTeXNavigatorPanel implements NavigatorPanel, PropertyChangeListener {
    
    private Lookup.Result dataObjectSelection;
    private final LookupListener selectionListener = new LookupListener() {
        public void resultChanged(LookupEvent ev) {
            displayDO(dataObjectSelection.allInstances());
        }
    };
    
    private JComponent panel;
    private ExplorerManager manager;
    
    private LookupImpl lookup;
    
    /**
     * Creates a new instance of BiBTeXNavigatorPanel
     */
    public BiBTeXNavigatorPanel() {
        manager = new ExplorerManager();
        lookup = new LookupImpl();
        
        manager.addPropertyChangeListener(this);
    }
    
    public String getDisplayName() {
        return "BiBTeX Navigator";
    }
    
    public String getDisplayHint() {
        return "BiBTeX Navigator";
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
    
    public void panelActivated(Lookup context) {
        dataObjectSelection = context.lookup(new Lookup.Template(DataObject.class));
        dataObjectSelection.addLookupListener(selectionListener);
        selectionListener.resultChanged(null);
    }
    
    public void panelDeactivated() {
        dataObjectSelection.removeLookupListener(selectionListener);
        dataObjectSelection = null;
    }
    
    public Lookup getLookup() {
        System.err.println("getLookup");
        return lookup;
    }
    
    private void displayDO(Collection/*<DataObject>*/ selectedFiles) {
        // Show list of targets for selected file:
        if (selectedFiles.size() == 1) {
            DataObject d = (DataObject) selectedFiles.iterator().next();
            
            manager.setRootContext(new RootNode(d.getPrimaryFile()));
            return ;
        }
        // Fallback:
        manager.setRootContext(Node.EMPTY);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Node[] nodes = manager.getSelectedNodes();
        Lookup[] innerLookups = new Lookup[nodes.length + 1];
        
        for (int cntr = 0; cntr < nodes.length; cntr++) {
            innerLookups[cntr + 1] = nodes[cntr].getLookup();
        }
        
        innerLookups[0] = Lookups.fixed(nodes);
        
        lookup.setLookupsImpl(innerLookups);
    }
    
    private static final class LookupImpl extends ProxyLookup {
        
        void setLookupsImpl(Lookup[] l) {
            super.setLookups(l);
        }
        
    }
    
}
