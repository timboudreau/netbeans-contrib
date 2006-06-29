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