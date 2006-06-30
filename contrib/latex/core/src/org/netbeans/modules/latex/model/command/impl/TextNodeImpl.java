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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.Position;

import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.TextNode;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.netbeans.modules.latex.test.TestCertificate;

/**
 *
 * @author Jan Lahoda
 */
public class TextNodeImpl extends NodeImpl implements TextNode {
    
    private List/*<Node>*/ children;
    
    /** Creates a new instance of TextNodeImpl */
    public TextNodeImpl(Node parent, NodeImpl previousCommandDefiningNode) {
        super(parent, previousCommandDefiningNode);
        
        children = new ArrayList();
    }
    
    public Node getChild(int index) {
        return (Node) children.get(index);
    }
    
    public int getChildrenCount() {
        return children.size();
    }
    
    public void addChild(Node c) {
        children.add(c);
    }
    
    public Iterator/*<Node>*/ getChildrenIterator() {
        return children.iterator();
    }
    
    protected void traverseImpl(TraverseHandler th) {
        int count = getChildrenCount();
        
        for (int cntr = 0; cntr < count; cntr++) {
            getChild(cntr).traverse(th);
        }
    }
    
    public void traverse(TraverseHandler th) {
        traverseImpl(th);
    }

    protected boolean isInChild(Object file, Position pos) {
        int count = getChildrenCount();
        
        for (int cntr = 0; cntr < count; cntr++) {
            if (isIn(file, pos, getChild(cntr)))
                return true;
        }
        
        return false;
    }
    
    public void dump(TestCertificate tc, PrintWriter pw) {
        pw.println("<TextNodeImpl>");
        dumpPositions(tc, pw);

        Iterator iter = getChildrenIterator();
        
        while (iter.hasNext()) {
            ((NodeImpl) iter.next()).dump(tc, pw);
        }
        
        pw.println("</TextNodeImpl>");
    }
    
}
