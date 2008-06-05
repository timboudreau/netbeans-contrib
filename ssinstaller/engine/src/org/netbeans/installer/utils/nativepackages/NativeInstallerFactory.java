/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.installer.utils.nativepackages;

/**
 *
 * @author lm153972
 */
public class NativeInstallerFactory {
        
    public static NativePackageInstaller getPlatformNativePackageInstaller(boolean isSolaris) {
            return isSolaris ?  new SolarisNativePackageInstaller() : new LinuxRPMPackageInstaller();
    }
}
