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

import java.util.*;

import org.netbeans.modules.vcscore.caching.VcsCacheDir;

/**
 * The listener, that is called from the directory reader to update the files
 * attributes in the given directory.
 *
 * @author Pavel Buzek, Martin Entlicher
 */
public interface DirReaderListener {

    /**
     * The reading of a directory was finished. The files attributes data are provided.
     * @param path the path of the read directory relative to the file system root.
     * @param rawData the collection of arrays of elements defined in
     * {@link org.netbeans.modules.vcscore.caching.RefreshCommandSupport} class.
     * @param whether the reading process succeeded
     */
    public void readDirFinished(String path, Collection rawData, boolean success);

    /**
     * The recursive reading of a directory was finished. The files attributes data are provided.
     * @param path the path of the read directory relative to the file system root.
     * @param rawData the container of the retrieved directory structure with
     * associated array of elements defined in
     * {@link org.netbeans.modules.vcscore.caching.RefreshCommandSupport} class.
     * @param whether the reading process succeeded
     */
    public void readDirFinishedRecursive(String path, VcsDirContainer rawData, boolean success);

}
