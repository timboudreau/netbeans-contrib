/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core.util;

import javax.swing.filechooser.FileFilter;

/**
 * File filter that chooses the file depending on their extension.
 */
public class ExtensionFileFilter extends FileFilter {
    private String[] extensions;
    private String description;
    
    /**
     * Creates a new instance of ExtensionFileFilter
     *
     * @param desc description for this filter
     * @param extensions valid extensions (e.g. ".java")
     */
    public ExtensionFileFilter(String desc, String[] extensions) {
        this.description = desc;
        this.extensions = extensions;
    }
    
    public boolean accept(java.io.File f) {
        if (f.isFile()) {
            String name = f.getName();
            for (int i = 0; i < extensions.length; i++) {
                if (name.endsWith(extensions[i]))
                    return true;
            }
            return false;
        } else {
            return true;
        }
    }
    
    public String getDescription() {
        return description;
    }    
}
