package org.netbeans.installer.utils.nativepackages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.product.components.Product;

/**
 *
 * @author Igor Nikiforov
 */
public class LinuxDebianPackageInstaller implements NativePackageInstaller {

    public static final String PACKAGES_COUNTER = "deb_packages_counter";
    public static final String PACKAGE = "deb_package.";
    
    private String target = null;
    
    public void install(String pathToPackage, Product product) throws InstallationException {
        String value = product.getProperty(PACKAGES_COUNTER);
        int counter = parseInteger(value) + 1;
        String packageName = getPackageName(pathToPackage);
        if (packageName != null) {
            try {
               // LogManager.log("executing command: dpkg -i " + pathToPackage);
                Process p = new ProcessBuilder("dpkg", "-i", pathToPackage).start();
                if (p.waitFor() != 0) throw new InstallationException("'dpkg -i' returned " + String.valueOf(p.exitValue()));
                product.setProperty(PACKAGE + String.valueOf(counter), packageName);        
                product.setProperty(PACKAGES_COUNTER, String.valueOf(counter));        
            } catch (InterruptedException ex) {
                throw new InstallationException("Error executing 'dpkg -i'!", ex);
            } catch (IOException ex) {
                throw new InstallationException("Error executing 'dpkg -i'!", ex);
            }
        }
    }

    public void uninstall(Product product) throws InstallationException {
        String packagesValue = product.getProperty(PACKAGES_COUNTER);
        for(int packageNumber=1; packageNumber<=parseInteger(packagesValue); packageNumber++) {
            try {
                String value = product.getProperty(PACKAGE + String.valueOf(packageNumber));
               // LogManager.log("executing command: dpkg -P " + value);
                Process p = new ProcessBuilder("dpkg", "-P", value).start();
                if (p.waitFor() != 0) throw new InstallationException("'dpkg -P' returned " + String.valueOf(p.exitValue()));
            } catch (InterruptedException ex) {
                throw new InstallationException("Error executing 'dpkg -P'!", ex);
            } catch (IOException ex) {
                throw new InstallationException("Error executing 'dpkg -P'!", ex);
            }
        }
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
            Logger.getLogger(LinuxDebianPackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LinuxDebianPackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setDestinationPath(String path) {
        target = path;
    }
    
}
