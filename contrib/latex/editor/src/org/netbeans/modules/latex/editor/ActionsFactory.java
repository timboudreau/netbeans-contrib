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
 * The Original Software is the Viewer module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.lexer.Token;
import org.netbeans.editor.BaseAction;
import org.netbeans.editor.DialogSupport;
import org.netbeans.editor.ext.ExtKit.GotoDeclarationAction;
import org.netbeans.modules.latex.model.LabelInfo;
import org.netbeans.modules.latex.model.command.LaTeXSource;

/**
 *
 * @author Jan Lahoda
 */
public final class ActionsFactory {
    
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

    public static class GoToDeclarationActionImpl extends GotoDeclarationAction {
        
        public GoToDeclarationActionImpl() {
        }
        
        public void actionPerformed(ActionEvent actionEvent, JTextComponent target) {
            int[] span = LaTeXGoToImpl.getDefault().getGoToNode(target.getDocument(), target.getCaretPosition(), true);
            
            if (span == null) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
        
    }

    
}
