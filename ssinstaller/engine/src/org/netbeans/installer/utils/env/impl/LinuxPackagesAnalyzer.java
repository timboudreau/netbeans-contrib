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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.netbeans.installer.utils.LogManager;

public abstract class LinuxPackagesAnalyzer implements Iterable<String> {
    
    private final int FIELDS_COUNT = 4;
    private final int SIZE_MULTIPLIER = 1024; // Package managers report size in kB
    
    protected File dataFile = null;
    private Map<String, String> versions = new HashMap<String, String>();
    private Map<String, String> archictectures = new HashMap<String, String>();
    private Map<String, Long> sizes = new HashMap<String, Long>();
   
    public boolean containsPackageInfo(String packageName) {
        return versions.containsKey(packageName);
    }
    
    private void initPackagesInfo() {
        BufferedReader output = null;
        try {
            output = new BufferedReader(new FileReader(dataFile));
            String line = null;
            while((line = output.readLine()) != null) {
                String[] fields = line.trim().split(" ");
                if (fields.length == FIELDS_COUNT) {
                    versions.put(fields[0].trim(), fields[1].trim());
                    sizes.put(fields[0].trim(), Long.parseLong(fields[2].trim()) * SIZE_MULTIPLIER);
                    archictectures.put(fields[0].trim(), fields[3].trim());
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
    
    public String getPackageVersion(String packageName) {
        if (dataFile != null) {
            if (versions.isEmpty()) initPackagesInfo();
            return versions.get(packageName);
        }
        return null;
    }
    
    public String getPackageArchitecture(String packageName) {
        if (dataFile != null) {
            if (archictectures.isEmpty()) initPackagesInfo();
            return archictectures.get(packageName);
        }
        return null;
    }

    public Long getPackageSize(String packageName) {
        if (dataFile != null) {
            if (sizes.isEmpty()) initPackagesInfo();
            return sizes.get(packageName);
        }
        return null;
    }    
    
    public Iterator<String> iterator() {
        if (dataFile != null && versions.isEmpty()) initPackagesInfo();
        return versions.keySet().iterator();
    }
    
    public boolean isActual() {
        if (dataFile != null) {
            if (archictectures.isEmpty()) initPackagesInfo();
            return !versions.isEmpty();
        }
        return false;        
    }
}
