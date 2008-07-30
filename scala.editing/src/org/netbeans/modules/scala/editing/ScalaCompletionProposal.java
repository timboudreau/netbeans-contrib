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
package org.netbeans.modules.scala.editing;

import org.netbeans.modules.scala.editing.ast.ScalaElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.gsf.api.CompletionProposal;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.gsf.api.Modifier;
import org.netbeans.modules.scala.editing.ScalaCodeCompletion.CompletionRequest;
import org.openide.util.Exceptions;
import scala.tools.nsc.symtab.Symbols.Symbol;
import scala.tools.nsc.symtab.Symbols.TypeSymbol;
import scala.tools.nsc.symtab.Types.Type;

/**
 * 
 * @author Caoyuan Deng 
 */
public abstract class ScalaCompletionProposal implements CompletionProposal {

    private static ImageIcon keywordIcon;
    protected CompletionRequest request;
    protected ScalaElement element;

    private ScalaCompletionProposal(ScalaElement element, CompletionRequest request) {
        this.request = request;
        this.element = element;
    }

    public int getAnchorOffset() {
        return request.anchor;
    }

    public String getName() {
        return element.getName();
    }

    public String getInsertPrefix() {
        return getName();
    }

    public String getSortText() {
        String name = getName();
        char c = name.charAt(0);
        if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
            return name;
        } else {
            return '~' + name;
        }
    }

    public ElementHandle getElement() {
        return element;
    }

    public ElementKind getKind() {
        return getElement().getKind();
    }

    public ImageIcon getIcon() {
        return null;
    }

    public String getLhsHtml(HtmlFormatter formatter) {
        boolean emphasize = !element.isInherited();
        boolean strike = element.isDeprecated();

        if (emphasize) {
            formatter.emphasis(true);
        }
        if (strike) {
            formatter.deprecated(true);
        }
        ElementKind kind = getKind();
        formatter.name(kind, true);
        formatter.appendText(getName());
        formatter.name(kind, false);

        if (strike) {
            formatter.deprecated(false);
        }
        if (emphasize) {
            formatter.emphasis(false);
        }

        return formatter.getText();
    }

    public String getRhsHtml(HtmlFormatter formatter) {
        Symbol symbol = element.getSymbol();

        formatter.type(true);
        Type retType = null;
        try {
            retType = symbol.tpe().resultType();
        } catch (Throwable ex) {
            ScalaGlobal.reset();
        }
        if (retType != null && !symbol.isConstructor()) {
            formatter.appendText(ScalaElement.typeToString(retType));
        }
        formatter.type(false);

        return formatter.getText();
    }

    public Set<Modifier> getModifiers() {
        return element.getModifiers();
    }

    @Override
    public String toString() {
        String cls = this.getClass().getName();
        cls = cls.substring(cls.lastIndexOf('.') + 1);

        return cls + "(" + getKind() + "): " + getName();
    }

    public boolean isSmart() {
        return false;
    //return indexedElement != null ? indexedElement.isSmart() : true;
    }

    public List<String> getInsertParams() {
        return null;
    }

    public String[] getParamListDelimiters() {
        return new String[]{"(", ")"}; // NOI18N

    }

    public String getCustomInsertTemplate() {
        return null;
    }

    protected static class FunctionProposal extends ScalaCompletionProposal {

        private Type functionType;

        FunctionProposal(ScalaElement element, CompletionRequest request) {
            super(element, request);
            Symbol symbol = element.getSymbol();
            functionType = symbol.tpe();
        }

        @Override
        public String getInsertPrefix() {
            return getName();
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.METHOD;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            boolean strike = element.isDeprecated();
            boolean emphasize = !element.isInherited();
            if (strike) {
                formatter.deprecated(true);
            }
            if (emphasize) {
                formatter.emphasis(true);
            }

            ElementKind kind = getKind();
            formatter.name(kind, true);
            formatter.appendText(getName());
            formatter.name(kind, false);

            if (emphasize) {
                formatter.emphasis(false);
            }
            if (strike) {
                formatter.deprecated(false);
            }

            scala.List typeParams = functionType.typeParams();
            if (!typeParams.isEmpty()) {
                formatter.appendHtml("[");
                int size = typeParams.size();
                for (int i = 0; i < size; i++) {
                    TypeSymbol typeParam = (TypeSymbol) typeParams.apply(i);
                    formatter.appendText(typeParam.nameString());

                    if (i < size - 1) {
                        formatter.appendText(", "); // NOI18N
                    }
                }

                formatter.appendHtml("]");
            }

            scala.List paramTypes = functionType.paramTypes();

            if (!paramTypes.isEmpty()) {
                formatter.appendHtml("("); // NOI18N

                int size = paramTypes.size();
                for (int i = 0; i < size; i++) {
                    Type param = (Type) paramTypes.apply(i);

                    formatter.parameters(true);
                    formatter.appendText("a" + Integer.toString(i));
                    formatter.parameters(false);
                    formatter.appendText(": ");
                    formatter.type(true);
                    formatter.appendText(param.toString());
                    formatter.type(false);

                    if (i < size - 1) {
                        formatter.appendText(", "); // NOI18N
                    }
                }

                formatter.appendHtml(")"); // NOI18N
            }

            return formatter.getText();
        }

        @Override
        public List<String> getInsertParams() {
            scala.List paramTypes = functionType.paramTypes();
            if (!paramTypes.isEmpty()) {
                int size = paramTypes.size();
                List<String> result = new ArrayList<String>(size);
                for (int i = 0; i < size; i++) { // && tIt.hasNext()) {
                    Type param = (Type) paramTypes.apply(i);
                    result.add(param.typeSymbol().nameString().toLowerCase());
                }
                return result;
            } else {
                return Collections.<String>emptyList();
            }
        }

        @Override
        public String getCustomInsertTemplate() {
            StringBuilder sb = new StringBuilder();

            final String insertPrefix = getInsertPrefix();
            sb.append(insertPrefix);

            if (functionType.paramTypes().isEmpty()) {
                return sb.toString();
            }

            List<String> params = getInsertParams();
            String startDelimiter = "(";
            String endDelimiter = ")";
            int paramCount = params.size();

            sb.append(startDelimiter);

            int id = 1;
            for (int i = 0; i < paramCount; i++) {
                String paramDesc = params.get(i);
                sb.append("${"); //NOI18N
                // Ensure that we don't use one of the "known" logical parameters
                // such that a parameter like "path" gets replaced with the source file
                // path!

                sb.append("js-cc-"); // NOI18N

                sb.append(Integer.toString(id++));
                sb.append(" default=\""); // NOI18N

                int typeIndex = paramDesc.indexOf(':');
                if (typeIndex != -1) {
                    sb.append(paramDesc, 0, typeIndex);
                } else {
                    sb.append(paramDesc);
                }
                sb.append("\""); // NOI18N

                sb.append("}"); //NOI18N

                if (i < paramCount - 1) {
                    sb.append(", "); //NOI18N

                }
            }
            sb.append(endDelimiter);

            sb.append("${cursor}"); // NOI18N

            // Facilitate method parameter completion on this item
            try {
                ScalaCodeCompletion.callLineStart = Utilities.getRowStart(request.doc, request.anchor);
            //ScalaCodeCompletion.callMethod = function;
            } catch (BadLocationException ble) {
                Exceptions.printStackTrace(ble);
            }

            return sb.toString();
        }
    }

    protected static class KeywordProposal extends ScalaCompletionProposal {

        private static final String KEYWORD = "org/netbeans/modules/scala/editing/resources/scala16x16.png"; //NOI18N
        private final String keyword;
        private final String description;

        KeywordProposal(String keyword, String description, CompletionRequest request) {
            super(null, request);
            this.keyword = keyword;
            this.description = description;
        }

        @Override
        public String getName() {
            return keyword;
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.KEYWORD;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            ElementKind kind = getKind();
            formatter.name(kind, true);
            formatter.appendHtml(getName());
            formatter.name(kind, false);

            return formatter.getText();
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            if (description != null) {
                formatter.appendText(description);

                return formatter.getText();
            } else {
                return null;
            }
        }

        @Override
        public ImageIcon getIcon() {
            if (keywordIcon == null) {
                keywordIcon = new ImageIcon(org.openide.util.Utilities.loadImage(KEYWORD));
            }

            return keywordIcon;
        }

        @Override
        public Set<Modifier> getModifiers() {
            return Collections.emptySet();
        }

        @Override
        public ElementHandle getElement() {
            // For completion documentation
            return new GsfElement(org.netbeans.modules.gsf.api.ElementKind.KEYWORD);
        }

        @Override
        public boolean isSmart() {
            return false;
        }
    }

    protected static class TagProposal extends ScalaCompletionProposal {

        private final String tag;
        private final String description;
        private final org.netbeans.modules.gsf.api.ElementKind kind;

        TagProposal(String keyword, String description, CompletionRequest request, org.netbeans.modules.gsf.api.ElementKind kind) {
            super(null, request);
            this.tag = keyword;
            this.description = description;
            this.kind = kind;
        }

        @Override
        public String getName() {
            return tag;
        }

        @Override
        public ElementKind getKind() {
            return kind;
        }

        //@Override
        //public String getLhsHtml() {
        //    // Override so we can put HTML contents in
        //    ElementKind kind = getKind();
        //    HtmlFormatter formatter = request.formatter;
        //    formatter.reset();
        //    formatter.name(kind, true);
        //    //formatter.appendText(getSimpleName());
        //    formatter.appendHtml(getSimpleName());
        //    formatter.name(kind, false);
        //
        //    return formatter.getText();
        //}
        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            if (description != null) {
                //formatter.appendText(description);
                formatter.appendHtml("<i>");
                formatter.appendHtml(description);
                formatter.appendHtml("</i>");

                return formatter.getText();
            } else {
                return null;
            }
        }

        @Override
        public Set<Modifier> getModifiers() {
            return Collections.emptySet();
        }

        @Override
        public ElementHandle getElement() {
            // For completion documentation
            return new GsfElement(org.netbeans.modules.gsf.api.ElementKind.KEYWORD);
        }

        @Override
        public boolean isSmart() {
            return true;
        }
    }

    protected static class PlainProposal extends ScalaCompletionProposal {

        PlainProposal(ScalaElement symbol, CompletionRequest request) {
            super(symbol, request);
        }
    }

    protected static class PackageItem extends ScalaCompletionProposal {

        PackageItem(ScalaElement symbol, CompletionRequest request) {
            super(symbol, request);

        }

        @Override
        public org.netbeans.modules.gsf.api.ElementKind getKind() {
            return org.netbeans.modules.gsf.api.ElementKind.PACKAGE;
        }

        @Override
        public String getName() {
            String name = element.getName();
            int lastDot = name.lastIndexOf('.');
            if (lastDot > 0) {
                name = name.substring(lastDot + 1, name.length());
            }
            return name;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            ElementKind kind = getKind();
            formatter.name(kind, true);
            formatter.appendText(getName());
            formatter.name(kind, false);

            return formatter.getText();
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            return null;
        }

        @Override
        public boolean isSmart() {
            return true;
        }
    }

    protected static class TypeProposal extends ScalaCompletionProposal {

        TypeProposal(ScalaElement element, CompletionRequest request) {
            super(element, request);

        }

        @Override
        public ElementKind getKind() {
            return ElementKind.CLASS;
        }

        @Override
        public String getName() {
            String name = element.getName();
            int lastDot = name.lastIndexOf('.');
            if (lastDot > 0) {
                name = name.substring(lastDot + 1, name.length());
            }
            return name;
        }

        @Override
        public String getLhsHtml(HtmlFormatter formatter) {
            org.netbeans.modules.gsf.api.ElementKind kind = getKind();
            boolean strike = element.isDeprecated();
            if (strike) {
                formatter.deprecated(true);
            }
            formatter.name(kind, true);
            formatter.appendText(getName());
            formatter.name(kind, false);
            if (strike) {
                formatter.deprecated(false);
            }

            return formatter.getText();
        }

        @Override
        public String getRhsHtml(HtmlFormatter formatter) {
            return null;
        }
    }
}
