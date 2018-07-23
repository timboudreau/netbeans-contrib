/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.portalpack.cms;

import java.io.File;

import java.util.Set;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Joshna
 */
public class CMSFrameworkProvider extends WebFrameworkProvider {

    private CMSFrameworkWizardPanel cmsFrameworkWizardPanel;

    /** Creates a new instance of CMSFrameworkProvider */
    public CMSFrameworkProvider() {
        super(NbBundle.getBundle(CMSFrameworkProvider.class).getString("OpenIDE-Module-Name"), NbBundle.getBundle(CMSFrameworkProvider.class).getString("OpenIDE-Module-Short-Description"));
    }

    @Override
    public WebModuleExtender createWebModuleExtender(WebModule wm, ExtenderController controller) {

        boolean customizer = (wm != null && isInWebModule(wm));
        cmsFrameworkWizardPanel = new CMSFrameworkWizardPanel(this, wm, controller);

        return cmsFrameworkWizardPanel;
    }

    public Set extendImpl(WebModule wm) {
        final FileObject documentBase = wm.getDocumentBase();
        Project project = FileOwnerQuery.getOwner(documentBase);
        try {

            String selectedValue = cmsFrameworkWizardPanel.getSelectedValueFromVisualPanel();
            if (selectedValue.equals("JCR")) {
                //createPropertyFiles(wm, selectedValue);
                Library cmsLibrary = LibraryManager.getDefault().getLibrary("cms");
                Library cmsTagLibrary = LibraryManager.getDefault().getLibrary("cmstaglib");//NOI18N
               if (cmsLibrary != null) {

                    Sources sources = (Sources) project.getLookup().lookup(Sources.class);
                    SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

                    for (int i = 0; i < groups.length; i++) {
                        ProjectClassPathModifier.addLibraries(new Library[]{cmsLibrary}, groups[i].getRootFolder(), ClassPath.COMPILE);
                    }
                } else {

                }
                if (cmsTagLibrary != null) {

                    Sources sources = (Sources) project.getLookup().lookup(Sources.class);
                    SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

                    for (int i = 0; i < groups.length; i++) {
                        ProjectClassPathModifier.addLibraries(new Library[]{cmsTagLibrary}, groups[i].getRootFolder(), ClassPath.COMPILE);
                    }
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isInWebModule(WebModule webModule) {

        try {
           
              ClassPath cp = ClassPath.getClassPath(webModule.getDocumentBase(), ClassPath.COMPILE);
       
              if(cp == null || cp.findResource("com/sun/portal/cms/mirage/model/custom/Content.class") == null) { //NOI18N)
                  return false;
              }
              
        } catch (Exception e) {
            return false;

        }
        return true;
    }

    public File[] getConfigurationFiles(WebModule arg0) {
        return null;
    }

  
}
