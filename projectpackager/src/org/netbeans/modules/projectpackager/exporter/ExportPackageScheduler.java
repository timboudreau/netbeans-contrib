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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.netbeans.modules.projectpackager.tools.ExecutionTools;
import org.openide.filesystems.FileObject;

/**
 * Schedules an export package
 * @author Roman "Roumen" Strobl
 */
public class ExportPackageScheduler {
    
    private static FileObject script;
    private static ArrayList fileList;
    private static ExportExecutorThread et;

    private static boolean initialized = false;
    
    private ExportPackageScheduler() {
    }
    
    /**
     * Initializes scheduler by creating an executor thread
     * @return executor thread
     */
    public static ExportExecutorThread init() {
        try {
            script = ExecutionTools.initScript(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Services/ProjectPackager/export_script.xml"));
            et = new ExportExecutorThread();
            initialized = true;            
        } catch (IOException e) {
            System.err.println(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("IO_error:_")+e);
        }                
        return et;
    }
    
    /**
     * Schedules packing of zips
     * @param et executor thread
     */
    public static void packZips(ExportExecutorThread et) {
        if (!initialized) return;
        
        String[] paths;
        fileList = new ArrayList();        
        
        for (int i = 0; i<ProjectInfo.getProjectCount(); i++) {
            if (!ProjectInfo.isSelected(i)) continue;
            Properties props = new Properties();
            props.setProperty("target_dir", ExportPackageInfo.getTargetDir());
            props.setProperty("zip_name", ProjectInfo.getName(i));
            fileList.add(ExportPackageInfo.getTargetDir()+File.separator+ProjectInfo.getName(i)+".zip");
            paths = ProjectInfo.getSourceRootPaths(i);
            for (int j = 0; j<paths.length; j++) {
                props.setProperty("src_dir", paths[j]);
                et.schedule(script, new String[] {"zip-project"}, props);
            }
        }
    }
    
    /**
     * Schedules sending of e-mail
     * @param et executor thread
     */
    public static void sendMail(ExportExecutorThread et) {
        if (!initialized || fileList==null) return;
        
        Properties props = new Properties();
        props.setProperty("to_addr", ExportPackageInfo.getEmail());
        String fileListText = "";
        for (int i = 0; i<fileList.size(); i++) {
            fileListText+=fileList.get(i);
            if (i<fileList.size()-1) {
                fileListText+=",";
            }
        }
        props.setProperty("file_list", fileListText);
        props.setProperty("smtp_server", ExportPackageInfo.getSmtpServer());
        props.setProperty("smtp_username", ExportPackageInfo.getSmtpUsername());
        props.setProperty("smtp_password", ExportPackageInfo.getSmtpPassword());
        et.schedule(script, new String[] {"mail-zips"}, props);
    }
    
    /**
     * Schedules deleting of zips
     * @param et executor thread
     */
    public static void deleteZips(ExportExecutorThread et) {
        if (!initialized || fileList==null) return;
        
        for (int i = 0; i<fileList.size(); i++) {
            Properties props = new Properties();
            props.setProperty("file_to_delete", (String) fileList.get(i));
            et.schedule(script, new String[] {"delete-zip"}, props);
        }        
    }
    
}
