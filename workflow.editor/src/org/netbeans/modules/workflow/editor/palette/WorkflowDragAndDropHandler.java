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


package org.netbeans.modules.workflow.editor.palette;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import org.netbeans.spi.palette.DragAndDropHandler;
import org.openide.text.ActiveEditorDrop;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExTransferable;

/*
 * 
 */
public class WorkflowDragAndDropHandler extends DragAndDropHandler {
    private static final java.util.logging.Logger mLog = java.util.logging.Logger.getLogger(WorkflowDragAndDropHandler.class.getName());

    /**
     * <code>DataFlavor</code> of palette items dragged from palette to editor. 
     * The trasfer data returned from <code>Transferable</code> for this 
     * <code>DataFlavor</code> is the <code>Lookup</code> of the <code>Node</code>
     * representing the palette item being dragged.
     */
    public static final DataFlavor ITEM_DATA_FLAVOR;
    static {
        try {
            ITEM_DATA_FLAVOR = new DataFlavor("text/active_editor_flavor;class=org.netbeans.modules.workflow.editor.palette.WorkflowActiveEditorDrop", // NOI18N
                    "Paste Item", // XXX missing I18N!
                    Lookup.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }
    
    public WorkflowDragAndDropHandler() {
    }

    public void customize(ExTransferable t, Lookup item) {
        WorkflowActiveEditorDrop activeDrop = (WorkflowActiveEditorDrop) item.lookup(WorkflowActiveEditorDrop.class);
        mLog.info("WorkflowActiveEditorDrop: " + activeDrop);
        PaletteItemTransferable s = new PaletteItemTransferable(activeDrop);
        t.put(s);
    }

    public boolean canDrop(Lookup targetCategory, DataFlavor[] flavors, int dndAction) {
        return false;
    }

    public boolean doDrop(Lookup targetCategory, Transferable item, int dndAction, int dropIndex) {
        return false;
    }
    
    private static class PaletteItemTransferable extends ExTransferable.Single {
        
        private WorkflowActiveEditorDrop drop;

        PaletteItemTransferable(WorkflowActiveEditorDrop drop) {
            super(ITEM_DATA_FLAVOR);
            this.drop = drop;
        }
               
        public Object getData () {
            return drop;
        }
        
    }
    
}


