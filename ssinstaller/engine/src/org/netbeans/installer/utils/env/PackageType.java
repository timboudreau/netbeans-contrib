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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        @Override
        public Collection<PackageDescr> getInstalledPackages() {
            SolarisPackagesAnalyzer spa = new SolarisPackagesAnalyzer();            
            if (spa.isActual()) {
                return Collections.unmodifiableCollection(spa.getInstalledPackageList());
            }
            return null;
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
        
        
        public Collection<String> getPackageNames(String pathToPackage) {
            Collection<String> result = new ArrayList<String>();
            if (SystemUtils.isSolaris()) {
                SolarisPackagesAnalyzer spa = new SolarisPackagesAnalyzer(pathToPackage);
                if (spa.isActual()) {
                    return Collections.unmodifiableCollection(spa.getInstalledPackageNames());
                }               
            } else {
                result.add((new File(pathToPackage)).getName());
            }
            return result;
        }

        public Platform getPackagePlatform(String pathToPackage) {
            SolarisPackagesAnalyzer spa = new SolarisPackagesAnalyzer(pathToPackage);
            if (spa.isActual()) {                
                for(PackageDescr name: spa.getInstalledPackageList()) {
                  String arch = name.getArch();
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

        public String getPackageVersion(String pathToPackage) {
            if (SystemUtils.isSolaris()) {
                SolarisPackagesAnalyzer spa = new SolarisPackagesAnalyzer(pathToPackage);
                if (spa.isActual()) {
                    for(PackageDescr name: spa) {
                        return name.getVersion();
                    }
                }               
            }
            return null;
        }
        
    }),
    LINUX_RPM(NativeInstallerFactory.getPlatformNativePackageInstaller(false), new PackagesInfo() {
        
        public Collection<PackageDescr> getInstalledPackages() {
            return LinuxPackagesInfo.getInstalledPackages(new LinuxRPMPackagesAnalyzer(false));
        }

        public Set<String> getInstalledPatches() {
            LinuxPackagesAnalyzer lpa = new LinuxRPMPackagesAnalyzer(true);
            Set<String> result = null;
            if (lpa.isActual()) {
                for(String name: lpa.getInstalledPackageNames()) {
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

        public Collection<String> getPackageNames(String pathToPackage) {
            if (LinuxRPMPackagesAnalyzer.isRPMSupported()) return LinuxPackagesInfo.getPackageNames(pathToPackage, new LinuxRPMPackagesAnalyzer(pathToPackage));
            Collection<String> result = null;
            if (pathToPackage.endsWith(".rpm")) {                
                String[] fields = LinuxRPMPackagesAnalyzer.getRPMPackageFieldsFromFileName(pathToPackage);
                if (fields != null) {
                    result = new ArrayList<String>();
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

        public String getPackageVersion(String pathToPackage) {
            if (LinuxRPMPackagesAnalyzer.isRPMSupported()) return LinuxPackagesInfo.getPackageVersion(pathToPackage, new LinuxRPMPackagesAnalyzer(pathToPackage));
            String result = null;
            if (pathToPackage.endsWith(".rpm")) {                
                String[] fields = LinuxRPMPackagesAnalyzer.getRPMPackageFieldsFromFileName(pathToPackage);
                if (fields != null) {
                    result = fields[1];
                }
            }
            return result;            
        }
       
    }),
    LINUX_DEB(null, new PackagesInfo() {

        public Collection<PackageDescr> getInstalledPackages() {
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

        public Collection<String> getPackageNames(String pathToPackage) {
            return LinuxPackagesInfo.getPackageNames(pathToPackage, new LinuxDebianPackagesAnalyzer(pathToPackage));
        }

        public Platform getPackagePlatform(String pathToPackage) {
            return LinuxPackagesInfo.getPackagePlatform(pathToPackage, new LinuxDebianPackagesAnalyzer(pathToPackage));
        }
        
        public String getPackageVersion(String pathToPackage) {
            return LinuxPackagesInfo.getPackageVersion(pathToPackage, new LinuxDebianPackagesAnalyzer(pathToPackage));
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

    public Collection<PackageDescr> getInstalledPackages() {
        return info.getInstalledPackages();
    }
    public Collection<String> getPackageNames(String pathToPackage) {
        return info.getPackageNames(pathToPackage);
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
  

    public Platform getPackagePlatform(String pathToPackage) {
        return info.getPackagePlatform(pathToPackage);
    }

    public String getPackageVersion(String pathToPackage) {
        return info.getPackageVersion(pathToPackage);
    }

        
}
class LinuxPackagesInfo {
    
    public static Collection<PackageDescr> getInstalledPackages(LinuxPackagesAnalyzer lpa) {
        Collection<PackageDescr> result = null;
        if (lpa.isActual()) {
            return lpa.getInstalledPackageList();
            
        }
        return result;
    }

    public static boolean isCorrectPackageFile(String pathToPackage, LinuxPackagesAnalyzer lpa) {
        return getPackageNames(pathToPackage, lpa) != null;
    }

    public static long getPackageContentSize(String pathToPackage, LinuxPackagesAnalyzer lpa) {
        if (lpa.isActual()) {
            for(PackageDescr name: lpa.getInstalledPackageList()) {
                return name.getSize();
            }                
        }
        return -1;
    }

    public static String getPackageVersion(String pathToPackage, LinuxPackagesAnalyzer lpa) {
        if (lpa.isActual()) {
            for(PackageDescr name: lpa.getInstalledPackageList()) {
                return name.getVersion();
            }                
        }
        return null;
    }    
    
    public static Collection<String> getPackageNames(String pathToPackage, LinuxPackagesAnalyzer lpa) {
        Collection<String> result = null;
        if (lpa.isActual()) {
            result = new ArrayList<String>();
            for(String name: lpa.getInstalledPackageNames()) {
                result.add(name);
            }
        }
        return result;
    }

    public static Platform getPackagePlatform(String pathToPackage, LinuxPackagesAnalyzer lpa) {
        if (lpa.isActual()) {
            for(PackageDescr name: lpa.getInstalledPackageList()) {
              return  archStringToPlatform(name.getArch());
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
