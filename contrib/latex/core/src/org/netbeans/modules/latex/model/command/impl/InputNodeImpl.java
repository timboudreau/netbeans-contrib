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
import org.netbeans.modules.latex.model.command.*;
import org.netbeans.modules.latex.test.TestCertificate;

/**
 *
 * @author Jan Lahoda
 */
public class InputNodeImpl extends CommandNodeImpl implements InputNode {
    
    private TextNode content;
    
    /** Creates a new instance of InputNodeImpl */
    public InputNodeImpl(Node parent, Command command, NodeImpl previousCommandDefiningNode) {
        super(parent, command, previousCommandDefiningNode);
    }
    
    public void traverse(TraverseHandler th) {
        super.traverse(th);
        
        TextNode content = getContent();
        
        if (content != null)
            content.traverse(th);
    }
    
    public TextNode getContent() {
        return content;
    }
    
    public void setContent(TextNode content) {
        this.content = content;
    }
    
    public void dump(TestCertificate tc, PrintWriter pw) {
        pw.println("<InputNodeImpl>");
        dumpPositions(tc, pw);
        
        pw.println("<command>");
        pw.println(getCommand());
        pw.println("</command>");
        
        for (int cntr = 0; cntr < getArgumentCount(); cntr++) {
            ((NodeImpl) getArgument(cntr)).dump(tc, pw);
        }
        
        pw.println("<content>");
        
        ((NodeImpl) getContent()).dump(tc, pw);
        
        pw.println("</content>");
        
        pw.println("<InputNodeImpl>");
    }
    
}
