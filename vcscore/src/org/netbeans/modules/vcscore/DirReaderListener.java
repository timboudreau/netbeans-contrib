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

package org.netbeans.modules.vcscore;

import java.util.*;

/**
 * The listener, that is called from the directory reader to update the files
 * attributes in the given directory.
 *
 * @author Pavel Buzek, Martin Entlicher
 */
public interface DirReaderListener extends EventListener {

    /**
     * The reading of a directory was finished. The files attributes data are provided.
     * @param path the path of the read directory relative to the file system root.
     * @param rawData the collection of arrays of elements defined in StatusFormat
     * @param success whether the reading process succeeded
     */
    public void readDirFinished(String path, Collection rawData, boolean success);

    /**
     * The recursive reading of a directory was finished. The files attributes data are provided.
     * @param path the path of the read directory relative to the file system root.
     * @param rawData the container of the retrieved directory structure with
     * associated array of elements defined in
     * {@link StatusFormat} class.
     * @param success whether the reading process succeeded
     */
    public void readDirFinishedRecursive(String path, VcsDirContainer rawData, boolean success);

}
