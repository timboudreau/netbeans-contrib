/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */

package org.netbeans.modules.latex.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
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
import org.netbeans.editor.ext.html.WeakHashSet;
import org.netbeans.modules.latex.model.Queue;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandCollection;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.LaTeXSource;
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
    
    private static boolean STATIC_COLORING = Boolean.getBoolean("netbeans.latex.coloring.static");
    
    private RequestProcessor.Task task = null;
    
    private static final int MAX_ENTRIES = 1000;
    
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
        constructToBeUpdated();
        
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(doc));
        
        source.addDocumentChangedListener(this);
        
        TexOptions options = ((TexOptions) TexOptions.getOptions(TexKit.class));
        
        options.addPropertyChangeListener(WeakListeners.propertyChange(this, options));
        fullSyntacticColoring = isFullSyntacticColoringImpl();
        
        updateDocumentVersion();
//        doc.addDocumentListener(this);
    }
    
    private boolean isFullSyntacticColoringImpl() {
        return ((TexOptions) TexOptions.getOptions(TexKit.class)).isFullSyntacticColoring();
    }
    
    private boolean isFullSyntacticColoring() {
        return STATIC_COLORING || fullSyntacticColoring;
    }
    
    private void updateDocumentVersion() {
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
        
        documentVersion = source.getDocumentVersion();
    }
    
    public void addComponent(JTextComponent jtc) {
        components.put(jtc, jtc);
    }
    
    private Queue/*<Token>*/ toBeUpdatedP1;
    private Queue/*<Token>*/ toBeUpdatedP2;
    
    private static final int LOW_PRIORITY = 2;
    private static final int HIGH_PRIORITY = 1;
    
    private synchronized void constructToBeUpdated() {
        this.toBeUpdatedP1 = new Queue();
        this.toBeUpdatedP2 = new Queue();
    }
    
    private synchronized void addToBeUpdated(Token token, int priority) {
        switch (priority) {
            case HIGH_PRIORITY:
                toBeUpdatedP1.put(token);
                break;
            case LOW_PRIORITY:
                toBeUpdatedP2.put(token);
                break;
            default:
                throw new IllegalStateException("priority=" + priority);
        }

        if (task == null) {
            task = processor.post(new ColoringTask(), 0, Thread.MIN_PRIORITY);
        }
    }
    
    private synchronized Token getToBeUpdated() {
        if (!toBeUpdatedP1.empty())
            return (Token) toBeUpdatedP1.pop();
        
        return (Token) toBeUpdatedP2.pop();
    }
    
    private synchronized boolean isEmptyToBeUpdated() {
        boolean empty = toBeUpdatedP1.empty() && toBeUpdatedP2.empty();
        
        if (empty)
            task = null;
        
        return empty;
    }
    
    private synchronized Coloring getColoringStatic(Token token) {
        Coloring proposed = null;
        
        if (token.getId() == TexLanguage.COMMAND)
            proposed = getColoringForName(TexColoringNames.COMMAND_CORRECT);
        else
            proposed = getStaticColoring(token);
        
        Boolean math = (Boolean) TokenAttributes.getTokenAttribute(token, TokenAttributesNames.IS_IN_MATH);
        
        if (math != null && math.booleanValue()) {
            if (proposed != null)
                proposed = proposed.apply(getColoringForName(TexColoringNames.MATH));
            else
                proposed = getColoringForName(TexColoringNames.MATH);
        }
        
        return proposed;
    }
    
    private synchronized Coloring getColoringFull(Token token) {
//        System.err.println("token=" + token);
        ColoringHolder holder = (ColoringHolder) token2Coloring.get(token);
        
        if (holder == null || holder.version < documentVersion) {
//            System.err.println("not up-to-date.");
            if (token.getId() == TexLanguage.WORD)
                addToBeUpdated(token, LOW_PRIORITY);
            else
                addToBeUpdated(token, HIGH_PRIORITY);
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
        
        switch (token.getId().getIntId()) {
            case TexLanguage.COMMAND_INT:
            case TexLanguage.WORD_INT:
                return true;
                
            default:
                return false;
        }
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
    
    private class ColoringTask implements Runnable {
        
        public void run() {
            int startAbs = Integer.MAX_VALUE;
            int endAbs   = Integer.MIN_VALUE;
            long lastUpdateTime = System.currentTimeMillis();
            
            while (!isEmptyToBeUpdated()) {
                Token token = null;
                
                synchronized (ColoringEvaluator.this) {
                    token = getToBeUpdated();
                    
                    ColoringHolder holder = (ColoringHolder) token2Coloring.get(token);
                    
                    if (holder != null && holder.version >= documentVersion)
                        continue;
                }
                
                if (token == null)
                    continue;
                
                Coloring proposed = computeColoring(token);
                
                synchronized (ColoringEvaluator.this) {
                    token2Coloring.put(token, new ColoringHolder(proposed, documentVersion));
                    
                    int start = Utilities.getTokenOffset(document, token);
                    int end   = start + token.getText().length();
                    
                    if (start < startAbs)
                        startAbs = start;
                    
                    if (end > endAbs)
                        endAbs = end;
                    
                    if ((System.currentTimeMillis() - lastUpdateTime) >= 1000) {
                        fireTokenColoringChanged(startAbs, endAbs);
                        startAbs = Integer.MAX_VALUE;
                        endAbs   = Integer.MIN_VALUE;
                        lastUpdateTime = System.currentTimeMillis();
                    }
                }
            }
            
            if (endAbs != Integer.MIN_VALUE)
                fireTokenColoringChanged(startAbs, endAbs);
        }
        
    }
    
    private void fireTokenColoringChanged(final int start, final int end) {
        SwingUtilities.invokeLater(
        new Runnable() {
            public void run() {
                Iterator i = components.keySet().iterator();
                
                while (i.hasNext()) {
                    JTextComponent comp = (JTextComponent) i.next();
                    
                    if (comp == null)
                        continue;
                    
                    TextUI ui = (TextUI)comp.getUI();
                    
                    ui.damageRange(comp, start, end);
                }
            }
        }
        );
    }
    
    private Coloring computeColoring(Token token) {
        Coloring proposed = getColoringForTokenId(token.getId());
        Boolean isInMath = (Boolean ) TokenAttributes.getTokenAttribute(token, TokenAttributesNames.IS_IN_MATH);
        
        if (isInMath != null && isInMath.booleanValue()) {
            proposed = getColoringForName(TexColoringNames.MATH).apply(proposed);
        }
        
        if (token.getId() == TexLanguage.COMMAND) {
            proposed = findCommandColoring(token, proposed);
        }
        
        if (org.netbeans.modules.latex.editor.Utilities.isTextWord(token)) {
            proposed = findWordColoring(token, proposed);
        }
        
        return proposed;
    }
    
    private Coloring findCommandColoring(Token token, Coloring proposed) {
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
        
        if (source != null && (source.isUpToDate() || source.getDocument() == null)) {
            LaTeXSource.Lock lock = null;
            
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
                if (false) {
                    lock = source.lock(false);
                    
                    if (lock ==null) {
                        lock = source.lock(true);
                    }
                }
                
                if (true || lock != null) {
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
                }
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            } finally {
                if (lock != null)
                    source.unlock(lock);
            }
        }
        
        return proposed;
    }

    private Coloring findWordColoring(Token token, Coloring proposed) {
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(document));
        
        if (source != null && (source.isUpToDate() || source.getDocument() == null)) {
            Dictionary dictionary = Dictionary.getDictionary(source.getDocumentLocale());
            
            LaTeXSource.Lock lock = null;
            
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
                if (false) {
                    lock = source.lock(false);
                    
                    if (lock ==null) {
                        lock = source.lock(true);
                    }
                }
                
                if (true || lock != null) {
                    long start = System.currentTimeMillis();
                    int  offset = org.netbeans.modules.latex.editor.Utilities.getTokenOffset(document, token);
                    Node node  = source.findNode(document, offset);
                    long end   = System.currentTimeMillis();
                    boolean doSpell = true;
                    
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
                        if (node instanceof ArgumentNode) {
                            ArgumentNode anode = (ArgumentNode) node;
                            
                            if (anode.getArgument().isEnumerable()) {
                                doSpell = false;
                                
                                if (anode.isValidEnum()) {
                                    proposed = getColoringForName(TexColoringNames.ENUM_ARG_CORRECT).apply(proposed);
                                } else {
                                    proposed = getColoringForName(TexColoringNames.ENUM_ARG_INCORRECT).apply(proposed);
                                }
                            } else {
                                CommandNode cnode = anode.getCommand();
                                
                                Node parent = cnode.getParent();
                                
                                if (parent instanceof BlockNode) {
                                    doSpell = false;
                                    
                                    BlockNode bnode = (BlockNode) parent;
                                    Environment env = source.getEnvironment(bnode.getStartingPosition(), bnode.getBlockName());
                                    
                                    if (env != null) {
                                        proposed = getColoringForName(TexColoringNames.ENUM_ARG_CORRECT).apply(proposed);
                                    } else {
                                        proposed = getColoringForName(TexColoringNames.ENUM_ARG_INCORRECT).apply(proposed);
                                    }
                                } else {
                                    doSpell = !anode.getArgument().isCodeLike();
                                }
                            }
                        }
                    }
                    
                    if (dictionary != null && !dictionary.isEmpty() && doSpell) {
                        CharSequence word = token.getText();
                        
                        if (word.length() != 0) {
                            Integer type = dictionary.findWord(word.toString());
                            
                            switch (type.intValue()) {
                                case Dictionary.BAD_INT: proposed = getColoringForName(TexColoringNames.WORD_BAD).apply(proposed); break;
                                case Dictionary.INCORRECT_INT: proposed = getColoringForName(TexColoringNames.WORD_INCORRECT).apply(proposed); break;
                                case Dictionary.INCOMPLETE_INT: proposed = getColoringForName(TexColoringNames.WORD_INCOMPLETE).apply(proposed); break;
                                case Dictionary.CORRECT_INT: /*nothing*/; break;
                                default: /*should never happen*/;break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            } finally {
                if (lock != null)
                    source.unlock(lock);
            }
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
