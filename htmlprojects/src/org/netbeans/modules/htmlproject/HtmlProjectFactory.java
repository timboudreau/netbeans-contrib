/*
The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.

You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.

When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */

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
