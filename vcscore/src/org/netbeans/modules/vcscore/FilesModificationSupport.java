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

package org.netbeans.modules.vcscore;

import javax.swing.event.ChangeListener;

/**
 * By implementing this interface the class declares that it can notify
 * clients about changes in files/directories.
 * It allows to attach a ChangeListener, that is called whenever a file
 * or directory content is changed.
 * The source in the provided ChangeEvent is the <code>File</code> that changed
 * (file or directory).
 *
 * @author Martin Entlicher
 */
public interface FilesModificationSupport {
    
    void addFilesStructureModificationListener(ChangeListener chl);
    
    void removeFilesStructureModificationListener(ChangeListener chl);
    
}
