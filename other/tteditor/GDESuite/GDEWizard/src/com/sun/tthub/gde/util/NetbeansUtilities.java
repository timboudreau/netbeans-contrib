
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package com.sun.tthub.gde.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.IllegalArgumentException;
import java.util.Properties;
import org.openide.filesystems.FileSystem;

import org.openide.modules.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.execution.ExecutorTask;
import org.apache.tools.ant.module.api.support.ActionUtils;


/**
 *
 * @author choonyin
 */
public class NetbeansUtilities {
    
    /** Creates a new instance of NetbeansUtil */
    public NetbeansUtilities() {
        
    }
    
    public static String getUserDir(){
        return System.getProperty("user.dir");
    }
    
    public static String getJAXBDir() throws FileNotFoundException{
        System.out.println("System.getProperties-"+System.getProperties());
        File file= InstalledFileLocator.getDefault().locate("modules/ext/jaxws20",null,false);
        if (file==null)
            throw new FileNotFoundException("unable to find netbeans jaxws20 directory");
        return file.getPath();
    }
    public static void copyDefaultGDEFolder(String gdefolder) throws FileNotFoundException,IOException{
       
        File destDir=new File(gdefolder);
        if(!destDir.isDirectory()){
            //if the gdefolder does not exists
            
            //Get module gde source dir
            File srcJar= InstalledFileLocator.getDefault().locate("modules/com-sun-tthub-gde.jar","com.sun.tthub.gde",false);
            
            FileObject srcJarRoot=FileUtil.getArchiveRoot(FileUtil.toFileObject(srcJar));
            FileObject srcDir=srcJarRoot.getFileObject("gdefolder");
            if (srcDir==null)
                throw new FileNotFoundException("unable to find gdewizard installation directory");
            
            System.out.println("copyDefaultGDEFolder-srcDir-"+srcDir.getPath());
            System.out.println("copyDefaultGDEFolder-destDir-"+destDir.getAbsolutePath());
            
            //copy source to dest folder
            copyFolder(srcDir,FileUtil.toFileObject(new File(destDir.getParent())));
            
            
        }
        
    }
    
    
    public static int ExecuteAntTask(File antFileObj, Properties properties) throws IllegalArgumentException, IOException {
        
        
        FileObject buildFile =FileUtil.toFileObject(antFileObj);
        
        ExecutorTask task=ActionUtils.runTarget(buildFile,null,properties);
        
        
        int result= task.result();
        
        return result;
    }
    
    public static void copyFolder(FileObject sourceLocation , FileObject targetFolder)
    throws IOException {
        System.out.println("srcLocation-"+sourceLocation.getPath());
        System.out.println("targetFolder-"+targetFolder.getPath());
        
        if (sourceLocation.isFolder()) {
            FileObject childTargetFolder=targetFolder;
            
            if (targetFolder.getFileObject(sourceLocation.getName())==null) {
                
                childTargetFolder=targetFolder.createFolder(sourceLocation.getName());
                
            }
            
            FileObject[] children = sourceLocation.getChildren();
            
            for (int i=0; i<children.length; i++) {
                System.out.println("children[i]-"+children[i].getPath());
                System.out.println("childTargetFolder[i]-"+childTargetFolder.getPath());
                
                copyFolder(children[i],childTargetFolder);
            }
            
            
        } else {
            System.out.println("Copy file"+sourceLocation.getPath());
            FileUtil.copyFile(sourceLocation, targetFolder, sourceLocation.getName());
            
        }
    }
    
}
