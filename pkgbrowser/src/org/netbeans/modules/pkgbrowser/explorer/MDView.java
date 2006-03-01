/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.pkgbrowser.explorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.netbeans.modules.pkgbrowser.FilterHistory;
import org.netbeans.modules.pkgbrowser.Filterable;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ListView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 * A basic master-detail view - has an explorer manager that syncs it's root
 * with the explorer manager in a parent component, and offers a place to put
 * a child component, inside its child split pane.
 *
 * @author Timothy Boudreau
 */
public class MDView extends JPanel implements ExplorerManager.Provider, PropertyChangeListener {
    
    private final ExplorerManager mgr = new ExplorerManager();
    private final JSplitPane jsp = new JSplitPane();
    
    /** Creates a new instance of MDView */
    public MDView(String filterKey) {
        setLayout (new BorderLayout());
        
        add (jsp, BorderLayout.CENTER);
        
        ListView lv = new ListView();
        
        //More border vileness...
        Border b = BorderFactory.createEmptyBorder();
        lv.setBorder(b);
        lv.setViewportBorder(b);
        jsp.setBorder (b);
        
        lv.setMinimumSize(new Dimension (220, 200));
        jsp.setLeftComponent(lv);
    }
    
    public void setRightView (JComponent view) {
        jsp.setRightComponent(view);
    }

    public ExplorerManager getExplorerManager() {
        return mgr;
    }
    
    private ExplorerManager.Provider provider = null;
    public void addNotify() {
        super.addNotify();
        provider = (ExplorerManager.Provider) SwingUtilities.getAncestorOfClass(ExplorerManager.Provider.class, this);
        if (provider != null) {
            provider.getExplorerManager().addPropertyChangeListener(this);
        }
    }
    
    public void removeNotify() {
        super.removeNotify();
        if (provider != null) {
            provider.getExplorerManager().removePropertyChangeListener(this);
            provider = null;
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        ExplorerManager mgr = (ExplorerManager) evt.getSource();
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            Node[] n = mgr.getSelectedNodes();
            if (n.length == 1) {
                this.mgr.setRootContext(n[0]);
            } else {
                this.mgr.setRootContext (new AbstractNode(Children.LEAF));
            }
        }
    }
}
