/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.apisupport.metainfservices;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import java.awt.Dialog;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
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

    
    protected void performAction(Node[] activatedNodes) {
        try     {
            FileObject fo = activatedNodes[0].getLookup().lookup(org.openide.filesystems.FileObject.class);

            if (fo == null) {
                return;
            }
            MyTask task = new ExportAction.MyTask();
            JavaSource source = JavaSource.forFileObject(fo);

            source.runUserActionTask(task, true);
        }
        catch (IOException ex) {
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE,
                                                             ex.getMessage(), ex);
        }
/*

        FileObject target = null;
        Project p = FileOwnerQuery.getOwner(fo.getPrimaryFile());

        if (p != null) {
            Sources s = ProjectUtils.getSources(p);
            SourceGroup[] arr = s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            if (arr != null && arr.length > 0) {
                target = arr[0].getRootFolder();
            }
        }


        if (clazz == null || target == null) {
            NotifyDescriptor d = new NotifyDescriptor.Message(
                NbBundle.getMessage(ExportAction.class, "MSG_CannotFindClass", fo.getPrimaryFile().getPath()),
                NotifyDescriptor.WARNING_MESSAGE
            );
            DialogDisplayer.getDefault().notify(d);
            return;
        }

        WizardDescriptor wd = new WizardDescriptor(new ExportWizardIterator());

        wd.putProperty("implName", clazz.getName()); // NOI18N
        wd.putProperty("interfaceNames", allInterfaces); // NOI18N
        wd.putProperty("target", target);

        Dialog d = DialogDisplayer.getDefault().createDialog(wd);
        d.setVisible(true);

        if (wd.FINISH_OPTION == wd.getValue()) {
            try {
                createFiles(clazz.getName(), wd, target);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }*/
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
/*
    static void findInterfaces(JavaClass clazz, List<String> all) {
        
        if (clazz == null) {
            return;
        }

        String n = clazz.getName();
        int idx = n.indexOf("<");
        if (idx >= 0) {
            n = n.substring(0, idx).trim();
        }

        if (!all.contains(n)) {
            all.add(n);
        }

        findInterfaces(clazz.getSuperClass(), all);

        Iterator it = clazz.getInterfaces().iterator();
        while (it.hasNext()) {
            JavaClass c = (JavaClass)it.next();
            findInterfaces(c, all);
        }
    }*/

    
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
        List<String> allInterfaces = new ArrayList<String>();
        
        public void cancel() {
        }

        public void run(CompilationController cont) throws Exception {
            cont.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
            System.err.println("cont: " + cont);
            System.err.println("cmpu: " + cont.getCompilationUnit());
            for (Tree t : cont.getCompilationUnit().getTypeDecls()) {
                if (t.getKind() == Tree.Kind.CLASS) {
                    ClassTree clazz = (ClassTree)t;
                   // findInterfaces(clazz);
                    System.err.println("extends: " + clazz.getExtendsClause());
                    System.err.println("impl   : " + clazz.getImplementsClause());
                }
            }
            /*
            JavaClass clazz = null;
            List<String> allInterfaces = new ArrayList<String>();

            JavaMetamodel.getDefaultRepository().beginTrans(false);
            try {
                Resource r = JavaModel.getResource(fo.getPrimaryFile());
                if (r != null) {
                    Iterator it = r.getClassifiers().iterator();
                    while (it.hasNext()) {
                        clazz = (JavaClass)it.next();
                        if (!clazz.isInterface() && Modifier.isPublic(clazz.getModifiers())) {
                            break;
                        }
                        clazz = null;
                    }
                }

                findInterfaces(clazz, allInterfaces);
                Collections.sort(allInterfaces);
            } finally {
                JavaMetamodel.getDefaultRepository().endTrans();
            }
*/
        }
    }
}

