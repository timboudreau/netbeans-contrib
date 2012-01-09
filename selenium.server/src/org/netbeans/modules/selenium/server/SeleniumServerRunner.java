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
import java.lang.reflect.InvocationTargetException;
import java.net.BindException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.server.properties.InstanceProperties;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;

/**
 *
 * @author Jindrich Sedek
 * @author Martin Fousek
 */
class SeleniumServerRunner implements Runnable, PropertyChangeListener {

    private static final Logger LOGGER = Logger.getLogger(SeleniumServerRunner.class.getName());

    private static final SeleniumServerRunner instance = new SeleniumServerRunner();
    private static Object server = null;
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
                    callSeleniumServerMethod("start");
                    break;
                case STOP:
                    callSeleniumServerMethod("stop");
                    break;
                case RESTART:
                    callSeleniumServerMethod("stop");
                    callSeleniumServerMethod("start");
                    break;
                case RELOAD:
                    callSeleniumServerMethod("stop");
                    server = null;
                    initializeServer();
                    callSeleniumServerMethod("start");
                    break;
                default:
                    assert false : "Invalid option";
            }
            if (action == null) {
                return;
            }
            isRunning = (!action.equals(Action.STOP));
            action = null;
        } catch (BindException bi) {
            LOGGER.log(Level.INFO, "Port already in use - the server is probably already running.", bi); //NOI18N
        } catch (Exception exc) {
            LOGGER.log(Level.INFO, null, exc);
        }
    }

    protected static URLClassLoader getSeleniumServerClassLoader() {
        URL url = null;
        try {
            url = InstalledFileLocator.getDefault().locate(
                        "modules/ext/selenium/selenium-server-2.16.1.jar", //NOI18N
                        null, //NOI18N
                        false).toURI().toURL();
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return URLClassLoader.newInstance(new URL[] {url}); //NOI18N
    }

    private void callSeleniumServerMethod(String method) {
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader curr = server.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(curr);
            server.getClass().getMethod(method).invoke(server);
        } catch (IllegalAccessException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (NoSuchMethodException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (SecurityException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (InvocationTargetException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    private static void initializeServer() throws Exception {
        URLClassLoader urlClassLoader = getSeleniumServerClassLoader();
        Class seleniumServer = urlClassLoader.loadClass("org.openqa.selenium.server.SeleniumServer"); //NOI18N
        Class remoteControlConfiguration = urlClassLoader.loadClass(
                "org.openqa.selenium.server.RemoteControlConfiguration"); //NOI18N

        InstanceProperties ip = SeleniumProperties.getInstanceProperties();
        Object remoteControlConfigurationInstance = remoteControlConfiguration.newInstance();
        int port = ip.getInt(
                SeleniumProperties.PORT,
                SeleniumProperties.getSeleniumDefaultPort()); //NOI18N
        remoteControlConfiguration.getMethod("setPort", int.class).invoke(
                remoteControlConfigurationInstance, port); //NOI18N
        server = seleniumServer.getConstructor(remoteControlConfiguration).
                newInstance(remoteControlConfigurationInstance);
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
