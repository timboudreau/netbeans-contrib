/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi.activation;

import java.util.*;

/**
 * Filter for filtering file extensions.
 * @author  jpokorsky
 * @version 
 */
public final class FileExtChooserFilter extends javax.swing.filechooser.FileFilter {
    
    private String description;
    private String[] filters = null;
    
    /** Create new filter.
     * @param description filter description
     * @param filter list of file extensions separated by '|' or null (like *).
     */
    public FileExtChooserFilter(String description,String filter) {
        if (description == null) description = ""; // NOI18N
        this.description = description;
        
        if (filter == null) return ;
        
        StringTokenizer tokenizer = new StringTokenizer(filter, "|"); // NOI18N
        filters = new String[tokenizer.countTokens()];
        for (int i = 0; i < filters.length; i++)
            filters[i] = tokenizer.nextToken().toLowerCase();
    }

    public String getDescription() {
        return description;
    }

    public boolean accept(java.io.File file) {
        if (filters == null || file.isDirectory()) return true;

        String name = file.getName().toLowerCase();
        for (int i = 0; i < filters.length; i++) {
            if (name.endsWith(filters[i])) return true;
        }
        return false;
    }

}
