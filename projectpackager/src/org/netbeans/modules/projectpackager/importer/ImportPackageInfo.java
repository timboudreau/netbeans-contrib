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