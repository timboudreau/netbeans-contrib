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

package org.netbeans.modules.portalpack.websynergy.portlets.hook.api;

import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.websynergy.portlets.util.TemplateNotFoundException;
import org.netbeans.modules.portalpack.websynergy.portlets.util.TemplateUtil;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author satyaranjan
 * @author Santh Chetan Chadalavada
 */
public abstract class HookTypeHandler {
    
    
    public WizardDescriptor.Panel getConfigPanel() {
        return new WizardDescriptorPanelWrapper(new DefaultConfigPanel());
    }
    
    public void addHook(WebModule wm, WizardDescriptor desc, Set result) {
        if(wm == null)
            return;
        
        String LIFERAY_HOOK_XML = "liferay-hook";
        FileObject webInf = wm.getWebInf();
        FileObject hookXml = webInf.getFileObject(LIFERAY_HOOK_XML, "xml");
        if(hookXml == null) {
            try {
                //create HookXML
                TemplateUtil templateUtil = new TemplateUtil("hook/templates");
                hookXml = templateUtil.createFileFromTemplate("liferayhook.template", webInf, LIFERAY_HOOK_XML, "xml");
            } catch (TemplateNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        addHookDefinition(hookXml, desc);
        createAdditionalFiles(Templates.getProject(desc), desc, result);
    }
    //add entry to hook xml
    public abstract void addHookDefinition(FileObject hookXml, WizardDescriptor desc);
    public abstract void createAdditionalFiles(Project project, WizardDescriptor desc, Set result);
    
}
