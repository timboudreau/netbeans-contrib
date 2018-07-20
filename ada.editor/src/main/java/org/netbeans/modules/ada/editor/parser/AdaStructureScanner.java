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
package org.netbeans.modules.ada.editor.parser;

import org.netbeans.modules.ada.editor.ast.nodes.Program;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.text.Document;
import org.netbeans.modules.ada.editor.ast.ASTError;
import org.netbeans.modules.ada.editor.ast.ASTUtils;
import org.netbeans.modules.ada.editor.ast.nodes.Block;
import org.netbeans.modules.ada.editor.ast.nodes.Comment;
import org.netbeans.modules.ada.editor.ast.nodes.Expression;
import org.netbeans.modules.ada.editor.ast.nodes.FieldsDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.FormalParameter;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramBody;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.DefaultVisitor;
import org.netbeans.modules.ada.editor.parser.AdaElementHandle.SubprogramSpecificationHandle;
import org.netbeans.modules.ada.editor.parser.AdaElementHandle.MethodSubprogSpecHandle;
import org.netbeans.modules.ada.editor.parser.AdaElementHandle.MethodSubprogBodyHandle;
import org.netbeans.modules.ada.editor.parser.AdaElementHandle.PackageBodyHandle;
import org.netbeans.modules.ada.editor.parser.AdaElementHandle.PackageSpecificationHandle;
import org.netbeans.modules.ada.editor.parser.AdaElementHandle.SubprogramBodyHandle;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Source;
import org.openide.util.ImageUtilities;

/**
 * Based on org.netbeans.modules.php.editor.parser.PhpStructureScanner
 *
 * @author Andrea Lucarelli
 */
public class AdaStructureScanner implements StructureScanner {

    private static final Logger LOGGER = Logger.getLogger(AdaStructureScanner.class.getName());
    private static ImageIcon TYPE_ICON = null;
    private static ImageIcon TYPE_PRIVATE_ICON = null;
    private ParserResult info;
    private static final String FOLD_CODE_BLOCKS = "codeblocks"; //NOI18N
    private static final String FOLD_PACKAGE = "codeblocks"; //NOI18N
    private static final String FOLD_ADADOC = "comments"; //NOI18N
    private static final String FOLD_COMMENT = "initial-comment"; //NOI18N
    private static final String FONT_GRAY_COLOR = "<font color=\"#999999\">"; //NOI18N
    private static final String CLOSE_FONT = "</font>";                   //NOI18N
    private static final String LAST_CORRECT_FOLDING_PROPERTY = "LAST_CORRECT_FOLDING_PROPERY";

    public List<? extends StructureItem> scan(final ParserResult info) {
        this.info = info;
        Program program = ASTUtils.getRoot(info);
        final List<StructureItem> items = new ArrayList<StructureItem>();
        if (program != null) {
            program.accept(new StructureVisitor(items, program));
            return items;
        }
        return Collections.emptyList();
    }

    public Map<String, List<OffsetRange>> folds(ParserResult info) {
        Program program = ASTUtils.getRoot(info);
        final Map<String, List<OffsetRange>> folds = new HashMap<String, List<OffsetRange>>();
        if (program != null) {
            if (program.getStatements().size() == 1) {
                // check whether the ast is broken.
                if (program.getStatements().get(0) instanceof ASTError) {
                    @SuppressWarnings("unchecked")
                    Map<String, List<OffsetRange>> lastCorrect = (Map<String, List<OffsetRange>>) info.getSnapshot().getSource().getDocument(false).getProperty(LAST_CORRECT_FOLDING_PROPERTY);
                    if (lastCorrect != null) {
                        return lastCorrect;
                    } else {
                        return Collections.emptyMap();
                    }
                }
            }
            (new FoldVisitor(folds)).scan(program);
            List<Comment> comments = program.getComments();
            if (comments != null) {
                for (Comment comment : comments) {
                    // TODO: for ada doc and spark ???
                }
            }
            Source source = info.getSnapshot().getSource();
            assert source != null : "source was null";
            Document doc = source.getDocument(false);
            if (doc != null){
                doc.putProperty(LAST_CORRECT_FOLDING_PROPERTY, folds);
            }
            return folds;
        }
        return Collections.emptyMap();
    }

    private OffsetRange createOffsetRange(ASTNode node) {
        return new OffsetRange(node.getStartOffset(), node.getEndOffset());
    }

    private List<OffsetRange> getRanges(Map<String, List<OffsetRange>> folds, String kind) {
        List<OffsetRange> ranges = folds.get(kind);
        if (ranges == null) {
            ranges = new ArrayList<OffsetRange>();
            folds.put(kind, ranges);
        }
        return ranges;
    }

