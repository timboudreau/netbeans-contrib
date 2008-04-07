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
import org.netbeans.modules.scala.editing.nodes.AstDefinition;
import org.netbeans.modules.scala.editing.nodes.AstScope;

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

        for (AstDefinition definition : rootScope.getDefinitions()) {
            items.add(new ScalaStructureItem(definition, info, formatter));
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

        private AstDefinition definition;
        private CompilationInfo info;
        private HtmlFormatter formatter;

        private ScalaStructureItem(AstDefinition definition, CompilationInfo info, HtmlFormatter formatter) {
            this.definition = definition;
            this.info = info;
            this.formatter = formatter;
        }

        public String getName() {
            return definition.getName();
        }

        public String getSortText() {
            return getName();
        }

        public String getHtml() {
            formatter.reset();
            definition.htmlFormat(formatter);
            return formatter.getText();
//            formatter.reset();
//            boolean strike = signature.getModifiers().contains(Modifier.DEPRECATED);
//            if (strike) {
//                formatter.deprecated(true);
//            }

//            formatter.appendText(getName());

//            if (strike) {
//                formatter.deprecated(false);
//            }

//            if (definition.getNode() instanceof FnDef) {
//                // Append parameters
//                FnDef fnDef = (FnDef) definition.getNode();
//                List<StaticParam> staticParams = fnDef.getStaticParams();
//                if (staticParams.size() > 0) {
//                    formatter.appendHtml("[\\");
//
//                    for (Iterator<StaticParam> itr = staticParams.iterator(); itr.hasNext();) {
//                        StaticParam param = itr.next();
//
//                        if (param instanceof NatParam) {
//                            formatter.appendText(param.toString());
//                        } else if (param instanceof SimpleTypeParam) {
//                            formatter.appendText(param.toString());
//                        } else {
//                            formatter.appendText(param.stringName());
//                        }
//
//
//                        if (itr.hasNext()) {
//                            formatter.appendHtml(", ");
//                        }
//                    }
//
//                    formatter.appendHtml("\\]");
//                }
//
//                Collection<Param> params = fnDef.getParams();
//
//                formatter.appendHtml("(");
//                if ((params != null) && (params.size() > 0)) {
//                    formatter.parameters(true);
//
//                    for (Iterator<Param> itr = params.iterator(); itr.hasNext();) {
//                        Param param = itr.next();
//                        String nameStr = param.getName().stringName();
//                        formatter.appendText(nameStr);
//
//                        String typeStr = null;
//                        if (param instanceof NormalParam) {
//                            Option<Type> typeOption = ((NormalParam) param).getType();
//                            if (typeOption.isNone()) {
//                            } else {
//                                Type type = Option.unwrap(typeOption);
//                                typeStr = getTypeHtml(type);
//                            }
//                        } else if (param instanceof VarargsParam) {
//                            Type type = ((VarargsParam) param).getVarargsType().getType();
//                            typeStr = getTypeHtml(type) + "...";
//                        }
//                        if (typeStr != null) {
//                            formatter.appendHtml(":");
//                            formatter.appendText(typeStr);
//                        }
//
//                        if (itr.hasNext()) {
//                            formatter.appendHtml(", ");
//                        }
//                    }
//
//                    formatter.parameters(false);
//                }
//                formatter.appendHtml(")");
//
//                Option<Type> retType = fnDef.getReturnType();
//                if (retType.isNone()) {
//                } else if (retType.isSome()) {
//                    formatter.appendHtml(" : ");
//                    Type type = Option.unwrap(retType);
//                    formatter.appendText(getTypeHtml(type));
//                }
//
//            }

//            return formatter.getText();
        }

//        private String getTypeHtml(Type type) {
//            StringBuilder sb = new StringBuilder();
//
//            if (type instanceof IdType) {
//                String typeName = ((IdType) type).getName().getName().getText();
//                sb.append(FortressUtils.unicodedTypeName(typeName));
//            } else if (type instanceof TupleType) {
//                sb.append("(");
//                List<Type> elements = ((TupleType) type).getElements();
//                for (Iterator<Type> itr = elements.iterator(); itr.hasNext();) {
//                    sb.append(getTypeHtml(itr.next()));
//
//                    if (itr.hasNext()) {
//                        sb.append(", ");
//                    }
//                }
//                sb.append(")");
//            } else if (type instanceof VoidType) {
//                sb.append("()");
//            } else if (type instanceof InstantiatedType) {
//                String idName = ((InstantiatedType) type).getName().getName().getText();
//                sb.append(idName);
//                sb.append("[\\"); // "[\\" "\u27E6" LEFT WHITE SQUARE BRACKET 
//
//                List<StaticArg> args = ((InstantiatedType) type).getArgs();
//                for (Iterator<StaticArg> itr = args.iterator(); itr.hasNext();) {
//                    StaticArg arg = itr.next();
//                    String argStr = getArgHtml(arg);
//
//                    sb.append(argStr);
//
//                    if (itr.hasNext()) {
//                        sb.append(", ");
//                    }
//                }
//                sb.append("\\]"); // "\\]" "\u27E7" RIGHT WHITE SQUARE BRACKET 
//
//            } else if (type instanceof ArrowType) {
//                Type domain = ((ArrowType) type).getDomain();
//                Type range = ((ArrowType) type).getRange();
//
//                sb.append(getTypeHtml(domain));
//                sb.append("\u2192"); // "->"
//
//                sb.append(getTypeHtml(range));
//            } else if (type instanceof ArrayType) {
//                Type element = ((ArrayType) type).getElement();
//                sb.append(getTypeHtml(element));
//                sb.append("[");
//
//                Indices indices = ((ArrayType) type).getIndices();
//                List<ExtentRange> extentRanges = indices.getExtents();
//                for (Iterator<ExtentRange> itr = extentRanges.iterator(); itr.hasNext();) {
//                    Option<StaticArg> arg = itr.next().getSize();
//                    if (arg.isSome()) {
//                        sb.append(getArgHtml(Option.unwrap(arg)));
//                    }
//
//                    if (itr.hasNext()) {
//                        sb.append(", ");
//                    }
//                }
//
//                sb.append("]");
//            } else {
//                // @todo, leave stringName to get its kind of type
//                sb.append(type.stringName());
//            }
//
//            return sb.toString();
//        }

//        private String getArgHtml(StaticArg arg) {
//            String argStr = null;
//
//            if (arg instanceof TypeArg) {
//                argStr = getTypeHtml(((TypeArg) arg).getType());
//            } else if (arg instanceof IntArg) {
//                argStr = ((IntArg) arg).getVal().toString();
//            } else {
//                argStr = getTypeHtml(arg);
//            }
//
//            return argStr;
//        }
        
        public ElementHandle getElementHandle() {
            return definition;
        }

        public ElementKind getKind() {
            return definition.getKind();
        }

        public Set<Modifier> getModifiers() {
            return definition.getModifiers();
        }

        public boolean isLeaf() {
            switch (definition.getKind()) {
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
                case PARAMETER:
                    return true;

                case FILE:
                case MODULE:
                case CLASS:
                    return false;

                default:
                    throw new RuntimeException("Unhandled kind: " + definition.getKind());
            }
        }

        public List<? extends StructureItem> getNestedItems() {
            List<AstDefinition> nested = definition.getBindingScope().getDefinitions();

            if ((nested != null) && (nested.size() > 0)) {
                List<ScalaStructureItem> children = new ArrayList<ScalaStructureItem>(nested.size());

                for (AstDefinition child : nested) {
                    children.add(new ScalaStructureItem(child, info, formatter));
                }

                return children;
            } else {
                return Collections.emptyList();
            }
        }

        public long getPosition() {
            return definition.getBindingScope().getRange().getStart();
        }

        public long getEndPosition() {
            return definition.getBindingScope().getRange().getEnd();
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

            if (definition.getKind() != d.definition.getKind()) {
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
            hash = (29 * hash) + ((this.definition.getKind() != null) ? this.definition.getKind().hashCode() : 0);

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
