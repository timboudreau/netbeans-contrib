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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.make2netbeans.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.cnd.make2netbeans.api.DivideProject;
import org.netbeans.modules.cnd.api.utils.AllSourceFileFilter;

/**
 * The class to divide project into subprojects
 *
 * @author Andrey Gubichev
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.cnd.make2netbeans.api.DivideProject.class)
public class DividerImpl implements DivideProject {

    private static String[] SourceSuffix;
    private static final String[] MakefileName = {"Makefile", "makefile", "GNUMakefile"}; // NOI18N
    private ArrayList<String> NewNames; //list of new makefil names
    private File mkfile; //main makefile
    private ArrayList<File> projectFiles; //list of project files
    private ArrayList<File> subproj; //list of subprojects
    private boolean isPerformed;

    /** Creates a new instance of DividerImpl */
    public DividerImpl() {
        SourceSuffix = AllSourceFileFilter.getAllSuffixes();
        isPerformed = false;
        projectFiles = new ArrayList<File>();
        subproj = new ArrayList<File>();
        NewNames = new ArrayList<String>();
        for (int i = 0; i < MakefileName.length; i++) {
            NewNames.add(MakefileName[i]);
        }
    }

    /**
     *
     * @return list of project files
     */
    public List<File> getFiles() {
        return projectFiles;
    }

    /**
     *
     * @return list of subprojects
     */
    public List<File> getSubprojects() {
        return subproj;
    }

    /**
     * initialize
     * @param makefile - project makefile
     */
    public void init(File makefile) {
        mkfile = makefile;
    }

    // check if file f is a makefile
    private boolean isMakefile(File f) {
        String s = f.getName();
        if (hasMakefileName(f)) {
            return true;
        }
        //    if (s.toLowerCase().contains("makefile")) {
        //    MakefileDetector detect = new MakefileDetector(f);
        //    if (detect.isMakefile()) {
        //        NewNames.add(f.getName());
        //        return true;
        //    }
        //    }
        return false;
    }

    // return true, if f has name "Makefile", "makefile" or "GNUMakefile"
    private boolean hasMakefileName(File f) {
        String s = f.getName();
        for (int i = 0; i < NewNames.size(); i++) {
            if (s.equals(NewNames.get(i))) {
                return true;
            }
        }
        return false;
    }

    //check if file f belongs to a project (i.e. f has right extension)
    private boolean isProjFile(File f) {
        String name = f.getName();
        for (int i = 0; i < SourceSuffix.length; i++) {
            if (name.endsWith("." + SourceSuffix[i])) { // NOI18N
                return true;
            }
        }
        return false;
    }

    /** find makefile in selected folder
     *  first of all, it attempts to find makefile with certain name from the MakefileName[].
     *  if there is no such file, it checks all files that are not source files
     *  if some file is a makefile, it's name will be added to the list
     */
    private File findMakefile(File folder) {
        if (!folder.isDirectory()) {
            return null;
        }
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory() && !isProjFile(files[i])) {
                if (hasMakefileName(files[i])) {
                    return files[i];
                }
            }
        }
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory() && !isProjFile(files[i])) {
                if (isMakefile(files[i])) {
                    return files[i];
                }
            }
        }
        return null;
    }

    // check if f is a project folder (i.e. f contains some  makefile)
    private boolean isProjFolder(File f) {
        File t = findMakefile(f);
        return t != null;
    }

    // divide project
    private void Divide() {
        isPerformed = true;
        File tmp = mkfile.getAbsoluteFile().getParentFile();
        if (!isMakefile(mkfile)) {
            return;
        }
        divide(tmp);
    }

    // auxiliary function for Divide()
    private void divide(File f) {
        //f -directory
        if (!f.isDirectory()) {
            return;
        }

        File[] fls = f.listFiles();

        for (int i = 0; i < fls.length; i++) {
            if (fls[i].isFile() && isProjFile(fls[i])) {
                projectFiles.add(fls[i]);
            } else {
                if (!isProjFolder(fls[i])) {
                    divide(fls[i]);
                } else {
                    subproj.add(fls[i]);
                }
            }
        }
    }

    /**
     *
     * @return true if project can be divided into subprojects
     */
    public boolean canBeDivided() {
        if (!isPerformed) {
            Divide();
        }

        return subproj.size() != 0;
    }
}