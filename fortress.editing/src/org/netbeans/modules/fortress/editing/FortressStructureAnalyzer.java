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
package org.netbeans.modules.fortress.editing;

import com.sun.fortress.nodes.FnDef;
import com.sun.fortress.nodes.IdType;
import com.sun.fortress.nodes.Node;
import com.sun.fortress.nodes.Param;
import com.sun.fortress.nodes.TupleType;
import com.sun.fortress.nodes.Type;
import com.sun.fortress.nodes.VoidType;
import edu.rice.cs.plt.tuple.Option;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import org.netbeans.modules.fortress.editing.visitors.Scope;
import org.netbeans.modules.fortress.editing.visitors.Signature;

/**
 *
 * @author Caoyuan Deng
 */
public class FortressStructureAnalyzer implements StructureScanner {

    public static final String NETBEANS_IMPORT_FILE = "__netbeans_import__"; // NOI18N

    private static final String DOT_CALL = ".call"; // NOI18N


    public List<? extends StructureItem> scan(CompilationInfo info, HtmlFormatter formatter) {
        FortressParserResult result = AstUtilities.getParserResult(info);
        if (result == null) {
            Collections.emptyList();
        }

        Node root = result.getRootNode();
        if (root == null) {
            Collections.emptyList();
        }

        Scope rootScope = result.getRootScope();
        List<StructureItem> itemList = new ArrayList<StructureItem>();

        for (Signature signature : rootScope.getDefinitions()) {
            itemList.add(new FortressStructureItem(signature, info, formatter));
        }

        return itemList;
    }

    public Map<String, List<OffsetRange>> folds(CompilationInfo info) {
        FortressParserResult result = AstUtilities.getParserResult(info);
        if (result == null) {
            Collections.emptyList();
        }

        Node root = result.getRootNode();
        if (root == null) {
            Collections.emptyMap();
        }

        Scope rootScope = result.getRootScope();
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

    private class FortressStructureItem implements StructureItem {

        private Signature signature;
        private CompilationInfo info;
        private HtmlFormatter formatter;

        private FortressStructureItem(Signature signature, CompilationInfo info, HtmlFormatter formatter) {
            this.signature = signature;
            this.info = info;
            this.formatter = formatter;
        }

        public String getName() {
            return signature.getName();
        }

        public String getSortText() {
            return getName();
        }

        public String getHtml() {
            formatter.reset();
//            boolean strike = signature.getModifiers().contains(Modifier.DEPRECATED);
//            if (strike) {
//                formatter.deprecated(true);
//            }

            formatter.appendText(getName());

//            if (strike) {
//                formatter.deprecated(false);
//            }

            if (signature.getNode() instanceof FnDef) {
                // Append parameters
                FnDef fnDef = (FnDef) signature.getNode();

                Collection<Param> params = fnDef.getParams();

                formatter.appendHtml("(");
                if ((params != null) && (params.size() > 0)) {
                    formatter.parameters(true);

                    for (Iterator<Param> itr = params.iterator(); itr.hasNext();) {
                        String nameStr = itr.next().getName().stringName();
                        // TODO - if I know types, list the type here instead. For now, just use the parameter name instead
                        formatter.appendText(nameStr);

                        if (itr.hasNext()) {
                            formatter.appendHtml(", ");
                        }
                    }

                    formatter.parameters(false);
                }
                formatter.appendHtml(")");

                Option<Type> retType = fnDef.getReturnType();
                if (retType.isNone()) {
                    formatter.appendHtml(" : ");
                    formatter.appendText("()");
                } else if (retType.isSome()) {
                    formatter.appendHtml(" : ");
                    Type type = Option.unwrap(retType);
                    formatter.appendText(getTypeHtml(type));
                }

            }

            return formatter.getText();
        }

        private String getTypeHtml(Type type) {
            StringBuilder name = new StringBuilder();
            
            if (type instanceof IdType) {
                name.append(((IdType) type).getName().getName().getText());
            } else if (type instanceof TupleType) {
                name.append("(");
                List<Type> elements = ((TupleType) type).getElements();
                for (Iterator<Type> itr = elements.iterator(); itr.hasNext();) {
                    name.append(getTypeHtml(itr.next()));
                    
                    if (itr.hasNext()) {
                        name.append(", ");
                    }
                }
                name.append(")");
            } else if (type instanceof VoidType){
                name.append("()");
            } else {
                name.append(type.stringName());
            }
            
            return name.toString();
        }

        public ElementHandle getElementHandle() {
            return signature;
        }

        public ElementKind getKind() {
            return signature.getKind();
        }

        public Set<Modifier> getModifiers() {
            return signature.getModifiers();
        }

        public boolean isLeaf() {
            switch (signature.getKind()) {
                case ATTRIBUTE:
                case CONSTANT:
                case CONSTRUCTOR:
                case METHOD:
                case FIELD:
                case KEYWORD:
                case VARIABLE:
                case OTHER:
                case GLOBAL:
                case PACKAGE:
                case PROPERTY:
                    return true;

                case FILE:
                case MODULE:
                case CLASS:
                    return false;

                default:
                    throw new RuntimeException("Unhandled kind: " + signature.getKind());
            }
        }

        public List<? extends StructureItem> getNestedItems() {
            List<Signature> nested = signature.getEnclosedScope().getDefinitions();

            if ((nested != null) && (nested.size() > 0)) {
                List<FortressStructureItem> children = new ArrayList<FortressStructureItem>(nested.size());

                for (Signature signature : nested) {
                    children.add(new FortressStructureItem(signature, info, formatter));
                }

                return children;
            } else {
                return Collections.emptyList();
            }
        }

        public long getPosition() {
            return AstUtilities.getRange(info, signature.getNode()).getStart();
        }

        public long getEndPosition() {
            return AstUtilities.getRange(info, signature.getNode()).getEnd();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (!(o instanceof FortressStructureItem)) {
                return false;
            }

            FortressStructureItem d = (FortressStructureItem) o;

            if (signature.getKind() != d.signature.getKind()) {
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
            hash = (29 * hash) + ((this.signature.getKind() != null) ? this.signature.getKind().hashCode() : 0);

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
