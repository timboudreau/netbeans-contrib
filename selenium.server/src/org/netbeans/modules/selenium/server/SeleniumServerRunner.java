/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.selenium.server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.BindException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.server.properties.InstanceProperties;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.RemoteControlConfiguration;

/**
 *
 * @author Jindrich Sedek
 */
class SeleniumServerRunner implements Runnable, PropertyChangeListener {

    private static final SeleniumServerRunner instance = new SeleniumServerRunner();
    private SeleniumServer server = null;
    private boolean isRunning = false;
    private static Action action = null;
    private static Task latestTask = null;

    private SeleniumServerRunner() {
    }

    static Task startServer() {
        if (isRunning()) {
            return Task.EMPTY;
        }
        action = Action.START;
        return postTask();
    }

    static Task stopServer() {
        if (!isRunning()) {
            return Task.EMPTY;
        }
        action = Action.STOP;
        return postTask();
    }

    static Task restartServer() {
        if (!isRunning()) {
            return startServer();
        } else {
            action = Action.RESTART;
            return postTask();
        }
    }

    static boolean isRunning() {
        return instance.isRunning;
    }

    private static Task postTask(){
        Task t = RequestProcessor.getDefault().post(instance);
        latestTask = t;
        return t;
    }

    @Override
    public void run() {
        try {
            if (server == null) {
                initializeServer();
            }
            switch (action) {
                case START:
                    server.start();
                    break;
                case STOP:
                    server.stop();
                    break;
                case RESTART:
                    server.stop();
                    server.start();
                    break;
                case RELOAD:
                    server.stop();
                    server = null;
                    initializeServer();
                    server.start();
                    break;
                default:
                    assert false : "Invalid option";
            }
            if (action == null) {
                return;
            }
            isRunning = (!action.equals(Action.STOP));
            action = null;
        } catch (BindException bi){
            Logger.getLogger(SeleniumServerRunner.class.getName()).log(Level.INFO,
                    "Port already in use - the server is probably already running.", bi);
        } catch (Exception exc) {
            Exceptions.printStackTrace(exc);
        }
    }

    private void initializeServer() throws Exception {
        InstanceProperties ip = SeleniumProperties.getInstanceProperties();
        RemoteControlConfiguration conf = new RemoteControlConfiguration();
        int port = ip.getInt(SeleniumProperties.PORT, RemoteControlConfiguration.DEFAULT_PORT);
        conf.setPort(port);
        server = new SeleniumServer(conf);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (SeleniumProperties.PORT.equals(evt.getPropertyName())){
            action = Action.RELOAD;
            RequestProcessor.getDefault().post(instance);
        }
    }

    // listen on SeleniumProperties
    static PropertyChangeListener getPropertyChangeListener() {
        return instance;
    }

    private static enum Action {

        START, STOP, RESTART, RELOAD
    }

    static void waitAllTasksFinished(){
        if (latestTask == null){
            return;
        }
        while (!latestTask.isFinished()){
            latestTask.waitFinished();
        }
    }
}
