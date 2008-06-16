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

import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.spi.DefaultParserFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.nodes.AstDef;
import org.netbeans.modules.scala.editing.nodes.tmpls.ClassTemplate;
import org.netbeans.modules.scala.editing.nodes.Function;
import org.netbeans.modules.scala.editing.nodes.tmpls.ObjectTemplate;
import org.netbeans.modules.scala.editing.nodes.tmpls.TraitTemplate;
import org.netbeans.modules.scala.editing.nodes.types.TypeRef;
import org.netbeans.modules.scala.editing.nodes.Var;
import org.netbeans.modules.scala.editing.nodes.types.TypeParam;
import org.netbeans.modules.scala.editing.nodes.types.TypeRef.PseudoTypeRef;
import org.netbeans.modules.scala.editing.nodes.types.WithTypeParams;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * An element coming from the Lucene index - not tied to an AST.
 * To obtain an equivalent AST element, use AstUtilities.getForeignNode().
 * 
 * @author Tor Norbye
 * @author Caoyuan Deng
 */
public class IndexedElement extends AstDef {

    protected static final int NAME_INDEX = 0;
    protected static final int IN_INDEX = 1;
    protected static final int CASE_SENSITIVE_INDEX = 2;
    protected static final int FLAG_INDEX = 3;
    protected static final int ARG_INDEX = 4;
    protected static final int NODE_INDEX = 5;
    protected static final int DOC_START_INDEX = 6;
    protected static final int DOC_END_INDEX = 7;
    protected static final int TYPE_PARAMS_INDEX = 8;
    protected static final int TYPE_INDEX = 9;
    // ------------- Flags/attributes -----------------

    // This should go into IndexedElement
    // Other attributes:
    // is constructor? prototype?
    // Plan: Stash a single item for class entries so I can search by document for the class.
    // Add more types into the types
    /** This method is documented */
    public static final int DOCUMENTED = 1 << 0;
    /** This method is protected */
    public static final int PROTECTED = 1 << 1;
    /** This method is private */
    public static final int PRIVATE = 1 << 2;
    /** This is a function, not a property */
    public static final int FUNCTION = 1 << 3;
    /** This element is "static" (e.g. it's a classvar for fields, class method for methods etc) */
    public static final int STATIC = 1 << 4;
    /** This element is deliberately not documented (rdoc :nodoc:) */
    public static final int NODOC = 1 << 5;
    /** This is a global variable */
    public static final int GLOBAL = 1 << 6;
    /** This is a constructor */
    public static final int CONSTRUCTOR = 1 << 7;
    /** This is a deprecated */
    public static final int DEPRECATED = 1 << 8;
    /** This is a documentation-only definition */
    public static final int DOC_ONLY = 1 << 9;
    /** This is a constant/final */
    public static final int FINAL = 1 << 10;
    public static final int CLASS = 1 << 11;
    public static final int OBJECT = 1 << 12;
    public static final int TRAIT = 1 << 13;
    /** This is a function with null params */
    public static final int NULL_ARGS = 1 << 14;
    public static final int FIELD = 1 << 15;
    public static final int PACKAGE = 1 << 16;
    public static final int JAVA = 1 << 17;
    protected String fqn;
    protected String name;
    protected String in;
    protected ScalaIndex index;
    protected String fileUrl;
    protected Document document;
    protected FileObject fileObject;
    protected int flags;
    protected String attributes;
    protected String signature;
    protected boolean smart;
    protected boolean inherited = true;
    protected ElementKind kind;
    private javax.lang.model.element.Element javaElement;
    private org.netbeans.api.java.source.CompilationInfo javaInfo;
    private Set<Modifier> modifiers;

    IndexedElement(String fqn, String name, String in, ScalaIndex index, String fileUrl, String attributes, int flags, ElementKind kind) {
        super(name, null, null, kind);
        this.fqn = fqn;
        this.name = name;
        this.in = in;
        this.index = index;
        this.fileUrl = fileUrl;
        this.attributes = attributes;
        this.flags = flags;
        this.kind = kind;
    }

