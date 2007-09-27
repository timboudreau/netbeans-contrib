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

package org.netbeans.modules.projectpackager.exporter;

import java.util.Vector;
import org.openide.filesystems.FileObject;

/**
 * Serves as storage of data related to projects
 * @author Roman "Roumen" Strobl
 */
public class ProjectInfo {

    private static Vector names;
    private static Vector sourceRootPaths;
    private static Vector isExternal;
    private static Vector selected;

    /** Creates a new instance of ProjectInfo */
    private ProjectInfo() {
    }

    /**
     * Return project name
     * @param index index of project
     * @return project name
     */
    public static String getName(int index) {
        return (String) names.get(index);
    }

    /**
     * Set project name
     * @param index index of project
     * @param name project name
     */
    public static void setName(int index, String name) {
        names.add(index, name);
    }

    /**
     * Returns source root paths for a project
     * @param index index of project
     * @return source roots
     */
    public static FileObject[] getSourceRootPaths(int index) {
        return (FileObject[]) sourceRootPaths.get(index);
    }

    /**
     * Set source roots of a project
     * @param index index of project
     * @param aSourceRootPaths source roots
     */
    public static void setSourceRootPaths(int index, FileObject[] aSourceRootPaths) {
        sourceRootPaths.add(index, aSourceRootPaths);
    }    

    /**
     * Is a project selected?
     * @param index index of project
     * @return true if selected
     */
    public static boolean isSelected(int index) {
        return ((Boolean) selected.get(index)).booleanValue();
    }

    /**
     * Set a project to be selected
     * @param index index of project
     * @param aSelected true if selected
     */
    public static void setSelected(int index, boolean aSelected) {
        selected.add(index, Boolean.valueOf(aSelected));
    }
    
    /**
     * Return number of projects shown in dialog
     * @return project count
     */
    public static int getProjectCount() {
        return names.size();
    }
    
    /**
     * Initialize projects - create new Vectors with data or empty them if they exist
     */
    public static void initProjects() {
        if (names==null) {
            names = new Vector();
        } else {
            names.clear();
        }
        if (sourceRootPaths==null) {
            sourceRootPaths = new Vector();
            isExternal = new Vector();
        } else {
            sourceRootPaths.clear();
            isExternal.clear();
        }
        if (selected==null) {
            selected = new Vector();
        } else {
            selected.clear();
        }
    }

    public static Boolean[] getIsExternal(int index) {
        return (Boolean[]) isExternal.get(index);
    }

    public static void setIsExternal(int index, Boolean[] aIsExternal) {
        isExternal.add(index, aIsExternal);
    }
    
}
