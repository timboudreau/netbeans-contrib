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
package org.netbeans.modules.latex.model.structural.section;

import java.util.Collection;
import java.util.Map;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.DelegatedParser;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class SectionDelegatedParser extends DelegatedParser {
    
    /** Creates a new instance of SectionDelegatedParser */
    public SectionDelegatedParser() {
    }
    
    private int getType(CommandNode node) {
        int type = getTypeForName(((CommandNode) node).getCommand().getCommand());
        
        if (type == (-1)) {
            throw new IllegalStateException("");
        }
        
        return type;
    }
    
    private boolean accept(CommandNode node) {
        int type = getTypeForName(((CommandNode) node).getCommand().getCommand());
        
        return type != (-1);
    }
    
    public StructuralElement getElement(Node node) {
        //Only for case that some malicious module marked some Environment with our attributes ;-(.
        if (node instanceof CommandNode) {
            CommandNode cnode = (CommandNode) node;
            
            if (!accept(cnode))
                return null;
            
            int type = getType(cnode);
            
            return new SectionStructuralElement(cnode, 1000*(type + 1), type);
        } else
            return null;
    }
    
    public String[] getSupportedAttributes() {
        return new String[] {"#section-command"};
    }
    
    private static String[] sectionNames = new String[] {
        "\\chapter",
        "\\section",
        "\\subsection",
        "\\subsubsection",
        "\\paragraph",
        "\\subparagraph"
    };
    
    /**Starting from 1.*/
    public static int getTypeForName(String name) {
        if (name.charAt(name.length() - 1) == '*') {
            name = name.substring(0, name.length() - 2);
            //            System.err.println("getTypeForName: name=" + name);
        }
        
        for (int cntr = 0; cntr < sectionNames.length; cntr++) {
            if (sectionNames[cntr].equals(name))
                return cntr + 1;
        }
        
        return (-1);
    }
    
    public static String getNameForType(int type) {
        return sectionNames[type - 1];
    }

    public StructuralElement updateElement(Node node, Collection/*<ParseError>*/ errors, StructuralElement element) {
        if (!(element instanceof SectionStructuralElement))
            throw new IllegalStateException("");
        
        ((SectionStructuralElement) element).fireNameChanged();
        return element;
    }
    
    public Object getKey(Node node) {
        if (node instanceof CommandNode) {
            CommandNode cnode = (CommandNode) node;
            
            if (!accept(cnode))
                return null;
            
            return new SectionKey(getType(cnode), cnode.getClass(), cnode.getStartingPosition(), cnode.getEndingPosition());
        } else
            return null;
    }

    private static class SectionKey {
        private int type;
        private Class nodeClass;
        private SourcePosition start;
        private SourcePosition end;
//        private String name;
        
        public SectionKey(int type, Class nodeClass, SourcePosition start, SourcePosition end/*, String name*/) {
            this.type = type;
            this.nodeClass = nodeClass;
            this.start = start;
            this.end = end;
//            this.name = name;
        }

        public boolean equals(Object o) {
            if (!getClass().equals(o.getClass()))
                return false;
            
            SectionKey key = (SectionKey) o;
            
            if (type != key.type)
                return false;
            
            if (!nodeClass.equals(key.nodeClass))
                return false;
            
            if (!start.equals(key.start))
                return false;
            
            if (!end.equals(key.end))
                return false;

//            if (!name.equals(key.name))
//                return false;
//            
            return true;
        }
        
        public int hashCode() {
            return 1; //just for testing!!!!
        }
    }
}
