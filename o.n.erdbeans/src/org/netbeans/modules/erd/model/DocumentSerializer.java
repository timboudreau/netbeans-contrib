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
import org.openide.awt.UndoRedo;

import org.openide.util.RequestProcessor;

import java.util.Vector;
import org.netbeans.modules.erd.io.DocumentSave;
import org.netbeans.modules.erd.io.DocumentLoad;
import org.netbeans.modules.erd.io.ERDContext;
import org.netbeans.modules.erd.io.ERDDataObject;

/**
 * @author David Kaspar
 */
// TODO - versioning + plugable serializers
public class DocumentSerializer {

   

    private Vector<ERDDocumentAwareness> listeners;
    private ERDDataObject dataObject;
    private ERDDocument document;
    private UndoRedo.Manager undoRedoManager;
    private ERDContext context;
    private boolean loaded = false;
    private boolean loading = false;

    private Runnable loader = new Runnable () {
        public void run () {
            undoRedoManager.discardAllEdits ();
            DocumentInterfaceImpl documentInterface = new DocumentInterfaceImpl (dataObject, undoRedoManager);
            ERDDocument loadingDocument = new ERDDocument (documentInterface);
            context=new ERDContext(dataObject,loadingDocument);
            DocumentLoad.load (context);
            documentInterface.enable ();
            synchronized (DocumentSerializer.this) {
                document = loadingDocument;
                loaded = true;
                loading = false;
                DocumentSerializer.this.notifyAll ();
            }
            fireDesignDocumentAwareness (loadingDocument);
        }
    };

    public DocumentSerializer (ERDDataObject dataObject) {
        this.dataObject = dataObject;
        listeners = new Vector<ERDDocumentAwareness> ();
        undoRedoManager = new UndoRedo.Manager ();
    }

    public ERDDocument getDocument () {
        startLoadingDocument ();
        return getActualDocument ();
    }

    ERDDocument getActualDocument () {
        synchronized (this) {
            return document;
        }
    }

    public void startLoadingDocument () {
        synchronized (this) {
            if (loaded  ||  loading)
                return;
            loading = true;
            RequestProcessor.getDefault ().post (loader);
        }
    }

    public void waitDocumentLoaded () {
        startLoadingDocument ();
        try {
            synchronized (this) {
                if (loaded)
                    return;
                if (loading)
                    wait ();
            }
        } catch (InterruptedException e) {
            ErrorManager.getDefault ().notify (e);
        }
    }

   public void reloadDocument () {
        try {
            synchronized (this) {
                if (loading)
                    wait ();
                loaded = false;
            }
        } catch (InterruptedException e) {
            ErrorManager.getDefault ().notify (e);
        }
        waitDocumentLoaded ();
    }
    
    public void saveDocument () {
        waitDocumentLoaded ();
        final ERDDocument savingDocument;
        synchronized (this) {
            savingDocument = document;
        }
        savingDocument.getTransactionManager ().readAccess (new Runnable () {
            public void run () {
                DocumentSave.save (context);
            }
        });
    }

    public void addDesignDocumentAwareness (ERDDocumentAwareness listener) {
        listeners.add (listener);
        listener.setERDDocument (getActualDocument ());
    }
    
    public void removeDesignDocumentAwareness (ERDDocumentAwareness listener) {
        listeners.remove (listener);
    }

    private void fireDesignDocumentAwareness (ERDDocument newDocument) {
        ERDDocumentAwareness[] array = listeners.toArray (new ERDDocumentAwareness[listeners.size ()]);
        for (ERDDocumentAwareness listener : array)
            listener.setERDDocument (newDocument);
    }

    public UndoRedo getUndoRedoManager () {
        synchronized (this) {
            return undoRedoManager;
        }
    }

}
