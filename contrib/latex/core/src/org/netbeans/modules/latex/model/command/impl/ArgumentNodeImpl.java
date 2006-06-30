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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.impl;

import java.io.PrintWriter;
import java.util.Iterator;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.netbeans.modules.latex.model.command.Command.Param;
import org.netbeans.modules.latex.test.TestCertificate;

/**
 *
 * @author Jan Lahoda
 */
public class ArgumentNodeImpl extends GroupNodeImpl implements ArgumentNode {
    
    private boolean present;
    private Param   param;
    
    /** Creates a new instance of ArgumentNodeImpl */
    public ArgumentNodeImpl(CommandNode parent, boolean present, NodeImpl previousCommandDefiningNode) {
        super(parent, previousCommandDefiningNode);
        
//        System.err.println("ArgumentNodeImpl construstor=" + this);
        
//        new Exception("ArgumentNodeImpl constructor, ihc=" + System.identityHashCode(this)).printStackTrace(System.err);
        
        this.present = present;
    }
    
    public boolean isPresent() {
        return present;
    }
    
    public boolean isValidEnum() {
        if (!getArgument().isEnumerable())
            return false;
        
        return getArgument().isValid(getText()) == Param.ENUM;
    }
    
    public Param getArgument() {
        return param;
    }
    
    public void setArgument(Param param) {
        this.param = param;
    }
    
    public CommandNode getCommand() {
        return (CommandNode) getParent();
    }

    public void traverse(TraverseHandler th) {
        if (th.argumentStart(this)) {
            super.traverseImpl(th);
        }
        
        th.argumentEnd(this);
    }

    public void dump(TestCertificate tc, PrintWriter pw) {
        pw.println("<ArgumentNodeImpl>");
        dumpPositions(tc, pw);

        Iterator iter = getChildrenIterator();
        
        while (iter.hasNext()) {
            ((NodeImpl) iter.next()).dump(tc, pw);
        }
        
        pw.println("</ArgumentNodeImpl>");
    }

    /**No attributes
     */
    public String getAttribute(String name) {
        return getArgument().getAttribute(name);
    }

    /**No attributes
     */
    public boolean hasAttribute(String name) {
        return getArgument().hasAttribute(name);
    }

}
