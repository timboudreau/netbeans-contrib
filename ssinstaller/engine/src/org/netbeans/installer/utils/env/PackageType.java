/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.installer.utils.env;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.env.impl.LinuxDebianPackagesAnalyzer;
import org.netbeans.installer.utils.env.impl.LinuxPackagesAnalyzer;
import org.netbeans.installer.utils.env.impl.LinuxRPMPackagesAnalyzer;
import org.netbeans.installer.utils.env.impl.SolarisPackagesAnalyzer;
import org.netbeans.installer.utils.helper.Platform;
import org.netbeans.installer.utils.nativepackages.NativeInstallerFactory;
import org.netbeans.installer.utils.nativepackages.NativePackageInstaller;

public enum PackageType implements PackagesInfo {
    
    SOLARIS_PKG(NativeInstallerFactory.getPlatformNativePackageInstaller(true), new PackagesInfo() {
        
        public Map<String, String> getInstalledPackages() {
            SolarisPackagesAnalyzer spa = new SolarisPackagesAnalyzer();
            Map<String, String> result = null;
            if (spa.isActual()) {
                result = new HashMap<String, String>();
                for(String name: spa) {
                    result.put(name, spa.getPackageVersion(name));
                }
            }
            return result;      
        }

        public Set<String> getInstalledPatches() {
            try {
                File tempFile = File.createTempFile("ssistaller", ".tmp");
                tempFile.deleteOnExit();
                Process p = new ProcessBuilder("sh", "-c", "/usr/bin/showrev -p > " + tempFile.getAbsolutePath()).start();
                if (p.waitFor() == 0) {
                    Set<String> result = new HashSet<String>();
                    BufferedReader output = new BufferedReader(new FileReader(tempFile));
                    String line = null;                    
                     while ((line = output.readLine()) != null) {
                        String[] fields = line.trim().split(" ");
                        if (fields.length >= 2) result.add(fields[1]);
                    }
                    return result;
                }
            } catch (InterruptedException ex) {
                LogManager.log(ex);
            } catch (IOException ex) {
                LogManager.log(ex);
            }
            return null;     
        }

        public boolean isCorrectPackageFile(String pathToPackage) {
            try {
                Process p = new ProcessBuilder("pkginfo", "-d", pathToPackage).start();
                return p.waitFor() == 0;
            } catch (InterruptedException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        }

        public long getPackageContentSize(String pathToPackage) {
            return (new File(pathToPackage)).length();
        }

        public Vector<String> getPackageNames(String pathToPackage) {
            Vector<String> result = new Vector<String>();            
            if (SystemUtils.isSolaris()) {
                SolarisPackagesAnalyzer spa = new SolarisPackagesAnalyzer(pathToPackage);
                if (spa.isActual()) {
                    for(String name: spa) {
                        result.add(name);
                    }
                }
                return result;
            } else {
                result.add((new File(pathToPackage)).getName());
            }
            return result;
        }

        public Platform getPackagePlatform(String pathToPackage) {
            SolarisPackagesAnalyzer spa = new SolarisPackagesAnalyzer(pathToPackage);
            if (spa.isActual()) {                
                for(String name: spa) {
                  String arch = spa.getPackageArchitecture(name);
                  if (arch.equals("i386")) return Platform.SOLARIS_X86;
                  if (arch.equals("i486")) return Platform.SOLARIS_X86;
                  if (arch.equals("i586")) return Platform.SOLARIS_X86;
                  if (arch.equals("i686")) return Platform.SOLARIS_X86;
                  if (arch.equals("amd64")) return Platform.SOLARIS_X86;
                  if (arch.equals("x86_64")) return Platform.SOLARIS_X86;
                  if (arch.equals("sparc")) return Platform.SOLARIS_SPARC;
                }
            }
            return null;
        }
        
    }),
    LINUX_RPM(NativeInstallerFactory.getPlatformNativePackageInstaller(false), new PackagesInfo() {
        
        public Map<String, String> getInstalledPackages() {
            return LinuxPackagesInfo.getInstalledPackages(new LinuxRPMPackagesAnalyzer(false));
        }

        public Set<String> getInstalledPatches() {
            LinuxPackagesAnalyzer lpa = new LinuxRPMPackagesAnalyzer(true);
            Set<String> result = null;
            if (lpa.isActual()) {
                for(String name: lpa) {
                    result.add(name);
                }
            }
            return result;
        }

        public boolean isCorrectPackageFile(String pathToPackage) {
            if (LinuxRPMPackagesAnalyzer.isRPMSupported()) return LinuxPackagesInfo.isCorrectPackageFile(pathToPackage, new LinuxRPMPackagesAnalyzer(pathToPackage));
            return pathToPackage.endsWith(".rpm");
        }

        public long getPackageContentSize(String pathToPackage) {
            if (LinuxRPMPackagesAnalyzer.isRPMSupported() && isCorrectPackageFile(pathToPackage)) return LinuxPackagesInfo.getPackageContentSize(pathToPackage, new LinuxRPMPackagesAnalyzer(pathToPackage));
            return (new File(pathToPackage)).length();
        }

        public Vector<String> getPackageNames(String pathToPackage) {
            if (LinuxRPMPackagesAnalyzer.isRPMSupported()) return LinuxPackagesInfo.getPackageNames(pathToPackage, new LinuxRPMPackagesAnalyzer(pathToPackage));
            Vector<String> result = null;
            if (pathToPackage.endsWith(".rpm")) {                
                String[] fields = LinuxRPMPackagesAnalyzer.getRPMPackageFieldsFromFileName(pathToPackage);
                if (fields != null) {
                    result = new Vector<String>();
                    result.add(fields[0]);
                    return result;
                }
            }
            return result;
        }

        public Platform getPackagePlatform(String pathToPackage) {
            if (LinuxRPMPackagesAnalyzer.isRPMSupported()) return LinuxPackagesInfo.getPackagePlatform(pathToPackage, new LinuxRPMPackagesAnalyzer(pathToPackage));
            Platform result = null;
            if (pathToPackage.endsWith(".rpm")) {
                String[] fields = LinuxRPMPackagesAnalyzer.getRPMPackageFieldsFromFileName(pathToPackage);
                if (fields != null) result = LinuxPackagesInfo.archStringToPlatform(fields[2]);
            }
            return result;            
        }
       
    }),
    LINUX_DEB(null, new PackagesInfo() {

        public Map<String, String> getInstalledPackages() {
            return LinuxPackagesInfo.getInstalledPackages(new LinuxDebianPackagesAnalyzer());
        }

        public Set<String> getInstalledPatches() {
            return null;
        }

        public boolean isCorrectPackageFile(String pathToPackage) {
            return LinuxPackagesInfo.isCorrectPackageFile(pathToPackage, new LinuxDebianPackagesAnalyzer(pathToPackage));
        }

        public long getPackageContentSize(String pathToPackage) {
            return LinuxPackagesInfo.getPackageContentSize(pathToPackage, new LinuxDebianPackagesAnalyzer(pathToPackage));
        }

        public Vector<String> getPackageNames(String pathToPackage) {
            return LinuxPackagesInfo.getPackageNames(pathToPackage, new LinuxDebianPackagesAnalyzer(pathToPackage));
        }

        public Platform getPackagePlatform(String pathToPackage) {
            return LinuxPackagesInfo.getPackagePlatform(pathToPackage, new LinuxDebianPackagesAnalyzer(pathToPackage));
        }
        
    });
    
    private NativePackageInstaller packageInstaller = null;
    private PackagesInfo info = null;
    
    PackageType(NativePackageInstaller packageInstaller, PackagesInfo info) {
        this.packageInstaller = packageInstaller;
        this.info = info;
    }

    public NativePackageInstaller getPackageInstaller() {
        return packageInstaller;
    }

    public Map<String, String> getInstalledPackages() {
        return info.getInstalledPackages();
    }

    public Set<String> getInstalledPatches() {
        return info.getInstalledPatches();
    }

    public boolean isCorrectPackageFile(String pathToPackage) {
        return info.isCorrectPackageFile(pathToPackage);
    }

    public long getPackageContentSize(String pathToPackage) {
        return info.getPackageContentSize(pathToPackage);
    }

    public Vector<String> getPackageNames(String pathToPackage) {
        return info.getPackageNames(pathToPackage);
    }

    public Platform getPackagePlatform(String pathToPackage) {
        return info.getPackagePlatform(pathToPackage);
    }
        
}

class LinuxPackagesInfo {
    
