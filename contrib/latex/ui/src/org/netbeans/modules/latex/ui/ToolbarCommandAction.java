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
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.CommandPackage;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.Node;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;

/**
 *
 * @author Jan Lahoda
 */
public abstract class ToolbarCommandAction extends AbstractAction {
    
    /** Creates a new instance of ToolbarCommandAction */
    public ToolbarCommandAction() {
    }
    
    protected void doUpdate(final CommandDescription wantedType) {
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
            Source source = Source.forDocument(doc);
            final int[]     removeFirstS  = {(-1)};
            final int[]     removeFirstE  = {(-1)};
            final int[]     removeSecondS = {(-1)};
            final int[]     removeSecondE = {(-1)};

            final boolean[] insert        = {false};

            source.runUserActionTask(new CancellableTask<CompilationController>() {
                public void cancel() {}
                public void run(CompilationController parameter) throws Exception {
                    parameter.toPhase(Phase.RESOLVED);
                    LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();
                    Node node = lpr.getCommandUtilities().findNode(doc, pane.getCaret().getDot());

                    CommandNode cn = findCorrespondingNode(node); //TODO: CommandNode?
                    CommandDescription currentType = findCorrespondingCommandDescription(cn);

                    if (currentType.equals(wantedType))
                        return ;

                    if (!currentType.isEmpty()) {
                        removeFirstS[0]  = cn.getStartingPosition().getOffsetValue();
                        removeFirstE[0]  = cn.getArgument(/*!!!*/0).getStartingPosition().getOffsetValue() + 1;
                        removeSecondS[0] = cn.getEndingPosition().getOffsetValue();
                        removeSecondE[0] = removeSecondS[0] + 1;
                    }

                    insert[0] = !wantedType.isEmpty();
                }
            }, enabled);
                    
            NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {
                public void run() {
                    try {
                        if (removeFirstS[0] != (-1)) {
                            int removeFirstLength = removeFirstE[0] - removeFirstS[0];
                            
                            doc.remove(removeFirstS[0], removeFirstLength);
                            doc.remove(removeSecondS[0] - removeFirstLength - 1, removeSecondE[0] - removeSecondS[0]);
                            pane.select(removeFirstS[0], removeSecondE[0] - removeFirstLength - 2);
                        }
                        
                        if (insert[0]) {
                            String selection = pane.getSelectedText();
                            
                            if (selection == null)
                                selection = "";
                            
                            int    dot       = pane.getCaret().getDot();
                            int    removeLen = pane.getSelectionEnd() - pane.getSelectionStart();
                            
                            doc.remove(pane.getSelectionStart(), removeLen);
                            doc.insertString(dot - removeLen, wantedType.command + "{" + selection + "}", null);
                        }
                    } catch (BadLocationException e) {
                        ErrorManager.getDefault().notify(e);
                        return ;
                    }
                }
            });
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
            return ;
        }
        
    }
    
//    protected abstract CommandNode findCorrespondingNode(Node node);
    
    protected abstract CommandDescription findCorrespondingCommandDescription(CommandNode node);
    
    protected CommandNode findCorrespondingNode(Node command) {
        while (command != null) {
            if (command instanceof CommandNode) {
                CommandDescription d = findCorrespondingCommandDescription((CommandNode) command);
                
                if (!d.isEmpty())
                    return (CommandNode) command;
            }
            
            command = command.getParent();
        }
        
        return null;
    }
    
    protected static class CommandDescription implements ToolbarUpdater.ToolbarUpdatable {
        private String displayName;
        private String command;
        private boolean isEmpty;
        
        public CommandDescription(String displayName, String command, boolean isEmpty) {
            this.displayName = displayName;
            this.command = command;
            this.isEmpty = isEmpty;
        }
        
        public String toString() {
            return displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getCommand() {
            return command;
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
            if (command.length() == 0) {
                setEnabled(true);
                return ;
            }
            
            DocumentNode node = lpr.getDocument();
            
            CommandPackage cp = CommandPackage.getCommandPackageForName(node.getDocumentClass());
            
            if (cp == null) {
                setEnabled(true);
                return ;
            }
            
            Map commands = cp.getCommands();
            
            if (commands == null) {
                setEnabled(true);
                return ;
            }
            
            setEnabled(commands.get(command) != null);
        }
    }
    
}
