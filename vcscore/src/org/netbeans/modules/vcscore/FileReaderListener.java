/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore;

import java.util.Collection;
/**
 * Listener, that is called to update the file attributes.
 *
 * @author  Martin Entlicher
 */
public interface FileReaderListener {

    /**
     * The reading of file attributes was finished.
     * @param path the path of the file relative to the file system root.
     * @param rawData the data with attributes of one or more files.
     */
    public void readFileFinished(String path, Collection rawData);
}