    public Configuration getConfiguration() {
        return null;
    }

    private class StructureVisitor extends DefaultVisitor {

        final List<StructureItem> items;
        private List<StructureItem> children = null;
        private Program program;
        private PackageSpecification pkgspc;
        private PackageBody pkgbdy;

        public StructureVisitor(List<StructureItem> items, Program program) {
            //LOGGER.setLevel(Level.FINE);
            this.items = items;
            this.program = program;
        }

        public StructureVisitor(List<StructureItem> items, PackageSpecification pkgspc) {
            //LOGGER.setLevel(Level.FINE);
            this.items = items;
            this.pkgspc = pkgspc;
        }

        public StructureVisitor(List<StructureItem> items, PackageBody pkgbdy) {
            //LOGGER.setLevel(Level.FINE);
            this.items = items;
            this.pkgbdy = pkgbdy;
        }

        @Override
        public void visit(SubprogramSpecification subprogSpec) {
            if (children == null && subprogSpec.getSubprogramName() != null) {
                AdaStructureItem item = new AdaSubprogSpecStructureItem(new AdaElementHandle.SubprogramSpecificationHandle(info, subprogSpec));
                items.add(item);
            }
        }

        @Override
        public void visit(SubprogramBody procedure) {
            if (children == null && procedure.getSubprogramSpecification().getSubprogramName() != null) {
                AdaStructureItem item = new AdaSubprogBodyStructureItem(new AdaElementHandle.SubprogramBodyHandle(info, procedure));
                items.add(item);
            }
        }

        @Override
        public void visit(PackageSpecification pkgspc) {
            if (pkgspc.getName() != null) {
                LOGGER.fine(pkgspc.getName().getName());
                if (children == null) {
                    children = new ArrayList<StructureItem>();
                    super.visit(pkgspc);
                    AdaStructureItem item = new AdaPackageSpecificationStructureItem(new AdaElementHandle.PackageSpecificationHandle(info, pkgspc), children); //NOI18N
                    items.add(item);
                } else {
                    final List<StructureItem> subitems = new ArrayList<StructureItem>();
                    pkgspc.accept(new StructureVisitor(subitems, pkgspc));
                    children.addAll(subitems);
                }
            }
        }

        @Override
        public void visit(PackageBody pkgbdy) {
            if (pkgbdy.getName() != null) {
                LOGGER.fine(pkgbdy.getName().getName());
                if (children == null) {
                    children = new ArrayList<StructureItem>();
                    super.visit(pkgbdy);
                    AdaStructureItem item = new AdaPackageBodyStructureItem(new AdaElementHandle.PackageBodyHandle(info, pkgbdy), children); //NOI18N
                    items.add(item);
                } else {
                    final List<StructureItem> subitems = new ArrayList<StructureItem>();
                    pkgbdy.accept(new StructureVisitor(subitems, pkgbdy));
                    children.addAll(subitems);
                }
            }
        }

        @Override
        public void visit(FieldsDeclaration fields) {
            Variable[] variables = fields.getVariableNames();
            if (variables != null) {
                for (Variable variable : variables) {
                    String name = ASTUtils.resolveVariableName(variable);
                    LOGGER.fine(name);
                    if (name != null) {
                        String text = name;
                        AdaStructureItem item = new AdaSimpleStructureItem(new AdaElementHandle.FieldsDeclarationHandle(info, fields), text, "0"); //NOI18N
                        children.add(item);
                    }
                }
            }
        }

        @Override
        public void visit(TypeDeclaration type) {
            Identifier id = type.getTypeName();
            if (id != null) {
                String name = id.getName();
                LOGGER.fine(name);
                if (name != null) {
                    String text = name;
                    AdaStructureItem item = new AdaTypeStructureItem(new AdaElementHandle.TypeDeclarationHandle(info, type), text, "0"); //NOI18N
                    children.add(item);
                }
            }
        }

        @Override
        public void visit(MethodDeclaration method) {
            if (children == null) {
                children = items;
            }
            LOGGER.fine(method.getMethodName());
            if (method.getSubprogramBody() != null) {
                SubprogramBody subprog = method.getSubprogramBody();
                if (subprog != null && subprog.getSubprogramSpecification().getSubprogramName() != null) {
                    AdaStructureItem item;
                    // className doesn't have to be defined if it's interace
                    item = new AdaMethodSubprogBodyStructureItem(new AdaElementHandle.MethodSubprogBodyHandle(info, method));
                    children.add(item);
                }
            } else {
                SubprogramSpecification subprog = method.getSubprogramSpecification();
                if (subprog != null && subprog.getSubprogramName() != null) {
                    AdaStructureItem item;
                    // className doesn't have to be defined if it's interace
                    item = new AdaMethodSubprogSpecStructureItem(new AdaElementHandle.MethodSubprogSpecHandle(info, method));
                    children.add(item);
                }
            }
        }
    }

