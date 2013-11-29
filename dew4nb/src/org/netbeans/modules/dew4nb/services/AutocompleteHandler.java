/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dew4nb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.support.ReferencesCount;
import org.netbeans.api.whitelist.WhiteListQuery;
import org.netbeans.modules.dew4nb.CompletionItem;
import org.netbeans.modules.dew4nb.Context;
import org.netbeans.modules.dew4nb.JavacCompletionResult;
import org.netbeans.modules.dew4nb.JavacQuery;
import org.netbeans.modules.dew4nb.JavacMessageType;
import org.netbeans.modules.dew4nb.RequestHandler;
import org.netbeans.modules.dew4nb.SourceProvider;
import org.netbeans.modules.dew4nb.Status;
import org.netbeans.modules.editor.completion.CompletionItemComparator;
import org.netbeans.modules.editor.java.JavaCompletionItem;
import org.netbeans.modules.editor.java.JavaCompletionItemFactory;
import org.netbeans.modules.editor.java.JavaCompletionProvider;
import org.netbeans.modules.editor.java.Utilities;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.openide.util.lookup.ServiceProvider;

/** Sample, code completion handler.
 */
@ServiceProvider(service = RequestHandler.class)
public class AutocompleteHandler extends RequestHandler<JavacQuery, JavacCompletionResult> {
    private static final Logger LOG = Logger.getLogger(AutocompleteHandler.class.getName());
    public AutocompleteHandler() {
        super(JavacMessageType.autocomplete, JavacQuery.class, JavacCompletionResult.class);
    }

    @Override
    protected boolean handle(JavacQuery query, JavacCompletionResult res) {
        assert query.getType() == JavacMessageType.autocomplete;
        final String java = query.getJava();
        final int offset = query.getOffset();
        final Context ctx = query.getContext();
        LOG.log(
            Level.INFO,
            "Autocomplete  on {0}", //NOI18N
            ctx == null ?
                "<unknown>" :       //NOI18N
                ctx.getPath());
        Status status = Status.runtime_error;
        try {
            final Source s = SourceProvider.getInstance().getSource(ctx, java);
            if (s != null) {
                List<? extends org.netbeans.spi.editor.completion.CompletionItem> items = JavaCompletionProvider.query(s, CompletionProvider.COMPLETION_QUERY_TYPE, offset, offset, new Item.Factory());
                Collections.sort(items, CompletionItemComparator.BY_PRIORITY);
                for (org.netbeans.spi.editor.completion.CompletionItem item : items) {
                    if (item instanceof Item) {
                        res.getCompletions().add(((Item)item).toCompletionItem());
                    }
                }
                status = Status.success;
            }
        } catch (Exception exception) {
            //pass
        }
        res.setStatus(status);
        return true;        
    }
    
    private abstract static class Item extends JavaCompletionItem {

        private static final class Factory implements JavaCompletionItemFactory {

            @Override
            public JavaCompletionItem createKeywordItem(String kwd, String postfix, int substitutionOffset, boolean smartType) {
                return new KeywordItem(kwd, 0, postfix, substitutionOffset, smartType);
            }

            @Override
            public JavaCompletionItem createPackageItem(String pkgFQN, int substitutionOffset, boolean inPackageStatement) {
                return new PackageItem(pkgFQN, substitutionOffset, inPackageStatement);
            }

            @Override
            public JavaCompletionItem createTypeItem(CompilationInfo info, TypeElement elem, DeclaredType type, int substitutionOffset, ReferencesCount referencesCount, boolean isDeprecated, boolean insideNew, boolean addTypeVars, boolean addSimpleName, boolean smartType, boolean autoImportEnclosingType, WhiteListQuery.WhiteList whiteList) {
                switch (elem.getKind()) {
                    case CLASS:
                        return new ClassItem(info, elem, type, 0, substitutionOffset, referencesCount, isDeprecated, insideNew, addTypeVars, addSimpleName, smartType, autoImportEnclosingType, whiteList);
                    case INTERFACE:
                        return new InterfaceItem(info, elem, type, 0, substitutionOffset, referencesCount, isDeprecated, insideNew, addTypeVars, addSimpleName, smartType, autoImportEnclosingType, whiteList);
                    case ENUM:
                        return new EnumItem(info, elem, type, 0, substitutionOffset, referencesCount, isDeprecated, insideNew, addSimpleName, smartType, autoImportEnclosingType, whiteList);
                    case ANNOTATION_TYPE:
                        return new AnnotationTypeItem(info, elem, type, 0, substitutionOffset, referencesCount, isDeprecated, insideNew, addSimpleName, smartType, autoImportEnclosingType, whiteList);
                    default:
                        throw new IllegalArgumentException("kind=" + elem.getKind());
                }
            }

