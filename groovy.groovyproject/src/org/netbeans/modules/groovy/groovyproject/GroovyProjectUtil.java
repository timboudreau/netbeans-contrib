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

package org.netbeans.modules.groovy.groovyproject;

import java.util.List;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;

/**
 * Miscellaneous utilities for the j2seproject module.
 * @author  Jiri Rechtacek
 */
public class GroovyProjectUtil {
    private GroovyProjectUtil () {}
    
    /** Returns the J2SEProject sources directory.
     *
     * @param p project
     * @return source directory or null if directory not set or if the project 
     * doesn't provide AntProjectHelper
     */    
    public static FileObject getProjectSourceDirectory(Project p) {
        GroovyProject j2seprj = (GroovyProject) p.getLookup().lookup(GroovyProject.class);
        if (j2seprj != null) {
            return j2seprj.getSourceDirectory();
        } else {
            return null;
        }
    }
    
    /**
     * Returns the property value evaluated by GroovyProject's PropertyEvaluator.
     *
     * @param p project
     * @param value of property
     * @return evaluated value of given property or null if the property not set or
     * if the project doesn't provide AntProjectHelper
     */    
    public static Object getEvaluatedProperty(Project p, String value) {
        if (value == null) {
            return null;
        }
        GroovyProject groovyprj = (GroovyProject) p.getLookup().lookup(GroovyProject.class);
        if (groovyprj != null) {
            return groovyprj.evaluator().evaluate(value);
        } else {
            return null;
        }
    }
    
    public static void getAllScripts(String prefix, FileObject sourcesRoot, List/*<String>*/ result) {
        FileObject children[] = sourcesRoot.getChildren();
        if (!"".equals(prefix)) {
            prefix += "/";
        }
        for (int i = 0; i < children.length; i++) {
            if (children[i].isData()) {
                result.add(prefix + children[i].getName());
            }
            if (children[i].isFolder()) {
                getAllScripts(prefix + children[i].getName(), children[i], result);
            }
        }
    }
    
    public static boolean isMainScript(String mainScript, FileObject sourcesRoot) {
        int slashIndex = mainScript.indexOf('/');
        String folderName = "";
        if (slashIndex >= 0) {
            folderName = mainScript.substring(0, slashIndex);
        }
        FileObject children[] = sourcesRoot.getChildren();
        for (int i = 0;i < children.length; i++) {
            if (children[i].isFolder() && children[i].getName().equals(folderName)) {
                return isMainScript(mainScript.substring(slashIndex+1, mainScript.length()), children[i]);
            }
            if (children[i].isData() && children[i].getName().equals(mainScript)) {
                return true;
            }
        }
        return false;
    }
}
