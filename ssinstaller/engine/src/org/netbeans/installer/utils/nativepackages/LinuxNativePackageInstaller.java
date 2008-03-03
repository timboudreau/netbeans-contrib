package org.netbeans.installer.utils.nativepackages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.LogManager;

/**
 *
 * @author Igor Nikiforov
 */
public class LinuxNativePackageInstaller implements NativePackageInstaller {

    public static final String PACKAGES_COUNTER = "packages_counter";
    public static final String PACKAGE = "package.";
    
    public boolean install(String pathToPackage, Product product) {
        String value = product.getProperty(PACKAGES_COUNTER);
        int counter = parseInteger(value) + 1;
        String packageName = getPackageName(pathToPackage);
        if (packageName != null) {
            try {
                LogManager.log("executing command: dpkg -i " + pathToPackage);
                Process p = new ProcessBuilder("dpkg", "-i", pathToPackage).start();
                if (p.waitFor() != 0) return false;
                product.setProperty(PACKAGE + String.valueOf(counter), packageName);        
                product.setProperty(PACKAGES_COUNTER, String.valueOf(counter));        
            } catch (InterruptedException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        }
        return true;
    }

    public boolean uninstall(Product product) {
        String packagesValue = product.getProperty(PACKAGES_COUNTER);
        for(int packageNumber=1; packageNumber<=parseInteger(packagesValue); packageNumber++) {
            try {
                String value = product.getProperty(PACKAGE + String.valueOf(packageNumber));
                LogManager.log("executing command: dpkg -P " + value);
                Process p = new ProcessBuilder("dpkg", "-P", value).start();
                if (p.waitFor() != 0) return false;
            } catch (InterruptedException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        }
        return true;
    }

    private int parseInteger(String value) {
        return (value == null || value.length() == 0)? 0: Integer.parseInt(value);
    }    
    
    public boolean isCorrectPackageFile(String pathToPackage) {
        return getPackageName(pathToPackage) != null;
    }
    
    public String getPackageName(String pathToPackage) {
        try {
            Process p = new ProcessBuilder("dpkg-deb", "-W", pathToPackage).start();
            if (p.waitFor() == 0) {
                BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = output.readLine();
                int tabIndex = line.indexOf('\t');
                if (line != null && tabIndex > -1) {
                    return line.substring(0, tabIndex);
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(LinuxNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LinuxNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
