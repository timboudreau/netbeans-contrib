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

package org.netbeans.modules.portalpack.servers.liferay.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.TemplateNotFoundException;
import org.netbeans.modules.portalpack.servers.liferay.common.LiferayConstants;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;

/**
 *
 * @author satyaranjan
 */
public class LiferayXMLUtil {

    public static String LR_PORTLET_TEMPLATE = "liferay-portlet-440.template";
    public static String LR_DISPLAY_TEMPLATE = "liferay-display-400.template";
    public static String LR_PLUGIN_PACKAGE_TEMPLATE = "liferay-plugin-package-430.template";
    
    private static String templateFolder = "liferay/templates";
    private static FileObject folder;
    protected static Logger logger = Logger.getLogger(LiferayConstants.LR_LOGGER);
    
    public static boolean createLRXMLFile(String templateName,String destFolder,String fileName)
    {
        FileObject templateFile = getTemplateFile(templateName);
        if(templateFile == null)
        {
            logger.severe("Template File "+templateName + " not found !!!");
            return false;
        }
        
        File destDir = new File(destFolder);
        if(destDir == null || !destDir.exists())
        {
            logger.severe("Destination Folder "+ destFolder + " doesn't exist !!!");
            return false;
        }
        
        FileObject destObj = FileUtil.toFileObject(destDir);
        if(destObj == null)
        {
            logger.severe("Destination Object for folder "+ destFolder + " is null !!!");
            return false;
        }
        try {
            FileUtil.copyFile(templateFile, destObj, fileName, "xml");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error creating lr xml file : " + fileName, ex);
            return false;
        }
        return true;
    }
    /**
     * It returns the template FileObject
     * 
     * @param name Template name. Template name is usually specified in the layer.xml 
     * @return Template FileObject
     **/
    public static FileObject getTemplateFile(String name) {
        FileObject fo = getFolder() != null ? getFolder().getFileObject(name) : null;
        return fo;
    }

    public static FileObject getFolder() {
        if (folder == null) {
            folder = Repository.getDefault().getDefaultFileSystem().findResource(templateFolder);
        }
        return folder;
    }

    public static void setFolder(FileObject folder) {
        folder = folder;
    }

}
