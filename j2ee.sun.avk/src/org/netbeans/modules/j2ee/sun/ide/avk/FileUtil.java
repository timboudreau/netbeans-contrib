/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
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
