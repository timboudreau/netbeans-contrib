/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.helper.FileEntry;
import org.netbeans.installer.utils.helper.Platform;
import org.netbeans.installer.utils.helper.Text;
import org.netbeans.installer.utils.nativepackages.NativeInstallerFactory;
import org.netbeans.installer.utils.nativepackages.NativePackageInstaller;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.components.WizardComponent;

/**
 *
 * @author lm153972
 */
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
    public void install(Progress progress) throws InstallationException {
        LogManager.logEntry("Installing native package...");
        /*try {
        if (!NativeUtils.getInstance().isCurrentUserAdmin()) throw new InstallationException("User is not root!");                
        } catch (NativeException ex) {
        throw new InstallationException("Unable to determine user rights!", ex);
        }*/
        final Platform platform = SystemUtils.getCurrentPlatform();
        if (PackageType.isPlatformSupported(platform)) {
            try {
                NativePackageInstaller packageInstaller =// NativeInstallerFactory.getPlatformNativePackageInstaller(platform.isCompatibleWith(Platform.SOLARIS));
                        PackageType.getPlatformNativePackage(platform).getPackageInstaller();
                packageInstaller.setDestinationPath(Registry.getInstance().getProducts(SS_BASE_UID).get(0).getInstallationLocation().getAbsolutePath());
                for (FileEntry installedFile : getProduct().getInstalledFiles()) {
                    if (!installedFile.isDirectory() && packageInstaller.isCorrectPackageFile(installedFile.getName())) {

                        String value = getProduct().getProperty(DEVICE_FILE_PACKAGES_COUNTER);
                        int counter = parseInteger(value) + 1;
//        DeviceFileAnalyzer analyzer = new DeviceFileAnalyzer(pathToPackage);
                        //product.setProperty(DEVICE_FILE + String.valueOf(counter) + DEVICE_FILE_PACKAGES_COUNTER, String.valueOf(analyzer.getPackagesCount()));
                        int i = counter;
                        //if (analyzer.containsPackages()) {
                        //Logger.getAnonymousLogger().warning("executing command: pkgadd -n -d " + pathToPackage + " " + packageName);
                        Iterable<String> installedPackageNames = packageInstaller.install(installedFile.getFile().getAbsolutePath());
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
        } else {
            throw new InstallationException("Platform is not supported!");
        }
        LogManager.logExit("Finish installing native package");
        progress.setPercentage(Progress.COMPLETE);
    }

    @Override
    public void uninstall(Progress progress) throws UninstallationException {
        LogManager.logEntry("Uninstalling native package...");
        /*try {
        if (!NativeUtils.getInstance().isCurrentUserAdmin()) throw new UninstallationException("User is not root!");                
        } catch (NativeException ex) {
        throw new UninstallationException("Unable to determine user rights!", ex);
        }*/
        final Platform platform = SystemUtils.getCurrentPlatform();
        if (PackageType.isPlatformSupported(platform)) {
            try {
                PackageType packageType = PackageType.getPlatformNativePackage(platform);
                NativePackageInstaller packageInstaller = packageType.getPackageInstaller();
                packageInstaller.setDestinationPath(Registry.getInstance().getProducts(SS_BASE_UID).get(0).getInstallationLocation().getAbsolutePath());
                // packageInstaller.uninstall(getProduct());
                String packagesValue = getProduct().getProperty(DEVICE_FILE_PACKAGES_COUNTER);
                for (int packageNumber = 1; packageNumber <= parseInteger(packagesValue); packageNumber++) {
                    String value = getProduct().getProperty(DEVICE_FILE_PACKAGE + String.valueOf(packageNumber));
                    packageInstaller.uninstall(value);
                }
            } catch (Exception e) {
                throw new UninstallationException("Inner Exception", e);
            }
        } else {
            throw new UninstallationException("Platform is not supported!");
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
}

enum PackageType {

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
}

