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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 * License header resource/file abstraction class with static methods to load a
 * license header from file or registered license template.
 *
 * @author Nils Hoffmann
 */
public final class LicenseHeader {

    /**
     * <p>
     * Creates a <code>LicenseHeader</code> from a given
     * <code>FileObject</code>.</p>
     *
     * <p>
     * If the passed in <code>licenseName</code> is <em>null</em>, the name is
     * inferred from the <code>FileObject</code>'s name. According to NetBeans
     * conventions, license file names start with a prefixed <em>license-</em>
     * before the actual license abbreviation, e.g. <em>epl10</em> for the
     * Eclipse public license version 1.0 will have a final name of
     * <em>license-epl10.txt</em>.</p>
     *
     * <p>
     * If the passed in licenseName starts with <em>license-</em>, that prefix
     * is removed.</p>
     *
     * @param file the FileObject containing the license header text
     * @param licenseName the name of the license
     * @param isNetBeansTemplate whether the license is a netbeans template or
     * not
     * @return a new LicenseHeader
     * @throws NullPointerException if file is null
     */
    public static LicenseHeader fromFileObject(FileObject file, String licenseName, boolean isNetBeansTemplate) throws NullPointerException {
        if (file == null) {
            throw new NullPointerException("Parameter 'file' must not be null!");
        }
        String name;
        if (licenseName == null) {
            name = file.getName();
        } else {
            name = licenseName;
        }
        if (name.startsWith("license-")) {
            name = name.substring("license-".length());
        }
        String content;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(file.getInputStream());
            out = new ByteArrayOutputStream();
            FileUtil.copy(in, out);
            content = new String(out.toByteArray());
            LicenseHeader header = new LicenseHeader(name, content, file, isNetBeansTemplate);
            return header;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * <p>
     * Creates a <code>LicenseHeader</code> from a given <code>FileObject</code>
     * and <code>licenseName</code>.</p>
     *
     * @param file the FileObject containing the license header text
     * @param licenseName the name of the license
     * @return a new LicenseHeader
     * @see #fromFileObject(org.openide.filesystems.FileObject,
     * java.lang.String, boolean)
     */
    public static LicenseHeader fromFileObject(FileObject file, String licenseName) throws NullPointerException {
        return fromFileObject(file, licenseName, false);
    }

    /**
     * <p>
     * Creates a <code>LicenseHeader</code> from a given <code>File</code>.</p>
     *
     * @param file the File containing the license header text
     * @return a new LicenseHeader
     *
     * @see #fromFileObject(org.openide.filesystems.FileObject,
     * java.lang.String)
     */
    public static LicenseHeader fromFile(File file) throws NullPointerException {
        if (file == null) {
            throw new NullPointerException("Parameter 'file' must not be null!");
        }
        return fromFileObject(FileUtil.toFileObject(file), null);
    }

    /**
     * <p>
     * Retrieves and returns the available license templates converted to
     * {@link LicenseHeader} objects. Empty licenses are not returned.</p>
     *
     * @return the available license headers
     */
    public static Collection<? extends LicenseHeader> fromTemplates() {
        FileObject licenseTemplates = FileUtil.getConfigFile("Templates/Licenses");
        List<LicenseHeader> templateLicenses = new LinkedList<LicenseHeader>();
        for (FileObject license : licenseTemplates.getChildren()) {
            String name = license.getName();
            name = name.substring("license-".length());
            LicenseHeader licenseHeader = LicenseHeader.fromFileObject(license, name, true);
            if (licenseHeader.getLicenseHeader() == null || licenseHeader.getLicenseHeader().isEmpty()) {
                Logger.getLogger(LicenseHeader.class.getName()).log(Level.WARNING, "License file {0} seems to be empty! Skipping...", name);
            } else {
                templateLicenses.add(licenseHeader);
            }
        }
        return templateLicenses;
    }

