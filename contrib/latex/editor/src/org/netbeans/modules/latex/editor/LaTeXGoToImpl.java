/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.modules.latex.model.LabelInfo;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.InputNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.Node;

/**
 *
 * @author Jan Lahoda
 */
public final class LaTeXGoToImpl {
    
    /** Creates a new instance of LaTeXGoToImpl */
    private LaTeXGoToImpl() {
    }
    
    private static LaTeXGoToImpl instance = null;
    
    public static synchronized LaTeXGoToImpl getDefault() {
        if (instance == null)
            instance = new LaTeXGoToImpl();
        
        return instance;
    }
    
    public Node getGoToNode(Document doc, int offset, boolean  doOpen) {
        LaTeXSource source   = Utilities.getDefault().getSource(doc);
        
        if (source == null)
            return null;
        
        LaTeXSource.Lock lock = null;
        
        try {
            lock = source.lock(false);
            
            if (lock == null)
                return null;
            
            Node        node     = source.findNode(doc, offset);
            
            if (node == null)
                return null;
            
            if (node instanceof ArgumentNode) {
                ArgumentNode anode = (ArgumentNode) node;
                CommandNode  cnode = anode.getCommand();
                
                if ("\\ref".equals(cnode.getCommand().getCommand())) {
                    if (doOpen)
                        openRef(source, anode);
                    
                    return anode;
                } else {
                    if (cnode.getCommand().isInputLike()) {
                        if (doOpen)
                            openInput(source, cnode);
                        
                        return anode;
                    }
                }
                
                //otherwise, test the parent command node:
                node = cnode;
            }
            
            if (node instanceof CommandNode) {
                Node parent = node.getParent();
                
                if (parent instanceof BlockNode) {
                    BlockNode bnode = (BlockNode) parent;
                    
                    if (bnode.getBeginCommand() == node) {
                        if (doOpen)
                            openAtCommand(source, bnode.getEndCommand());
                        
                        return node;
                    }
                    
                    if (bnode.getEndCommand() == node) {
                        if (doOpen)
                            openAtCommand(source, bnode.getBeginCommand());
                        
                        return node;
                    }
                }
            }
            
            return null;
        } catch (IOException e) {
            IllegalStateException exc = new IllegalStateException();
            
            ErrorManager.getDefault().annotate(exc, e);
            
            throw exc;
        } finally {
            if (lock != null)
                source.unlock(lock);
        }
    }
    
    private void openRef(LaTeXSource source, ArgumentNode anode) {
        String       label  = anode.getText().toString();
        List         labels = Utilities.getDefault().getLabels(source);
        
        for (Iterator i = labels.iterator(); i.hasNext(); ) {
            LabelInfo info = (LabelInfo) i.next();
            
            if (label.equals(info.getLabel()))
                Utilities.getDefault().openPosition(info.getStartingPosition());
        }
    }
    
    private void openInput(LaTeXSource source, CommandNode cnode) {
        Utilities.getDefault().openPosition(((InputNode) cnode).getContent().getStartingPosition());
    }
    
    private void openAtCommand(LaTeXSource source, CommandNode cnode) {
        if (cnode == null)
            return ;
        
        Utilities.getDefault().openPosition(cnode.getStartingPosition());
    }

}
