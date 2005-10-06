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
import java.util.Vector;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.netbeans.modules.projectpackager.tools.ExecutionTools;
import org.netbeans.modules.projectpackager.tools.ProjectPackagerSettings;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Schedules an export package
 * @author Roman "Roumen" Strobl
 */
public class ExportPackageScheduler {
    
    private static FileObject script;
    private static ArrayList fileList;
    private static ExportExecutorThread et;
    private static Vector unsharableFiles;
    
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
        
        FileObject[] paths = null;
        Boolean[] isExternal = null;
        fileList = new ArrayList();
        
        for (int i = 0; i<ProjectInfo.getProjectCount(); i++) {
            if (!ProjectInfo.isSelected(i)) continue;
            paths = ProjectInfo.getSourceRootPaths(i);
            isExternal = ProjectInfo.getIsExternal(i);
            int external = 0;
            for (int j = 0; j<paths.length; j++) {
                Properties props = new Properties();
                props.setProperty("target_dir", ExportPackageInfo.getTargetDir());
                
                // create extra zips for external source roots
                if (isExternal[j].booleanValue()) {
                    external++;
                    String fileName = ProjectInfo.getName(i)+"_"+java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("external")+external;
                    props.setProperty("zip_name", fileName);
                    fileList.add(ExportPackageInfo.getTargetDir()+File.separator+fileName+".zip");
                } else {
                    props.setProperty("zip_name", ProjectInfo.getName(i));
                    fileList.add(ExportPackageInfo.getTargetDir()+File.separator+ProjectInfo.getName(i)+".zip");
                }
                props.setProperty("src_dir", FileUtil.toFile(paths[j].getParent()).getAbsolutePath());
                props.setProperty("dir_name", paths[j].getName());
                if (unsharableFiles==null) {
                    unsharableFiles = new Vector();
                } else {
                    unsharableFiles.clear();
                }
                
                // set which files should not be included
                traverseDirForSharability(FileUtil.toFile(paths[j].getParent()).getAbsolutePath(),
                        FileUtil.toFile(paths[j]));
                String excludes = "";
                for (int k = 0; k<unsharableFiles.size(); k++) {
                    excludes+=(String)unsharableFiles.get(k);
                    if (k<unsharableFiles.size()-1) excludes+=",";
                }
                props.setProperty("exclude_list", excludes);
                
                // schedule creation of zip
                et.schedule(script, new String[] {"zip-project"}, props);
            }
        }
    }

    /**
     * Creates a list of files for exclude in zip target.
     */
    private static void traverseDirForSharability(String topDir, File f) {        
        if (SharabilityQuery.getSharability(f)==SharabilityQuery.NOT_SHARABLE) {
            String path = f.getAbsolutePath();
            String projectDir = topDir + File.separator;            
            if (f.isDirectory()) {
                // ignore NOT_SHARABLE directory, add "/" to ignore subdirectories as well
                if (path.indexOf(projectDir)>-1) {
                    unsharableFiles.add(f.getAbsolutePath().substring(projectDir.length())+File.separator);
                }
            } else {
                // ignore NOT_SHARABLE file
                unsharableFiles.add(f.getAbsolutePath().substring(projectDir.length()));
            }
        } else if (SharabilityQuery.getSharability(f)==SharabilityQuery.MIXED) {
            // MIXED means directory contains some NOT_SHARABLEs, traverse it
            for (int i=0; i<f.listFiles().length; i++) {
                traverseDirForSharability(topDir, f.listFiles()[i]);
            }
        }
        // files or directories with status SHARABLE or UNKNOWN are accepted
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
        if (ExportPackageInfo.getSmtpUseSSL()) {
            props.setProperty("smtp_use_ssl", "true");
            props.setProperty("smtp_mailport", "465");
        } else {
            props.setProperty("smtp_use_ssl", "false");
            props.setProperty("smtp_mailport", "25");            
        }
        final ProjectPackagerSettings pps = ProjectPackagerSettings.getDefault();        
        props.setProperty("mail_from", pps.getMailFrom());
        props.setProperty("mail_subject", pps.getMailSubject());
        props.setProperty("mail_body", pps.getMailBody());
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
