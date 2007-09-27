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
 * The Original Software is the Viewer module.
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

import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.api.retouche.source.CompilationController;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.retouche.source.Phase;
import org.netbeans.api.retouche.source.Source;
import org.netbeans.editor.BaseAction;
import org.netbeans.editor.DialogSupport;
import org.netbeans.editor.ext.ExtKit.GotoDeclarationAction;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.LabelInfo;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.openide.util.Exceptions;

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
            int count = org.netbeans.modules.latex.editor.Utilities.countWords(target.getDocument());
            
            MessageFormat format = new MessageFormat("The document contains {0} words.");
            
            result.add(new JLabel(format.format(new Object[] {new Integer(count)} )));
            
            dialog = DialogSupport.createDialog("Word count", result, true,
                new JButton[] {new JButton("OK")}, false, 0, 0, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if ("OK".equals(evt.getActionCommand()))
                            dialog.setVisible(false);
                    }
            });
            
            dialog.setVisible(true);
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
        
        private List<String> citeValues(List<PublicationEntry> references) {
            List<String> result = new ArrayList<String>();
            
            for (PublicationEntry entry : references) {
                result.add(entry.getTag() + ":" + entry.getTitle()); //NOI18N
            }
            
            return result;
        }
        
        public void actionPerformed(ActionEvent evt, final JTextComponent target) {
            Source s = Source.forDocument(target.getDocument());
            final List references = new LinkedList();
            
            try {
                s.runUserActionTask(new CancellableTask<CompilationController>() {
                    public void cancel() {}
                    public void run(CompilationController parameter) throws Exception {
                        parameter.toPhase(Phase.RESOLVED);
                        LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();
                        if (type == CITE) {
                            references.addAll(Utilities.getDefault().getAllBibReferences(lpr));
                        } else {
                            references.addAll(new ArrayList(org.netbeans.modules.latex.model.Utilities.getDefault().getLabels(lpr)));
                        }
                    }
                }, true);
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            }
            
            if (type == REF)
                Collections.sort(references);
            
            final JCitePanel result = new JCitePanel(type == CITE ? citeValues(references) : references, type == CITE ? "Cite" : "Ref");
            
            dialog = DialogSupport.createDialog(type == CITE ? "Add Citation" : "Add Reference", result, true,
                new JButton[] {new JButton("Add"), new JButton("Cancel")}, false, 0, 0, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if ("Add".equals(evt.getActionCommand())) {
                            dialog.setVisible(false);
                            
                            int resultIndex = result.getSelected();
                            
                            if (resultIndex == (-1))
                                return ;
                            
                            String result;
                            Object ref = references.get(resultIndex);
                            
                            result = type ==CITE ? ((PublicationEntry) ref).getTag() : ((LabelInfo) ref).getLabel();
                            
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
                            dialog.setVisible(false);
                        }
                    }
            });
            
            dialog.setVisible(true);
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
