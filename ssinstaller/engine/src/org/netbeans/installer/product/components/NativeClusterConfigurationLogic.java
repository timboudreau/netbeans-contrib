/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.installer.product.components;

import java.util.Collections;
import java.util.List;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.helper.FileEntry;
import org.netbeans.installer.utils.helper.Platform;
import org.netbeans.installer.utils.helper.Text;
import org.netbeans.installer.utils.nativepackages.NativePackageInstaller;
import org.netbeans.installer.utils.nativepackages.PackageType;
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
            NativePackageInstaller packageInstaller = PackageType.
                    getPlatformNativePackage(platform).getPackageInstaller();
            packageInstaller.setDestinationPath( Registry.getInstance().getProducts(SS_BASE_UID)
                    .get(0).getInstallationLocation().getAbsolutePath());
            for (FileEntry installedFile : getProduct().getInstalledFiles()) {
                if (!installedFile.isDirectory() 
                        && packageInstaller.isCorrectPackageFile(installedFile.getName())) {
                    packageInstaller.install(installedFile.getName(), getProduct());
                    installedFile.getFile().delete();
                }
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
            PackageType packageType = PackageType.getPlatformNativePackage(platform);
            NativePackageInstaller packageInstaller = packageType.getPackageInstaller();
            packageInstaller.setDestinationPath( Registry.getInstance().getProducts(SS_BASE_UID)
                    .get(0).getInstallationLocation().getAbsolutePath());
            packageInstaller.uninstall(getProduct());            
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
