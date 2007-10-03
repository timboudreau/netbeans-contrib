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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
 * FileUtil.java
 *
 * Created on October 14, 2005, 4:40 PM
 */

package org.netbeans.modules.j2ee.sun.ide.avk;

import java.io.File;
import java.util.Set;
import java.util.Iterator;
import java.lang.reflect.Method;

import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.sun.api.SunDeploymentManagerInterface;

import org.netbeans.modules.j2ee.sun.ide.Installer;
import org.netbeans.modules.j2ee.sun.ide.j2ee.DeploymentManagerProperties;

import org.netbeans.modules.j2ee.sun.api.ServerLocationManager;

/**
 *
 * @author Nitya Doraisamy
 */
public class FileUtil {
    
    public static void clearResults(SunDeploymentManagerInterface sunDm, DeploymentManagerProperties dmProps){
        String domainDir = dmProps.getLocation() + dmProps.getDomainName();
        String resultDir = domainDir + File.separator + "logs" + File.separator + "reporttool"; //N0I18N       
        deleteAllFilesUnder(new File(resultDir), sunDm);
    }
         
    private static void deleteAllFilesUnder(File directory, SunDeploymentManagerInterface sunDm) {
        try {
            if (directory != null && !directory.exists())
                return;
                
            Set files = getFiles(directory, sunDm);
            Set dirs = new java.util.HashSet();
            Set filesList = new java.util.HashSet();
            for (Iterator i = files.iterator(); i.hasNext();) {
                File actualFile = new File(directory, i.next().toString());
                if(actualFile.isDirectory()){
                    dirs.add(actualFile);
                } else 
                    filesList.add(actualFile);
            }
                        
            deleteFiles(directory, filesList);
            deleteFiles(directory, dirs);
            directory.delete();
        } catch (Exception ex) {
            return;
        }
    }

    private static Set getFiles(File resultDir, SunDeploymentManagerInterface sunDm) throws Exception {
        Set result = null;
        ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        try{
            Class[] argClass = new Class[1];
            argClass[0] = File.class;
            Object[] argObject = new Object[1];
            argObject[0] = resultDir;
            
            Class controllerUtilClass = ServerLocationManager.getNetBeansAndServerClassLoader(sunDm.getPlatformRoot()).
                    loadClass("com.sun.enterprise.util.FileUtil"); //NOI18N
            
            
            Method method = controllerUtilClass.getMethod("getAllFilesAndDirectoriesUnder", argClass);
            
            Thread.currentThread().setContextClassLoader(
                    ServerLocationManager.getNetBeansAndServerClassLoader(sunDm.getPlatformRoot()));
            
            result = (Set)method.invoke(controllerUtilClass.newInstance(), argObject);
            
        } catch (Exception e){
            throw e;
        } finally {
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }
        return result;
    }
    
    private static void deleteFiles(File directory, Set files){
        for (Iterator i = files.iterator(); i.hasNext();) {
            File next = (File) i.next();
            next.delete();
        }
    }
    
}
