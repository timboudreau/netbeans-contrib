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
import javax.swing.text.Position;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandCollection;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.TextNode;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.netbeans.modules.latex.test.TestCertificate;

/**
 *
 * @author Jan Lahoda
 */
public class BlockNodeImpl extends NodeImpl implements BlockNode {
    
    public static final Environment NULL_ENVIRONMENT = new Environment("nullenvironments");
    
    private CommandNodeImpl begin;
    private CommandNodeImpl end;
    private TextNodeImpl    content;
    private Environment    environment;
    
    /** Creates a new instance of BlockNodeImpl */
    public BlockNodeImpl(Node parent, NodeImpl previousCommandDefiningNode, Environment env) {
        super(parent, previousCommandDefiningNode);
        this.environment = env;
    }
    
    public CommandNode getBeginCommand() {
        return begin;
    }
    
    public void setBeginCommand(CommandNodeImpl begin) {
        this.begin = begin;
        setStartingPosition(begin.getStartingPosition());
        begin.setParent(this);
    }
    
    public CommandNode getEndCommand() {
        return end;
    }
    
    public void setEndCommand(CommandNodeImpl end) {
        this.end = end;
        setEndingPosition(end.getEndingPosition());
        end.setParent(this);
    }
    
    public TextNode getContent() {
        return content;
    }
    
    public void setContent(TextNodeImpl content) {
        this.content = content;
    }
    
    public void traverse(TraverseHandler th) {
        th.blockStart(this);
        getBeginCommand().traverse(th);
        getContent().traverse(th);
        if (getEndCommand() != null)
            getEndCommand().traverse(th);
        th.blockEnd(this);
    }

    protected boolean isInChild(Object file, Position pos) {
        return    begin.isInChild(file, pos)
               || content.isInChild(file, pos)
               || (end != null && end.isInChild(file, pos));
    }
    
    public void dump(TestCertificate tc, PrintWriter pw) {
        pw.println("<BlockNodeImpl>");
        dumpPositions(tc, pw);

        pw.println("<begin>");
        ((NodeImpl) getBeginCommand()).dump(tc, pw);
        pw.println("</begin>");
        pw.println("<content>");
        ((NodeImpl) getContent()).dump(tc, pw);
        pw.println("</content>");
        pw.println("<end>");
        ((NodeImpl) getEndCommand()).dump(tc, pw);
        pw.println("</end>");
        pw.println("</BlockNodeImpl>");
    }
    
    public String getBlockName() {
        CommandNode start = getBeginCommand();
        
        if (start.getArgumentCount() > 0) {
            CharSequence fullName = start.getArgument(0).getFullText();
            
            if (fullName.charAt(0) == '{') {
                fullName = fullName.subSequence(1, fullName.length());
            }
            
            if (fullName.charAt(fullName.length() - 1) == '}') {
                fullName = fullName.subSequence(0, fullName.length() - 1);
            }
        
            return fullName.toString();
        }
        
        return "";
    }
    
    public String toString() {
        return "BlockNodeImpl[name=" + getBlockName() + ", begin=" + getBeginCommand() + ",end=" + getEndCommand() + "]";
    }
    
    public void setCommandCollection(CommandCollection commandCollection) {
        super.setCommandCollection(commandCollection);
    }
    
    public Environment getEnvironment() {
        return environment;
    }
    
    /**No attributes
     */
    public String getAttribute(String name) {
        return getEnvironment().getAttribute(name);
    }

    /**No attributes
     */
    public boolean hasAttribute(String name) {
        return getEnvironment().hasAttribute(name);
    }

}
