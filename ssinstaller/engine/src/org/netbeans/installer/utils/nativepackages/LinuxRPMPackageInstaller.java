package org.netbeans.installer.utils.nativepackages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.helper.Platform;

/**
 *
 * @author Igor Nikiforov
 */
public class LinuxRPMPackageInstaller implements NativePackageInstaller {

    public static final String PACKAGES_COUNTER = "rpm_packages_counter";
    public static final String PACKAGE = "rpm_package.";
    
    private String target = null;
    
    public void install(String pathToPackage, Product product) throws InstallationException {
        Platform platform = Platform.LINUX_X64;
        if ( SystemUtils.getCurrentPlatform().equals(Platform.LINUX_X86) 
                && pathToPackage.contains("x86_64")) {
            return;
        }
        String value = product.getProperty(PACKAGES_COUNTER);
        int counter = parseInteger(value) + 1;
        String packageName = getPackageName(pathToPackage);
        if (packageName != null) {
            try {
              //  LogManager.log("executing command: rpm -i " + pathToPackage + (target == null? "": " --root " + target));
                Process p = null;
            
                if (target == null) {
                    p = new ProcessBuilder("rpm", "-i", "--nodeps", pathToPackage).start();
                } else {
                    p = new ProcessBuilder("rpm", "-i", "--nodeps", pathToPackage,"--relocate" , "/opt/sun=" + target).start();
                }
                
                     {
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
                 }
                
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
                product.setProperty(PACKAGE + String.valueOf(counter), packageName);        
                product.setProperty(PACKAGES_COUNTER, String.valueOf(counter));        
            } catch (InterruptedException ex) {
                throw new InstallationException("Error executing 'rpm -i'!", ex);
            } catch (IOException ex) {
                throw new InstallationException("Error executing 'rpm -i'!", ex);
            }
        }
    }
 
    public void uninstall(Product product) throws InstallationException {
        String packagesValue = product.getProperty(PACKAGES_COUNTER);
        List<String> arguments = new LinkedList<String>();
        arguments.add("rpm");
        arguments.add("-e");
        for(int packageNumber=1; packageNumber<=parseInteger(packagesValue); packageNumber++) {
            arguments.add(product.getProperty(PACKAGE + String.valueOf(packageNumber)));
        }/*
        if (target != null) {
            arguments.add("--root");
            arguments.add(target);
        }*/
        try {
           // LogManager.log("executing command: " + listToString(arguments));
            Process p = new ProcessBuilder(arguments).start();
            if (p.waitFor() != 0) throw new InstallationException("'rpm -e' returned " + String.valueOf(p.exitValue()));
        } catch (InterruptedException ex) {
            throw new InstallationException("Error executing 'rpm -e'!", ex);
        } catch (IOException ex) {
            throw new InstallationException("Error executing 'rpm -e'!", ex);
        }        
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
