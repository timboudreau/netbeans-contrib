/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
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
import org.netbeans.modules.latex.model.command.BlockNode;
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
                            
                            BlockNode bn = findCorrespondingNode(node); //TODO: CommandNode?
                            EnvironmentDescription currentType = findCorrespondingEnvironmentDescription(bn);
                            
                            if (currentType.equals(wantedType))
                                return ;
                            
                            if (!currentType.isEmpty()) {
                                removeFirstS  = bn.getBeginCommand().getStartingPosition().getOffsetValue();
                                removeFirstE  = bn.getContent().getStartingPosition().getOffsetValue();
                                removeSecondS = bn.getEndCommand().getStartingPosition().getOffsetValue() + 1;
                                removeSecondE = bn.getEndCommand().getEndingPosition().getOffsetValue()+ 1;
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
                            pane.select(removeFirstS, removeSecondS - removeFirstLength - 1);
                        }
                        
                        if (insert) {
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
    
    protected static class EnvironmentDescription {
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
        
        public boolean isEnabled() {
            if (environment.length() == 0)
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
            
            Map environments = cp.getEnvironments();
            
            if (environments == null)
                return true;
            
            return environments.get(environment) != null;
        }
    }
    
}
