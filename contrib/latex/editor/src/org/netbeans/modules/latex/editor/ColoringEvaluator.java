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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */

package org.netbeans.modules.latex.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.InterruptedException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsNames;
import org.netbeans.modules.latex.model.LabelInfo;
import org.netbeans.modules.latex.model.Queue;
import org.netbeans.modules.latex.model.command.ArgumentContainingNode;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.MathNode;
import org.netbeans.modules.latex.model.command.Node;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

/**
 *
 * @author Jan Lahoda
 */
public class ColoringEvaluator implements DocumentListener, LaTeXSource.DocumentChangedListener, PropertyChangeListener {
    
    private Map token2Coloring;
    private Map/*Map*/ components;

    private Document document;
    
    private static Map document2ColoringEvaluator = null;
    
    private static RequestProcessor processor = new RequestProcessor("Coloring Updating Request Processor");
    
    static {
        processor.post(new ColoringTask(), 0, Thread.MIN_PRIORITY);
    }
    
    private static boolean STATIC_COLORING = Boolean.getBoolean("netbeans.latex.coloring.static");
    
    private boolean fullSyntacticColoring = false;
    
    private long documentVersion = -1;
    
    public static synchronized ColoringEvaluator getColoringEvaluator(Document doc) {
        if (document2ColoringEvaluator == null)
            document2ColoringEvaluator = new WeakHashMap();
        
        ColoringEvaluator eval = (ColoringEvaluator) document2ColoringEvaluator.get(doc);
        
        if (eval == null) {
            eval = new ColoringEvaluator(doc);
            document2ColoringEvaluator.put(doc, eval);
        }
        
        return eval;
    }
    
    /** Creates a new instance of ColoringEvaluator */
    private ColoringEvaluator(Document doc) {
        this.document = doc;
        this.token2Coloring = new WeakHashMap();///*new CachingMap(*/new IdentityHashMap()/*, MAX_ENTRIES)*/;
        this.components = new WeakHashMap();
        
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(doc));
        
        source.addDocumentChangedListener(this);
        
        TexOptions options = ((TexOptions) TexOptions.getOptions(TexKit.class));
        
        options.addPropertyChangeListener(WeakListeners.propertyChange(this, options));
        fullSyntacticColoring = isFullSyntacticColoringImpl();
        
