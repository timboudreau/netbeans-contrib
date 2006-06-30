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
    
    protected FileObject findPrimaryFile (FileObject fo) {
        FileObject result = super.findPrimaryFile(fo);
        if (result == null)
            return null;
        String fileName = result.getName();
        if (fileName.indexOf('[')==-1 || fileName.indexOf(']')==-1)
            return null;
        return result;
    }
    
}
