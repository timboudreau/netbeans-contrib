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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.javafx.project.api;

import java.util.Map;
import javax.swing.JComponent;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;

/**
 * Provider of component that will be added to Run customizer panel that will
 * be used for additional customization of set of properties affected by given
 * run configuration. Implementation of the interface should be registered to
 * default lookup (e.g. META-INF/services).
 * 
 * @author Milan Kubec
 * @since 1.10
 */
public interface JavaFXRunConfigProvider {
    
    /**
     * Provides component that is added to Run Customizer panel of javafxproject
     * 
     * @param proj project to create the customizer component for
     * @param listener listener to be notified when properties should be updated
     */
    JComponent createComponent(Project proj, PropertyEvaluator evaluator, ConfigChangeListener listener);
    
    /**
     * Method is called when the config is changed (or created), 
     * component is updated according to properties of the config
     * 
     * @param props all properties (shared + private) of the new config;
     *        properites are not evaluated
     */
    void configUpdated(Map<String,String> props);
    
    /**
     * Callback listener for setting properties that are changed by interaction 
     * with the component
     */
    interface ConfigChangeListener {
        /**
         * Method is called when properties should be updated, null prop value 
         * means property will be removed from the property file, only shared 
         * properties are updated; properties are not evaluated
         * 
         * @param updates map holding updated properties
         */
        void propertiesChanged(Map<String,String> updates);
    }
    
}