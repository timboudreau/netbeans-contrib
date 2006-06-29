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
