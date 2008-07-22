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

package org.netbeans.installer.utils.env;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.nativepackages.NativeInstallerFactory;
import org.netbeans.installer.utils.nativepackages.NativePackageInstaller;

public enum PackageType {
    
    SOLARIS_PKG(NativeInstallerFactory.getPlatformNativePackageInstaller(true), new PackagesInfo() {

        public Map<String, String> getInstalledPackages() {
            try {
                File tempFile = File.createTempFile("ssistaller", ".tmp");
                tempFile.deleteOnExit();
                Process p = new ProcessBuilder("sh", "-c", "pkginfo -x > " + tempFile.getAbsolutePath()).start();
                if (p.waitFor() == 0) {
                    Map<String, String> result = new HashMap<String, String>();
                    BufferedReader output = new BufferedReader(new FileReader(tempFile));
                    String line = null;                    
                    int n = 0;
                    String name = null;
                    while ((line = output.readLine()) != null) {
                        String[] fields = line.trim().split("\\s");
                        if (fields.length >= 2) {
                            if (n % 2 == 0) name = fields[0].trim();
                            if (n % 2 == 1) result.put(name, fields[1].trim());
                        }
                        n++;
                    }
                    return result;
                }
            } catch (InterruptedException ex) {
                LogManager.log(ex);
            } catch (IOException ex) {
                LogManager.log(ex);
            }
            return null;      
        }

        public Set<String> getInstalledPatches() {
            try {
                File tempFile = File.createTempFile("ssistaller", ".tmp");
                tempFile.deleteOnExit();
                Process p = new ProcessBuilder("sh", "-c", "/usr/bin/showrev -p > " + tempFile.getAbsolutePath()).start();
                if (p.waitFor() == 0) {
                    Set<String> result = new HashSet<String>();
                    BufferedReader output = new BufferedReader(new FileReader(tempFile));
                    String line = null;                    
                     while ((line = output.readLine()) != null) {
                        String[] fields = line.trim().split(" ");
                        if (fields.length >= 2) result.add(fields[1]);
                    }
                    return result;
                }
            } catch (InterruptedException ex) {
                LogManager.log(ex);
            } catch (IOException ex) {
                LogManager.log(ex);
            }
            return null;     
        }
        
    }),
    LINUX_RPM(NativeInstallerFactory.getPlatformNativePackageInstaller(false), new PackagesInfo() {

        private Map<String, String> queryRPM(String additionalParameters) {
            try {
                File tempFile = File.createTempFile("ssistaller", ".tmp");
                tempFile.deleteOnExit();
                Process p = new ProcessBuilder("sh", "-c", "rpm -qa" + additionalParameters + " --qf \"%{NAME} %{VERSION}\\n\" > " + tempFile.getAbsolutePath()).start();
                if (p.waitFor() == 0) {
                    Map<String, String> result = new HashMap<String, String>();
                    BufferedReader output = new BufferedReader(new FileReader(tempFile));
                    String line = null;                    
                    while ((line = output.readLine()) != null) {
                        String[] fields = line.split(" ");
                        if (fields.length == 2) {
                            result.put(fields[0].trim(), fields[1].trim());
                        }
                    }
                    return result;
                }
            } catch (InterruptedException ex) {
                LogManager.log(ex);
            } catch (IOException ex) {
                LogManager.log(ex);
            }
            return null;            
        }
        
        public Map<String, String> getInstalledPackages() {
            return queryRPM("");
        }

        public Set<String> getInstalledPatches() {
            Map<String, String> output = queryRPM("P");
            return (output == null)? null: new HashSet<String>(output.keySet());
        }
        
    }),
    LINUX_DEB(null, new PackagesInfo() {

        public Map<String, String> getInstalledPackages() {
            try {
                File tempFile = File.createTempFile("ssistaller", ".tmp");
                tempFile.deleteOnExit();
                Process p = new ProcessBuilder("sh", "-c", "dpkg -l > " + tempFile.getAbsolutePath()).start();
                if (p.waitFor() == 0) {
                    Map<String, String> result = new HashMap<String, String>();
                    BufferedReader output = new BufferedReader(new FileReader(tempFile));
                    for(int i=1; i<=5; i++)
                        output.readLine();
                    String line = null;                    
                    while ((line = output.readLine()) != null) {
                        StringTokenizer st = new StringTokenizer(line, " ");
                        if (st.countTokens() > 3) {
                            st.nextToken();
                            result.put(st.nextToken().trim(), st.nextToken().trim());
                        }
                    }
                    return result;
                }
            } catch (InterruptedException ex) {
                LogManager.log(ex);
            } catch (IOException ex) {
                LogManager.log(ex);
            }
            return null;
        }

        public Set<String> getInstalledPatches() {
            return null;
        }
        
    });
    
    private NativePackageInstaller packageInstaller = null;
    private PackagesInfo info = null;
    
    PackageType(NativePackageInstaller packageInstaller, PackagesInfo info) {
        this.packageInstaller = packageInstaller;
        this.info = info;
    }

    public NativePackageInstaller getPackageInstaller() {
        return packageInstaller;
    }

    public PackagesInfo getInfo() {
        return info;
    }
        
}
