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

import java.util.Collection;
import java.util.EventListener;

/**
 * Listener, that is called to update the file attributes.
 *
 * @author  Martin Entlicher
 */
public interface FileReaderListener extends EventListener {

    /**
     * The reading of file attributes was finished.
     * @param path the path of the file relative to the file system root.
     * @param rawData the data with attributes of one or more files.
     * Trailing '/' in file name denotes folder.
     */
    public void readFileFinished(String path, Collection rawData);
}

