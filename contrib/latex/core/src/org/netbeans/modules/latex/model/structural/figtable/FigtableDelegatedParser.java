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
package org.netbeans.modules.latex.model.structural.figtable;

import java.util.Collection;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.DelegatedParser;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class FigtableDelegatedParser extends DelegatedParser {
    
    /** Creates a new instance of SectionDelegatedParser */
    public FigtableDelegatedParser() {
    }
    
    private int getType(BlockNode bnode) {
        Environment env = bnode.getEnvironment();
        int type = FigtableStructuralElement.UNKNOWN;
        
        if (env.hasAttribute("#figure-environment"))
            type = FigtableStructuralElement.FIGURE;
        
        if (env.hasAttribute("#table-environment"))
            type = FigtableStructuralElement.TABLE;
        
        return type;
    }
    
    public StructuralElement getElement(Node node, Collection/*<ParseError>*/ errors) {
        //Only for case that some malicious module marked some Command with our attributes ;-(.
        if (node instanceof BlockNode) {
            BlockNode bnode = (BlockNode) node;
            int type = getType(bnode);
            
            if (type == FigtableStructuralElement.UNKNOWN)
                return null;
            
            return new FigtableStructuralElement((BlockNode) node, type);
        } else
            return null;
    }
    
    public String[] getSupportedAttributes() {
        return new String[] {
            "#table-environment",
            "#figure-environment",

        };
    }
    
    public StructuralElement updateElement(Node node, Collection/*<ParseError>*/ errors, StructuralElement element) {
        if (!(element instanceof FigtableStructuralElement))
            throw new IllegalStateException("");
        
        ((FigtableStructuralElement) element).fireNameChanged();
        return element;
    }
    
    public Object getKey(Node node) {
        if (node instanceof BlockNode) {
            BlockNode bnode = (BlockNode) node;
            int type = getType(bnode);
            
            if (type == FigtableStructuralElement.UNKNOWN)
                return null;
            
            return new FigtableKey(type, bnode.getClass(), bnode.getStartingPosition(), bnode.getEndingPosition());
        } else
            return null;
    }

    private static class FigtableKey {
        private int type;
        private Class nodeClass;
        private SourcePosition start;
        private SourcePosition end;
//        private String name;
        
        public FigtableKey(int type, Class nodeClass, SourcePosition start, SourcePosition end/*, String name*/) {
            this.type = type;
            this.nodeClass = nodeClass;
            this.start = start;
            this.end = end;
//            this.name = name;
        }

        public boolean equals(Object o) {
            if (!getClass().equals(o.getClass()))
                return false;
            
            FigtableKey key = (FigtableKey) o;
            
            if (type != key.type)
                return false;
            
            if (!nodeClass.equals(key.nodeClass))
                return false;
            
            if (!start.equals(key.start))
                return false;
            
            if (!end.equals(key.end))
                return false;

            return true;
        }
        
        public int hashCode() {
            return 1; //just for testing!!!!
        }
    }

}
