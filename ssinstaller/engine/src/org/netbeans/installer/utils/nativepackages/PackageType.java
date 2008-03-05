/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.installer.utils.nativepackages;

import org.netbeans.installer.utils.helper.Platform;

/**
 *
 * @author Igor Nikiforov
 */
public enum PackageType {

    SOLARIS_PKG(new SolarisNativePackageInstaller(), Platform.SOLARIS),
    LINUX_DEB(new LinuxNativePackageInstaller(), Platform.LINUX);
    
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
        for(PackageType type: PackageType.values()) {
            if (isCompatiblePlatforms(type.getPlatform(), platform)) return true;
        }
        return false;
    }
    
    private static boolean isCompatiblePlatforms(Platform platform1, Platform platform2) {
        return platform1.getOsFamily().equals(platform2.getOsFamily());
    }
    
    public static PackageType getPlatformNativePackage(Platform platform) {
        if (isCompatiblePlatforms(platform, Platform.SOLARIS)) return SOLARIS_PKG;
        if (isCompatiblePlatforms(platform, Platform.LINUX)) return LINUX_DEB;
        return null;
    }
            
}
