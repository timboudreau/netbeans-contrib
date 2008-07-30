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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
/**
 *
 * @author nassern
 */
public class ScriptLibrary implements Runnable {

    private StringBuffer preDefScriptDir;
    private StringBuffer usrDefScriptDir;
    private String userHomeDir;
    private String userCurDir;
    private String chimeHome;
    private FileFilter fileFilter;
    private FileFilter dirFilter;
    private boolean grayOut;
    private Process p;
    
    public ScriptLibrary() {
        preDefScriptDir = new StringBuffer();
        usrDefScriptDir = new StringBuffer();

        userHomeDir = System.getProperty("user.home");
        if (userHomeDir.length() != 0) {
            preDefScriptDir.append(userHomeDir);
            preDefScriptDir.append("/");
            preDefScriptDir.append("DTraceScripts");
        }
        
        userCurDir = System.getProperty("user.dir");
        if (userCurDir.length() != 0) {
            usrDefScriptDir.append(userCurDir);
            usrDefScriptDir.append("/");
            usrDefScriptDir.append("DTraceScripts");
        } 
        
        chimeHome = preDefScriptDir.toString() + "/chime";
        System.setProperty("CHIME_HOME", chimeHome);
        // System.setProperty("JAVA_CONVERSION_API_DEBUG", "1");
        // System.getProperties().list(System.out);
        String arch = System.getProperty("os.arch");
        File libraryFile = InstalledFileLocator.getDefault().locate("modules/lib",
                "org.netbeans.modules.dtrace",
                false);
  
        if (libraryFile != null  && libraryFile.exists()) {
            String path = libraryFile.toString();
            File netbeansLib = new File(path, arch);
            String libraryPath = System.getProperty("java.library.path");
            libraryPath += ":";
            libraryPath += netbeansLib.getAbsolutePath();
            System.setProperty("java.library.path", libraryPath);
        }
    /*   
        String classPath = System.getProperty("java.class.path");
        String dtraceJar = "/usr/share/lib/java/dtrace.jar";
        classPath +=":" + dtraceJar;
        System.setProperty("java.class.path", classPath);
       
        try {
            URLClassLoader urlLoader = getURLClassLoader(new URL("file", null, dtraceJar));
            JarInputStream jis = new JarInputStream(new FileInputStream(dtraceJar));
            JarEntry entry = jis.getNextJarEntry();
            
            while (entry != null) {
              String name = entry.getName();
              if (name.endsWith(".class")) {
                name = name.substring(0, name.length() - 6);
                name = name.replace('/', '.');
                System.out.print("> " + name);

                try {
                  ClassLoader sysLoader = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
                  sysLoader.loadClass(name);
                  System.out.println("\t- loaded");
                } catch (Throwable e) {
                  System.out.println("\t- not loaded");
                  System.out.println("\t " + e.getClass().getName() + ": " + e.getMessage());
                }

              }
              entry = jis.getNextJarEntry();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
       */
       // String netbeansUser = System.getProperty("netbeans.user");
        //String libraryPath = System.getProperty("java.library.path");
       // File netbeansLib = new File(netbeansUser, "lib");
        //netbeansLib = new File(netbeansLib, arch);
       // libraryPath += ":";
      //  libraryPath += netbeansLib.getAbsolutePath();
       // System.out.println(libraryPath);
        //System.setProperty("java.library.path", libraryPath);
        
        if (Utilities.getOperatingSystem() == Utilities.OS_SOLARIS) {       
            installScripts();
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
                    } else if (name.compareToIgnoreCase("Code") == 0) {
                        acceptDir = false;
                    } else if (name.compareToIgnoreCase("Include") == 0) {
                        acceptDir = false;
                    } else if (name.compareToIgnoreCase("Notes") == 0) {
                        acceptDir = false;                       
                    } else if (name.compareToIgnoreCase("chime") == 0) {
                        acceptDir = false;
                    } else if (name.compareToIgnoreCase("Legal") == 0) {
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
   
    private static URLClassLoader getURLClassLoader(URL jarURL) {
        return new URLClassLoader(new URL[]{jarURL});
    }
    
    public void installScripts() {

        File scriptDir = new File(preDefScriptDir.toString());  
        
        if (scriptDir != null && scriptDir.exists()) {
            return;
        }
       
        synchronized (this) { 
            String procName = "Install DTraceScripts";
            InputOutput io = IOProvider.getDefault().getIO(procName, true);
            io.select();            
            ExecutorTask task = ExecutionEngine.getDefault().execute(procName, this, io);
            task.waitFinished();
        }
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
    
    public void copyInputStream(InputStream in, OutputStream out)
    throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }  
    
    public void copyFile(File from, File to) {
        try {
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(from));
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(to));
            byte[] buf = new byte[4096];
            int cnt;
            while ((cnt = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, cnt);
            }
            is.close();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public void run() {    
   
        Enumeration entries;
        ZipFile zipFile;
        
        File scriptFile = InstalledFileLocator.getDefault().locate("modules/ext/DTraceScripts.zip",
                "org.netbeans.modules.dtrace",
                false);
  
        if (scriptFile != null  && !scriptFile.exists()) {
            return;
        } 
              
        StringBuffer path = new StringBuffer(scriptFile.toString());
        String zipPath = userHomeDir + File.separator + "DTraceScripts.zip";
        File zipSource = new File(path.toString());
        File zipDest = new File(zipPath);
        copyFile(zipSource, zipDest);
 
        try {
            zipFile = new ZipFile(zipPath);
            entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)entries.nextElement();

                if (entry.isDirectory()) {
                    (new File(userHomeDir + File.separator + entry.getName())).mkdir();
                    continue;
                }

                copyInputStream(zipFile.getInputStream(entry),
                new BufferedOutputStream(new FileOutputStream(userHomeDir + 
                        File.separator + entry.getName())));
            }
            zipFile.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }  

        String command = "/bin/chmod -R 755 DTraceScripts";
        try {
            File userHomeDirFile = new File(userHomeDir);
            p = Runtime.getRuntime().exec(command, null, userHomeDirFile);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
       int retCode = 0;
        try {
            retCode = p.waitFor();                 
        } catch (InterruptedException ex) {
            // We've interupted the process. Kill it and wait for the process to finish.
            p.destroy();
            while (retCode < 0) {
                try {
                    retCode= p.waitFor();
                } catch (InterruptedException ex1) {
                    ex1.getStackTrace();
                }
            }
        }
    }
}
