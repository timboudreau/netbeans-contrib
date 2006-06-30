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
