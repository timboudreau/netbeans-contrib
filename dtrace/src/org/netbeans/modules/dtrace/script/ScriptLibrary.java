/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.

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

 * Contributor(s):

 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.

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
 * ScriptLibrary.java
 *
 * Created on April 2, 2007, 7:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.dtrace.script;

import java.io.File;
import java.io.FileFilter;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author nassern
 */
public class ScriptLibrary {

    private StringBuffer preDefScriptDir;
    private StringBuffer usrDefScriptDir;
    private String userHomeDir;
    private String userCurDir;
    private FileFilter fileFilter;
    private FileFilter dirFilter;
    private boolean grayOut;
    
    public ScriptLibrary() {
        preDefScriptDir = new StringBuffer();
        usrDefScriptDir = new StringBuffer();
        userHomeDir = new String(); 
        userCurDir = new String(); 
        Properties props = System.getProperties();
        Enumeration iter = props.propertyNames();
        
        for (; iter.hasMoreElements(); ) {
             String propName = (String)iter.nextElement();
             if (propName.equals("user.home")) {
                 userHomeDir = (String)props.get(propName);
             } else if (propName.equals("user.dir")) {
                 userCurDir = (String)props.get(propName);
             }
        }
        
        if (userHomeDir.length() != 0) {
            preDefScriptDir.append(userHomeDir);
            preDefScriptDir.append("/");
            preDefScriptDir.append("DTraceScripts");
        }
        
        if (userCurDir.length() != 0) {
            usrDefScriptDir.append(userCurDir);
            usrDefScriptDir.append("/");
            usrDefScriptDir.append("DTraceScripts");
        } 
        
        fileFilter = new FileFilter() {
            public boolean accept(File file) {
                boolean acceptFile = true;
                String name = file.getName();
                int dot = name.lastIndexOf('.');
                if (dot != -1) {
                    String extension = name.substring(dot + 1);
                    if (extension.compareToIgnoreCase("xml") == 0) {
                        acceptFile = false;
                    }
                }
                
                if (file.isDirectory()) {
                    acceptFile = false;
                } else if (name.compareToIgnoreCase("Readme") == 0) {
                    acceptFile = false;
                }
                
                return acceptFile;
            }
        }; 
        
        grayOut = false;
        dirFilter = new FileFilter() {
            public boolean accept(File file) {
                boolean acceptDir = false;
                String name = file.getName();
                
                if (file.isDirectory()) {
                    acceptDir = true;
                    if (name.compareToIgnoreCase("Docs") == 0) {
                        acceptDir = false;
                    } else if (name.compareToIgnoreCase("Extra") == 0) {
                        acceptDir = false;
                    } else if (name.compareToIgnoreCase("Bin") == 0) {
                        acceptDir = false;
                    } else if (name.compareToIgnoreCase("Man") == 0) {
                        acceptDir = false;
                    } else if (name.compareToIgnoreCase("zzz") == 0) {
                        acceptDir = false;
                    } else if (name.compareToIgnoreCase("Examples") == 0) {
                        acceptDir = false;
                    }
                    
                  //  if (name.compareToIgnoreCase("Examples") == 0) {
                   //     grayOut = true;
                   // } 
                }
                
                return acceptDir;
            }
        };
    }
    
    public boolean getGrayOut() {
        return grayOut;
    }
    
    public void setGrayOut(boolean grayOut) {
        this.grayOut = grayOut;
    }
    
    public String getUserHomeDir () {
        return userHomeDir;
    }
    
    public String getUserCurDir() {
        return userCurDir;
    }
        
    public String getUsrDefScriptDir(){
        return usrDefScriptDir.toString();
    } 
    
    public String getPreDefScriptDir() {
        return preDefScriptDir.toString();
    }
    
    public File[] getPreDefDirs() {
        File dir = new File(preDefScriptDir.toString());
        File[] files = dir.listFiles(dirFilter);
        return files;
    }
    
    public File[] getPreDefFiles() {
        File dir = new File(preDefScriptDir.toString());
        File[] files = dir.listFiles(fileFilter);
        return files;
    }
       
    public File[] getUsrDefDirs() {
        File dir = new File(usrDefScriptDir.toString());
        File[] files = dir.listFiles(dirFilter);
        return files;
    }
    
    public File[] getUsrDefFiles() {
        File dir = new File(usrDefScriptDir.toString());
        File[] files = dir.listFiles(fileFilter);
        return files;
    }
    
    public File[] getFiles(String dirName) {
        File dir = new File(dirName);        
        File[] files = dir.listFiles(fileFilter);
        return files;
    }
}
