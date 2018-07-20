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


package org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util;

import java.io.File;
import java.text.MessageFormat;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletModeType;
//import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectConstants;
//import org.netbeans.modules.portalpack.portlets.ruby.RubyProjectConstants;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;


/**
 * This class implements the portlet support for a project.
 * @author David Botterill
 * @author Satyaranjan
 */
public class PortletSupportImpl {
    private Project project;
    public static final String PATH_WEB_INF = "web/WEB-INF";  // NOI18N
    public static final String PATH_DOC_ROOT = "web";  // NOI18N
    
    private String viewInitParmName =  "init-view";  //NOI18N default value
    private String editInitParamName = "init-edit"; //NOI18N default value
    private String helpInitParamName = "init-help"; //NOI18N default value

    public PortletSupportImpl(Project project) {
        
        this.project = project;
    }
    /**
     * This constructor needs to be as FAST as possible since it will be called
     * by a static method each time someone simply CHECKS for portlet support on
     * a project.
     * @param project The Project that this class will provide portlet support for.
     * @param viewParamName View Page Init Param Name
     * @param editParamName Edit Page Init Param Name
     * @param helpParamName Help Page Init Param Name
     */
    public PortletSupportImpl(Project project,String viewParamName,
                                    String editParamName, String helpParamName) {
        this.project = project;
        this.viewInitParmName = viewParamName;
        this.editInitParamName = editParamName;
        this.helpInitParamName = helpParamName;
    }

    /**
     * This method will set the portlet name
     * @param oldName the current name of the portlet to change.
     * @param newName the new name to give to the portlet.
     * @throws org.netbeans.modules.visualweb.project.jsf.JsfPortletSupportException thrown if oldName or newName == null
     */
    public void setPortletName(String oldName, String newName) throws Exception {
        if(null == oldName || null == newName ) {
            throw new Exception(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_NAMES_NULL"));
        }
        /**
         * Now find the portlet.xml file.
         */

        File portletDDFile = getPortletDD();

        /**
         * Create a helper and set the inital page.
         */
        PortletDDHelper ddHelper = new PortletDDHelper(portletDDFile,viewInitParmName,
                                        editInitParamName,helpInitParamName);

        ddHelper.setPortletName(oldName,newName);
    }
    /**
     * This method will set the initial page for the portlet.
     * @param inMode The PortletModeType to set the initial page for
     * @param inFilePath The path of the file using the context relative path.  The path should include the leading "/".
     * @throws org.netbeans.modules.visualweb.project.jsf.JsfPortletSupportException thrown if inMode or inFilePath == null
     */
    public void setInitialPage(PortletModeType inMode, String portlet, String inFilePath) throws PortletSupportException {

        File initialFile = new File(inFilePath);
        if(null == initialFile || null == inMode) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_DATAOBJECT_NULL"));
        }

        /**
         * Now find the portlet.xml file.
         */

        File portletDDFile = getPortletDD();

        /**
         * Create a helper and set the inital page.
         */
        PortletDDHelper ddHelper = new PortletDDHelper(portletDDFile, viewInitParmName,
                                        editInitParamName,helpInitParamName);
        ddHelper.setInitialPage(inMode, portlet, inFilePath);

    }

