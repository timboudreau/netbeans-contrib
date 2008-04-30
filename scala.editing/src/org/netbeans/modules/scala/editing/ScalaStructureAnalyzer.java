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
package org.netbeans.modules.scala.editing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.gsf.api.Modifier;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.StructureItem;
import org.netbeans.modules.gsf.api.StructureScanner;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.nodes.AstDef;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.openide.util.Exceptions;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaStructureAnalyzer implements StructureScanner {

    public static final String NETBEANS_IMPORT_FILE = "__netbeans_import__"; // NOI18N

    private static final String DOT_CALL = ".call"; // NOI18N


    public List<? extends StructureItem> scan(CompilationInfo info, HtmlFormatter formatter) {
        ScalaParserResult result = AstUtilities.getParserResult(info);
        if (result == null) {
            return Collections.emptyList();
        }

        AstScope rootScope = result.getRootScope();
        if (rootScope == null) {
            return Collections.emptyList();
        }

        List<StructureItem> items = new ArrayList<StructureItem>();

        for (AstDef def : rootScope.getDefs()) {
            if (def.getKind() != ElementKind.PARAMETER && def.getKind() != ElementKind.VARIABLE && def.getKind() != ElementKind.OTHER) {
                items.add(new ScalaStructureItem(def, info, formatter));
            }
        }

        return items;
    }

    public Map<String, List<OffsetRange>> folds(CompilationInfo info) {
        ScalaParserResult result = AstUtilities.getParserResult(info);
        if (result == null) {
            Collections.emptyList();
        }

        AstScope rootScope = result.getRootScope();
        if (rootScope == null) {
            return Collections.emptyMap();
        }

        Map<String, List<OffsetRange>> folds = new HashMap<String, List<OffsetRange>>();
        List<OffsetRange> codeblocks = new ArrayList<OffsetRange>();
        folds.put("codeblocks", codeblocks); // NOI18N

//        try {
//            BaseDocument doc = (BaseDocument)info.getDocument();
//
//            for (AstElement element : elements) {
//                ElementKind kind = element.getKind();
//                switch (kind) {
//                case METHOD:
//                case CONSTRUCTOR:
//                case CLASS:
//                case MODULE:
//                    Node node = element.getNode();
//                    OffsetRange range = AstUtilities.getRange(node);
//                    
//                    if(source != null) {
//                        int lexStart = source.getLexicalOffset(range.getStart());
//                        int lexEnd = source.getLexicalOffset(range.getEnd());
//                        if (lexStart < lexEnd) {
//                            //recalculate the range if we parsed the virtual source
//                            range = new OffsetRange(lexStart,lexEnd);
//                        }
//                    }
//
//                    if (kind == ElementKind.METHOD || kind == ElementKind.CONSTRUCTOR ||
//                        // Only make nested classes/modules foldable, similar to what the java editor is doing
//                        (range.getStart() > Utilities.getRowStart(doc, range.getStart()))) {
//
//                        int start = range.getStart();
//                        // Start the fold at the END of the line
//                        start = org.netbeans.editor.Utilities.getRowEnd(doc, start);
//                        int end = range.getEnd();
//                        if (start != (-1) && end != (-1) && start < end && end <= doc.getLength()) {
//                            range = new OffsetRange(start, end);
//                            codeblocks.add(range);
//                        }
//                    }
//                    break;
//                }
//
//                assert element.getChildren().size() == 0;
//            }
//        } catch (Exception ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        
        return folds;
    }

    private class ScalaStructureItem implements StructureItem {

        private AstDef def;
        private CompilationInfo info;
        private Document doc;
        private HtmlFormatter formatter;

        private ScalaStructureItem(AstDef def, CompilationInfo info, HtmlFormatter formatter) {
            this.def = def;
            this.info = info;

            try {
                this.doc = info.getDocument();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            if (doc == null) {
                ScalaLexUtilities.getDocument(info.getFileObject(), true);
            }

            this.formatter = formatter;
        }

        public String getName() {
            return def.getName();
        }

        public String getSortText() {
            return getName();
        }

        public String getHtml() {
            formatter.reset();
            def.htmlFormat(formatter);
            return formatter.getText();
        }

        public ElementHandle getElementHandle() {
            return def;
        }

        public ElementKind getKind() {
            return def.getKind();
        }

        public Set<Modifier> getModifiers() {
            return def.getModifiers();
        }

        public boolean isLeaf() {
            switch (def.getKind()) {
                case ATTRIBUTE:
                case CONSTANT:
                case CONSTRUCTOR:
                case METHOD:
                case FIELD:
                case KEYWORD:
                case VARIABLE:
                case OTHER:
                case GLOBAL:
                case PROPERTY:
                case PARAMETER:
                    return true;

                case FILE:
                case PACKAGE:
                case MODULE:
                case CLASS:
                    return false;

                default:
                    throw new RuntimeException("Unhandled kind: " + def.getKind());
            }
        }

        public List<? extends StructureItem> getNestedItems() {
            List<AstDef> nested = def.getBindingScope().getDefs();

            if ((nested != null) && (nested.size() > 0)) {
                List<ScalaStructureItem> children = new ArrayList<ScalaStructureItem>(nested.size());

                for (AstDef child : nested) {
                    if (child.getKind() != ElementKind.PARAMETER && child.getKind() != ElementKind.VARIABLE && child.getKind() != ElementKind.OTHER) {
                        children.add(new ScalaStructureItem(child, info, formatter));
                    }
                }

                return children;
            } else {
                return Collections.emptyList();
            }
        }

        public long getPosition() {
            /** @Todo: TokenHierarchy.get(doc) may throw NPE, don't why, need further dig */
            try {
                TokenHierarchy th = TokenHierarchy.get(doc);
                return def.getOffset(th);
            } catch (Exception ex) {
                return 0;
            }
        }

        public long getEndPosition() {
            /** @Todo: TokenHierarchy.get(doc) may throw NPE, don't why, need further dig */
            try {
                TokenHierarchy th = TokenHierarchy.get(doc);
                return def.getEndOffset(th);
            } catch (Exception ex) {
                return 0;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (!(o instanceof ScalaStructureItem)) {
                return false;
            }

            ScalaStructureItem d = (ScalaStructureItem) o;

            if (def.getKind() != d.def.getKind()) {
                return false;
            }

            if (!getName().equals(d.getName())) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;

            hash = (29 * hash) + ((this.getName() != null) ? this.getName().hashCode() : 0);
            hash = (29 * hash) + ((this.def.getKind() != null) ? this.def.getKind().hashCode() : 0);

            return hash;
        }

        @Override
        public String toString() {
            return getName();
        }

        public ImageIcon getCustomIcon() {
            return null;
        }
    }
}