    /**
     * <p>
     * Add the given header as a netbeans template under
     * <code>Templates/Licenses</code>.</p>
     *
     * <p>
     * The new license template will have a name according to license template
     * conventions. E.g. a license header with name <code>example</code> will
     * result in a template file stored at
     * <code>Templates/Licenses/license-example.txt</code> in the virtual file
     * system. The file can be retrieved via
     * <code>FileUtil.getConfigFile("Templates/Licenses/license-example.txt")</code>.</p>
     *
     * @param header the license header to add as a template
     * @return the license header representing the netbeans template or
     * <em>null</em> if any exceptions occurred.
     */
    public static LicenseHeader addAsNetBeansTemplate(LicenseHeader header) {
        String filename = "license-" + header.getName() + ".txt";
        FileObject licenseTemplates = FileUtil.getConfigFile("Templates/Licenses");
        BufferedWriter bos = null;
        try {
            FileObject templateFile = licenseTemplates.createData(filename);
            try {
                DataObject dobj = DataObject.find(templateFile);
                dobj.setTemplate(true);
                dobj.getNodeDelegate().setDisplayName(filename);
            } catch (DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
            }
            //even if the dataobject was not found,
            //we can still write our template to the file object
            try {
                bos = new BufferedWriter(new OutputStreamWriter(templateFile.getOutputStream(), Charset.forName("UTF-8")));
                bos.write(header.getLicenseHeader());
                bos.close();
            } catch (IOException ioex) {
                Exceptions.printStackTrace(ioex);
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return LicenseHeader.fromFileObject(templateFile, null, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    private final String name;
    private final String licenseHeader;
    private final FileObject fo;
    private final boolean netBeansTemplate;

    private LicenseHeader(String name, String licenseHeader, FileObject fo, boolean netBeansTemplate) {
        if (name == null) {
            throw new NullPointerException("Parameter 'name' must not be null!");
        }
        if (licenseHeader == null) {
            throw new NullPointerException("Parameter 'licenseHeader' must not be null!");
        }
        this.name = name;
        this.licenseHeader = licenseHeader;
        this.fo = fo;
        this.netBeansTemplate = netBeansTemplate;
    }

    /**
     * <p>
     * Returns the name of this LicenseHeader.</p>
     *
     * @return the license header name
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Returns the raw license header text that may require further
     * interpolation by e.g. freemarker.</p>
     *
     * @return the raw license header text
     */
    public String getLicenseHeader() {
        return licenseHeader;
    }

    /**
     * <p>
     * The FileObject associated to this LicenseHeader.</p>
     *
     * @return the physical file object
     */
    public FileObject getFileObject() {
        return this.fo;
    }

    /**
     * <p>
     * For informational purposes only. The method returns true, if
	 * {@link LicenseHeader#addAsNetBeansTemplate(org.netbeans.modules.licensechanger.api.LicenseHeader) }
     * has been used to add a custom license, or if the LicenseHeader has been
     * loaded via {@link LicenseHeader#fromTemplates() }.</p>
     *
     * @return whether this is a NetBeans template
     */
    public boolean isNetBeansTemplate() {
        return netBeansTemplate;
    }

    /**
     * Returns the display name of the associated <code>FileObject</code>'s
     * <code>DataObject</code>. If no <code>DataObject</code> can be found, the
     * name of this LicenseHeader is returned.
     *
     * @return the display name
     */
    @Override
    public String toString() {
        if (fo != null) {
            try {
                return DataObject.find(getFileObject()).getNodeDelegate().getDisplayName();
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.licenseHeader != null ? this.licenseHeader.hashCode() : 0);
        hash = 59 * hash + (this.fo != null ? this.fo.hashCode() : 0);
        hash = 59 * hash + (this.netBeansTemplate ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LicenseHeader other = (LicenseHeader) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.licenseHeader == null) ? (other.licenseHeader != null) : !this.licenseHeader.equals(other.licenseHeader)) {
            return false;
        }
        if (this.fo != other.fo && (this.fo == null || !this.fo.equals(other.fo))) {
            return false;
        }
        if (this.netBeansTemplate != other.netBeansTemplate) {
            return false;
        }
        return true;
    }
}
