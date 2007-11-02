/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.logmanagement;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;
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
        return NbBundle.getMessage(LoggerNode.class, "Logger_Level", logger.getLevel());
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