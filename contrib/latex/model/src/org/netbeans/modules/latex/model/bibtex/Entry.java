/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.bibtex;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public abstract class Entry implements Node.Cookie /*Only for Node.getCookie! Remove once possible.*/{
    
    private SourcePosition start;
    private SourcePosition end;
    
    private PropertyChangeSupport pcs;
    
    private BiBTeXModel model;
    
    /** Creates a new instance of Entry */
    protected Entry() {
        this.model = null;
        pcs = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    protected void firePropertyChange(String property, Object old, Object nue) {
//        System.err.println("firePropertyChange(" + property + ", " + old + ", " + nue + ")");
        pcs.firePropertyChange(property, old, nue);
//        System.err.println("endFire.");
    }
    
    /**
     * Getter for property start.
     * @return Value of property start.
     */
    public SourcePosition getStartPosition() {
        return start;
    }
    
    /**
     * Setter for property start.
     * @param start New value of property start.
     */
    public void setStartPosition(SourcePosition start) {
        this.start = start;
    }
    
    /**
     * Getter for property end.
     * @return Value of property end.
     */
    public SourcePosition getEndPosition() {
        return end;
    }
    
    /**
     * Setter for property end.
     * @param end New value of property end.
     */
    public void setEndPosition(SourcePosition end) {
        this.end = end;
    }
    
    public BiBTeXModel getModel() {
        return model;
    }
    
    public /*!!!!*/ void setModel(BiBTeXModel model) {
        this.model = model;
        firePropertyChange("MODEL", null, model);
    }
    
    public abstract String writeOut();
    
    public abstract void update(Entry entry);
}
