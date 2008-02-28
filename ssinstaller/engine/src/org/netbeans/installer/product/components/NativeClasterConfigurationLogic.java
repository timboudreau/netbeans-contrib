/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.installer.product.components;

import java.util.Collections;
import java.util.List;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.helper.FileEntry;
import org.netbeans.installer.utils.helper.Platform;
import org.netbeans.installer.utils.nativepackages.NativePackageInstaller;
import org.netbeans.installer.utils.nativepackages.PackageType;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.components.WizardComponent;

/**
 *
 * @author lm153972
 */
public class NativeClasterConfigurationLogic extends ProductConfigurationLogic {
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
            NativePackageInstaller packageInstaller = PackageType.getPlatformNativePackage(platform).getPackageInstaller();
            for (FileEntry installedFile : getProduct().getInstalledFiles()) {
                if (!installedFile.isDirectory() && packageInstaller.isCorrectPackageFile(installedFile.getName())) {
                    if (!packageInstaller.install(installedFile.getName(), getProduct())) {
                        throw new InstallationException("Native package installation exception!");
                    } else installedFile.getFile().delete();
                }
            }
        } else throw new InstallationException("Platform is not supported!");
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
            PackageType packageType = PackageType.getPlatformNativePackage(platform);
            if (!packageType.getPackageInstaller().uninstall(getProduct())) {
                throw new UninstallationException("Native package uninstallation exception!");
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
    
}
