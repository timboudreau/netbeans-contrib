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

package org.netbeans.modules.projectpackager.exporter;

import java.util.Vector;
import javax.swing.JFileChooser;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.netbeans.modules.projectpackager.tools.DirectoryFilter;
import org.netbeans.modules.projectpackager.tools.ProjectPackagerSettings;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Tools for Export zip dialog
 * @author Roman "Roumen" Strobl
 */
public class ExportZipUITools {
    private static ExportZipDialog zpd;    
    private static String targetDir;
    private static Vector listData;
    private static EmailSettingsDialog esd;
    
    private ExportZipUITools() {
    }
    
    /**
     * Set reference to zip project dialog
     * @param azpd zip project dialog
     */
    public static void setZipProjectDialog(ExportZipDialog azpd) {
        zpd = azpd;
    }
    
    /**
     * Shows chooser for directory
     */
    public static void showFileChooser() {
        JFileChooser fc = new JFileChooser();
        String aTargetDir = "";
        
        fc.addChoosableFileFilter(new DirectoryFilter());
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);        
        
        int returnVal = fc.showOpenDialog(zpd);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            aTargetDir = fc.getSelectedFile().getAbsolutePath();
        }
        targetDir = aTargetDir;
        zpd.setTargetDir(aTargetDir);
    }
    
    /**
     * Processes Ok button - verifies pack, schedules tasks and executes them
     */
    public static void processOkButton() {
        for (int i = 0; i<ProjectInfo.getProjectCount(); i++) {
            ProjectInfo.setSelected(i, false);
        }
        int[] selected = zpd.getSelectedIndices();
        for (int i = 0; i<selected.length; i++) {
            ProjectInfo.setSelected(selected[i], true);
        }
        ExportPackageInfo.setTargetDir(zpd.getTargetDir());
        ExportPackageInfo.setSendMail(zpd.isMailSelected());
        ExportPackageInfo.setEmail(zpd.getMail());
        ExportPackageInfo.setDeleteZip(zpd.isDeleteSelected());
        ProjectPackagerSettings pps = ProjectPackagerSettings.getDefault();
        String smtpServer = pps.getSmtpServer();
        if (smtpServer!=null && !smtpServer.equals("")) {
            ExportPackageInfo.setSmtpServer(smtpServer);
        } else {
            smtpServer = System.getProperty("smtp_server");
            if (smtpServer!=null && !smtpServer.equals("")) ExportPackageInfo.setSmtpServer(smtpServer);
        }
        String smtpUsername = pps.getSmtpUsername();
        if (smtpUsername!=null && !smtpUsername.equals("")) {
            ExportPackageInfo.setSmtpUsername(smtpUsername);
        } else {
            smtpUsername = System.getProperty("smtp_username");
            if (smtpUsername!=null && !smtpUsername.equals("")) ExportPackageInfo.setSmtpUsername(smtpUsername);
        }
        String smtpPassword = pps.getSmtpPassword();
        if (smtpPassword!=null && !smtpPassword.equals("")) {
            ExportPackageInfo.setSmtpPassword(smtpPassword);
        } else {
            smtpPassword = System.getProperty("smtp_password");
            if (smtpPassword!=null && !smtpPassword.equals("")) ExportPackageInfo.setSmtpPassword(smtpPassword);
        }
        Boolean smtpUseSSL = pps.getSmtpUseSSL();
        if (smtpUseSSL!=null) {
            ExportPackageInfo.setSmtpUseSSL(smtpUseSSL.booleanValue());
        } else {
            smtpUseSSL = Boolean.valueOf(System.getProperty("smtp_use_ssl"));
            if (smtpUseSSL!=null && !smtpUseSSL.equals("")) ExportPackageInfo.setSmtpUseSSL(smtpUseSSL.booleanValue());
        }        
        if (!ExportPackageValidator.validate()) {
            zpd.requestFocus();
            return;
        }
        
        zpd.dispose();
        
        ExportExecutorThread et = ExportPackageScheduler.init();
        ExportPackageScheduler.packZips(et);
        if (ExportPackageInfo.isSendMail()) {
            ExportPackageScheduler.sendMail(et);
        }
        if (ExportPackageInfo.isSendMail() && ExportPackageInfo.isDeleteZip()) {
            ExportPackageScheduler.deleteZips(et);
        }
        et.start();
    }
    
    /**
     * Processes cancel button - just dispose
     */
    public static void processCancelButton() {
        // we're done
        ExportPackageInfo.setProcessed(false);
        zpd.dispose();
    }
    
    /**
     * Shows the E-mail settings dialog
     */
    public static void showEmailSettings() {
        if (esd!=null && esd.isShowing()) {
            esd.requestFocus();            
        } else {
            esd = new EmailSettingsDialog();        
            esd.setVisible(true);
        }
    }
            
    
    /**
     * Returns names of project for the JList
     * @return project names
     */
    public static Vector getListData() {        
        if (listData==null) {
            listData = new Vector();
        } else {
            listData.clear();
        }        
        ProjectInfo.initProjects();
        ProjectTools.readProjectInfo();
        if (ProjectInfo.getProjectCount()==0) {
            NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("No_projects_are_currently_opened._Please_open_at_least_one_project_and_re-run_exporter."), NotifyDescriptor.ERROR_MESSAGE);
            d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error:_no_project_opened"));
            DialogDisplayer.getDefault().notify(d);
            return null;
        }
        for (int i = 0; i<ProjectInfo.getProjectCount(); i++) {
            listData.addElement(ProjectInfo.getName(i));
        }
        return listData;
    }    
    
    
}
