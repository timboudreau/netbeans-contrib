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

package org.netbeans.api.registry.fs;

import org.netbeans.core.registry.ContextImpl;
import org.netbeans.core.registry.ResettableContextImpl;
import org.netbeans.spi.registry.BasicContext;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.Repository;

/**
 * This class contains helper methods for creation of BasicContext over the
 * Filesystem.
 *
 * @author  David Konecny
 */
public final class FileSystemContextFactory {

    private FileSystemContextFactory() {
    }
    
    /** 
     * Create context implementation over the Filesystem. The
     * passed file object will be root of the context hierarchy.
     *
     * @param root file object which will be root of the context hierarchy
     * @return instance of RootContext created for the given fileobject
     * @deprecated relation between FileObjects and Contexts is just implementation detail.
     * No replacement for this method. 
     */    
    public static BasicContext createContext(FileObject root) {
        BasicContext rc;
        boolean isSFS = false;
        try {
            isSFS = root.getFileSystem().equals(Repository.getDefault().getDefaultFileSystem());
        } catch (FileStateInvalidException ex) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, ex.toString());
            isSFS = false;
        }
        if (isSFS) {
            rc = new ResettableContextImpl(root);
        } else {
            rc = new ContextImpl (root);
        }
        return rc;
    }
    
}
