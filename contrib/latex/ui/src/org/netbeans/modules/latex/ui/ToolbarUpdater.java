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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.LaTeXSource.DocumentChangeEvent;
import org.netbeans.modules.latex.model.command.LaTeXSource.DocumentChangedListener;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.ErrorManager;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jan Lahoda
 */
public class ToolbarUpdater implements CaretListener, PropertyChangeListener, DocumentChangedListener {
    
    private Reference   currentNode;
    private LaTeXSource source;
    private JEditorPane currentPane;
    
    private List        listeners;
    
    private static ToolbarUpdater instance = null;
    
    public static synchronized ToolbarUpdater getDefault() {
        if (instance == null) {
            instance = new ToolbarUpdater();
        }
        
        return instance;
    }
    
    protected static synchronized void destroy() {
        TopComponent.getRegistry().removePropertyChangeListener(instance);
        instance = null;
    }
    
    /** Creates a new instance of ToolbarUpdater */
    protected ToolbarUpdater() {
        listeners = new ArrayList();
        
        TopComponent.getRegistry().addPropertyChangeListener(this);
        setup();
    }
    
    private Node getCurrentNodeImpl() {
        if (currentNode == null)
            return null;
        
        Node node = (Node) currentNode.get();
        
        if (node == null)
            return null;
        
        if (source.getDocument() != node.getDocumentNode())
            return null;
        
        return node;
    }
    
    public void nodesAdded(DocumentChangeEvent evt) {
        heavyUpdate();
    }
    
    public void nodesChanged(DocumentChangeEvent evt) {
        heavyUpdate();
    }
    
    public void nodesRemoved(DocumentChangeEvent evt) {
        heavyUpdate();
    }
    
    private RequestProcessor.Task updateTask = null;
    
    private synchronized void prepareUpdateTask(Runnable r) {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        
        updateTask = RequestProcessor.getDefault().post(r, 200);
    }
    
    public void caretUpdate(CaretEvent e) {
        prepareUpdateTask(new Runnable() {
            public void run() {
                if (currentPane == null)
                    return ;
                
                LaTeXSource.Lock lock        = null;
                boolean          heavyUpdate = false;
                
                try {
                    lock = source.lock(false);
                    if (lock != null) {
                        Node node = getCurrentNodeImpl();
                        
                        if (node == null) {
                            heavyUpdate = true;
                        } else {
                            Document doc = currentPane.getDocument();
                            
                            heavyUpdate = !node.contains(new SourcePosition(Utilities.getDefault().getFile(doc), doc, /*e.getDot()*/currentPane.getCaret().getDot()));
                        }
                    } else {
                        //no update in this case...
                    }
                } finally {
                    if (lock != null)
                        source.unlock(lock);
                }
                
                if (heavyUpdate)
                    heavyUpdate();
            }
        });
    }
    
    public void heavyUpdate() {
        LaTeXSource.Lock lock   = null;
        boolean          enable = false;
        
        try {
            lock = source.lock(false);
            
            if (lock != null) {
                Node node = source.findNode(currentPane.getDocument(), currentPane.getCaret().getDot());
                
                if (node == null) {
                    fireToolbarEnableChange(false);
                    
                    return ;
                }
                
                currentNode = new WeakReference(node);
                
                enable = true;
                fireToolbarStatusChange(node);
            }
        } catch (IOException e) {
            //cannot find ;-(
            ErrorManager.getDefault().notify(e);
        } finally {
            if (lock != null)
                source.unlock(lock);
        }
        
        fireToolbarEnableChange(enable);
    }
    
    protected void fireToolbarStatusChange(Node node) {
        ToolbarStatusChangeListener[] listnrs = null;
        
        synchronized (this) {
            listnrs = (ToolbarStatusChangeListener[] ) listeners.toArray(new ToolbarStatusChangeListener[0]);
        }
        
        for (int cntr = 0; cntr < listnrs.length; cntr++) {
            listnrs[cntr].statusChange(node);
        }
    }
    
    protected void fireToolbarEnableChange(boolean enable) {
        ToolbarStatusChangeListener[] listnrs = null;
        
        synchronized (this) {
            listnrs = (ToolbarStatusChangeListener[] ) listeners.toArray(new ToolbarStatusChangeListener[0]);
        }
        
        for (int cntr = 0; cntr < listnrs.length; cntr++) {
            listnrs[cntr].enableChange(enable);
        }
    }

    public synchronized void addToolbarStatusChangeListener(ToolbarStatusChangeListener l) {
        listeners.add(l);
    }
    
    public synchronized void removeToolbarStatusChangeListener(ToolbarStatusChangeListener l) {
        listeners.remove(l);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        setup();
    }
    
    private synchronized void setup() {
        if (currentPane != null)
            currentPane.removeCaretListener(this);
        
        if (source != null)
            source.removeDocumentChangedListener(this);
        
        currentPane = UIUtilities.getCurrentEditorPane();
        
        if (currentPane == null) {
            fireToolbarEnableChange(false);
            return ;
        }
        
        if (currentPane.getEditorKit().getContentType() != "text/x-tex") {
            currentPane = null;
            fireToolbarEnableChange(false);
            return ;
        }
        
        try {
            source      = LaTeXSource.get(Utilities.getDefault().getFile(currentPane.getDocument()));
        } catch (LaTeXSource.UnsupportedFileTypeException e) {
            //ehm. nothing.
            //...
            fireToolbarEnableChange(false);
            return ;
        }
        
        currentPane.addCaretListener(this);
        
        if (source != null) //!!!
            source.addDocumentChangedListener(this);
    }
    
}
