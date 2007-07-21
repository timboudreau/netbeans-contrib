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
 * The Original Software is the DocSup module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
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
import org.netbeans.modules.latex.model.command.Node;

/**
 *
 * @author Jan Lahoda
 */
public class TexSyntaxSupport extends ExtSyntaxSupport {
    public TexSyntaxSupport(BaseDocument doc) {
        super(doc);
    }
    
    //XXX:
//    public int[] findMatchingBlockX(int offset, boolean simpleSearch)
//    throws BadLocationException {
//        char bracketChar = getDocument().getChars(offset, 1)[0];
//        //isBracket?:
//        if ("([])".indexOf(bracketChar) != (-1))
//            return super.findMatchingBlock(offset, simpleSearch);
//        
//        Object file = Utilities.getDefault().getFile(getDocument());
//        LaTeXSource source = LaTeXSource.get(file);
//
//        LaTeXSource.Lock lock = null;
//        
//        try {
//            lock = source.lock(!simpleSearch);
//            
//            if (lock == null)
//                return null;
//            
//            Node node = source.findNode(getDocument(), offset);
//            
//            if (node instanceof ArgumentNode) {
//                node = ((ArgumentNode) node).getCommand();
//            }
//            
//            if (node instanceof CommandNode) {
//                CommandNode cnode = (CommandNode) node;
//                Node        parent = cnode.getParent();
//                
//                if (parent instanceof BlockNode) {
//                    BlockNode bnode = (BlockNode) parent;
//                    CommandNode opossite = null;
//                    
//                    if (bnode.getBeginCommand() == cnode) {
//                        opossite = bnode.getEndCommand();
//                    } else {
//                        if (bnode.getEndCommand() == cnode) {
//                            opossite = bnode.getBeginCommand();
//                        }
//                    }
//                    
//                    if (opossite != null) {
//                        return new int[] {
//                            opossite.getStartingPosition().getOffsetValue(),
//                            opossite.getEndingPosition().getOffsetValue()
//                        };
//                    }
//                }
//            }
//        } catch (IOException e) {
//            ErrorManager.getDefault().notify(e);
//        } finally {
//            if (lock != null)
//                source.unlock(lock);
//        }
//
//        if ("{}".indexOf(bracketChar) != (-1))
//            return super.findMatchingBlock(offset, simpleSearch);
//
//        return null; //nothing found ;-(.
//    }
}
