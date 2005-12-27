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
package org.netbeans.modules.latex.guiproject;

import java.io.File;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.spi.queries.SharabilityQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXSharabilityQuery implements SharabilityQueryImplementation {
    
    private LaTeXGUIProject p;
    
    /*package private*/ final static String[] IGNORED_EXTENSIONS = new String[] {
        "aux",
        "bbl",
        "blg",
        "dvi",
        "log",
        "ps",
        "toc",
        "lot",
        "lof",
    };
    
    /** Creates a new instance of LaTeXSharabilityQuery */
    public LaTeXSharabilityQuery(LaTeXGUIProject p) {
        this.p = p;
    }

    public int getSharability(File file) {
        if (file.isDirectory()) {
            //no anwser for directories:
            return SharabilityQuery.UNKNOWN;
        }
        
        boolean ignored = false;
        
        for (int cntr = 0; cntr < IGNORED_EXTENSIONS.length && !ignored; cntr++) {
            ignored |= file.getName().endsWith("." + IGNORED_EXTENSIONS[cntr]); //TODO creates a new object
        }
        
        if (ignored) {
            return SharabilityQuery.NOT_SHARABLE;
        }
        
        //private.xml should not be sharable:
        if (FileUtil.toFileObject(file) == p.getProjectDirectory().getFileObject("private.xml"))
            return SharabilityQuery.NOT_SHARABLE;
        
        return SharabilityQuery.SHARABLE;
    }
    
}
