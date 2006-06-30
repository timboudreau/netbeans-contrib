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
