/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
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
