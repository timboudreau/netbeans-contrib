/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.ssinstaller.products.packages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
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
public class SolarisNativePackageInstaller implements NativePackageInstaller {

    public static final String DEVICE_FILES_COUNTER = "device_files_counter";
    public static final String DEVICE_FILE = "device_file.";
    public static final String DEVICE_FILE_PACKAGES_COUNTER = ".packages_counter";
    public static final String DEVICE_FILE_PACKAGE = ".package.";
    
    public boolean install(String pathToPackage, Product product) {
        String value = product.getProperty(DEVICE_FILES_COUNTER);
        int counter = parseInteger(value) + 1;
        DeviceFileAnalizer analizer = new DeviceFileAnalizer(pathToPackage);
        product.setProperty(DEVICE_FILE + String.valueOf(counter) + DEVICE_FILE_PACKAGES_COUNTER, String.valueOf(analizer.getPackagesCount()));
        int i = 1;
        if (analizer.containsPackages()) {
            for(String packageName: analizer) {
                try {
                    LogManager.log("executing command: pkgadd -n -d " + pathToPackage + " " + packageName);
                    Process p = new ProcessBuilder("pkgadd", "-n", "-d", pathToPackage, packageName).start();
                    if (p.waitFor() != 0) return false;
                    product.setProperty(DEVICE_FILE + String.valueOf(counter) + DEVICE_FILE_PACKAGE + String.valueOf(i), packageName);
                    i++;
                } catch (InterruptedException ex) {
                    return false;
                } catch (IOException ex) {
                    return false;
                }
            }
            product.setProperty(DEVICE_FILES_COUNTER, String.valueOf(counter));        
        }
        return true;
    }

    public boolean uninstall(Product product) {
        String devicesValue = product.getProperty(DEVICE_FILES_COUNTER);
        for(int deviceNumber=1; deviceNumber<=parseInteger(devicesValue); deviceNumber++) {
            String packagesValue = product.getProperty(DEVICE_FILE + String.valueOf(deviceNumber) + DEVICE_FILE_PACKAGES_COUNTER);
            for(int packageNumber=1; packageNumber<=parseInteger(packagesValue); packageNumber++) {
                try {
                    String value = product.getProperty(DEVICE_FILE + String.valueOf(deviceNumber) + DEVICE_FILE_PACKAGE + String.valueOf(packageNumber));
                    LogManager.log("executing command: pkgrm -n " + value);
                    Process p = new ProcessBuilder("pkgrm", "-n", value).start();
                    if (p.waitFor() != 0) return false;
                } catch (InterruptedException ex) {
                    return false;
                } catch (IOException ex) {
                    return false;
                }
            }
        }
        product.setProperty(DEVICE_FILES_COUNTER, "0");        
        return true;
    }

    public boolean isCorrectPackageFile(String pathToPackage) {
        return DeviceFileAnalizer.isCorrectPackage(pathToPackage);
    }    
    
    private int parseInteger(String value) {
        return (value == null || value.length() == 0)? 0: Integer.parseInt(value);
    }
    
    public static class DeviceFileAnalizer implements Iterable<String> {
        
        public static int PKGINFO_OUTPUT_FILEDS_COUNT = 3;
        public static int PKGINFO_PACKAGE_NAME_FIELD_INDEX = 1;
        
        private List<String> packages = new LinkedList<String>();
                
        public DeviceFileAnalizer(String fileName) {
            try {
                Process p = new ProcessBuilder("pkginfo", "-d", fileName).start();
                if (p.waitFor() == 0) {
                    BufferedReader lines = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while((line = lines.readLine()) != null) {
                        String[] fields = line.split("[ ]+", PKGINFO_OUTPUT_FILEDS_COUNT);
                        if (fields.length == PKGINFO_OUTPUT_FILEDS_COUNT) packages.add(fields[PKGINFO_PACKAGE_NAME_FIELD_INDEX]);                    
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(SolarisNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SolarisNativePackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public static boolean isCorrectPackage(String fileName) {
            try {
                Process p = new ProcessBuilder("pkginfo", "-d", fileName).start();
                return p.waitFor() == 0;
            } catch (InterruptedException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        }
        
        public boolean containsPackages() {
            return !packages.isEmpty();
        }
        
        public int getPackagesCount() {
            return packages.size();
        }
        
        public Iterator<String> iterator() {
            return packages.iterator();
        }
        
    }

}
