/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ant.tasks;

import java.io.File;

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
    
}
