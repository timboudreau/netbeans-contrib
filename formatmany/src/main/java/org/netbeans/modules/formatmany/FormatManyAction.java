/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.formatmany;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.api.fileinfo.NonRecursiveFolder;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.LifecycleManager;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 * Format files, folders, packages, projects
 *
 * @todo Deal with guarded-sections documents? E.g. form files
 * 
 * @author Tor Norbye
 */
public final class FormatManyAction extends CookieAction {

    private int count = 0;

    protected void performAction(Node[] activatedNodes) {
        Lookup lookup = activatedNodes[0].getLookup();

        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(FormatManyAction.class, "Formatting"));

        ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(FormatManyAction.class, "FormattingTask"));
        handle.start();
        handle.switchToIndeterminate();

        NonRecursiveFolder folder = lookup.lookup(NonRecursiveFolder.class);
        if (folder != null) {
            formatFolder(handle, folder.getFolder(), false);
        } else {
            Project project = lookup.lookup(Project.class);
            if (project != null) {
                formatProject(handle, project);
            } else {
                DataObject dataObject = lookup.lookup(DataObject.class);
                if (dataObject != null) {
                    FileObject primaryFile = dataObject.getPrimaryFile();
                    if (primaryFile.isFolder()) {
                        formatFolder(handle, primaryFile, true);
                    } else {
                        formatFile(handle, primaryFile);
                    }
                }
            }
        }

        LifecycleManager.getDefault().saveAll();
        handle.finish();
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(FormatManyAction.class, "FormattingCount", count));
    }

    protected int mode() {
        return CookieAction.MODE_ALL;
    }

    public String getName() {
        return NbBundle.getMessage(FormatManyAction.class, "CTL_FormatManyAction");
    }

    protected Class[] cookieClasses() {

        return new Class[]{DataObject.class, Project.class, NonRecursiveFolder.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return true;
    }

    private void formatProject(ProgressHandle handle, Project project) {
        Sources sources = ProjectUtils.getSources(project);

        SourceGroup[] sourceGroups = sources.getSourceGroups(Sources.TYPE_GENERIC);
        for (SourceGroup group : sourceGroups) {
            FileObject root = group.getRootFolder();
            formatFolder(handle, root, true);
        }
    }

    private void formatFolder(ProgressHandle handle, FileObject dir, boolean recursive) {
        String dirName = dir.getNameExt();
        handle.progress("Formatting " + dirName);
        if (dirName.equals("nbproject")) {
            return;
        }
        final FileObject[] children = dir.getChildren();
        StatusDisplayer.getDefault().setStatusText(
                NbBundle.getMessage(FormatManyAction.class, "FormattingIn", children.length, dir.getNameExt()));
        for (FileObject file : children) {
            if (file.isReadOnly() || file.isVirtual() || file.isLocked() || !file.isValid()) {
                continue;
            }
            if (file.isFolder()) {
                if (recursive) {
                    formatFolder(handle, file, recursive);
                }
            } else {
                formatFile(handle, file);
            }
        }
    }

    private void formatFile(ProgressHandle handle, final FileObject fo) {
        try {
            DataObject dobj = DataObject.find(fo);
            EditorCookie ec = dobj.getLookup().lookup(EditorCookie.class);
            if (ec == null) {
                return;
            }
            handle.setDisplayName(NbBundle.getMessage(FormatManyAction.class, "FormattingName", fo.getNameExt()));
            StyledDocument document = ec.openDocument();
            if (document instanceof BaseDocument) {
                final BaseDocument doc = (BaseDocument) document;
                final Reformat f = Reformat.get(doc);
                f.lock();
                try {
                    doc.runAtomic(new Runnable() {
                        public void run() {
                            try {
                                f.reformat(0, doc.getLength());
                                count++;
                            } catch (BadLocationException ex) {
                                Exceptions.attachMessage(ex, "Failure while formatting " + FileUtil.getFileDisplayName(fo));
                                Exceptions.printStackTrace(ex);
                            }

                        }
                    });
                } finally {
                    f.unlock();
                }
            }
        } catch (DataObjectNotFoundException ex) {
            Exceptions.attachMessage(ex, "Failure while formatting " + FileUtil.getFileDisplayName(fo));
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.attachMessage(ex, "Failure while formatting " + FileUtil.getFileDisplayName(fo));
            Exceptions.printStackTrace(ex);
        }
    }
}

