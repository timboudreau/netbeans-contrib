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
 * The Original Software is the LaTeX module.
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
import java.util.Iterator;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.napi.gsfret.source.CompilationController;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.LabelInfo;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.netbeans.modules.latex.model.command.ArgumentContainingNode;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.InputNode;
import org.netbeans.modules.latex.model.command.Node;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;

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
    
    public int[] getGoToNode(final Document doc, final int offset, final boolean  doOpen, final String[] tooltip) {
        Source source = Source.forDocument(doc);
        final int[][] result = new int[1][];
        
        try {
        source.runUserActionTask(new CancellableTask<CompilationController>() {
            public void cancel() {}
            public void run(CompilationController parameter) throws Exception {
                parameter.toPhase(Phase.RESOLVED);
                
                LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();
                Node        node     = lpr.getCommandUtilities().findNode(doc, offset);

                if (node == null)
                    return ;

                if (node instanceof ArgumentNode) {
                    ArgumentNode anode = (ArgumentNode) node;
                    ArgumentContainingNode  cnode = anode.getCommand();

                    if (anode.hasAttribute("#ref")) {
                        if (doOpen)
                            openRef(lpr, anode);

                        if (tooltip != null) {
                            LabelInfo ref = findRef(lpr, anode);
                            
                            if (ref == null) {
                                return ;
                            }
                            
                            tooltip[0] = ref.getCaption();
                        }
                        
                        result[0] = getSpanForNode(anode);

                        return ;
                    } else {
                        if (cnode instanceof CommandNode && ((CommandNode) cnode).getCommand().isInputLike()) {
                            if (doOpen)
                                openInput((CommandNode) cnode);

                            result[0] = getSpanForNode(anode);
                            return ;
                        } else {
                            if (anode.hasAttribute("#cite")) {
                                result[0] = handleCite(lpr, anode, offset, doOpen, tooltip);
                                return ;
                            }
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
                                openAtCommand(bnode.getEndCommand());

                            result[0] = getSpanForNode(node);
                            return ;
                        }

                        if (bnode.getEndCommand() == node) {
                            if (doOpen)
                                openAtCommand(bnode.getBeginCommand());

                            result[0] = getSpanForNode(node);
                            return ;
                        }
                    }
                }
            }
        }, true);
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
        
        return result[0];
    }
    
    private LabelInfo findRef(LaTeXParserResult lpr, ArgumentNode anode) {
        String label = anode.getText().toString();
        List labels = Utilities.getDefault().getLabels(lpr);

        for (Iterator i = labels.iterator(); i.hasNext();) {
            LabelInfo info = (LabelInfo) i.next();

            if (label.equals(info.getLabel())) {
                return info;
            }
        }
        
        return null;
    }
    
    private void openRef(LaTeXParserResult lpr, ArgumentNode anode) {
        LabelInfo ref = findRef(lpr, anode);
        
        if (ref != null) {
            Utilities.getDefault().openPosition(ref.getStartingPosition());
        }
    }
    
    private void openInput(CommandNode cnode) {
        Utilities.getDefault().openPosition(((InputNode) cnode).getContent().getStartingPosition());
    }
    
    private void openAtCommand(CommandNode cnode) {
        if (cnode == null)
            return ;
        
        Utilities.getDefault().openPosition(cnode.getStartingPosition());
    }
    
    private int[] handleCite(LaTeXParserResult lpr, ArgumentNode anode, int offset, boolean  doOpen, String[] tooltip) {
        int start = anode.getStartingPosition().getOffsetValue();
        int end   = anode.getEndingPosition().getOffsetValue();
//                            int[] fullArgSpan = getSpanForNode(anode);
        String content = anode.getFullText().toString();
        
        if (anode.getArgument().getType() != Command.Param.FREE) {
            start++;
            end--;
            content = content.substring(1, content.length() - 1);
        }
        
        String[] parts = content.split(",");
        int currentStart = start;
        
        for (int cntr = 0; cntr < parts.length; cntr++) {
            if (currentStart <= offset && offset <= currentStart + parts[cntr].length()) {
                if (doOpen || tooltip != null) {
                    List<? extends PublicationEntry> references = Utilities.getDefault().getAllBibReferences(lpr);
                    boolean     found = false;
                    
                    for (PublicationEntry entry : references) {
                        if (parts[cntr].equals(entry.getTag())) {
                            if (doOpen) {
                                Utilities.getDefault().openPosition(entry.getStartPosition());
                            }
                            
                            if (tooltip != null) {
                                tooltip[0] = entry.getAuthor() + ":" + entry.getTitle();
                            }
                            
                            found = true;
                            
                            break;
                        }
                        
                    }
                    
                    if (!found) {
                        StatusDisplayer.getDefault().setStatusText("Cannot open BiBTeX entry: " + parts[cntr]);
                    }
                }
                
                return new int[] {currentStart, currentStart + parts[cntr].length()};
            }
            
            currentStart += parts[cntr].length() + 1;
        }
        
        return null;
    }

    private int[] getSpanForNode(Node node) {
        int[] result = new int[] {node.getStartingPosition().getOffsetValue(), node.getEndingPosition().getOffsetValue()};
        
        if (node instanceof ArgumentNode && ((ArgumentNode) node).getArgument().getType() != Command.Param.FREE) {
            result[0]++;
            result[1]--;
        }
        
        return result;
    }
}
