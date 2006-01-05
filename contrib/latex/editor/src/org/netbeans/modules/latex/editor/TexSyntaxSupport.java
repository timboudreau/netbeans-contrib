/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the DocSup module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.ext.ExtSyntaxSupport;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.Node;

/**
 *
 * @author Jan Lahoda
 */
public class TexSyntaxSupport extends ExtSyntaxSupport {
    public TexSyntaxSupport(BaseDocument doc) {
        super(doc);
    }
    
    public int[] findMatchingBlockX(int offset, boolean simpleSearch)
    throws BadLocationException {
        char bracketChar = getDocument().getChars(offset, 1)[0];
        //isBracket?:
        if ("([])".indexOf(bracketChar) != (-1))
            return super.findMatchingBlock(offset, simpleSearch);
        
        Object file = Utilities.getDefault().getFile(getDocument());
        LaTeXSource source = LaTeXSource.get(file);

        LaTeXSource.Lock lock = null;
        
        try {
            lock = source.lock(!simpleSearch);
            
            if (lock == null)
                return null;
            
            Node node = source.findNode(getDocument(), offset);
            
            if (node instanceof ArgumentNode) {
                node = ((ArgumentNode) node).getCommand();
            }
            
            if (node instanceof CommandNode) {
                CommandNode cnode = (CommandNode) node;
                Node        parent = cnode.getParent();
                
                if (parent instanceof BlockNode) {
                    BlockNode bnode = (BlockNode) parent;
                    CommandNode opossite = null;
                    
                    if (bnode.getBeginCommand() == cnode) {
                        opossite = bnode.getEndCommand();
                    } else {
                        if (bnode.getEndCommand() == cnode) {
                            opossite = bnode.getBeginCommand();
                        }
                    }
                    
                    if (opossite != null) {
                        return new int[] {
                            opossite.getStartingPosition().getOffsetValue(),
                            opossite.getEndingPosition().getOffsetValue()
                        };
                    }
                }
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        } finally {
            if (lock != null)
                source.unlock(lock);
        }

        if ("{}".indexOf(bracketChar) != (-1))
            return super.findMatchingBlock(offset, simpleSearch);

        return null; //nothing found ;-(.
    }
}
