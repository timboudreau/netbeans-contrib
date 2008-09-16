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
import java.util.Set;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.env.impl.Solaris10CPUInfoParser;
import org.netbeans.installer.utils.env.impl.Solaris9CPUInfoParser;
import org.netbeans.installer.utils.env.impl.SolarisCPUInfoParser;

public class SolarisEnvironmentInfo extends EnvironmentInfo {

    private String name = null;
    private String version = null;
    private SolarisCPUInfoParser cpuInfoParser = null;

    SolarisEnvironmentInfo() {
    }

    private void fillOSInfo() {
        try {
            BufferedReader output = new BufferedReader(new FileReader("/etc/release"));
            String line = output.readLine();
            if (line != null) {
                String[] fields = line.trim().split("\\s");
                if (fields.length > 3) {                    
                    if (fields[1].contains("Express")) {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < fields.length - 2; i++) {
                            sb.append(fields[i]);
                            sb.append(' ');
                        }
                        name = sb.toString().trim();
                        version = fields[fields.length - 2];
                    } else {
                        name = fields[0].trim();
                        StringBuffer sb = new StringBuffer();
                        for (int i = 1; i < fields.length - 1; i++) {
                            sb.append(fields[i]);
                            sb.append(' ');
                        }
                        version = sb.toString().trim();
                    }                    
                }
            }
        } catch (IOException ex) {
            LogManager.log(ex);
        }
    }

    @Override
    public String getOSName() {
        if (name == null) fillOSInfo();
        return name;
    }

    @Override
    public String getOSVersion() {
        if (version == null) fillOSInfo();
        return version;
    }

    @Override
    protected Set<String> createInstalledPatchesSet() {
        return getPackageType().getInstalledPatches();
    }

 
    @Override
    public long getPhisicalMemorySize() {
            try {
                File tempFile = File.createTempFile("ssistaller", ".tmp");
                tempFile.deleteOnExit();
                Process p = new ProcessBuilder("sh", "-c", "/usr/sbin/prtconf > " + tempFile.getAbsolutePath()).start();
                if (p.waitFor() == 0) {
                    BufferedReader output = new BufferedReader(new FileReader(tempFile));
                    String line = output.readLine();                    
                    line = output.readLine();
                    if (line != null) {
                        long result = 0;
                        try {
                            result = Long.parseLong(line.replace("Memory size:", "").replace("Megabytes", "").trim());
                        } catch (NumberFormatException ex) {
                            LogManager.log(ex);
                        }
                        return result * 1024;    
                    }                    
                }
            } catch (InterruptedException ex) {
                LogManager.log(ex);
            } catch (IOException ex) {
                LogManager.log(ex);
            }
            return 0;
    }

    private void createCPUInfoParser() {
        if (version == null || name == null) fillOSInfo();
        if (version != null && version.startsWith("10") || name.equals("OpenSolaris")) {
            cpuInfoParser = new Solaris10CPUInfoParser();
        } else {
            cpuInfoParser = new Solaris9CPUInfoParser();
        }        
    }
    
    @Override
    public String getCPUInformation() {
        if (cpuInfoParser == null) createCPUInfoParser();
        return (cpuInfoParser == null)? null: cpuInfoParser.getCPUInformation();
    }

    @Override
    public PackageType getPackageType() {
        return PackageType.SOLARIS_PKG;
    }

    @Override
    public float getCPUClock() {
        if (cpuInfoParser == null) createCPUInfoParser();
        return (cpuInfoParser == null)? 0: cpuInfoParser.getCPUSpeed();
    }
         
}
