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

package org.netbeans.modules.projectpackager.importer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.netbeans.modules.projectpackager.tools.ExecutionTools;
import org.openide.filesystems.FileObject;

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
            System.err.println(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("IO_error:_")+e);
        }                
        return et;
    }
    
    /**
     * Schedule unzipping of project
     * @param et executor thread
     */
    public static void unZipProject(ImportExecutorThread et) {
        if (!initialized) return;
                
        Properties props = new Properties();
        props.setProperty("zip_file", ImportPackageInfo.getZip());
        props.setProperty("unzip_dir", ImportPackageInfo.getUnzipDir());
        props.setProperty("project_name", ImportPackageInfo.getProjectName());
        et.schedule(script, new String[] {"unzip-project"}, props);
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
