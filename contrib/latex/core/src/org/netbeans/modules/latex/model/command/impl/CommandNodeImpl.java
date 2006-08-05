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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.modules.latex.model.Utilities;

import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.Command.Param;
import org.netbeans.modules.latex.model.command.CommandCollection;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.test.TestCertificate;
import org.openide.ErrorManager;

/**
 *
 * @author Jan Lahoda
 */
public class CommandNodeImpl extends ArgumentContainingNodeImpl implements CommandNode {
    
    public static final Command NULL_COMMAND = new Command("\\nullcommand: ");
    private Command                   command;
    
    
    /** Creates a new instance of CommandNodeImpl */
    public CommandNodeImpl(Node parent, Command command, NodeImpl previousCommandDefiningNode) {
        super(parent, previousCommandDefiningNode, command);
        this.command = command;
    }
    
    public Command getCommand() {
        return command;
    }
    
    public boolean isComplete() {
        return getArgumentCount() == command.getArgumentCount();
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("CommandNodeImpl[");
        sb.append("start=");
        sb.append(getStartingPosition());
        sb.append(", end=");
        sb.append(getEndingPosition());
        sb.append(", command=");
        sb.append(getCommand().getCommand());
        sb.append("]");
        
        return sb.toString();
    }
    
    public void traverse(TraverseHandler th) {
        if (th.commandStart(this)) {
            int count = getArgumentCount();
            
            for (int cntr = 0; cntr < count; cntr++) {
                getArgument(cntr).traverse(th);
            }
        } else {
//            System.err.println("commandStart returned false, th=" + th + ", this=" + this);
        }
        
        th.commandEnd(this);
    }
    
    public boolean isValid() {
        if (getCommand() == NULL_COMMAND)
            return false;
        
        //TODO: Check arguments;
        //TODO: Check arguments validity:
        
        return true;
    }

    public void setCommand(LaTeXSource.WriteLock lock, Command command) {
        //Validation of the lock is missing.
        SourcePosition start = getStartingPosition();
        
        try {
            Document doc = Utilities.getDefault().openDocument(start.getFile());

            doc.remove(start.getOffsetValue(), getCommand().getCommand().length());
            doc.insertString(start.getOffsetValue(), command.getCommand(), null);
        } catch (BadLocationException e) {
            IllegalStateException ex = new IllegalStateException();
            
            ErrorManager.getDefault().annotate(ex, e);
            
            throw ex;
        } catch (IOException e) {
            IllegalStateException ex = new IllegalStateException();
            
            ErrorManager.getDefault().annotate(ex, e);
            
            throw ex;
        }
        
        this.command = command;
    }

    protected boolean isInChild(Object file, Position pos) {
        int count = getArgumentCount();
        
        for (int cntr = 0; cntr < count; cntr++) {
            if (isIn(file, pos, getArgument(cntr)))
                return true;
        }
        
        return false;
    }
    
    public void dump(TestCertificate tc, PrintWriter pw) {
        pw.print("<CommandNodeImpl ");
        pw.print("name=\"");
        pw.print(getCommand());
        pw.println("\">");
        
        dumpPositions(tc, pw);
        
        for (int cntr = 0; cntr < getArgumentCount(); cntr++) {
            ((NodeImpl) getArgument(cntr)).dump(tc, pw);
        }
        
        pw.println("</CommandNodeImpl>");
    }

    public void setCommandCollection(CommandCollection commandCollection) {
        super.setCommandCollection(commandCollection);
    }
    
    /**No attributes
     */
    public String getAttribute(String name) {
        return getCommand().getAttribute(name);
    }

    /**No attributes
     */
    public boolean hasAttribute(String name) {
        return getCommand().hasAttribute(name);
    }

}
