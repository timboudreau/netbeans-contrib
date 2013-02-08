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

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.server.properties.InstanceProperties;
import org.netbeans.api.server.properties.InstancePropertiesManager;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.NbBundle;

/**
 *
 * @author Jindrich Sedek
 * @author Martin Fousek
 */
public class SeleniumProperties {

    private static Logger LOGGER = Logger.getLogger(SeleniumProperties.class.getName());

    public static int seleniumDefaultPort = -1;
    public static final String PORT = "Port"; //NOI18N
    public static final String START_ON_STARTUP = "Startup"; //NOI18N
    public static final String FIREFOX_PROFILE = "FirefoxProfile"; //NOI18N
    public static final String SINGLE_WINDOW = "SingleWindow"; //NOI18N
    public static final String USER_EXTENSIONS = "UserExtensions"; //NOI18N
    
    private static InstanceProperties instanceProps;
    private static final String NAMESPACE = "Selenium server properties namespace"; //NOI18N

    static Sheet createSheet() {
        InstanceProperties props = getInstanceProperties();
        Sheet sheet = Sheet.createDefault();
        Set set = sheet.get(Sheet.PROPERTIES);
        set.put(new ServerIntProperty(PORT, props));
        set.put(new ServerBoolProperty(START_ON_STARTUP, props));
        set.put(new ServerStringProperty(FIREFOX_PROFILE, props));
        set.put(new ServerBoolProperty(SINGLE_WINDOW, props));
        set.put(new ServerStringProperty(USER_EXTENSIONS, props));
        return sheet;
    }

    /**
     * Gets the default server port contained in the Selenium server configuration.
     * @return default Selenium server port
     */
    public static int getSeleniumDefaultPort() {
        if (seleniumDefaultPort == -1) {
            try {
                URLClassLoader urlClassLoader = SeleniumServerRunner.getSeleniumServerClassLoader();
                Class remoteControlConfiguration = urlClassLoader.loadClass(
                        "org.openqa.selenium.server.RemoteControlConfiguration"); //NOI18N
                seleniumDefaultPort = remoteControlConfiguration.getDeclaredField(
                        "DEFAULT_PORT").getInt(remoteControlConfiguration); //NOI18N
            } catch (NoSuchFieldException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return seleniumDefaultPort;
    }

    public static InstanceProperties getInstanceProperties(){
        if (instanceProps == null){
            InstancePropertiesManager manager = InstancePropertiesManager.getInstance();
            synchronized (NAMESPACE){
                List<InstanceProperties> allProps = manager.getProperties(NAMESPACE);
                if (!allProps.isEmpty()) {
                    instanceProps = allProps.iterator().next();
                } else {
                    instanceProps = manager.createProperties(NAMESPACE);
                    instanceProps.putInt(PORT, getSeleniumDefaultPort());
                    instanceProps.putBoolean(START_ON_STARTUP, true);
                    instanceProps.putString(FIREFOX_PROFILE, ""); //NOI18N
                    instanceProps.putBoolean(SINGLE_WINDOW, false);
                    instanceProps.putString(USER_EXTENSIONS, ""); //NOI18N
                    allProps.add(instanceProps);
                }
            }
        }
        return instanceProps;
    }

    private static final class ServerBoolProperty extends ServerProperty<Boolean> {

        public ServerBoolProperty(String propertyName, InstanceProperties props) {
            super(Boolean.class, propertyName, props);
        }

        @Override
        public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
            return props.getBoolean(getName(), true);
        }

        @Override
        public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            Boolean oldValue = getValue();
            if (oldValue.equals(val)){
                return;
            }
            props.putBoolean(getName(), val);
        }

    }

    private static final class ServerIntProperty extends ServerProperty<Integer> {

        public ServerIntProperty(String propertyName, InstanceProperties props) {
            super(Integer.class, propertyName, props);
        }

        @Override
        public Integer getValue() throws IllegalAccessException, InvocationTargetException {
            return props.getInt(getName(), 0);
        }

        @Override
        protected void writeNewValue(Integer val) {
            props.putInt(getName(), val);
        }

    }

    private static final class ServerStringProperty extends ServerProperty<String> {

        public ServerStringProperty(String propertyName, InstanceProperties props) {
            super(String.class, propertyName, props);
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return props.getString(getName(), ""); //NOI18N
        }

        @Override
        protected void writeNewValue(String val) {
            props.putString(getName(), val);
        }

    }

    private static abstract class ServerProperty<T> extends Node.Property<T>{

        protected InstanceProperties props;

        public ServerProperty(Class<T> type, String propertyName, InstanceProperties props) {
            super(type);
            this.props = props;
            setName(propertyName);
            setDisplayName(NbBundle.getMessage(SeleniumProperties.class, "displayName_" + propertyName));
            setShortDescription(NbBundle.getMessage(SeleniumProperties.class, "desc_" + propertyName));
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean canWrite() {
            return true;
        }

        @Override
        public void setValue(T val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            T oldValue = getValue();
            if (oldValue.equals(val)){
                return;
            }
            writeNewValue(val);
            PropertyChangeEvent evt = new PropertyChangeEvent(this, getName(), oldValue, val);
            SeleniumServerRunner.getPropertyChangeListener().propertyChange(evt);
        }

        protected void writeNewValue(T val){}

    }
}


