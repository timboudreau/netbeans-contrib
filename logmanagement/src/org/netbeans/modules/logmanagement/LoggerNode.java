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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */ /*
 * LoggerNode.java
 *
 * Created on Sep 11, 2007, 1:47:28 PM
 *
 */

package org.netbeans.modules.logmanagement;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.logmanagement.ui.LogViewerConfigUI;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;



/**
 *
 * @author Anuradha G
 */
class LoggerNode extends AbstractNode {

    private Logger logger;


    LoggerNode(Logger logger) {
        super(!logger.getChilderns().isEmpty() ? Children.create(new LoggerFactory(logger), true) : Children.LEAF);
        this.logger = logger;
    }

    @Override
    public String getDisplayName() {

        return logger.getName();
    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/netbeans/modules/logmanagement/resources/logs.gif", true);
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    @Override
    public String getShortDescription() {
        return NbBundle.getMessage(LoggerNode.class, "Log_Levele_:_") + logger.getLevel();
    }

    /**
     *
     * @param popup
     * @return
     */
    public Logger getLogger() {
        return logger;
    }

    public void showOutput() {
        LogViewerConfigUI.CreateLogViewerConfigUI(logger);
    }



    /**
     *
     * @param popup
     * @return
     */
    @Override
    public Action[] getActions(boolean popup) {
        Action[] actions = super.getActions(popup);
        List<Action> asList = Arrays.asList(actions);
        List<Action> actionsList = new ArrayList<Action>();
        actionsList.add(new OutputAction());
        actionsList.add(null);
        actionsList.addAll(asList);
        return actionsList.toArray(new Action[0]);
    }

    @Override
    protected Sheet createSheet() {
        return new LoggerSheet(logger).getSheet();
    }

    private class OutputAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public OutputAction() {
            putValue(NAME, NbBundle.getMessage(LoggerNode.class, "Show_output"));
        }

        public void actionPerformed(ActionEvent e) {
            showOutput();
        }
    }

   
}