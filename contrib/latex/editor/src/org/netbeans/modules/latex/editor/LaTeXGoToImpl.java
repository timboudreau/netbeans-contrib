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
import org.netbeans.modules.latex.editor.AnalyseBib.BibRecord;
import org.netbeans.modules.latex.model.LabelInfo;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModel;
import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.InputNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.Node;
import org.openide.awt.StatusDisplayer;

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
    
    public int[] getGoToNode(Document doc, int offset, boolean  doOpen) {
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
                
                if (anode.hasAttribute("#ref")) {
                    if (doOpen)
                        openRef(source, anode);
                    
                    return getSpanForNode(anode);
                } else {
                    if (cnode.getCommand().isInputLike()) {
                        if (doOpen)
                            openInput(source, cnode);
                        
                        return getSpanForNode(anode);
                    } else {
                        if (anode.hasAttribute("#cite")) {
                            return handleCite(anode, offset, doOpen);
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
                            openAtCommand(source, bnode.getEndCommand());
                        
                        return getSpanForNode(node);
                    }
                    
                    if (bnode.getEndCommand() == node) {
                        if (doOpen)
                            openAtCommand(source, bnode.getBeginCommand());
                        
                        return getSpanForNode(node);
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
    
    private int[] handleCite(ArgumentNode anode, int offset, boolean  doOpen) {
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
                if (doOpen) {
                    List references = org.netbeans.modules.latex.editor.Utilities.getAllBibReferences(anode.getDocumentNode().getSource());
                    boolean     found = false;
                    
                    for (Iterator i = references.iterator(); i.hasNext(); ) {
                        BibRecord record = (BibRecord) i.next();
                        PublicationEntry entry = record.getEntry();
                        
                        if (parts[cntr].equals(entry.getTag())) {
                            Utilities.getDefault().openPosition(entry.getStartPosition());
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
