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

package org.netbeans.modules.projectpackager.tools;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.openide.util.NbBundle;

/**
 * Filter to show only directories and zips, allow only directories
 * @author Roman "Roumen" Strobl
 */
public class DirectoryFilter extends FileFilter {

    /**
     * Accept filter
     * @param f file
     * @return true if accepted
     */
    public boolean accept(File f) {
        if (f.isDirectory() || f.getName().endsWith(".zip")) {
            return true;
        } else {
            return false;
        }
    }

    //The description of this filter
    /**
     * Return description shown in dialog
     * @return description
     */
    public String getDescription() {
       return NbBundle.getBundle(Constants.BUNDLE).getString("Directories");
    }
}
