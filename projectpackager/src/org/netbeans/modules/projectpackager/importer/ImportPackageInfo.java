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

/**
 * Data storage for information about import
 * @author Roman "Roumen" Strobl
 */
public class ImportPackageInfo {
    
    // following properties remain static so that they are remembered during session
    private static String zip = "";
    private static String unzipDir = "";
    private static String projectName = "";
    private static boolean deleteZip = false;
    private static String originalName = "";
    private static boolean processed = false;      
    
    /** Creates a new instance of PackageInfo */
    private ImportPackageInfo() {        
    }

    /**
     * Is another import being processed?
     * @return true if processed
     */
    public static boolean isProcessed() {
        return processed;
    }

    /**
     * Set processing
     * @param aProcessed true for start, end for finish
     */
    public static void setProcessed(boolean aProcessed) {
        processed = aProcessed;
    }

    /**
     * Return path to zip
     * @return zip
     */
    public static String getZip() {
        return zip;
    }

    /**
     * Set path to zip
     * @param aZip path to zip
     */
    public static void setZip(String aZip) {
        zip = aZip;
    }

    /**
     * Return directory where to unzip
     * @return directory
     */
    public static String getUnzipDir() {
        return unzipDir;
    }

    /**
     * Set directory where to unzip
     * @param aUnzipDir directory
     */
    public static void setUnzipDir(String aUnzipDir) {
        unzipDir = aUnzipDir;
    }

    /**
     * Return project name
     * @return project name
     */
    public static String getProjectName() {
        return projectName;
    }

    /**
     * Set project name
     * @param aProjectName project name
     */
    public static void setProjectName(String aProjectName) {
        projectName = aProjectName;
    }

    /**
     * Is delete zip checked?
     * @return true if checked
     */
    public static boolean isDeleteZip() {
        return deleteZip;
    }

    /**
     * Set delete zip
     * @param aDeleteZip true if checked
     */
    public static void setDeleteZip(boolean aDeleteZip) {
        deleteZip = aDeleteZip;
    }

    /**
     * Get original name
     * @return original name
     */
    public static String getOriginalName() {
        return originalName;
    }

    /**
     * Set the original folder name for comparison
     * @param aOriginalName original name
     */
    public static void setOriginalName(String aOriginalName) {
        originalName = aOriginalName;
    }

}