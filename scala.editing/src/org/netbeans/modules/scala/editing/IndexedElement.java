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

import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.Modifier;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.spi.DefaultParserFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.Function;
import org.netbeans.modules.scala.editing.nodes.TypeRef;
import org.netbeans.modules.scala.editing.nodes.Var;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * An element coming from the Lucene index - not tied to an AST.
 * To obtain an equivalent AST element, use AstUtilities.getForeignNode().
 * 
 * @author Tor Norbye
 * @author Caoyuan Deng
 */
public abstract class IndexedElement extends AstElement {

    protected static final int NAME_INDEX = 0;
    protected static final int IN_INDEX = 1;
    protected static final int CASE_SENSITIVE_INDEX = 2;
    protected static final int FLAG_INDEX = 3;
    protected static final int ARG_INDEX = 4;
    protected static final int NODE_INDEX = 5;
    protected static final int DOC_INDEX = 6;
    protected static final int BROWSER_INDEX = 7;
    protected static final int TYPE_INDEX = 8;
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
    public static final int JAVA = 1 << 14;
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

    IndexedElement(String fqn, String name, String in, ScalaIndex index, String fileUrl, String attributes, int flags, ElementKind kind) {
        super(null, null);
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
        int flags = IndexedElement.decode(attributes, attrIndex, 0);
        if (createPackage) {
            IndexedPackage func = new IndexedPackage(fqn, name, in, index, fileUrl, attributes, flags, ElementKind.PACKAGE);
            return func;
        }
        if ((flags & FUNCTION) != 0) {
            ElementKind kind = ((flags & CONSTRUCTOR) != 0) ? ElementKind.CONSTRUCTOR : ElementKind.METHOD;
            IndexedFunction func = new IndexedFunction(fqn, name, in, index, fileUrl, attributes, flags, kind);
            return func;
        } else if ((flags & GLOBAL) != 0) {
            ElementKind kind = Character.isUpperCase(name.charAt(0)) ? ElementKind.CLASS : ElementKind.GLOBAL;
            IndexedTemplate property = new IndexedTemplate(fqn, name, in, index, fileUrl, attributes, flags, kind);
            return property;
        } else {
            IndexedTemplate property = new IndexedTemplate(fqn, name, in, index, fileUrl, attributes, flags, ElementKind.CLASS);
            return property;
        }
    }

    static IndexedElement create(String name, String signature, String fileUrl, ScalaIndex index, boolean createPackage) {
        String elementName = null;
        int nameEndIdx = signature.indexOf(';');
        assert nameEndIdx != -1;
        elementName = signature.substring(0, nameEndIdx);
        nameEndIdx++;

        String funcIn = null;
        int inEndIdx = signature.indexOf(';', nameEndIdx);
        assert inEndIdx != -1;
        if (inEndIdx > nameEndIdx + 1) {
            funcIn = signature.substring(nameEndIdx, inEndIdx);
        }
        inEndIdx++;

        int startCs = inEndIdx;
        inEndIdx = signature.indexOf(';', startCs);
        assert inEndIdx != -1;
        if (inEndIdx > startCs) {
            // Compute the case sensitive name
            elementName = signature.substring(startCs, inEndIdx);
        }
        inEndIdx++;

        String fqn = null; // Compute lazily

        int lastDot = elementName.lastIndexOf('.');
        if (name.length() < lastDot) {
            int nextDot = elementName.indexOf('.', name.length());
            if (nextDot != -1) {
                String pkg = elementName.substring(0, nextDot);
                IndexedPackage indexedElement = new IndexedPackage(null, pkg, fqn, index, fileUrl, signature, IndexedElement.decode(signature, inEndIdx, 0), ElementKind.PACKAGE);
                return indexedElement;
            }
        }

        IndexedElement indexedElement = IndexedElement.create(signature, fileUrl, fqn, elementName, funcIn, inEndIdx, index, createPackage);

        return indexedElement;
    }

