/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.model.command.impl;

import java.io.PrintWriter;
import java.util.Iterator;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.ParagraphNode;
import org.netbeans.modules.latex.test.TestCertificate;

/**
 *
 * @author Jan Lahoda
 */
public class ParagraphNodeImpl extends TextNodeImpl implements ParagraphNode {
    
    /** Creates a new instance of ParagraphNodeImpl */
    public ParagraphNodeImpl(Node parent, NodeImpl previousCommandDefiningNode) {
        super(parent, previousCommandDefiningNode);
    }
    
    public void dump(TestCertificate tc, PrintWriter pw) {
        pw.println("<ParagraphNodeImpl>");
        dumpPositions(tc, pw);

        Iterator iter = getChildrenIterator();
        
        while (iter.hasNext()) {
            ((NodeImpl) iter.next()).dump(tc, pw);
        }
        
        pw.println("</ParagraphNodeImpl>");
    }
    
}
