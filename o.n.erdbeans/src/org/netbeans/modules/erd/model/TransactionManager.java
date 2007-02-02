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

import org.openide.ErrorManager;
import org.openide.util.Mutex;

import javax.swing.undo.*;
import java.util.Collection;
import javax.swing.event.UndoableEditEvent;

public final class TransactionManager {

    private final ERDDocument document;
    private ListenerManager listenerManager;
    private final Mutex mutex = new Mutex ();
    private boolean notRootLevelWriteAccess = false;
    private boolean assertEventAllowed = false;
    private boolean rollback = false;
    private boolean useUndoManager = false;
    private boolean discardAllEdits = false;
    private TransactionEdit transactionEdit;

    TransactionManager (ERDDocument document,ListenerManager listenerManager) {
        this.listenerManager=listenerManager;
        this.document = document;
       
    }
    
    public void discardAllEdits(){
        this.discardAllEdits=true;
    }
    
    /**
     * Executes a Runnable.run method with read access.
     * @param runnable the runnable
     */
    public void readAccess (final Runnable runnable) {
       
                mutex.readAccess (runnable);
       
    }

    /**
     * Executes a Runnable.run method with write access.
     * @param runnable the runnable
     * @return the event id at the end of the write transaction
     */
    public long writeAccess (final Runnable runnable) {
        final long eventID[] = new long[] { 0 };
       
                mutex.writeAccess (new Runnable () {
                    public void run () {
                        writeAccessCore (runnable);
                        
                    }
                });
       return eventID[0];
    }

    private void writeAccessCore (Runnable runnable) {
        boolean rootLevel = ! notRootLevelWriteAccess;
        notRootLevelWriteAccess = true;

        if (rootLevel)
            writeAccessRootBegin ();

        try {
            runnable.run ();
        } finally {
            if (rootLevel)
                writeAccessRootEnd ();
        }
    }

    private void writeAccessRootBegin () {
        assertEventAllowed = true;
        rollback = false;
        useUndoManager = true;
        discardAllEdits = false;
        transactionEdit = null;
    }

    private void writeAccessRootEnd () {
        assertEventAllowed = false;
        ERDEvent event = null;
        try {
            if (rollback)
                rollbackCore ();
            event = listenerManager.fireEvent ();
        } finally {
            try {
                if (useUndoManager) {
                    if (discardAllEdits)
                        document.getDocumentInterface ().discardAllEdits ();
                    else if (transactionEdit != null) {
                        transactionEdit.end ();
                        UndoableEditEvent editEvent=new UndoableEditEvent(document,transactionEdit);
                        document.getDocumentInterface ().undoableEditHappened (editEvent);
                    }
                }
            } finally {
                notRootLevelWriteAccess = false;
                if (event != null  &&  event.isStructureChanged ())
                    document.getDocumentInterface ().notifyModified ();    
            }
        }
    }

    /**
     * Checks whether the current thread has a read or a write access granted.
     * @return true if a read or a write access is granted
     */
    public boolean isAccess () {
        return mutex.isReadAccess ()  ||  mutex.isWriteAccess ();
    }

    /**
     * Checks whether the current thread has a write access granted.
     * @return true if a write access is granted
     */
    public boolean isWriteAccess () {
        return mutex.isWriteAccess ();
    }

   
    public void rollback () {
        assert assertEventAllowed;
        rollback = true;
        useUndoManager = false;
    }

    private void rollbackCore () {
        if (discardAllEdits) {
            ErrorManager.getDefault ().log (ErrorManager.ERROR, "Cannot rollback operation");
            return;
        }
        // TODO - implement rollback
        ErrorManager.getDefault ().log (ErrorManager.ERROR, "Rollback is not supported"); // NOI18N
    }

   

    

    void writePropertyHappened (ERDComponent component, Enum propertyName, String oldValue, String newValue) {
        assert assertEventAllowed;
        listenerManager.addAffectedERDComponent(component);
        undoableEditHappened (new WritePropertyEdit (component, propertyName, oldValue, newValue));
    }

    void notifyComponentCreated (ERDComponent component) {
        assert assertEventAllowed;
        listenerManager.notifyComponentCreated(component);
        
    }

    /**
     * Adds an undoable edit into a undo-redo queue.
     * <p>
     * Note: use this to add an additional undoable edit that cannot be produces by the model directly.
     * <p>
     * @param edit the edit; for whole edit instance lifecycle, it has to: edit.isSignificant must return false, edit.canUndo and edit.canRedo must return true, edit.undo and edit.redo must not throw any exception.
     */
    public void undoableEditHappened (UndoableEdit edit) {
        assert isWriteAccess ();
        assert ! edit.isSignificant ();
        if (transactionEdit == null)
            transactionEdit = new TransactionEdit ();
        transactionEdit.addEdit (edit);
    }

    private class TransactionEdit extends CompoundEdit {

        public boolean isSignificant () {
            return true;
        }

        public void undo () throws CannotUndoException {
            final boolean[] error = new boolean[1];
            writeAccess (new Runnable () {
                public void run () {
                    useUndoManager = false;
                    try {
                        TransactionEdit.super.undo ();
                    } catch (CannotUndoException e) {
                        error[0] = true;
                        ErrorManager.getDefault ().notify (ErrorManager.ERROR, e);
                    }
                }
            });
            if (error[0])
                throw new CannotUndoException ();
        }

        public void redo () throws CannotRedoException {
            final boolean[] error = new boolean[1];
            writeAccess (new Runnable() {
                public void run () {
                    useUndoManager = false;
                    try {
                        TransactionEdit.super.redo ();
                    } catch (CannotRedoException e) {
                        error[0] = true;
                        ErrorManager.getDefault ().notify (ErrorManager.ERROR, e);
                    }
                }
            });
            if (error[0])
                throw new CannotRedoException ();
        }

    }

   

    public class WritePropertyEdit extends AbstractUndoableEdit {

        private ERDComponent component;
        private Enum propertyName;
        private String oldValue;
        private String newValue;

        public WritePropertyEdit (ERDComponent component, Enum propertyName, String oldValue, String newValue) {
            this.component = component;
            this.propertyName = propertyName;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public boolean isSignificant () {
            return false;
        }

        public void undo () throws CannotUndoException {
            super.undo ();
            component.writeProperty (propertyName, oldValue);
        }

        public void redo () throws CannotRedoException {
            super.redo ();
            component.writeProperty (propertyName, newValue);
        }

    }

   
}
