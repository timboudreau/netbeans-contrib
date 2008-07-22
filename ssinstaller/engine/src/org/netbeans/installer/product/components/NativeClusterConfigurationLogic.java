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
package org.netbeans.installer.product.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.env.EnvironmentInfoFactory;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.helper.FileEntry;
import org.netbeans.installer.utils.helper.Platform;
import org.netbeans.installer.utils.helper.RemovalMode;
import org.netbeans.installer.utils.helper.Text;
import org.netbeans.installer.utils.nativepackages.NativeInstallerFactory;
import org.netbeans.installer.utils.nativepackages.NativePackageInstaller;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.components.WizardComponent;


public class NativeClusterConfigurationLogic extends ProductConfigurationLogic {

    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String SS_BASE_UID =
            "ss-base"; // NOI18N

    public static final String DEVICE_FILE_PACKAGES_COUNTER = ".packages_counter";
    public static final String DEVICE_FILE_PACKAGE = ".package.";

    private int parseInteger(String value) {
        return (value == null || value.length() == 0) ? 0 : Integer.parseInt(value);
    }

    @Override
    public int getLogicPercentage() {
        return 100;
    }

    @Override
    public void install(Progress progress) throws InstallationException {
        LogManager.logEntry("Installing native package...");
        String installationLocation = Registry.getInstance().getProducts(SS_BASE_UID).get(0).getInstallationLocation().getAbsolutePath();
        final Platform platform = SystemUtils.getCurrentPlatform();
//        if (!PackageType.isPlatformSupported(platform)) {
//            throw new InstallationException("Platform is not supported!");
//        }

        try {
            NativePackageInstaller packageInstaller =// NativeInstallerFactory.getPlatformNativePackageInstaller(platform.isCompatibleWith(Platform.SOLARIS));
                    //PackageType.getPlatformNativePackage(platform).getPackageInstaller();
                    EnvironmentInfoFactory.getInstance().getPackageType().getPackageInstaller();
            packageInstaller.setDestinationPath(installationLocation);
            final int percentageChunk = Progress.COMPLETE / getProduct().getInstalledFiles().getSize();
            final int percentageLeak = Progress.COMPLETE % getProduct().getInstalledFiles().getSize();
            for (FileEntry installedFile : getProduct().getInstalledFiles()) {
                if (!installedFile.isDirectory() && packageInstaller.isCorrectPackageFile(installedFile.getName())) {
                    String value = getProduct().getProperty(DEVICE_FILE_PACKAGES_COUNTER);
                    int i = parseInteger(value) + 1;
                    Iterable<String> installedPackageNames = packageInstaller.install(installedFile.getFile().getAbsolutePath());
                    progress.addPercentage(percentageChunk);
                    progress.setDetail("Installing package" + installedFile.getName());
                    for (String packageName : installedPackageNames) {
                        Logger.getAnonymousLogger().warning("Installed package: " + packageName);
                        getProduct().setProperty(DEVICE_FILE_PACKAGE + String.valueOf(i), packageName);
                        i++;
                    }
                    getProduct().setProperty(DEVICE_FILE_PACKAGES_COUNTER, String.valueOf(i - 1));
                    installedFile.getFile().delete();
                }
            }
        } catch (Exception e) {
            throw new InstallationException("Inner Exception", e);
        }

        LogManager.logExit("Finish installing native package");
        progress.setPercentage(Progress.COMPLETE);

    }

