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
package org.netbeans.modules.latex.model.structural.label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.structural.DelegatedParser;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class LabelDelegatedParser extends DelegatedParser {
    
    /** Creates a new instance of SectionDelegatedParser */
    public LabelDelegatedParser() {
    }
    
    public void reset() {
        //This would be better done at the end of the parsing, wouldn't it?
        captions.clear();
        captions.push("");
        label2Refs.clear();
    }
    
    private Stack captions = new Stack();
    private Map/*<String, List or LabelStructuralElement>*/ label2Refs = new HashMap();
    
    private void handleEnvCommand(CommandNode cnode) {
        Node parent = cnode.getParent();
        
        if (!(parent instanceof BlockNode))
            return ;
        
        BlockNode bnode = (BlockNode) parent;
        Environment env = bnode.getEnvironment();
        
        if (env != null && env.hasAttribute("#captionable")) {
            if (cnode.getCommand().hasAttribute("begin"))
                captions.push("");
            else
                captions.pop();
        }
    }
    
    private void createRefElement(ArgumentNode anode) {
        RefStructuralElement el = new RefStructuralElement(anode.getCommand(), anode.getText().toString());
        
        Object fromMap = label2Refs.get(el.getLabel());
        
        if (fromMap == null) {
            label2Refs.put(el.getLabel(), fromMap = new ArrayList());
        }
        
        if (fromMap instanceof List) {
            ((List) fromMap).add(el);
        } else {
            ((LabelStructuralElement) fromMap).addSubElement(el);
        }
    }
    
    private LabelStructuralElement createLabelElement(ArgumentNode anode) {
        LabelStructuralElement el = new LabelStructuralElement(anode.getCommand(), anode.getText().toString(), captions.peek().toString());
        
        Object fromMap = label2Refs.get(el.getLabel());
        
        if (fromMap != null) {
            if (!(fromMap instanceof List))
                //Some other label element with the same label. A small problem for user, ignore...
                return el;
            
            for (Iterator i = ((List) fromMap).iterator(); i.hasNext(); ) {
                RefStructuralElement refEl = (RefStructuralElement) i.next();
                
                el.addSubElement(refEl);
            }
        }
        
        label2Refs.put(el.getLabel(), el);
        
        return el;
    }
    
    public StructuralElement getElement(Node node, Collection/*<ParseError>*/ errors) {
        if (node instanceof CommandNode) {
            CommandNode cnode = (CommandNode) node;
            
            if (cnode.getCommand().hasAttribute("begin") || cnode.getCommand().hasAttribute("end")) {
                handleEnvCommand(cnode);
                
                return null;
            }
            
            return null;
        } else {
            if (node instanceof ArgumentNode) {
                ArgumentNode anode = (ArgumentNode) node;
                
                if (anode.getArgument().hasAttribute("#caption")) {
                    if (captions.size() > 0)
                        captions.pop();
                    
                    captions.push(anode.getText().toString());
                    return null;
                }
                
                if (anode.getArgument().hasAttribute("#label")) {
                    return createLabelElement(anode);
                }
                
                if (anode.getArgument().hasAttribute("#ref")) {
                    createRefElement(anode);
                    return null;
                }
            }
        }
            return null;
    }
    
    public String[] getSupportedAttributes() {
        return new String[] {
            "begin",
            "end",
            "#label",
            "#caption",
            "#ref"
        };
    }
    
}
