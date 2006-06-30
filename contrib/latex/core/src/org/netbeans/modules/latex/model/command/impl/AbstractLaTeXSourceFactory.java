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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.impl;

import java.util.Map;
import java.util.WeakHashMap;
import org.openide.filesystems.FileObject;

import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.LaTeXSource.UnsupportedFileTypeException;
import org.netbeans.modules.latex.model.command.LaTeXSourceFactory;
import org.openide.loaders.DataShadow;

public abstract class AbstractLaTeXSourceFactory extends LaTeXSourceFactory {

    public AbstractLaTeXSourceFactory() {
    }

    private static Map fileToSource = null;
    protected static synchronized Map getFileToSource() {
        if (fileToSource == null) {
            fileToSource = new WeakHashMap();
        }
        
        return fileToSource;
    }
    
    public LaTeXSource get(Object file) {
        Map fileToSource = getFileToSource();
        
        LaTeXSource source = (LaTeXSource) fileToSource.get(file);
        
        if (source == null) {
            if (!supports(file))
                throw new UnsupportedFileTypeException("File " + file + ", class " + file.getClass() + " is of unsupported type.");
                
            source = createSource(file);
            fileToSource.put(file, source);
        }
        
        return source;
    }
    
    public boolean supports(Object file) {
        return file instanceof FileObject;
    }

    public abstract LaTeXSource createSource(Object file);

}
