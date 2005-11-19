/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.ui.navigator;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import org.netbeans.modules.latex.model.command.DebuggingSupport;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.ui.NodeNode;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;


/**Copied from ant module
 *
 * @author Jesse Glick, Jan Lahoda
 */
public class DebugNavigatorProviderImpl implements NavigatorPanel {
    
    private Lookup.Result selection;
    private final LookupListener selectionListener = new LookupListener() {
        public void resultChanged(LookupEvent ev) {
            display(selection.allInstances());
        }
    };
    private JComponent panel;
    private final ExplorerManager manager = new ExplorerManager();
    
    /**
     * Default constructor for layer instance.
     */
    public DebugNavigatorProviderImpl() {
        manager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    if (manager.getSelectedNodes().length == 1) {
                        Node n = manager.getSelectedNodes()[0];
                        org.netbeans.modules.latex.model.command.Node node = (org.netbeans.modules.latex.model.command.Node) n.getLookup().lookup(org.netbeans.modules.latex.model.command.Node.class);
                        
                        DebuggingSupport.getDefault().setCurrentSelectedNode(node);
                    }
                }
            }
        });
    }
    
    public String getDisplayName() {
        return "Tree";
    }
    
    public String getDisplayHint() {
        return "Tree View";
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
        selection = context.lookup(new Lookup.Template(DataObject.class));
        selection.addLookupListener(selectionListener);
        selectionListener.resultChanged(null);
    }
    
    public void panelDeactivated() {
        selection.removeLookupListener(selectionListener);
        selection = null;
    }
    
    public Lookup getLookup() {
        return null;
    }
    
    private FileObject currentFO;
    
    private void display(Collection/*<DataObject>*/ selectedFiles) {
        // Show list of targets for selected file:
        if (selectedFiles.size() == 1) {
            DebuggingSupport.getDefault().setDebuggingEnabled(true);
            
            DataObject d = (DataObject) selectedFiles.iterator().next();
            
            manager.setRootContext(NodeNode.constructRootNodeFor(LaTeXSource.get(d.getPrimaryFile())));
            return ;
        }
        // Fallback:
        manager.setRootContext(Node.EMPTY);
    }
    
}
