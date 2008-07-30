/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.helper.Platform;

/**
 *
 * @author Igor Nikiforov
 */
class LinuxRPMPackageInstaller implements NativePackageInstaller {

    //  public static final String PACKAGES_COUNTER = ".packages_counter";
    // public static final String PACKAGE = ".package.";
    private String target = null;

    public Iterable<String> install(String pathToPackage, Collection<String> packageNames) throws InstallationException {
                //Platform platform = Platform.LINUX_X64;
        if (SystemUtils.getCurrentPlatform().equals(Platform.LINUX_X86) && pathToPackage.contains("x86_64")) {
            return new ArrayList<String>();
        }
        String packageName = getPackageName(pathToPackage);
        if (packageName != null) {
            try {
                //  LogManager.log("executing command: rpm -i " + pathToPackage + (target == null? "": " --root " + target));
                Process p = null;

                if (target == null) {
                    p = new ProcessBuilder("rpm", "-i", "--nodeps", pathToPackage).start();
                } else {
                    p = new ProcessBuilder("rpm", "-i", "--nodeps", pathToPackage, "--relocate", "/opt/sun=" + target).start();
                }

                getProcessOutput(p);

                if (p.waitFor() != 0) {
                    throw new InstallationException("Error native. " + getProcessOutput(p));
                }
            } catch (InterruptedException ex) {
                throw new InstallationException("Error executing 'rpm -i'!", ex);
            } catch (IOException ex) {
                throw new InstallationException("Error executing 'rpm -i'!", ex);
            }
        }
        return null;
    }

    public Iterable<String> install(String pathToPackage) throws InstallationException {
                //Platform platform = Platform.LINUX_X64;
        if (SystemUtils.getCurrentPlatform().equals(Platform.LINUX_X86) && pathToPackage.contains("x86_64")) {
            return new ArrayList();
        }
        String packageName = getPackageName(pathToPackage);
        if (packageName != null) {
            try {
                //  LogManager.log("executing command: rpm -i " + pathToPackage + (target == null? "": " --root " + target));
                Process p = null;

                if (target == null) {
                    p = new ProcessBuilder("rpm", "-i", "--nodeps", pathToPackage).start();
                } else {
                    p = new ProcessBuilder("rpm", "-i", "--nodeps", pathToPackage, "--relocate", "/opt/sun=" + target).start();
                }

                getProcessOutput(p);

                if (p.waitFor() != 0) {
                    throw new InstallationException("Error native. " + getProcessOutput(p));
                }
                ArrayList<String> res = new ArrayList<String>(1);
                res.add(packageName);
                return res;
            } catch (InterruptedException ex) {
                throw new InstallationException("Error executing 'rpm -i'!", ex);
            } catch (IOException ex) {
                throw new InstallationException("Error executing 'rpm -i'!", ex);
            }            
        }
        return null;
    }

  

    public void uninstall(String packageName) throws InstallationException {
        List<String> arguments = new LinkedList<String>();
        arguments.add("rpm");
        arguments.add("-e");
        arguments.add("--nodeps");
        arguments.add(packageName);

        try {
            // LogManager.log("executing command: " + listToString(arguments));
            Process p = new ProcessBuilder(arguments).start();
            if (p.waitFor() != 0) {
                throw new InstallationException("'rpm -e' returned " + String.valueOf(p.exitValue()) + " ! " + getProcessOutput(p));
            }
        } catch (InterruptedException ex) {
            throw new InstallationException("Error executing 'rpm -e'!", ex);
        } catch (IOException ex) {
            throw new InstallationException("Error executing 'rpm -e'!", ex);
        }
    }

    public void uninstall(Collection<String> packageNames) throws InstallationException {
        List<String> arguments = new LinkedList<String>();
        arguments.add("rpm");
        arguments.add("-e");
        arguments.add("--nodeps");
        arguments.addAll(packageNames);

        try {
            // LogManager.log("executing command: " + listToString(arguments));
            Process p = new ProcessBuilder(arguments).start();
            if (p.waitFor() != 0) {
                throw new InstallationException("'rpm -e' returned " + String.valueOf(p.exitValue()) + " ! " + getProcessOutput(p));
            }
        } catch (InterruptedException ex) {
            throw new InstallationException("Error executing 'rpm -e'!", ex);
        } catch (IOException ex) {
            throw new InstallationException("Error executing 'rpm -e'!", ex);
        }
    }

    private String getProcessOutput(Process p) throws IOException {
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
        return message.toString();
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
                if (line != null) {
                    return line.trim();
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(LinuxDebianPackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LinuxDebianPackageInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private int parseInteger(String value) {
        return (value == null || value.length() == 0) ? 0 : Integer.parseInt(value);
    }

    public void setDestinationPath(String path) {
        target = path;
    }
}
