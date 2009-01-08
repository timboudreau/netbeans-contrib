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
import java.util.List;
import org.netbeans.api.server.properties.InstanceProperties;
import org.netbeans.api.server.properties.InstancePropertiesManager;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.NbBundle;
import org.openqa.selenium.server.SeleniumServer;

/**
 *
 * @author Jindrich Sedek
 */
class SeleniumProperties {

    static final String PORT = "Port";
    private static InstanceProperties instanceProps;
    private static final String NAMESPACE = "Selenium server properties namespace"; //NOI18N

    static Sheet createSheet() {
        InstanceProperties props = getInstanceProperties();
        Sheet sheet = Sheet.createDefault();
        Set set = sheet.get(Sheet.PROPERTIES);
        Node.Property ip = new ServerIntProperty(PORT, props);      //NOI18N
        set.put(ip);
        return sheet;
    }

    static InstanceProperties getInstanceProperties(){
        if (instanceProps == null){
            InstancePropertiesManager manager = InstancePropertiesManager.getInstance();
            List<InstanceProperties> allProps = manager.getProperties(NAMESPACE);
            assert (allProps.size() <= 1);
            if (!allProps.isEmpty()) {
                instanceProps = allProps.iterator().next();
            } else {
                instanceProps = manager.createProperties(NAMESPACE);
                instanceProps.putInt(PORT, SeleniumServer.DEFAULT_PORT);
                allProps.add(instanceProps);
            }
        }
        return instanceProps;
    }

    private static final class ServerIntProperty extends Node.Property<Integer> {

        private String propertyName;
        private InstanceProperties props;

        public ServerIntProperty(String propertyName, InstanceProperties props) {
            super(Integer.class);
            this.propertyName = propertyName;
            this.props = props;
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
        public Integer getValue() throws IllegalAccessException, InvocationTargetException {
            return props.getInt(propertyName, 0);
        }

        @Override
        public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            Integer oldValue = props.getInt(propertyName, val);
            if (oldValue.equals(val)){
                return;
            }
            props.putInt(propertyName, val);
            PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, val);
            SeleniumServerRunner.getPropertyChangeListener().propertyChange(evt);
        }
    }
}
