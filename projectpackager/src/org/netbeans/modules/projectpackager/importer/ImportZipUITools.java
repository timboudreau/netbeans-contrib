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

import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JFileChooser;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.netbeans.modules.projectpackager.tools.DirectoryFilter;
import org.netbeans.modules.projectpackager.tools.ZipFilter;
import org.openide.util.NbBundle;

/**
 * Tools for Import Zip dialog
 * @author Roman "Roumen" Strobl
 */
public class ImportZipUITools {
    private static ImportZipDialog izd;
    private static Vector listData;
    
    private ImportZipUITools() {
    }
    
    /**
     * Sets the reference to the Import zip project dialog
     * @param aizd import zip project dialog
     */
    public static void setZipProjectDialog(ImportZipDialog aizd) {
        izd = aizd;
    }
    
    /**
     * Shows file chooser for zip
     */
    public static void showFileChooser() {
        JFileChooser fc = new JFileChooser();
        String zip = "";

        fc.addChoosableFileFilter(new ZipFilter());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int returnVal = fc.showOpenDialog(izd);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            zip = fc.getSelectedFile().getAbsolutePath();
        }        
        izd.setZip(zip);
        boolean isProject = false;
        if (fc.getSelectedFile()!=null) {
            String name = "";
            try {
                ZipFile zf = new ZipFile(fc.getSelectedFile());
                Enumeration zipEntries = zf.entries();
                name = ((ZipEntry)zipEntries.nextElement()).getName().split("/")[0];
                if (zf.getEntry(name+"/nbproject")!=null) isProject = true;
            } catch (Exception e) {
                // something's wrong so we just won't show the name
            }            
            if (!isProject) {
                // seems to be a source root, change projectName
                izd.setProjectNameLabel(NbBundle.getBundle(Constants.BUNDLE).getString("Source_Root_Folder_Name"));
            }
            ImportPackageInfo.setOriginalName(name);
            izd.setProjectName(name);
        }
    }    
    
    /**
     * Show directory chooser
     */
    public static void showDirectoryChooser() {
        JFileChooser fc = new JFileChooser();
        String zipDir = "";
        
        fc.addChoosableFileFilter(new DirectoryFilter());
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);        
        
        int returnVal = fc.showOpenDialog(izd);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            zipDir = fc.getSelectedFile().getAbsolutePath();
        }
        izd.setUnZipDir(zipDir);
    }
    
    /**
     * Processes Ok button - validates package, schedules tasks and executes them
     */
    public static void processOkButton() {
        ImportPackageInfo.setZip(izd.getZipFile());
        ImportPackageInfo.setProjectName(izd.getProjectName());
        ImportPackageInfo.setUnzipDir(izd.getUnzipDir());
        ImportPackageInfo.setDeleteZip(izd.isDeleteSelected());

        if (!ImportPackageValidator.validate()) {
            izd.requestFocus();
            return;
        }
        
        izd.dispose();
        
        ImportExecutorThread et = ImportPackageScheduler.init();
        ImportPackageScheduler.unZipProject(et);
        if (izd.isDeleteSelected()) {
            ImportPackageScheduler.deleteZip(et);
        }
        et.start();         
    }
    
    /**
     * Processes cancel button - just dispose
     */
    public static void processCancelButton() {
        // we're done
        ImportPackageInfo.setProcessed(false);
        izd.dispose();
    }
}
