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
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.openide.util.NbBundle;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.components.EDMOutputTopComponent;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.utils.MashupGraphUtil;
import org.netbeans.modules.sql.framework.model.SQLDefinition;
import org.netbeans.modules.sql.framework.ui.SwingWorker;
import org.netbeans.modules.sql.framework.ui.utils.UIUtil;
import org.netbeans.modules.sql.framework.ui.view.validation.SQLValidationView;

/**
 *
 * @author karthikeyan s
 */
public class ValidationAction extends AbstractAction {
    
    private MashupDataObject mObj;
    
    private SQLValidationView validationView;
    
    /** Creates a new instance of EditJoinAction */
    public ValidationAction(MashupDataObject dObj) {
        super("",new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.VALIDATE)));
        mObj = dObj;
        validationView = new SQLValidationView(mObj.getEditorView().getGraphView());
        String validationLabel = NbBundle.getMessage(ValidationAction.class, "LBL_validationview_tab");
        validationView.setName(validationLabel);
    }
    
    public ValidationAction(MashupDataObject dObj, String name) {
        super(name,new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.VALIDATE)));
        mObj = dObj;
        validationView = new SQLValidationView(mObj.getEditorView().getGraphView());
        String validationLabel = NbBundle.getMessage(ValidationAction.class, "LBL_validationview_tab");
        validationView.setName(validationLabel);        
    }
    
    public void actionPerformed(ActionEvent e) {
        validationView.clearView();
        ValidationThread vThread = new ValidationThread(mObj.getModel().getSQLDefinition());
        vThread.start();
        EDMOutputTopComponent outputWindow = EDMOutputTopComponent.findInstance();
        outputWindow.addComponent(validationView);
        outputWindow.open();
        outputWindow.setVisible(true);        
    }
    
    class ValidationThread extends SwingWorker {
        private SQLDefinition execModel;
        private List list;
        
        public ValidationThread(SQLDefinition execModel) {
            this.execModel = execModel;
        }
        
        /**
         * Compute the value to be returned by the <code>get</code> method.
         *
         * @return object
         */
        public Object construct() {
            UIUtil.startProgressDialog(mObj.getName(), "Validating model...");
            list = execModel.validate();
            return "";
        }
        
        //Runs on the event-dispatching thread.
        public void finished() {
            if (execModel.getAllObjects().size() == 0) {
                validationView.appendToView(NbBundle.getMessage(ValidationAction.class, "MSG_validation_noitems"));
            } else if (execModel.getSourceTables().size() == 0) {
                validationView.appendToView(NbBundle.getMessage(ValidationAction.class, "MSG_validation_nosource"));
            } else if (list.size() == 0) {                
                validationView.appendToView(NbBundle.getMessage(ValidationAction.class, "MSG_validation_ok"));                
            } else {
                validationView.setValidationInfos(list);
            }
            UIUtil.stopProgressDialog();
        }
    }
}