    static IndexedElement create(String attributes, String fileUrl, String fqn, String name, String in, int attrIndex, ScalaIndex index, boolean createPackage) {
        int flags = IndexedElement.decodeFlags(attributes, attrIndex, 0);

        if (createPackage) {
            IndexedPackage pkg = new IndexedPackage(fqn, name, in, index, fileUrl, attributes, flags, ElementKind.PACKAGE);
            return pkg;
        }

        if ((flags & FUNCTION) != 0) {
            ElementKind kind = (flags & CONSTRUCTOR) != 0 ? ElementKind.CONSTRUCTOR : ElementKind.METHOD;
            IndexedFunction function = new IndexedFunction(fqn, name, in, index, fileUrl, attributes, flags, kind);
            return function;
        } else if ((flags & CLASS) != 0) {
            IndexedType type = new IndexedType(fqn, name, in, index, fileUrl, attributes, flags, ElementKind.CLASS);
            return type;
        } else if ((flags & OBJECT) != 0) {
            IndexedType type = new IndexedType(fqn, name, in, index, fileUrl, attributes, flags, ElementKind.CLASS);
            return type;
        } else if ((flags & TRAIT) != 0) {
            IndexedType type = new IndexedType(fqn, name, in, index, fileUrl, attributes, flags, ElementKind.INTERFACE);
            return type;
        } else if ((flags & PACKAGE) != 0){
            return new IndexedPackage(fqn, name, in, index, fileUrl, attributes, flags, ElementKind.PACKAGE);
        } else {
            return new IndexedElement(fqn, name, in, index, fileUrl, attributes, flags, ElementKind.OTHER);
        }
    }

    static IndexedElement create(String name, String attributes, String fileUrl, ScalaIndex index, boolean createPackage) {
        String elementName = null;
        int nameEndIdx = attributes.indexOf(';');
        assert nameEndIdx != -1;
        elementName = attributes.substring(0, nameEndIdx);
        nameEndIdx++;

        String funcIn = null;
        int inEndIdx = attributes.indexOf(';', nameEndIdx);
        assert inEndIdx != -1;
        if (inEndIdx > nameEndIdx + 1) {
            funcIn = attributes.substring(nameEndIdx, inEndIdx);
        }
        inEndIdx++;

        int startCs = inEndIdx;
        inEndIdx = attributes.indexOf(';', startCs);
        assert inEndIdx != -1;
        if (inEndIdx > startCs) {
            // Compute the case sensitive name
            elementName = attributes.substring(startCs, inEndIdx);
        }
        inEndIdx++;

        String fqn = null; // Compute lazily

        int lastDot = elementName.lastIndexOf('.');
        if (name.length() < lastDot) {
            int nextDot = elementName.indexOf('.', name.length());
            if (nextDot != -1) {
                String pkg = elementName.substring(0, nextDot);
                IndexedPackage indexedElement = new IndexedPackage(null, pkg, fqn, index, fileUrl, attributes, IndexedElement.decodeFlags(attributes, inEndIdx, 0), ElementKind.PACKAGE);
                return indexedElement;
            }
        }

        IndexedElement indexedElement = IndexedElement.create(attributes, fileUrl, fqn, elementName, funcIn, inEndIdx, index, createPackage);

        return indexedElement;
    }

    static IndexedElement create(AstDef element, TokenHierarchy th, ScalaIndex index) {
        String in = element.getIn();
        String sName = element.getSimpleName().toString();
        StringBuilder base = new StringBuilder();
        base.append(sName.toLowerCase());
        base.append(';');
        if (in != null) {
            base.append(in);
        }
        base.append(';');
        base.append(sName);
        base.append(';');
        base.append(encodeAttributes(element, th));

        return create(element.getSimpleName().toString(), base.toString(), "", index, false);
    }

    public void setJavaInfo(javax.lang.model.element.Element javaElement, org.netbeans.api.java.source.CompilationInfo javaInfo) {
        assert isJava() : "Only IndexedElement for Java's element has javaElement";
        this.javaElement = javaElement;
        this.javaInfo = javaInfo;
    }

    public String getSignature() {
        if (signature == null) {
            StringBuilder sb = new StringBuilder();
            if (in != null) {
                sb.append(in);
                sb.append('.');
            }
            sb.append(name);
            signature = sb.toString();
        }

        return signature;
    }

    @Override
    public String toString() {
        return getSignature() + ":" + getFilenameUrl() + ";" + decodeFlags(flags);
    }

    public ScalaIndex getIndex() {
        return index;
    }

    public String getFqn() {
        if (fqn == null) {
            if (in != null && in.length() > 0) {
                fqn = in + "." + name;
            } else {
                fqn = name;
            }
        }
        return fqn;
    }

    @Override
    public Name getSimpleName() {
        return new AstName(name);
    }

    @Override
    public String getIn() {
        return in;
    }

    @Override
    public ElementKind getKind() {
        if (isJava()) {
            switch (javaElement.getKind()) {
                case PACKAGE:
                    return ElementKind.PACKAGE;
                case CONSTRUCTOR:
                    return ElementKind.CONSTRUCTOR;
                case METHOD:
                    return ElementKind.METHOD;
                case FIELD:
                    return ElementKind.FIELD;
                case CLASS:
                    return ElementKind.CLASS;
                case INTERFACE:
                    return ElementKind.INTERFACE;
                default:
                    return ElementKind.OTHER;
            }
        } else {
            if (kind != null) {
                return kind;
            }
            if (isConstructor()) {
                return ElementKind.CONSTRUCTOR;
            } else if (isFunction()) {
                return ElementKind.METHOD;
            } else {
                return ElementKind.FIELD;
            }
        }
    }