            @Override
            public JavaCompletionItem createTypeItem(ElementHandle<TypeElement> handle, EnumSet<ElementKind> kinds, int substitutionOffset, ReferencesCount referencesCount, Source source, boolean insideNew, boolean addTypeVars, boolean afterExtends, WhiteListQuery.WhiteList whiteList) {
                return null;
            }

            @Override
            public JavaCompletionItem createArrayItem(CompilationInfo info, ArrayType type, int substitutionOffset, ReferencesCount referencesCount, Elements elements, WhiteListQuery.WhiteList whiteList) {
                int dim = 0;
                TypeMirror tm = type;
                while(tm.getKind() == TypeKind.ARRAY) {
                    tm = ((ArrayType)tm).getComponentType();
                    dim++;
                }
                if (tm.getKind().isPrimitive()) {
                    return new KeywordItem(tm.toString(), dim, null, substitutionOffset, true);
                }
                if (tm.getKind() == TypeKind.DECLARED || tm.getKind() == TypeKind.ERROR) {
                    DeclaredType dt = (DeclaredType)tm;
                    TypeElement elem = (TypeElement)dt.asElement();
                    switch (elem.getKind()) {
                        case CLASS:
                            return new ClassItem(info, elem, dt, dim, substitutionOffset, referencesCount, elements.isDeprecated(elem), false, false, false, true, false, whiteList);
                        case INTERFACE:
                            return new InterfaceItem(info, elem, dt, dim, substitutionOffset, referencesCount, elements.isDeprecated(elem), false, false, false, true, false, whiteList);
                        case ENUM:
                            return new EnumItem(info, elem, dt, dim, substitutionOffset, referencesCount, elements.isDeprecated(elem), false, false, true, false, whiteList);
                        case ANNOTATION_TYPE:
                            return new AnnotationTypeItem(info, elem, dt, dim, substitutionOffset, referencesCount, elements.isDeprecated(elem), false, false, true, false, whiteList);
                    }
                }
                throw new IllegalArgumentException("array element kind=" + tm.getKind());
            }

            @Override
            public JavaCompletionItem createTypeParameterItem(TypeParameterElement elem, int substitutionOffset) {
                return new TypeParameterItem(elem, substitutionOffset);
            }

            @Override
            public JavaCompletionItem createVariableItem(CompilationInfo info, VariableElement elem, TypeMirror type, int substitutionOffset, ReferencesCount referencesCount, boolean isInherited, boolean isDeprecated, boolean smartType, int assignToVarOffset, WhiteListQuery.WhiteList whiteList) {
                switch (elem.getKind()) {
                    case LOCAL_VARIABLE:
                    case RESOURCE_VARIABLE:
                    case PARAMETER:
                    case EXCEPTION_PARAMETER:
                        return new VariableItem(info, type, elem.getSimpleName().toString(), substitutionOffset, false, smartType, assignToVarOffset);
                    case ENUM_CONSTANT:
                    case FIELD:
                        return new FieldItem(info, elem, type, substitutionOffset, referencesCount, isInherited, isDeprecated, smartType, assignToVarOffset, whiteList);
                    default:
                        throw new IllegalArgumentException("kind=" + elem.getKind());
                }
            }

            @Override
            public JavaCompletionItem createVariableItem(CompilationInfo info, String varName, int substitutionOffset, boolean newVarName, boolean smartType) {
                return new VariableItem(info, null, varName, substitutionOffset, newVarName, smartType, -1);
            }

            @Override
            public JavaCompletionItem createExecutableItem(CompilationInfo info, ExecutableElement elem, ExecutableType type, int substitutionOffset, ReferencesCount referencesCount, boolean isInherited, boolean isDeprecated, boolean inImport, boolean addSemicolon, boolean smartType, int assignToVarOffset, boolean memberRef, WhiteListQuery.WhiteList whiteList) {
                switch (elem.getKind()) {
                    case METHOD:
                        return new MethodItem(info, elem, type, substitutionOffset, referencesCount, isInherited, isDeprecated, inImport, addSemicolon, smartType, assignToVarOffset, memberRef, whiteList);
                    case CONSTRUCTOR:
                        return new ConstructorItem(info, elem, type, substitutionOffset, isDeprecated, smartType, null, whiteList);
                    default:
                        throw new IllegalArgumentException("kind=" + elem.getKind());
                }
            }

