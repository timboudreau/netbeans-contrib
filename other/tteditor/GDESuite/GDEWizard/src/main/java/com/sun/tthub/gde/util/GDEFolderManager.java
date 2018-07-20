
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
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

