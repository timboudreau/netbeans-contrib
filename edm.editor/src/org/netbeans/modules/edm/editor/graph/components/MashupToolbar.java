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
import javax.swing.JToolBar;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.actions.RuntimeInputAction;
import org.netbeans.modules.edm.editor.graph.actions.AutoLayoutAction;
import org.netbeans.modules.edm.editor.graph.actions.EditJoinAction;
import org.netbeans.modules.edm.editor.graph.actions.EditConnectionAction;
import org.netbeans.modules.edm.editor.graph.actions.TestRunAction;

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