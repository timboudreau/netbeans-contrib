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
package org.netbeans.modules.latex.model.command;

/**
 *
 * @author Jan Lahoda
 */
public abstract class TraverseHandler {
    
    /** Creates a new instance of TraverseHandler */
    public TraverseHandler() {
    }
    
    public abstract boolean commandStart(CommandNode node);
    public abstract void commandEnd(CommandNode node);
    public abstract boolean argumentStart(ArgumentNode node);
    public abstract void argumentEnd(ArgumentNode node);
    public abstract boolean blockStart(BlockNode node);
    public abstract void blockEnd(BlockNode node);
//    public abstract void textStart(TextNode node);
//    public abstract void textEnd(TextNode node);
}