            @Override
            public JavaCompletionItem createThisOrSuperConstructorItem(CompilationInfo info, ExecutableElement elem, ExecutableType type, int substitutionOffset, boolean isDeprecated, String name, WhiteListQuery.WhiteList whiteList) {
                if (elem.getKind() == ElementKind.CONSTRUCTOR) {
                    return new ConstructorItem(info, elem, type, substitutionOffset, isDeprecated, false, name, whiteList);
                }
                throw new IllegalArgumentException("kind=" + elem.getKind());
            }

            @Override
            public JavaCompletionItem createOverrideMethodItem(CompilationInfo info, ExecutableElement elem, ExecutableType type, int substitutionOffset, boolean implement, WhiteListQuery.WhiteList whiteList) {
                return null;
            }

            @Override
            public JavaCompletionItem createGetterSetterMethodItem(CompilationInfo info, VariableElement elem, TypeMirror type, int substitutionOffset, String name, boolean setter) {
                return null;
            }

            @Override
            public JavaCompletionItem createDefaultConstructorItem(TypeElement elem, int substitutionOffset, boolean smartType) {
                return null;
            }

            @Override
            public JavaCompletionItem createParametersItem(CompilationInfo info, ExecutableElement elem, ExecutableType type, int substitutionOffset, boolean isDeprecated, int activeParamIndex, String name) {
                return null;
            }

            @Override
            public JavaCompletionItem createAnnotationItem(CompilationInfo info, TypeElement elem, DeclaredType type, int substitutionOffset, ReferencesCount referencesCount, boolean isDeprecated, WhiteListQuery.WhiteList whiteList) {
                return new AnnotationItem(info, elem, type, substitutionOffset, referencesCount, isDeprecated, true, whiteList);
            }

            @Override
            public JavaCompletionItem createAttributeItem(CompilationInfo info, ExecutableElement elem, ExecutableType type, int substitutionOffset, boolean isDeprecated) {
                return new AttributeItem(info, elem, type, substitutionOffset, isDeprecated);
            }

            @Override
            public JavaCompletionItem createAttributeValueItem(CompilationInfo info, String value, String documentation, TypeElement element, int substitutionOffset, ReferencesCount referencesCount, WhiteListQuery.WhiteList whiteList) {
                return null;
            }

            @Override
            public JavaCompletionItem createStaticMemberItem(CompilationInfo info, DeclaredType type, Element memberElem, TypeMirror memberType, boolean multipleVersions, int substitutionOffset, boolean isDeprecated, boolean addSemicolon, WhiteListQuery.WhiteList whiteList) {
                return null;
            }

            @Override
            public JavaCompletionItem createStaticMemberItem(ElementHandle<TypeElement> handle, String name, int substitutionOffset, boolean addSemicolon, ReferencesCount referencesCount, Source source, WhiteListQuery.WhiteList whiteList) {
                return null;
            }

            @Override
            public JavaCompletionItem createChainedMembersItem(CompilationInfo info, List<? extends Element> chainedElems, List<? extends TypeMirror> chainedTypes, int substitutionOffset, boolean isDeprecated, boolean addSemicolon, WhiteListQuery.WhiteList whiteList) {
                return null;
            }

            @Override
            public JavaCompletionItem createInitializeAllConstructorItem(CompilationInfo info, boolean isDefault, Iterable<? extends VariableElement> fields, ExecutableElement superConstructor, TypeElement parent, int substitutionOffset) {
                return null;
            }        
        }
        
        private static final String DEPRECATED = " Deprecated"; //NOI18N
        private static final String NOT_INHERITED = " NotInherited"; //NOI18N
        
        private CompletionItem toCompletionItem() {
            return new CompletionItem(getInsertPrefix().toString(), getDisplayName(), getExtraText(), getRightText(), getStyle());            
        }

        protected Item(int substitutionOffset) {
            super(substitutionOffset);
        }

        protected String getDisplayName() {
            return null;
        }

        protected String getExtraText() {
            return null;
        }

        protected String getRightText() {
            return null;
        }

        protected String getStyle() {
            return null;
        }

        private static class KeywordItem extends Item {

            private static final String STYLE = "Java-hint Keyword"; //NOI18N

            private String kwd;
            private int dim;
            private boolean smartType;

