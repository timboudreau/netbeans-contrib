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
