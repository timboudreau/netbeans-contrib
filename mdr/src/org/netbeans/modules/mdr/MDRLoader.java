/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.mdr;

import org.openide.loaders.*;
import org.openide.filesystems.FileObject;
import java.io.IOException;

/**
 *
 * @author  mmatula
 */
public class MDRLoader extends UniFileLoader {
    private static final String MDR_EXTENSION = "mdr"; // NOI18N

    /** Creates new MDRLoader */
    public MDRLoader() {
        super("org.netbeans.modules.mdr.MDRDataObject"); // NOI18N
        ExtensionList mdrExt = new ExtensionList();
        mdrExt.addExtension(MDR_EXTENSION);
        this.setExtensions(mdrExt);
    }
    
    public MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new MDRDataObject(primaryFile, this);
    }
}