            private KeywordItem(String kwd, int dim, String postfix, int substitutionOffset, boolean smartType) {
                super(substitutionOffset);
                this.kwd = kwd;
                this.dim = dim;
                this.smartType = smartType;
            }

            @Override
            public int getSortPriority() {
                return smartType ? 670 - SMART_TYPE : 670;
            }

            @Override
            public CharSequence getSortText() {
                return kwd;
            }

            @Override
            public String getInsertPrefix() {
                return kwd;
            }

            @Override
            protected String getStyle() {
                return STYLE;
            }

            @Override
            protected String getDisplayName() {
                StringBuilder sb = new StringBuilder();
                sb.append(kwd);
                for(int i = 0; i < dim; i++) {
                    sb.append("[]"); //NOI18N
                }
                return sb.toString();
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder(kwd);
                for(int i = 0; i < dim; i++) {
                    sb.append("[]"); //NOI18N
                }
                return sb.toString();
            }
        }

        private static class PackageItem extends Item {

            private static final String STYLE = "Java-hint Package"; //NOI18N

            private String simpleName;
            private String sortText;

            private PackageItem(String pkgFQN, int substitutionOffset, boolean inPackageStatement) {
                super(substitutionOffset);
                int idx = pkgFQN.lastIndexOf('.');
                this.simpleName = idx < 0 ? pkgFQN : pkgFQN.substring(idx + 1);
                this.sortText = this.simpleName + "#" + pkgFQN; //NOI18N
            }

            @Override
            public int getSortPriority() {
                return 900;
            }

            @Override
            public CharSequence getSortText() {
                return sortText;
            }

            @Override
            public String getInsertPrefix() {
                return simpleName;
            }

            @Override
            protected String getStyle() {
                return STYLE;
            }

            @Override
            public String toString() {
                return simpleName;
            }
        }

        private static class ClassItem extends Item {

            private static final String STYLE = "Java-hint Class"; //NOI18N

            private int dim;
            protected boolean isDeprecated;
            private boolean smartType;
            private String simpleName;
            private String typeName;
            private String enclName;
            private CharSequence sortText;

            private ClassItem(CompilationInfo info, TypeElement elem, DeclaredType type, int dim, int substitutionOffset, ReferencesCount referencesCount, boolean isDeprecated, boolean insideNew, boolean addTypeVars, boolean addSimpleName, boolean smartType, boolean autoImportEnclosingType, WhiteListQuery.WhiteList whiteList) {
                super(substitutionOffset);
                this.dim = dim;
                this.isDeprecated = isDeprecated;
                this.smartType = smartType;
                this.simpleName = elem.getSimpleName().toString();
                this.typeName = Utilities.getTypeName(info, type, false).toString();
                this.enclName = null;
                this.sortText = this.simpleName;
            }

            @Override
            public int getSortPriority() {
                return smartType ? 800 - SMART_TYPE : 800;
            }

            @Override
            public CharSequence getSortText() {
                return sortText;
            }

            @Override
            public String getInsertPrefix() {
                return simpleName;
            }

            @Override
            protected String getDisplayName() {
                StringBuilder sb = new StringBuilder();
                sb.append(typeName);
                for(int i = 0; i < dim; i++) {
                    sb.append("[]"); //NOI18N
                }
                return sb.toString();
            }

            @Override
            protected String getExtraText() {
                return enclName != null && enclName.length() > 0 ? " (" + enclName + ")" : null; //NOI18N
            }

            @Override
            protected String getStyle() {
                return isDeprecated ? STYLE + DEPRECATED : STYLE;
            }

            @Override
            public String toString() {
                return simpleName;
            }
        }

        private static class InterfaceItem extends ClassItem {

            private static final String STYLE = "Java-hint Interface"; //NOI18N

            private InterfaceItem(CompilationInfo info, TypeElement elem, DeclaredType type, int dim, int substitutionOffset, ReferencesCount referencesCount, boolean isDeprecated, boolean insideNew, boolean addTypeVars, boolean addSimpleName, boolean smartType, boolean autoImport, WhiteListQuery.WhiteList whiteList) {
                super(info, elem, type, dim, substitutionOffset, referencesCount, isDeprecated, insideNew, addTypeVars, addSimpleName, smartType, autoImport, whiteList);
            }

            @Override
            protected String getStyle() {
                return isDeprecated ? STYLE + DEPRECATED : STYLE;
            }
        }

        private static class EnumItem extends ClassItem {

            private static final String STYLE = "Java-hint Enum"; //NOI18N

