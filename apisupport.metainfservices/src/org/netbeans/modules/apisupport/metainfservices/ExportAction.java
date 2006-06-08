/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.apisupport.metainfservices;

import java.awt.Dialog;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
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
        DataObject obj = activatedNodes[0].getLookup().lookup(DataObject.class);
        if (obj == null) {
            return;
        }

        JavaClass clazz = null;
        List<String> allInterfaces = new ArrayList<String>();

        JavaMetamodel.getDefaultRepository().beginTrans(false);
        try {
            Resource r = JavaModel.getResource(obj.getPrimaryFile());
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

        FileObject target = null;
        Project p = FileOwnerQuery.getOwner(obj.getPrimaryFile());

        if (p != null) {
            Sources s = ProjectUtils.getSources(p);
            SourceGroup[] arr = s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            if (arr != null && arr.length > 0) {
                target = arr[0].getRootFolder();
            }
        }


        if (clazz == null || target == null) {
            NotifyDescriptor d = new NotifyDescriptor.Message(
                NbBundle.getMessage(ExportAction.class, "MSG_CannotFindClass", obj.getPrimaryFile().getPath()),
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
    
}

