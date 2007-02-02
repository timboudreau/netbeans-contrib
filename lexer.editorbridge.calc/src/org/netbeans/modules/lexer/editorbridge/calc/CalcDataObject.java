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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

    static final String ICON =
        "org/netbeans/modules/lexer/editorbridge/calc/resources/calcObject.png"; // NOI18N

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
