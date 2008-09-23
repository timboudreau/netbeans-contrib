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

package org.netbeans.installer.utils.env.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.env.PackageDescr;

public class SolarisPackagesAnalyzer extends AbstractPackageAnalyzer {
        
    public SolarisPackagesAnalyzer() {
        try {
            dataFile  = File.createTempFile("ssinstaller", ".tmp");
            dataFile.deleteOnExit();
            Process p = new ProcessBuilder("sh", "-c", "pkginfo -l > " + dataFile.getAbsolutePath()).start();
            p.waitFor();
        } catch (InterruptedException ex) {
            dataFile = null;
            LogManager.log(ex);
        } catch (IOException ex) {
            dataFile = null;
            LogManager.log(ex);
        }
    }
    
    public SolarisPackagesAnalyzer(String deviceFilePath) {
        try {
            dataFile  = File.createTempFile("ssinstaller", ".tmp");
            dataFile.deleteOnExit();
            Process p = new ProcessBuilder("sh", "-c", "pkginfo -d " + deviceFilePath + " -l > " + dataFile.getAbsolutePath()).start();
            p.waitFor();
        } catch (InterruptedException ex) {
            dataFile = null;
            LogManager.log(ex);
        } catch (IOException ex) {
            dataFile = null;
            LogManager.log(ex);
        }
    }
    

    
    protected  void initPackagesInfo() {
        BufferedReader output = null;
        try {
            output = new BufferedReader(new FileReader(dataFile));
            String line = null;
            int n = 0;
            PackageDescr packageDescr = null;
            while((line = output.readLine()) != null) {                
                String[] pair = line.trim().split(":");
                if (pair.length < 2) {                 
                    continue;
                }              
                pair[1] = pair[1].trim();
                if (pair[0].equals("PKGINST")) {
                    packageDescr = new PackageDescr(pair[1]);
                    installedPackages.put(pair[1], packageDescr);
                } else if (pair[0].equals("VERSION")) {
                    packageDescr.setVersion(pair[1].split(",")[0].trim());
                } else if (pair[0].equals("ARCH")) {
                    packageDescr.setArch(pair[1]);
                } else if (pair[0].equals("BASEDIR")) {
                    packageDescr.setBaseDirectory(pair[1]);
                }
            }
        } catch (FileNotFoundException ex) {
            LogManager.log(ex);
        } catch (IOException ex) {
            LogManager.log(ex);
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                LogManager.log(ex);
            }
        }
    }

}
