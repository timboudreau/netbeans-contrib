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
 * The Original Software is the LaTeX module.
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
package org.netbeans.modules.latex.ui;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.gsf.api.CancellableTask;
import org.netbeans.napi.gsfret.source.CompilationController;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source;
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
                    LaTeXParserResult lpr = LaTeXParserResult.get(parameter);
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
