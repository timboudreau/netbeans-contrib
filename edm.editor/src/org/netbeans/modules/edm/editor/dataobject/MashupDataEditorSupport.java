/*
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the License). You may not use this 
 * file except in compliance with the License.  You can obtain a copy of the
 *  License at http://www.netbeans.org/cddl.html
 *
 * When distributing Covered Code, include this CDDL Header Notice in each
 * file and include the License. If applicable, add the following below the
 * CDDL Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package org.netbeans.modules.edm.editor.dataobject;

import org.netbeans.modules.etl.ui.model.impl.ETLCollaborationModel;
import org.netbeans.modules.edm.editor.multiview.MashupGraphMultiViewDesc;
import org.netbeans.modules.edm.editor.multiview.MashupMultiViewFactory;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import javax.swing.text.Document;
import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViewPerspective;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.spi.multiview.CloseOperationHandler;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.UndoRedo;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.PrintCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.CloneableEditorSupport.Pane;
import org.openide.text.DataEditorSupport;
import org.openide.util.NbBundle;
import org.openide.util.Task;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import java.io.IOException;

/**
 * DOCUMENT ME!
 *
 * To change the template for this generated type comment go to Window - Preferences
 *         - Java - Code Generation - Code and Comments
 *
 * @author tli
 *
 */

