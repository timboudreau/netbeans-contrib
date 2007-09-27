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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
//import org.netbeans.api.timers.TimesCollector;
import org.netbeans.modules.latex.model.command.ArgumentContainingNode;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.ArgumentContainingNode;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.MathNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.lexer.TexTokenId;
import org.netbeans.spi.editor.highlighting.support.PositionsBag;
import org.openide.filesystems.FileObject;
import org.openide.text.NbDocument;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public class ColoringEvaluator implements /*LaTeXSource.DocumentChangedListener, Runnable,*/ PropertyChangeListener {//implements DocumentListener, LaTeXSource.DocumentChangedListener,  {
    
//    private Map token2Coloring;
//    private Map/*Map*/ components;
//
    private Document document;
    
    private final static Map<Document, ColoringEvaluator> document2ColoringEvaluator = new WeakHashMap<Document, ColoringEvaluator>();
    
    private static RequestProcessor processor = new RequestProcessor("Coloring Updating Request Processor", 1);
    
    private RequestProcessor.Task task;
    
//    static {
//        processor.post(new ColoringTask(), 0, Thread.MIN_PRIORITY);
//    }
    
//    private static boolean STATIC_COLORING = Boolean.getBoolean("netbeans.latex.coloring.static");
//    
//    private boolean fullSyntacticColoring = false;
//    
//    private long documentVersion = -1;
//    
    public static synchronized ColoringEvaluator getColoringEvaluator(Document doc) {
        ColoringEvaluator eval = document2ColoringEvaluator.get(doc);
        
        if (eval == null) {
            eval = new ColoringEvaluator(doc);
            document2ColoringEvaluator.put(doc, eval);
        }
        
        return eval;
    }
    
//    private void updateSource() {
//        Object o = org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document);
//        
//        if (o == null) {
////            System.err.println("!!!!!!!!!!!!!!!!!: o== null");
//            processor.post(new Runnable() {
//                public void run() {
//                    updateSource();
//                }
//            }, 1000);
//            return ;
//        }
//        
//        LaTeXSource source = LaTeXSource.get(o);
//        
//        if (source != null) {
//            source.addDocumentChangedListener(this);
//            postUpdateRequest();
//        }
//    }
    
//    /** Creates a new instance of ColoringEvaluator */
    private ColoringEvaluator(Document doc) {
        this.document = doc;
//        this.token2Coloring = new WeakHashMap();///*new CachingMap(*/new IdentityHashMap()/*, MAX_ENTRIES)*/;
//        this.components = new WeakHashMap();
//        
//        TexOptions options = ((TexOptions) TexOptions.getOptions(TexKit.class));
//        
//        options.addPropertyChangeListener(WeakListeners.propertyChange(this, options));
//        fullSyntacticColoring = isFullSyntacticColoringImpl();
//        
//        updateDocumentVersion();
//        task = processor.create(this);
//        task.setPriority(Thread.MIN_PRIORITY);
        
        //XXX testing!:
//        processor.post(new Runnable() {
//            public void run() {
//                updateSource();
//            }
//        }, 5000);
    }
//    
//    private boolean isFullSyntacticColoringImpl() {
//        return ((TexOptions) TexOptions.getOptions(TexKit.class)).isFullSyntacticColoring();
//    }
//    
//    private boolean isFullSyntacticColoring() {
//        return STATIC_COLORING || fullSyntacticColoring;
//    }
//    
//    private void updateDocumentVersion() {
//        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
//        long result = source.getDocumentVersion();
//        
//        synchronized (ColoringEvaluator.this) {
//            documentVersion = result;
//        }
//    }
//    
//    public void addComponent(JTextComponent jtc) {
//        components.put(jtc, jtc);
//    }
//    
//    private static Queue<TokenHolder> toBeUpdated = new Queue<TokenHolder>();
//    
//    private void addToBeUpdated(Token token) {
//        synchronized(ColoringEvaluator.class) {
//            toBeUpdated.put(new TokenHolder(this, token));
//            ColoringEvaluator.class.notifyAll();
//        }
//    }
//    
//    private static synchronized TokenHolder getToBeUpdated() {
//        while (toBeUpdated.empty()) {
//            try {
//                ColoringEvaluator.class.wait();
//            } catch (InterruptedException e) {
//                //TODO: better logging:
//                e.printStackTrace();
//            }
//        }
//        
//        return toBeUpdated.pop();
//    }
//    
//    private synchronized boolean isEmptyToBeUpdated() {
//        boolean empty = toBeUpdated.empty();
//        
//        return empty;
//    }
//    
//    private synchronized Coloring getColoringStatic(Token token) {
//        Coloring proposed = null;
//        
//        if (token.id() == TexTokenId.COMMAND)
//            proposed = getColoringForName(TexColoringNames.COMMAND_CORRECT);
//        else
//            proposed = getStaticColoring(token);
//        
////        if (TokenAttributes.isInMathToken(token)) {
////            if (proposed != null)
////                proposed = proposed.apply(getColoringForName(TexColoringNames.MATH));
////            else
////                proposed = getColoringForName(TexColoringNames.MATH);
////        }
//        
//        return proposed;
//    }
//    
//    private synchronized Coloring getColoringFull(Token token) {
////        System.err.println("token=" + token);
//        ColoringHolder holder = (ColoringHolder) token2Coloring.get(token);
//        
//        if (holder == null || holder.version < documentVersion) {
////            System.err.println("not up-to-date.");
//                addToBeUpdated(token);
//        }
//        
////        System.err.println("cached coloring:" + c);
//        
//        if (holder == null || holder.coloring == null) {
//            Coloring c = getStaticColoring(token);
//            
//            token2Coloring.put(token, new ColoringHolder(c, -1));
//            
//            return c;
//        }
//        
////        System.err.println("resulting coloring:" + c);
//        
//        return holder.coloring;
//    }
//    
//    private boolean fullColoring(Token token) {
//        if (!isFullSyntacticColoring())
//            return false;
//        
////        switch (token.id().getIntId()) {
////            case TexLanguage.COMMAND_INT:
////            case TexLanguage.WORD_INT:
////                return true;
////            default:
////                return false;
////        }
//        //XXX: this causes that the coloring is quite slow, but without this the #ref arguments will not be colored
//        //properly...
//        return true;
//    }
//    
//    public synchronized Coloring getColoring(Token token) {
//        if (fullColoring(token)) {
//            return getColoringFull(token);
//        } else {
//            return getColoringStatic(token);
//        }
//    }
//    
//    private Coloring getStaticColoring(Token token) {
//        return getColoringForTokenId(token.id());
//    }
//
    private AttributeSet getColoringForName(String name) {
        FontColorSettings fontColorSettings = MimeLookup.getLookup(MimePath.get("text/x-tex")).lookup(FontColorSettings.class);
        
        return fontColorSettings.getTokenFontColors(name);
    }
    
//    private Coloring toColoring(AttributeSet as) {
//        int fontApplyMode = 0;
//        int fontStyle = 0;
//        int fontSize;
//        String fontFamily = (String)as.getAttribute(StyleConstants.FontFamily);
//        Integer sz = (Integer)as.getAttribute(StyleConstants.FontSize);
//        boolean bold = Boolean.TRUE.equals(as.getAttribute(StyleConstants.Bold));
//        boolean italic = Boolean.TRUE.equals(as.getAttribute(StyleConstants.Italic));
//        if (fontFamily != null) {
//            fontApplyMode |= Coloring.FONT_MODE_APPLY_NAME;
//        } else {
//            fontFamily = "Monospaced";
//        }
//        if (sz != null) {
//            fontSize = sz.intValue();
//            fontApplyMode |= Coloring.FONT_MODE_APPLY_SIZE;
//        } else {
//            fontSize = 10;
//        }
//        if (bold) {
//            fontStyle |= Font.BOLD;
//            fontApplyMode |= Coloring.FONT_MODE_APPLY_STYLE;
//        }
//        if (italic) {
//            fontStyle |= Font.ITALIC;
//            fontApplyMode |= Coloring.FONT_MODE_APPLY_STYLE;
//        }
//        
//        Font font = new Font(
//                fontFamily,
//                fontStyle,
//                fontSize
//                );
//        
//        return new Coloring(
//                font,
//                fontApplyMode,
//                (Color) as.getAttribute(StyleConstants.Foreground),
//                (Color) as.getAttribute(StyleConstants.Background),
//                (Color) as.getAttribute(StyleConstants.Underline),
//                (Color) as.getAttribute(StyleConstants.StrikeThrough),
//                (Color) as.getAttribute(EditorStyleConstants.WaveUnderlineColor)
//                );
//        
//    }
    
//    private Coloring getColoringForTokenId(TokenId id) {
//        return getColoringForName(id.name());
//    }
//    
//    private static class ColoringTask implements Runnable {
//        
//        public void run() {
//            int startAbs = Integer.MAX_VALUE;
//            int endAbs   = Integer.MIN_VALUE;
//            long lastUpdateTime = System.currentTimeMillis();
//            
//            while (true) {
//                try {
//                    TokenHolder token = getToBeUpdated();
//                    
//                    synchronized (token.eval) {
//                        ColoringHolder holder = (ColoringHolder) token.eval.token2Coloring.get(token.token);
//                        
//                        if (holder != null && holder.version >= token.eval.documentVersion)
//                            continue;
//                    }
//                    
//                    if (token == null)
//                        continue;
//                    
//                    Coloring proposed = token.eval.computeColoring(token.token);
//                    
//                    synchronized (token.eval) {
//                        token.eval.token2Coloring.put(token.token, new ColoringHolder(proposed, token.eval.documentVersion));
//                        
//                        int start = Utilities.getTokenOffset(token.eval.document, token.token);
//                        int end   = start + token.token.getText().length();
//                        
//                        if (start < startAbs)
//                            startAbs = start;
//                        
//                        if (end > endAbs)
//                            endAbs = end;
//                        
//                        if ((System.currentTimeMillis() - lastUpdateTime) >= 1000) {
//                            token.eval.fireTokenColoringChanged(startAbs, endAbs);
//                            startAbs = Integer.MAX_VALUE;
//                            endAbs   = Integer.MIN_VALUE;
//                            lastUpdateTime = System.currentTimeMillis();
//                        }
//                    }
//                } catch (ThreadDeath td) {
//                    throw td;
//                } catch (Throwable t) {
//                    ErrorManager.getDefault().notifyInformational(t);
//                }
//            }
//        }
//        
//    }
//    
//    private void fireTokenColoringChanged(final int start, final int end) {
//        SwingUtilities.invokeLater(
//        new Runnable() {
//            public void run() {
//                try {
//                    Iterator i = components.keySet().iterator();
//                    
//                    while (i.hasNext()) {
//                        JTextComponent comp = (JTextComponent) i.next();
//                        
//                        if (comp == null)
//                            continue;
//                        
//                        TextUI ui = (TextUI)comp.getUI();
//                        
//                        ui.damageRange(comp, start, end);
//                    }
//                } catch (NullPointerException e) {
//                    //Sometimes a NPE from Views may occur. It it hopefully harmless.
//                    ErrorManager.getDefault().notifyInformational(e);
//                }
//            }
//        }
//        );
//    }
//    
    
    public static PositionsBag getDelegate(Document doc) {
        PositionsBag bag = (PositionsBag) doc.getProperty(ColoringEvaluator.class);
        
        if (bag == null) {
            doc.putProperty(ColoringEvaluator.class, bag = new PositionsBag(doc));
        }
        
        return bag;
    }
    
//    private void performColoring(int startOffset, int endOffset) {
//        TokenHierarchy h = TokenHierarchy.get(document);
//        TokenSequence ts = h.tokenSequence();
//        PositionsBag bag = new PositionsBag(null);
//        long start = System.currentTimeMillis();
//        int result = ts.move(startOffset);
//        
//        if (!ts.moveNext()) {
//            return;
//        }
//        
//        try {
//            while (ts.offset() < endOffset) {
//                AttributeSet c = computeExtendedColoring(ts.offset(), ts.token());
//                
//                if (c != null) {
//                    bag.addHighlight(document.createPosition(ts.offset()), NbDocument.createPosition(document, ts.offset() + ts.token().length(), Position.Bias.Backward), c);
//                }
//                
//                if (!ts.moveNext())
//                    break;
//            }
//            
//            getDelegate(document).setHighlights(bag);
//        } catch (BadLocationException e) {
//            ErrorManager.getDefault().notify(e);
//        } catch (IllegalStateException e) {
//            //XXX: debug only
//            ErrorManager.getDefault().notify(e);
//            System.err.println("result= " + result);
//            e.printStackTrace();
//        } finally {
////            TimesCollector.getDefault().reportTime((FileObject) Utilities.getDefault().getSource(document).getMainFile(), "ColoringEvaluator", "Coloring Evaluator", System.currentTimeMillis() - start);
//        }
//    }
//    
//    private AttributeSet computeExtendedColoring(int offset, Token token, DocumentNode node) {
//        List<AttributeSet> parts = new ArrayList<AttributeSet>();
//        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
//        
//        if (source != null && (source.isUpToDate() || source.getDocument() == null)) {
//            try {
//                if (source.getDocument() == null) {
//                    LaTeXSource.Lock lock1 = null;
//                    try {
//                        lock1 = source.lock(true);
//                    } finally {
//                        if (lock1 != null) {
//                            source.unlock(lock1);
//                        }
//                    }
//                }
//                
//                long start = System.currentTimeMillis();
//                Node node  = source.findNode(document, offset);
//                long end   = System.currentTimeMillis();
//                
//                Node loop = node;
//                
//                while (!(loop instanceof DocumentNode)) {
//                    if (loop instanceof MathNode) {
//                        parts.add(getColoringForName(TexColoringNames.MATH));
//                        break;
//                    }
//                    loop = loop.getParent();
//                }
//            } catch (IOException e) {
//                ErrorManager.getDefault().notify(e);
//            }
//        }
//                
//        if (token.id() == TexTokenId.COMMAND) {
//            findCommandColoring(offset, token, parts);
//        }
//        
//        if (org.netbeans.modules.latex.editor.Utilities.isTextWord(token)) {
//            findWordColoring(offset, token, parts);
//        }
//        
////        proposed = checkRefArgument(token, proposed);
//        
//        if (parts.isEmpty())
//            return null;
//        
//        return AttributesUtilities.createImmutable(parts.toArray(new AttributeSet[0]));
//    }
//    
//    private void findCommandColoring(int offset, Token token, List<AttributeSet> parts) {
//        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
//        
//        if (source != null && (source.isUpToDate() || source.getDocument() == null)) {
//            try {
//                if (source.getDocument() == null) {
//                    LaTeXSource.Lock lock1 = null;
//                    try {
//                        lock1 = source.lock(true);
//                    } finally {
//                        if (lock1 != null) {
//                            source.unlock(lock1);
//                        }
//                    }
//                }
//                
//                long start = System.currentTimeMillis();
//                Node node  = source.findNode(document, offset);
//                long end   = System.currentTimeMillis();
//                
///*                        if (!(node instanceof CommandNode)) {
//                            System.err.println("Finding a node spent: " + (end - start));
//                            System.err.println("token=" + token.getText().toString() + ", node=" + node);
//                            System.err.println("offset=" + offset);
//                            TextNode tn = (TextNode) node.getParent();
// 
//                            for (int cntr = 0; cntr < tn.getChildrenCount(); cntr++) {
//                                Node n = tn.getChild(cntr);
// 
//                                System.err.println(cntr + "=" + n);
//                            }
//                            try {
//                            Thread.sleep(40000);
//                            } catch (InterruptedException e) {
//                            }
//                        }*/
//                
//                if (node != null) {
//                    if (node instanceof CommandNode) {
//                        CommandNode cnode = (CommandNode) node;
//                        
//                        if (cnode.isValid())
//                            parts.add(getColoringForName(TexColoringNames.COMMAND_CORRECT));
//                        else
//                            parts.add(getColoringForName(TexColoringNames.COMMAND_INCORRECT));
//                    } else {
//                        if (node instanceof ArgumentNode) {
//                            ArgumentNode anode = (ArgumentNode) node;
//                            
//                            if (anode.getArgument().hasAttribute(Command.Param.ATTR_NO_PARSE)) {
//                                parts.add(getColoringForName(TexColoringNames.DEFINITION));
//                            }
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                ErrorManager.getDefault().notify(e);
//            }
//        }
//    }
//
//    private void findWordColoring(int offset, Token token, List<AttributeSet> parts) {
//        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
//        
//        if (source != null && (source.isUpToDate() || source.getDocument() == null)) {
//            try {
//                if (source.getDocument() == null) {
//                    LaTeXSource.Lock lock1 = null;
//                    try {
//                        lock1 = source.lock(true);
//                    } finally {
//                        if (lock1 != null) {
//                            source.unlock(lock1);
//                        }
//                    }
//                }
//                long start = System.currentTimeMillis();
//                Node node  = source.findNode(document, offset);
//                long end   = System.currentTimeMillis();
//                
//                if (node != null) {
//                    if (node instanceof ArgumentNode) {
//                        ArgumentNode anode = (ArgumentNode) node;
//                        
//                        if (anode.getArgument().isEnumerable()) {
//                            if (anode.isValidEnum()) {
//                                parts.add(getColoringForName(TexColoringNames.ENUM_ARG_CORRECT));
//                            } else {
//                                parts.add(getColoringForName(TexColoringNames.ENUM_ARG_INCORRECT));
//                            }
//                        } else {
//                            ArgumentContainingNode cnode = anode.getCommand();
//                            
//                            if (cnode instanceof CommandNode && cnode.getParent() instanceof BlockNode) {
//                                BlockNode bnode = (BlockNode) cnode.getParent();
//                                Environment env = source.getEnvironment(bnode.getStartingPosition(), bnode.getBlockName());
//                                
//                                if (env != null) {
//                                    parts.add(getColoringForName(TexColoringNames.ENUM_ARG_CORRECT));
//                                } else {
//                                    parts.add(getColoringForName(TexColoringNames.ENUM_ARG_INCORRECT));
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                ErrorManager.getDefault().notify(e);
//            }
//        }
//    }
    
//    private Coloring checkRefArgument(Token token, Coloring proposed) {
//        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
//        int  offset = org.netbeans.modules.latex.editor.Utilities.getTokenOffset(document, token);
//        
//        try {
//            Node n = source.findNode(document, offset);
//            
//            if (n instanceof ArgumentNode) {
//                ArgumentNode anode = (ArgumentNode) n;
//                
//                if (anode.hasAttribute("#ref")) {
//                    //check validity of the ref:
//                    String proposedLabel = anode.getText().toString();
//                    boolean found = false;
//                    
//                    for (Iterator i = org.netbeans.modules.latex.model.Utilities.getDefault().getLabels(source).iterator(); i.hasNext(); ) {
//                        LabelInfo info = (LabelInfo) i.next();
//                        
//                        if (proposedLabel.equals(info.getLabel())) {
//                            found = true;
//                            break;
//                        }
//                    }
//                    
//                    if (!found) {
//                        //do not color the surrounding brackets:
//                        if (anode.getStartingPosition().getOffsetValue() < offset && offset < anode.getEndingPosition().getOffsetValue() - 1) {
//                            proposed = getColoringForName(TexColoringNames.ARG_INCORRECT).apply(proposed);
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            ErrorManager.getDefault().notify(e);
//        }
//        
//        return proposed;
//    }
//
//    public void changedUpdate(javax.swing.event.DocumentEvent e) {
//        //Ignored on purpose.
//    }
//    
//    public synchronized void insertUpdate(javax.swing.event.DocumentEvent e) {
//        updateDocumentVersion();
//    }
//    
//    public synchronized void removeUpdate(javax.swing.event.DocumentEvent e) {
//        updateDocumentVersion();
//    }
//    
//    public void nodesAdded(LaTeXSource.DocumentChangeEvent evt) {
//        postUpdateRequest();
//    }
//    
//    public void nodesChanged(LaTeXSource.DocumentChangeEvent evt) {
//        postUpdateRequest();
//    }
//    
//    public void nodesRemoved(LaTeXSource.DocumentChangeEvent evt) {
//        postUpdateRequest();
//    }
//    
//    private void postUpdateRequest() {
//        task.schedule(1000);
//    }
    
//    public void run() {
//        //TODO: locking (but do not lock the document during all the operation!)
//        performColoring(0, document.getLength());
//    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        
//        fullSyntacticColoring = isFullSyntacticColoringImpl();
//        token2Coloring.clear(); //can this be done?
    }
