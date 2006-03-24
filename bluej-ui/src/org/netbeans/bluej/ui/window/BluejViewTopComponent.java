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
package org.netbeans.bluej.ui.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Arrays;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.text.DefaultEditorKit;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.bluej.api.BluejLogicalViewProvider;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.ListView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
final class BluejViewTopComponent extends TopComponent implements ExplorerManager.Provider {
    
    private static final long serialVersionUID = 1L;
    
    private static BluejViewTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/bluej/ui/window/bluejview.png"; //NOI18N
    
    private static final String PREFERRED_ID = "BluejViewTopComponent"; //NOI18N
    private ListView view;
    private ExplorerManager manager;
    private JButton upButton;
    
    private JComboBox projectsCombo;
    private OpenedBluejProjects openedProjects;
    private ItemListener itemListener;
    
    private BluejViewTopComponent() {
        manager = new ExplorerManager();
        ActionMap map = getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true));

        openedProjects = new OpenedBluejProjects();
        initComponents();
        setName(NbBundle.getMessage(BluejViewTopComponent.class, "CTL_BluejViewTopComponent"));
        setToolTipText(NbBundle.getMessage(BluejViewTopComponent.class, "HINT_BluejViewTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));
        view = new ListView() {
            protected JList createList() {
                JList list = super.createList();
                list.setCellRenderer(new HackedNodeRenderer());
                return list;
            }
        };
        add(view, BorderLayout.CENTER);
        associateLookup( ExplorerUtils.createLookup(manager, map) );
        JPanel toolbarPanel = new JPanel();
        upButton = new JButton("Up");
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Node nd = manager.getExploredContext();
                if (nd.getParentNode() != null) {
                    manager.setExploredContext(nd.getParentNode());
                }
            }
        });
        manager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (ExplorerManager.PROP_EXPLORED_CONTEXT.equals(evt.getPropertyName())) {
                    Node cont = manager.getExploredContext();
                    upButton.setEnabled(cont != null && cont.getParentNode() != null);
                }
            }
        });
        
        toolbarPanel.setLayout(new BorderLayout());
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        toolbarPanel.add(upButton, BorderLayout.WEST);
        
        projectsCombo = new JComboBox();
        projectsCombo.setEditable(false);
        projectsCombo.setMinimumSize(new Dimension(150, 22));
        projectsCombo.setPreferredSize(new Dimension(150, 22));
        toolbarPanel.add(projectsCombo, BorderLayout.EAST);
        itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                // change main project and selected project in the BJ view
                if (e.getStateChange() == ItemEvent.SELECTED ||
                    (e.getStateChange() == ItemEvent.DESELECTED && 
                     e.getItemSelectable().getSelectedObjects() == null ||
                     e.getItemSelectable().getSelectedObjects().length == 0)) {
                    updateContent();
                }
            }
        };
        
        add(toolbarPanel, BorderLayout.NORTH);
    }
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    private void updateContent() {
        Project project = openedProjects.getSelectedProject();
        if ( project != null && Arrays.asList(OpenProjects.getDefault().getOpenProjects()).contains(project)) {
            // if it's not in the list of opened projects we probably are closing multiple projects as once (or shutting down)
            OpenProjects.getDefault().setMainProject(project);
            BluejLogicalViewProvider provider = (BluejLogicalViewProvider) project.getLookup().lookup(BluejLogicalViewProvider.class);
            manager.setRootContext(provider.getBigIconRootNode());
        } else {
            manager.setRootContext(new AbstractNode(Children.LEAF));
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized BluejViewTopComponent getDefault() {
        if (instance == null) {
            instance = new BluejViewTopComponent();
        }
        return instance;
    }
    
    /**
     * Obtain the BluejViewTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized BluejViewTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find BluejView component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof BluejViewTopComponent) {
            return (BluejViewTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public void componentOpened() {
        openedProjects.addNotify();
        projectsCombo.setModel(openedProjects.getComboModel());
        updateContent();
        projectsCombo.addItemListener(itemListener);
    }
    
    public void componentClosed() {
        openedProjects.removeNotify();
        projectsCombo.removeItemListener(itemListener);
        projectsCombo.setModel(new DefaultComboBoxModel());
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return BluejViewTopComponent.getDefault();
        }
    }
}
