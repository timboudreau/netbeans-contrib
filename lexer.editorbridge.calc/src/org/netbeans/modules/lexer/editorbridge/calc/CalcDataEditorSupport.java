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
import org.openide.cookies.EditorCookie;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.PrintCookie;
import org.openide.cookies.SaveCookie;
import org.openide.loaders.DataObject;
import org.openide.text.DataEditorSupport;

/**
 * Calc editor support.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

class CalcDataEditorSupport extends DataEditorSupport
implements OpenCookie, EditCookie, EditorCookie, PrintCookie {
    
    private final Save save; // extra object to distinguish from other cookies

    CalcDataEditorSupport(DataObject obj) {
        super(obj, new DataEditorSupportEnv(obj));

        save = new Save();
        setMIMEType(CalcDataLoader.CALC_MIME_TYPE);
    }

    protected boolean notifyModified () {
        if (!super.notifyModified ()) {
            return false;
        } else {
            ((CalcDataObject)getDataObject()).enableSave(save);
            return true;
        }
    }

    protected void notifyUnmodified () {
        super.notifyUnmodified ();
        ((CalcDataObject)getDataObject()).disableSave(save);
    }

    private class Save implements SaveCookie {
        
        public void save() throws IOException {
            saveDocument();
            getDataObject().setModified(false);
        }

    }
        
}
