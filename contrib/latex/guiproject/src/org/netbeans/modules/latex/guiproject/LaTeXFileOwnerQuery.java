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
package org.netbeans.modules.latex.guiproject;

import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.FileOwnerQueryImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**A FileOwnerQuery implementation for the LaTeX GUI Project. Currently
 * searches all known LaTeX projects to find the given file.
 *
 * @author Jan Lahoda
 */
public final class LaTeXFileOwnerQuery implements FileOwnerQueryImplementation {
    
    /** Creates a new instance of LaTeXFileOwnerQuery */
    public LaTeXFileOwnerQuery() {
    }

    public Project getOwner(URI file) {
        ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "LaTeXFileOwnerQuery.getOwner(URI) not implemented.");
        return null;
    }

    public Project getOwner(FileObject file) {
        for (Iterator i = LaTeXGUIProjectFactorySourceFactory.get().mainFile2Project.values().iterator(); i.hasNext(); ) {
            LaTeXGUIProject p = (LaTeXGUIProject) i.next();
            
            if (p.contains(file)) {
                return p;
            }
            
            if (Arrays.asList(LaTeXSharabilityQuery.IGNORED_EXTENSIONS).contains(file.getExt())) {
                FileObject brother = FileUtil.findBrother(file, "tex");
                
                if (brother != null && p.contains(brother)) {
                    return p;
                }
            }
        }
        
        return null;
    }
    
}
