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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.netbeans.modules.projectpackager.tools.ExecutionTools;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * Scheduler of import tasks
 * @author Roman "Roumen" Strobl
 */
public class ImportPackageScheduler {

    private static FileObject script;
    private static ArrayList fileList;
    private static ImportExecutorThread et;

    private static boolean initialized = false;
    
    private ImportPackageScheduler() {
    }
    
    /**
     * Initialize scheduler by creating an executor thread
     * @return executor thread
     */
    public static ImportExecutorThread init() {
        try {
            script = ExecutionTools.initScript("Services/ProjectPackager/import_script.xml");
            et = new ImportExecutorThread();
            initialized = true;            
        } catch (IOException e) {
            System.err.println(NbBundle.getBundle(Constants.BUNDLE).getString("IO_error:_")+e);
        }                
        return et;
    }
    
    /**
     * Schedule unzipping of project
     * @param et executor thread
     */
    public static void unZipProject(ImportExecutorThread et) {
        if (!initialized) return;

        if (ImportPackageInfo.getOriginalName().equals(ImportPackageInfo.getProjectName())) {
            Properties props = new Properties();
            props.setProperty("zip_file", ImportPackageInfo.getZip());
            props.setProperty("unzip_dir", ImportPackageInfo.getUnzipDir());
            et.schedule(script, new String[] {"unzip-project"}, props);
        } else {
            Properties props = new Properties();
            props.setProperty("zip_file", ImportPackageInfo.getZip());
            props.setProperty("unzip_dir", ImportPackageInfo.getUnzipDir());
            props.setProperty("orig_project_name", ImportPackageInfo.getOriginalName());
            props.setProperty("project_name", ImportPackageInfo.getProjectName());
            et.schedule(script, new String[] {"unzip-renamed-project"}, props);
        }
    }
    
    /**
     * Schedule deleting of zip
     * @param et executor thread
     */
    public static void deleteZip(ImportExecutorThread et) {
        if (!initialized) return;
        Properties props = new Properties();
        props.setProperty("file_to_delete", ImportPackageInfo.getZip());
        et.schedule(script, new String[] {"delete-zip"}, props);        
    }
}
