/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.launch4jint;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

/** Handles location of Launch4j installation, asks the user and persists
 * received information.  
 *
 * @author Dafe Simonek
 */
public class Launch4jFinder {
    
    private static final String JAR_NAME = "launch4j.jar";
    
    private static final String KEY_DIR = "launch4jDir";
    
    /** A little hack - file object as placeholder to store simple data as its
     * properties */
    private static String CONFIG_PATH = "Services/org-netbeans-modules-launch4jint-Config.data";
    private static final FileObject configFO = Repository.getDefault().getDefaultFileSystem().
                                findResource(CONFIG_PATH);
    
    /** Accepts only files named "launch4j.jar" */
    private static final FilenameFilter LAUNCH4J_JAR = new FilenameFilter () {
        public boolean accept(File dir, String name) {
            return JAR_NAME.equalsIgnoreCase(name);
        }
    };
    
    /** utility class, no need to instantiate */
    private Launch4jFinder () {
    }
    
    /** Retrieves and returns installation directory of Launch4j product.
     *
     * @return Launch4j install dir or null if Launch4j couldn't be located.
     */
    public static String getLaunch4jDir () {
        // check persisted data first
        String dir = getPersistedDir();
        if (dir != null && checkDir(dir)) {
            return dir;
        }
        
        // ask user to install/locate Launch4j
        String result = LocatePanel.obtainLaunch4jDir();
        if (result != null && checkDir(result)) {
            setPersistedDir(result);
            return result;
        }
        
        return null;
    }
    
    private static String getPersistedDir() {
        return (String) configFO.getAttribute(KEY_DIR);
    }
    
    private static void setPersistedDir(String dir) {
        try {
            configFO.setAttribute(KEY_DIR, dir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    static boolean checkDir(String dir) {
        File file = new File(dir);
        File[] list = file.listFiles(LAUNCH4J_JAR);
        return list != null && list.length == 1;
    }
    
    
}
