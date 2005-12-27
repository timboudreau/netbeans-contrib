/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2005.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Action;
import org.netbeans.modules.latex.bibtex.*;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModel;

import org.netbeans.modules.latex.model.bibtex.Entry;

import org.openide.actions.DeleteAction;
import org.openide.actions.OpenAction;
import org.openide.actions.PropertiesAction;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jan Lahoda
 */
public class BiBEntryNode extends AbstractNode implements PropertyChangeListener {
    private FileObject source;
    private Entry entry;
    
    public BiBEntryNode(Entry entry, FileObject source) {
        super(Children.LEAF, Lookups.fixed(new Object[] {entry, new OpenCookieImpl(entry)}));
        this.entry = entry;
        this.source = source;
        entry.addPropertyChangeListener(this);
        
        setDisplayName("<unknown>");
    }
    
    public Action[] getActions(boolean context) {
        return new Action[] {
            OpenAction.get(OpenAction.class),
            null,
            EditEntryAction.get(EditEntryAction.class),
            SystemAction.get(DeleteAction.class),
            null,
            SystemAction.get(PropertiesAction.class)
        };
    }
    
    public Entry getEntry() {
        return entry;
    }
    
    public boolean canDestroy() {
        return true;
    }
    
    public Action getPreferredAction() {
        return OpenAction.get(OpenAction.class);
    }
    
    public void destroy() throws IOException {
        BiBTeXModel.getModel(source).removeEntry(getEntry());
        super.destroy();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        //TODO: this is not absolutely correct, see the IllegalStateException being thrown to the console.
        firePropertyChange(evt.getPropertyName(), null, null);
    }
    
    private static final class OpenCookieImpl implements OpenCookie {
        
        private Entry entry;
        
        public OpenCookieImpl(Entry entry) {
            this.entry = entry;
        }
        
        public void open() {
            Utilities.getDefault().openPosition(entry.getStartPosition());
        }
        
    }
}