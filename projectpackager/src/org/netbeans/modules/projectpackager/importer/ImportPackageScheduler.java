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
