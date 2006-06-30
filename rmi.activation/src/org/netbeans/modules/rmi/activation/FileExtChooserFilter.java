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
