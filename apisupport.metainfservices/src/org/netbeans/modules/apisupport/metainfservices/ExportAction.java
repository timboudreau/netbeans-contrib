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
package org.netbeans.modules.apisupport.metainfservices;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.awt.Dialog;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class ExportAction extends CookieAction {
    private static final Logger LOG = Logger.getLogger(ExportAction.class.getName());
    
    protected void performAction(Node[] activatedNodes) {
        MyTask task = new ExportAction.MyTask();
        FileObject fo = activatedNodes[0].getLookup().lookup(org.openide.filesystems.FileObject.class);
        if (fo == null) {
            return;
        }
        try {
            JavaSource source = JavaSource.forFileObject(fo);
            source.runUserActionTask(task, true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        task.postProcess(fo);
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(ExportAction.class, "CTL_ExportAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataObject.class
        };
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }

    private static final class MyTask implements CancellableTask<CompilationController> {
        Collection<String> allInterfaces = new TreeSet<String>();
        String clazzName;
        
        public void cancel() {
        }

        public void run(CompilationController cont) throws Exception {
            cont.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
            for (Tree t : cont.getCompilationUnit().getTypeDecls()) {
                if (t.getKind() == Tree.Kind.CLASS) {
                    TreePath path = cont.getTrees().getPath(cont.getCompilationUnit(), t);
                    Element e = cont.getTrees().getElement(path);
                    if (e instanceof TypeElement) {
                        TypeElement te = (TypeElement)e;
                        clazzName = te.getQualifiedName().toString();
                    }
                    findInterfaces(cont, e);
                }
            }
        }
        
        private void findInterfaces(CompilationController cont, Element e) {
            if (e == null) {
                return;
            }
            if (!e.getKind().isClass() && !e.getKind().isInterface()) {
                return;
            }
            TypeElement type = (TypeElement)e;
            allInterfaces.add(type.getQualifiedName().toString());
            
            findInterfaces(cont, type.getSuperclass());
            for (TypeMirror m : type.getInterfaces()) {
                findInterfaces(cont, m);
            }
        }
        
        private void findInterfaces(CompilationController cont, TypeMirror m) {
            findInterfaces(cont, cont.getTypes().asElement(m));
        }

        public void postProcess(FileObject fo) {
            FileObject target = null;
            Project p = FileOwnerQuery.getOwner(fo);

            if (p != null) {
                Sources s = ProjectUtils.getSources(p);
                SourceGroup[] arr = s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
                if (arr != null && arr.length > 0) {
                    target = arr[0].getRootFolder();
                }
            }


            if (allInterfaces.isEmpty() || clazzName == null) {
                NotifyDescriptor d = new NotifyDescriptor.Message(
                    NbBundle.getMessage(ExportAction.class, "MSG_CannotFindClass", fo),
                    NotifyDescriptor.WARNING_MESSAGE
                );
                DialogDisplayer.getDefault().notify(d);
                return;
            }

            WizardDescriptor wd = new WizardDescriptor(new ExportWizardIterator());

            wd.putProperty("implName", clazzName); // NOI18N
            wd.putProperty("interfaceNames", allInterfaces); // NOI18N
            wd.putProperty("target", target);

            Dialog d = DialogDisplayer.getDefault().createDialog(wd);
            d.setVisible(true);

            if (wd.FINISH_OPTION == wd.getValue()) {
                try {
                    createFiles(clazzName, wd, target);
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void createFiles(String implName, WizardDescriptor wd, FileObject target)
    throws IOException, FileNotFoundException {
        List<String> files = (List<String>)wd.getProperty("files"); // NOI18N
        createFiles(implName, files, target);
    }

    static void createFiles(String implName, List<String> files, FileObject target)
    throws IOException, FileNotFoundException {
        // lets apply the files
        for (String s : files) {
            FileObject f = FileUtil.createData(target, s);
            byte[] exist = new byte[(int)f.getSize()];
            InputStream is = f.getInputStream();
            int len = is.read(exist);
            is.close();
            //assert len == exist.length;

            String content = new String(exist);
            if (content.length() > 0 && !content.endsWith("\n")) { // NOI18N
                content = content + "\n"; // NOI18N
            }

            content = content + implName + "\n"; // NOI18N

            FileLock lock = f.lock();
            OutputStream os = f.getOutputStream(lock);
            os.write(content.getBytes());
            os.close();
            lock.releaseLock();
        }
    }
}

