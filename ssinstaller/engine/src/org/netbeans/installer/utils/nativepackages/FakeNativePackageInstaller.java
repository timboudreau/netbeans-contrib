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
