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
