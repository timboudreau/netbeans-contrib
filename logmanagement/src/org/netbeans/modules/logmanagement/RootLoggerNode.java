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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.logmanagement;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

/**
 *
 * @author Anuradha G
 */
public class RootLoggerNode extends AbstractNode {

    private static RootLoggerNode node;
    private Logger logger;

    RootLoggerNode(Logger logger) {
        super(Children.create(new LoggerFactory(logger), true)); 
        this.logger = logger;
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            public void run() {
                refresh();
            }
        });
    }

    public static synchronized RootLoggerNode getRootLoggerNode() {
        if (node == null) {
            node = new RootLoggerNode(LoggerManager.getInstance().getRoot());
        }
        return node;
    }

    Logger getLogger() {
        return logger;
    }

    @Override
    public String getDisplayName() {

        return NbBundle.getMessage(LoggerNode.class,"Log");
    }

    @Override
    public Image getIcon(int arg0) {
        return Utilities.loadImage("org/netbeans/modules/logmanagement/resources/logs.gif",true);
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    @Override
    public String getShortDescription() {
        return NbBundle.getMessage(LoggerNode.class,"Log_Levele_:_") + logger.getLevel();
    }

    public synchronized void refresh() {
        LoggerManager.getInstance().refresh();
        setChildren(Children.create(new LoggerFactory(logger), true));
    }

    @Override
    protected Sheet createSheet() {
        return new LoggerSheet(logger).getSheet();
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
        actionsList.add( new RefreshAction());
        actionsList.add( new ResetAction());
        actionsList.add( null);
        actionsList.addAll(asList);
        return actionsList.toArray(new Action[0]);
    }

    private class ResetAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public ResetAction() {
            putValue(NAME, NbBundle.getMessage(LoggerNode.class,"Reset_to_Default"));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                LogManager.getLogManager().readConfiguration();
                refresh();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (SecurityException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private class RefreshAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public RefreshAction() {
            putValue(NAME, NbBundle.getMessage(LoggerNode.class,"Refresh"));
        }

        public void actionPerformed(ActionEvent e) {
            refresh();
        }
    }
}