    /**
     * This method will set the initial VIEW page for the portlet.
     * @param inMode The PortletModeType to set the initial page for
     * @param inFileObject The FileObject of the file to set as the inital VIEW Page.
     * @throws org.netbeans.modules.visualweb.project.jsf.JsfPortletSupportException thrown if inFileObject == null
     */
    public void setInitialPage(PortletModeType inMode, String portlet, FileObject inFileObject) throws PortletSupportException {
        if(null == inFileObject ) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_DATAOBJECT_NULL"));
        }
        if(null == inMode) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_MODE_NULL"));
        }
        
        /**
         * First get the file to be made the initial page.
         */
        FileObject initialFO= inFileObject;
        File initialFile = FileUtil.toFile(initialFO);
        if(null == initialFile || null == initialFile.getName() || initialFile.getName().equals("")) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_DATAOBJECT_NULL"));
        }
        
        /**
         * Now find the portlet.xml file.
         */
        
        File portletDDFile = getPortletDD();
        
        /**
         * Create a helper and set the inital page.
         */
        PortletDDHelper ddHelper = new PortletDDHelper(portletDDFile,viewInitParmName,
                                    editInitParamName,helpInitParamName);

        String initialPageName = initialFile.getName();
        String initialPath = getPortletPageFolderPath(initialFO);
       this.setInitialPage(inMode, portlet, initialPath + "/" + initialPageName);
        
    }
    /**
     * This method will unset the given initial page to no initial page.
     * @param inFileObject The FileObject of the file to set as the inital VIEW Page.
     * @throws org.netbeans.modules.visualweb.project.jsf.JsfPortletSupportException thrown if inFileObject == null
     */
    public void unsetInitialPage(String portlet,FileObject inFileObject) throws PortletSupportException {
        if(null == inFileObject ) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_DATAOBJECT_NULL"));
        }
        /**
         * First get the file to be made the initial page.
         */
        FileObject initialFO= inFileObject;
        File initialFile = FileUtil.toFile(initialFO);
        if(null == initialFile || null == initialFile.getName() || initialFile.getName().equals("")) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_DATAOBJECT_NULL"));
        }
        
        /**
         * Now find the portlet.xml file.
         */
        
        File portletDDFile = getPortletDD();
        
        /**
         * Create a helper and set the inital page.
         */
        PortletDDHelper ddHelper =
            new PortletDDHelper(portletDDFile,viewInitParmName,editInitParamName,helpInitParamName);
        String initialPageName = initialFile.getName();
        String initialPath = getPortletPageFolderPath(initialFO);        
        ddHelper.unsetInitialPage(portlet, initialPath + "/" + initialPageName);
        
    }
    
    /**
     * This method will check whether a given page is the initial page for a given mode.
     * @param inMode The PortletModeType to check for the initial page.
     * @param inFileObject The FileObject of the file to set check as the initial page.
     * @throws org.netbeans.modules.visualweb.project.jsf.JsfPortletSupportException thrown if inFileObject == null
     */
    public boolean isInitialPage(PortletModeType inMode, String portlet, FileObject inFileObject) throws PortletSupportException {
        if(null == inFileObject ) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_DATAOBJECT_NULL"));
        }
        if(null == inMode) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_MODE_NULL"));
        }
        
        /**
         * First get the file to be made checked as initial page.
         */
        FileObject initialFO= inFileObject;
        
        File initialFile = FileUtil.toFile(initialFO);
        if(null == initialFile || null == initialFile.getName() || initialFile.getName().equals("")) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_DATAOBJECT_NULL"));
        }
        
        /**
         * Now find the portlet.xml file.
         */
        
        File portletDDFile = getPortletDD();
        
        /**
         * Create a helper and set check the inital page.
         */
        PortletDDHelper ddHelper =
            new PortletDDHelper(portletDDFile,viewInitParmName,editInitParamName,helpInitParamName);
        String initialPageName = initialFile.getName();
        String initialPath = getPortletPageFolderPath(initialFO);
        return ddHelper.isInitialPage(inMode, portlet, initialPath + "/" + initialPageName);
        
    }
    
    /**
     * This method will check whether a given page is an initial page for any mode.
     * @param inFileObject The FileObject of the file to set check as the initial page.
     * @return the PortletModeType the page is the initial page for.  If the page is not an initial page, 
     * null is returned.
     * @throws org.netbeans.modules.visualweb.project.jsf.JsfPortletSupportException thrown if inFileObject == null
     */
    public PortletModeType getPortletMode(String portlet,FileObject inFileObject) throws PortletSupportException {
        if(null == inFileObject ) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_DATAOBJECT_NULL"));
        }
        
        /**
         * First get the file to be made checked as initial page.
         */
        FileObject initialFO= inFileObject;
        
        File initialFile = FileUtil.toFile(initialFO);
        if(null == initialFile || null == initialFile.getName() || initialFile.getName().equals("")) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_DATAOBJECT_NULL"));
        }
        
        /**
         * Now find the portlet.xml file.
         */
        
        File portletDDFile = getPortletDD();
        
        /**
         * Create a helper and set check the inital page.
         */
        PortletDDHelper ddHelper = 
            new PortletDDHelper(portletDDFile,viewInitParmName,editInitParamName,helpInitParamName);
        String initialPageName = initialFile.getName();
        String initialPath = getPortletPageFolderPath(initialFO);
        return ddHelper.isInitialPage(portlet, initialPath + "/" + initialPageName);
        
    }
    /**
     * This method return the page for the given initial mode.
     * @param inMode The PortletModeType to check for the initial page.
     * @throws org.netbeans.modules.visualweb.project.jsf.JsfPortletSupportException thrown if inMode == null
     */
    public String getInitialPage(PortletModeType inMode,String portlet) throws PortletSupportException {
        
        if(null == inMode) {
            throw new PortletSupportException(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_MODE_NULL"));
        }
                
        /**
         * Now find the portlet.xml file.
         */
        
        File portletDDFile = getPortletDD();
        
        /**
         * Create a helper and set the inital page.
         */
        PortletDDHelper ddHelper =
            new PortletDDHelper(portletDDFile,viewInitParmName,editInitParamName,helpInitParamName);        
        return ddHelper.getInitialPage(inMode, portlet);
        
    }
    
    /**
     * This method returns the portlet.xml file for the current project.
     * @return File representing the "portlet.xml" file for the project.
     * @throws org.netbeans.modules.visualweb.project.jsf.JsfPortletSupportException thrown if the portlet.xml file can not be found.
     */
    
    public File getPortletDD() throws PortletSupportException {
        FileObject portletDDFO = project.getProjectDirectory().getFileObject(PATH_WEB_INF + "/portlet.xml");  // NOI18N
        if(null == portletDDFO) {
            String message = MessageFormat.format(NbBundle.getMessage(PortletSupportImpl.class, "MSG_JsfPortletSupportImpl_PORTLETDDNOTFOUND"),
                    new Object[] {project.getProjectDirectory().getName()});
                    throw new PortletSupportException(message);
        }
        
        File portletDDFile = FileUtil.toFile(portletDDFO);
        
        return portletDDFile;
    }
    
    /**
     * This method returns the relative path of the folder for the given portlet page.  
     * @param inFO is the FileObject to get the relative Folder path for.
     * @returns String representing the relative path of the folder for the given portlet relative to the 
     * portlet project. For example, if the inFO has a path of "/home/david/Creator/Projects/Portlet1/web/edit/EditPage.jsp"
     * the folder "/edit" will be returned.
     */
    public String getPortletPageFolderPath(FileObject inFO) {
        String returnPath = null;
        FileObject webRoot = project.getProjectDirectory().getFileObject(PATH_DOC_ROOT);
        if(webRoot == null) {
            return null;
        }
        String webRootPath = webRoot.getPath();
        String jspPath = inFO.getParent().getPath();
        if(jspPath.startsWith(webRootPath)) {
            returnPath = jspPath.substring(webRootPath.length());
        }
        
        return returnPath;
    }
    
}
