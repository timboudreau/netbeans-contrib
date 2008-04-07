/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.installer.utils.nativepackages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;

/**
 *
 * @author Igor Nikiforov
 */
public class SolarisNativePackageInstaller implements NativePackageInstaller {

    public static final String DEVICE_FILES_COUNTER = "device_files_counter";
    public static final String DEVICE_FILE = "device_file.";
    public static final String DEVICE_FILE_PACKAGES_COUNTER = ".packages_counter";
    public static final String DEVICE_FILE_PACKAGE = ".package.";
    private String target = null;
    //TODO fix
    static final File TMP_DIR = new File("/tmp");
    File defaultResponse;
    File defaultAdminFile;

    public SolarisNativePackageInstaller() {
        try {
            defaultResponse = File.createTempFile("nbi-response", "", TMP_DIR);
            PrintStream out = new PrintStream(defaultResponse);
            out.println("LIST_FILE=/tmp/depend_list.\nTRLR_RESP=/tmp/response.\n");
        } catch (IOException ex) {
            throw new Error("Unexpected Error.", ex);
        }
    }

    public void setDestinationPath(String path) {
        target = path;
        try {
            defaultAdminFile = File.createTempFile("nbi-admin", "", TMP_DIR);
            PrintStream out = new PrintStream(defaultAdminFile);
            out.println("mail=\ninstance=unique\npartial=nocheck\nrunlevel=nocheck" +
                    "\nidepend=nocheck\nrdepend=nocheck\nspace=quit\nsetuid=nocheck\n" +
                    "conflict=nocheck\naction=nocheck\nbasedir=" + path + "\n");
        } catch (IOException ex) {
            throw new Error("Unexpected Error.", ex);
        }
    }

    public void install(String pathToPackage, Product product) throws InstallationException {
        String value = product.getProperty(DEVICE_FILES_COUNTER);
        int counter = parseInteger(value) + 1;
        DeviceFileAnalizer analizer = new DeviceFileAnalizer(pathToPackage);
        product.setProperty(DEVICE_FILE + String.valueOf(counter) + DEVICE_FILE_PACKAGES_COUNTER, String.valueOf(analizer.getPackagesCount()));
        int i = 1;
        if (analizer.containsPackages()) {
            for (String packageName : analizer) {
                try {
                    LogManager.log("executing command: pkgadd -n -d " + pathToPackage + " " + packageName);
                    Process p = new ProcessBuilder("/usr/sbin/pkgadd", "-n",
                            "-a", defaultAdminFile.getAbsolutePath(),
                            "-r", defaultResponse.getAbsolutePath(),
                            "-d", pathToPackage,
                            packageName).start();
                    
                    if (p.waitFor() != 0) {
                        String line;
                        StringBuffer message = new StringBuffer();
                        message.append("Error = ");
                        BufferedReader input =
                                new BufferedReader(new InputStreamReader(p.getErrorStream()));
                        while ((line = input.readLine()) != null) {
                            message.append(line);
                        }
                        message.append("\n Output = ");
                        input =
                                new BufferedReader(new InputStreamReader(p.getInputStream()));
                        while ((line = input.readLine()) != null) {
                            message.append(line);
                        }
                        throw new InstallationException("Error native. " + message);
                    }
                    product.setProperty(DEVICE_FILE + String.valueOf(counter) + DEVICE_FILE_PACKAGE + String.valueOf(i), packageName);
                    i++;
                } catch (InterruptedException ex) {
                    throw new InstallationException("Error native.", ex);
                } catch (IOException ex) {
                    throw new InstallationException("Error native.", ex);
                }
            }
            product.setProperty(DEVICE_FILES_COUNTER, String.valueOf(counter));
        }
    }

    public void uninstall(Product product) throws UninstallationException {
        String devicesValue = product.getProperty(DEVICE_FILES_COUNTER);
        for (int deviceNumber = 1; deviceNumber <= parseInteger(devicesValue); deviceNumber++) {
            String packagesValue = product.getProperty(DEVICE_FILE + String.valueOf(deviceNumber) + DEVICE_FILE_PACKAGES_COUNTER);
            for (int packageNumber = 1; packageNumber <= parseInteger(packagesValue); packageNumber++) {
                try {
                    String value = product.getProperty(DEVICE_FILE + String.valueOf(deviceNumber) + DEVICE_FILE_PACKAGE + String.valueOf(packageNumber));
                    LogManager.log("executing command: pkgrm -R " + target + " -n " + value);
                    Process p = new ProcessBuilder("/usr/sbin/pkgrm", "-n",
                              "-a", defaultAdminFile.getAbsolutePath(), value).start();
                    if (p.waitFor() != 0) {
                        throw new UninstallationException("Error native. Returned not zero.");
                    }
                } catch (InterruptedException ex) {
                    throw new UninstallationException("Error native.", ex);
                } catch (IOException ex) {
                    throw new UninstallationException("Error native.", ex);
                }
            }
        }
        product.setProperty(DEVICE_FILES_COUNTER, "0");
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
                    while ((line = lines.readLine()) != null) {
                        String[] fields = line.split("[ ]+", PKGINFO_OUTPUT_FILEDS_COUNT);
                        if (fields.length == PKGINFO_OUTPUT_FILEDS_COUNT) {
                            packages.add(fields[PKGINFO_PACKAGE_NAME_FIELD_INDEX]);
                        }
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
