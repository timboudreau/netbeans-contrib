/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.lexer.editorbridge.calc;

import java.io.IOException;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.filesystems.FileObject;
import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;
import org.openide.actions.FileSystemAction;
import org.openide.actions.CutAction;
import org.openide.actions.CopyAction;
import org.openide.actions.PasteAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.RenameAction;
import org.openide.actions.ToolsAction;
import org.openide.actions.PropertiesAction;


/**
 * Loader for calc data objects.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public class CalcDataLoader extends UniFileLoader {

    /** Extension of calc files */
    static final String CALC_EXTENSION = "calc"; // NOI18N

    /** Mime type assigned to calc files */
    static final String CALC_MIME_TYPE = "text/x-calc"; // NOI18N

    static final long serialVersionUID = 1L;
    
    public CalcDataLoader() {
        super("org.netbeans.modules.lexer.editorbridge.calc.CalcDataObject"); // NOI18N
    }

    protected void initialize() {
        super.initialize();
        getExtensions().addExtension(CALC_EXTENSION);
    }

    protected MultiDataObject createMultiObject(FileObject primaryFile)
    throws DataObjectExistsException, IOException {
        return new CalcDataObject(primaryFile, this);
    }
    
    protected SystemAction[] defaultActions() {
        return new SystemAction[] {
            SystemAction.get(OpenAction.class),
            SystemAction.get(FileSystemAction.class),
            null,
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(PasteAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            SystemAction.get(RenameAction.class),
            null,
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class),
        };
    }

}
