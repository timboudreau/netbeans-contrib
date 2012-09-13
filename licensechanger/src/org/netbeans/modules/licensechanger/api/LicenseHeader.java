/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.licensechanger.api;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * License header resource/file abstraction class with static 
 * methods to load a license header from file or registered license template.
 * @author Nils Hoffmann
 */
public final class LicenseHeader {
 
    public static LicenseHeader fromFileObject(FileObject file, String licenseName, boolean isNetBeansTemplate) {
        String name = licenseName==null?file.getName():licenseName;
        String content;
        InputStream in;
        try {
            in = new BufferedInputStream(file.getInputStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FileUtil.copy(in, out);
            content = new String(out.toByteArray());
            LicenseHeader header = new LicenseHeader(name, content, file, isNetBeansTemplate);
            return header;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
    
    public static LicenseHeader fromFileObject(FileObject file, String licenseName) {
        return fromFileObject(file, licenseName, false);
    }
    
    public static LicenseHeader fromFile(File file) {
        return fromFileObject(FileUtil.toFileObject(file),null);
    }
    
    public static Collection<? extends LicenseHeader> fromTemplates() {
        FileObject licenseTemplates = FileUtil.getConfigFile("Templates/Licenses");
        List<LicenseHeader> templateLicenses = new LinkedList<LicenseHeader>();
        for(FileObject license:licenseTemplates.getChildren()) {
            String name = license.getName();
            name = name.substring("license-".length());
            LicenseHeader licenseHeader = LicenseHeader.fromFileObject(license,name,true);
            if(licenseHeader.getLicenseHeader() == null || licenseHeader.getLicenseHeader().isEmpty()) {
                System.err.println("License file "+name+" seems to be empty! Skipping...");
            }else{
                licenseHeader.setIsNetBeansTemplate(true);
                templateLicenses.add(licenseHeader);
            }
        }
        return templateLicenses;
    }
    
    public static void addAsNetBeansTemplate(LicenseHeader header) {
        String filename = "license-"+header.getName()+".txt";
        FileObject licenseTemplates = FileUtil.getConfigFile("Templates/Licenses");
        BufferedWriter bos = null;
        try {
            FileObject templateFile = licenseTemplates.createData(filename);
            bos = new BufferedWriter(new OutputStreamWriter(templateFile.getOutputStream(),Charset.forName("UTF-8")));
            bos.write(header.getLicenseHeader());
            header.setIsNetBeansTemplate(true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private final String name;
    private final String licenseHeader;
    private final FileObject fo;
    private boolean isNetBeansTemplate = false;

    private LicenseHeader(String name, String licenseHeader, FileObject fo, boolean isNetBeansTemplate) {
        this.name = name;
        this.licenseHeader = licenseHeader;
        this.fo = fo;
        this.isNetBeansTemplate = isNetBeansTemplate;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLicenseHeader() {
        return licenseHeader;
    }

    public FileObject getFileObject() {
        return this.fo;
    }

    public boolean isIsNetBeansTemplate() {
        return isNetBeansTemplate;
    }

    public void setIsNetBeansTemplate(boolean isNetBeansTemplate) {
        this.isNetBeansTemplate = isNetBeansTemplate;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
