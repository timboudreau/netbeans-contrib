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

package org.netbeans.modules.archiver;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.ExtensionList;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class ArchiveDataLoader extends UniFileLoader {

    private static final String MIME_TYPE = "text/xml+java-archiver";

    private static final long serialVersionUID = 1L;

    public ArchiveDataLoader() {
       super("org.netbeans.modules.archiver.ArchiveDataObject");
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(ArchiveDataLoader.class, "LBL_loaderName");
    }
    
    protected void initialize() {
        super.initialize();
        ExtensionList extensions = new ExtensionList();
        extensions.addMimeType(MIME_TYPE);
        setExtensions(extensions);
    }
    
    protected String actionsContext() {
        return "Loaders/" + MIME_TYPE + "/Actions"; // NOI18N
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new ArchiveDataObject(primaryFile, this);
    }

}
