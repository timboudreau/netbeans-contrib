
package org.netbeans.modules.fort.core.editor;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.fort.model.AbstractFModel;
import org.netbeans.modules.fort.model.FModel;
import org.netbeans.modules.fort.model.FModelAccessor;
import org.netbeans.modules.fort.model.FState;
import org.netbeans.modules.fort.model.FSyntax;
import org.netbeans.modules.fort.model.lang.FCompoundStatement;
import org.netbeans.modules.fort.model.lang.syntax.FParser;
import org.netbeans.modules.fort.model.util.FModelUtilities;
import org.netbeans.modules.fort.model.util.Pair;
import org.netbeans.spi.lexer.Lexer;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

/**
 *
 * @author Andrey Gubichev
 */
public class FModelAccessorImpl implements FModelAccessor {            
    
    private static final Logger LOGGER = 
            Logger.getLogger(FModelAccessorImpl.class.getName());
    
    private static final RequestProcessor reqProc = 
            new RequestProcessor ("parser",1);
    
    private static final int INPUT_REACTION_DELAY = 500;
            
    private FState lastState;   
    
    private final AbstractFModel model;
    private final Document doc;
    private final FSyntax synt;
    
    private AsmDocumentListener docListener;
               
    private final RequestProcessor.Task reparseTask;
      
    private List<ParseListener> listeners;
       
    public FModelAccessorImpl(FModel model, FSyntax synt, 
                                Document doc) {        
        
        this.model = (AbstractFModel) model;
        this.synt = synt;           
        this.doc = doc;
        
        reparseTask = reqProc.create(new ReparseTask(), true);
        listeners = 
                Collections.synchronizedList(new LinkedList<ParseListener>());
        notifyChange(true);
        
        docListener = new AsmDocumentListener(doc);
    }
    
    public static String getText(final Document doc) {
        final String []text = new String[1];
        
        doc.render(new Runnable() {
            public void run() {
                try {
                    text[0] = doc.getText(0, doc.getLength() - 1);
                } catch (BadLocationException ex) {
                    text[0] = "";
                    LOGGER.log(Level.INFO, "error in getText()"); // NOI18N
                }
            }
        });
        
        return text[0];
    }
    public FState getState() {
        if (lastState == null)
            notifyChange(true);
        
        return lastState;
    }
                 
    public void addParseListener(FModelAccessor.ParseListener list) {
        listeners.add(list);
    }
            
    
    private void notifyChange(boolean immediate) {
        
        int delay = immediate ? 0 : INPUT_REACTION_DELAY;                        
        
        reparseTask.schedule(delay);               
    }
    
    private void fireParsed() {
        for (ParseListener l: listeners) {
            l.notifyParsed();
        }
    }        
    
    private class ReparseTask implements Runnable {
                                                                
        public void run() {
            
            long start = System.currentTimeMillis();
            
            FParser parser = synt.createParser();
            FCompoundStatement res = parser.parse(     
                        new StringReader(getText(doc)) );
            
            long end = System.currentTimeMillis();
            
            LOGGER.log(Level.INFO, "parse time: " + (end - start));
            
            if (!Thread.currentThread().isInterrupted()) {
                lastState = new FStateImpl(res, parser.getFuncsBounds());               
                fireParsed();
            }
        }        
    }
        
    
    private class FStateImpl implements FState {
       
        private FCompoundStatement elements;
        private Map<String, Object> prop;
        
        public FStateImpl(FCompoundStatement elements, List<Integer> globals) {
            this.elements = elements;
            
            prop = new HashMap<String, Object>();
            prop.put("", globals);            
        }
        
        public FCompoundStatement getElements() {
            return elements;
        }

        public boolean isActual() {
            return lastState == this;
        }
        
        public Pair<FCompoundStatement, FCompoundStatement> resolveLink(int pos) {        
            return Pair.getPair(null, null);            
        }

        
        public Object getProperty(String name) {
            return prop.get(name);
        }    
        
   
         
    }
    
    public Lexer getHighlightLexer() {
        return null;
    }
    
    ////////////////////////

    private class AsmDocumentListener implements DocumentListener {
        
        public AsmDocumentListener(Document doc) {
            doc.addDocumentListener(WeakListeners.document(this, doc));
        }
        
        public void insertUpdate(DocumentEvent e) {
            notifyChange(false);
        }

        public void removeUpdate(DocumentEvent e) {
            notifyChange(false);        
        }

        public void changedUpdate(DocumentEvent e) {
            // nothing
        }
    }
    
    
    
}