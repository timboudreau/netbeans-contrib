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

package org.netbeans.modules.docbook;

import org.openide.actions.*;
import org.openide.cookies.*;
import org.openide.filesystems.FileObject;
import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;

public class DocBookDataObject extends MultiDataObject {
    
    public DocBookDataObject(FileObject pf, DocBookDataLoader loader) throws DataObjectExistsException {
        super(pf, loader);
        getCookieSet().add(new DocBookEditorSupport(this));
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected Node createNodeDelegate() {
        return new DocBookDataNode(this);
    }
    
    final void addSaveCookie(SaveCookie save) {
        getCookieSet().add(save);
    }
     
    final void removeSaveCookie(SaveCookie save) {
        getCookieSet().remove(save);
    }
    
}
