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

import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.swing.JFileChooser;
import org.netbeans.modules.projectpackager.tools.DirectoryFilter;
import org.netbeans.modules.projectpackager.tools.ZipFilter;

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
        if (fc.getSelectedFile()!=null) {
//            izd.setProjectName(fc.getSelectedFile().getName().split("\\.zip")[0]);
            String name = "";
            try {
                ZipFile zf = new ZipFile(fc.getSelectedFile());
                Enumeration zipEntries = zf.entries();
                name = ((ZipEntry)zipEntries.nextElement()).getName().split("/")[0];
            } catch (Exception e) {
                // something's wrong so we just won't show the name
            }
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
