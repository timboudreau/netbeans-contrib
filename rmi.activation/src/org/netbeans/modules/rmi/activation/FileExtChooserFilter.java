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
