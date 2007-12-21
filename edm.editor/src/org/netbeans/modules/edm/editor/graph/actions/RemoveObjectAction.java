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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.utils.MashupGraphUtil;
import org.netbeans.modules.sql.framework.model.SQLObject;
import org.netbeans.modules.sql.framework.model.SourceTable;
import org.netbeans.modules.sql.framework.model.impl.SQLDefinitionImpl;

/**
 *
 * @author karthikeyan s
 */
public class RemoveObjectAction extends AbstractAction {
    
    private MashupDataObject mObj;
    
    private SQLObject obj;
    
    /** Creates a new instance of EditJoinAction */
    public RemoveObjectAction(MashupDataObject dObj, SQLObject obj) {
        super("",new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.REMOVE)));
        mObj = dObj;
        this.obj = obj;
    }
    
    public RemoveObjectAction(MashupDataObject dObj, SQLObject obj, String name) {
        super(name,new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.REMOVE)));
        mObj = dObj;
        this.obj = obj;
    }    
    
    public void actionPerformed(ActionEvent e) {
        SQLDefinitionImpl defn = (SQLDefinitionImpl)mObj.getModel().getSQLDefinition();
        try {
            if(obj instanceof SourceTable) {
                SourceTable srcTbl = (SourceTable) obj;
                if(srcTbl.isUsedInJoin()) {
                    mObj.getGraphManager().setLog(
                            "Failed to remove Table. Cause: Table is used in Join.");
                    return;
                }
            }
            defn.removeObject(obj);
            mObj.getMashupDataEditorSupport().synchDocument();
            mObj.getGraphManager().setLog("Removed the Object successfully.");
            mObj.getGraphManager().refreshGraph();
        } catch (Exception ex) {
            mObj.getGraphManager().setLog("Failed to remove the specified object.");
        }
        
    }    
}