    private abstract class AdaStructureItem implements StructureItem {

        final private AdaElementHandle elementHandle;
        final private List<? extends StructureItem> children;
        final private String sortPrefix;

        public AdaStructureItem(AdaElementHandle elementHandle, List<? extends StructureItem> children, String sortPrefix) {
            this.elementHandle = elementHandle;
            this.sortPrefix = sortPrefix;
            if (children != null) {
                this.children = children;
            } else {
                this.children = Collections.emptyList();
            }
        }

        @Override
        public boolean equals(Object obj) {
            boolean thesame = false;
            if (obj instanceof AdaStructureItem) {
                AdaStructureItem item = (AdaStructureItem) obj;
                if (item.getName() != null && this.getName() != null) {
                    thesame = item.elementHandle.getName().equals(elementHandle.getName()) && item.elementHandle.getASTNode().getStartOffset() == elementHandle.getASTNode().getStartOffset();
                }
            }
            return thesame;
        }

        @Override
        public int hashCode() {
            //int hashCode = super.hashCode();
            int hashCode = 11;
            if (getName() != null) {
                hashCode = 31 * getName().hashCode() + hashCode;
            }
            hashCode = (int) (31 * getPosition() + hashCode);
            return hashCode;
        }

        public String getName() {
            return elementHandle.getName();
        }

        public String getSortText() {
            return sortPrefix + elementHandle.getName();
        }

        public ElementHandle getElementHandle() {
            return elementHandle;
        }

        public ElementKind getKind() {
            return elementHandle.getKind();
        }

        public Set<Modifier> getModifiers() {
            return elementHandle.getModifiers();
        }

        public boolean isLeaf() {
            return (children.size() == 0);
        }

        public List<? extends StructureItem> getNestedItems() {
            return children;
        }

        public long getPosition() {
            return elementHandle.getASTNode().getStartOffset();
        }

        public long getEndPosition() {
            return elementHandle.getASTNode().getEndOffset();
        }

        public ImageIcon getCustomIcon() {
            return null;
        }

        protected void appendSubprogDescription(SubprogramSpecification subprog, HtmlFormatter formatter) {
            formatter.reset();
            if (subprog == null || subprog.getSubprogramName() == null) {
                return;
            }
            formatter.appendText(subprog.getSubprogramName().getName());
            formatter.appendText("(");   //NOI18N

            List<FormalParameter> parameters = subprog.getFormalParameters();
            if (parameters != null && parameters.size() > 0) {
                boolean first = true;
                for (FormalParameter formalParameter : parameters) {
                    String name = null;
                    Expression parameter = formalParameter.getParameterName();
                    if (parameter != null) {
                        Variable variable = null;
                        if (parameter instanceof Variable) {
                            variable = (Variable) parameter;
                        }

                        if (variable != null) {
                            name = ASTUtils.resolveVariableName(variable);
                        } else {
                            name = "??"; //NOI18N
                        }
                    }
                    String type = null;
                    if (formalParameter.getParameterType() != null) {
                        type = formalParameter.getParameterType().getTypeName().getName();
                    }
                    if (name != null) {
                        if (!first) {
                            formatter.appendText("; "); //NOI18N
                        }

                        formatter.appendText(name);

                        if (type != null) {
                            formatter.appendText(" : ");   //NOI18N
                            FormalParameter.Mode mode = formalParameter.getParameterMode();
                            formatter.appendHtml(FONT_GRAY_COLOR);
                            if (mode == FormalParameter.Mode.IN) {
                                formatter.appendText("in ");   //NOI18N
                            } else if (mode == FormalParameter.Mode.OUT) {
                                formatter.appendText("out ");   //NOI18N
                            } else if (mode == FormalParameter.Mode.IN_OUT) {
                                formatter.appendText("in out ");   //NOI18N
                            }
                            formatter.appendText(type);
                            formatter.appendHtml(CLOSE_FONT);
                        }
                        first = false;
                    }
                }
            }
            formatter.appendText(")");   //NOI18N

            if (subprog.getSubtypeReturn() != null) {
                String type = subprog.getSubtypeReturn().getName();
                formatter.appendText(" : out ");   //NOI18N
                formatter.appendHtml(FONT_GRAY_COLOR);
                formatter.appendText(type);
                formatter.appendHtml(CLOSE_FONT);
            }

        }

