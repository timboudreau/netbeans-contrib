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
import javax.swing.ImageIcon;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.gsf.api.Modifier;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.StructureItem;
import org.netbeans.modules.gsf.api.StructureScanner;
import org.netbeans.modules.ada.editor.ast.ASTUtils;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.DefaultVisitor;

/**
 * Based on org.netbeans.modules.php.editor.parser.PhpStructureScanner
 *
 * @author Andrea Lucarelli
 */
public class AdaStructureScanner implements StructureScanner {

    private CompilationInfo info;

    public List<? extends StructureItem> scan(final CompilationInfo info) {

        System.out.println("AdaStructureScanner.scan");

        this.info = info;
        Program program = ASTUtils.getRoot(info);
        final List<StructureItem> items = new ArrayList<StructureItem>();
        if (program != null) {
            program.accept(new StructureVisitor(items));
            return items;
        }
        return Collections.emptyList();
    }

    public Map<String, List<OffsetRange>> folds(CompilationInfo info) {
        Program program = ASTUtils.getRoot(info);
        final Map<String, List<OffsetRange>> folds = new HashMap<String, List<OffsetRange>>();
        if (program != null) {
            program.accept(new FoldVisitor(folds));
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
        private String className;

        public StructureVisitor(List<StructureItem> items) {
            this.items = items;
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

        protected void appendInterfeas(List<Identifier> interfaes, HtmlFormatter formatter) {
            boolean first = true;
            for (Identifier identifier : interfaes) {
                if (identifier != null) {
                    if (!first) {
                        formatter.appendText(", ");  //NOI18N

                    } else {
                        first = false;
                    }
                    formatter.appendText(identifier.getName());
                }

            }
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

    private class FoldVisitor extends DefaultVisitor {

        final Map<String, List<OffsetRange>> folds;
        private String foldType;

        public FoldVisitor(Map<String, List<OffsetRange>> folds) {
            this.folds = folds;
            foldType = null;

        }
    }
}
