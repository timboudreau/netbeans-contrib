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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
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

    private List<StructuralElement> labels;

    public MainStructuralElement() {
        labels = new ArrayList<StructuralElement>();
    }
    
    public SystemAction[] createActions() {
        return new SystemAction[0];
    }
    
    public int getPriority() {
        return -1;
    }
    
    public synchronized List<? extends StructuralElement> getLabels() {
        //Defense copy (against concurrent modifications):
        return new ArrayList<StructuralElement>(labels);
    }
    
    public synchronized void addLabel(LabelStructuralElement el) {
        labels.add(el);
    }
    
    public synchronized void clearLabels() {
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
