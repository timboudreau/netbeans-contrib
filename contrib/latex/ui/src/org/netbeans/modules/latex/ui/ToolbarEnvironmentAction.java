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
                    LaTeXParserResult lpr = LaTeXParserResult.get(cc);
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
