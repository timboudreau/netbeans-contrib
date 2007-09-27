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

package org.netbeans.modules.projectpackager.importer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import javax.swing.SwingUtilities;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * A thread which executes ant targets and shows result of the actions to user.
 * @author Roman "Roumen" Strobl
 */
public class ImportExecutorThread extends Thread {
    private Vector scripts;
    private Vector targets;
    private Vector properties;
    private int count;
    private int scheduledCount;
    private boolean silent = false;
    
    private HashMap status;
    private HashMap scheduledTasks;
    
    private ProgressHandle handle;
    
    /**
     * Creates a new instance of ImportExecutorThread
     */
    public ImportExecutorThread() {
        scripts = new Vector();
        targets = new Vector();
        properties = new Vector();
        status = new HashMap();
        scheduledTasks = new HashMap();
        count = 0;
        scheduledCount = 0;
    }
    
    /**
     * Run method, overrides run() from Thread
     */
    public void run() {
        if (!isSilent()) {
            handle = ProgressHandleFactory.createHandle(NbBundle.getBundle(Constants.BUNDLE).getString("Processing_zips"));
            handle.start();
        }
        while (count<scheduledCount) {
            FileObject script = (FileObject) scripts.get(count);
            String[] target = (String[]) targets.get(count);
            Properties props = (Properties) properties.get(count);
            try {
                ExecutorTask et = ActionUtils.runTarget(script, target, props);
                if (et.result()!=0) {
                    System.err.println(NbBundle.getBundle(Constants.BUNDLE).getString("Task_execution_error_during_")+target[0]+".");
                    status.put(target[0], new Boolean(false));
                } else {
                    if (status.get(target[0])==null) {
                        status.put(target[0], new Boolean(true));
                    }
                }
            } catch (IOException e) {
                System.err.println(NbBundle.getBundle(Constants.BUNDLE).getString("IO_error:_")+e);
            }
            count++;
        }
        
        // we're done
        ImportPackageInfo.setProcessed(false);
        
        // open imported project, has to be done in AWT thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                File projectDir = new File(ImportPackageInfo.getUnzipDir() + File.separator + ImportPackageInfo.getProjectName());
                try {
                    Project project = (Project) ProjectManager.getDefault().findProject(FileUtil.toFileObject(FileUtil.normalizeFile(projectDir)));
                    
                    // project is null for external source roots
                    if (project!=null) {
                        OpenProjects.getDefault().open(new Project[] { project },false);
                    }
                } catch (IOException e) {
                    System.err.println(NbBundle.getBundle(Constants.BUNDLE).getString("IO_error:_")+e);
                }
            }
        });
        
        if (!isSilent()) {
            handle.finish();
        } else {
            return;
        }
        
        String errorMsg = "";
        if (scheduledTasks.get("unzip-project")!=null && !((Boolean) status.get("unzip-project")).booleanValue()) {
            errorMsg+=NbBundle.getBundle(Constants.BUNDLE).getString("Could_not_unzip_project._Disk_error?");
        }
        if (scheduledTasks.get("delete-zip")!=null && !((Boolean) status.get("delete-zip")).booleanValue()) {
            errorMsg+=NbBundle.getBundle(Constants.BUNDLE).getString("Could_not_delete_zip.__Disk_not_writable?");
        }
        if (!errorMsg.equals("")) {
            NotifyDescriptor d = new NotifyDescriptor.Message(errorMsg, NotifyDescriptor.ERROR_MESSAGE);
            d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error_during_processing_zips"));
            DialogDisplayer.getDefault().notify(d);
        } else {
            if (scheduledTasks.get("delete-zip")==null && scheduledTasks.get("unzip-project")!=null && 
                    ((Boolean) scheduledTasks.get("unzip-project")).booleanValue()) {
                NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("Project_imported_successfully."), NotifyDescriptor.INFORMATION_MESSAGE);
                d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Zip_importer_finished"));
                DialogDisplayer.getDefault().notify(d);
                return;
            }
            if (scheduledTasks.get("delete-zip")==null && scheduledTasks.get("unzip-renamed-project")!=null && 
                    ((Boolean) scheduledTasks.get("unzip-renamed-project")).booleanValue()) {
                NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("Project_imported_successfully."), NotifyDescriptor.INFORMATION_MESSAGE);
                d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Zip_importer_finished"));
                DialogDisplayer.getDefault().notify(d);
                return;
            }
            if (((Boolean) scheduledTasks.get("delete-zip")).booleanValue()) {
                NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("Project_imported_successfully,_original_zip_deleted."), NotifyDescriptor.INFORMATION_MESSAGE);
                d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Zip_importer_finished"));
                DialogDisplayer.getDefault().notify(d);
            }
        }
    }
    
    /**
     * Schedules a task for import
     * @param script ant script
     * @param target targets for ant script
     * @param props ant script properties
     */
    public void schedule(FileObject script, String[] target, Properties props) {
        scripts.add(script);
        targets.add(target);
        properties.add(props);
        scheduledCount++;
        scheduledTasks.put(target[0], new Boolean(true));
    }
    
    /**
     * Show messages to user?
     * @return true if yes
     */
    public boolean isSilent() {
        return silent;
    }
    
    /**
     * Set showing messages to user
     * @param silent true if yes
     */
    public void setSilent(boolean silent) {
        this.silent = silent;
    }
    
    
}