            private EnumItem(CompilationInfo info, TypeElement elem, DeclaredType type, int dim, int substitutionOffset, ReferencesCount referencesCount, boolean isDeprecated, boolean insideNew, boolean addSimpleName, boolean smartType, boolean autoImport, WhiteListQuery.WhiteList whiteList) {
                super(info, elem, type, dim, substitutionOffset, referencesCount, isDeprecated, insideNew, false, addSimpleName, smartType, autoImport, whiteList);
            }

            @Override
            protected String getStyle() {
                return isDeprecated ? STYLE + DEPRECATED : STYLE;
            }
        }

        private static class AnnotationTypeItem extends ClassItem {

            private static final String STYLE = "Java-hint AnnotationType"; //NOI18N

            private AnnotationTypeItem(CompilationInfo info, TypeElement elem, DeclaredType type, int dim, int substitutionOffset, ReferencesCount referencesCount, boolean isDeprecated, boolean insideNew, boolean addSimpleName, boolean smartType, boolean autoImport, WhiteListQuery.WhiteList whiteList) {
                super(info, elem, type, dim, substitutionOffset, referencesCount, isDeprecated, insideNew, false, addSimpleName, smartType, autoImport, whiteList);
            }

            @Override
            protected String getStyle() {
                return isDeprecated ? STYLE + DEPRECATED : STYLE;
            }
        }

         private static class TypeParameterItem extends Item {

            private static final String STYLE = "Java-hint TypeParameter"; //NOI18N

            private String simpleName;

            private TypeParameterItem(TypeParameterElement elem, int substitutionOffset) {
                super(substitutionOffset);
                this.simpleName = elem.getSimpleName().toString();
            }

            @Override
            public int getSortPriority() {
                return 700;
            }

            @Override
            public CharSequence getSortText() {
                return simpleName;
            }

            @Override
            public String getInsertPrefix() {
                return simpleName;
            }

            @Override
            protected String getStyle() {
                return STYLE;
            }

            @Override
            public String toString() {
                return simpleName;
            }
        }

        private static class VariableItem extends Item {

            private static final String STYLE = "Java-hint LocalVariable"; //NOI18N

            private String varName;
            private boolean smartType;
            private String typeName;

            private VariableItem(CompilationInfo info, TypeMirror type, String varName, int substitutionOffset, boolean newVarName, boolean smartType, int assignToVarOffset) {
                super(substitutionOffset);
                this.varName = varName;
                this.smartType = smartType;
                this.typeName = type != null ? Utilities.getTypeName(info, type, false).toString() : null;
            }

            @Override
            public int getSortPriority() {
                return smartType ? 200 - SMART_TYPE : 200;
            }

            @Override
            public CharSequence getSortText() {
                return varName;
            }

            @Override
            public String getInsertPrefix() {
                return varName;
            }

            @Override
            protected String getRightText() {
                return typeName;
            }

            @Override
            protected String getStyle() {
                return STYLE;
            }

            @Override
            public String toString() {
                return (typeName != null ? typeName + " " : "") + varName; //NOI18N
            }
        }

        private static class FieldItem extends Item {

            private static final String STYLE = "Java-hint Field"; //NOI18N
            private static final String STYLE_PACKAGE = "Package"; //NOI18N
            private static final String STYLE_PRIVATE = "Private"; //NOI18N
            private static final String STYLE_PROTECTED = "Protected"; //NOI18N
            private static final String STYLE_STATIC = "Static"; //NOI18N

            private boolean isInherited;
            private boolean isDeprecated;
            private boolean smartType;
            private String simpleName;
            private Set<Modifier> modifiers;
            private String typeName;
            private CharSequence enclSortText;

            private FieldItem(CompilationInfo info, VariableElement elem, TypeMirror type, int substitutionOffset, ReferencesCount referencesCount, boolean isInherited, boolean isDeprecated, boolean smartType, int assignToVarOffset, WhiteListQuery.WhiteList whiteList) {
                super(substitutionOffset);
                this.isInherited = isInherited;
                this.isDeprecated = isDeprecated;
                this.smartType = smartType;
                this.simpleName = elem.getSimpleName().toString();
                this.modifiers = elem.getModifiers();
                this.typeName = Utilities.getTypeName(info, type, false).toString();
                this.enclSortText = ""; //NOI18N
            }

            @Override
            public int getSortPriority() {
                return smartType ? 300 - SMART_TYPE : 300;
            }