public class MashupDataEditorSupport extends DataEditorSupport
        implements OpenCookie, EditCookie, EditorCookie.Observable,
        LineCookie, CloseCookie, PrintCookie {
    /** Used for managing the prepareTask listener. */
    private transient Task prepareTask2;
    private TopComponent multiviewTC;
    
    /**
     *
     *
     */
    public MashupDataEditorSupport(MashupDataObject sobj) {
        super(sobj, new MashupEditorEnv(sobj));
        setMIMEType(MashupDataLoader.REQUIRED_MIME);
    }
    
    
    /**
     *
     *
     */
    public MashupEditorEnv getEnv() {
        return (MashupEditorEnv)env;
    }
    
    @Override
    protected Pane createPane() {
        multiviewTC = MashupMultiViewFactory.createMultiView(
                (MashupDataObject) getDataObject());
        
        multiviewTC.setName(getDataObject().getPrimaryFile().getNameExt());
        
        Mode editorMode = WindowManager.getDefault().findMode(
                MashupDataEditorSupport.EDITOR_MODE);
        if (editorMode != null) {
            editorMode.dockInto(multiviewTC);
        }
        return (Pane)multiviewTC;
    }
    
    /**
     * This is called by the multiview elements whenever they are created
     * (and given a observer knowing their multiview TopComponent). It is
     * important during deserialization and clonig the multiview - i.e. during
     * the operations we have no control over. But anytime a multiview is
     * created, this method gets called.
     *
     * @param  topComp  TopComponent to which we are associated.
     */
    public void setTopComponent(TopComponent mvtc) {
        this.multiviewTC = mvtc;
        
        // Force the title to update so the * left over from when the
        // modified data object was discarded is removed from the title.
        // It is okay for this to be invoked multiple times.
        if (!getEnv().getMashupDataObject().isModified()) {
            // Update later to avoid a loop.
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    updateTitles();
                }
            });
        }
    }
    
    /**
     *
     *
     */
    public static boolean isLastView(TopComponent tc) {
        
        if (!(tc instanceof CloneableTopComponent))
            return false;
        
        boolean oneOrLess = true;
        Enumeration en =
                ((CloneableTopComponent)tc).getReference().getComponents();
        if (en.hasMoreElements()) {
            en.nextElement();
            if (en.hasMoreElements())
                oneOrLess = false;
        }
        
        return oneOrLess;
    }
    
    @Override
    protected Task reloadDocument() {
        Task task = super.reloadDocument();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Remove the undo listener only if columns view is showing.
                if (multiviewTC != null) {
                    MultiViewHandler mvh = MultiViews.findMultiViewHandler(multiviewTC);
                    if (mvh != null) {
                        MultiViewPerspective mvp = mvh.getSelectedPerspective();
                        if (mvp != null) {
                            if (mvp.preferredID().contains(
                                    MashupGraphMultiViewDesc.PREFERRED_ID)) {
                                Document doc = getDocument();
                                UndoRedo.Manager urm = getUndoRedo();
                                if (doc != null && urm != null) {
                                    doc.removeUndoableEditListener(urm);
                                }
                            }
                        }
                    }
                }
            }
        });
        return task;
    }
    
    // Change method access to public
    @Override
    protected void initializeCloneableEditor(CloneableEditor editor) {
        super.initializeCloneableEditor(editor);
        // Force the title to update so the * left over from when the
        // modified data object was discarded is removed from the title.
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Have to do this later to avoid infinite loop.
                updateTitles();
            }
        });
    }
    
    @Override
    protected void updateTitles() {
        super.updateTitles();
        
        // We need to get the title updated on the MultiViewTopComponent.
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (multiviewTC != null) {
                    multiviewTC.setHtmlDisplayName(messageHtmlName());
                    String name = messageName();
                    multiviewTC.setDisplayName(name);
                    multiviewTC.setName(name);
                    multiviewTC.setToolTipText(messageToolTip());
                }
            }
        });
    }
    
    /*
    @Override
    protected UndoRedo.Manager createUndoRedoManager() {
        // Override so the superclass will use our proxy undo manager
        // instead of the default, then we can intercept edits.
        return new QuietUndoManager(super.createUndoRedoManager());
        // Note we cannot set the document on the undo manager right
        // now, as CES is probably trying to open the document.
    }
     */
    
    /**
     * Returns the UndoRedo.Manager instance managed by this editor support.
     *
     * @return UndoRedo.Manager instance.
     */
    /*
    public QuietUndoManager getUndoManager() {
        return (QuietUndoManager) getUndoRedo();
    }
     */
    
    /*
    @Override
    public Task prepareDocument() {
        Task task = super.prepareDocument();
        // Avoid listening to the same task more than once.
        if (task != prepareTask2) {
            prepareTask2 = task;
            task.addTaskListener(new TaskListener() {
                public void taskFinished(Task task) {
                    QuietUndoManager undo = getUndoManager();
                    StyledDocument doc = getDocument();
                    synchronized (undo) {
                        // Now that the document is ready, pass it to the manager.
                        undo.setDocument((AbstractDocument) doc);
                        if (!undo.isCompound()) {
                            // The superclass prepareDocument() adds the undo/redo
                            // manager as a listener -- we need to remove it since
                            // we will initially listen to the model instead.
                            doc.removeUndoableEditListener(undo);
                            // If not listening to document, then listen to model.
                            addUndoManagerToModel(undo);
                        }
                    }
                }
            });
        }
        return task;
    }
    public Task reloadDocument() {
        Task task = super.reloadDocument();
        task.addTaskListener(new TaskListener() {
            public void taskFinished(Task task) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        QuietUndoManager undo = getUndoManager();
                        StyledDocument doc = getDocument();
                        // The superclass reloadDocument() adds the undo
                        // manager as an undoable edit listener.
                        synchronized (undo) {
                            if (!undo.isCompound()) {
                                doc.removeUndoableEditListener(undo);
                            }
                        }
                    }
                });
            }
        });
        return task;
    }
     
    protected void notifyClosed() {
        // Stop listening to the undoable edit sources when we are closed.
        QuietUndoManager undo = getUndoManager();
        StyledDocument doc = getDocument();
        synchronized (undo) {
            // May be null when closing the editor.
            if (doc != null) {
                doc.removeUndoableEditListener(undo);
                undo.endCompound();
            }
            try {
                CasaModel model = getModel();
                if (model != null) {
                    //model.removeUndoableEditListener(undo);
                }
                // Must unset the model when no longer listening to it.
                undo.setModel(null);
            } catch (IOException ioe) {
                // Model is gone, but just removing the listener is not
                // going to matter anyway.
            }
        }
     
        super.notifyClosed();
    }
     */
    
    public ETLCollaborationModel getModel() throws IOException {
        return getEnv().getMashupDataObject().getModel();
        /*
        ModelSource modelSource = Utilities.getModelSource(dobj.getPrimaryFile(), true);
        if(modelSource != null) {
            return CasaModelFactory.getDefault().getModel(modelSource);
        }
         */
    }
    
    /**
     * Adds the undo/redo manager to the document as an undoable edit
     * listener, so it receives the edits onto the queue. The manager
     * will be removed from the model as an undoable edit listener.
     *
     * <p>This method may be called repeatedly.</p>
     */
    /*
    public void addUndoManagerToDocument() {
        // This method may be called repeatedly.
        // Stop the undo manager from listening to the model, as it will
        // be listening to the document now.
        QuietUndoManager undo = getUndoManager();
        StyledDocument doc = getDocument();
        synchronized (undo) {
            try {
                CasaModel model = getModel();
                if (model != null) {
                    // model.removeUndoableEditListener(undo);
                }
                // Must unset the model when no longer listening to it.
                undo.setModel(null);
            } catch (IOException ioe) {
                // Model is gone, but just removing the listener is not
                // going to matter anyway.
            }
            // Document may be null if the cloned views are not behaving correctly.
            if (doc != null) {
                // Ensure the listener is not added twice.
                doc.removeUndoableEditListener(undo);
                doc.addUndoableEditListener(undo);
                // Start the compound mode of the undo manager, such that when
                // we are hidden, we will treat all of the edits as a single
                // compound edit. This avoids having the user invoke undo
                // numerous times when in the model view.
                undo.beginCompound();
            }
        }
    }
     */
    
    /**
     * Removes the undo/redo manager undoable edit listener from the
     * document, to stop receiving undoable edits. The manager will
     * be added to the model as an undoable edit listener.
     *
     * <p>This method may be called repeatedly.</p>
     */
    /*
    public void removeUndoManagerFromDocument() {
        // This method may be called repeatedly.
        QuietUndoManager undo = getUndoManager();
        StyledDocument doc = getDocument();
        synchronized (undo) {
            // May be null when closing the editor.
            if (doc != null) {
                doc.removeUndoableEditListener(undo);
                undo.endCompound();
            }
            // Have the undo manager listen to the model when it is not
            // listening to the document.
            addUndoManagerToModel(undo);
        }
    }
     */
    
    /**
     * Add the undo/redo manager undoable edit listener to the model.
     *
     * <p>Caller should synchronize on the undo manager prior to calling
     * this method, to avoid thread concurrency issues.</p>
     *
     * @param  undo  the undo manager.
     */
    /*
    private void addUndoManagerToModel(QuietUndoManager undo) {
        // This method may be called repeatedly.
        try {
            CasaModel model = getModel();
            if (model != null) {
                // Ensure the listener is not added twice.
                //model.removeUndoableEditListener(undo);
                //model.addUndoableEditListener(undo);
                // Ensure the model is sync'd when undo/redo is invoked,
                // otherwise the edits are added to the queue and eventually
                // cause exceptions.
                //undo.setModel(model);
            }
        } catch (IOException ioe) {
            // Model is gone, nothing will work, return immediately.
        }
    }
     */
    
    /**
     * This method allows the close behavior of CloneableEditorSupport to be
     * invoked from the SourceMultiViewElement. The close method of
     * CloneableEditorSupport at least clears the undo queue and releases
     * the swing document.
     */
    public boolean silentClose() {
        return super.close(false);
    }
    
    /**
     * Have the WSDL model sync with the document.
     */
    public void syncModel() {
        // Only sync the document if the change relates to loss of focus,
        // which indicates that we are switching from the source view.
        // Update the tree with the modified text.
        try {
            if(getModel() != null) {
                synchDocument();
            }
        } catch (Throwable ioe) {
            // The document cannot be parsed
            NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(MashupDataEditorSupport.class, "MSG_NotWellformedWsdl"), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        
    }
    
    public void synchDocument() {
        // implement synchDocument;
        try {
            MashupDataObject mashupDataObject = (MashupDataObject) getDataObject();
            if(mashupDataObject.getModel() != null) {
                String content = mashupDataObject.getModel().getETLDefinition().toXMLString("");
                Document doc = getDocument();
                if(doc != null) {
                    doc.remove(0, getDocument().getLength());
                    doc.insertString(0, content, null);
                }
                mashupDataObject.getModel().setDirty(false);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Inner class
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Env class extends DataEditorSupport.Env.
     */
    protected static class MashupEditorEnv extends DataEditorSupport.Env {
        
        static final long serialVersionUID =1099957785497677206L;
        
        public MashupEditorEnv(MashupDataObject obj) {
            super(obj);
        }
        
        public CloneableEditorSupport findTextEditorSupport() {
            return getMashupDataObject().getMashupDataEditorSupport();
        }
        
        public MashupDataObject getMashupDataObject(){
            return (MashupDataObject) getDataObject();
        }
        
        @Override
        protected FileObject getFile() {
            return getDataObject().getPrimaryFile();
        }
        
        @Override
        protected FileLock takeLock() throws IOException {
            return getDataObject().getPrimaryFile().lock();
        }
    }
    
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Inner class
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Implementation of CloseOperationHandler for multiview. Ensures both
     * column view and xml editor are correctly closed, data saved, etc. Holds
     * a reference to DataObject only - to be serializable with the
     * multiview TopComponent without problems.
     */
    /* */
    public static class CloseHandler implements CloseOperationHandler, Serializable {
        private static final long serialVersionUID =-3838395157610633251L;
        private DataObject dataObject;
        
        private CloseHandler() {
            super();
        }
        
        public CloseHandler(DataObject dobj) {
            dataObject = dobj;
        }
        
        private MashupDataEditorSupport getAspectDataEditorSupport() {
            return dataObject instanceof MashupDataObject ?
                ((MashupDataObject) dataObject).getMashupDataEditorSupport() : null;
        }
        
        public boolean resolveCloseOperation(CloseOperationState[] elements) {
            MashupDataEditorSupport mashupEditor = getMashupEditorSupport();
            if (mashupEditor != null) {
                // This handles saving the document.
                boolean close = mashupEditor.canClose();
                if (close) {
                    if(dataObject.isValid()) {
                        // In case user discarded edits, need to reload.
                        if (dataObject.isModified()) {
                            // In case user discarded edits, need to reload.
                            mashupEditor.reloadDocument().waitFinished();
                        }
                        
                        mashupEditor.syncModel();
                        // Need to properly close the support, too.
                        mashupEditor.notifyClosed();
                    }
                }
                return close;
            }
            return true;
        }
        
        private MashupDataEditorSupport getMashupEditorSupport() {
            return dataObject != null &&
                    dataObject instanceof MashupDataObject ?
                        ((MashupDataObject)dataObject).getMashupDataEditorSupport() : null;
            
        }
    }
    /* */
}

