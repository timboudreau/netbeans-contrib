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

package org.netbeans.modules.projectpackager.exporter;
import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.netbeans.modules.projectpackager.tools.ExecutionTools;
import org.netbeans.modules.projectpackager.tools.ProjectPackagerSettings;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * Schedules an export package
 * @author Roman "Roumen" Strobl
 */
public class ExportPackageScheduler {
    
    private static FileObject script;
    private static ArrayList fileList;
    private static ExportExecutorThread et;
    private static Vector unsharableFiles;
    private static String timeStamp;
    
    private static boolean initialized = false;
    
    private ExportPackageScheduler() {
    }
    
    /**
     * Initializes scheduler by creating an executor thread
     * @return executor thread
     */
    public static ExportExecutorThread init() {
        try {
            // XXX this string should not be internationalized!
            script = ExecutionTools.initScript(NbBundle.getBundle(Constants.BUNDLE).getString("Services/ProjectPackager/export_script.xml"));
            et = new ExportExecutorThread();
            initialized = true;
        } catch (IOException e) {
            // XXX should use ErrorManager, or at least e.dumpStack(); and strings printed to console should never be internationalized
            System.err.println(NbBundle.getBundle(Constants.BUNDLE).getString("IO_error:_")+e);
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
        Format formatter = new SimpleDateFormat("yyMMdd-HHmmss");
        timeStamp = formatter.format(new Date());
                
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
                    String fileName = ProjectInfo.getName(i)+"_"+NbBundle.getBundle(Constants.BUNDLE).getString("external")+external+"_"+timeStamp;
                    props.setProperty("zip_name", fileName);
                    fileList.add(ExportPackageInfo.getTargetDir()+File.separator+fileName+".zip");
                } else {
                    String fileName = ProjectInfo.getName(i)+"_"+timeStamp;
                    props.setProperty("zip_name", fileName);
                    fileList.add(ExportPackageInfo.getTargetDir()+File.separator+fileName+".zip");
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
                } else {
                    assert false : "Incorrect project path: "+projectDir;
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
        props.setProperty("target_dir", ExportPackageInfo.getTargetDir());
        props.setProperty("file_list", "*_"+timeStamp+".zip");
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
