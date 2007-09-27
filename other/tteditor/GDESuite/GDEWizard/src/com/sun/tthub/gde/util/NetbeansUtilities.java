
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
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
        
        File file= InstalledFileLocator.getDefault().locate("modules/ext/jaxws20",null,false);
        if (file==null){
            file=InstalledFileLocator.getDefault().locate("modules/ext/jaxws21",null,false);
            if(file==null)
                throw new FileNotFoundException("unable to find netbeans jaxws20 or jaxws21 directory");
        }
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
