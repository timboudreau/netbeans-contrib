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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.latex.model.command.impl;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.CommandUtilities;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.InputNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.command.TextNode;

/**
 *
 * @author Jan Lahoda
 */
public class CommandUtilitiesImpl implements CommandUtilities {

    private DocumentNode document;
    
    public CommandUtilitiesImpl(DocumentNode document) {
        this.document = document;
    }

    private boolean isIn(SourcePosition pos, SourcePosition start, SourcePosition end) {
        return comparePositions(pos, start, end) == 0;
    }
    
    
    /**Compares positions.
     *
     * @return -1 if and only if pos < start
     *          0 if and only if start <= pos <= end.
     *          1 if and only if end < pos
     */
    private int comparePositions(SourcePosition pos, SourcePosition start, SourcePosition end) {
        assert Utilities.getDefault().compareFiles(pos.getFile(), start.getFile()) && Utilities.getDefault().compareFiles(pos.getFile(), end.getFile()) : "pos.getFile()=" + pos.getFile() + ", start.getFile()=" + start.getFile() + ", end.getFile()=" + end.getFile();
        
        //TODO: some kind of locking:
        int posOff = pos.getOffsetValue();
        int startOff = start.getOffsetValue();
        int endOff = end.getOffsetValue();
        
        if (posOff < startOff)
            return (-1);
        
        if (endOff <= posOff)
            return 1;
        
        return 0;
//        boolean result = startOff <= posOff && posOff <= endOff;
//        
////        System.err.println("pos=" + pos + ", start=" + start + ", end=" + end + ", result=" + result);
//        
//        return result;
    }

