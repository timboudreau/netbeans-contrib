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

import java.util.Map;
import java.util.Set;
import org.netbeans.installer.utils.env.impl.LinuxDistribution;
import org.netbeans.installer.utils.env.impl.LinuxDistributionInfo;
import org.netbeans.installer.utils.env.impl.LinuxProcFileSystemReader;


public class LinuxEnvironmentInfo extends EnvironmentInfo {

    private final String CPU_MODEL_FIELD_NAME = "model name";
    private final String CPU_SPEED_FIELD_NAME = "cpu MHz";
    private final String MEMORY_SIZE_FIELD_NAME = "MemTotal";
    
    private LinuxDistributionInfo info = null;
    
    LinuxEnvironmentInfo() {}
    
    private LinuxDistributionInfo getLinuxDistributionInfo() {
        if (info == null) info = LinuxDistribution.getCurrentDistribution().getInfo();        
        return info;
    }
    
    @Override
    public String getOSName() {
        return getLinuxDistributionInfo().getDistributionName();
    }

    @Override
    public String getOSVersion() {
        return getLinuxDistributionInfo().getDistributionVersion();
    }

    @Override
    protected Set<String> createInstalledPatchesSet() {
        return getPackageType().getInfo().getInstalledPatches();
    }

    @Override
    protected Map<String, String> createInstalledPackagesSet() {
        return getPackageType().getInfo().getInstalledPackages();
    }

    @Override
    public long getPhisicalMemorySize() {
        LinuxProcFileSystemReader pfsr = new LinuxProcFileSystemReader("meminfo");
        if (pfsr.containsField(MEMORY_SIZE_FIELD_NAME)) { 
            String[] memInfo = pfsr.getFieldValue(MEMORY_SIZE_FIELD_NAME).split(" ");
            if (memInfo.length == 2) return Long.parseLong(memInfo[0]);
        }
        return 0;
    }

    @Override
    public String getCPUInformation() {
        LinuxProcFileSystemReader pfsr = new LinuxProcFileSystemReader("cpuinfo");
        if (pfsr.containsField(CPU_MODEL_FIELD_NAME)) return pfsr.getFieldValue(CPU_MODEL_FIELD_NAME);
        return null;
    }

    @Override
    public PackageType getPackageType() {
        return getLinuxDistributionInfo().getPackageType();
    }

    @Override
    public float getCPUClock() {
        LinuxProcFileSystemReader pfsr = new LinuxProcFileSystemReader("cpuinfo");
        if (pfsr.containsField(CPU_SPEED_FIELD_NAME)) return Float.parseFloat(pfsr.getFieldValue(CPU_SPEED_FIELD_NAME));
        return 0;
    }

}
