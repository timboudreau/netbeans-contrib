/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.bibtex.nodes;

import java.util.Collections;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModel;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModelChangeListener;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModelChangedEvent;
import org.netbeans.modules.latex.model.bibtex.Entry;
import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *  @author Jan Lahoda
 */
public class BiBTeXModelChildren extends Children.Keys implements BiBTeXModelChangeListener {
    
    private FileObject source;
    private BiBTeXModelChangeListener listener; 
    private boolean initialized;
    
    public BiBTeXModelChildren(FileObject source) {
        this.source = source;
        initialized = false;
    }
    
    public void addNotify() {
        //TODO: deffer into another thread:
        BiBTeXModel model = BiBTeXModel.getModel(source);
        
        setKeys(model.getEntries());
        model.addBiBTexModelChangeListener(listener = BiBTeXModel.createWeakListeners(this, model));
        initialized = true;
    }
    
    public Node[] createNodes(Object key) {        
        Node created = null;
        
        if (key instanceof PublicationEntry) {
            created = new PublicationEntryNode((PublicationEntry) key, source);
        }
        
        if (created == null) {
            created = new BiBEntryNode((Entry) key, source);
        }

        return new Node[] {created};
    }
    
    public void removeNotify() {
        setKeys(Collections.EMPTY_LIST);
        //free the listener:
        listener = null;
        initialized = false;
    }
    
    public void entriesRemoved(BiBTeXModelChangedEvent event) {
        if (initialized)
            setKeys(((BiBTeXModel) event.getSource()).getEntries());
    }
    
    public void entriesAdded(BiBTeXModelChangedEvent event) {
        if (initialized)
            setKeys(((BiBTeXModel) event.getSource()).getEntries());
    }

}