            @Override
            public CharSequence getSortText() {
                return simpleName + "#" + enclSortText; //NOI18N
            }

            @Override
            public String getInsertPrefix() {
                return simpleName;
            }

            @Override
            protected String getRightText() {
                return typeName;
            }

            @Override
            protected String getStyle() {
                StringBuilder style = new StringBuilder(STYLE);
                if (modifiers.contains(Modifier.STATIC)) {
                    style.append(STYLE_STATIC);
                }
                switch (getProtectionLevel(modifiers)) {
                    case PRIVATE_LEVEL:
                        style.append(STYLE_PRIVATE);
                        break;
                    case PACKAGE_LEVEL:
                        style.append(STYLE_PACKAGE);
                        break;
                    case PROTECTED_LEVEL:
                        style.append(STYLE_PROTECTED);
                        break;
                }
                if (isDeprecated) {
                    style.append(DEPRECATED);
                }
                if (!isInherited) {
                    style.append(NOT_INHERITED);
                }
                return style.toString();
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                for(Modifier mod : modifiers) {
                   sb.append(mod.toString());
                   sb.append(' ');
                }
                sb.append(typeName);
                sb.append(' ');
                sb.append(simpleName);
                return sb.toString();
            }
        }

        private static class MethodItem extends Item {

            private static final String STYLE = "Java-hint Method"; //NOI18N
            private static final String STYLE_PACKAGE = "Package"; //NOI18N
            private static final String STYLE_PRIVATE = "Private"; //NOI18N
            private static final String STYLE_PROTECTED = "Protected"; //NOI18N
            private static final String STYLE_STATIC = "Static"; //NOI18N

            private boolean isInherited;
            private boolean isDeprecated;
            private boolean smartType;
            private String simpleName;
            protected Set<Modifier> modifiers;
            private List<ParamDesc> params;
            private String typeName;
            private String sortText;
            private CharSequence enclSortText;

            private MethodItem(CompilationInfo info, ExecutableElement elem, ExecutableType type, int substitutionOffset, ReferencesCount referencesCount, boolean isInherited, boolean isDeprecated, boolean inImport, boolean addSemicolon, boolean smartType, int assignToVarOffset, boolean memberRef, WhiteListQuery.WhiteList whiteList) {
                super(substitutionOffset);
                this.isInherited = isInherited;
                this.isDeprecated = isDeprecated;
                this.smartType = smartType;
                this.simpleName = elem.getSimpleName().toString();
                this.modifiers = elem.getModifiers();
                this.params = new ArrayList<>();
                Iterator<? extends VariableElement> it = elem.getParameters().iterator();
                Iterator<? extends TypeMirror> tIt = type.getParameterTypes().iterator();
                while(it.hasNext() && tIt.hasNext()) {
                    TypeMirror tm = tIt.next();
                    if (tm == null) {
                        break;
                    }
                    this.params.add(new ParamDesc(tm.toString(), Utilities.getTypeName(info, tm, false, elem.isVarArgs() && !tIt.hasNext()).toString(), it.next().getSimpleName().toString()));
                }
                TypeMirror retType = type.getReturnType();
                this.typeName = Utilities.getTypeName(info, retType, false).toString();
                this.enclSortText = ""; //NOI18N
            }

            @Override
            public int getSortPriority() {
                return smartType ? 500 - SMART_TYPE : 500;
            }

            @Override
            public CharSequence getSortText() {
                if (sortText == null) {
                    StringBuilder sortParams = new StringBuilder();
                    sortParams.append('(');
                    int cnt = 0;
                    for(Iterator<ParamDesc> it = params.iterator(); it.hasNext();) {
                        ParamDesc param = it.next();
                        sortParams.append(param.typeName);
                        if (it.hasNext()) {
                            sortParams.append(',');
                        }
                        cnt++;
                    }
                    sortParams.append(')');
                    sortText = simpleName + "#" + enclSortText + "#" + ((cnt < 10 ? "0" : "") + cnt) + "#" + sortParams.toString(); //NOI18N
                }
                return sortText;
            }

            @Override
            public String getInsertPrefix() {
                return simpleName;
            }

            @Override
            protected String getExtraText() {
                StringBuilder sb = new StringBuilder();
                sb.append('(');
                for (Iterator<ParamDesc> it = params.iterator(); it.hasNext();) {
                    ParamDesc paramDesc = it.next();
                    sb.append(paramDesc.typeName);
                    sb.append(' ');
                    sb.append(paramDesc.name);
                    if (it.hasNext()) {
                        sb.append(", "); //NOI18N
                    }
                }
                sb.append(')');
                return sb.toString();
            }

