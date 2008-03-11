package org.netbeans.installer.utils.nativepackages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.LogManager;

/**
 *
 * @author Igor Nikiforov
 */
public class LinuxRPMPackageInstaller implements NativePackageInstaller {

    public static final String PACKAGES_COUNTER = "rpm_packages_counter";
    public static final String PACKAGE = "rpm_package.";
    
    private String target = null;
    
    public boolean install(String pathToPackage, Product product) {
        String value = product.getProperty(PACKAGES_COUNTER);
        int counter = parseInteger(value) + 1;
        String packageName = getPackageName(pathToPackage);
        if (packageName != null) {
            try {
                LogManager.log("executing command: rpm -i " + pathToPackage + (target == null? "": " --root " + target));
                Process p = null;
                if (target == null) {
                    p = new ProcessBuilder("rpm", "-i", pathToPackage).start();
                } else {
                    p = new ProcessBuilder("rpm", "-i", pathToPackage, "--root", target).start();
                }
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
        List<String> arguments = new LinkedList<String>();
        arguments.add("rpm");
        arguments.add("-e");
        for(int packageNumber=1; packageNumber<=parseInteger(packagesValue); packageNumber++) {
            arguments.add(product.getProperty(PACKAGE + String.valueOf(packageNumber)));
        }
        if (target != null) {
            arguments.add("--root");
            arguments.add(target);
        }
        try {
            LogManager.log("executing command: " + listToString(arguments));
            Process p = new ProcessBuilder(arguments).start();
            if (p.waitFor() != 0) return false;
        } catch (InterruptedException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }        
        return true;
    }
    
    private String listToString(List<String> list) {
        StringBuffer sb = new StringBuffer();
        for(String str: list) {
            sb.append(str);
            sb.append(' ');
        }
        return sb.toString();
    }
    
    public boolean isCorrectPackageFile(String pathToPackage) {
        return getPackageName(pathToPackage) != null;
    }
    
    public String getPackageName(String pathToPackage) {
        try {
            Process p = new ProcessBuilder("rpm", "-q", "-p", pathToPackage).start();
            if (p.waitFor() == 0) {
                BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = output.readLine();
                if (line != null) return line.trim();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(LinuxDebianPackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LinuxDebianPackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }    
    
    private int parseInteger(String value) {
        return (value == null || value.length() == 0)? 0: Integer.parseInt(value);
    }

    public void setDestinationPath(String path) {
        target = path;
    }

}
