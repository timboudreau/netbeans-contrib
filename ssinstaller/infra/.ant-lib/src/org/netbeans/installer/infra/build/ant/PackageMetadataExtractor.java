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

public class PackageMetadataExtractor extends Task {
    
    private final String PROPERTIES_LENGTH = "product.properties.length";
    private final String PROPERTY_NAME_PATTERN = "product.properties.%1$d.name";
    private final String PROPERTY_VALUE_PATTERN = "product.properties.%1$d.value";
    private final String PACKAGES_LENGTH_PROPERTY = "packages_length";
    private final String PACKAGE_NAME_PROPERTY_PATTERN = "package_%1$d_name";
    
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    /**
     * File for which the size should be calculated.
     */
    private File file;
    
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
    
    // execution ////////////////////////////////////////////////////////////////////
    /**
     * Executes the task.
     */
    public void execute() {
        fillPackagesFields(file, String.format(PROPERTY_VALUE_PATTERN, getPackagesLengthPropertyNumber()));
    }
    
    private int getPackagesLengthPropertyNumber() {
        int propertiesLength = Integer.parseInt(getProject().getProperty(PROPERTIES_LENGTH));
        for(int i=1; i<=propertiesLength; i++) {
            if (getProject().getProperty(String.format(PROPERTY_NAME_PATTERN, i)).equals(PACKAGES_LENGTH_PROPERTY)) return i;
        }
        return addProperty(PACKAGES_LENGTH_PROPERTY, "0");
    }
    
    private int addProperty(String name, String value) {
        int number = increasePropertyValue(PROPERTIES_LENGTH);
        getProject().setProperty(String.format(PROPERTY_NAME_PATTERN, number), name);
        getProject().setProperty(String.format(PROPERTY_VALUE_PATTERN, number), value);
        return number;
    }
  
    private int increasePropertyValue(String property) {
        int value = Integer.parseInt(getProject().getProperty(property));
        getProject().setProperty(property, String.valueOf(++value));
        return value;
    }
    
    private void fillPackagesFields(final File file, String packagesCountProperty) {
        if (file.isDirectory()) {
            for (File child: file.listFiles()) {
                fillPackagesFields(child, packagesCountProperty);
            }
        } else {        
            if (PackageType.LINUX_RPM.isCorrectPackageFile(file.getAbsolutePath())) {
                addPackageName(PackageType.LINUX_RPM.getPackageNames(file.getAbsolutePath()).get(0), packagesCountProperty);
            } else {
                addPackageName(PackageType.SOLARIS_PKG.getPackageNames(file.getAbsolutePath()).get(0), packagesCountProperty);
            }
        }
    }   
    
    private void addPackageName(String name, String packagesCountProperty) {
        addProperty(String.format(PACKAGE_NAME_PROPERTY_PATTERN, increasePropertyValue(packagesCountProperty)), name);
    }
   
}


