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
import org.netbeans.api.server.properties.InstanceProperties;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor.Message;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openqa.selenium.server.SeleniumServer;

/**
 *
 * @author Jindrich Sedek
 */
class SeleniumServerRunner implements Runnable, PropertyChangeListener {

    private static final SeleniumServerRunner instance = new SeleniumServerRunner();
    private SeleniumServer server = null;
    private boolean isRunning = false;
    private static Action action = null;

    private SeleniumServerRunner() {
    }

    static Task startServer() {
        if (isRunning()) {
            return Task.EMPTY;
        }
        action = Action.START;
        return RequestProcessor.getDefault().post(instance);
    }

    static Task stopServer() {
        if (!isRunning()) {
            return Task.EMPTY;
        }
        action = Action.STOP;
        return RequestProcessor.getDefault().post(instance);
    }

    static Task restartServer() {
        if (!isRunning()) {
            return startServer();
        } else {
            action = Action.RESTART;
            return RequestProcessor.getDefault().post(instance);
        }
    }

    static boolean isRunning() {
        return instance.isRunning;
    }

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
            isRunning = (!action.equals(Action.STOP));
            action = null;
        } catch (Exception exc) {
            Exceptions.printStackTrace(exc);
        }
    }

    private void initializeServer() throws Exception {
        InstanceProperties ip = SeleniumProperties.getInstanceProperties();
        int port = ip.getInt(SeleniumProperties.PORT, SeleniumServer.DEFAULT_PORT);
        server = new SeleniumServer(port);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Message desc = new DialogDescriptor.Message("Please restart NetBeans to aply changes", DialogDescriptor.WARNING_MESSAGE);
        DialogDisplayer.getDefault().notify(desc);
        // TODO replace message with server reload after selenium update to version 1.0
//        if (SeleniumProperties.PORT.equals(evt.getPropertyName())){
//            action = Action.RELOAD;
//            RequestProcessor.getDefault().post(instance);
//        }

    }

    // listen on SeleniumProperties
    static PropertyChangeListener getPropertyChangeListener() {
        return instance;
    }

    private static enum Action {

        START, STOP, RESTART, RELOAD
    }
}
