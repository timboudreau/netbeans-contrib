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

package org.netbeans.modules.lexer.editorbridge.calc;

import java.io.IOException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiDataObject;
import org.openide.text.DataEditorSupport;

/**
 * Env for data editor support
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

class DataEditorSupportEnv extends DataEditorSupport.Env {

    private static final long serialVersionUID = 1L;
        
    DataEditorSupportEnv(DataObject obj) {
        super(obj);
    }

    protected FileObject getFile() {
        return getDataObject().getPrimaryFile();
    }

    protected FileLock takeLock() throws IOException {
        return ((MultiDataObject)getDataObject()).getPrimaryEntry().takeLock();
    }

}
