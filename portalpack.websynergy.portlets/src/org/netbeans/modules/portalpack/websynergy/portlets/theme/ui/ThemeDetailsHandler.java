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

package org.netbeans.modules.portalpack.websynergy.portlets.theme.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.websynergy.common.WebSpacePropertiesUtil;
import org.netbeans.modules.portalpack.websynergy.portlets.theme.helper.LookAndFeelXMLHelper;
import org.netbeans.modules.portalpack.websynergy.portlets.util.PluginXMLUtil;
import org.netbeans.modules.portalpack.websynergy.portlets.util.TemplateNotFoundException;
import org.netbeans.modules.portalpack.websynergy.portlets.util.TemplateUtil;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.w3c.dom.Element;

/**
 *
 * @author Santh Chetan Chadalavada
 */
public class ThemeDetailsHandler {

    Project project;
    WizardDescriptor wd;
    public ThemeDetailsHandler(Project project, WizardDescriptor wd) {
        this.project = project;
        this.wd = wd;
    }

    public String createThemeConfigurationFiles(Set result) {
        String themeId = null;
        String themeName = null;
        String themeFolder = null;

        PSConfigObject psconfig = WebSpacePropertiesUtil.getSelectedServerProperties(project);
        int liferayVersion = 0;
        String version = "5_1_0";
        String dtdVersion = "5.1.0";
        if (psconfig != null) {
            liferayVersion = WebSpacePropertiesUtil.getLiferayVersion(psconfig);
            if(liferayVersion >= 6000) {
                version = "5_2_0";
                dtdVersion = "6.0";
            } else if (liferayVersion >= 5200) {
                version = "5_2_0";
                dtdVersion = "5.2.0";
            } else {
                version = "5_1_0";
                dtdVersion = "5.1.0";
            }
        }
        Map values = new HashMap();

        values.put("DTD_VERSION", dtdVersion);
        values.put("VERSION", version);

        FileObject webInf = PortletProjectUtils.getWebModule(project).getWebInf();
        FileObject lookAndFeelXml = webInf.getFileObject("liferay-look-and-feel", "xml");

        TemplateUtil templateUtil = new TemplateUtil("theme/templates");
        FileObject templateFile = null;
        try {
            templateFile = templateUtil.getTemplateFile("liferay-look-and-feel.xml");
        } catch (TemplateNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (templateFile != null) {
                try {
                    if (lookAndFeelXml == null) {
                        lookAndFeelXml = templateUtil.mergeTemplateToFile(
                                templateFile, webInf, "liferay-look-and-feel", values);
                    }
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                themeId = (String) wd.getProperty("themeId");
                themeName = (String) wd.getProperty("themeName");
                themeFolder = (String) wd.getProperty("themeFolder");
            //PluginXMLUtil util = new PluginXMLUtil(lookAndFeelXml);
            LookAndFeelXMLHelper util = new LookAndFeelXMLHelper(lookAndFeelXml);
            Element themeIdElem = util.addThemeId(themeId, themeName);
            if (themeFolder != null && themeFolder.trim().length() > 0) {
                util.addThemeFolderDetails(themeFolder, themeIdElem);
            }
            util.store();
        }
        result.add(lookAndFeelXml);

        return themeFolder;
    }
}
