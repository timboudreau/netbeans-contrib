/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javafx.editor.semantic;

import com.sun.javafx.api.tree.ClassDeclarationTree;
import com.sun.javafx.api.tree.FunctionDefinitionTree;
import com.sun.javafx.api.tree.JavaFXTreePathScanner;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.CancellableTask;
import org.netbeans.api.javafx.source.CompilationInfo;
import org.netbeans.api.javafx.source.TreeUtilities;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author Anton Chechel
 */
public class SemanticHighlighter implements CancellableTask<CompilationInfo> {

    private static final String ID_METHOD = "method";
    private static final String ID_METHOD_INVOCATION = "methodInvocation";
    private static final String ID_FIELD = "field";
    private static final String ID_IDENTIFIER = "identifier";
    private static final String ID_CLASS = "class";
    
    private static final AttributeSet FIELD_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, new Color(0, 153, 0));
    private static final AttributeSet FIELD_STATIC_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, new Color(0, 153, 0), StyleConstants.Italic, Boolean.TRUE);
    private static final AttributeSet METHOD_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.BLACK, StyleConstants.Bold, Boolean.TRUE);
    private static final AttributeSet METHOD_STATIC_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.BLACK, StyleConstants.Bold, Boolean.TRUE, StyleConstants.Italic, Boolean.TRUE);
    private static final AttributeSet METHOD_INVOCATION_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.BLACK);
    private static final AttributeSet METHOD_STATIC_INVOCATION_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.BLACK, StyleConstants.Italic, Boolean.TRUE);
    private static final AttributeSet IDENTIFIER_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Background, new Color(255, 127, 127));
    private static final AttributeSet CLASS_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.BLACK, StyleConstants.Bold, Boolean.TRUE);
    private static final AttributeSet CLASS_STATIC_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.BLACK, StyleConstants.Bold, Boolean.TRUE, StyleConstants.Italic, Boolean.TRUE);
    
    private static final Logger LOGGER = Logger.getLogger(SemanticHighlighter.class.getName());
    private static final boolean LOGGABLE = LOGGER.isLoggable(Level.FINE);
    
    private FileObject file;
    private AtomicBoolean cancel = new AtomicBoolean();
    private List<Result> identifiers = new ArrayList<Result>();

    SemanticHighlighter(FileObject file) {
        this.file = file;
    }

    public void cancel() {
        cancel.set(true);
    }

    public void run(CompilationInfo info) {
        cancel.set(false);
        process(info);
    }

    private void process(CompilationInfo info) {
        try {
            DataObject od = DataObject.find(file);
            EditorCookie ec = od.getLookup().lookup(EditorCookie.class);
            if (ec == null) {
                return;
            }
            Document doc = ec.getDocument();
            if (doc == null) {
                return;
            }

            List<Result> result = new ArrayList<Result>();
            CompilationUnitTree compilationUnit = info.getCompilationUnit();
            JavaFXThreeVisitor javaFXThreeVisitor = new JavaFXThreeVisitor(info);
            javaFXThreeVisitor.scan(compilationUnit, result);
            setHighlights(doc, result, identifiers);
            identifiers.clear(); // clear cache

        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    static void setHighlights(Document doc, List<Result> results, List<Result> identifiers) {
        OffsetsBag bag = new OffsetsBag(doc, true);
        for (Result result : results) {
            int start = (int) result.start;
            int end = (int) result.end;

            if (start >= 0 && end >= 0) {
                bag.addHighlight(start, end, getAttributeSet(result));
            } else {
                log("* Incorrect positions for highlighting: " + start + ", " + end);
            }

            // highlighting fot variables from cache
            if (ID_FIELD.equals(result.identifier)) {
                for (Result id : identifiers) {
                    final String idText = id.token.text().toString();
                    final String resText = result.token.text().toString();
                    if (idText.equals(resText)) {
                        bag.addHighlight((int) id.start, (int) id.end, getAttributeSet(result));
                    }
                }
            }

        }

        getBag(doc).setHighlights(bag);
    }

    private static AttributeSet getAttributeSet(Result res) {
        if (ID_METHOD.equals(res.identifier)) {
            return res.isStatic ? METHOD_STATIC_HIGHLIGHT : METHOD_HIGHLIGHT;
        } else if (ID_METHOD_INVOCATION.equals(res.identifier)) {
            return res.isStatic ? METHOD_STATIC_INVOCATION_HIGHLIGHT : METHOD_INVOCATION_HIGHLIGHT;
        } else if (ID_FIELD.equals(res.identifier)) {
            return res.isStatic ? FIELD_STATIC_HIGHLIGHT : FIELD_HIGHLIGHT;
        } else if (ID_IDENTIFIER.equals(res.identifier)) {
            return IDENTIFIER_HIGHLIGHT;
        } else if (ID_CLASS.equals(res.identifier)) {
            return res.isStatic ? CLASS_STATIC_HIGHLIGHT : CLASS_HIGHLIGHT;
        }
        return FIELD_HIGHLIGHT;
    }

    static OffsetsBag getBag(Document doc) {
        OffsetsBag bag = (OffsetsBag) doc.getProperty(SemanticHighlighter.class);

        if (bag == null) {
            doc.putProperty(SemanticHighlighter.class, bag = new OffsetsBag(doc));
        }

        return bag;
    }

    private static void log(String s) {
        if (LOGGABLE) {
            LOGGER.fine(s);
        }
    }

    private class JavaFXThreeVisitor extends JavaFXTreePathScanner<Void, List<Result>> {

        private CompilationInfo info;
        private TreeUtilities tu;

        public JavaFXThreeVisitor(CompilationInfo info) {
            this.info = info;
            tu = new TreeUtilities(info);
        }

        @Override
        public Void visitFunctionDefinition(FunctionDefinitionTree tree, List<Result> list) {
//            String name = ((JFXFunctionDefinition) tree).getName().toString();

            SourcePositions sourcePositions = info.getTrees().getSourcePositions();
            long start = sourcePositions.getStartPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
            long end = sourcePositions.getEndPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());

            if (start < 0 || end < 0) { // synthetic
                return super.visitFunctionDefinition(tree, list);
            }

            Element element = info.getTrees().getElement(getCurrentPath());
            Set<Modifier> modifiers = element != null ? element.getModifiers() : null;

            TokenSequence<JFXTokenId> ts = tu.tokensFor(tree);
            while (ts.moveNext()) {
                Token t = ts.token();
                if (JFXTokenId.IDENTIFIER.equals(t.id())) { // first identifier is a name
                    start = ts.offset();
                    end = start + t.length();
                    boolean isStatic = modifiers != null && modifiers.contains(Modifier.STATIC);
                    list.add(new Result(start, end, ID_METHOD, t, isStatic));
                    break;
                }
            }

            return super.visitFunctionDefinition(tree, list);
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree tree, List<Result> list) {
            SourcePositions sourcePositions = info.getTrees().getSourcePositions();
            long start = sourcePositions.getStartPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
            long end = sourcePositions.getEndPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());

            if (start < 0 || end < 0) { // synthetic
                return super.visitMethodInvocation(tree, list);
            }

            Element element = info.getTrees().getElement(getCurrentPath());
            Set<Modifier> modifiers = element != null ? element.getModifiers() : null;

            TokenSequence<JFXTokenId> ts = tu.tokensFor(tree);
            Token name = null;
            
            ts.moveEnd();
            boolean metLBrace = false;
            while (ts.movePrevious()) {
                Token t = ts.token();
                if (!metLBrace) {
                    metLBrace = JFXTokenId.LPAREN.equals(t.id());
                    if (!metLBrace) {
                        continue;
                    }
                }
                if (JFXTokenId.IDENTIFIER.equals(t.id())) {
                    start = ts.offset();
                    name = t; // last identifier followed left parenthis is a name
                    break;
                }
            }

            if (name != null) {
                end = start + name.length();
                boolean isStatic = modifiers != null && modifiers.contains(Modifier.STATIC);
                list.add(new Result(start, end, ID_METHOD_INVOCATION, name, isStatic));
            }

            return super.visitMethodInvocation(tree, list);
        }

        @Override
        public Void visitVariable(VariableTree tree, List<Result> list) {
            SourcePositions sourcePositions = info.getTrees().getSourcePositions();
            long start = sourcePositions.getStartPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
            long end = sourcePositions.getEndPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());

            if (start < 0 || end < 0) { // synthetic
                return super.visitVariable(tree, list);
            }

            Element element = info.getTrees().getElement(getCurrentPath());
            Set<Modifier> modifiers = element != null ? element.getModifiers() : null;

            TokenSequence<JFXTokenId> ts = tu.tokensFor(tree);
            while (ts.moveNext()) {
                Token t = ts.token();
                if (JFXTokenId.IDENTIFIER.equals(t.id())) { // first identifier is a name
                    start = ts.offset();
                    end = start + t.length();
                    boolean isStatic = modifiers != null && modifiers.contains(Modifier.STATIC);
                    list.add(new Result(start, end, ID_FIELD, t, isStatic));
                    break;
                }
            }

            return super.visitVariable(tree, list);
        }

        @Override
        public Void visitIdentifier(IdentifierTree tree, List<Result> list) {
            SourcePositions sourcePositions = info.getTrees().getSourcePositions();
            long start = sourcePositions.getStartPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
            long end = sourcePositions.getEndPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());

            if (start < 0 || end < 0) { // synthetic
                return super.visitIdentifier(tree, list);
            }

            Element element = info.getTrees().getElement(getCurrentPath());
            Set<Modifier> modifiers = element != null ? element.getModifiers() : null;
            
