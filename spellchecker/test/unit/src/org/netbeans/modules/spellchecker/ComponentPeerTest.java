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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Jan Lahoda
 */
public class ComponentPeerTest extends NbTestCase {
    
    public ComponentPeerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        UnitUtilities.setLookup(new Object[0], this.getClass().getClassLoader());
    }

    public void testTokenListUpdates() throws Exception {
        FileObject dataDir = UnitUtilities.makeScratchDir(this);
        assertNotNull(dataDir);
        
        FileObject dataFile = dataDir.createData("test", "txt");
        
        final TokenListImpl i = new TokenListImpl();
        ComponentPeer.ACCESSOR = new LookupAccessor() {
            public Dictionary lookupDictionary(Locale l) {
                return new DictionaryImpl();
            }
            public TokenList lookupTokenList(Document doc) {
                return i;
            }
        };
        
        JTextComponent pane = new JEditorPane();
        
        pane.setText("text text text");
        pane.getDocument().putProperty(Document.StreamDescriptionProperty, DataObject.find(dataFile));
        
        i.latch = new CountDownLatch(1);
        
        ComponentPeer.assureInstalled(pane);
        
        assertTrue(i.latch.await(10, TimeUnit.SECONDS));
        
        i.latch = new CountDownLatch(1);
        
        i.fireChangeEvent();
        
        assertTrue(i.latch.await(10, TimeUnit.SECONDS));
        
        i.latch = new CountDownLatch(1);
        
        i.fireChangeEvent();
        
        assertTrue(i.latch.await(10, TimeUnit.SECONDS));
    }
    
    public void testReclaimable() throws Exception {
        final Reference[] references = new Reference[3];
        final Exception[] exc = new Exception[1];
        
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    FileObject dataDir = UnitUtilities.makeScratchDir(ComponentPeerTest.this);
                    assertNotNull(dataDir);
                    
                    FileObject dataFile = dataDir.createData("test", "txt");
                    
                    final TokenListImpl[] i = new TokenListImpl[] {new TokenListImpl()};
                    ComponentPeer.ACCESSOR = new LookupAccessor() {
                        public Dictionary lookupDictionary(Locale l) {
                            return new DictionaryImpl();
                        }
                        public TokenList lookupTokenList(Document doc) {
                            return i[0];
                        }
                    };
                    
                    JTextComponent pane = new JEditorPane();
                    
                    pane.setText("text text text");
                    pane.getDocument().putProperty(Document.StreamDescriptionProperty, DataObject.find(dataFile));
                    
                    i[0].latch = new CountDownLatch(1);
                    
                    ComponentPeer.assureInstalled(pane);
                    
                    assertTrue(i[0].latch.await(10, TimeUnit.SECONDS));
                    
                    i[0].doc = pane.getDocument();
                    
                    references[0] = new WeakReference(pane);
                    references[1] = new WeakReference(i[0].doc);
                    references[2] = new WeakReference(i[0]);
                    
                    pane = null;
                    i[0] = null;
                } catch (Exception e) {
                    exc[0] = e;
                }
            }
        });
                
        
        if (exc[0] != null)
            throw exc[0];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                JEditorPane p = new JEditorPane();
                
                p.setVisible(true);
                p.setVisible(false);
                
            }
        });
        
        for (Reference r : references) {
            assertGC("", r);
        }
    }

    private class DictionaryImpl implements Dictionary {
        public ValidityType validateWord(CharSequence word) {
            return ValidityType.VALID;
        }

        public List<String> findValidWordsForPrefix(CharSequence word) {
            return Collections.emptyList();
        }

        public List<String> findProposals(CharSequence word) {
            return Collections.emptyList();
        }
        
    }
    
    private class TokenListImpl implements TokenList {
        
        private CountDownLatch latch;
        
        private Document doc;
        
        public void setStartOffset(int offset) {
            latch.countDown();
        }

        public boolean nextWord() {
            return false;
        }

        public int getCurrentWordStartOffset() {
            throw new IllegalStateException();
        }

        public CharSequence getCurrentWordText() {
            throw new IllegalStateException();
        }

        private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
        
        public synchronized void addChangeListener(ChangeListener l) {
            listeners.add(l);
        }

        public synchronized void removeChangeListener(ChangeListener l) {
            listeners.remove(l);
        }
        
        public synchronized void fireChangeEvent() {
            ChangeEvent e = new ChangeEvent(this);
            
            for (ChangeListener l : listeners) {
                l.stateChanged(e);
            }
        }
        
    }
    
    protected boolean runInEQ() {
        if ("testTokenListUpdates".equals(getName()))
            return true;
        
        return false;
    }

}
