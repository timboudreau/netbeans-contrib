/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the DocSup module.
 * The Initial Developer of the Original Software is Jan Lahoda.
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
