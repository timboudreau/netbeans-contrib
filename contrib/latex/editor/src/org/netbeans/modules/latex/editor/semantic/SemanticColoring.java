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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.editor.semantic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.retouche.source.CompilationInfo;
import org.netbeans.modules.latex.editor.ColoringEvaluator;
import org.netbeans.modules.latex.editor.TexColoringNames;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.command.ArgumentContainingNode;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.MathNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.netbeans.modules.latex.model.lexer.TexTokenId;
import org.netbeans.spi.editor.highlighting.support.PositionsBag;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class SemanticColoring implements CancellableTask<CompilationInfo> {

    private AtomicBoolean cancelled = new AtomicBoolean();
    private FileObject file;
    
    public SemanticColoring(FileObject file) {
        this.file = file;
    }

    public void cancel() {
        cancelled.set(true);
    }

    public Document getDocument() {
        try {
            DataObject d = DataObject.find(file);
            EditorCookie ec = d.getCookie(EditorCookie.class);
            
            if (ec == null)
                return null;
            
            return ec.getDocument();
        } catch (IOException e) {
            Logger.getLogger(SemanticColoring.class.getName()).log(Level.FINE, "SemanticColoring: Cannot find DataObject for file: " + FileUtil.getFileDisplayName(file), e);
            return null;
        }
    }
    
    public void run(CompilationInfo parameter) throws Exception {
        cancelled.set(false);
        
        final Document document = getDocument();
        
        if (document == null) {
            return ;
        }
        
        DocumentNode dn = ((LaTeXParserResult) parameter.getParserResult()).getDocument();
        final Map<Token, List<AttributeSet>> token2Attributes = new HashMap<Token, List<AttributeSet>>();
        
        dn.traverse(new TraverseHandler() {
            public boolean commandStart(CommandNode node) {
                if (cancelled.get()) {
                    return false;
                }
                
                if (node.getStartingPosition().getDocument() != document)
                    return true;
                
                try {
                    Token cmd = (Token) node.getNodeTokens().iterator().next();
                    if (node.isValid())
                        add(token2Attributes, cmd, getColoringForName(TexColoringNames.COMMAND_CORRECT));
                    else
                        add(token2Attributes, cmd, getColoringForName(TexColoringNames.COMMAND_INCORRECT));
                } catch (IOException e) {
                    Exceptions.printStackTrace(e);
                }
                
                return true;
            }

            public void commandEnd(CommandNode node) {}

            public boolean argumentStart(ArgumentNode node) {
                if (cancelled.get()) {
                    return false;
                }
                
                if (node.getStartingPosition().getDocument() != document)
                    return true;
                
                AttributeSet attrs = null;
                
                if (node.getArgument().hasAttribute(Command.Param.ATTR_NO_PARSE)) {
                    attrs = getColoringForName(TexColoringNames.DEFINITION);
                } else {
                    if (node.getArgument().isEnumerable()) {
                        if (node.isValidEnum()) {
                            attrs = getColoringForName(TexColoringNames.ENUM_ARG_CORRECT);
                        } else {
                            attrs = getColoringForName(TexColoringNames.ENUM_ARG_INCORRECT);
                        }
                    } else {
                        ArgumentContainingNode cnode = node.getCommand();

                        if (cnode instanceof CommandNode && cnode.getParent() instanceof BlockNode) {
                            BlockNode bnode = (BlockNode) cnode.getParent();
                            Environment env = bnode.getEnvironment();

                            if (env != null) {
                                attrs = getColoringForName(TexColoringNames.ENUM_ARG_CORRECT);
                            } else {
                                attrs = getColoringForName(TexColoringNames.ENUM_ARG_INCORRECT);
                            }
                        }
                    }
                }
                
                try {
                    if (node.getChildrenCount() > 0) {
                        for (Iterator<Node> children = node.getChildrenIterator(); children.hasNext();) {
                            for (Token t : children.next().getNodeTokens()) {
                                add(token2Attributes, t, attrs);
                            }
                        }
                    } else {
                        boolean first = true;
                        
                        for (Iterator<? extends Token> it = node.getNodeTokens().iterator(); it.hasNext();) {
                            Token t = it.next();
                            
                            if (first && t.id() == TexTokenId.COMP_BRACKET_LEFT)
                                continue;
                            
                            if (t.id() == TexTokenId.COMP_BRACKET_RIGHT && !it.hasNext())
                                continue;
                            
                            add(token2Attributes, t, attrs);
                            first = false;
                        }
                    }
                } catch (IOException e) {
                    Exceptions.printStackTrace(e);
                }
                
                return true;
            }

            public void argumentEnd(ArgumentNode node) {}

            public boolean blockStart(BlockNode node) {
                if (cancelled.get()) {
                    return false;
                }

                return true;
            }
            
            public void blockEnd(BlockNode node) {}

            @Override
            public boolean mathStart(MathNode node) {
                if (cancelled.get()) {
                    return false;
                }
                
                if (node.getStartingPosition().getDocument() != document)
                    return true;
                
                AttributeSet attrs = getColoringForName(TexColoringNames.MATH);
                
                try {
                    for (Token t : node.getDeepNodeTokens()) {
                        add(token2Attributes, t, attrs);
                    }
                } catch (IOException e) {
                    Exceptions.printStackTrace(e);
                }
                
                return true;
            }
        });

        PositionsBag bag = new PositionsBag(null);
//        long start = System.currentTimeMillis();
        
        try {
            for (Entry<Token, List<AttributeSet>> e : token2Attributes.entrySet()) {
                Token t = e.getKey();
                AttributeSet c = AttributesUtilities.createComposite(e.getValue().toArray(new AttributeSet[0]));
                
                //XXX:
                if (c == null) {
                    c = SimpleAttributeSet.EMPTY;
                }

                bag.addHighlight(NbDocument.createPosition(document, t.offset(null), Position.Bias.Backward), NbDocument.createPosition(document, t.offset(null) + t.length(), Position.Bias.Forward), c);
            }
            
            ColoringEvaluator.getDelegate(document).setHighlights(bag);
        } catch (BadLocationException e) {
            Exceptions.printStackTrace(e);
        } catch (IllegalStateException e) {
            //XXX: debug only
            Exceptions.printStackTrace(e);
//            System.err.println("result= " + result);
//            e.printStackTrace();
        } finally {
            //            TimesCollector.getDefault().reportTime((FileObject) Utilities.getDefault().getSource(document).getMainFile(), "ColoringEvaluator", "Coloring Evaluator", System.currentTimeMillis() - start);
        }
    }

    private void add(Map<Token, List<AttributeSet>> token2Attributes, Token t, AttributeSet att) {
        List<AttributeSet> l = token2Attributes.get(t);
        
        if (l == null) {
            token2Attributes.put(t, l = new LinkedList<AttributeSet>());
        }
        
        l.add(att);
    }
    
    private AttributeSet getColoringForName(String name) {
        FontColorSettings fontColorSettings = MimeLookup.getLookup(MimePath.get("text/x-tex")).lookup(FontColorSettings.class);
        
        return fontColorSettings.getTokenFontColors(name);
    }
}
