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
        firePropertyChange("model", null, model);
    }
    
    public abstract String writeOut();
    
    public abstract void update(Entry entry);
}
