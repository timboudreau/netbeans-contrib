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
package org.netbeans.modules.latex.model.structural.label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.netbeans.modules.latex.model.command.ArgumentContainingNode;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.structural.DelegatedParser;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.filesystems.FileObject;
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
        errors.clear();
    }
    
    private Stack captions = new Stack();
    private Map/*<String, List or LabelStructuralElement>*/ label2Refs = new HashMap();
    private Map<String, ErrorDescription> errors = new HashMap<String, ErrorDescription>();
    
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
    
    private void createRefElement(ArgumentNode anode, CommandNode cmd) {
        RefStructuralElement el = new RefStructuralElement(cmd, anode.getText().toString());
        
        Object fromMap = label2Refs.get(el.getLabel());
        
        if (fromMap == null) {
            label2Refs.put(el.getLabel(), fromMap = new ArrayList());
        }
        
        if (fromMap instanceof List) {
            ((List) fromMap).add(el);
            errors.put(el.getLabel(), ErrorDescriptionFactory.createErrorDescription(Severity.WARNING, "Undefined label: " + el.getLabel(), (FileObject) anode.getStartingPosition().getFile(), anode.getStartingPosition().getOffsetValue(), anode.getEndingPosition().getOffsetValue()));
        } else {
            ((LabelStructuralElement) fromMap).addSubElement(el);
        }
    }
    
    private LabelStructuralElement createLabelElement(ArgumentNode anode, CommandNode cmd) {
        LabelStructuralElement el = new LabelStructuralElement(cmd, anode.getText().toString(), captions.peek().toString());
        
        Object fromMap = label2Refs.get(el.getLabel());
        
        if (fromMap != null) {
            if (!(fromMap instanceof List))
                //Some other label element with the same label. A small problem for user, ignore...
                return el;
            
            for (Iterator i = ((List) fromMap).iterator(); i.hasNext(); ) {
                RefStructuralElement refEl = (RefStructuralElement) i.next();
                
                el.addSubElement(refEl);
            }
            
            errors.remove(el.getLabel());
        }
        
        label2Refs.put(el.getLabel(), el);
        
        return el;
    }
    
    public StructuralElement getElement(Node node) {
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
                
                ArgumentContainingNode arg = anode.getCommand();
                
                if (arg instanceof CommandNode) {
                    CommandNode cmd = (CommandNode) arg;
                    
                    if (anode.getArgument().hasAttribute("#label")) {
                        return createLabelElement(anode, cmd);
                    }
                    
                    if (anode.getArgument().hasAttribute("#ref")) {
                        createRefElement(anode, cmd);
                        return null;
                    }
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
    
    public Collection<ErrorDescription> getErrors() {
        return errors.values();
    }
    
}
