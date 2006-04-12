/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.spellchecker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.editor.Coloring;
import org.netbeans.modules.editor.highlights.spi.DefaultHighlight;
import org.netbeans.modules.editor.highlights.spi.Highlight;
import org.netbeans.modules.editor.highlights.spi.Highlighter;
import org.netbeans.modules.spellchecker.hints.DictionaryBasedHintsProvider;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.api.LocaleQuery;
import org.netbeans.modules.spellchecker.spi.dictionary.DictionaryProvider;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.netbeans.modules.spellchecker.spi.language.TokenListProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Template;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

/**
 *
 * @author Jan Lahoda
 */
public class ComponentPeer implements PropertyChangeListener, DocumentListener, ChangeListener {

    public static void assureInstalled(JTextComponent pane) {
        if (pane.getClientProperty(ComponentPeer.class) == null) {
            pane.putClientProperty(ComponentPeer.class, new ComponentPeer(pane));
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (doc != pane.getDocument()) {
            if (doc != null)
                doc.removeDocumentListener(this);
            reschedule();
            doc = pane.getDocument();
            doc.addDocumentListener(this);
        }
    }

    private JTextComponent pane;
    private Document doc;
    private List<Highlight> currentHighlights;

    private static Map<Document, List<Highlight>> doc2Highlights = new WeakHashMap<Document, List<Highlight>>();

    private RequestProcessor.Task checker = RequestProcessor.getDefault().create(new Runnable() {
        public void run() {
            try {
                process();
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
    });

    private void reschedule() {
        checker.schedule(100);
        cancel();
    }

    /** Creates a new instance of ComponentPeer */
    private ComponentPeer(JTextComponent pane) {
        this.pane = pane;
//        reschedule();
        pane.addPropertyChangeListener(this);
        doc = pane.getDocument();
        doc.addDocumentListener(this);
    }

    private Component parentWithListener;

    private int[] computeVisibleSpan() {
        Component parent = pane.getParent();

        if (parent instanceof JViewport) {
            JViewport vp = (JViewport) parent;

            Point start = vp.getViewPosition();
            Dimension size = vp.getExtentSize();
            Point end = new Point((int) (start.getX() + size.getWidth()), (int) (start.getY() + size.getHeight()));

            int startPosition = pane.viewToModel(start);
            int endPosition = pane.viewToModel(end);

            if (parentWithListener != vp) {
                vp.addChangeListener(WeakListeners.change(this, vp));
                parentWithListener = vp;
            }
            return new int[] {startPosition, endPosition};
        }

        return new int[] {0, pane.getDocument().getLength()};
    }

    private synchronized void updateCurrentVisibleSpan() {
        //check possible change in visible rect:
        int[] newSpan = computeVisibleSpan();
        
        if (currentVisibleRange == null || currentVisibleRange[0] != newSpan[0] || currentVisibleRange[1] != newSpan[1]) {
            currentVisibleRange = newSpan;
            reschedule();
        }
    }

    private int[] currentVisibleRange;

    private synchronized int[] getCurrentVisibleSpan() {
        if (currentVisibleRange == null) {
            currentVisibleRange = computeVisibleSpan();
        }

        return currentVisibleRange;
    }

    private void process() throws BadLocationException {
        FileObject file = getFile(doc);
        
        if (file == null) {
            return ;
        }
        
        long startTime = System.currentTimeMillis();

        resume();

        try {
            TokenList lCompute = null;
            
            for (TokenListProvider p : (Collection<TokenListProvider>)Lookup.getDefault().lookup(new Template(TokenListProvider.class)).allInstances()) {
                lCompute = p.findTokenList(doc);
                
                if (lCompute != null)
                    break;
            }
            
            if (lCompute == null) {
                //nothing to do:
                return ;
            }
            
            final TokenList l = lCompute;

            Dictionary d = getDictionary(doc);

            if (d == null)
                return ;
            
            final int[] span = getCurrentVisibleSpan();

//            System.err.println("span=" + span[0] + "-" + span[1]);
//
//            DefaultHighlight spanHL = new DefaultHighlight(new Coloring(null, null, Color.ORANGE), doc.createPosition(span[0]), doc.createPosition(span[1]));
//
//            Highlighter.getDefault().setHighlights(file, "spellchecker-span", Collections.singletonList(spanHL));

            if (span[0] == (-1)) {
                //not initialized yet:
                reschedule();
                return ;
            }

            l.setStartOffset(span[0]);

            List<Highlight> localHighlights = new ArrayList<Highlight>();
            final Document doc = pane.getDocument();
            
            final boolean[] cont = new boolean [1];
            final Position[] bounds = new Position[2];
            final CharSequence[] word = new CharSequence[1];
            
            while (!isCanceled()) {
                doc.render(new Runnable() {
                    public void run() {
                        if (isCanceled()) {
                            cont[0] = false;
                            return ;
                        }
                        
                        if (cont[0] = l.nextWord()) {
                            if (l.getCurrentWordStartOffset() > span[1]) {
                                cont[0] = false;
                                return ;
                            }
                            try {
                                word[0] = l.getCurrentWordText();
                                //XXX: maybe very slow, creating positions for all words:
                                bounds[0] = NbDocument.createPosition(doc, l.getCurrentWordStartOffset(), Position.Bias.Forward);
                                bounds[1] = NbDocument.createPosition(doc, l.getCurrentWordStartOffset() + word[0].length(), Position.Bias.Backward);
                            } catch (BadLocationException e) {
                                ErrorManager.getDefault().notify(e);
                                cont[0] = false;
                            }
                        }
                    }
                });
                
                if (!cont[0])
                    break;
                Highlight h = null;

//                System.err.println("word=" + word[0]);
                switch (d.validateWord(word[0])) {
                    case INVALID:
                        h = new DefaultHighlight(ERROR, bounds[0], bounds[1]);
                }
                
                if (h != null) {
                    localHighlights.add(h);
                }
            }
            setHighlights(file, localHighlights);
        } finally {
            System.err.println("Spellchecker time: " + (System.currentTimeMillis() - startTime));
        }
    }

    private void setHighlights(FileObject file, List<Highlight> newHighlights) {
        synchronized (ComponentPeer.class) {
            List<Highlight> all = doc2Highlights.get(doc);
            
            if (all == null) {
                doc2Highlights.put(doc, all = new ArrayList<Highlight>());
            } else {
                if (currentHighlights != null)
                    all.removeAll(currentHighlights);
            }
            
            all.addAll(newHighlights);

            currentHighlights =  newHighlights;
            
            Highlighter.getDefault().setHighlights(file, "spellchecker", all);
            DictionaryBasedHintsProvider.create().modified(doc);
        }
    }

    public static synchronized List<Highlight> getHighlightsCopy(Document doc) {
        List<Highlight> all = doc2Highlights.get(doc);

        if (all == null)
            return Collections.emptyList();

        return new ArrayList(all);
    }

    public static Dictionary getDictionary(Document doc) {
        FileObject file = getFile(doc);

        if (file == null)
            return null;

        Locale locale = LocaleQuery.findLocale(file);
        
        Dictionary d = null;
        
        for (DictionaryProvider p : (Collection<DictionaryProvider>)Lookup.getDefault().lookup(new Template(DictionaryProvider.class)).allInstances()) {
            d = p.getDictionary(locale);
            
            if (d != null)
                break;
        }

        return d;
    }

    private synchronized boolean isCanceled() {
        return cancel;
    }

    private synchronized void cancel() {
        cancel = true;
    }

    private synchronized void resume() {
        cancel = false;
    }

    private boolean cancel = false;

    private static final Coloring ERROR = new Coloring(null, 0, null, null, null, null, Color.RED);

    private static FileObject getFile(Document doc) {
        DataObject file = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);

        if (file == null)
            return null;

        return file.getPrimaryFile();
    }

    public void insertUpdate(DocumentEvent e) {
        documentUpdate();
    }

    public void removeUpdate(DocumentEvent e) {
        documentUpdate();
    }

    public void changedUpdate(DocumentEvent e) {
    }

    private void documentUpdate() {
        if (SwingUtilities.isEventDispatchThread()) {
            updateCurrentVisibleSpan();
            reschedule();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateCurrentVisibleSpan();
                    reschedule();
                }
            });
        }
    }

    public void stateChanged(ChangeEvent e) {
        updateCurrentVisibleSpan();
    }

}
