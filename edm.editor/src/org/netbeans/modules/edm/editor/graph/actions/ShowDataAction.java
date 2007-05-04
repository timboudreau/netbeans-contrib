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
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.sql.framework.evaluators.database.DB;
import org.netbeans.modules.sql.framework.evaluators.database.DBFactory;
import org.netbeans.modules.sql.framework.evaluators.database.StatementContext;
import org.netbeans.modules.sql.framework.model.SQLConstants;
import org.netbeans.modules.sql.framework.model.SQLDBModel;
import org.netbeans.modules.sql.framework.model.SQLJoinView;
import org.netbeans.modules.sql.framework.model.SQLObject;

/**
 *
 * @author karthikeyan s
 */
public class ShowDataAction extends AbstractAction {
    
    private MashupDataObject mObj;
    
    private SQLObject obj;
    
    private MashupGraphManager manager;
    
    private static final Image OUTPUT_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/showOutput.png"); // NOI18N
    
    /** Creates a new instance of EditJoinAction */
    public ShowDataAction(MashupDataObject dObj, SQLObject obj) {
        super("", new ImageIcon(OUTPUT_IMAGE));
        mObj = dObj;
        this.manager = dObj.getGraphManager();
        this.obj = obj;
    }
    
    public ShowDataAction(MashupDataObject dObj, SQLObject obj, String name) {
        super(name, new ImageIcon(OUTPUT_IMAGE));
        mObj = dObj;
        this.manager = dObj.getGraphManager();
        this.obj = obj;
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            manager.showOutput(obj, mObj.getModel().getSQLDefinition());
        } catch (Exception ex) {
            manager.setLog("Failed to generate output");
        }
    }
}