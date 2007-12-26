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
 * Contributor(s):
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.reorder;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 * Operations for swaping items in a list of paramters, arguments and array initializers.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class SwapOperations {
    enum Bias {BACKWARD, FORWARD};

    private static EnumSet<Tree.Kind> kindSet = EnumSet.of(
            Tree.Kind.METHOD
            ,Tree.Kind.METHOD_INVOCATION
            ,Tree.Kind.NEW_CLASS
            ,Tree.Kind.NEW_ARRAY
            );

    private static class Token {
        private String text;

        public Token(String text) {
            this.text = text;
        }

        String getText() {
            return text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    // Swap with pervious item
    static void swapBackward(final JEditorPane editorPane) {
        swap(editorPane, Bias.BACKWARD);
    }

    // Swap with next item
    static void swapForward(final JEditorPane editorPane) {
        swap(editorPane, Bias.FORWARD);
    }

    static void swap(final JEditorPane editorPane, final Bias bias) {
        // Non editable
        if (!editorPane.isEditable()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        final Document doc = editorPane.getDocument();

        // Is it Java code
        JavaSource javaSource = JavaSource.forDocument(doc);
        if (javaSource == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        try {
            javaSource.runUserActionTask(new CancellableTask<CompilationController>() {
                public void cancel() {
                }

                public void run(CompilationController compilationController) throws IOException {
                    // Move to resolved phase
                    compilationController.toPhase(Phase.ELEMENTS_RESOLVED);

                    // Get the caret
                    int caretAt = editorPane.getCaretPosition();

                    CompilationUnitTree compilationUnitTree = compilationController.getCompilationUnit();

                    // Find the TreePath for the caret position
                    TreePath treePath = compilationController.getTreeUtilities().pathFor(caretAt);
                    for (Tree tree : treePath) {
                        final Kind kind = tree.getKind();
                        // Found interesting tree
                        if (kindSet.contains(kind)) {
                            List<? extends Tree> trees = null;
                            switch(kind) {
                                case METHOD:
                                    MethodTree methodTree = (MethodTree) tree;
                                    trees = methodTree.getParameters();
                                    break;
                                case METHOD_INVOCATION:
                                    MethodInvocationTree methodInvocationTree = (MethodInvocationTree) tree;
                                    trees = methodInvocationTree.getArguments();
                                    break;
                                case NEW_CLASS:
                                    NewClassTree newClassTree = (NewClassTree) tree;
                                    trees = newClassTree.getArguments();
                                    break;
                                case NEW_ARRAY:
                                    NewArrayTree newArrayTree = (NewArrayTree) tree;
                                    trees = newArrayTree.getInitializers();
                                    break;
                            }

                            // minimum two items required for swapping
                            if (trees != null && trees.size() >= 2) {
                                SourcePositions sourcePositions = compilationController.getTrees().getSourcePositions();
                                final long firstStart = sourcePositions.getStartPosition(compilationUnitTree, trees.get(0));
                                final long lastEnd = sourcePositions.getEndPosition(compilationUnitTree, trees.get(trees.size() - 1));

                                // Is the caret in the range
                                if (firstStart <= caretAt && caretAt <= lastEnd) {
                                    int caretOffset = -1;
                                    List<Token> tokens = new LinkedList<SwapOperations.Token>();
                                    int currentTokenIndex = -1;
                                    Token currentToken = null;
                                    long previousEnd = -1;

                                    // Add items and intevening whitespaces to a list
                                    // Also remember the item that surrounds the caret
                                    for (Tree variableTree : trees) {
                                        long startPosition = sourcePositions.getStartPosition(compilationUnitTree, variableTree);
                                        long endPosition = sourcePositions.getEndPosition(compilationUnitTree, variableTree);
                                        if (previousEnd != -1) {
                                            try {
                                                tokens.add(new Token(doc.getText((int) (previousEnd),
                                                                                 (int) (startPosition - previousEnd))));
                                            } catch (BadLocationException ex) {
                                                Exceptions.printStackTrace(ex);
                                                return;
                                            }
                                        }
                                        final Token token = new Token(variableTree.toString());
                                        tokens.add(token);

                                        if (startPosition <= caretAt && caretAt <= endPosition) {
                                            currentTokenIndex = tokens.size() -1;
                                            currentToken = token;
                                            caretOffset = caretAt - (int) startPosition;
                                        }
                                        previousEnd = endPosition;
                                    }

                                    if (tokens.size() > 0 && currentTokenIndex != -1) {
                                        Token current = tokens.get(currentTokenIndex);
                                        switch (bias) {
                                            case BACKWARD:
                                                if (currentTokenIndex == 0) {
                                                    Toolkit.getDefaultToolkit().beep();
                                                    return;
                                                }
                                                Token previous = tokens.get(currentTokenIndex - 2);
                                                tokens.set(currentTokenIndex - 2, current);
                                                tokens.set(currentTokenIndex, previous);
                                                break;
                                            case FORWARD:
                                                if (currentTokenIndex == (tokens.size() - 1)) {
                                                    Toolkit.getDefaultToolkit().beep();
                                                    return;
                                                }
                                                Token next = tokens.get(currentTokenIndex + 2);
                                                tokens.set(currentTokenIndex + 2, current);
                                                tokens.set(currentTokenIndex, next);
                                                break;
                                        }

                                        // Build the insertion string
                                        final StringBuilder stringBuilder = new StringBuilder();
                                        int offset = 0;
                                        final int[] moveCaretOffset = new int[1];
                                        for (Token token : tokens) {
                                            final String text = token.getText();
                                            if (token == currentToken) {
                                                moveCaretOffset[0] = offset + caretOffset;
                                            }
                                            offset += text.length();
                                            stringBuilder.append(text);
                                        }

                                        // Now replace the old text with new text
                                        try {
                                            NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {
                                                public void run() {
                                                    try {
                                                        doc.remove((int) firstStart, (int) (lastEnd - firstStart));
                                                        doc.insertString((int) firstStart, stringBuilder.toString(), null);
                                                        editorPane.setCaretPosition((int) firstStart + moveCaretOffset[0]);
                                                    } catch (BadLocationException ex) {
                                                        Exceptions.printStackTrace(ex);
                                                    }
                                                }
                                            });
                                        } catch (BadLocationException ex) {
                                            Exceptions.printStackTrace(ex);
                                        }

                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }, true);
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }

    }
}
