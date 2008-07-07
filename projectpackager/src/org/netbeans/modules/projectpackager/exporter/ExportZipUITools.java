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
        String smtpServer = ProjectPackagerSettings.getSmtpServer();
        if (smtpServer!=null && !smtpServer.equals("")) {
            ExportPackageInfo.setSmtpServer(smtpServer);
        } else {
            smtpServer = System.getProperty("smtp_server");
            if (smtpServer!=null && !smtpServer.equals("")) ExportPackageInfo.setSmtpServer(smtpServer);
        }
        String smtpUsername = ProjectPackagerSettings.getSmtpUsername();
        if (smtpUsername!=null && !smtpUsername.equals("")) {
            ExportPackageInfo.setSmtpUsername(smtpUsername);
        } else {
            smtpUsername = System.getProperty("smtp_username");
            if (smtpUsername!=null && !smtpUsername.equals("")) ExportPackageInfo.setSmtpUsername(smtpUsername);
        }
        String smtpPassword = ProjectPackagerSettings.getSmtpPassword();
        if (smtpPassword!=null && !smtpPassword.equals("")) {
            ExportPackageInfo.setSmtpPassword(smtpPassword);
        } else {
            smtpPassword = System.getProperty("smtp_password");
            if (smtpPassword!=null && !smtpPassword.equals("")) ExportPackageInfo.setSmtpPassword(smtpPassword);
        }
        boolean smtpUseSSL = ProjectPackagerSettings.getSmtpUseSSL();
        ExportPackageInfo.setSmtpUseSSL(smtpUseSSL);
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
