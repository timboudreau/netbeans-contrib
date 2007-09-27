/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.


The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s): */

package org.netbeans.modules.htmlproject;

import java.io.IOException;
import java.util.Locale;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 * A very simple html project w/ no metadata dir.
 *
 * @author Tim Boudreau
 */
public final class HtmlProjectFactory implements ProjectFactory {
    public HtmlProjectFactory() {
    }

    public boolean isProject(FileObject fo) {
        boolean result = false;
        if (fo.isFolder()) {
            String nm = fo.getName().toUpperCase(Locale.ENGLISH);
            result = "WWW".equals(nm) ||
                    "PUBLIC-HTML".equals(nm);
            if (!result) {
                result = fo.getFileObject ("nbweb") != null;
            }
            if (!result) {
                result = isKnownHtmlProject(fo);
            }
        }
        return result;
    }

    public Project loadProject(FileObject fo, ProjectState state) throws IOException {
        if (isProject (fo)) {
            HtmlProject result = new HtmlProject (fo, state);
            /* Not a good idea to modify state without user action:
            addKnownHtmlProject (result);
             */
            return result;
        } else {
            return null;
        }
    }

    public void saveProject(Project p) throws IOException, ClassCastException {
        //do nothing
    }

    private static final String KEY_BASE = "org.netbeans.modules.htmlproject.";
    private static final String KEY_MARKER = KEY_BASE + "marker";
    private static final String KEY_NAME = KEY_BASE + "name";
    private static final String KEY_ZIP_DEST_DIR = KEY_BASE + "zipDestDir";
    private static final String KEY_MAIN_FILE = KEY_BASE + "mainFile";

    private static void addKnownHtmlProject (HtmlProject proj) throws IOException {
        addKnownHtmlProject (proj.getProjectDirectory());
    }
    public static void addKnownHtmlProject (FileObject projdir) throws IOException {
        projdir.setAttribute(KEY_MARKER, Boolean.TRUE);
    }
    private static boolean isKnownHtmlProject(FileObject projdir) {
        return Boolean.TRUE.equals(projdir.getAttribute(KEY_MARKER));
    }

    static String getHtmlProjectName (FileObject projdir) {
        return (String) projdir.getAttribute(KEY_NAME);
    }

    static String getZipDestDir (FileObject projdir) {
        return (String) projdir.getAttribute(KEY_ZIP_DEST_DIR);
    }

    static String getProjectMainFile (FileObject projdir) {
        return (String) projdir.getAttribute(KEY_MAIN_FILE);
    }

    static void putHtmlProjectName(FileObject projdir, String name) throws IOException {
        projdir.setAttribute(KEY_NAME, name);
    }

    static void putHtmlZipDestDir(FileObject projdir, String name) throws IOException {
        projdir.setAttribute(KEY_ZIP_DEST_DIR, name);
    }

    static void putHtmlMainFile(FileObject projdir, String name) throws IOException {
        projdir.setAttribute(KEY_MAIN_FILE, name);
    }

}
