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
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * A very simple html project w/ no metadata.
 *
 * @author Tim Boudreau
 */
public final class HtmlProjectFactory implements ProjectFactory {
    public HtmlProjectFactory() {
    }

    public boolean isProject(FileObject fo) {
        boolean result = false;
        if (fo.isFolder()) {
            String nm = fo.getName().toUpperCase();
            result = "WWW".equals(nm) ||
                    "PUBLIC-HTML".equals(nm);
            if (!result) {
                result = fo.getFileObject ("nbweb") != null;
            }
            if (!result) {
                result = getKnownHtmlProjects().contains (fo.getPath());
            }
        }
        return result;
    }

    public Project loadProject(FileObject fo, ProjectState state) throws IOException {
        if (isProject (fo)) {
            HtmlProject result = new HtmlProject (fo, state);
            addKnownHtmlProject (result);
            return result;
        } else {
            return null;
        }
    }

    public void saveProject(Project p) throws IOException, ClassCastException {
        //do nothing
    }

    public static Set getKnownHtmlProjects() {
        String s = Preferences.userNodeForPackage(HtmlProjectFactory.class).get(HTML_PRJ_KEY, "");
        HashSet result = new HashSet();
        for (StringTokenizer tok = new StringTokenizer (s, ","); tok.hasMoreTokens();) {
            result.add (tok.nextToken().trim());
        }
        return result;
    }

    private static final String HTML_PRJ_KEY = "htmlProjects";
    public static void addKnownHtmlProject (HtmlProject proj) {
        addKnownHtmlProject (proj.getProjectDirectory());
    }
    public static void addKnownHtmlProject (FileObject fdir) {
        String dir = fdir.getPath();
        if (!getKnownHtmlProjects().contains(dir)) {
            String s= Preferences.userNodeForPackage(HtmlProjectFactory.class).get(HTML_PRJ_KEY, "");
            s = s + ',' + dir;
            Preferences.userNodeForPackage(HtmlProjectFactory.class).put(HTML_PRJ_KEY,
                    s);
        }
    }

    static void putHtmlProjectName(FileObject dir, String name) {
        String path = FileUtil.toFile(dir).getPath();
        Preferences.userNodeForPackage(HtmlProjectFactory.class).put (path, 
                name);
    }

    static String getHtmlProjectName (FileObject dir) {
        String path = FileUtil.toFile(dir).getPath();
        return Preferences.userNodeForPackage(HtmlProjectFactory.class).get (
                path, null);
    }

    static String getZipDestDir (FileObject projdir) {
        String path = FileUtil.toFile(projdir).getPath();
        return Preferences.userNodeForPackage(HtmlProjectFactory.class).get (
                path + ".zipdir", null); //NOI18N
    }

    static String getProjectMainFile (FileObject projdir) {
        String path = FileUtil.toFile(projdir).getPath();
        return Preferences.userNodeForPackage(HtmlProjectFactory.class).get (
                path + ".mainfile", null); //NOI18N
    }

    static void putHtmlZipDestDir(FileObject zipdir, String name) {
        String path = FileUtil.toFile(zipdir).getPath();
        Preferences.userNodeForPackage(HtmlProjectFactory.class).put (
                path + ".zipdir", name); //NOI18N
    }

    static void putHtmlMainFile(FileObject file, String name) {
        String path = FileUtil.toFile(file).getPath();
        Preferences.userNodeForPackage(HtmlProjectFactory.class).put (
                path + ".mainfile", name); //NOI18N
    }

    static {
        if (Boolean.getBoolean ("nb.htmlproject.clear")) { //NOI18N
            Preferences p = Preferences.userNodeForPackage(
                    HtmlProjectFactory.class);
            try {
                p.clear();
                p.flush();
            } catch (BackingStoreException bse) {
                ErrorManager.getDefault().notify (bse);
            }
        }
    }
}
