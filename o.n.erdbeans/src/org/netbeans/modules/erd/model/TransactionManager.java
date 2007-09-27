/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