        protected void appendSubprogDescription(SubprogramBody subprog, HtmlFormatter formatter) {
            formatter.reset();
            if (subprog == null || subprog.getSubprogramSpecification().getSubprogramName() == null) {
                return;
            }
            formatter.appendText(subprog.getSubprogramSpecification().getSubprogramName().getName());
            formatter.appendText("(");   //NOI18N

            List<FormalParameter> parameters = subprog.getSubprogramSpecification().getFormalParameters();
            if (parameters != null && parameters.size() > 0) {
                boolean first = true;
                for (FormalParameter formalParameter : parameters) {
                    String name = null;
                    Expression parameter = formalParameter.getParameterName();
                    if (parameter != null) {
                        Variable variable = null;
                        if (parameter instanceof Variable) {
                            variable = (Variable) parameter;
                        }

                        if (variable != null) {
                            name = ASTUtils.resolveVariableName(variable);
                        } else {
                            name = "??"; //NOI18N
                        }
                    }
                    String type = null;
                    if (formalParameter.getParameterType() != null) {
                        type = formalParameter.getParameterType().getTypeName().getName();
                    }
                    if (name != null) {
                        if (!first) {
                            formatter.appendText("; "); //NOI18N
                        }

                        formatter.appendText(name);

                        if (type != null) {
                            formatter.appendText(" : ");   //NOI18N
                            FormalParameter.Mode mode = formalParameter.getParameterMode();
                            formatter.appendHtml(FONT_GRAY_COLOR);
                            if (mode == FormalParameter.Mode.IN) {
                                formatter.appendText("in ");   //NOI18N
                            } else if (mode == FormalParameter.Mode.OUT) {
                                formatter.appendText("out ");   //NOI18N
                            } else if (mode == FormalParameter.Mode.IN_OUT) {
                                formatter.appendText("in out ");   //NOI18N
                            }
                            formatter.appendText(type);
                            formatter.appendHtml(CLOSE_FONT);
                        }
                        first = false;
                    }
                }
            }
            formatter.appendText(")");   //NOI18N

            if (subprog.getSubprogramSpecification().getSubtypeReturn() != null) {
                String type = subprog.getSubprogramSpecification().getSubtypeReturn().getName();
                formatter.appendText(" : out ");   //NOI18N
                formatter.appendHtml(FONT_GRAY_COLOR);
                formatter.appendText(type);
                formatter.appendHtml(CLOSE_FONT);
            }
        }
    }

    private class AdaTypeStructureItem extends AdaStructureItem {

        private static final String ADA_TYPE_ICON = "org/netbeans/modules/ada/editor/resources/icons/type_16.png"; //NOI18N
        private static final String ADA_TYPE_PRIVATE_ICON = "org/netbeans/modules/ada/editor/resources/icons/type_private_16.png"; //NOI18N
        private String simpleText;

        public AdaTypeStructureItem(AdaElementHandle elementHandle, String simpleText, String prefix) {
            super(elementHandle, null, prefix);
            this.simpleText = simpleText;
        }

        @Override
        public ImageIcon getCustomIcon() {
            Set<Modifier> modifiers = this.getModifiers();

            if (TYPE_ICON == null) {
                TYPE_ICON = new ImageIcon(ImageUtilities.loadImage(ADA_TYPE_ICON));
            }

            ImageIcon icon = TYPE_ICON;

            for (Modifier modifier : modifiers) {
                if (modifier == Modifier.PRIVATE) {
                    if (TYPE_PRIVATE_ICON == null) {
                        TYPE_PRIVATE_ICON = new ImageIcon(ImageUtilities.loadImage(ADA_TYPE_PRIVATE_ICON));
                    }
                    icon = TYPE_PRIVATE_ICON;
                }
            }
            return icon;
        }

        public String getHtml(HtmlFormatter formatter) {
            formatter.appendText(simpleText);
            return formatter.getText();
        }
    }

    private class AdaSimpleStructureItem extends AdaStructureItem {

        private String simpleText;

        public AdaSimpleStructureItem(AdaElementHandle elementHandle, String simpleText, String prefix) {
            super(elementHandle, null, prefix);
            this.simpleText = simpleText;
        }

        public String getHtml(HtmlFormatter formatter) {
            formatter.appendText(simpleText);
            return formatter.getText();
        }
    }

    private class AdaSubprogSpecStructureItem extends AdaStructureItem {

        public AdaSubprogSpecStructureItem(AdaElementHandle elementHandle) {
            super(elementHandle, null, "subspc"); //NOI18N
        }

