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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
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
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.CommandPackage;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.LaTeXSource.Lock;
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
            //TODO:may this cause deadlock?:
            NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {
                public void run() {
                    LaTeXSource source   = LaTeXSource.get(Utilities.getDefault().getFile(doc));
                    Lock lock = null;
                    
                    int     removeFirstS  = (-1);
                    int     removeFirstE  = (-1);
                    int     removeSecondS = (-1);
                    int     removeSecondE = (-1);
                    
                    boolean insert        = false;
                    
                    try {
                        lock = source.lock(true);
                        
                        if (lock != null) {
                            Node node = source.findNode(doc, pane.getCaret().getDot());
                            
                            CommandNode cn = findCorrespondingNode(node); //TODO: CommandNode?
                            CommandDescription currentType = findCorrespondingCommandDescription(cn);
                            
                            if (currentType.equals(wantedType))
                                return ;
                            
                            if (!currentType.isEmpty()) {
                                removeFirstS  = cn.getStartingPosition().getOffsetValue();
                                removeFirstE  = cn.getArgument(/*!!!*/0).getStartingPosition().getOffsetValue() + 1;
                                removeSecondS = cn.getEndingPosition().getOffsetValue();
                                removeSecondE = removeSecondS + 1;
                            }
                            
                            insert = !wantedType.isEmpty();
                        }
                    } catch (IOException e) {
                        ErrorManager.getDefault().notify(e);
                    } finally {
                        if (lock != null)
                            source.unlock(lock);
                    }
                    
                    try {
                        if (removeFirstS != (-1)) {
                            int removeFirstLength = removeFirstE - removeFirstS;
                            
                            doc.remove(removeFirstS, removeFirstLength);
                            doc.remove(removeSecondS - removeFirstLength - 1, removeSecondE - removeSecondS);
                            pane.select(removeFirstS, removeSecondE - removeFirstLength - 2);
                        }
                        
                        if (insert) {
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
    
    protected static class CommandDescription {
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
        
        public boolean isEnabled() {
            if (command.length() == 0)
                return true;
            
            JEditorPane pane = UIUtilities.getCurrentEditorPane();
            
            if (pane == null)
                return true;
            
            LaTeXSource source = Utilities.getDefault().getSource(pane.getDocument());
            
            if (source == null)
                return true;
            
            DocumentNode node = source.getDocument();
            
            if (node == null) {
                return true;
            }
            
            CommandPackage cp = CommandPackage.getCommandPackageForName(node.getDocumentClass());
            
            if (cp == null)
                return true;
            
            Map commands = cp.getCommands();
            
            if (commands == null)
                return true;
            
            return commands.get(command) != null;
        }
    }
    
}
