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

package org.netbeans.modules.portalpack.websynergy.portlets.hook.jsp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.portalpack.websynergy.portlets.hook.api.HookTypeHandler;
import org.netbeans.modules.portalpack.websynergy.portlets.hook.api.WizardDescriptorPanelWrapper;
import org.netbeans.modules.portalpack.websynergy.portlets.util.PluginXMLUtil;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author satyaranjan
 */
public class JSPHookTypeHandler extends HookTypeHandler {
    
    private Project project;
    private String jspsDir;
    CreateFilesAndFolders createFilesAndFolders;
    FileSystem fs;
    public JSPHookTypeHandler(Project project) {
        this.project = project;
    }

    @Override
    public void addHookDefinition(FileObject hookXml, WizardDescriptor desc) {
        jspsDir = "/WEB-INF/jsp";
        if (hookXml == null)
            return;
        
        PluginXMLUtil util = new PluginXMLUtil(hookXml);
        util.addJSPHandlerHook(jspsDir);
        util.store();
    }

    @Override
    public void createAdditionalFiles(Project project, WizardDescriptor desc, Set result) {
        final ProgressHandle handle =
                ProgressHandleFactory.createHandle(NbBundle.getMessage(JSPHookTypeHandler.class, "JSP_FILES_FOLDERS_CREATE_PROGRESS_MESSAGE"));
        handle.start();
        try {
            FileObject webInf = PortletProjectUtils.getWebInf(project);
            FileObject destFolder = webInf.getFileObject("jsp", null);
            if (destFolder == null) {
                destFolder = webInf.createFolder("jsp");
            }
            FileObject htmlFO = destFolder.getFileObject("html");
            if(htmlFO == null)
                htmlFO = destFolder.createFolder("html");
            HashMap selectedDirsMap = (HashMap) desc.getProperty("dir_list");
            
            createFilesAndFolders = new CreateFilesAndFolders(
                    PortletProjectUtils.getWebModule(project), selectedDirsMap,
                    htmlFO);
            if (webInf != null) {
                fs = webInf.getFileSystem();
                RequestProcessor.getDefault().post(new Runnable(){
                    
                    public void run() {
                        try {
                            fs.runAtomicAction(createFilesAndFolders);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        } finally {
                            handle.finish();
                        }
                    }
                });
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
   
    @Override
    public Panel getConfigPanel() {
        return new WizardDescriptorPanelWrapper(new JSPHookConfigPanel(project));
    }

    
    private class CreateFilesAndFolders implements FileSystem.AtomicAction {
        private WebModule webModule;
        private HashMap selectedDirsMap;
        private FileObject htmlFO;
        
        public CreateFilesAndFolders(WebModule webModule,
                HashMap selectedDirsMap, FileObject htmlFO) {

            this.webModule = webModule;
            this.selectedDirsMap = selectedDirsMap;
            this.htmlFO = htmlFO;
        }

        public void run(){
            FileObject fo = null;
            String path = null;
            for (Object key : selectedDirsMap.keySet()) {
                path = (String) key;
                fo = (FileObject) selectedDirsMap.get(path);
                File jspFolderFile = new File(FileUtil.toFile(htmlFO),path);
                jspFolderFile.mkdirs();
                try {
                    copyDirectory(fo, FileUtil.toFileObject(jspFolderFile).getParent());
                } catch (IOException ex) {
                }
            }
        }
        
        public void copyDirectory(FileObject sourceLocation , FileObject destParent/*, String file*/)
        throws IOException {

            if (sourceLocation.isFolder()) {

                FileObject destFO = destParent.getFileObject(sourceLocation.getName());
                if (destFO == null) {
                    try{
                        destFO = destParent.createFolder(sourceLocation.getName());
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                FileObject[] children = sourceLocation.getChildren();
                for (int i=0; i<children.length; i++) {
                    copyDirectory(children[i],destFO);
                }
            } else {
                try {
                    FileUtil.copyFile(sourceLocation, destParent,sourceLocation.getName());
                }catch(Exception e) {
                }
            }
        }
    }
}
