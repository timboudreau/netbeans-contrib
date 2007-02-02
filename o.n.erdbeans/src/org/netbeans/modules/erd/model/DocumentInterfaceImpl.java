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

package org.netbeans.modules.erd.model;


import javax.swing.event.UndoableEditEvent;
import org.openide.awt.UndoRedo;

import javax.swing.undo.UndoableEdit;
import org.netbeans.modules.erd.io.ERDDataObject;

/**
 * @author David Kaspar 
 */
public class DocumentInterfaceImpl {

    private ERDDataObject  dataObject;
    private UndoRedo.Manager undoRedoManager;
    private boolean enabled;

    public DocumentInterfaceImpl (ERDDataObject dataObject, UndoRedo.Manager undoRedoManager) {
        this.dataObject = dataObject;
        this.undoRedoManager = undoRedoManager;
    }


    public void notifyModified () {
        if (enabled)
             dataObject.getEditorSupport ().notifyModified ();
    }

    public void undoableEditHappened (UndoableEditEvent event) {
        if (enabled)
            //undoRedoManager.addEdit (edit);
            undoRedoManager.undoableEditHappened(event);
    }

    public void discardAllEdits () {
        if (enabled)
            undoRedoManager.discardAllEdits ();
    }

    public void enable () {
        enabled = true;
    }

}
