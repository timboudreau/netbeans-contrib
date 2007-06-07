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

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.sql.framework.model.SQLCondition;
import org.netbeans.modules.sql.framework.ui.view.IGraphViewContainer;
import org.netbeans.modules.sql.framework.ui.view.conditionbuilder.ConditionBuilderView;
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.utils.MashupGraphUtil;
import org.netbeans.modules.sql.framework.model.SQLObject;
import org.netbeans.modules.sql.framework.model.impl.SQLGroupByImpl;
import org.netbeans.modules.sql.framework.ui.view.conditionbuilder.ConditionBuilderUtil;

/**
 * This is an action class for invoking having condition editor.
 * @author karthikeyan s
 */
public class EditHavingConditionAction extends AbstractAction {
    
    private SQLGroupByImpl grpby;
    
    private MashupDataObject mObj;
    
    private MashupGraphManager manager;
    
    /** Creates a new instance of EditJoinAction */
    public EditHavingConditionAction(MashupDataObject dObj, SQLGroupByImpl op) {
        super("",new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.JOINCONDITION)));
        mObj = dObj;
        grpby = op;
        this.manager = dObj.getGraphManager();
    }
    
    /** Creates a new instance of EditJoinAction */
    public EditHavingConditionAction(MashupDataObject dObj, SQLGroupByImpl op, String name) {
        super(name,new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.JOINCONDITION)));
        mObj = dObj;
        grpby = op;
        this.manager = dObj.getGraphManager();
    }
    
    /**
     * Implements action performed.     
     */
    public void actionPerformed(ActionEvent e) {
        if (grpby != null && mObj.getEditorView() != null) {
            ConditionBuilderView conditionView = ConditionBuilderUtil.getHavingConditionBuilderView(getParentObject(),
                    (IGraphViewContainer)mObj.getEditorView().getGraphView().getGraphViewContainer());
            DialogDescriptor dd = new DialogDescriptor(conditionView, "Edit Join Condition", true, NotifyDescriptor.OK_CANCEL_OPTION, null, null);
            
            if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
                SQLCondition cond = (SQLCondition) conditionView.getPropertyValue();
                if (cond != null) {
                    SQLCondition oldCondition = grpby.getHavingCondition();
                    if (grpby != null && !cond.equals(oldCondition)) {
                        grpby.setHavingCondition(cond);                        
                        mObj.getMashupDataEditorSupport().synchDocument();
                        manager.setLog("Having clause sucessfully modified");
                    }
                }
            }
        }
    }
    
    /*
     *  method to object parent object for the given sql group by impl object.
     */
    private SQLObject getParentObject() {
        SQLObject obj = (SQLObject) grpby.getParentObject();
        return mObj.getModel().getSQLDefinition().getObject(obj.getId(), obj.getObjectType());        
    }    
}