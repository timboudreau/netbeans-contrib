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
package org.netbeans.modules.portalpack.visualweb.api;

import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Satyaranjan
 */
public class JsfPortletProjectUtil {

    public static WebModule getWebModule(Project project) {
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());

        if (wm != null) {
            return wm;
        }

        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (SourceGroup group : groups) {
            wm = WebModule.getWebModule(group.getRootFolder());
            if (wm != null) {
                return wm;
            }
        }

        return null;
    }

    /**
     * Convenience method to obtain the WEB-INF folder.
     * @param project the Project object
     * @return the FileObject of the WEB-INF folder
     */
    public static FileObject getWebInf(Project project) {
        if (project == null) {
            return null;
        }

        WebModule wm = getWebModule(project);
        if (wm == null) {
            return null;
        }

        return wm.getWebInf();
    }

    /**
     * Obtain the portlet support helper object from the project
     * @param project the Project object
     * @return the portlet support object or null if the project is not
     * capable of supporting portlets
     */
    public static JsfPortletSupport getPortletSupport(Project project) {
        FileObject webInf = getWebInf(project);
        if (webInf == null) {
            return null;
        }

        FileObject fo = webInf.getFileObject("portlet.xml");  // NOI18N
        if (fo == null) {
            return null;
        } else {
            // TODO: This really should be a lookup on web/project.  Currently the module dependencies
            // are incorrect.  The web/project should provide the interface to JsfPortletSupport and hide
            // the implementation.  The web/project should contain the JsfPortletSupport implementation or
            // there should be an API/SPI arrangement set up to elliminate the web/project to project/jsfportlet
            // module dependecy.
            //
            // Current hack:  Because this method is likely to be called numerous
            // times by time-critical modules like "designer", we MUST place the
            // implementation either in the project/jsfprojectapi or in project/jsfportlet
            // and create a dependencey between project/jsfprojectapi and project/jsfportlet.
            // Since the portlet support implementation also needs module portletcontainer interaction,
            // we will put the implementation in project/jsfportlet to elliminate the need for
            // a dependency between project/jsfprojectapi and portletcontainer.
            // In order to elliminate the module dependency between
            // project/jsfprojectapi and project/jsfportlet, we would need to use the
            // layer.xml files and the built-in lookup facility to find the interface.  This would
            // be too time-consuming to do each time this static method was called.  We
            // can't put a static reference to the implementation since the the user
            // can have multiple projects open at once.  The portlet suppport for the first project
            // opened would always be the portlet support given out to all projects.
            // - David Botterill 5/13/2005
            return new JsfPortletSupportImpl(project);
        }

    }
}
