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
package org.netbeans.modules.latex.model.structural.section;

import java.beans.IntrospectionException;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNode;
import org.openide.actions.OpenAction;
import org.openide.actions.PropertiesAction;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class SectionStructuralElementNode extends StructuralNode {
    
    private static String[] iconNames = new String[] {
        "chap_icon",
        "sec_icon",
        "ssec_icon",
        "sssec_icon",
        "para_icon",
        "spara_icon",
    };
    
    /** Creates a new instance of SectionStructuralElementNode */
    public SectionStructuralElementNode(StructuralElement el) throws IntrospectionException {
        super(el);
        
        assert el instanceof SectionStructuralElement;
        
        

        int type = ((SectionStructuralElement) el).getType();
        
        if (type > 0) {
            String iconBase = "org/netbeans/modules/latex/model/structural/impl/resources/" + iconNames[type - 1];
            
//                System.err.println("Setting iconBase=" + iconBase);
            this.setIconBase(iconBase);
        }
}
    
//    public SystemAction[] createActions() {
//        return new SystemAction[] {
//            SystemAction.get(OpenAction.class),
//            null,
//            SystemAction.get(PropertiesAction.class),
//        };
//    }
    
    public SourcePosition getOpeningPosition() {
        return ((SectionStructuralElement) getBean()).getNode().getStartingPosition();
    }
}