    @Override
    public void uninstall(Progress progress) throws UninstallationException {
        LogManager.logEntry("Uninstalling native package...");
        final Platform platform = SystemUtils.getCurrentPlatform();
//        if (!PackageType.isPlatformSupported(platform)) {
//            throw new UninstallationException("Platform is not supported!");
//        }
        try {
//            PackageType packageType = PackageType.getPlatformNativePackage(platform);
//            NativePackageInstaller packageInstaller = packageType.getPackageInstaller();
            NativePackageInstaller packageInstaller = EnvironmentInfoFactory.getInstance().getPackageType().getPackageInstaller();
            //packageInstaller.setDestinationPath(Registry.getInstance().getProducts(SS_BASE_UID).get(0).getInstallationLocation().getAbsolutePath());
            packageInstaller.setDestinationPath("");

            //packageInstaller.uninstall(getProduct());
            String packagesValue = getProduct().getProperty(DEVICE_FILE_PACKAGES_COUNTER);
            for (int packageNumber = 1; packageNumber <= parseInteger(packagesValue); packageNumber++) {
                String value = getProduct().getProperty(DEVICE_FILE_PACKAGE + String.valueOf(packageNumber));
                progress.addPercentage( (Progress.COMPLETE - Progress.START) / parseInteger(packagesValue));
                packageInstaller.uninstall(value);
            }
        } catch (Exception e) {
            throw new UninstallationException("Inner Exception", e);
        }
        LogManager.logExit("Finish uninstalling native package");
        progress.setPercentage(Progress.COMPLETE);
    }

    @Override
    public List<WizardComponent> getWizardComponents() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean registerInSystem() {
        return false;
    }

    @Override
    public Text getLicense() {
        return null;
    }

    @Override
    public RemovalMode getRemovalMode() {
        return RemovalMode.LIST;
    }
    
    
}
/*enum PackageType {

SOLARIS_PKG(NativeInstallerFactory.getPlatformNativePackageInstaller(true), Platform.SOLARIS),
// LINUX_DEB(new LinuxDebianPackageInstaller(), Platform.LINUX),
LINUX_RPM(NativeInstallerFactory.getPlatformNativePackageInstaller(false), Platform.LINUX);
private NativePackageInstaller packageInstaller = null;
private Platform platform = null;

PackageType(NativePackageInstaller packageInstaller, Platform platform) {
this.packageInstaller = packageInstaller;
this.platform = platform;
}

public NativePackageInstaller getPackageInstaller() {
return packageInstaller;
}

public Platform getPlatform() {
return platform;
}

public static boolean isPlatformSupported(Platform platform) {
for (PackageType type : PackageType.values()) {
if (isCompatiblePlatforms(type.getPlatform(), platform)) {
return true;
}
}
return false;
}

private static boolean isCompatiblePlatforms(Platform platform1, Platform platform2) {
return platform1.getOsFamily().equals(platform2.getOsFamily());
}

public static PackageType getPlatformNativePackage(Platform platform) {
if (isCompatiblePlatforms(platform, Platform.SOLARIS)) {
return SOLARIS_PKG;
}
if (isCompatiblePlatforms(platform, Platform.LINUX)) {
//if (isCompatibleLinuxDistribution(UBUNTU, DEBIAN)) {
//   return LINUX_DEB;
//} else {
return LINUX_RPM;
//}
}
return null;
}

public static boolean isCompatibleLinuxDistribution(String... distributionNames) {
try {
// This is a preffered way, but it's only possible then distribution is LSB compartible. 
//Process p = new ProcessBuilder("lsb_release", "-sd").start();
Process p = new ProcessBuilder("sh", "-c", "cat /etc/*-release").start();
if (p.waitFor() == 0) {
BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
String line = null;
while ((line = output.readLine()) != null) {
for (String distributionName : distributionNames) {
if (line.toLowerCase().contains(distributionName.toLowerCase())) {
return true;
}
}
}
}
} catch (InterruptedException ex) {
Logger.getLogger(NativeClusterConfigurationLogic.class.getName()).log(Level.SEVERE, null, ex);
} catch (IOException ex) {
Logger.getLogger(NativeClusterConfigurationLogic.class.getName()).log(Level.SEVERE, null, ex);
}
return false;
}
private static final String UBUNTU = "ubuntu";
private static final String DEBIAN = "debian";
}*/
