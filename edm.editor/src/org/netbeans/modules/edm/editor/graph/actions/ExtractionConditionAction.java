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
import org.netbeans.modules.sql.framework.model.SQLObject;
import org.netbeans.modules.sql.framework.ui.graph.ICommand;

/**
 *
 * @author karthikeyan s
 */
public class ExtractionConditionAction extends AbstractAction {
    
    private MashupDataObject mObj;
    
    private SQLObject obj;
    
    private static final Image EXTRACTION_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/filter16.gif"); // NOI18N
    
    /** Creates a new instance of EditJoinAction */
    public ExtractionConditionAction(MashupDataObject dObj, SQLObject obj) {
        super("", new ImageIcon(EXTRACTION_IMAGE));
        mObj = dObj;
        this.obj = obj;
    }
    
    public ExtractionConditionAction(MashupDataObject dObj, SQLObject obj, String name) {
        super(name, new ImageIcon(EXTRACTION_IMAGE));
        mObj = dObj;
        this.obj = obj;
    }    
    
    public void actionPerformed(ActionEvent e) {
        Object[] args = new Object[2];
        args[0] = null;
        args[1] = obj;
        mObj.getEditorView().execute(ICommand.DATA_EXTRACTION, args);
        mObj.getMashupDataEditorSupport().synchDocument();
        mObj.getGraphManager().refreshGraph();
        mObj.getGraphManager().setLog("Data Extraction condition modified.");
    }    
}