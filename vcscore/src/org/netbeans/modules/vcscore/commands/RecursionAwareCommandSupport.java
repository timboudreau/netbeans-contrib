/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

/**
 * This extension of CommandSupport can be asked whether the created commands
 * will be able to process folders nonrecursively.
 * This is necessary for flat views, which do not show subfolders.
 *
 * @author  Martin Entlicher
 */
public interface RecursionAwareCommandSupport {
    
    /**
     * Tells, whether the created commands will be able to process folders
     * non-recursively. If this is true, the created commands should implement
     * {@link RecursionAwareCommand} and when <code>setRecursionBanned(true)</code>
     * is called on them, they should still return non-<code>null</code>
     * from <code>getApplicableFiles()</code> method for some files.
     * @return true when the created commands can process folders non-recursively.
     */
    boolean canProcessFoldersNonRecursively();
    
}
