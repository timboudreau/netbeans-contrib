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

}
