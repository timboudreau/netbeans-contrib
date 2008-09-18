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

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.SystemUtils;

public class LinuxRPMPackagesAnalyzer extends LinuxPackagesAnalyzer {
    
    private final String QUERY_OPTIONS = " --qf \"%{NAME} %{VERSION} %{SIZE} %{ARCH} %{DIRNAMES}\\n\" ";
    
    private static Boolean isRPMSupported = null;
    
    public LinuxRPMPackagesAnalyzer(boolean patches) {
        try {
            dataFile  = File.createTempFile("ssinstaller", ".tmp");
            dataFile.deleteOnExit();
            Process p = new ProcessBuilder("sh", "-c", "rpm -qa" + (patches? "P": "") + QUERY_OPTIONS + " > " + dataFile.getAbsolutePath()).start();
            p.waitFor();
        } catch (InterruptedException ex) {
            dataFile = null;
            LogManager.log(ex);
        } catch (IOException ex) {
            dataFile = null;
            LogManager.log(ex);
        }
    }
    
    public LinuxRPMPackagesAnalyzer(String packageFilePath) {
        try {
            dataFile  = File.createTempFile("ssinstaller", ".tmp");
            dataFile.deleteOnExit();
            Process p = new ProcessBuilder("sh", "-c", "rpm -qp " + packageFilePath + QUERY_OPTIONS + " > " + dataFile.getAbsolutePath()).start();
            p.waitFor();
        } catch (InterruptedException ex) {
            dataFile = null;
            LogManager.log(ex);            
        } catch (IOException ex) {
            dataFile = null;
            LogManager.log(ex);            
        }
    }
    
    public static boolean isRPMSupported() {
        if (isRPMSupported == null) {
            boolean result = SystemUtils.isLinux();
            if (result) {
                try {
                    Process p = new ProcessBuilder("rpm", "--version").start();
                    result = (p.waitFor() == 0);
                } catch (InterruptedException ex) {
                    LogManager.log(ex);
                } catch (IOException ex) {
                    LogManager.log(ex);
                }    
            }
            isRPMSupported = result;
        }
        return isRPMSupported;
    }
    
    public static String[] getRPMPackageFieldsFromFileName(String pathToPackage) {
        String[] result = new String[3];
        String name = (new File(pathToPackage)).getName().replace(".rpm", "");
        int index = name.lastIndexOf('.');
        if (index > -1) {
            result[2] = name.substring(index + 1); 
            name = name.substring(0, index);
        } else return null;
        Matcher m = Pattern.compile("[1234567890]").matcher(name);
        m.find();
        index = m.start();
        if (index > -1) {
            result[0] = name.substring(0, index - 2); 
            result[1] = name.substring(index); 
        } else return null;        
        return result;
    }
    
}
