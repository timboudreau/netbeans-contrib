
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package com.sun.tthub.gde.util;

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gde.logic.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class contains the functions  to load files/folders/clasess from the
 * GDE folder. All the operations that queries or alters the GDE folder is 
 * handled by this class.
 *
 * @author Hareesh Ravindran
 */
public class GDEFolderManager {
    
    /** Creates a new instance of GDEFolderManager */
    public GDEFolderManager() {}
    
    /**
     * Retrieves the GDE folder name from the AppContext. This function can
     * be used by all classes as a convineance method to get the GDE folder.
     *
     * @throws com.sun.tthub.gde.GDEException if there is an error in retrieving
     *      the gde folder from the app context.
     *
     * @return the full path of the GDE folder.
     */
    public String getGdeFolderName() throws GDEException {
        GDEPreferencesController controller = 
                GDEAppContext.getInstance().getGdePrefsController();
        return controller.retrievePreferences().getGdeFolder();        
    }
    
    /**
     * This private function is used to check if the GDE folder is valid.
     * If so, it returns the file object that represents the GDE folder.
     *
     * @throws com.sun.tthub.gde.GDEException if there is any issue while 
     *      retrieving the GDE folder from the app context, or if the GDE
     *      folder retrieved from the app context does not exist or is not
     *      readable.
     *
     * @return the java.io.File object representing the GDE folder.
     */
    private File openFolder(String folderName) throws GDEException {
        File file = new File(folderName);
        if (!file.exists() || !file.isDirectory() || !file.canRead()) {
            throw new GDEFileMngmtException("The GDEFolder directory" + "'" +
                    folderName + "' does not exist or is not readable.");            
        }
        return file;
    }
    
    /**
     * Retrieves all the jar files from the GDE folder and returns the 
     * collection. If there are no jar files in the GDE folder, the method
     * returns an empty collection. The method uses the FileUtil utility class
     * to extract the extension of the file.
     *
     * @throws com.sun.tthub.gde.GDEException if there is any issue with opening
     *      the GDE folder.
     *
     * @return the Collection containing the java.io.File objects representing
     *      the jar files in the GDE folder.
     */
    public Collection loadJarFilesFromGdeFolder() throws GDEException {
        String folderName = getGdeFolderName() + "/lib";
        File file = openFolder(folderName);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                // get the extension of the file and check if it is a '.jar'
                // file. If so, return true otherwise return false
                String extStr = FileUtilities.getExtension(name);
                return (extStr != null && extStr.equals("jar"));
            }
        };        
        File[] jarFiles = file.listFiles(filter);  // list all the jar files 
                                                   // in the GDE Folder
        return Arrays.asList(jarFiles);
    }        
}