    static IndexedElement create(AstElement element, ScalaIndex index) {
            String in = element.getIn();
            String thename = element.getName();
            StringBuilder base = new StringBuilder();
            base.append(thename.toLowerCase());
            base.append(';');
            if (in != null) {
                base.append(in);
            }
            base.append(';');
            base.append(thename);
            base.append(';');
            base.append(IndexedElement.computeAttributes(element));

            return IndexedElement.create(element.getName(), base.toString(), "", index, false);       
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

    public String getName() {
        return name;
    }

    public String getIn() {
        return in;
    }

    public void setKind(ElementKind kind) {
        this.kind = kind;
    }

    public ElementKind getKind() {
        return kind;
    }

    public Set<Modifier> getModifiers() {
        /* @TODO */
        return Collections.emptySet();
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

        //document = NbUtilities.getBaseDocument(fileObject, true);
        }

        return document;
    }

    public ParserFile getFile() {
        boolean platform = false; // XXX FIND OUT WHAT IT IS!

        return new DefaultParserFile(getFileObject(), null, platform);
    }

    public FileObject getFileObject() {
        if ((fileObject == null) && (fileUrl != null)) {
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

    protected int getAttributeSection(int section) {
        assert section != 0; // Obtain directly, and logic below (+1) is wrong

        int attributeIndex = 0;
        for (int i = 0; i < section; i++) {
            attributeIndex = attributes.indexOf(';', attributeIndex + 1);
        }

        assert attributeIndex != -1;
        return attributeIndex + 1;
    }

    int getDocOffset() {
        int docOffsetIndex = getAttributeSection(DOC_INDEX);
        if (docOffsetIndex != -1) {
            int docOffset = IndexedElement.decode(attributes, docOffsetIndex, -1);
            return docOffset;
        }
        return -1;
    }

    protected List<String> getComments() {
        int docOffsetIndex = getAttributeSection(DOC_INDEX);
        if (docOffsetIndex != -1) {
            int docOffset = IndexedElement.decode(attributes, docOffsetIndex, -1);
            if (docOffset == -1) {
                return null;
            }
            try {
                BaseDocument doc = (BaseDocument) getDocument();
                if (doc == null) {
                    return null;
                }
                if (docOffset < doc.getLength()) {
                    //return LexUtilities.gatherDocumentation(null, doc, docOffset);
                    OffsetRange range = ScalaLexUtilities.getCommentBlock(doc, docOffset, false);
                    if (range != OffsetRange.NONE) {
                        String comment = doc.getText(range.getStart(), range.getLength());
                        String[] lines = comment.split("\n");
                        List<String> comments = new ArrayList<String>();
                        for (int i = 0, n = lines.length; i < n; i++) {
                            String line = lines[i];
                            line = line.trim();
                            if (i == n - 1 && line.endsWith("*/")) {
                                line = line.substring(0, line.length() - 2);
                            }
                            if (line.startsWith("/**")) {
                                comments.add(line.substring(3));
                            } else if (line.startsWith("/*")) {
                                comments.add(line.substring(2));
                            } else if (line.startsWith("//")) {
                                comments.add(line.substring(2));
                            } else if (line.startsWith("*")) {
                                comments.add(line.substring(1));
                            } else {
                                comments.add(line);
                            }
                        }
                        return comments;
                    }
                    return Collections.emptyList();
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
                return null;
            }
        }

        return null;
    }

    public String getTypeString() {
        if (getKind() == ElementKind.CLASS || getKind() == ElementKind.PACKAGE) {
            return null;
        }
        int typeIndex = getAttributeSection(TYPE_INDEX);
        int endIndex = attributes.indexOf(';', typeIndex);
        if (endIndex > typeIndex) {
            return attributes.substring(typeIndex, endIndex);
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

    /** Return a string (suitable for persistence) encoding the given flags */
    public static String encode(int flags) {
        return Integer.toString(flags, 16);
    }

    /** Return flag corresponding to the given encoding chars */
    public static int decode(String s, int startIndex, int defaultValue) {
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

    public static int computeFlags(AstElement element) {
        // Return the flags corresponding to the given AST element
        int value = 0;

        ElementKind k = element.getKind();
        if (k == ElementKind.CONSTRUCTOR) {
            value = value | CONSTRUCTOR;
        }
        if (k == ElementKind.METHOD || k == ElementKind.CONSTRUCTOR) {
            value = value | FUNCTION;
        } else if (k == ElementKind.GLOBAL) {
            value = value | GLOBAL;
        }
        if (element.getModifiers().contains(Modifier.STATIC)) {
            value = value | STATIC;
        }
        if (element.getModifiers().contains(Modifier.DEPRECATED)) {
            value = value | DEPRECATED;
        }
        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            value = value | PRIVATE;
        }

        return value;
    }

    public static int computeFlags(javax.lang.model.element.Element jelement) {
        // Return the flags corresponding to the given AST element
        int value = 0 | IndexedElement.JAVA;

        javax.lang.model.element.ElementKind k = jelement.getKind();
        if (k == javax.lang.model.element.ElementKind.CONSTRUCTOR) {
            value = value | IndexedElement.CONSTRUCTOR;
        }

        if (k == javax.lang.model.element.ElementKind.METHOD || k == javax.lang.model.element.ElementKind.CONSTRUCTOR) {
            value = value | IndexedElement.FUNCTION;
        }

        if (jelement.getModifiers().contains(javax.lang.model.element.Modifier.STATIC)) {
            value = value | IndexedElement.STATIC;
        }

        if (jelement.getModifiers().contains(javax.lang.model.element.Modifier.PRIVATE)) {
            value = value | IndexedElement.PRIVATE;
        }

        return value;
    }

    public static String computeAttributes(AstElement element) {
        OffsetRange docRange = getDocumentationOffset(element);
        int docOffset = -1;
        if (docRange != OffsetRange.NONE) {
            docOffset = docRange.getStart();
        }
        //Map<String,String> typeMap = element.getDocProps();

        // Look up compatibility
        int index = IndexedElement.FLAG_INDEX;
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

        assert index == IndexedElement.FLAG_INDEX;
        StringBuilder sb = new StringBuilder();
        int flags = IndexedElement.computeFlags(element);
        // Add in info from documentation
//            if (typeMap != null) {
//                // Most flags are already handled by AstElement.getFlags()...
//                // Consider handling the rest too
//                if (typeMap.get("@ignore") != null) { // NOI18N
//                    flags = flags | IndexedElement.NODOC;
//                }
//            }
        if (docOffset != -1) {
            flags = flags | IndexedElement.DOCUMENTED;
        }
        sb.append(IndexedElement.encode(flags));

        // Parameters
        sb.append(';');
        index++;
        assert index == IndexedElement.ARG_INDEX;
        if (element instanceof Function) {
            Function func = (Function) element;

            int argIndex = 0;
            for (Var param : func.getParams()) {
                String paramName = param.getName();
                if (argIndex == 0 && "super".equals(paramName)) { // NOI18N
                    // Prototype inserts these as the first param to handle inheritance/super

                    argIndex++;
                    continue;
                }
                if (argIndex > 0) {
                    sb.append(',');
                }
                sb.append(paramName);
                TypeRef paramType = param.getType();
                if (paramType != null) {
                    String typeName = paramType.getName();
                    if (typeName != null) {
                        sb.append(':');
                        sb.append(typeName);
                    }
                }
                argIndex++;
            }
        }

        // Node offset
        sb.append(';');
        index++;
        assert index == IndexedElement.NODE_INDEX;
        sb.append('0');
        //sb.append(IndexedElement.encode(element.getNode().getSourceStart()));

        // Documentation offset
        sb.append(';');
        index++;
        assert index == IndexedElement.DOC_INDEX;
        if (docOffset != -1) {
            sb.append(IndexedElement.encode(docOffset));
        }

        // Browser compatibility
        sb.append(';');
        index++;
        assert index == IndexedElement.BROWSER_INDEX;
        sb.append(compatibility);

        // Types
        sb.append(';');
        index++;
        assert index == IndexedElement.TYPE_INDEX;
        TypeRef type = element.getType();
//            if (type == null) {
//                type = typeMap != null ? typeMap.get(JsCommentLexer.AT_RETURN) : null; // NOI18N
//            }
        if (type != null) {
            sb.append(type.getName());
        }
        sb.append(';');

        return sb.toString();
    }

    public static String computeAttributes(javax.lang.model.element.Element jelement) {
        TypeMirror type = jelement.asType();
        OffsetRange docRange = OffsetRange.NONE;//getDocumentationOffset(element);
        int docOffset = -1;
        if (docRange != OffsetRange.NONE) {
            docOffset = docRange.getStart();
        }
        //Map<String,String> typeMap = element.getDocProps();

        // Look up compatibility
        int index = IndexedElement.FLAG_INDEX;
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

        assert index == IndexedElement.FLAG_INDEX;
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
        if (docOffset != -1) {
            flags = flags | IndexedElement.DOCUMENTED;
        }
        sb.append(IndexedElement.encode(flags));

        // Parameters
        sb.append(';');
        index++;
        assert index == IndexedElement.ARG_INDEX;
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
        assert index == IndexedElement.NODE_INDEX;
        sb.append('0');
        //sb.append(IndexedElement.encode(element.getNode().getSourceStart()));

        // Documentation offset
        sb.append(';');
        index++;
        assert index == IndexedElement.DOC_INDEX;


        if (docOffset != -1) {
            sb.append(IndexedElement.encode(docOffset));
        }

        // Browser compatibility
        sb.append(';');
        index++;
        assert index == IndexedElement.BROWSER_INDEX;
        sb.append(compatibility);

        // Types
        sb.append(';');
        index++;
        assert index == IndexedElement.TYPE_INDEX;                            
//            if (type == null) {
//                type = typeMap != null ? typeMap.get(JsCommentLexer.AT_RETURN) : null; // NOI18N
//            }
        if (type != null) {
            String typeName = JavaUtilities.getTypeName(type, false).toString();
            sb.append(typeName);
        }
        sb.append(';');

        return sb.toString();
    }

    private static OffsetRange getDocumentationOffset(AstElement element) {
        return OffsetRange.NONE; // @TODO
//            int astOffset = element.getEnclosingScope().getRange().getStart();
//            // XXX This is wrong; I should do a
//            //int lexOffset = LexUtilities.getLexerOffset(result, astOffset);
//            // but I don't have the CompilationInfo in the ParseResult handed to the indexer!!
//            int lexOffset = astOffset;
//            try {
//                if (lexOffset > doc.getLength()) {
//                    return OffsetRange.NONE;
//                }
//                lexOffset = Utilities.getRowStart(doc, lexOffset);
//            } catch (BadLocationException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//            OffsetRange range = ScalaLexUtilities.getCommentBlock(doc, lexOffset, true);
//            if (range != OffsetRange.NONE) {
//                return range;
//            } else {
//                return OffsetRange.NONE;
//            }

    }

    public boolean isDocumented() {
        return (flags & DOCUMENTED) != 0;
    }

    public boolean isPublic() {
        return (flags & PRIVATE) == 0;
    }

    public boolean isPrivate() {
        return (flags & PRIVATE) != 0;
    }

    public boolean isFunction() {
        return (flags & FUNCTION) != 0;
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

    public boolean isConstructor() {
        return (flags & CONSTRUCTOR) != 0;
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
}
