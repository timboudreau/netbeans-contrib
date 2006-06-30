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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ant.tasks;

import java.io.File;

import org.apache.tools.ant.Project;

/**Utility methods for Ant tasks.
 *
 * @author Jan Lahoda
 */
public class Utilities {

    /** Creates a new instance of Utilities */
    private Utilities() {
    }

    /**Checks whether the target file is newer than the source file.
     *
     * @param source the source file.
     * @param target the target file.
     * @return true is the target file is up-to-date.
     */
    public static boolean isUpToDate(File source, File target) {
        return target.exists() && target.lastModified() >= source.lastModified();
    }
    
    public static File replaceExtension(File source, String targetExtension) {
        String name    = source.getName();
        int    lastDot = name.lastIndexOf('.');
        
        if (lastDot != (-1)) {
            name = name.substring(0, lastDot) + targetExtension;
        } else {
            name += targetExtension;
        }
        
        return new File(source.getParentFile(), name);
    }
    
    public static boolean isUpToDate(File source, String targetExtension) {
        return isUpToDate(source, replaceExtension(source, targetExtension));
    }
    
    public static File resolveFile(Project project, String fileName) {
        File resolved = new File(project.getBaseDir(), fileName);
        
        if (resolved.exists())
            return resolved;
        
        resolved = new File(fileName);
        
        return resolved;
    }
    
}