        public String getHtml(HtmlFormatter formatter) {
            formatter.reset();
            SubprogramSpecificationHandle handle = (SubprogramSpecificationHandle) getElementHandle();
            SubprogramSpecification subprog = (SubprogramSpecification) handle.getASTNode();
            appendSubprogDescription(subprog, formatter);
            return formatter.getText();
        }
    }

    private class AdaSubprogBodyStructureItem extends AdaStructureItem {

        public AdaSubprogBodyStructureItem(AdaElementHandle elementHandle) {
            super(elementHandle, null, "subbdy"); //NOI18N
        }

        public String getHtml(HtmlFormatter formatter) {
            formatter.reset();
            SubprogramBodyHandle handle = (SubprogramBodyHandle) getElementHandle();
            SubprogramBody subprog = (SubprogramBody) handle.getASTNode();
            appendSubprogDescription(subprog, formatter);
            return formatter.getText();
        }
    }

    private class AdaPackageSpecificationStructureItem extends AdaStructureItem {

        public AdaPackageSpecificationStructureItem(AdaElementHandle elementHandle, List<? extends StructureItem> children) {
            super(elementHandle, children, "pkgspc"); //NOI18N
        }

        public String getHtml(HtmlFormatter formatter) {
            formatter.reset();
            PackageSpecificationHandle handle = (PackageSpecificationHandle) getElementHandle();
            formatter.appendText(handle.getName());
            formatter.appendHtml(FONT_GRAY_COLOR + " (specification)" + CLOSE_FONT);
            return formatter.getText();
        }
    }

    private class AdaPackageBodyStructureItem extends AdaStructureItem {

        public AdaPackageBodyStructureItem(AdaElementHandle elementHandle, List<? extends StructureItem> children) {
            super(elementHandle, children, "pkgbdy"); //NOI18N
        }

        public String getHtml(HtmlFormatter formatter) {
            formatter.reset();
            PackageBodyHandle handle = (PackageBodyHandle) getElementHandle();
            formatter.appendText(handle.getName());
            formatter.appendHtml(FONT_GRAY_COLOR + " (body)" + CLOSE_FONT);
            return formatter.getText();
        }
    }

    private class AdaMethodSubprogSpecStructureItem extends AdaStructureItem {

        public AdaMethodSubprogSpecStructureItem(AdaElementHandle elementHandle) {
            super(elementHandle, null, "subspc"); //NOI18N
        }

        public String getHtml(HtmlFormatter formatter) {
            formatter.reset();
            MethodSubprogSpecHandle handle = (MethodSubprogSpecHandle) getElementHandle();
            MethodDeclaration method = (MethodDeclaration) handle.getASTNode();
            appendSubprogDescription(method.getSubprogramSpecification(), formatter);
            return formatter.getText();
        }
    }

    private class AdaMethodSubprogBodyStructureItem extends AdaStructureItem {

        public AdaMethodSubprogBodyStructureItem(AdaElementHandle elementHandle) {
            super(elementHandle, null, "subbdy"); //NOI18N
        }

        public String getHtml(HtmlFormatter formatter) {
            formatter.reset();
            MethodSubprogBodyHandle handle = (MethodSubprogBodyHandle) getElementHandle();
            MethodDeclaration method = (MethodDeclaration) handle.getASTNode();
            appendSubprogDescription(method.getSubprogramBody(), formatter);
            return formatter.getText();
        }
    }

    private class FoldVisitor extends DefaultVisitor {

        final Map<String, List<OffsetRange>> folds;
        private String foldType;

        public FoldVisitor(Map<String, List<OffsetRange>> folds) {
            this.folds = folds;
            this.foldType = null;

        }

        @Override
        public void visit(PackageSpecification pkgspc) {
            this.foldType = FOLD_PACKAGE;
            if (pkgspc.getBody() != null) {
                scan(pkgspc.getBody());
            }
        }

        @Override
        public void visit(PackageBody pkgbdy) {
            this.foldType = FOLD_PACKAGE;
            if (pkgbdy.getBody() != null) {
                scan(pkgbdy.getBody());
            }
        }

        @Override
        public void visit(Block block) {
            if (foldType != null) {
                getRanges(folds, foldType).add(createOffsetRange(block));
                foldType = null;
            }
            if (block.getStatements() != null) {
                scan(block.getStatements());
            }
        }

        @Override
        public void visit(SubprogramBody subprog) {
            foldType = FOLD_CODE_BLOCKS;
            if (subprog.getDeclarations() != null) {
                scan(subprog.getDeclarations());
            }
            foldType = FOLD_CODE_BLOCKS;
            if (subprog.getBody() != null) {
                scan(subprog.getBody());
            }
        }
    }
}
