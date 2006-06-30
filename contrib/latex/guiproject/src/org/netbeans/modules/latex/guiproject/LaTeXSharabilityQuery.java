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