    public static Map<String, String> getInstalledPackages(LinuxPackagesAnalyzer lpa) {
        Map<String, String> result = null;
        if (lpa.isActual()) {
            result = new HashMap<String, String>();
            for(String name: lpa) {
                result.put(name, lpa.getPackageVersion(name));
            }
        }
        return result;
    }

    public static boolean isCorrectPackageFile(String pathToPackage, LinuxPackagesAnalyzer lpa) {
        return getPackageNames(pathToPackage, lpa) != null;
    }

    public static long getPackageContentSize(String pathToPackage, LinuxPackagesAnalyzer lpa) {
        if (lpa.isActual()) {
            for(String name: lpa) {
                return lpa.getPackageSize(name);
            }                
        }
        return -1;
    }

    public static Vector<String> getPackageNames(String pathToPackage, LinuxPackagesAnalyzer lpa) {
        Vector<String> result = null;
        if (lpa.isActual()) {
            result = new Vector<String>();
            for(String name: lpa) {
                result.add(name);
            }
        }
        return result;
    }

    public static Platform getPackagePlatform(String pathToPackage, LinuxPackagesAnalyzer lpa) {
        if (lpa.isActual()) {
            for(String name: lpa) {
              return  archStringToPlatform(lpa.getPackageArchitecture(name));
            }                
        }
        return null;
    }   
    
    public static Platform archStringToPlatform(String arch) {
        if (arch.equals("i386")) return Platform.LINUX_X86;
        if (arch.equals("i486")) return Platform.LINUX_X86;
        if (arch.equals("i586")) return Platform.LINUX_X86;
        if (arch.equals("i686")) return Platform.LINUX_X86;
        if (arch.equals("noarch")) return Platform.LINUX;
        if (arch.equals("all")) return Platform.LINUX;
        if (arch.equals("amd64")) return Platform.LINUX_X64;
        if (arch.equals("x86_64")) return Platform.LINUX_X64;        
        return null;
    }
    
}
