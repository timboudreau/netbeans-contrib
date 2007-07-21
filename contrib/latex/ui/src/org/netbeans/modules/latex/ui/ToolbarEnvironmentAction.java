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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.retouche.source.CompilationController;
import org.netbeans.api.retouche.source.Phase;
import org.netbeans.api.retouche.source.Source;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandPackage;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.Node;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public abstract class ToolbarEnvironmentAction extends AbstractAction {
    
    /** Creates a new instance of ToolbarCommandAction */
    public ToolbarEnvironmentAction() {
    }
    
    protected void doUpdate(final EnvironmentDescription wantedType) {
        final JEditorPane pane    = UIUtilities.getCurrentEditorPane();
        
        if (pane == null)
            return ;
        
        if (pane.getEditorKit().getContentType() != "text/x-tex") {
            //The Action should not be even enabled!
            Toolkit.getDefaultToolkit().beep();
            return ;
        }

        final Document    doc      = pane.getDocument();
        
        try {
            final int[]     removeFirstS  = {(-1)};
            final int[]     removeFirstE  = {(-1)};
            final int[]     removeSecondS = {(-1)};
            final int[]     removeSecondE = {(-1)};

            final boolean[] insert        = {false};

            Source source = Source.forDocument(doc);

            source.runUserActionTask(new CancellableTask<CompilationController>() {
                public void cancel() {}

                public void run(CompilationController cc) throws Exception {
                    cc.toPhase(Phase.RESOLVED);
                    LaTeXParserResult lpr = (LaTeXParserResult) cc.getParserResult();
                Node node = lpr.getCommandUtilities().findNode(doc, pane.getCaret().getDot());

                BlockNode bn = findCorrespondingNode(node); //TODO: CommandNode?
                EnvironmentDescription currentType = findCorrespondingEnvironmentDescription(bn);

                if (currentType.equals(wantedType))
                    return ;

                if (!currentType.isEmpty()) {
                    removeFirstS[0]  = bn.getBeginCommand().getStartingPosition().getOffsetValue();
                    removeFirstE[0]  = bn.getContent().getStartingPosition().getOffsetValue();
                    removeSecondS[0] = bn.getEndCommand().getStartingPosition().getOffsetValue() + 1;
                    removeSecondE[0] = bn.getEndCommand().getEndingPosition().getOffsetValue()+ 1;
                }

                insert[0] = !wantedType.isEmpty();
            }
            }, true);
                    
            //TODO:may this cause deadlock?:
            NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {
                public void run() {
                    try {
                        if (removeFirstS[0] != (-1)) {
                            int removeFirstLength = removeFirstE[0] - removeFirstS[0];
                            
                            doc.remove(removeFirstS[0], removeFirstLength);
                            doc.remove(removeSecondS[0] - removeFirstLength - 1, removeSecondE[0] - removeSecondS[0]);
                            pane.select(removeFirstS[0], removeSecondS[0] - removeFirstLength - 1);
                        }
                        
                        if (insert[0]) {
                            String selection = pane.getSelectedText();
                            
                            if (selection == null)
                                selection = "";
                            
                            int    dot       = pane.getCaret().getDot();
                            int    removeLen = pane.getSelectionEnd() - pane.getSelectionStart();
                            
                            doc.remove(pane.getSelectionStart(), removeLen);
                            doc.insertString(dot - removeLen, "\\begin{" + wantedType.getEnvironment() + "}" + selection + "\\end{" + wantedType.getEnvironment() + "}", null);
                        }
                    } catch (BadLocationException e) {
                        ErrorManager.getDefault().notify(e);
                        return ;
                    }
                }
            });
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
            return ;
        }
        
    }
    
//    protected abstract CommandNode findCorrespondingNode(Node node);
    
    protected abstract EnvironmentDescription findCorrespondingEnvironmentDescription(BlockNode node);
    
    protected BlockNode findCorrespondingNode(Node command) {
        while (command != null) {
            if (command instanceof BlockNode) {
                EnvironmentDescription d = findCorrespondingEnvironmentDescription((BlockNode) command);
                
                if (!d.isEmpty())
                    return (BlockNode) command;
            }
            
            command = command.getParent();
        }
        
        return null;
    }
    
    protected static class EnvironmentDescription implements ToolbarUpdater.ToolbarUpdatable {
        private String displayName;
        private String environment;
        private boolean isEmpty;
        
        public EnvironmentDescription(String displayName, String environment, boolean isEmpty) {
            this.displayName = displayName;
            this.environment = environment;
            this.isEmpty = isEmpty;
        }
        
        public String toString() {
            return displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getEnvironment() {
            return environment;
        }
        
        public boolean isEmpty() {
            return isEmpty;
        }
        
        public synchronized boolean isEnabled() {
            return enabled;
        }

        private boolean enabled = true;
        
        private synchronized void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public void update(LaTeXParserResult lpr) {
            if (environment.length() == 0) {
                setEnabled(true);
                return ;
            }
            
            CommandPackage cp = CommandPackage.getCommandPackageForName(lpr.getDocument().getDocumentClass());
            
            if (cp == null) {
                setEnabled(true);
                return ;
            }
            
            Map environments = cp.getEnvironments();
            
            if (environments == null) {
                setEnabled(true);
                return ;
            }
            
            setEnabled(environments.get(environment) != null);
        }
    }
    
}
