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

import java.util.Collection;
import java.util.Map;
import org.netbeans.modules.latex.model.command.Node;

/**SPI
 *
 * @author Jan Lahoda
 */
public abstract class DelegatedParser {
    
    /** Creates a new instance of DelegatedParser */
    public DelegatedParser() {
    }
    
    public abstract String[] getSupportedAttributes();
    
    public abstract StructuralElement getElement(Node node, Collection/*<ParseError>*/ errors);
    
    public void reset() {}
    
    public StructuralElement updateElement(Node node, Collection/*<ParseError>*/ errors, StructuralElement element) {
        return getElement(node, errors);
    }
    
    public Object getKey(Node node) {
        return null;
    }
}
