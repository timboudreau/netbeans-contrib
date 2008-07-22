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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.netbeans.installer.utils.LogManager;

public class LinuxProcFileSystemReader {
    
    private final String PROC_PATH = "/proc/";
    private final String PROC_FIELDS_DELIMITER = ":";
    
    private String fileName;
    private Map<String, String> fields = new HashMap<String, String>();
    
    public LinuxProcFileSystemReader(String fileName) {
        if (fileName == null || fileName.length() == 0) throw new IllegalArgumentException("Invalid file name provided");
        File file = new File(PROC_PATH + fileName);
        if (!file.exists()) throw new IllegalArgumentException("File not found in proc filesystem");
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
    
    private void readFields() {
        BufferedReader procFileReader = null;
        try {
            procFileReader = new BufferedReader(new FileReader(PROC_PATH + fileName));
            String line = null;
            while ((line = procFileReader.readLine()) != null) {
                String[] field = line.split(PROC_FIELDS_DELIMITER);
                if (field.length == 2) {
                    fields.put(field[0].trim(), field[1].trim());
                }
            }      
        } catch (FileNotFoundException ex) {
            LogManager.log(ex);            
        } catch (IOException ex) {
            LogManager.log(ex);
        } finally {
            try {
                procFileReader.close();
            } catch (IOException ex) {
                LogManager.log(ex);
            }
        }

    }
    
    public Set<String> getFieldNames() {
        if (fields.isEmpty()) readFields();
        return Collections.unmodifiableSet(fields.keySet());
    }
    
    public boolean containsField(String fieldName) {
        if (fields.isEmpty()) readFields();
        return fields.containsKey(fieldName);
    }
    
    public String getFieldValue(String fieldName) {
        if (fields.isEmpty()) readFields();
        return fields.get(fieldName);
    }
    
}

