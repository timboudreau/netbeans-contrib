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

/**
 *
 * @author Igor Nikifrov
 */
public class FakeNativePackageInstaller implements NativePackageInstaller {

//    public static String FAKE_PACKAGES_DIR = System.getProperty("user.home") + File.separator + "fake_packages";
    public static final String FAKE_PACKAGES_DIR = "/tmp/installed_fake_packages/";
    public static final String FAKE_PACKAGES_COUNTER = "fake_packages_counter";
    public static final String FAKE_PACKAGE = "fake_package.";
    
    public String target = FAKE_PACKAGES_DIR;
    
    public boolean install(String pathToPackage, Product product) {
        try {
            LogManager.log("executing command: touch " + FAKE_PACKAGES_DIR + extractFileName(pathToPackage));
            int counter = parseInteger(product.getProperty(FAKE_PACKAGES_COUNTER)) + 1;            
            String packageName = extractFileName(pathToPackage);
            Process p = new ProcessBuilder("touch", FAKE_PACKAGES_DIR + packageName).start();
            if (p.waitFor() != 0) return false;
            product.setProperty(FAKE_PACKAGE + String.valueOf(counter), packageName);        
            product.setProperty(FAKE_PACKAGES_COUNTER, String.valueOf(counter));                    
        } catch (InterruptedException ex) {
            Logger.getLogger(FakeNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FakeNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean uninstall(Product product) {
        try {
            int counter = parseInteger(product.getProperty(FAKE_PACKAGES_COUNTER));            
            for(int i=1; i<=counter; i++) {
                String packageName = product.getProperty(FAKE_PACKAGE + String.valueOf(i));
                LogManager.log("executing command: rm " + FAKE_PACKAGES_DIR + packageName);
                Process p = new ProcessBuilder("rm", FAKE_PACKAGES_DIR + packageName).start();
                if (p.waitFor() != 0) return false;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(FakeNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FakeNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private int parseInteger(String value) {
        return (value == null || value.length() == 0)? 0: Integer.parseInt(value);
    }        
    
    private String extractFileName(String pathToPackage) {
        return pathToPackage.substring(pathToPackage.lastIndexOf(File.separatorChar) + 1);
    }

    public boolean isCorrectPackageFile(String pathToPackage) {
        return !pathToPackage.contains("LICENSE") && !(new File(pathToPackage).isDirectory());
    }

    public void setDestinationPath(String path) {
        target = path;
    }

}
