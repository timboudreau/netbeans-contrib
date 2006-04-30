/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.model.platform;

import java.net.URI;
import org.openide.filesystems.FileObject;

/** This class allows to view the content of a particular file.
 *  
 *  It also allows to view a particular position in the file.
 *
 *  @author Jan Lahoda
 */
public interface Viewer {
    
    /** Shows the given file in a viewer.
     *
     *  @param file file to show
     *
     *  @throws NullPointerException is the file is null
     */
    public void show(FileObject file, FilePosition startPosition) throws NullPointerException;

    public String getName();

    public String getDisplayName();

    public boolean isSupported();

    public boolean accepts(URI uri);
    
}
