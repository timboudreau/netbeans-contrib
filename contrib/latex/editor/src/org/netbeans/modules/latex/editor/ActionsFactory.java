/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the Viewer module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.lexer.Token;
import org.netbeans.editor.BaseAction;
import org.netbeans.editor.DialogSupport;
import org.netbeans.editor.ext.ExtKit;
import org.netbeans.modules.latex.model.LabelInfo;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.InputNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.Line;

/**
 *
 * @author Jan Lahoda
 */
public final class ActionsFactory {
    
    public static final String GO_TO_DECLARATION_ACTION = "go-to-declaration-action"; //NOI18N
    public static final String CITE_ACTION = "cite-action"; //NOI18N
    public static final String REF_ACTION = "ref-action"; //NOI18N
    public static final String WORD_COUNT_ACTION = "word-count-action"; //NOI18N
    public static final String PUT_SYMBOL_INTO_EDITOR_ACTION = "put-symbol-into-editor-action"; //NOI18N
    public static final String BUILD_APPROXIMATE_WORD_LIST_ACTION = "build-approximate-word-list"; //NOI18N

    /** no instances */
    private ActionsFactory() {
    }
    
    public static class WordCountAction extends BaseAction {
        
        public WordCountAction() {
            super(WORD_COUNT_ACTION);
        }
        
        private Dialog dialog;
        
        public void actionPerformed(ActionEvent evt, JTextComponent target) {
            dialog = null;
            
            JPanel result = new JPanel();
            int count = Utilities.countWords(target.getDocument());
            
            MessageFormat format = new MessageFormat("The document contains {0} words.");
            
            result.add(new JLabel(format.format(new Object[] {new Integer(count)} )));
            
            dialog = DialogSupport.createDialog("Word count", result, true,
                new JButton[] {new JButton("OK")}, false, 0, 0, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if ("OK".equals(evt.getActionCommand()))
                            dialog.hide();
                    }
            });
            
            dialog.show();
        }
        
    }
    
    public static class CiteAction extends BaseAction {
        
        private int type;
        public static final int CITE = 1;
        public static final int REF  = 2;
        
        public CiteAction(int type) {
            super(type == CITE ? CITE_ACTION : REF_ACTION, MAGIC_POSITION_RESET | ABBREV_RESET );
            this.type = type == CITE ? CITE : REF;
        }
        
        Dialog dialog;
        
        private List citeValues(List references) {
            Iterator            referencesIter = references.iterator();
            List                result = new ArrayList();
            
            while (referencesIter.hasNext()) {
                AnalyseBib.BibRecord record = (AnalyseBib.BibRecord) referencesIter.next();
                
                result.add(record.getRef() + ":" + record.getTitle()); //NOI18N
            }
            
            return result;
        }
        
        public void actionPerformed(ActionEvent evt, final JTextComponent target) {
            Object file = org.netbeans.modules.latex.model.Utilities.getDefault().getFile(target.getDocument());
            LaTeXSource source = LaTeXSource.get(file);
            
            final List references = type == CITE ? Utilities.getAllBibReferences(source) : new ArrayList(org.netbeans.modules.latex.model.Utilities.getDefault().getLabels(source));
            
            if (type == REF)
                Collections.sort(references);
            
            final JCitePanel result = new JCitePanel(type == CITE ? citeValues(references) : references, type == CITE ? "Cite" : "Ref");
            
            dialog = DialogSupport.createDialog(type == CITE ? "Add Citation" : "Add Reference", result, true,
                new JButton[] {new JButton("Add"), new JButton("Cancel")}, false, 0, 0, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if ("Add".equals(evt.getActionCommand())) {
                            dialog.hide();
                            
                            int resultIndex = result.getSelected();
                            
                            if (resultIndex == (-1))
                                return ;
                            
                            String result;
                            Object ref = references.get(resultIndex);
                            
                            result = type ==CITE ? ((AnalyseBib.BibRecord) ref).getRef() : ((LabelInfo) ref).getLabel();
                            
                            if (result != null) {
                                int dot = target.getCaret().getDot();
                                
                                try {
                                    target.getDocument().insertString(dot, (type == CITE ? "\\cite{" : "\\ref{") + result + "}", null); //NOI18N
                                } catch (BadLocationException e) {
                                    ErrorManager.getDefault().notify(e);
                                }
                            }
                        }
                        
                        if ("Cancel".equals(evt.getActionCommand())) {
                            dialog.hide();
                        }
                    }
            });
            
            dialog.show();
        }
        
    }

    public static class GoToDeclarationAction extends BaseAction {
        
        public GoToDeclarationAction() {
            super(GO_TO_DECLARATION_ACTION, SAVE_POSITION);
        }
        
        public void actionPerformed(ActionEvent actionEvent, JTextComponent target) {
            Node found = LaTeXGoToImpl.getDefault().getGoToNode(target.getDocument(), target.getCaretPosition(), true);
            
            if (found == null) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
        
    }
    
    public static class BuildApproximateWordList extends BaseAction {
        
        public BuildApproximateWordList() {
            super(BUILD_APPROXIMATE_WORD_LIST_ACTION);
        }
        
        public JMenuItem getPopupMenuItem(JTextComponent component) {
            Document doc = component.getDocument();
            Token t = Utilities.getToken(doc, component.getCaret().getDot());
            int start = Utilities.getStartingOffset(doc, component.getCaret().getDot()) - 1;
            JMenu item = new JMenu("Spelling");
            
            if (t.getId() != TexLanguage.WORD) {
                JMenuItem empty = new JMenuItem("Empty");
                
                empty.setEnabled(false);
                
                item.add(empty);
            } else {
                String tokenText = t.getText().toString();
                LaTeXSource source = org.netbeans.modules.latex.model.Utilities.getDefault().getSource(doc);
                Dictionary d = Dictionary.getDictionary(source.getDocumentLocale());
                List spell = d.getSimilarWords(tokenText);
                
                if (spell.size() == 0) {
                    JMenuItem empty = new JMenuItem("Empty");
                    
                    empty.setEnabled(false);
                    
                    item.add(empty);
                } else {
                    Iterator i = spell.iterator();
                    
                    while (i.hasNext()) {
                        String word = (String) i.next();
                        JMenuItem menuItem = new JMenuItem(word);
                        
                        menuItem.addActionListener(new ActionListenerImpl(doc, start, tokenText.length(), word));
                        item.add(menuItem);
                    }
                }
            }
            
            return item;
        }
        
        public void actionPerformed(ActionEvent evt, JTextComponent target) {
            //nothing...
        }
        
        private static class ActionListenerImpl implements ActionListener {
            
            private Document doc;
            private int      offset;
            private int      len;
            private String   word;
            
            public ActionListenerImpl(Document doc, int offset, int len, String word) {
                this.doc = doc;
                this.offset = offset;
                this.len = len;
                this.word = word;
            }
            
            public void actionPerformed(ActionEvent e) {
                try {
                    doc.remove(offset, len);
                    doc.insertString(offset, word, null);
                } catch (BadLocationException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
            
        }
        
    }
    
}
