/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the DocSup module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jan Lahoda
 */
public class PropertyWindow extends TopComponent implements PropertyChangeListener {
    
    private PropertySheet   sheet;
    
    /** Creates a new instance of PropertyWindow */
    public PropertyWindow() {
        setLayout(new BorderLayout());
        add(sheet = new PropertySheet(), BorderLayout.CENTER);
        
    }
    
    public void addNotify() {
        super.addNotify();
        
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(this);
        setNodes();
    }

    public void removeNotify() {
        super.removeNotify();
        
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(this);
        sheet.setNodes(new Node[0]);
    }

    private void setNodes() {
         sheet.setNodes(WindowManager.getDefault().getRegistry().getActivatedNodes());
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TopComponent.Registry.PROP_ACTIVATED_NODES))
            setNodes();
    }
    
}
