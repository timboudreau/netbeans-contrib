/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */
package org.netbeans.installer.utils.nativepackages;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.env.ExistingSunStudioChecker;

/**
 *
 * @author Igor Nikiforov
 */
class SolarisNativePackageInstaller implements NativePackageInstaller {

//    public static final String DEVICE_FILES_COUNTER = "device_files_counter";
//    public static final String DEVICE_FILE = "device_file.";
   
    private String target = null;
    //TODO fix
    static final File TMP_DIR = new File("/tmp");
    File defaultResponse;
    File defaultAdminFile;
    private boolean isLocalZone = false;
    
    private static String pkgRoot = System.getenv("USE_ALTERNATIVE_PACK_ROOT") == null ?
        "/usr/sbin"  : System.getenv("USE_ALTERNATIVE_PACK_ROOT");

    public SolarisNativePackageInstaller() {
        try {
            defaultResponse = File.createTempFile("nbi-response", "", TMP_DIR);
            defaultResponse.deleteOnExit();
            PrintStream out = new PrintStream(defaultResponse);
            out.println("LIST_FILE=/tmp/depend_list.\nTRLR_RESP=/tmp/response.\n");
            // TODO should be removed to somewhere else.           

        } catch (IOException ex) {
            throw new Error("Unexpected Error.", ex);
        }
    }

    public void setDestinationPath(String path) {
        target = path;
        try {
            defaultAdminFile = File.createTempFile("nbi-admin", "", TMP_DIR);
            defaultAdminFile.deleteOnExit();
            PrintStream out = new PrintStream(defaultAdminFile);
            out.println("mail=\ninstance=unique\npartial=nocheck\nrunlevel=nocheck" +
                    "\nidepend=nocheck\nrdepend=nocheck\nspace=quit\nsetuid=nocheck\n" +
                    "conflict=nocheck\naction=nocheck\nbasedir=" + path + "\n");
        } catch (IOException ex) {
            throw new Error("Unexpected Error.", ex);
        }
    }
    
    public String install(String pathToPackage, String packageName) throws InstallationException {
        String installedName = packageName;
        try {
            Process p;
             if (ExistingSunStudioChecker.getInstance().isOnlyLocalInstallationPossible()) {
                isLocalZone = true;
            }
            if (isLocalZone) {
                Logger.getAnonymousLogger().warning("executing command: pkgadd -n -d -G" + pathToPackage + " " + packageName);
                p = new ProcessBuilder(pkgRoot + "/pkgadd", "-n", "-G",
                        "-a", defaultAdminFile.getAbsolutePath(),
                        "-r", defaultResponse.getAbsolutePath(),
                        "-d", pathToPackage,
                        packageName).start();
            } else {
                Logger.getAnonymousLogger().warning("executing command: pkgadd -n -d " + pathToPackage + " " + packageName);
                p = new ProcessBuilder(pkgRoot + "/pkgadd", "-n",
                        "-a", defaultAdminFile.getAbsolutePath(),
                        "-r", defaultResponse.getAbsolutePath(),
                        "-d", pathToPackage,
                        packageName).start();
            }
            if (p.waitFor() != 0) {
                String line;
                StringBuffer message = new StringBuffer();
                message.append("Error = ");
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((line = input.readLine()) != null) {
                    message.append(line);
                }
                message.append("\n Output = ");
                input =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = input.readLine()) != null) {
                    message.append(line);
                }
                throw new InstallationException("Error native. " + message);
            } else {
                String line;
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(p.getErrorStream()));
                LogManager.log("Install output");
                while ((line = input.readLine()) != null) {
                    LogManager.log(line);
                    if (line.matches("Installation of <.*> was successful.")) {
                        installedName = line.replaceAll(".*<(.*)>.*", "$1");
                        LogManager.log("WOOOOOOOOOOOOW " + installedName);
                    }
                }       
            }
        } catch (InterruptedException ex) {
            throw new InstallationException("Error native.", ex);
        } catch (IOException ex) {
            throw new InstallationException("Error native.", ex);
        }        
        return installedName;
    }
    
    public Iterable<String> install(String pathToPackage, Collection<String> packageNames) throws InstallationException {        
        ArrayList installedPackageNames = new ArrayList(packageNames.size());
        for(String packageName : packageNames) {           
            installedPackageNames.add(install(pathToPackage, packageName));
        }
        return installedPackageNames;
    }      
    
    public Iterable<String> install(String pathToPackage) throws InstallationException {
        DeviceFileAnalyzer analyzer = new DeviceFileAnalyzer(pathToPackage); 
        Logger.getAnonymousLogger().warning("File is:" + pathToPackage + " packages" +
                ((analyzer.getNames().size() > 0) ? analyzer.getNames().iterator().next() : " none-----xxxxx" ));
        return install(pathToPackage, analyzer.getNames());
    }      
        
    public void uninstall(String packageName) throws InstallationException {                
            try {
                Logger.getAnonymousLogger().warning("executing command: pkgrm -R " + target + " -n " + packageName);
                Process p = new ProcessBuilder(pkgRoot + "/pkgrm", "-n",
                        "-a", defaultAdminFile.getAbsolutePath(), packageName).start();
                if (p.waitFor() != 0) {
                    throw new InstallationException("Error native. Returned not zero.");
                }
            } catch (InterruptedException ex) {
                throw new InstallationException("Error native.", ex);
            } catch (IOException ex) {
                throw new InstallationException("Error native.", ex);
            }
      
    }
    
    public void uninstall(Collection<String> packageNames) throws InstallationException {                
        for (String value : packageNames) {
            uninstall(value);
        }        
    }   

    public boolean isCorrectPackageFile(String pathToPackage) {
        return DeviceFileAnalyzer.isCorrectPackage(pathToPackage);
    }
    
    public static class DeviceFileAnalyzer {

        public static int PKGINFO_OUTPUT_FILEDS_COUNT = 3;
        public static int PKGINFO_PACKAGE_NAME_FIELD_INDEX = 1;
        
        private List<String> packages = new LinkedList<String>();

        public DeviceFileAnalyzer(String fileName) {
            try {
                Process p = new ProcessBuilder("pkginfo", "-d", fileName).start();
                if (p.waitFor() == 0) {
                    BufferedReader lines = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while ((line = lines.readLine()) != null) {
                        String[] fields = line.split("[ ]+", PKGINFO_OUTPUT_FILEDS_COUNT);
                        if (fields.length == PKGINFO_OUTPUT_FILEDS_COUNT) {
                            packages.add(fields[PKGINFO_PACKAGE_NAME_FIELD_INDEX]);
                        }
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(SolarisNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SolarisNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public static boolean isCorrectPackage(String fileName) {
            try {
                Process p = new ProcessBuilder("pkginfo", "-d", fileName).start();
                return p.waitFor() == 0;
            } catch (InterruptedException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        }

        public Collection<String> getNames(){
            return packages;
        }
    }

}
