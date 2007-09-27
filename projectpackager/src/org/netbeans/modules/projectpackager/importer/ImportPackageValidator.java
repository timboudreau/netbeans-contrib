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
import org.netbeans.modules.projectpackager.tools.Constants;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Validator of import package
 * @author Roman "Roumen" Strobl
 */
public class ImportPackageValidator {

    /** Creates a new instance of PackageValidator */
    private ImportPackageValidator() {
    }

    /**
     * Validates the import package
     * @return true if ok
     */
    public static boolean validate() {
        if (ImportPackageInfo.getZip().equals("")) {
            NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("Please_choose_a_zip_file_with_project_to_import."), NotifyDescriptor.ERROR_MESSAGE);
            d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error:_no_zip_selected"));
            DialogDisplayer.getDefault().notify(d);
            return false;
        }

        if (ImportPackageInfo.getUnzipDir().equals("")) {
            NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("Please_specify_a_directory_where_to_unzip_project."), NotifyDescriptor.ERROR_MESSAGE);
            d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error:_project_directory_not_specified"));
            DialogDisplayer.getDefault().notify(d);
            return false;            
        }

        File projectDir = new File(ImportPackageInfo.getUnzipDir()+File.separator+ImportPackageInfo.getProjectName());
        if (projectDir.exists()) {
            NotifyDescriptor.InputLine d = new NotifyDescriptor.InputLine(NbBundle.getBundle(Constants.BUNDLE).getString("Project_directory_already_exists._Please_choose_a_different_directory."),
                    NbBundle.getBundle(Constants.BUNDLE).getString("Error:_project_directory_exists"));
            d.setInputText(ImportPackageInfo.getOriginalName());
            DialogDisplayer.getDefault().notify(d);
            // new project folder name
            if (!d.getInputText().equals("")) {
                ImportPackageInfo.setProjectName(d.getInputText());
                // revalidate
                if (validate()) return true;
            }            
            return false;            
        }
        
        File target = new File(ImportPackageInfo.getUnzipDir());
        if (!target.canWrite()) {
            NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("Project_directory_is_not_writable._Please_choose_a_different_directory."), NotifyDescriptor.ERROR_MESSAGE);
            d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error:_project_directory_not_writable"));
            DialogDisplayer.getDefault().notify(d);
            return false;            
        }

        if (ImportPackageInfo.getProjectName().equals("")) {
            NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("Please_specify_a_non-empty_project_name."), NotifyDescriptor.ERROR_MESSAGE);
            d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error:_empty_project_name"));
            DialogDisplayer.getDefault().notify(d);
            return false;
        }
                
        return true;
    }
    
}