            @Override
            protected String getRightText() {
                return typeName;
            }

            @Override
            protected String getStyle() {
                StringBuilder style = new StringBuilder(STYLE);
                if (modifiers.contains(Modifier.STATIC)) {
                    style.append(STYLE_STATIC);
                }
                switch (getProtectionLevel(modifiers)) {
                    case PRIVATE_LEVEL:
                        style.append(STYLE_PRIVATE);
                        break;
                    case PACKAGE_LEVEL:
                        style.append(STYLE_PACKAGE);
                        break;
                    case PROTECTED_LEVEL:
                        style.append(STYLE_PROTECTED);
                        break;
                }
                if (isDeprecated) {
                    style.append(DEPRECATED);
                }
                if (!isInherited) {
                    style.append(NOT_INHERITED);
                }
                return style.toString();
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                for (Modifier mod : modifiers) {
                    sb.append(mod.toString());
                    sb.append(' ');
                }
                sb.append(typeName);
                sb.append(' ');
                sb.append(simpleName);
                sb.append('(');
                for (Iterator<ParamDesc> it = params.iterator(); it.hasNext();) {
                    ParamDesc paramDesc = it.next();
                    sb.append(paramDesc.typeName);
                    sb.append(' ');
                    sb.append(paramDesc.name);
                    if (it.hasNext()) {
                        sb.append(", "); //NOI18N
                    }
                }
                sb.append(')');
                return sb.toString();
            }
        }

        private static class ConstructorItem extends Item {

            private static final String STYLE = "Java-hint Constructor"; //NOI18N
            private static final String STYLE_PACKAGE = "Package"; //NOI18N
            private static final String STYLE_PRIVATE = "Private"; //NOI18N
            private static final String STYLE_PROTECTED = "Protected"; //NOI18N

            private boolean isDeprecated;
            private boolean smartType;
            private String simpleName;
            protected Set<Modifier> modifiers;
            private List<ParamDesc> params;
            private boolean insertName;
            private String sortText;

            private ConstructorItem(CompilationInfo info, ExecutableElement elem, ExecutableType type, int substitutionOffset, boolean isDeprecated, boolean smartType, String name, WhiteListQuery.WhiteList whiteList) {
                super(substitutionOffset);
                this.isDeprecated = isDeprecated;
                this.smartType = smartType;
                this.simpleName = name != null ? name : elem.getEnclosingElement().getSimpleName().toString();
                this.insertName = name != null;
                this.modifiers = elem.getModifiers();
                this.params = new ArrayList<>();
                Iterator<? extends VariableElement> it = elem.getParameters().iterator();
                Iterator<? extends TypeMirror> tIt = type.getParameterTypes().iterator();
                while(it.hasNext() && tIt.hasNext()) {
                    TypeMirror tm = tIt.next();
                    if (tm == null) {
                        break;
                    }
                    this.params.add(new ParamDesc(tm.toString(), Utilities.getTypeName(info, tm, false, elem.isVarArgs() && !tIt.hasNext()).toString(), it.next().getSimpleName().toString()));
                }
            }

            @Override
            public int getSortPriority() {
                return insertName ? 550 : smartType ? 650 - SMART_TYPE : 650;
            }

            @Override
            public CharSequence getSortText() {
                if (sortText == null) {
                    StringBuilder sortParams = new StringBuilder();
                    sortParams.append('(');
                    int cnt = 0;
                    for (Iterator<ParamDesc> it = params.iterator(); it.hasNext();) {
                        ParamDesc paramDesc = it.next();
                        sortParams.append(paramDesc.typeName);
                        if (it.hasNext()) {
                            sortParams.append(',');
                        }
                        cnt++;
                    }
                    sortParams.append(')');
                    sortText = simpleName + "#" + ((cnt < 10 ? "0" : "") + cnt) + "#" + sortParams.toString(); //NOI18N
                }
                return sortText;
            }

            @Override
            public String getInsertPrefix() {
                return simpleName;
            }

            @Override
            protected String getExtraText() {
                StringBuilder sb = new StringBuilder();
                sb.append('(');
                for (Iterator<ParamDesc> it = params.iterator(); it.hasNext();) {
                    ParamDesc paramDesc = it.next();
                    sb.append(paramDesc.typeName);
                    sb.append(' ');
                    sb.append(paramDesc.name);
                    if (it.hasNext()) {
                        sb.append(", "); //NOI18N
                    }
                }
                sb.append(')');
                return sb.toString();
            }

