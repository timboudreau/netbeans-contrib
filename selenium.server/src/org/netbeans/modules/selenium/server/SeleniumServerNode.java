/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.selenium.server;

import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jindrich Sedek
 */
class SeleniumServerNode extends AbstractNode implements TaskListener {

    private final Action startAction = new StartServerAction();
    private final Action stopAction = new StopServerAction();
    private final Action restartAction = new RestartServerAction();
    private static final String RUNNING_ICON 
            = "org/netbeans/modules/glassfish/common/resources/running.png"; // NOI18N
    private static final String IMAGE_PATH = "org/netbeans/modules/selenium/resources/logo16.png";  //NOI18N
    private static final Image IMG = ImageUtilities.loadImage(IMAGE_PATH);
    private static SeleniumServerNode instance;
    
    static synchronized SeleniumServerNode getInstance() {
        if (instance == null){
            instance = new SeleniumServerNode();
        }
        return instance;
    }

    SeleniumServerNode() {
        super(Children.LEAF);
        setDisplayName(NbBundle.getMessage(SeleniumServerInstance.class, "DisplayName"));
        setIconBaseWithExtension("org/netbeans/modules/selenium/resources/logo16.png");
        checkEnabledActions();
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] superActions = super.getActions(context);
        Action[] result = new Action[superActions.length + 4];
        for (int i = 0; i < superActions.length; i++) {
            result[i + 4] = superActions[i];
        }
        result[0] = startAction;
        result[1] = stopAction;
        result[2] = restartAction;
        result[3] = null;
        return result;
    }

    @Override
    protected Sheet createSheet() {
        return SeleniumProperties.createSheet();
    }

    @Override
    public Image getIcon(int type) {
        if (SeleniumServerRunner.isRunning()){
            Image badge = ImageUtilities.loadImage(RUNNING_ICON);
            if (badge!= null){
                return ImageUtilities.mergeImages(IMG, badge, 15, 8);
            }
        }
        return IMG;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    // --------------- actions ---------------------- //

    public void taskFinished(Task task) {
        checkEnabledActions();
        fireIconChange();
    }

    private class RestartServerAction extends SeleniumNodeAction {

        @Override
        public void actionPerformed(ActionEvent ev) {
            Task serverTask = SeleniumServerRunner.restartServer();
            serverTask.addTaskListener(SeleniumServerNode.this);
        }

        @Override
        public String getName() {
            return NbBundle.getMessage(StartServerAction.class, "RestartServer");
        }
    }

    private class StartServerAction extends SeleniumNodeAction {

        @Override
        public void actionPerformed(ActionEvent ev) {
            Task serverTask = SeleniumServerRunner.startServer();
            serverTask.addTaskListener(SeleniumServerNode.this);
        }

        @Override
        public String getName() {
            return NbBundle.getMessage(StartServerAction.class, "StartServer");
        }
    }

    private class StopServerAction extends SeleniumNodeAction {

        @Override
        public void actionPerformed(ActionEvent ev) {
            Task serverTask = SeleniumServerRunner.stopServer();
            serverTask.addTaskListener(SeleniumServerNode.this);
        }

        @Override
        public String getName() {
            return NbBundle.getMessage(StartServerAction.class, "StopServer");
        }
    }

    private abstract class SeleniumNodeAction extends SystemAction {

        @Override
        public HelpCtx getHelpCtx() {
            return HelpCtx.DEFAULT_HELP;
        }
    }

    private void checkEnabledActions() {
        startAction.setEnabled(!SeleniumServerRunner.isRunning());
        stopAction.setEnabled(SeleniumServerRunner.isRunning());
    }
}
