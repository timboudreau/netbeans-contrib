/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the DocSup module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
