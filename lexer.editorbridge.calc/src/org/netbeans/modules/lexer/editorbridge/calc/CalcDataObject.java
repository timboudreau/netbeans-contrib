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
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.UniFileLoader;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

/**
 * Data object that represents calc file.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */
public class CalcDataObject extends MultiDataObject {

    static final String ICON_BASE =
        "org/netbeans/modules/lexer/editorbridge/calc/calcObject"; // NOI18N

    static final long serialVersionUID = 1L;
    
    public CalcDataObject(FileObject pf, UniFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
        getCookieSet().add(new CalcDataEditorSupport(this));
    }

    protected Node createNodeDelegate() {
        return new CalcDataNode(this);
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(CalcDataObject.class);
    }

    void enableSave(SaveCookie save) {
        if (save != null && getCookie(SaveCookie.class) == null) {
            getCookieSet().add(save);
            setModified(true);
        }
    }

    void disableSave(SaveCookie save) {
        if (getCookie(SaveCookie.class) == save) {
            getCookieSet().remove(save);
        }
    }

}
