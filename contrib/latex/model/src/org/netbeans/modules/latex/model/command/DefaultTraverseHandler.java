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
public class DefaultTraverseHandler extends TraverseHandler {
    
    /** Creates a new instance of DefaultTraverseHandler */
    public DefaultTraverseHandler() {
    }
    
    public void argumentEnd(ArgumentNode node) {
    }
    
    public boolean argumentStart(ArgumentNode node) {
        return true;
    }
    
    public void commandEnd(CommandNode node) {
    }
    
    public boolean commandStart(CommandNode node) {
        return true;
    }
    
    public void blockEnd(BlockNode node) {
    }
    
    public boolean blockStart(BlockNode node) {
        return true;
    }
    
//    public void textEnd(TextNode node) {
//    }
//    
//    public void textStart(TextNode node) {
//    }
    
}