//            if (element != null && element.getKind().isField()) {
//                TokenSequence<JFXTokenId> ts = tu.tokensFor(tree);
//                if (ts.moveNext()) {
//                    Token t = ts.token();
//                    start = ts.offset();
//                    end = start + t.length();
//                    boolean isStatic = modifiers != null && modifiers.contains(Modifier.STATIC);
////                    identifiers.add(new Result(start, end, ID_IDENTIFIER, t, isStatic)); // identfiers chache
//                    list.add(new Result(start, end, ID_IDENTIFIER, t)); // debug only
//                }
//            }

            TokenSequence<JFXTokenId> ts = tu.tokensFor(tree);
            while (ts.moveNext()) {
                Token t = ts.token();
                if (JFXTokenId.IDENTIFIER.equals(t.id())) {
                    start = ts.offset();
                    end = start + t.length();
                    boolean isStatic = modifiers != null && modifiers.contains(Modifier.STATIC);
                    identifiers.add(new Result(start, end, ID_IDENTIFIER, t, isStatic)); // identfiers chache
//                    list.add(new Result(start, end, ID_IDENTIFIER, t)); // debug only
                    break;
                }
            }

            return super.visitIdentifier(tree, list);
        }

        @Override
        public Void visitClassDeclaration(ClassDeclarationTree tree, List<Result> list) {
            SourcePositions sourcePositions = info.getTrees().getSourcePositions();
            long start = sourcePositions.getStartPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
            long end = sourcePositions.getEndPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());

            if (start < 0 || end < 0) { // synthetic
                return super.visitClassDeclaration(tree, list);
            }

            Element element = info.getTrees().getElement(getCurrentPath());
            Set<Modifier> modifiers = element != null ? element.getModifiers() : null;
            
            TokenSequence<JFXTokenId> ts = tu.tokensFor(tree);
            while (ts.moveNext()) {
                Token t = ts.token();
                if (JFXTokenId.IDENTIFIER.equals(t.id())) { // first identifier is a name
                    start = ts.offset();
                    end = start + t.length();
                    boolean isStatic = modifiers != null && modifiers.contains(Modifier.STATIC);
                    list.add(new Result(start, end, ID_CLASS, t, isStatic));
                    break;
                }
            }

            return super.visitClassDeclaration(tree, list);
        }

        @Override
        public Void visitMemberSelect(MemberSelectTree tree, List<Result> list) {
            SourcePositions sourcePositions = info.getTrees().getSourcePositions();
            long start = sourcePositions.getStartPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
            long end = sourcePositions.getEndPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());

            if (start < 0 || end < 0) { // synthetic
                return super.visitMemberSelect(tree, list);
            }

            Element element = info.getTrees().getElement(getCurrentPath());
            if (element != null) {
                TokenSequence<JFXTokenId> ts = tu.tokensFor(tree);

                while (ts.moveNext()) {
                    Token t = ts.token();
                    String tokenStr = t.text().toString();
                    
                    if (JFXTokenId.IDENTIFIER.equals(t.id())) {
                        start = ts.offset();
                        TreePath subPath = tu.pathFor((int) start);
                        Element subElement = info.getTrees().getElement(subPath);
                        if (subElement != null) {
                            String subElementName = subElement.getSimpleName().toString();

                            if (tokenStr.equals(subElementName)) {
                                Set<Modifier> modifiers = element != null ? element.getModifiers() : null;
                                start = ts.offset();
                                end = start + t.length();
                                boolean isStatic = modifiers != null && modifiers.contains(Modifier.STATIC);

                                if (subElement.getKind().isField()) {
                                    list.add(new Result(start, end, ID_FIELD, t, isStatic));
                                } else if (ElementKind.METHOD.equals(subElement.getKind())) {
                                    list.add(new Result(start, end, ID_METHOD_INVOCATION, t, isStatic));
                                }
                            }
                        }
                    }

                }
            }

            return super.visitMemberSelect(tree, list);
        }

    }

    private static class Result {

        long start;
        long end;
        String identifier; // temporary since element doesn't work

        Token token;
        boolean isStatic;

        public Result(long start, long end, String identifier, Token token) {
            this(start, end, identifier, token, false);
        }

        public Result(long start, long end, String identifier, Token token, boolean isStatic) {
            this.start = start;
            this.end = end;
            this.identifier = identifier;
            this.token = token;
            this.isStatic = isStatic;
        }

        @Override
        public String toString() {
            return "[" + start + ", " + end + ", " + identifier + "]";
        }
    }
}