//    
//    private static class ColoringHolder {
//        public Coloring coloring;
//        public long     version;
//        
//        public ColoringHolder(Coloring coloring, long version) {
//            this.coloring = coloring;
//            this.version  = version;
//        }
//    }
//    
//    private static class TokenHolder {
//        public ColoringEvaluator eval;
//        public Token token;
//        
//        public TokenHolder(ColoringEvaluator eval, Token token) {
//            this.eval = eval;
//            this.token = token;
//        }
//
//        public boolean equals(Object o) {
//            if (o == null)
//                return false;
//            if (getClass() != o.getClass())
//                return false;
//            final TokenHolder test = (TokenHolder) o;
//
//            if (this.eval != test.eval && this.eval != null &&
//                !this.eval.equals(test.eval))
//                return false;
//            if (this.token != test.token && this.token != null &&
//                !this.token.equals(test.token))
//                return false;
//            return true;
//        }
//
//        public int hashCode() {
//            int hash = 3;
//
//            hash = 13 * hash + (this.eval != null ? this.eval.hashCode()
//                                                  : 0);
//            hash = 13 * hash + (this.token != null ? this.token.hashCode()
//                                                   : 0);
//            return hash;
//        }
//
//    }
//    
//    private static class CachingMap implements Map {
//        
//        private Map delegateTo;
//        private LinkedList lru;
//        int limit;
//        
//        public CachingMap(Map delegateTo, int limit) {
//            lru = new LinkedList();
//            this.limit = limit;
//            this.delegateTo = delegateTo;
//        }
//        
//        private void clearToLimit() {
//            while (lru.size() >= limit) {
////                System.err.println("clearToLimit, size=" + lru.size());
//                Object key = lru.remove(0);
//                
//                delegateTo.remove(key);
//                
////                System.err.println("cleared: key = " + key );
//            }
//        }
//        
//        public void clear() {
//            lru.clear();
//            delegateTo.clear();
//        }
//        
//        public boolean containsKey(Object key) {
//            return delegateTo.containsKey(key);
//        }
//        
//        public boolean containsValue(Object value) {
//            return delegateTo.containsValue(value);
//        }
//        
//        public Set entrySet() {
//            return delegateTo.entrySet();
//        }
//        
//        public Object get(Object key) {
//            if (!delegateTo.containsKey(key))
//                return null;
//            
//            lru.remove(key);
//            lru.add(key);
//            
//            return delegateTo.get(key);
//        }
//        
//        public boolean isEmpty() {
//            return delegateTo.isEmpty();
//        }
//        
//        public Set keySet() {
//            return delegateTo.keySet();
//        }
//        
//        public Object put(Object key, Object value) {
////            System.err.println("put start: lru.size()=" + lru.size() + ", map.size()=" + delegateTo.size());
////            System.err.println("key = " + key );
////            System.err.println("value = " + value );
//            
//            clearToLimit();
//            lru.remove(key);
//            lru.add(key);
//            
//            Object val = delegateTo.put(key, value);
//
//            return val;
//        }
//        
//        public void putAll(Map t) {
//            for (Iterator i = t.keySet().iterator(); i.hasNext(); ) {
//                Object key = i.next();
//                
//                put(key, t.get(key));
//            }
//        }
//        
//        public Object remove(Object key) {
//            lru.remove(key);
//            
//            return delegateTo.remove(key);
//        }
//        
//        public int size() {
//            return delegateTo.size();
//        }
//        
//        public Collection values() {
//            return delegateTo.values();
//        }
//        
//    }
//
}