    /**{@inheritDoc}
     */
    public Node findNode(SourcePosition pos) throws IOException {
        List         queue    = new LinkedList();
        Node         found    = null;
        
        queue.add(document);
        
        while (queue.size() > 0) {
            Node node = (Node) queue.remove(0);
            
            if (node == null)
                continue;
            
            SourcePosition start = node.getStartingPosition();
            SourcePosition end   = node.getEndingPosition();
            
            assert Utilities.getDefault().compareFiles(start.getFile(), end.getFile());
            
            if (Utilities.getDefault().compareFiles(pos.getFile(), start.getFile())) {
                found = node;
                break;
            }
            
            if (node instanceof CommandNode) {
                CommandNode cnode = (CommandNode) node;
                int count = cnode.getArgumentCount();
                
                for (int cntr = 0; cntr < count; cntr++) {
                    queue.add(cnode.getArgument(cntr));
                }
                
                if (node instanceof InputNode) {
                    queue.add(((InputNode) node).getContent());
                }
            } else {
                if (node instanceof TextNode) {
                    TextNode tn    = (TextNode) node;
                    int      count = tn.getChildrenCount();
                    
                    for (int cntr = 0; cntr < count; cntr++) {
                        queue.add(tn.getChild(cntr));
                    }
                } else {
                    if (node instanceof BlockNode) {
                        BlockNode bn = (BlockNode) node;
                        
                        queue.add(bn.getBeginCommand());
                        queue.add(bn.getContent());
                        queue.add(bn.getEndCommand());
                    } else {
                        //Nothing to add.
                    }
                }
            }
        }
        
        if (found == null) {
            return null;
        }
        
        MAIN_LOOP: while (true) {
            //                    System.err.println("found = " + found );
            if (found instanceof CommandNode) {
                CommandNode cfound = (CommandNode) found;
                int         count  = cfound.getArgumentCount();
                
                for (int cntr = 0; cntr < count; cntr++) {
                    ArgumentNode an = cfound.getArgument(cntr);
                    
                    if (isIn(pos, an.getStartingPosition(), an.getEndingPosition())) {
                        //                                System.err.println("2");
                        found = an;
                        continue MAIN_LOOP;
                    }
                }
            } else {
                if (found instanceof TextNode) {
                    TextNode tfound = (TextNode) found;
                    int      count  = tfound.getChildrenCount();
                    
                    if (count > 0) {
                    int      lower   = 0;
//                    System.err.println("count = " + count );
                    int      higher  = count - 1;
                    boolean  last    = false;
                    boolean  findIterativelly = false;
                    
                    while (!last && lower != higher) {
//                        System.err.println("lower = " + lower );
//                        System.err.println("higher = " + higher );
                        int      toTest = lower + (higher - lower) / 2;
//                        System.err.println("toTest = " + toTest );
                        Node     n = tfound.getChild(toTest);
                        boolean  canCompare = Utilities.getDefault().compareFiles(pos.getFile(), n.getStartingPosition().getFile()) && Utilities.getDefault().compareFiles(pos.getFile(), n.getEndingPosition().getFile());
                        
                        if (!canCompare) {
                            //cannot be found by binary division:
                            findIterativelly = true;
                            break;
                        }
                        
                        int      result = comparePositions(pos, n.getStartingPosition(), n.getEndingPosition());
//                        System.err.println("result=" + result);
                        if (result == 0) {
                            found = n;
//                            System.err.println("found=" + found);
                            continue MAIN_LOOP;
                        }
                        
                        if ((higher - lower) == 1)
                            last = true;
                        
                        if (result > 0)
                            lower = toTest + 1;
                        
                        if (result < 0)
                            higher = toTest - 1;
                    }
                    
//                    System.err.println("done, lower=" + lower);
                    if (!findIterativelly) {
                        Node n = tfound.getChild(lower);
                        
                        if (isIn(pos, n.getStartingPosition(), n.getEndingPosition())) {
                            found = n;
                            continue MAIN_LOOP;
                        }
                    }
                    
                    for (int childNum = 0; childNum < count; childNum++) {
                        Node n = tfound.getChild(childNum);
                        boolean  canCompare = Utilities.getDefault().compareFiles(pos.getFile(), n.getStartingPosition().getFile()) && Utilities.getDefault().compareFiles(pos.getFile(), n.getEndingPosition().getFile());
                        
                        if (canCompare && comparePositions(pos, n.getStartingPosition(), n.getEndingPosition()) == 0) {
                            found = n;
                            continue MAIN_LOOP;
                        }
                    }
                    }
                } else {
                    if (found instanceof BlockNode) {
                        BlockNode bn = (BlockNode) found;
                        
                        Node n = bn.getBeginCommand();
                        
                        if (isIn(pos, n.getStartingPosition(), n.getEndingPosition())) {
                            found = n;
                            continue MAIN_LOOP;
                        }
                        
                        n = bn.getEndCommand();
                        
                        if (n != null && isIn(pos, n.getStartingPosition(), n.getEndingPosition())) {
                            found = n;
                            continue MAIN_LOOP;
                        }

                        n = bn.getContent();
                        
                        if (isIn(pos, n.getStartingPosition(), n.getEndingPosition())) {
                            found = n;
                            continue MAIN_LOOP;
                        }
                        
                    } else {
                        //...
                    }
                }
            }
            
            return found;
        }
    }
    
    public Node findNode(Document doc, int offset) throws IOException {
        return findNode(new SourcePosition(Utilities.getDefault().getFile(doc), doc, offset));
    }

    public List getCommands(SourcePosition pos) throws IOException {
        return ((NodeImpl) findNode(pos)).getCommands(false);
    }
    
    public Command getCommand(SourcePosition pos, String name) throws IOException {
        return ((NodeImpl) findNode(pos)).getCommand(name, false);
    }

    public List getEnvironments(SourcePosition pos) throws IOException {
        return ((NodeImpl) findNode(pos)).getEnvironments(false);
    }
    
    public Environment getEnvironment(SourcePosition pos, String name) throws IOException {
        return ((NodeImpl) findNode(pos)).getEnvironment(name, false);
    }
    
}