    @Override
    public Set<Modifier> getModifiers() {
        if (modifiers == null) {
            modifiers = new HashSet<Modifier>();

            if (isPrivate()) {
                modifiers.add(Modifier.PRIVATE);
            } else if (isProtected()) {
                modifiers.add(Modifier.PROTECTED);
            } else if (isPublic()) {
                modifiers.add(Modifier.PUBLIC);
            }

            if (isStatic()) {
                modifiers.add(Modifier.STATIC);
            }

            if (modifiers.isEmpty()) {
                modifiers = Collections.<Modifier>emptySet();
            }
        }

        return modifiers;
    }

    public String getFilenameUrl() {
        return fileUrl;
    }

    public Document getDocument() throws IOException {
        if (document == null) {
            FileObject fo = getFileObject();

            if (fo == null) {
                return null;
            }

            document = NbUtilities.getBaseDocument(fileObject, true);
        }

        return document;
    }

    public ParserFile getFile() {
        boolean platform = false; // XXX FIND OUT WHAT IT IS!

        return new DefaultParserFile(getFileObject(), null, platform);
    }

    public FileObject getFileObject() {
        if (fileObject != null) {
            return fileObject;
        }

        if (isJava()) {
            fileObject = JavaUtilities.getOriginFileObject(javaInfo, javaElement);
        } else if (fileUrl != null && fileUrl.length() > 0) {
            fileObject = ScalaIndex.getFileObject(fileUrl);
            if (fileObject == null) {
                // Don't try again
                fileUrl = null;
            }
//
//            // Prefer sdoc files for doc-only items
//            if (isDocOnly() && !fileUrl.endsWith(".sdoc")) { // NOI18N
//                // This is probably a builtin library reference; correct the URL
//                FileObject fo = JsIndexer.findScriptDocFor(fileUrl, fileObject);
//                if (fo != null) {
//                    fileObject = fo;
//                }
//            }
        }

        return fileObject;
    }

    int getOffset() {
        if (this instanceof IndexedPackage) {
            return -1;
        }
        int offset = 0;
        if (isJava()) {
            try {
                offset = JavaUtilities.getOffset(javaInfo, javaElement);
            } catch (IOException ex) {
            }
        } else {
            int OffsetIndex = getAttributeSection(NODE_INDEX);
            if (OffsetIndex != -1) {
                offset = IndexedElement.decodeFlags(attributes, OffsetIndex, -1);
            }
        }
        return offset;
    }

    @Override
    public int getPickOffset(TokenHierarchy th) {
        return getOffset();
    }   

    OffsetRange getDocRange() {
        int docOffsetIndex = getAttributeSection(DOC_START_INDEX);
        int docEndOffsetIndex = getAttributeSection(DOC_END_INDEX);
        if (docOffsetIndex != -1 && docEndOffsetIndex != -1) {
            int docOffset = decodeFlags(attributes, docOffsetIndex, -1);
            int docEndOffset = decodeFlags(attributes, docEndOffsetIndex, -1);
            return new OffsetRange(docOffset, docEndOffset);
        }
        return OffsetRange.NONE;
    }

