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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.turbo.TurboUtil;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.turbo.Statuses;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery;
import org.netbeans.api.vcs.FileStatusInfo;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import java.util.*;
import java.io.*;

/**
 * Adds selected local files to ignore list.
 * It future we can support CVS/Repository's EmptyDir entry too.
 *
 *
 * @author Petr Kuzel
 */
public class CvsAddToIgnoreList implements VcsAdditionalCommand {

    private VcsFileSystem vfs;

    public boolean exec(Hashtable vars, String[] args, CommandOutputListener stdoutListener, CommandOutputListener stderrListener, CommandDataOutputListener stdoutDataListener, String dataRegex, CommandDataOutputListener stderrDataListener, String errorRegex) {

        // collect data

        Collection c= ExecuteCommand.createProcessingFiles(vfs, vars);
        Iterator it = c.iterator();
        FileObject parent = null;
        List ignoredFiles = new ArrayList(c.size());
        while (it.hasNext()) {
            String next = (String) it.next();
            FileObject fo = vfs.findResource(next);
            String name = fo.getNameExt();
            FileProperties fprops = Turbo.getMeta(fo);
            if (fprops != null && FileStatusInfo.LOCAL.getName().equals(fprops.getStatus())) {
                parent = fo.getParent();
                ignoredFiles.add(name);
            } else {
                stdoutListener.outputLine(name + " is not local, skipping...");
            }
        }

        if (parent == null) return true;

        // append file names to .cvs ignore end

        File folder = FileUtil.toFile(parent);
        if (folder.canWrite() == false) return false;

        File ignore = new File(folder, ".cvsignore");  // NOI18N
        if (ignore.exists() == false) {
            try {
                ignore.createNewFile();
            } catch (IOException e) {
                stderrListener.outputLine(e.getLocalizedMessage());
                return false;
            }
        }

        try {
            OutputStream os = new FileOutputStream(ignore, true);
            PrintStream ps = new PrintStream(os);
            Iterator it2 = ignoredFiles.iterator();
            ps.println();
            while (it2.hasNext()) {
                String next = (String) it2.next();
                FileObject ignored = parent.getFileObject(next);
                ignoreRecursively(ignored);
                ps.println(next);
            }
            ps.close();
            stdoutListener.outputLine(ignore.getPath() + " updated.");
            return ps.checkError() == false;
        } catch (FileNotFoundException e) {
            stderrListener.outputLine(e.getLocalizedMessage());
            return false;
        }
    }

    /** Called by introspection. */
    public void setFileSystem(VcsFileSystem vfs) {
        this.vfs = vfs;
    }

    /** Assure that ignored file and their descendants status is ignored. */
    private void ignoreRecursively(FileObject fo) {
        FileAttributeQuery faq = FileAttributeQuery.getDefault();
        FileProperties fprops = (FileProperties) faq.readAttribute(fo, FileProperties.ID);
        FileProperties ignoredProps = new FileProperties(fprops);
        ignoredProps.setStatus(Statuses.STATUS_IGNORED);
        faq.writeAttribute(fo, FileProperties.ID, ignoredProps);
        if (fo.isFolder()) {
            FileObject[] children = fo.getChildren();
            for (int i = 0; i < children.length; i++) {
                FileObject next = children[i];
                ignoreRecursively(next);
            }
        }
    }
}
