package org.netbeans.installer.utils.nativepackages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.utils.helper.Platform;

/**
 *
 * @author Igor Nikiforov
 */
public enum PackageType {

    SOLARIS_PKG(new SolarisNativePackageInstaller(), Platform.SOLARIS),
    LINUX_DEB(new LinuxDebianPackageInstaller(), Platform.LINUX),
    LINUX_RPM(new LinuxRPMPackageInstaller(), Platform.LINUX);
    
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
        if (isCompatiblePlatforms(platform, Platform.LINUX)) {
            if (isCompatibleLinuxDistribution(UBUNTU, DEBIAN)) return LINUX_DEB;
            else return LINUX_RPM;
        }
        return null;
    }
    
    public static boolean isCompatibleLinuxDistribution(String ... distributionNames) {
        try {
            // This is a preffered way, but it's only possible then distribution is LSB compartible. 
            //Process p = new ProcessBuilder("lsb_release", "-sd").start();
            Process p = new ProcessBuilder("sh", "-c", "cat /etc/*-release").start();
            if (p.waitFor() == 0) {
                BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = null;
                while((line = output.readLine()) != null) {
                    for(String distributionName: distributionNames) {
                        if (line.toLowerCase().contains(distributionName.toLowerCase())) return true;
                    }
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(LinuxDebianPackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LinuxDebianPackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return false;
    }
    
    private static final String UBUNTU = "ubuntu";
    private static final String DEBIAN = "debian";
            
}
