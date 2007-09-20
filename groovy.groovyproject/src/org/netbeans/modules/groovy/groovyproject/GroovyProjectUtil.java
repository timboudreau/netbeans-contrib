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
