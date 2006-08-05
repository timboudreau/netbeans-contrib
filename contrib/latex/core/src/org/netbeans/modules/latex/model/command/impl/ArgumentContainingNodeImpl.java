/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.model.command.impl;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.latex.model.command.ArgumentContainingNode;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.NamedAttributableWithArguments;
import org.netbeans.modules.latex.model.command.Node;

/**
 *
 * @author Jan Lahoda
 */
public abstract class ArgumentContainingNodeImpl extends NodeImpl implements ArgumentContainingNode {

    private NamedAttributableWithArguments command;
    private List<ArgumentNode>    arguments;
    
    public ArgumentContainingNodeImpl(Node parent, NodeImpl previousCommandDefiningNode, NamedAttributableWithArguments command) {
        super(parent, previousCommandDefiningNode);
        this.command = command;
        arguments    = new ArrayList<ArgumentNode>();
    }

    public ArgumentNode getArgument(int index) {
        return (ArgumentNode) arguments.get(index);
    }
    
    public int getArgumentCount() {
        return arguments.size();
    }
    
    public void putArgument(int index, ArgumentNode arg) {
        while (arguments.size() < index) {
            ArgumentNodeImpl an = new ArgumentNodeImpl(this, false, ((NodeImpl) arg).getPreviousCommandDefiningNode());
            
            an.setArgument(command.getArgument(arguments.size()));
            an.setStartingPosition(arg.getStartingPosition());
            an.setEndingPosition(arg.getStartingPosition());
            
            arguments.add(an);
        }
        
        if (arguments.size() == index) {
            arguments.add(index, arg);
        } else {
            arguments.set(index, arg);
        }
    }
    
    public void addArgument(ArgumentNode arg) {
        putArgument(arguments.size(), arg);
    }
    
    public NamedAttributableWithArguments getArgumentsSpecification() {
        return command;
    }
    
}
