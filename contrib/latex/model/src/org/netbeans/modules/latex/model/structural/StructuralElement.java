/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.structural;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public abstract class StructuralElement implements HelpCtx.Provider {
    
    public static final String SUB_ELEMENTS = "subElements";
    
    protected static final int NO_PRIORITY = (-1);
    
    private List/*<StructuralElement>*/ subElements;
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    /** Creates a new instance of StructuralNode */
    public StructuralElement() {
        subElements = new ArrayList();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    public List getSubElements() {
        return Collections.unmodifiableList(subElements);
    }
    
    public void addSubElement(StructuralElement el) {
        assert getPriority() == NO_PRIORITY || el.getPriority() == NO_PRIORITY || getPriority() < el.getPriority() : "Priority inversion parent priority=" + getPriority() + ", child priority=" + el.getPriority();
        
        subElements.add(el);
        
//        pcs.firePropertyChange(SUB_ELEMENTS, null, null);
    }
    
    //!!!!only authorized personel should do this (in particular, this should be done only when the element is updated)!
    public void clearSubElements() {
        subElements.clear();
//        pcs.firePropertyChange(SUB_ELEMENTS, null, null);
    }
    
    public void fireSubElementsChange() {
        pcs.firePropertyChange(SUB_ELEMENTS, null, null);
    }
    
    public abstract int getPriority();
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
        
//    public SourcePosition getStartingPosition() {
//        return null;
//    }
//    
//    public SourcePosition getEndingPosition() {
//        return null;
//    }
}