        updateDocumentVersion();
    }
    
    private boolean isFullSyntacticColoringImpl() {
        return ((TexOptions) TexOptions.getOptions(TexKit.class)).isFullSyntacticColoring();
    }
    
    private boolean isFullSyntacticColoring() {
        return STATIC_COLORING || fullSyntacticColoring;
    }
    
    private void updateDocumentVersion() {
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
        long result = source.getDocumentVersion();
        
        synchronized (ColoringEvaluator.this) {
            documentVersion = result;
        }
    }
    
    public void addComponent(JTextComponent jtc) {
        components.put(jtc, jtc);
    }
    
    private static Queue<TokenHolder> toBeUpdated = new Queue<TokenHolder>();
    
    private void addToBeUpdated(Token token) {
        synchronized(ColoringEvaluator.class) {
            toBeUpdated.put(new TokenHolder(this, token));
            ColoringEvaluator.class.notifyAll();
        }
    }
    
    private static synchronized TokenHolder getToBeUpdated() {
        while (toBeUpdated.empty()) {
            try {
                ColoringEvaluator.class.wait();
            } catch (InterruptedException e) {
                //TODO: better logging:
                e.printStackTrace();
            }
        }
        
        return toBeUpdated.pop();
    }
    
    private synchronized boolean isEmptyToBeUpdated() {
        boolean empty = toBeUpdated.empty();
        
        return empty;
    }
    
    private synchronized Coloring getColoringStatic(Token token) {
        Coloring proposed = null;
        
        if (token.getId() == TexLanguage.COMMAND)
            proposed = getColoringForName(TexColoringNames.COMMAND_CORRECT);
        else
            proposed = getStaticColoring(token);
        
//        if (TokenAttributes.isInMathToken(token)) {
//            if (proposed != null)
//                proposed = proposed.apply(getColoringForName(TexColoringNames.MATH));
//            else
//                proposed = getColoringForName(TexColoringNames.MATH);
//        }
        
        return proposed;
    }
    
    private synchronized Coloring getColoringFull(Token token) {
//        System.err.println("token=" + token);
        ColoringHolder holder = (ColoringHolder) token2Coloring.get(token);
        
        if (holder == null || holder.version < documentVersion) {
//            System.err.println("not up-to-date.");
                addToBeUpdated(token);
        }
        
//        System.err.println("cached coloring:" + c);
        
        if (holder == null || holder.coloring == null) {
            Coloring c = getStaticColoring(token);
            
            token2Coloring.put(token, new ColoringHolder(c, -1));
            
            return c;
        }
        
//        System.err.println("resulting coloring:" + c);
        
        return holder.coloring;
    }
    
    private boolean fullColoring(Token token) {
        if (!isFullSyntacticColoring())
            return false;
        
//        switch (token.getId().getIntId()) {
//            case TexLanguage.COMMAND_INT:
//            case TexLanguage.WORD_INT:
//                return true;
//            default:
//                return false;
//        }
        //XXX: this causes that the coloring is quite slow, but without this the #ref arguments will not be colored
        //properly...
        return true;
    }
    
    public synchronized Coloring getColoring(Token token) {
        if (fullColoring(token)) {
            return getColoringFull(token);
        } else {
            return getColoringStatic(token);
        }
    }
    
    private Coloring getStaticColoring(Token token) {
        return getColoringForTokenId(token.getId());
    }

    private Coloring getColoringForName(String name) {
        return (Coloring) Settings.getValue(TexKit.class, name + SettingsNames.COLORING_NAME_SUFFIX);
    }
    
    private Coloring getColoringForTokenId(TokenId id) {
        return getColoringForName(id.getName());
    }
    
    private static class ColoringTask implements Runnable {
        
        public void run() {
            int startAbs = Integer.MAX_VALUE;
            int endAbs   = Integer.MIN_VALUE;
            long lastUpdateTime = System.currentTimeMillis();
            
            while (true) {
                try {
                    TokenHolder token = getToBeUpdated();
                    
                    synchronized (token.eval) {
                        ColoringHolder holder = (ColoringHolder) token.eval.token2Coloring.get(token.token);
                        
                        if (holder != null && holder.version >= token.eval.documentVersion)
                            continue;
                    }
                    
                    if (token == null)
                        continue;
                    
                    Coloring proposed = token.eval.computeColoring(token.token);
                    
                    synchronized (token.eval) {
                        token.eval.token2Coloring.put(token.token, new ColoringHolder(proposed, token.eval.documentVersion));
                        
                        int start = Utilities.getTokenOffset(token.eval.document, token.token);
                        int end   = start + token.token.getText().length();
                        
                        if (start < startAbs)
                            startAbs = start;
                        
                        if (end > endAbs)
                            endAbs = end;
                        
                        if ((System.currentTimeMillis() - lastUpdateTime) >= 1000) {
                            token.eval.fireTokenColoringChanged(startAbs, endAbs);
                            startAbs = Integer.MAX_VALUE;
                            endAbs   = Integer.MIN_VALUE;
                            lastUpdateTime = System.currentTimeMillis();
                        }
                    }
                } catch (ThreadDeath td) {
                    throw td;
                } catch (Throwable t) {
                    ErrorManager.getDefault().notifyInformational(t);
                }
            }
        }
        
    }
    
    private void fireTokenColoringChanged(final int start, final int end) {
        SwingUtilities.invokeLater(
        new Runnable() {
            public void run() {
                try {
                    Iterator i = components.keySet().iterator();
                    
                    while (i.hasNext()) {
                        JTextComponent comp = (JTextComponent) i.next();
                        
                        if (comp == null)
                            continue;
                        
                        TextUI ui = (TextUI)comp.getUI();
                        
                        ui.damageRange(comp, start, end);
                    }
                } catch (NullPointerException e) {
                    //Sometimes a NPE from Views may occur. It it hopefully harmless.
                    ErrorManager.getDefault().notifyInformational(e);
                }
            }
        }
        );
    }
    
    private Coloring computeColoring(Token token) {
        Coloring proposed = getColoringForTokenId(token.getId());
        
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
        
        if (source != null && (source.isUpToDate() || source.getDocument() == null)) {
            try {
                if (source.getDocument() == null) {
                    LaTeXSource.Lock lock1 = null;
                    try {
                        lock1 = source.lock(true);
                    } finally {
                        if (lock1 != null) {
                            source.unlock(lock1);
                        }
                    }
                }
                
                long start = System.currentTimeMillis();
                int  offset = org.netbeans.modules.latex.editor.Utilities.getTokenOffset(document, token);
                Node node  = source.findNode(document, offset);
                long end   = System.currentTimeMillis();
                
                Node loop = node;
                
                while (!(loop instanceof DocumentNode)) {
                    if (loop instanceof MathNode) {
                        proposed = getColoringForName(TexColoringNames.MATH).apply(proposed);
                        break;
                    }
                    loop = loop.getParent();
                }
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
                
        if (token.getId() == TexLanguage.COMMAND) {
            proposed = findCommandColoring(token, proposed);
        }
        
        if (org.netbeans.modules.latex.editor.Utilities.isTextWord(token)) {
            proposed = findWordColoring(token, proposed);
        }
        
        proposed = checkRefArgument(token, proposed);
        
        return proposed;
    }
    
    private Coloring findCommandColoring(Token token, Coloring proposed) {
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
        
        if (source != null && (source.isUpToDate() || source.getDocument() == null)) {
            try {
                if (source.getDocument() == null) {
                    LaTeXSource.Lock lock1 = null;
                    try {
                        lock1 = source.lock(true);
                    } finally {
                        if (lock1 != null) {
                            source.unlock(lock1);
                        }
                    }
                }
                
                long start = System.currentTimeMillis();
                int  offset = org.netbeans.modules.latex.editor.Utilities.getTokenOffset(document, token);
                Node node  = source.findNode(document, offset);
                long end   = System.currentTimeMillis();
                
/*                        if (!(node instanceof CommandNode)) {
                            System.err.println("Finding a node spent: " + (end - start));
                            System.err.println("token=" + token.getText().toString() + ", node=" + node);
                            System.err.println("offset=" + offset);
                            TextNode tn = (TextNode) node.getParent();
 
                            for (int cntr = 0; cntr < tn.getChildrenCount(); cntr++) {
                                Node n = tn.getChild(cntr);
 
                                System.err.println(cntr + "=" + n);
                            }
                            try {
                            Thread.sleep(40000);
                            } catch (InterruptedException e) {
                            }
                        }*/
                
                if (node != null) {
                    if (node instanceof CommandNode) {
                        CommandNode cnode = (CommandNode) node;
                        
                        if (cnode.isValid())
                            proposed = getColoringForName(TexColoringNames.COMMAND_CORRECT).apply(proposed);
                        else
                            proposed = getColoringForName(TexColoringNames.COMMAND_INCORRECT).apply(proposed);
                    } else {
                        if (node instanceof ArgumentNode) {
                            ArgumentNode anode = (ArgumentNode) node;
                            
                            if (anode.getArgument().hasAttribute(Command.Param.ATTR_NO_PARSE)) {
                                proposed = getColoringForName(TexColoringNames.DEFINITION).apply(proposed);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        
        return proposed;
    }

    private Coloring findWordColoring(Token token, Coloring proposed) {
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
        
        if (source != null && (source.isUpToDate() || source.getDocument() == null)) {
            try {
                if (source.getDocument() == null) {
                    LaTeXSource.Lock lock1 = null;
                    try {
                        lock1 = source.lock(true);
                    } finally {
                        if (lock1 != null) {
                            source.unlock(lock1);
                        }
                    }
                }
                long start = System.currentTimeMillis();
                int  offset = org.netbeans.modules.latex.editor.Utilities.getTokenOffset(document, token);
                Node node  = source.findNode(document, offset);
                long end   = System.currentTimeMillis();
                
                if (node != null) {
                    if (node instanceof ArgumentNode) {
                        ArgumentNode anode = (ArgumentNode) node;
                        
                        if (anode.getArgument().isEnumerable()) {
                            if (anode.isValidEnum()) {
                                proposed = getColoringForName(TexColoringNames.ENUM_ARG_CORRECT).apply(proposed);
                            } else {
                                proposed = getColoringForName(TexColoringNames.ENUM_ARG_INCORRECT).apply(proposed);
                            }
                        } else {
                            ArgumentContainingNode cnode = anode.getCommand();
                            
                            if (cnode instanceof CommandNode && cnode.getParent() instanceof BlockNode) {
                                BlockNode bnode = (BlockNode) cnode.getParent();
                                Environment env = source.getEnvironment(bnode.getStartingPosition(), bnode.getBlockName());
                                
                                if (env != null) {
                                    proposed = getColoringForName(TexColoringNames.ENUM_ARG_CORRECT).apply(proposed);
                                } else {
                                    proposed = getColoringForName(TexColoringNames.ENUM_ARG_INCORRECT).apply(proposed);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        
        return proposed;
    }
    
    private Coloring checkRefArgument(Token token, Coloring proposed) {
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
        int  offset = org.netbeans.modules.latex.editor.Utilities.getTokenOffset(document, token);
        
        try {
            Node n = source.findNode(document, offset);
            
            if (n instanceof ArgumentNode) {
                ArgumentNode anode = (ArgumentNode) n;
                
                if (anode.hasAttribute("#ref")) {
                    //check validity of the ref:
                    String proposedLabel = anode.getText().toString();
                    boolean found = false;
                    
                    for (Iterator i = org.netbeans.modules.latex.model.Utilities.getDefault().getLabels(source).iterator(); i.hasNext(); ) {
                        LabelInfo info = (LabelInfo) i.next();
                        
                        if (proposedLabel.equals(info.getLabel())) {
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        //do not color the surrounding brackets:
                        if (anode.getStartingPosition().getOffsetValue() < offset && offset < anode.getEndingPosition().getOffsetValue() - 1) {
                            proposed = getColoringForName(TexColoringNames.ARG_INCORRECT).apply(proposed);
                        }
                    }
                }
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        
        return proposed;
    }

    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        //Ignored on purpose.
    }
    
    public synchronized void insertUpdate(javax.swing.event.DocumentEvent e) {
        updateDocumentVersion();
    }
    
    public synchronized void removeUpdate(javax.swing.event.DocumentEvent e) {
        updateDocumentVersion();
    }
    
    public void nodesAdded(LaTeXSource.DocumentChangeEvent evt) {
        updateDocumentVersion();
    }
    
    public void nodesChanged(LaTeXSource.DocumentChangeEvent evt) {
        updateDocumentVersion();
    }
    
    public void nodesRemoved(LaTeXSource.DocumentChangeEvent evt) {
        updateDocumentVersion();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        fullSyntacticColoring = isFullSyntacticColoringImpl();
        token2Coloring.clear(); //can this be done?
    }
    
    private static class ColoringHolder {
        public Coloring coloring;
        public long     version;
        
        public ColoringHolder(Coloring coloring, long version) {
            this.coloring = coloring;
            this.version  = version;
        }
    }
    
    private static class TokenHolder {
        public ColoringEvaluator eval;
        public Token token;
        
        public TokenHolder(ColoringEvaluator eval, Token token) {
            this.eval = eval;
            this.token = token;
        }

        public boolean equals(Object o) {
            if (o == null)
                return false;
            if (getClass() != o.getClass())
                return false;
            final TokenHolder test = (TokenHolder) o;

            if (this.eval != test.eval && this.eval != null &&
                !this.eval.equals(test.eval))
                return false;
            if (this.token != test.token && this.token != null &&
                !this.token.equals(test.token))
                return false;
            return true;
        }

        public int hashCode() {
            int hash = 3;

            hash = 13 * hash + (this.eval != null ? this.eval.hashCode()
                                                  : 0);
            hash = 13 * hash + (this.token != null ? this.token.hashCode()
                                                   : 0);
            return hash;
        }

    }
    
    private static class CachingMap implements Map {
        
        private Map delegateTo;
        private LinkedList lru;
        int limit;
        
        public CachingMap(Map delegateTo, int limit) {
            lru = new LinkedList();
            this.limit = limit;
            this.delegateTo = delegateTo;
        }
        
        private void clearToLimit() {
            while (lru.size() >= limit) {
//                System.err.println("clearToLimit, size=" + lru.size());
                Object key = lru.remove(0);
                
                delegateTo.remove(key);
                
//                System.err.println("cleared: key = " + key );
            }
        }
        
        public void clear() {
            lru.clear();
            delegateTo.clear();
        }
        
        public boolean containsKey(Object key) {
            return delegateTo.containsKey(key);
        }
        
        public boolean containsValue(Object value) {
            return delegateTo.containsValue(value);
        }
        
        public Set entrySet() {
            return delegateTo.entrySet();
        }
        
        public Object get(Object key) {
            if (!delegateTo.containsKey(key))
                return null;
            
            lru.remove(key);
            lru.add(key);
            
            return delegateTo.get(key);
        }
        
        public boolean isEmpty() {
            return delegateTo.isEmpty();
        }
        
        public Set keySet() {
            return delegateTo.keySet();
        }
        
        public Object put(Object key, Object value) {
//            System.err.println("put start: lru.size()=" + lru.size() + ", map.size()=" + delegateTo.size());
//            System.err.println("key = " + key );
//            System.err.println("value = " + value );
            
            clearToLimit();
            lru.remove(key);
            lru.add(key);
            
            Object val = delegateTo.put(key, value);

            return val;
        }
        
        public void putAll(Map t) {
            for (Iterator i = t.keySet().iterator(); i.hasNext(); ) {
                Object key = i.next();
                
                put(key, t.get(key));
            }
        }
        
        public Object remove(Object key) {
            lru.remove(key);
            
            return delegateTo.remove(key);
        }
        
        public int size() {
            return delegateTo.size();
        }
        
        public Collection values() {
            return delegateTo.values();
        }
        
    }

}
