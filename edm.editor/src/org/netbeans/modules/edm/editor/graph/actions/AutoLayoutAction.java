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

package org.netbeans.modules.edm.editor.graph.actions;

import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.openide.util.Utilities;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;

/**
 *
 * @author karthikeyan s
 */
public class AutoLayoutAction extends AbstractAction {
    
    private MashupDataObject mObj;
    
    private static final Image LAYOUT_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/layout.png"); // NOI18N
    
    /** Creates a new instance of EditJoinAction */
    public AutoLayoutAction(MashupDataObject dObj) {
        super("", new ImageIcon(LAYOUT_IMAGE));
        mObj = dObj;
    }
    
    public AutoLayoutAction(MashupDataObject dObj, String name) {
        super(name, new ImageIcon(LAYOUT_IMAGE));
        mObj = dObj;
    }    
    
    public void actionPerformed(ActionEvent e) {
        mObj.getGraphManager().refreshGraph();
        mObj.getGraphManager().getScene().layoutScene();
        mObj.getGraphManager().setLog("Sucessfully laid out graph.");
    }    
}