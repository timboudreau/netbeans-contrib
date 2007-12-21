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
import org.netbeans.modules.sql.framework.model.SQLConstants;
import org.netbeans.modules.sql.framework.model.SQLJoinView;
import org.netbeans.modules.sql.framework.ui.view.join.JoinMainDialog;
import org.netbeans.modules.sql.framework.ui.view.join.JoinUtility;

/**
 *
 * @author karthikeyan s
 */
public class EditJoinAction extends AbstractAction {
    
    private MashupDataObject mObj;
    
    /** Creates a new instance of EditJoinAction */
    public EditJoinAction(MashupDataObject dObj) {
        super("",new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.EDITJOIN)));
        mObj = dObj;
    }
    
    public EditJoinAction(MashupDataObject dObj, String name) {
        super(name,new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.EDITJOIN)));
        mObj = dObj;
    }    
    /** 
     * implements edit join action. 
     */    
    public void actionPerformed(ActionEvent e) {
        SQLJoinView[] joinViews = (SQLJoinView[]) mObj.getModel().getSQLDefinition().getObjectsOfType(
                SQLConstants.JOIN_VIEW).toArray(new SQLJoinView[0]);
        SQLJoinView jView = null;
        if(joinViews != null && joinViews.length != 0) {
            jView = joinViews[0];
        }
        JoinMainDialog.showJoinDialog(
                mObj.getModel().getSQLDefinition().getJoinSources(), jView,
                null);
        if (JoinMainDialog.getClosingButtonState() == JoinMainDialog.OK_BUTTON) {
            SQLJoinView joinView = JoinMainDialog.getSQLJoinView();
            try {
                if (joinView != null) {
                    mObj.getModel().getSQLDefinition().removeObjects(
                            mObj.getModel().getSQLDefinition().getObjectsOfType(
                            SQLConstants.JOIN_VIEW));
                    JoinUtility.handleNewJoinCreation(joinView,
                            JoinMainDialog.getTableColumnNodes(),
                            mObj.getEditorView().getCollaborationView().getGraphView());
                    mObj.getMashupDataEditorSupport().synchDocument();
                    mObj.getGraphManager().generateGraph(mObj.getModel().getSQLDefinition());
                    //mObj.getGraphManager().getScene().layoutScene();
                    mObj.getGraphManager().setLog("Join view successfully edited.");
                }
            } catch (Exception ex) {
                 mObj.getGraphManager().setLog("Error adding Join view.");
            }
        }
    }
}