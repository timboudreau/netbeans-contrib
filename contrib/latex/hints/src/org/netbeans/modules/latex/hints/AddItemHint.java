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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.latex.hints;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.fpi.gsf.CancellableTask;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.latex.editor.TexLanguage;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.lexer.TexTokenId;
import org.netbeans.napi.gsfret.source.CompilationInfo;
import org.netbeans.napi.gsfret.source.ModificationResult;
import org.netbeans.napi.gsfret.source.ModificationResult.Difference;
import org.netbeans.napi.gsfret.source.ModificationResult.Difference.Kind;
import org.netbeans.napi.gsfret.source.Source;
import org.netbeans.napi.gsfret.source.WorkingCopy;
import org.netbeans.napi.gsfret.source.support.CaretAwareSourceTaskFactory;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.NbDocument;
import org.openide.text.PositionRef;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class AddItemHint implements HintProvider {

    public boolean accept(CompilationInfo info, Node n) {
        return n instanceof CommandNode && n.hasAttribute("item-command") && ((CommandNode) n).getCommand().getArgumentCount() == 2 /*XXX*/;
    }

    public List<ErrorDescription> computeHints(CompilationInfo info, Node n) throws Exception {
        return computeHints(info, n, CaretAwareSourceTaskFactory.getLastPosition(info.getFileObject()));
    }
    
    List<ErrorDescription> computeHints(CompilationInfo info, Node n, int offset) throws Exception {
        Document doc = info.getDocument();
        
        if (doc == null) {
            return null;
        }
        
        List<Fix> fixes = new LinkedList<Fix>();
        
        boolean hasFirstArgument = ((CommandNode) n).getArgument(0).isPresent();
        ArgumentNode secondArgument = ((CommandNode) n).getArgument(1);
        Iterator<? extends Token<TexTokenId>> tokens = secondArgument.getDeepNodeTokensCopy().iterator();
        boolean hasBracketsInTheSecondArgument = tokens.hasNext() && tokens.next().id() == TexTokenId.COMP_BRACKET_LEFT;
        int start = secondArgument.getStartingPosition().getOffsetValue();
        int end = secondArgument.getEndingPosition().getOffsetValue();

        if (start > offset || offset > end) {
            return null;
        }
        
        if (checkOffset(info.getTokenHierarchy().tokenSequence(TexLanguage.description()).subSequence(start, offset), offset, TexTokenId.COMP_BRACKET_LEFT)) {
            fixes.add(new FixImpl(doc, info.getSource(), n.getStartingPosition().getOffset(), true, hasFirstArgument, hasBracketsInTheSecondArgument));
        }
        
        if (checkOffset(info.getTokenHierarchy().tokenSequence(TexLanguage.description()).subSequence(offset, end), offset, TexTokenId.COMP_BRACKET_RIGHT)) {
            fixes.add(new FixImpl(doc, info.getSource(), n.getEndingPosition().getOffset(), false, hasFirstArgument, hasBracketsInTheSecondArgument));
        }
        
        if (!fixes.isEmpty()) {
            return Collections.singletonList(ErrorDescriptionFactory.createErrorDescription(Severity.HINT, "Add Item", fixes, info.getFileObject(), offset, offset));
        } else {
            return null;
        }
    }
    
    private boolean checkOffset(TokenSequence<TexTokenId> tokens, int offset, TexTokenId bracket) {
        boolean wasBracket = false;
        
        while (tokens.moveNext()) {
            if (tokens.token().id() != TexTokenId.WHITESPACE && tokens.token().id() != bracket)
                return false;
            
            if (tokens.token().id() == bracket) {
                if (wasBracket) {
                    return false;
                } else {
                    wasBracket = true;
                }
            }
        }
        
        return true;
    }
        

    static final class FixImpl implements Fix {

        private Source s;
        private Position pos;
        private Document doc;
        private boolean beforeAfter;
        private boolean hasFirstArgument;
        private boolean hasBracketsInTheSecondArgument;

        public FixImpl(Document doc, Source s, Position pos, boolean beforeAfter, boolean hasFirstArgument, boolean hasBracketsInTheSecondArgument) {
            this.doc = doc;
            this.s = s;
            this.pos = pos;
            this.beforeAfter = beforeAfter;
            this.hasFirstArgument = hasFirstArgument;
            this.hasBracketsInTheSecondArgument = hasBracketsInTheSecondArgument;
        }
        
        public String getText() {
            return beforeAfter ? "Add \\item above" : "Add \\item below";
        }

        public ChangeInfo implement() throws Exception {
            final ModificationResult mr = computeResult();
            final Position[] pos = new Position[1];
            
            NbDocument.runAtomic((StyledDocument) doc, new Runnable() {
                public void run() {
                    try {
                        int offset = FixImpl.this.pos.getOffset();
                        
                        mr.commit();
                        
                        String textToInsert = computeTextToInsert();
                        
                        pos[0] = doc.createPosition(offset + textToInsert.indexOf('|'));
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });
            
            return new ChangeInfo(s.getFileObjects().iterator().next(), pos[0], pos[0]);
        }

        private ModificationResult computeResult() throws IOException {
            final ModificationResult mr = s.runModificationTask(new CancellableTask<WorkingCopy>() {
                public void cancel() {
                }

                public void run(WorkingCopy copy) throws Exception {
                    DataObject d = DataObject.find(copy.getFileObject());
                    EditorCookie ec = d.getLookup().lookup(EditorCookie.class);

                    if (ec instanceof CloneableEditorSupport) {
                        CloneableEditorSupport ces = (CloneableEditorSupport) ec;
                        PositionRef ref = ces.createPositionRef(pos.getOffset(), Position.Bias.Backward);
                        String textToInsert = computeTextToInsert();
                        
                        textToInsert = textToInsert.replace("|", "");
                        
                        copy.addDiff(new Difference(Kind.INSERT, ref, ref, null, textToInsert, "Add \\item"));
                    }
                }

            });

            return mr;
        }

        private String computeTextToInsert() {
            String textToInsert;

            if (hasFirstArgument) {
                if (hasBracketsInTheSecondArgument) {
                    textToInsert = "\\item[|]{}\n";
                } else {
                    textToInsert = "\\item[|] \n";
                }
            } else {
                if (hasBracketsInTheSecondArgument) {
                    textToInsert = "\\item{|}\n";
                } else {
                    textToInsert = "\\item |\n";
                }
            }

            return textToInsert;
        }
        
    }
    
}
