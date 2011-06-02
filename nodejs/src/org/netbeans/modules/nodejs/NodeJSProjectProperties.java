/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Contributor(s): Tim Boudreau
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.builtin.Validators;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbCollections;
import org.openide.util.Parameters;

/**
 *
 * @author Tim Boudreau
 */
public class NodeJSProjectProperties {

    private final NodeJSProject project;

    NodeJSProjectProperties(NodeJSProject project) {
        this.project = project;
    }

    Project project() {
        return project;
    }

    public String getDisplayName() {
        String result = project.metadata().getValue(ProjectMetadata.PROP_NAME);
        if (result == null) {
            result = project.getProjectDirectory().getName();
        }
        return result;
    }

    public void setDisplayName(String name) {
        Parameters.notNull("name", name);
        project.metadata().setValue(ProjectMetadata.PROP_NAME, name);
    }

    public void setDescription(String description) {
        System.out.println("set description '" + description + "'");
        Parameters.notNull("description", description);
        project.metadata().setValue(ProjectMetadata.PROP_DESCRIPTION, description);
    }

    public void setLicense(String kind, URL url) {
        project.metadata().setValue(ProjectMetadata.PROP_LICENSE_KIND, kind);
        project.metadata().setValue(ProjectMetadata.PROP_LICENSE_URL, url.toString());
    }

    public void setLicenseType(String type) {
        project.metadata().setValue(ProjectMetadata.PROP_LICENSE_KIND, type);
    }

    public void setLicenseURL(URL url) {
        project.metadata().setValue(ProjectMetadata.PROP_LICENSE_URL, url.toString());
    }

    public String getLicenseType() {
        return project.metadata().getValue(ProjectMetadata.PROP_LICENSE_KIND);
    }

    public String getLicenseURL() {
        return project.metadata().getValue(ProjectMetadata.PROP_LICENSE_URL);
    }

    public String getDescription() {
        return project.metadata().getValue(ProjectMetadata.PROP_DESCRIPTION);
    }

    public void setAuthor(String author) {
        project.metadata().setValue(ProjectMetadata.PROP_AUTHOR_NAME, author);
    }

    public void setAuthorEmail(String email) {
        if (email != null && !"".equals(email)) {
            Problems p = new Problems();
            assert Validators.EMAIL_ADDRESS.validate(p, "email", email); //NOI18N
            if (p.hasFatal()) {
                throw new IllegalArgumentException(p.getLeadProblem().getMessage());
            }
        }
        project.metadata().setValue(ProjectMetadata.PROP_AUTHOR_EMAIL, email);
    }

    public void setBugTrackerURL(URL url) {
        project.metadata().setValue(ProjectMetadata.PROP_BUG_URL, url + "");
    }

    public void setKeywords(String commaDelimited) {
        String[] s = commaDelimited.split(",");
        List<String> l = new ArrayList<String>();
        for (String ss : s) {
            l.add(ss.trim());
        }
        project.metadata().setValue(ProjectMetadata.PROP_KEYWORDS, l);
    }

    public List<String> getKeywords() {
        List<?> l = project.metadata().getValues(ProjectMetadata.PROP_KEYWORDS);
        System.out.println("Keywords: " + l);
        return NbCollections.checkedListByCopy(l, String.class, false);
    }

    public void setMainFile(FileObject fileObject) {
        Parameters.notNull("fileObject", fileObject); //NOI18N
        FileObject root = project.getProjectDirectory();
        String path = FileUtil.getRelativePath(root, fileObject);
        project.metadata().setValue(ProjectMetadata.PROP_MAIN_FILE, path);
    }

    public FileObject getMainFile() {
        String relPath = project.metadata().getValue(ProjectMetadata.PROP_MAIN_FILE);
        if (relPath != null) {
            return project.getProjectDirectory().getFileObject(relPath);
        }
        return null;
    }

    public FileObject getSourceDir() {
        FileObject result = project.getProjectDirectory();
        return result;
    }

    public String getAuthorEmail() {
        return project.metadata().getValue(ProjectMetadata.PROP_AUTHOR_EMAIL);
    }

    public String getAuthorName() {
        return project.metadata().getValue(ProjectMetadata.PROP_AUTHOR_NAME);
    }

    String getBugTrackerURL() {
        return project.metadata().getValue(ProjectMetadata.PROP_BUG_URL);
    }

    public void setRunArguments(String args) {
        //Run arguments are very likely to be machine-specific and have no
        //business in package.json
        try {
            FileObject fo = project.getProjectDirectory().getFileObject(".nbrun");
            if (fo != null && (args == null || args.trim().length() == 0) ) {
                fo.delete();
                return;
            } else if (fo == null) {
                fo = project.getProjectDirectory().createData(".nbrun");
            }
            OutputStream out = fo.getOutputStream();
            PrintStream ps = new PrintStream(out);
            ps.println(args);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public String getRunArguments() {
        FileObject fo = project.getProjectDirectory().getFileObject(".nbrun");
        if (fo != null) {
            try {
                return fo.asText().trim();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }
}
