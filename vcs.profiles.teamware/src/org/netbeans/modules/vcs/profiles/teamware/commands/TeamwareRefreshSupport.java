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
 * The Original Software is the Teamware module.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
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
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.commands;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import org.netbeans.modules.vcs.profiles.teamware.util.SFile;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

class TeamwareRefreshSupport {

    private static final File[] EMPTY_FILE_ARRAY = new File[0];

    static boolean ignoreFile(File file) {
        String leaf = file.getName();
        if (leaf.startsWith(".") || leaf.endsWith("~")) {
            return true;
        }
        if (file.isDirectory()) {
            return leaf.equals("SCCS") || leaf.equals("Codemgr_wsdata")
                || leaf.equals("deleted_files");
        } else {
            return false;
        }
    }
    
    static String[] listFile(
        File file,
        File sccsDir,
        CommandOutputListener stderr) {
            
        String state = null;
        String fileName = file.getName();
        String revision = "";
        if (file.isDirectory()) {
            if (ignoreFile(file)) {
                state = "Ignored";
            } else {
                state = "";
            }
            fileName += "/";
        } else {
            SFile sFile = new SFile(file);
            File pFile = new File(sccsDir, "p." + fileName);
            if (!sFile.exists()) {
                if (ignoreFile(file)) {
                    state = "Ignored";
                } else {
                    state = "Local";
                }
            } else {
                revision = sFile.getRevisions().getActiveRevision()
                    .getRevision();
                if (revision == null) {
                    revision = "";
                    stderr.outputLine(file + ": cannot determine revision number");
                }
                boolean writable = file.canWrite();
                if (pFile.exists()) {
                    state = "Editing";
                    if (!writable) {
                        stderr.outputLine(file + " should be writable");
                    }
                } else {
                    if (file.exists()) {
                        if (writable) {
                            state = "Writable but not checked out for editing";
                        } else {
                            state = "Checked in";
                        }
                    } else {
                        state = "Needs checkout";
                    }
                    if (writable) {
                        stderr.outputLine(file + " should not be writable");
                    }
                }
            }
        }
        String[] data = {
            state,
            fileName,
            revision
        };
        return data;
    }
    
    static File[] listFilesInDir(final File dir) {
        if (ignoreFile(dir)) {
            return EMPTY_FILE_ARRAY;
        }
        final Set fileSet = new TreeSet();
        File[] files = dir.listFiles();
        if (files != null) {
            fileSet.addAll(Arrays.asList(files));
        }
        File sccsDir = new File(dir, "SCCS");
        if (sccsDir.exists()) {
            sccsDir.list(new FilenameFilter() {
                public boolean accept(File sdir, String name) {
                    if (name.startsWith("s.")) {
                        fileSet.add(new File(dir, name.substring(2)));
                    }
                    return false;
                }
            });
        }
        return (File[]) fileSet.toArray(EMPTY_FILE_ARRAY);
    }
    
}
