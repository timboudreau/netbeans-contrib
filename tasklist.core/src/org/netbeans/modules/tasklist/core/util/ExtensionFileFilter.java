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
