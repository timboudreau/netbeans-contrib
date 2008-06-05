package org.netbeans.installer.utils.nativepackages;

import java.io.File;
import java.util.Collection;


/**
 *
 * @author Igor Nikifrov
 */
class FakeNativePackageInstaller implements NativePackageInstaller {

//    public static String FAKE_PACKAGES_DIR = System.getProperty("user.home") + File.separator + "fake_packages";
    public static final String FAKE_PACKAGES_DIR = "/tmp/installed_fake_packages/";
    public static final String FAKE_PACKAGES_COUNTER = "fake_packages_counter";
    public static final String FAKE_PACKAGE = "fake_package.";
    
    private String target = FAKE_PACKAGES_DIR;
    /*
    public void install(String pathToPackage, Product product) throws InstallationException {
        try {
         //   LogManager.log("executing command: touch " + target + extractFileName(pathToPackage));
            int counter = parseInteger(product.getProperty(FAKE_PACKAGES_COUNTER)) + 1;            
            String packageName = extractFileName(pathToPackage);
            Process p = new ProcessBuilder("touch", target + packageName).start();
            if (p.waitFor() != 0) throw new InstallationException("touch returned " + String.valueOf(p.exitValue()));
            
            product.setProperty(FAKE_PACKAGES_COUNTER, String.valueOf(counter));                    
        } catch (InterruptedException ex) {
            throw new InstallationException("Error executing touch!", ex);
        } catch (IOException ex) {
            throw new InstallationException("Error executing touch!", ex);
        }
    }

    public void uninstall(Product product) throws InstallationException {
        try {
            int counter = parseInteger(product.getProperty(FAKE_PACKAGES_COUNTER));            
            for(int i=1; i<=counter; i++) {
                String packageName = product.getProperty(FAKE_PACKAGE + String.valueOf(i));
           //     LogManager.log("executing command: rm " + target + packageName);
                Process p = new ProcessBuilder("rm", target + packageName).start();
                if (p.waitFor() != 0) throw new InstallationException("rm returned " + String.valueOf(p.exitValue()));
            }
        } catch (InterruptedException ex) {
            throw new InstallationException("Error executing rm!", ex);
        } catch (IOException ex) {
            throw new InstallationException("Error executing rm!", ex);
        }
    }
*/
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
        if (path != null && path.length() > 0) target = path;
    }

    public Iterable<String> install(String pathToPackage, Collection<String> packageNames) throws InstallationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterable<String> install(String pathToPackage) throws InstallationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void uninstall(Collection<String> packageNames) throws InstallationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void uninstall(String packageName) throws InstallationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
