/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.utils.nativepackages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Igor Nikiforov
 */
class LinuxDebianPackageInstaller implements NativePackageInstaller {

    public static final String PACKAGES_COUNTER = "deb_packages_counter";
    public static final String PACKAGE = "deb_package.";
    
    private String target = null;
  /*  
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
*/
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
