/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.registry;

import java.io.File;

/**
 * The recognizer of a VCS filesystem.
 *
 * @author  Martin Entlicher
 */
public abstract class FSRecognizer {
    
    /**
     * Get a filesystem info for the given physical folder.
     * @param folder The folder, that is to be recognized.
     * @return Filesystem info for the given folder or <code>null</code> when
     *         no filesystem is recognized.
     */
    public abstract FSInfo findFSInfo(File folder);
    
    /**
     * Create an empty customizable filesystem info.
     * That is intended for creating of new filesystem information,
     * that were not recognized automatically.
     */
    public abstract FSInfo createFSInfo();
    
}
