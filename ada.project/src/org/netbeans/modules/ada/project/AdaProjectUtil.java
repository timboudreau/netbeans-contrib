/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ada.project;

import org.netbeans.api.ada.platform.AdaPlatform;
import org.netbeans.api.ada.platform.AdaPlatformManager;
import org.netbeans.api.project.Project;
import org.netbeans.modules.ada.project.ui.properties.AdaProjectProperties;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

/**
 *
 * @author Andrea Lucarelli
 */
public class AdaProjectUtil {
    
    private AdaProjectUtil() {}
    
    public static AdaProject getProject (final Project project) {
        assert project != null;
        return project.getLookup().lookup(AdaProject.class);
    }
    
    public static AntProjectHelper getProjectHelper (final Project project) {
        final AdaProject adaProject = getProject(project);
        return adaProject == null ? null : adaProject.getHelper();
    }
    
    public static AdaPlatform getActivePlatform (final Project project) {
        final AdaProject adaProject = getProject(project);
        if (adaProject == null) {
            // No Ada project
            return null;
        }
        final AdaPlatformManager manager = AdaPlatformManager.getInstance();
        String platformId = adaProject.getEvaluator().getProperty(AdaProjectProperties.ACTIVE_PLATFORM);
        if (platformId == null) {
            platformId = manager.getDefaultPlatform();
        }
        if (platformId == null) {
            // No Ada platform in the IDE
            return null;
        }
        return manager.getPlatform(platformId);
    }
   
}
