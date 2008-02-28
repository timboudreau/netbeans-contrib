/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.installer.utils.nativepackages;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.helper.FileEntry;

/**
 *
 * @author Igor Nikifrov
 */
public class FakeNativePackageInstaller implements NativePackageInstaller {

//    public static String FAKE_PACKAGES_DIR = System.getProperty("user.home") + File.separator + "fake_packages";
    public static String FAKE_PACKAGES_DIR = "/tmp/installed_fake_packages";

    public boolean install(String pathToPackage, Product product) {
        try {
            LogManager.log("executing command: touch " + FAKE_PACKAGES_DIR + extractFileName(pathToPackage));
            Process p = new ProcessBuilder("touch", FAKE_PACKAGES_DIR + extractFileName(pathToPackage)).start();
            return p.waitFor() == 0;
        } catch (InterruptedException ex) {
            Logger.getLogger(FakeNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FakeNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean uninstall(Product product) {
        if (product == null) return true;
        try {
            for (FileEntry installedFile : product.getInstalledFiles()) {
                if (!installedFile.isDirectory()) {
                    LogManager.log("executing command: rm " + FAKE_PACKAGES_DIR + extractFileName(installedFile.getName()));
                    Process p = new ProcessBuilder("rm", FAKE_PACKAGES_DIR + extractFileName(installedFile.getName())).start();
                    LogManager.logExit("Finish uninstalling fake native package");
                    if (p.waitFor() != 0) return false;
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(FakeNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FakeNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private String extractFileName(String pathToPackage) {
        return pathToPackage.substring(pathToPackage.lastIndexOf(File.separatorChar));
    }

    public boolean isCorrectPackageFile(String pathToPackage) {
        return true;
    }
}