    String getComment() {
        String comment = null;

        if (isJava()) {
            try {
                String docComment = JavaUtilities.getDocComment(javaInfo, javaElement);
                if (docComment != null) {
                    comment = "/**" + docComment + "*/";
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            OffsetRange range = getDocRange();
            if (range == OffsetRange.NONE) {
                return null;
            }
            try {
                BaseDocument doc = (BaseDocument) getDocument();
                if (doc != null && range.getEnd() < doc.getLength()) {
                    comment = doc.getText(range.getStart(), range.getLength());
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }

        return comment;
    }

    @Override
    public TypeRef asType() {
        if (getKind() == ElementKind.CLASS || getKind() == ElementKind.PACKAGE) {
            return null;
        }

        int typeIdx = getAttributeSection(TYPE_INDEX);
        int endIdx = attributes.indexOf(';', typeIdx);
        if (endIdx > typeIdx) {
            String typeAttribute = attributes.substring(typeIdx, endIdx);
            int[] posAndLevel = new int[]{0, 0};
            TypeRef typeName = decodeType(typeAttribute, posAndLevel, null);

            return typeName;
        }

        return null;
    }

    public void setSmart(boolean smart) {
        this.smart = smart;
    }

    public boolean isSmart() {
        return smart;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public boolean isInherited() {
        return inherited;
    }

    public IndexedElement findDocumentedSibling() {
        if (!isDocumented()) {
            String queryName = null;
            String queryType = getFqn();
            if (queryType.indexOf('.') == -1) {
                queryName = queryType;
                queryType = null;
            }
            Set<IndexedElement> elements = getIndex().getAllElements(queryName, queryType, NameKind.EXACT_NAME, ScalaIndex.ALL_SCOPE, null);
            for (IndexedElement e : elements) {
                if (e.isDocumented()) {
                    return e;
                }
            }
        }

        return null;
    }

    public IndexedElement findRealFileElement() {
        if (isDocOnly()) {
            String queryName = null;
            String queryType = getFqn();
            if (queryType.indexOf('.') == -1) {
                queryName = queryType;
                queryType = null;
            }
            Set<IndexedElement> elements = getIndex().getAllElements(queryName, queryType, NameKind.EXACT_NAME, ScalaIndex.ALL_SCOPE, null);
            for (IndexedElement e : elements) {
                if (!e.isDocOnly()) {
                    return e;
                }
            }
        }

        return null;
    }

    int getAttributeSection(int section) {
        assert section != 0; // Obtain directly, and logic below (+1) is wrong

        int attributeIndex = 0;
        for (int i = 0; i < section; i++) {
            attributeIndex = attributes.indexOf(';', attributeIndex + 1);
        }

        if (attributeIndex == -1) {
            assert false;
        }
        return attributeIndex + 1;
    }

    /** Return a string (suitable for persistence) encoding the given flags */
    public static String encodeFlags(int flags) {
        return Integer.toString(flags, 16);
    }

    /** Return flag corresponding to the given encoding chars */
    public static int decodeFlags(String s, int startIndex, int defaultValue) {
        int value = 0;
        for (int i = startIndex, n = s.length(); i < n; i++) {
            char c = s.charAt(i);
            if (c == ';') {
                if (i == startIndex) {
                    return defaultValue;
                }
                break;
            }

            value = value << 4;

            if (c > '9') {
                value += c - 'a' + 10;
            } else {
                value += c - '0';
            }
        }

        return value;
    }

    /** Return the flags corresponding to the given AST element */
    public static int computeFlags(AstDef element) {
        int flags = 0;

        if (element instanceof ClassTemplate) {
            flags = flags | CLASS;
        } else if (element instanceof ObjectTemplate) {
            flags = flags | OBJECT;
        } else if (element instanceof TraitTemplate) {
            flags = flags | TRAIT;
            flags = flags | STATIC;
        } else if (element instanceof Function) {
            Function fun = (Function) element;
            flags = flags | FUNCTION;
            if (fun.getParameters() == null) {
                flags = flags | NULL_ARGS;
            }
        }

        switch (element.getKind()) {
            case CONSTRUCTOR:
                flags = flags | CONSTRUCTOR;
                break;
            case FIELD:
                flags = flags | FIELD;
                break;
            default:
                break;
        }


        if (element.getModifiers().contains(Modifier.STATIC)) {
            flags = flags | STATIC;
        }

//        if (element.getModifiers().contains(Modifier.DEPRECATED)) {
//            flags = flags | DEPRECATED;
//        }

        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            flags = flags | PRIVATE;
        }

        if (element.getModifiers().contains(Modifier.PROTECTED)) {
            flags = flags | PROTECTED;
        }

        return flags;
    }

    /** Return the flags corresponding to the given Java element */
    public static int computeFlags(javax.lang.model.element.Element jelement) {
        int flags = 0 | IndexedElement.JAVA;

        switch (jelement.getKind()) {
            case PACKAGE:
                flags = flags | PACKAGE;
                break;
            case CLASS:
                flags = flags | CLASS;
                break;
            case INTERFACE:
                flags = flags | TRAIT;
                break;
            case ENUM:
                flags = flags | OBJECT;
                break;
            case CONSTRUCTOR:
                flags = flags | CONSTRUCTOR;
                flags = flags | FUNCTION;
                break;
            case METHOD:
                flags = flags | FUNCTION;
                break;
            case ENUM_CONSTANT:
            case FIELD:
                flags = flags | FIELD;
                break;
            default:
                break;
        }

        if (jelement.getModifiers().contains(javax.lang.model.element.Modifier.STATIC)) {
            flags = flags | STATIC;
        }

        if (jelement.getModifiers().contains(javax.lang.model.element.Modifier.PRIVATE)) {
            flags = flags | PRIVATE;
        }

        if (jelement.getModifiers().contains(javax.lang.model.element.Modifier.PROTECTED)) {
            flags = flags | PROTECTED;
        }

        return flags;
    }

    public static String encodeAttributes(AstDef element, TokenHierarchy th) {
        //Map<String,String> typeMap = element.getDocProps();

        // Look up compatibility
        int index = FLAG_INDEX;
        String compatibility = "";
//            if (file.getNameExt().startsWith("stub_")) { // NOI18N
//                int astOffset = element.getNode().getSourceStart();
//                int lexOffset = astOffset;
//                TranslatedSource source = pResult.getTranslatedSource();
//                if (source != null) {
//                    lexOffset = source.getLexicalOffset(astOffset);
//                }
//                try {
//                    String line = doc.getText(lexOffset,
//                            Utilities.getRowEnd(doc, lexOffset)-lexOffset);
//                    int compatIdx = line.indexOf("COMPAT="); // NOI18N
//                    if (compatIdx != -1) {
//                        compatIdx += "COMPAT=".length(); // NOI18N
//                        EnumSet<BrowserVersion> es = BrowserVersion.fromFlags(line.substring(compatIdx));
//                        compatibility = BrowserVersion.toCompactFlags(es);
//                    }
//                } catch (BadLocationException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//            }

        assert index == FLAG_INDEX;
        StringBuilder sb = new StringBuilder();

        int flags = computeFlags(element);
        // Add in info from documentation
//            if (typeMap != null) {
//                // Most flags are already handled by AstElement.getFlags()...
//                // Consider handling the rest too
//                if (typeMap.get("@ignore") != null) { // NOI18N
//                    flags = flags | IndexedElement.NODOC;
//                }
//            }
        OffsetRange docRange = ScalaLexUtilities.getDocumentationRange(element, th);
        if (docRange != OffsetRange.NONE) {
            flags = flags | DOCUMENTED;
        }
        sb.append(IndexedElement.encodeFlags(flags));

        // Parameters
        sb.append(';');
        index++;
        assert index == ARG_INDEX;
        if (element instanceof Function) {
            Function function = (Function) element;

            List<Var> params = function.getParameters();
            if (params != null) {
                int argIndex = 0;
                for (Var param : params) {
                    String paramName = param.getSimpleName().toString();
                    if (argIndex == 0 && "super".equals(paramName)) { // NOI18N
                        // Prototype inserts these as the first param to handle inheritance/super

                        argIndex++;
                        continue;
                    }
                    if (argIndex > 0) {
                        sb.append(',');
                    }
                    sb.append(paramName);
                    TypeRef paramType = param.asType();
                    if (paramType != null) {
                        String typeName = paramType.getSimpleName().toString();
                        if (typeName != null) {
                            sb.append(':');
                            sb.append(typeName);
                        }
                    }
                    argIndex++;
                }
            }
        }

        // Node offset
        sb.append(';');
        index++;
        assert index == NODE_INDEX;
        sb.append(IndexedElement.encodeFlags(element.getPickOffset(th)));

        // Documentation offset
        sb.append(';');
        index++;
        assert index == DOC_START_INDEX;
        if (docRange != OffsetRange.NONE) {
            sb.append(IndexedElement.encodeFlags(docRange.getStart()));
        }

        // Documentation end offset
        sb.append(';');
        index++;
        assert index == DOC_END_INDEX;
        if (docRange != OffsetRange.NONE) {
            sb.append(IndexedElement.encodeFlags(docRange.getEnd()));
        }

        // TypeParams
        sb.append(';');
        index++;
        assert index == TYPE_PARAMS_INDEX;
        if (element instanceof WithTypeParams) {
            encodeTypeParams(((WithTypeParams) element).getTypeParameters(), sb);
        }

        // Type
        sb.append(';');
        index++;
        assert index == TYPE_INDEX;
        TypeRef type = element.asType();
//            if (type == null) {
//                type = typeMap != null ? typeMap.get(JsCommentLexer.AT_RETURN) : null; // NOI18N
//            }
        if (type != null) {
            encodeType(type, sb);
        } else {
            // @Todo
        }
        sb.append(';');

        return sb.toString();
    }

    /**
     * We'll keep the sigunature as same as java's class file format for type paramters, also
     * @see org.netbeans.modules.scala.editing.JavaUtilities#getTypeName(TypeMirror, boolean, boolean)
     * @param type to be encoded
     * @param StringBuilder for attributes
     */
    private static void encodeType(TypeRef type, StringBuilder sb) {
        if (type.isResolved()) {
            sb.append(type.getQualifiedName());
        } else {
            sb.append(type.getSimpleName());
        }

        List<TypeRef> typeArgs = type.getTypeArgs();
        if (typeArgs.size() > 0) {
            sb.append("<");
            for (Iterator<TypeRef> itr = typeArgs.iterator(); itr.hasNext();) {
                TypeRef typeArg = itr.next();
                encodeType(typeArg, sb);
                if (itr.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append(">");
        }
    }

    /** @todo decode tuple type, function type etc */
    private TypeRef decodeType(String typeAttr, int[] posAndLevel, List<TypeRef> typeArgs) {
        PseudoTypeRef curr = new PseudoTypeRef();
        StringBuilder sb = new StringBuilder();
        while (posAndLevel[0] < typeAttr.length()) {
            char c = typeAttr.charAt(posAndLevel[0]);
            posAndLevel[0]++;
            if (c == '<') {
                posAndLevel[1]++;
                curr.setSimpleName(sb.toString());
                typeArgs = new ArrayList<TypeRef>();
                curr.setTypeArgs(typeArgs);

                TypeRef typeArg = decodeType(typeAttr, posAndLevel, typeArgs);
                typeArgs.add(typeArg);
            } else if (c == '>') {
                posAndLevel[1]--;
            } else if (c == ',') {
                TypeRef typeArg = decodeType(typeAttr, posAndLevel, typeArgs);
                if (typeArgs != null) {
                    typeArgs.add(typeArg);
                } else {
                    //System.out.println(typeAttr);
                }
            } else if (c == ' ') {
                // strip it
            } else {
                sb.append(c);
            }
        }

        if (curr.getSimpleName() == null) {
            curr.setSimpleName(sb);
        }

        return curr;
    }    
    
    private static void encodeTypeParams(List<? extends TypeParam> typeParams, StringBuilder sb) {
        if (!typeParams.isEmpty()) {
            sb.append("[");
            for (Iterator<? extends TypeParam> itr = typeParams.iterator(); itr.hasNext();) {
                TypeParam typeParam = itr.next();
                sb.append(typeParam.getSimpleName());
                if (typeParam.getVariant() != null) {
                    sb.append(typeParam.getVariant());
                }
                if (typeParam.getBound() != null) {
                    sb.append(typeParam.getBound());
                    encodeType(typeParam.getBoundType(), sb);
                }
                                
                encodeTypeParams(typeParam.getParams(), sb);
                if (itr.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append("]");
        }
    }
        
    public static String encodeAttributes(javax.lang.model.element.Element jelement) {
        OffsetRange docRange = OffsetRange.NONE;

        TypeMirror type = jelement.asType();
        //Map<String,String> typeMap = element.getDocProps();

        // Look up compatibility
        int index = FLAG_INDEX;
        String compatibility = "";
//            if (file.getNameExt().startsWith("stub_")) { // NOI18N
//                int astOffset = element.getNode().getSourceStart();
//                int lexOffset = astOffset;
//                TranslatedSource source = pResult.getTranslatedSource();
//                if (source != null) {
//                    lexOffset = source.getLexicalOffset(astOffset);
//                }
//                try {
//                    String line = doc.getText(lexOffset,
//                            Utilities.getRowEnd(doc, lexOffset)-lexOffset);
//                    int compatIdx = line.indexOf("COMPAT="); // NOI18N
//                    if (compatIdx != -1) {
//                        compatIdx += "COMPAT=".length(); // NOI18N
//                        EnumSet<BrowserVersion> es = BrowserVersion.fromFlags(line.substring(compatIdx));
//                        compatibility = BrowserVersion.toCompactFlags(es);
//                    }
//                } catch (BadLocationException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//            }

        assert index == FLAG_INDEX;
        StringBuilder sb = new StringBuilder();

        int flags = computeFlags(jelement);

        // Add in info from documentation
//            if (typeMap != null) {
//                // Most flags are already handled by AstElement.getFlags()...
//                // Consider handling the rest too
//                if (typeMap.get("@ignore") != null) { // NOI18N
//                    flags = flags | IndexedElement.NODOC;
//                }
//            }
        if (docRange != OffsetRange.NONE) {
            flags = flags | DOCUMENTED;
        }
        sb.append(IndexedElement.encodeFlags(flags));

        // Parameters
        sb.append(';');
        index++;
        assert index == ARG_INDEX;
        if (jelement instanceof ExecutableElement) {
            ExecutableElement func = (ExecutableElement) jelement;
            ExecutableType funcType = (ExecutableType) func.asType();

            int argIndex = 0;
            Iterator<? extends VariableElement> itr = func.getParameters().iterator();
            Iterator<? extends TypeMirror> tItr = funcType.getParameterTypes().iterator();
            while (itr.hasNext()) {
                String paramName = itr.next().getSimpleName().toString();
                if (argIndex == 0 && "super".equals(paramName)) {
                    // Prototype inserts these as the first param to handle inheritance/super
                    argIndex++;
                    continue;
                }
                if (argIndex > 0) {
                    sb.append(',');
                }
                sb.append(paramName);
                if (tItr.hasNext()) {
                    TypeMirror tm = tItr.next();
                    String paramType = JavaUtilities.getTypeName(tm, false, func.isVarArgs() && !tItr.hasNext()).toString();
                    sb.append(':');
                    sb.append(paramType);
                }
                argIndex++;
            }
            type = funcType.getReturnType();
        }


        // Node offset
        sb.append(';');
        index++;
        assert index == NODE_INDEX;
        int offset = 0; // will compute lazily
        sb.append(encodeFlags(offset));

        // Documentation offset
        sb.append(';');
        index++;
        assert index == DOC_START_INDEX;
        if (docRange != OffsetRange.NONE) {
            sb.append(IndexedElement.encodeFlags(docRange.getStart()));
        }

        // Documentation end offset
        sb.append(';');
        index++;
        assert index == DOC_END_INDEX;
        if (docRange != OffsetRange.NONE) {
            sb.append(IndexedElement.encodeFlags(docRange.getEnd()));
        }

        // Browser compatibility
        sb.append(';');
        index++;
        assert index == TYPE_PARAMS_INDEX;
        sb.append(compatibility);

        // Type
        sb.append(';');
        index++;
        assert index == TYPE_INDEX;
//            if (type == null) {
//                type = typeMap != null ? typeMap.get(JsCommentLexer.AT_RETURN) : null; // NOI18N
//            }
        if (type != null) {
            String typeName = JavaUtilities.getTypeName(type, true).toString();
            sb.append(typeName);
        }
        sb.append(';');

        return sb.toString();
    }

    public boolean isDocumented() {
        return (flags & DOCUMENTED) != 0;
    }

    public boolean isPublic() {
        return !isPrivate() && !isProtected();
    }

    public boolean isProtected() {
        return (flags & PROTECTED) != 0;
    }

    public boolean isPrivate() {
        return (flags & PRIVATE) != 0;
    }

    public boolean isFunction() {
        return (flags & FUNCTION) != 0;
    }

    public boolean isConstructor() {
        return (flags & CONSTRUCTOR) != 0;
    }

    public boolean isNullArgs() {
        return (flags & NULL_ARGS) != 0;
    }

    public boolean isField() {
        return (flags & FIELD) != 0;
    }

    public boolean isStatic() {
        return (flags & STATIC) != 0;
    }

    public boolean isNoDoc() {
        return (flags & NODOC) != 0;
    }

    public boolean isFinal() {
        return (flags & FINAL) != 0;
    }

    public boolean isDeprecated() {
        return (flags & DEPRECATED) != 0;
    }

    public boolean isDocOnly() {
        return (flags & DOC_ONLY) != 0;
    }

    public boolean isJava() {
        return (flags & JAVA) != 0;
    }

    public static boolean isTemplate(int flags) {
        return (flags & CLASS) != 0 ||
                (flags & TRAIT) != 0 ||
                (flags & OBJECT) != 0;
    }

    public static String decodeFlags(int flags) {
        StringBuilder sb = new StringBuilder();
        if ((flags & DOCUMENTED) != 0) {
            sb.append("|DOCUMENTED");
        }

        if ((flags & PRIVATE) != 0) {
            sb.append("|PRIVATE");
        }

        if ((flags & CONSTRUCTOR) != 0) {
            sb.append("|CONSTRUCTOR");
        } else if ((flags & FUNCTION) != 0) {
            sb.append("|FUNCTION");
        } else if ((flags & GLOBAL) != 0) {
            sb.append("|GLOBAL");
        } else {
            sb.append("|PROPERTY");
        }

        if ((flags & STATIC) != 0) {
            sb.append("|STATIC");
        }

        if ((flags & NODOC) != 0) {
            sb.append("|NODOC");
        }

        if ((flags & DEPRECATED) != 0) {
            sb.append("|DEPRECATED");
        }

        if ((flags & DOC_ONLY) != 0) {
            sb.append("|DOC_ONLY");
        }

        if ((flags & FINAL) != 0) {
            sb.append("|FINAL");
        }

        if (sb.length() > 0) {
            sb.append("|");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IndexedElement other = (IndexedElement) obj;
        if (!getSignature().equals(other.getSignature())) {
            return false;
        }
//        if (this.flags != other.flags) {
//            return false;
//        }
        if (!getKind().equals(other.getKind())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + getSignature().hashCode();
//        hash = 53 * hash + flags;
        hash = 53 * hash + getKind().hashCode();
        return hash;
    }

    public String getOrigin() {
        String filename = getFilenameUrl();
        if (filename != null) {
            int lastSlash = filename.lastIndexOf('/');
            if (lastSlash == -1) {
                return null;
            }
            lastSlash++;
            if (filename.startsWith("stub_core", lastSlash)) { // NOI18N

                return "Core JS";
            } else if (filename.startsWith("stub_", lastSlash)) { // NOI18N

                return "DOM";
            } else if (filename.startsWith("jquery", lastSlash)) { // NOI18N

                return "jQuery";
            } else if (filename.startsWith("dojo", lastSlash)) { // NOI18N

                return "dojo";
            } else if (filename.startsWith("yui", lastSlash)) { // NOI18N

                return "YUI";
            }
        // TODO: Map to sdocs somehow. Tricky because sometimes I get the source
        // element rather than the sdoc when doing equals
        //} else if (filename.endsWith("sdoc")) {
        }

        return null;
    }

    public static String getHtmlSignature(IndexedElement element) {
        StringBuilder sb = new StringBuilder();

        IndexedElement indexedElement = element;
        // Insert browser icons... TODO - consult flags etc.
        sb.append("<table width=\"100%\" border=\"0\"><tr>\n"); // NOI18N

        sb.append("<td>"); // NOI18N

        /** none indexedElement getIn() may cause none enclosingScope error */
        if (element.getIn() != null) {
            String in = element.getIn();
            if (in != null && in.length() > 0) {
                sb.append("<i>"); // NOI18N
                sb.append(in);
                sb.append("</i>"); // NOI18N

                if (indexedElement != null) {
                    String url = indexedElement.getFilenameUrl();
                    if (url != null) {
                        if (url.indexOf("jsstubs/stub_core_") != -1) { // NOI18N
                            sb.append(" (Core JavaScript)");
                        } else if (url.indexOf("jsstubs/stub_") != -1) { // NOI18N
                            sb.append(" (DOM)");
                        }
                    }
                }

                sb.append("<br>"); // NOI18N
            }
        }
        // TODO - share this between Navigator implementation and here...
        sb.append("<b>"); // NOI18N
        sb.append(element.getSimpleName());
        sb.append("</b>"); // NOI18N

        if (element instanceof IndexedFunction) {
            IndexedFunction function = (IndexedFunction) element;
            Collection<String> args = function.getArgs();

            if (!function.isNullArgs()) {
                sb.append("("); // NOI18N
                if ((args != null) && (args.size() > 0)) {

                    for (Iterator<String> it = args.iterator(); it.hasNext();) {
                        String ve = it.next();
                        int typeIndex = ve.indexOf(':');
                        if (typeIndex != -1) {
                            sb.append("<font color=\"#808080\">"); // NOI18N
                            for (int i = typeIndex + 1, n = ve.length(); i < n; i++) {
                                char c = ve.charAt(i);
                                if (c == '<') { // Handle types... Array<String> etc
                                    sb.append("&lt;");
                                } else if (c == '>') {
                                    sb.append("&gt;");
                                } else {
                                    sb.append(c);
                                }
                            }
                            //sb.append(ve, typeIndex+1, ve.length());
                            sb.append("</font>"); // NOI18N
                            sb.append(" ");
                            sb.append("<font color=\"#a06001\">"); // NOI18N
                            sb.append(ve, 0, typeIndex);
                            sb.append("</font>"); // NOI18N
                        } else {
                            sb.append("<font color=\"#a06001\">"); // NOI18N
                            sb.append(ve);
                            sb.append("</font>"); // NOI18N
                        }

                        if (it.hasNext()) {
                            sb.append(", "); // NOI18N
                        }
                    }

                }
                sb.append(")"); // NOI18N
            }

            TypeRef retType = function.asType();

            if (retType != null) {
                sb.append(" :").append(function.asType().toString());
            }
        }

        sb.append("</td>\n"); // NOI18N
        sb.append("</tr></table>"); // NOI18N

        if (indexedElement != null && indexedElement.getFilenameUrl() != null && indexedElement.getFilenameUrl().indexOf("jsstubs") == -1) {
            sb.append(NbBundle.getMessage(ScalaCodeCompletion.class, "FileLabel"));
            sb.append(" <tt>"); // NOI18N
            String file = indexedElement.getFilenameUrl();
            int baseIndex = file.lastIndexOf('/');
            if (baseIndex != -1) {
                file = file.substring(baseIndex + 1);
            }
            sb.append(file);
            sb.append("</tt><br>"); // NOI18N
        }

        return sb.toString();
    }
}
