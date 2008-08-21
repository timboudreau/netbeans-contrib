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

package org.netbeans.installer.infra.build.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.tools.ant.Task;
import org.netbeans.installer.infra.build.ant.utils.Utils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.env.impl.LinuxRPMPackagesAnalyzer;
import org.netbeans.installer.utils.env.PackageType;

public class ExtendedSizeOf extends Task {
    
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    /**
     * File for which the size should be calculated.
     */
    private File file;
    
    /**
     * Name of the property whose value should contain the size.
     */
    private String property;
    
    // setters //////////////////////////////////////////////////////////////////////
    /**
     * Setter for the 'file' property.
     * 
     * @param path New value for the 'path' property.
     */
    public void setFile(final String path) {
        file = new File(path);
        if (!file.equals(file.getAbsoluteFile())) {
            file = new File(getProject().getBaseDir(), path);
        }
    }
    
    /**
     * Setter for the 'property' property.
     * 
     * @param property New value for the 'property' property.
     */
    public void setProperty(final String property) {
        this.property = property;
    }
    
    // execution ////////////////////////////////////////////////////////////////////
    /**
     * Executes the task.
     */
    public void execute() {
        Utils.setProject(getProject());
        
        String[] fields = null;
        if (LinuxRPMPackagesAnalyzer.isRPMSupported()) {
            log("For rpm-packages real content size will be used");
            long realSize = extendedSize(file);
            long size = Utils.size(file);
            if (size != realSize) {
                log(String.format("Real content size is: %1$d, files size is: %2$d", realSize, size));
            }
            getProject().setProperty(property, Long.toString(realSize));
        } else {
            log("Using file size as content size: " + file.getAbsolutePath());            
            getProject().setProperty(property, Long.toString(Utils.size(file)));
        }
    }
    
    private long extendedSize(final File file) {
        long size = 0;
        
        if (file.isDirectory()) {
            for (File child: file.listFiles()) {
                size += extendedSize(child);
            }
        }
        
        size += PackageType.LINUX_RPM.getPackageContentSize(file.getAbsolutePath());
        
        return size;
    }    
   
}

