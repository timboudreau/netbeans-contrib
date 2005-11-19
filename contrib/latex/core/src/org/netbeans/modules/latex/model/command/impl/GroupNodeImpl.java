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
package org.netbeans.modules.latex.model.command.impl;

import org.netbeans.modules.latex.model.command.GroupNode;
import org.netbeans.modules.latex.model.command.Node;

/**
 *
 * @author Jan Lahoda
 */
public class GroupNodeImpl extends TextNodeImpl implements GroupNode {
    
    /** Creates a new instance of GroupNodeImpl */
    public GroupNodeImpl(Node parent, NodeImpl previousCommandDefiningNode) {
        super(parent, previousCommandDefiningNode);
    }

    public boolean hasAttribute(String name) {
        return "hide-surrounding-brackets".equals(name);
    }

    public String getAttribute(String name) {
        if ("hide-surrounding-brackets".equals(name))
            return "true";
        
        return null;
    }
    
}
