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

package org.netbeans.modules.autoproject.core;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.autoproject.spi.Cache;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Removes mark that this is an automatic project.
 * Also restores freeform metadata if that was previously moved aside.
 * Closes this project and tries to remove it from memory, reopening the replacement if any.
 * XXX might be better as a DeleteOperationImplementation; see #157043.
 */
class DeregisterAction extends AbstractAction {

    private final AutomaticProject p;

    public DeregisterAction(AutomaticProject p) {
        super(NbBundle.getMessage(DeregisterAction.class, "DeregisterAction.label"));
        this.p = p;
    }

    public void actionPerformed(ActionEvent e) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                try {
                    final FileObject dir = p.getProjectDirectory();
                    final FileObject nbprojectBak = dir.getFileObject("nbproject.bak");
                    if (nbprojectBak != null) { // #153232
                        FileObject nbproject = dir.getFileObject("nbproject");
                        if (nbproject == null) {
                            // Simply move it back.
                            FileLock lock = nbprojectBak.lock();
                            try {
                                nbprojectBak.rename(lock, "nbproject", null);
                            } finally {
                                lock.releaseLock();
                            }
                        } else {
                            // SVN rename leaves the original behind at least until you commit.
                            // Impossible to even copy children one by one (because parent is marked deleted).
                            // Punt and ask user to fix it!
                            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                                    NbBundle.getMessage(DeregisterAction.class, "DeregisterAction.manual_restore"),
                                    NotifyDescriptor.WARNING_MESSAGE));
                        }
                    }
                    Cache.put(FileUtil.toFile(dir) + Cache.PROJECT, null);
                    OpenProjects.getDefault().close(new Project[] {p});
                    p.unregister();
                    Project p2 = ProjectManager.getDefault().findProject(dir);
                    if (p2 != null) {
                        OpenProjects.getDefault().open(new Project[] {p2}, false, true);
                    }
                } catch (IOException x) {
                    Logger.getLogger(DeregisterAction.class.getName()).log(Level.WARNING, null, x);
                }
            }
        });
    }

}