            @Override
            protected String getStyle() {
                StringBuilder style = new StringBuilder(STYLE);
                switch (getProtectionLevel(modifiers)) {
                    case PRIVATE_LEVEL:
                        style.append(STYLE_PRIVATE);
                        break;
                    case PACKAGE_LEVEL:
                        style.append(STYLE_PACKAGE);
                        break;
                    case PROTECTED_LEVEL:
                        style.append(STYLE_PROTECTED);
                        break;
                }
                if (isDeprecated) {
                    style.append(DEPRECATED);
                }
                style.append(NOT_INHERITED);
                return style.toString();
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                for (Modifier mod : modifiers) {
                    sb.append(mod.toString());
                    sb.append(' ');
                }
                sb.append(simpleName);
                sb.append('('); //NOI18N
                for (Iterator<ParamDesc> it = params.iterator(); it.hasNext();) {
                    ParamDesc paramDesc = it.next();
                    sb.append(paramDesc.typeName);
                    sb.append(' ');
                    sb.append(paramDesc.name);
                    if (it.hasNext()) {
                        sb.append(", "); //NOI18N
                    }
                }
                sb.append(')');
                return sb.toString();
            }
        }

        private static class AnnotationItem extends AnnotationTypeItem {

            private AnnotationItem(CompilationInfo info, TypeElement elem, DeclaredType type, int substitutionOffset, ReferencesCount referencesCount, boolean isDeprecated, boolean smartType, WhiteListQuery.WhiteList whiteList) {
                super(info, elem, type, 0, substitutionOffset, referencesCount, isDeprecated, false, false, smartType, false, whiteList);
            }

            @Override
            public String getInsertPrefix() {
                return "@" + super.getInsertPrefix(); //NOI18N
            }
        }

        static class AttributeItem extends Item {

            private static final String STYLE = "Java-hint Attribute"; //NOI18N

            private boolean isDeprecated;
            private String simpleName;
            private String typeName;
            private String defaultValue;

            private AttributeItem(CompilationInfo info, ExecutableElement elem, ExecutableType type, int substitutionOffset, boolean isDeprecated) {
                super(substitutionOffset);
                this.isDeprecated = isDeprecated;
                this.simpleName = elem.getSimpleName().toString();
                this.typeName = Utilities.getTypeName(info, type.getReturnType(), false).toString();
                AnnotationValue value = elem.getDefaultValue();
                this.defaultValue = value != null ? value.getValue() instanceof TypeMirror ? Utilities.getTypeName(info, (TypeMirror)value.getValue(), false).toString() + ".class" : value.toString() : null; //NOI18N
            }

            @Override
            public int getSortPriority() {
                return 100;
            }

            @Override
            public CharSequence getSortText() {
                return simpleName;
            }

            @Override
            public String getInsertPrefix() {
                return simpleName;
            }

            @Override
            protected String getExtraText() {
                return defaultValue != null ? " = " + defaultValue : null; //NOI18N
            }

            @Override
            protected String getRightText() {
                return typeName;
            }

            @Override
            protected String getStyle() {
                StringBuilder style = new StringBuilder(STYLE);
                if (isDeprecated) {
                    style.append(DEPRECATED);
                }
                if (defaultValue == null) {
                    style.append(NOT_INHERITED);
                }
                return style.toString();
            }

            @Override
            public String toString() {
                return simpleName;
            }
        }

        private static final int PUBLIC_LEVEL = 3;
        private static final int PROTECTED_LEVEL = 2;
        private static final int PACKAGE_LEVEL = 1;
        private static final int PRIVATE_LEVEL = 0;

        private static int getProtectionLevel(Set<Modifier> modifiers) {
            if (modifiers.contains(Modifier.PUBLIC)) {
                return PUBLIC_LEVEL;
            }
            if (modifiers.contains(Modifier.PROTECTED)) {
                return PROTECTED_LEVEL;
            }
            if (modifiers.contains(Modifier.PRIVATE)) {
                return PRIVATE_LEVEL;
            }
            return PACKAGE_LEVEL;
        }

        private static class ParamDesc {
            private final String fullTypeName;
            private final String typeName;
            private final String name;

            public ParamDesc(String fullTypeName, String typeName, String name) {
                this.fullTypeName = fullTypeName;
                this.typeName = typeName;
                this.name = name;
            }
        }
    }
}
