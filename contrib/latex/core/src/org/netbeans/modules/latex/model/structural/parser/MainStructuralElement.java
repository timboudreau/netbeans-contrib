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
package org.netbeans.modules.latex.model.structural.parser;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.label.LabelStructuralElement;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class MainStructuralElement extends StructuralElement {
    
    private List labels;
    
    public MainStructuralElement() {
        labels = new ArrayList();
    }
    
    public SystemAction[] createActions() {
        return new SystemAction[0];
    }
    
    public int getPriority() {
        return -1;
    }
    
    public List getLabels() {
        return labels;
    }
    
    public void addLabel(LabelStructuralElement el) {
        labels.add(el);
    }
    
    public void clearLabels() {
        labels.clear();
    }
    
//    public boolean equals(Object o) {
//        System.err.println("MainStructuralElement.equals, this=" + this + ", o=" + o);
//        if (!getClass().equals(o.getClass()))
//            return false;
//        
//        System.err.println("same class.");
//        
//        MainStructuralElement el = (MainStructuralElement) o;
//        
//        if (getPriority() != el.getPriority())
//            return false;
//        
//        System.err.println("same priority");
//        
////        System.err.println("getSubElements = " + getSubElements() );
////        System.err.println("el.getSubElements = " + el.getSubElements() );
////        
////        if (!getSubElements().equals(el.getSubElements()))
////            return false;
//        
//        System.err.println("same subelements");
//        
//        return true;
//    }
//    
//    public int hashCode() {
//        return 1; //!!!!!
//    }

}
