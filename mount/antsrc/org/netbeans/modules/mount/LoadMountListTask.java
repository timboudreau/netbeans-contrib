/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.mount;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * Defines the "classpath" and/or "sourcepath" corresponding to the mount list.
 * @author Jesse Glick
 */
public final class LoadMountListTask extends Task {

    private File mountlist;
    public void setMountlist(File f) {
        mountlist = f;
    }
    
    private String classpathProperty;
    public void setClasspathProperty(String p) {
        classpathProperty = p;
    }
    
    private String sourcepathProperty;
    public void setSourcepathProperty(String p) {
        sourcepathProperty = p;
    }
    
    public void execute() throws BuildException {
        if (mountlist == null) {
            throw new BuildException("You must set the 'mountlist' attribute", getLocation());
        }
        if (!mountlist.isFile() || !mountlist.canRead()) {
            throw new BuildException("No such file (or unreadable): " + mountlist, getLocation());
        }
        if (classpathProperty == null && sourcepathProperty == null) {
            throw new BuildException("You must set at least one of the 'classpath' or 'sourcepath' properties", getLocation());
        }
        StringBuffer classpath = new StringBuffer();
        StringBuffer sourcepath = new StringBuffer();
        try {
            InputStream is = new FileInputStream(mountlist);
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String url;
                while ((url = r.readLine()) != null) {
                    URL u = new URL(url);
                    String protocol = u.getProtocol();
                    if (protocol.equals("file")) {
                        // Directory entry. Add to classpath & sourcepath.
                        File dir = new File(URI.create(url));
                        addEntry(classpath, dir);
                        addEntry(sourcepath, dir);
                    } else if (protocol.equals("jar")) {
                        // JAR entry. Add to classpath only.
                        if (!url.endsWith("!/")) {
                            throw new BuildException("Cannot include JAR subfolders in a classpath: " + url, getLocation());
                        }
                        assert url.startsWith("jar:") : url;
                        String suburl = url.substring(4, url.length() - 2);
                        URL subu = new URL(suburl);
                        if (!subu.getProtocol().equals("file")) {
                            throw new BuildException("Only handle file: protocols nested inside jar: protocols: " + url, getLocation());
                        }
                        File archive = new File(URI.create(suburl));
                        addEntry(classpath, archive);
                    } else {
                        throw new BuildException("Unknown URL protocol in " + url, getLocation());
                    }
                }
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new BuildException("Error reading " + mountlist + ": " + e, e, getLocation());
        }
        if (classpathProperty != null) {
            String val = classpath.toString();
            log("Setting " + classpathProperty + " to " + val, Project.MSG_VERBOSE);
            getProject().setNewProperty(classpathProperty, val);
        }
        if (sourcepathProperty != null) {
            String val = sourcepath.toString();
            log("Setting " + sourcepathProperty + " to " + val, Project.MSG_VERBOSE);
            getProject().setNewProperty(sourcepathProperty, val);
        }
    }
    
    private static void addEntry(StringBuffer buf, File f) {
        if (buf.length() > 0) {
            buf.append(File.pathSeparatorChar);
        }
        buf.append(f.getAbsolutePath());
    }
    
}
