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
