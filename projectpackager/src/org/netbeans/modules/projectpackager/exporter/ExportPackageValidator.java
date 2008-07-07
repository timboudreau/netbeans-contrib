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

import java.io.File;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.netbeans.modules.projectpackager.tools.NotifyDescriptorInputPassword;
import org.netbeans.modules.projectpackager.tools.ProjectPackagerSettings;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Validator of export package
 * @author Roman "Roumen" Strobl
 */
public class ExportPackageValidator {

    /** Creates a new instance of PackageValidator */
    private ExportPackageValidator() {
    }
    
    /**
     * Validates the whole package which will be exported
     * @return true if all ok
     */
    public static boolean validate() {
        boolean selected = false;
        for (int i = 0; i<ProjectInfo.getProjectCount(); i++) {
            if (ProjectInfo.isSelected(i)) selected = true;
        }
        if (!selected) {
            NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("No_projects_were_chosen._Please_choose_at_least_one_project_in_the_dialog."), NotifyDescriptor.ERROR_MESSAGE);
            d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error:_no_project_selected"));
            DialogDisplayer.getDefault().notify(d);
            return false;
        }
        
        if (ExportPackageInfo.getTargetDir().equals("")) {
            NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("Please_specify_a_directory_where_to_store_zip_files."), NotifyDescriptor.ERROR_MESSAGE);
            d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error:_target_directory_not_specified"));
            DialogDisplayer.getDefault().notify(d);
            return false;            
        }
        
        File target = new File(ExportPackageInfo.getTargetDir());
        if (!target.canWrite()) {
            NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("Target_directory_is_not_writable._Please_choose_a_different_directory."), NotifyDescriptor.ERROR_MESSAGE);
            d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error:_target_directory_not_writable"));
            DialogDisplayer.getDefault().notify(d);
            return false;            
        }
        
        if (ExportPackageInfo.isSendMail() && ExportPackageInfo.getEmail().equals("")) {
            NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("No_e-mail_address_specified._Please_fill_in_your_e-mail_address_in_the_dialog."), NotifyDescriptor.ERROR_MESSAGE);
            d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error:_no_e-mail_address"));
            DialogDisplayer.getDefault().notify(d);
            return false;                        
        }

        if (ExportPackageInfo.isSendMail()) {
            if (ExportPackageInfo.getSmtpServer().equals("")) {
                NotifyDescriptor.InputLine d = new NotifyDescriptor.InputLine(NbBundle.getBundle(Constants.BUNDLE).getString("SMTP_server:"), NbBundle.getBundle(Constants.BUNDLE).getString("Please_specify_a_SMTP_server"));
                DialogDisplayer.getDefault().notify(d);
                if (d.getInputText().equals("")) {
                    return false;
                } else {
                    ProjectPackagerSettings.setSmtpServer(d.getInputText());
                }
                ExportPackageInfo.setSmtpServer(d.getInputText());
                NotifyDescriptor.InputLine d2 = new NotifyDescriptor.InputLine(NbBundle.getBundle(Constants.BUNDLE).getString("Username_(optional):"), NbBundle.getBundle(Constants.BUNDLE).getString("Please_enter_username_for_SMTP_server"));
                DialogDisplayer.getDefault().notify(d2);
                ExportPackageInfo.setSmtpUsername(d2.getInputText());
                ProjectPackagerSettings.setSmtpUsername(d2.getInputText());
                NotifyDescriptorInputPassword d3 = new NotifyDescriptorInputPassword(NbBundle.getBundle(Constants.BUNDLE).getString("Password_(optional):"), NbBundle.getBundle(Constants.BUNDLE).getString("Please_enter_password_for_SMTP_server"));
                DialogDisplayer.getDefault().notify(d3);
                ExportPackageInfo.setSmtpPassword(d3.getInputText());
                // do not save password to settings from security reasons
            }        

            if (ExportPackageInfo.getSmtpUsername().equals("") && ExportPackageInfo.getSmtpPassword().equals("")) {
                NotifyDescriptor.InputLine d2 = new NotifyDescriptor.InputLine(NbBundle.getBundle(Constants.BUNDLE).getString("Username_(optional):"), NbBundle.getBundle(Constants.BUNDLE).getString("Please_enter_username_for_SMTP_server"));
                DialogDisplayer.getDefault().notify(d2);
                ExportPackageInfo.setSmtpUsername(d2.getInputText());
                ProjectPackagerSettings.setSmtpUsername(d2.getInputText());
                NotifyDescriptorInputPassword d = new NotifyDescriptorInputPassword(NbBundle.getBundle(Constants.BUNDLE).getString("SMTP_password:"), NbBundle.getBundle(Constants.BUNDLE).getString("Please_enter_password_for_SMTP_server"));
                DialogDisplayer.getDefault().notify(d);
                ExportPackageInfo.setSmtpPassword(d.getInputText());
                // do not save password to settings from security reasons
            } else if (!ExportPackageInfo.getSmtpUsername().equals("") && ExportPackageInfo.getSmtpPassword().equals("")) {
                NotifyDescriptorInputPassword d = new NotifyDescriptorInputPassword(NbBundle.getBundle(Constants.BUNDLE).getString("SMTP_password:"), NbBundle.getBundle(Constants.BUNDLE).getString("Please_enter_password_for_SMTP_server"));
                DialogDisplayer.getDefault().notify(d);
                ExportPackageInfo.setSmtpPassword(d.getInputText());
            }
        }

        return true;
    }
    
}
