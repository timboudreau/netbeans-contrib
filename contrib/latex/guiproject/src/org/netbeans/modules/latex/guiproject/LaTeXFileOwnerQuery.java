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
package org.netbeans.modules.latex.guiproject;
import java.net.URI;
import java.util.Iterator;


import org.netbeans.api.project.Project;
import org.netbeans.modules.projectapi.SimpleFileOwnerQueryImplementation;

import org.openide.filesystems.FileObject;

/**A FileOwnerQuery implementation for the LaTeX GUI Project. Currently
 * searches all known LaTeX projects to find the given file.
 *
 * @author Jan Lahoda
 */
public final class LaTeXFileOwnerQuery extends SimpleFileOwnerQueryImplementation {
    
    /** Creates a new instance of LaTeXFileOwnerQuery */
    public LaTeXFileOwnerQuery() {
    }

    //public Project getOwner(URI file) { is defined in the super class and delegates to getOwner(FileObject).

    public Project getOwner(FileObject file) {
        for (Iterator i = LaTeXGUIProjectFactorySourceFactory.instanceCreate().mainFile2Project.values().iterator(); i.hasNext(); ) {
            LaTeXGUIProject p = (LaTeXGUIProject) i.next();
            
            if (p.contains(file)) {
                return p;
            }
        }
        
        return null;
    }
    
}
