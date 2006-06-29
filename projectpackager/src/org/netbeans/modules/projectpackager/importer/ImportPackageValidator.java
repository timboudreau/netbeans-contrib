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
