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
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;



/**
 *
 * @author Anuradha G
 */
class LoggerNode extends AbstractNode {

    private Logger logger;
    private InputOutput io;

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
        getInputOutput().select();
    }

    private synchronized InputOutput getInputOutput() {
        if (io == null) {
            io = IOProvider.getDefault().getIO(logger.getName(), false);

            final OutputWriter w = io.getOut();

            StreamHandler streamHandler = new CustomHandler(w, new SimpleFormatter());
            LogManager.getLogManager().getLogger(logger.getName()).addHandler(streamHandler);
        }
        return io;
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

    private static class CustomHandler extends StreamHandler {

        private OutputWriter w;

        public CustomHandler(OutputWriter w, Formatter formatter) {
            this.w = w;
            setFormatter(formatter);
        }

        @Override
        public synchronized void publish(LogRecord record) {
            w.print(getFormatter().format(record));
        }
    }
}