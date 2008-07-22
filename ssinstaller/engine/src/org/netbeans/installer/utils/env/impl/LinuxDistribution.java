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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.netbeans.installer.utils.env.PackageType;
import org.netbeans.installer.utils.LogManager;

public enum LinuxDistribution {

    UBUNTU("buntu", new LinuxDistributionInfo() {

        private String version = null;
        private String name = null;
        
        private void fillFields() {
            try {
                Process p = new ProcessBuilder("lsb_release", "-sd").start();
                if (p.waitFor() == 0) {
                    BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = output.readLine();
                    if (line != null) {
                        String[] fields = line.split(" ");
                        if (fields.length == 2) {
                            name = fields[0];
                            version = fields[1];
                        }
                    }
                }
            } catch (InterruptedException ex) {
                LogManager.log(ex);
            } catch (IOException ex) {
                LogManager.log(ex);
            }
        }
        
        public PackageType getPackageType() {
            return PackageType.LINUX_DEB;
        }

        public String getDistributionName() {
            if (name == null) fillFields();
            return name;
        }

        public String getDistributionVersion() {
            if (version == null) fillFields();
            return version;
        }
    }),
    RED_HAT_ENTERPRISE("Red Hat", new LinuxDistributionInfo() {

        private final String DELIMITER = "release";
        
        private String version = null;
        private String name = null;
        
        public PackageType getPackageType() {
            return PackageType.LINUX_RPM;
        }

        private void fillFields() {
            try {
                Process p = new ProcessBuilder("lsb_release", "-sd").start();
                if (p.waitFor() == 0) {
                    BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = output.readLine();
                    if (line != null) {
                        line = line.replace("\"", "");
                        String[] fields = line.split(DELIMITER);
                        if (fields.length == 2) {
                            name = fields[0].trim();
                            version = fields[1].trim();
                        }
                    }
                }
            } catch (InterruptedException ex) {
                LogManager.log(ex);
            } catch (IOException ex) {
                LogManager.log(ex);
            }
        }
        
        public String getDistributionName() {
            if (name == null) fillFields();
            return name;
        }

        public String getDistributionVersion() {
            if (version == null) fillFields();
            return version;
        }
        
    }),            
    SUSE_9("SuSE", new LinuxDistributionInfo() {

        private String version = null;
        private String name = null;
        
        public PackageType getPackageType() {
            return PackageType.LINUX_RPM;
        }

        private void fillFields() {
            try {
                Process p = new ProcessBuilder("lsb_release", "-sd").start();
                if (p.waitFor() == 0) {
                    BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = output.readLine();
                    if (line != null) {
                        line = line.replace("\"", "");
                        String[] fields = line.split(" ");
                        if (fields.length >= 3) {
                            StringBuffer sb = new StringBuffer();
                            for(int i=0; i<fields.length-2; i++) {
                                sb.append(fields[i]);
                                sb.append(' ');
                            }
                            name = sb.toString().trim();
                            version = fields[fields.length - 2].trim();
                        }
                    }
                }
            } catch (InterruptedException ex) {
                LogManager.log(ex);
            } catch (IOException ex) {
                LogManager.log(ex);
            }
        }
        
        public String getDistributionName() {
            if (name == null) fillFields();
            return name;
        }

        public String getDistributionVersion() {
            if (version == null) fillFields();
            return version;
        }
        
    }),                        
    SUSE_10("SUSE", new LinuxDistributionInfo() {

        private String version = null;
        private String name = null;
        
        public PackageType getPackageType() {
            return PackageType.LINUX_RPM;
        }

        private void fillFields() {
            try {
                Process p = new ProcessBuilder("sh", "-c", "cat /etc/*-release").start();
                if (p.waitFor() == 0) {
                    BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = output.readLine();
                    if (line != null) {
                        if (line.startsWith("LSB_VERSION")) line = output.readLine();
                        if (line != null) {
                            String[] fields = line.split(" ");
                            if (fields.length >= 3) { 
                                StringBuffer sb = new StringBuffer();
                                for(int i=0; i<fields.length-2; i++) {
                                    sb.append(fields[i]);
                                    sb.append(' ');
                                }
                                name = sb.toString().trim();
                                version = fields[fields.length - 2].trim();
                            }
                        }
                    }
                }
            } catch (InterruptedException ex) {
                LogManager.log(ex);
            } catch (IOException ex) {
               LogManager.log(ex);
            }
        }
        
        public String getDistributionName() {
            if (name == null) fillFields();
            return name;
        }

        public String getDistributionVersion() {
            if (version == null) fillFields();
            return version;
        }
        
    }),
    UNKNOWN("__UnKnoWn_LiNuX_DiStR__", new LinuxDistributionInfo() {

        public PackageType getPackageType() {
            return null;
        }

        public String getDistributionName() {
            return null;
        }

        public String getDistributionVersion() {
            return null;
        }
        
    });
           
    private String id;
    private LinuxDistributionInfo info;
    
    LinuxDistribution(String id, LinuxDistributionInfo info) {
        this.id = id;
        this.info = info;
    }

    public String getId() {
        return id;
    }

    public LinuxDistributionInfo getInfo() {
        return info;
    }
    
    public static LinuxDistribution getCurrentDistribution() {
        try {
            BufferedReader versionFile = new BufferedReader(new FileReader("/proc/version"));
            String line = versionFile.readLine();
            if (line != null) {
                for (LinuxDistribution ld : values()) {
                    if (line.contains(ld.getId())) return ld;
                }
            }
        } catch (IOException ex) {
            LogManager.log(ex);
        }
        return UNKNOWN;
    }
      
}
