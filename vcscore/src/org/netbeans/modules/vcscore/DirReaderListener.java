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

/*
 * Log
 *  5    Jaga      1.3.1.0     3/8/00   Martin Entlicher Recursive refresh added.
 *  4    Gandalf   1.3         12/21/99 Martin Entlicher 
 *  3    Gandalf   1.2         10/25/99 Pavel Buzek     copyright and log
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         9/30/99  Pavel Buzek     
 * $
 */
