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

/*
 * ThemeBuilderProjectType.java
 *
 * Created on March 2, 2007, 6:24 PM
 */

package org.netbeans.modules.themebuilder.project;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.AntBasedProjectType;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

/**
 * Ant based project type for Theme Builder.
 * @author winstonp
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.spi.project.support.ant.AntBasedProjectType.class)
public class ThemeBuilderProjectType implements AntBasedProjectType{
    
    /**
     * The project type for Theme Builder
     */
    public static final String PROJECT_TYPE = "org.netbeans.modules.themebuilder.project";
    /**
     * Project Configuration name  
     */
    private static final String PROJECT_CONFIGURATION_NAME = "data"; // NOI18N
    /**
     * Project Configuration name Space
     */
    public static final String PROJECT_CONFIGURATION_NAMESPACE = "http://www.netbeans.org/ns/themebuilder/3"; // NOI18N
     
    
    public String getType() {
        return PROJECT_TYPE;
    }

    public Project createProject(AntProjectHelper helper) throws IOException {
        return new ThemeBuilderProject (helper);         
    }

    public String getPrimaryConfigurationDataElementName(boolean shared) {
        return PROJECT_CONFIGURATION_NAME;
    }

    public String getPrimaryConfigurationDataElementNamespace(boolean shared) {
        return PROJECT_CONFIGURATION_NAMESPACE;
    }

}