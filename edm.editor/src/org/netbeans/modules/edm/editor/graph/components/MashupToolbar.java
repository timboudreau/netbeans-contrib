/*
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the License). You may not use this 
 * file except in compliance with the License.  You can obtain a copy of the
 *  License at http://www.netbeans.org/cddl.html
 *
 * When distributing Covered Code, include this CDDL Header Notice in each
 * file and include the License. If applicable, add the following below the
 * CDDL Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package org.netbeans.modules.edm.editor.graph.components;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.actions.RuntimeInputAction;
import org.netbeans.modules.edm.editor.graph.actions.AutoLayoutAction;
import org.netbeans.modules.edm.editor.graph.actions.CollapseAllAction;
import org.netbeans.modules.edm.editor.graph.actions.EditJoinAction;
import org.netbeans.modules.edm.editor.graph.actions.EditConnectionAction;
import org.netbeans.modules.edm.editor.graph.actions.ExpandAllAction;
import org.netbeans.modules.edm.editor.graph.actions.FitToPageAction;
import org.netbeans.modules.edm.editor.graph.actions.TestRunAction;
import org.netbeans.modules.edm.editor.graph.actions.ZoomInAction;
import org.netbeans.modules.edm.editor.graph.actions.ZoomOutAction;

/**
 *
 * @author karthikeyan s
 */
public class MashupToolbar extends JToolBar {
    
    private MashupDataObject mObj;
    
    /** Creates a new instance of MashupToolbar */
    public MashupToolbar(MashupDataObject dObj) {
        mObj = dObj;
        setRollover(true);
    }
    
    public JToolBar getToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.addSeparator();    
        
        // Fit to page button.
        JButton expandButton = new JButton(new ExpandAllAction(mObj));
        expandButton.setToolTipText("Expand All Widgets");
        toolBar.add(expandButton);        
        
        // Auto layout button.
        JButton collapseButton = new JButton(new CollapseAllAction(mObj));
        collapseButton.setToolTipText("Collapse All Widgets");
        toolBar.add(collapseButton);               

        // Fit to page button.
        JButton fitButton = new JButton(new FitToPageAction(mObj));
        fitButton.setToolTipText("Fit to Page");
        toolBar.add(fitButton);     
        
        toolBar.addSeparator();
        
        // Zoom in button.
        JButton zoominButton = new JButton(new ZoomInAction(mObj));
        zoominButton.setToolTipText("Zoom In");
        toolBar.add(zoominButton);        
        
        // Zoom in button.
        JButton zoomoutButton = new JButton(new ZoomOutAction(mObj));
        zoomoutButton.setToolTipText("Zoom Out");
        toolBar.add(zoomoutButton);                
        
        // Fit to page button.
        JComboBox zoomBox = new ZoomCombo(mObj.getGraphManager());
        zoomBox.setToolTipText("Zoom graph");
        toolBar.add(zoomBox);                
        
        toolBar.addSeparator();
        
        // Auto layout button.
        JButton layoutButton = new JButton(new AutoLayoutAction(mObj));
        layoutButton.setToolTipText("Auto Layout");
        toolBar.add(layoutButton);
        
        toolBar.addSeparator();
        
        // Edit join view button.
        JButton editButton = new JButton(new EditJoinAction(mObj));
        editButton.setToolTipText("Edit Join");
        toolBar.add(editButton);
        
        // Edit join view button.
        JButton editDBButton = new JButton(new EditConnectionAction(mObj));
        editDBButton.setToolTipText("Edit Database Properties");
        toolBar.add(editDBButton);        
        
        // Runtime input button.
        JButton runtimeInputButton = new JButton(new RuntimeInputAction(mObj));
        runtimeInputButton.setToolTipText("Edit Runtime Input Arguments");
        toolBar.add(runtimeInputButton);       
        
        toolBar.addSeparator();
        
        // Run collaboration button.
        JButton runButton = new JButton(new TestRunAction(mObj));
        runButton.setToolTipText("Run");
        toolBar.add(runButton);            
        
        return toolBar;
    }  
}