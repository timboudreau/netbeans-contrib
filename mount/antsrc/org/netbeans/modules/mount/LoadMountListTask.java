/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
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
