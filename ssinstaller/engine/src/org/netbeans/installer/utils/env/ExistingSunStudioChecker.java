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


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.helper.ExecutionMode;
import org.netbeans.installer.utils.helper.Status;
import org.netbeans.installer.wizard.Utils;


/**
 *
 * @author Leonid Mesnik
 */
public class ExistingSunStudioChecker {

    private static ExistingSunStudioChecker checker = new ExistingSunStudioChecker();
    
    

    private final String PACKAGES_LENGTH_PROPERTY = "packages_length";
    private final String PACKAGE_NAME_PROPERTY_PATTERN = "package_%1$d_name";
    private final String PACKAGE_VERSION_PROPERTY_PATTERN = "package_%1$d_version";

    List<PackageDescr> packagesToInstall;
    
    List<PackageDescr> conflictedPackages;
    public final String VERSION = "2008.11";
    public final String VERSION_11 = "11.0";
    private ExistingSunStudioChecker() {
        conflictedPackages = new ArrayList<PackageDescr>();       
        packagesToInstall = new ArrayList<PackageDescr>();
        Collection<PackageDescr> installedPackages = EnvironmentInfoFactory.getInstance().getPackageType().getInstalledPackages();        
        for (Product product : Registry.getInstance().getProductsToInstall()) {
            String count = product.getProperty(PACKAGES_LENGTH_PROPERTY);
            if (count != null && count.length() > 0) {
                for (int i = 1; i <= Integer.parseInt(count); i++) {
                    String packageName = product.getProperty(String.format(PACKAGE_NAME_PROPERTY_PATTERN, i));
                    String packageVersion = product.getProperty(String.format(PACKAGE_VERSION_PROPERTY_PATTERN, i));
                    PackageDescr descr = new PackageDescr(packageName);
                    descr.setVersion(packageVersion == null? VERSION: packageVersion);
                    packagesToInstall.add(descr);
                }
            }
        }        
        LogManager.log("Already installed Sun Studio packages are:");
        for (PackageDescr installedPackage : installedPackages) {
            for (PackageDescr packageToInstall : packagesToInstall) {
                // special for Linux
                if (SystemUtils.isLinux() && installedPackage.getName().equals(packageToInstall.getName())) {
                    conflictedPackages.add(installedPackage);
                    LogManager.log(installedPackage.getName());
                }                
                // special for Solaris
                if (SystemUtils.isSolaris() && (installedPackage.getName().equals(packageToInstall.getName())
                        || installedPackage.getName().startsWith(packageToInstall.getName() + "."))) {
                    if(!Utils.getSSBase().getStatus().equals(Status.INSTALLED)
                      || !installedPackage.getVersion().equals(VERSION)) {
                            conflictedPackages.add(installedPackage);
                            LogManager.log(installedPackage.getName());
                       //  }
                    }
                }
            }
        }

    }

    public static ExistingSunStudioChecker getInstance() {
        return checker;
    }

                
    public boolean isSunStudioInstallationFound() {
        return conflictedPackages.size() != 0;
    }
    
    public boolean isOnlyLocalInstallationPossible() {
        if (!SystemUtils.isSolaris()) {
            return false;
        }
        for (String version : getInstalledVersions()) {
            LogManager.log("Checking version: '" + version + "'" );
            // if Sun Studio 11 is already installed then only local zone could be used.
            if (version.equals(VERSION_11)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInstallationPossible() {        
        for (String version : getInstalledVersions()) {
            if (getResolutionForVersion(version) == INSTALLATION_BLOCKED) {
                return false;
            }
        }
        return true;
    }

    public Collection<String> getInstalledVersions() {
        Set<String> versions = new HashSet();
        for (PackageDescr descr : conflictedPackages) {
            versions.add(descr.getVersion());
        }
        return versions;
    }
    
    public List<String> getBaseDirsForVersion(String version) {
        Set<String> baseDirs = new HashSet();
        for (PackageDescr descr : conflictedPackages) {
            if (descr.getVersion().equals(version)) {
                baseDirs.add(descr.getBaseDirectory());
            }
        }
        return new ArrayList<String>(getDirsRoots(baseDirs));
    }

    private Set<String> getDirsRoots(Collection<String> dirs) {
        Set<String> result = new HashSet();
        List<File> dirsList = new LinkedList<File>();
        for(String dir: dirs) {
            dirsList.add(new File(dir));
        }
        File[] dirsArray = new File[dirsList.size()];
        dirsList.toArray(dirsArray);
        for(int i=0; i<dirsArray.length; i++) {
            if (dirsArray[i] == null) continue;
            for(int j=i+1; j<dirsArray.length; j++) {
                if (dirsArray[j] == null) continue;
                if (FileUtils.isParent(dirsArray[i], dirsArray[j])) {
                    dirsArray[j] = null;
                } else if (FileUtils.isParent(dirsArray[j], dirsArray[i])) {
                    dirsArray[i] = null;
                    break;
                }
            }   
        }
        for(File dir: dirsArray) {
            if (dir != null) result.add(dir.getAbsolutePath());
        }
        return result;
    }
    
    public List<String> getPackagesForVersion(String version) {
        Set<String> names = new HashSet();
        for (PackageDescr descr : conflictedPackages) {
            if (descr.getVersion().equals(version)) {
                names.add(descr.getName());
            }
        }
        return new ArrayList<String>(names);
    }
    
    public int getResolutionForVersion(String version) {        
        if (version.equals(VERSION)) {            
            return getBaseDirsForVersion(version).size() == 1 
                    ? ONLY_THIS_LOCATION_USED : INSTALLATION_BLOCKED;
        }
        return LOCATION_COULD_NOT_BE_USED;
    }


    public List<String> getRestrictedDirectories() {
        List<String> result = new ArrayList<String>();
        for (String version : getInstalledVersions()) {
            if (!version.equals(getCurrentVersion())) {
                result.addAll(getBaseDirsForVersion(version));
            }
        }
        return result;
    }
    
    public String getAllowedDirectory() {  
        for (PackageDescr descr : conflictedPackages) {
            if (descr.getVersion().equals(getCurrentVersion())) {
                return descr.getBaseDirectory();
            }
        }
        return null;
    }

    public String getCurrentVersion() {
        return VERSION;
    }
    
    public static int LOCATION_COULD_NOT_BE_USED = 0;
    public static int ONLY_THIS_LOCATION_USED = 1;
    public static int INSTALLATION_BLOCKED = 2;
    
}
