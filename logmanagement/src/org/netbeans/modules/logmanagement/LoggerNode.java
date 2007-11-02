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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.logmanagement;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;
import java.util.logging.LogManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.logmanagement.actions.FormatterChooserAction;
import org.netbeans.modules.logmanagement.actions.LevelChooserAction;
import org.netbeans.modules.logmanagement.handlers.CustomHandler;
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
    private InputOutput inputOutput;

    LoggerNode(Logger logger) {
        super(!logger.getChildren().isEmpty() ? Children.create(new LoggerFactory(logger), true) : Children.LEAF);
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
        return NbBundle.getMessage(LoggerNode.class, "Logger_Level", logger.getLevel());
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

    private synchronized InputOutput getInputOutput() {
        if (inputOutput == null) {
            Action[] actions = new Action[2];
            //TODO
        //Action[] actions = new Action[3];


            CustomHandler streamHandler = new CustomHandler();
            actions[0] = new LevelChooserAction(logger);
            actions[1] = new FormatterChooserAction(streamHandler);
            //TODO
        //actions[2] = new FilterAction();
            inputOutput = IOProvider.getDefault().getIO(logger.getName(), actions);
            OutputWriter w = inputOutput.getOut();
            w.append(NbBundle.getMessage(LoggerNode.class, "FMT_Output_Note", logger.getLevel()) + "\n");
            streamHandler.setOutputWriter(w);
            LogManager.getLogManager().getLogger(logger.getName()).addHandler(streamHandler);
        }
        return inputOutput;
    }
}