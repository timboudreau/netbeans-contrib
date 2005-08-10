/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.projectpackager.exporter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;

/**
 * A thread which executes ant targets and shows result of the actions to user.
 * @author Roman "Roumen" Strobl
 */
public class ExportExecutorThread extends Thread {
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
     * Creates a new instance of ExecutorThread
     */
    public ExportExecutorThread() {
        scripts = new Vector();
        targets = new Vector();
        properties = new Vector();
        status = new HashMap();
        scheduledTasks = new HashMap();
        count = 0;
        scheduledCount = 0;
    }
    
    /**
     * Run method overriding run from Thread
     */
    public void run() {
        if (!isSilent()) {
            handle = ProgressHandleFactory.createHandle(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Processing_zips"));  
            handle.start();            
        }
        while (count<scheduledCount) {
            FileObject script = (FileObject) scripts.get(count);
            String[] target = (String[]) targets.get(count);
            Properties props = (Properties) properties.get(count);
            try {
                ExecutorTask et = ActionUtils.runTarget(script, target, props);
                if (et.result()!=0) {
                    System.err.println(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Task_execution_error_during_")+target[0]+".");
                    status.put(target[0], new Boolean(false));
                } else {
                    if (status.get(target[0])==null) {
                        status.put(target[0], new Boolean(true));
                    }
                }
            } catch (IOException e) {
                System.err.println(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("IO_error:_")+e);
            }
            count++;
        }

        // we're done
        ExportPackageInfo.setProcessed(false);            

        if (!isSilent()) {
            handle.finish();            
        } else {
            return;
        }
        
        String errorMsg = "";
        if (scheduledTasks.get("zip-project")!=null && !((Boolean) status.get("zip-project")).booleanValue()) {
            errorMsg+=java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Could_not_zip_projects._Disk_not_writable?\n");
        }
        if (scheduledTasks.get("mail-zips")!=null && !((Boolean) status.get("mail-zips")).booleanValue()) {
            errorMsg+=java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Could_not_send_e-mail._Check_SMTP_server_settings.\n");
            ExportPackageInfo.setSmtpServer("");
            ExportPackageInfo.setSmtpUsername("");
            ExportPackageInfo.setSmtpPassword("");
        }
        if (scheduledTasks.get("delete-zip")!=null && !((Boolean) status.get("delete-zip")).booleanValue()) {
            errorMsg+=java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Could_not_delete_projects._Disk_not_writable?\n");
        }
        if (!errorMsg.equals("")) {
            NotifyDescriptor d = new NotifyDescriptor.Message(errorMsg, NotifyDescriptor.ERROR_MESSAGE);
            d.setTitle(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Error_during_processing_zips"));
            DialogDisplayer.getDefault().notify(d);            
        } else {
            if (scheduledTasks.get("mail-zips")==null && ((Boolean) scheduledTasks.get("zip-project")).booleanValue()) {
                NotifyDescriptor d = new NotifyDescriptor.Message(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Zip(s)_created_successfully."), NotifyDescriptor.INFORMATION_MESSAGE);
                d.setTitle(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Zip_exporter_finished"));
                DialogDisplayer.getDefault().notify(d);
                return;
            }
            if (scheduledTasks.get("delete-zip")==null && ((Boolean) scheduledTasks.get("mail-zips")).booleanValue()) {
                NotifyDescriptor d = new NotifyDescriptor.Message(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Zip(s)_created_and_sent_successfully."), NotifyDescriptor.INFORMATION_MESSAGE);
                d.setTitle(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Zip_exporter_finished"));
                DialogDisplayer.getDefault().notify(d);
                return;
            }
            if (((Boolean) scheduledTasks.get("delete-zip")).booleanValue()) {
                NotifyDescriptor d = new NotifyDescriptor.Message(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Zip(s)_created,_sent_and_deleted_successfully."), NotifyDescriptor.INFORMATION_MESSAGE);
                d.setTitle(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Zip_exporter_finished"));
                DialogDisplayer.getDefault().notify(d);
            }            
        }
    }   
    
    /**
     * Schedules an ant task
     * @param script ant script
     * @param target targets of ant script
     * @param props properties of ant script
     */
    public void schedule(FileObject script, String[] target, Properties props) {
        scripts.add(script);
        targets.add(target);
        properties.add(props);
        scheduledCount++;
        scheduledTasks.put(target[0], new Boolean(true));
    }

    /**
     * Should executor thread display results?
     * @return true if should display results
     */
    public boolean isSilent() {
        return silent;
    }

    /**
     * Set if executor thread should display results
     * @param silent true if results should be shown
     */
    public void setSilent(boolean silent) {
        this.silent = silent;
    }
    
}