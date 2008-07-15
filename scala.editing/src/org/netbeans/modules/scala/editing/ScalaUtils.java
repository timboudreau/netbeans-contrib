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
 * Contributor(s):
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.scala.editing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.classfile.ClassFile;
import org.netbeans.modules.gsf.api.CancellableTask;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.tmpls.Template;
import org.netbeans.napi.gsfret.source.ClasspathInfo;
import org.netbeans.napi.gsfret.source.CompilationController;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import scala.tools.nsc.symtab.Symbols.Symbol;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaUtils {

    private ScalaUtils() {
    }

    public static boolean isFortressFile(FileObject f) {
        return ScalaMimeResolver.MIME_TYPE.equals(f.getMIMEType());
    }

    public static boolean isOperator(String name) {
        // TODO - this must be rewritten for Ruby; see ECMA-262 section 7.7
        if (name.length() == 0) {
            return false;
        }
        // Pieced together from various sources (JsYaccLexer, DefaultJsParser, ...)
        switch (name.charAt(0)) {
            case '+':
                return name.equals("+") || name.equals("+@");
            case '-':
                return name.equals("-") || name.equals("-@");
            case '*':
                return name.equals("*") || name.equals("**");
            case '<':
                return name.equals("<") || name.equals("<<") || name.equals("<=") || name.equals("<=>");
            case '>':
                return name.equals(">") || name.equals(">>") || name.equals(">=");
            case '=':
                return name.equals("=") || name.equals("==") || name.equals("===") || name.equals("=~");
            case '!':
                return name.equals("!=") || name.equals("!~");
            case '&':
                return name.equals("&") || name.equals("&&");
            case '|':
                return name.equals("|") || name.equals("||");
            case '[':
                return name.equals("[]") || name.equals("[]=");
            case '%':
                return name.equals("%");
            case '/':
                return name.equals("/");
            case '~':
                return name.equals("~");
            case '^':
                return name.equals("^");
            case '`':
                return name.equals("`");
            default:
                return false;
        }
    }

    // There are lots of valid method names...   %, *, +, -, <=>, ...
    /**
     * Js identifiers should consist of [a-zA-Z0-9_]
     * http://www.headius.com/rubyspec/index.php/Variables
     * <p>
     * This method also accepts the field/global chars
     * since it's unlikely 
     */
    public static boolean isSafeIdentifierName(String name, int fromIndex) {
        int i = fromIndex;
        for (; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!(c == '$' || c == '@' || c == ':')) {
                break;
            }
        }
        for (; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c == '_') ||
                    (c >= 'A' && c <= 'Z') ||
                    (c >= '0' && c <= '9') ||
                    (c == '?') || (c == '=') || (c == '!'))) { // Method suffixes; only allowed on the last line

                if (isOperator(name)) {
                    return true;
                }

                return false;
            }
        }

        return true;
    }

    /** 
     * Return null if the given identifier name is valid, otherwise a localized
     * error message explaining the problem.
     */
    public static String getIdentifierWarning(String name, int fromIndex) {
        if (isSafeIdentifierName(name, fromIndex)) {
            return null;
        } else {
            return NbBundle.getMessage(ScalaUtils.class, "UnsafeIdentifierName");
        }
    }

    /** Similar to isValidFortressClassName, but allows a number of ::'s to join class names */
    public static boolean isValidFortressTraitName(String name) {
        if (name.trim().length() == 0) {
            return false;
        }

        String[] mods = name.split("::"); // NOI18N

        for (String mod : mods) {
            if (!isValidClassName(mod)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidClassName(String name) {
        if (isFortressKeyword(name)) {
            return false;
        }

        if (name.trim().length() == 0) {
            return false;
        }

        if (!Character.isUpperCase(name.charAt(0))) {
            return false;
        }

        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!isStrictIdentifierChar(c)) {
                return false;
            }

//            if (c == '!' || c == '=' || c == '?') {
//                // Not allowed in constant names
//                return false;
//            }

        }

        return true;
    }

    public static boolean isValidFortressLocalVarName(String name) {
        if (isFortressKeyword(name)) {
            return false;
        }

        if (name.trim().length() == 0) {
            return false;
        }

        if (Character.isUpperCase(name.charAt(0)) || Character.isWhitespace(name.charAt(0))) {
            return false;
        }

        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            return false;
        }

        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isJavaIdentifierPart(c)) {
                return false;
            }
            // Identifier char isn't really accurate - I can have a function named "[]" etc.
            // so just look for -obvious- mistakes
            if (Character.isWhitespace(name.charAt(i))) {
                return false;
            }

        }

        return true;
    }

    public static boolean isValidFortressFunctionName(String name) {
        if (isFortressKeyword(name)) {
            return false;
        }

        if (name.trim().length() == 0) {
            return false;
        }

        if (isOperator(name)) {
            return true;
        }

        if (Character.isUpperCase(name.charAt(0)) || Character.isWhitespace(name.charAt(0))) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!(Character.isLetterOrDigit(c) || c == '_')) {
                // !, = and ? can only be in the last position
                if (i == name.length() - 1 && ((c == '!') || (c == '=') || (c == '?'))) {
                    return true;
                }
                return false;
            }

        }

        return true;
    }

    public static boolean isValidFortressIdentifier(String name) {
        if (isFortressKeyword(name)) {
            return false;
        }

        if (name.trim().length() == 0) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            // Identifier char isn't really accurate - I can have a function named "[]" etc.
            // so just look for -obvios- mistakes
            if (Character.isWhitespace(name.charAt(i))) {
                return false;
            }

        // TODO - make this more accurate, like the method validifier
        }

        return true;
    }

    public static boolean isFortressKeyword(String name) {
        for (String s : FORTRESS_KEYWORDS) {
            if (s.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static String getLineCommentPrefix() {
        return "//"; // NOI18N

    }

    /** Includes things you'd want selected as a unit when double clicking in the editor */
    public static boolean isIdentifierChar(char c) {
        return Character.isJavaIdentifierPart(c) || (// Globals, fields and parameter prefixes (for blocks and symbols)
                c == '$') || (c == '@') || (c == '&') || (c == ':') || (// Function name suffixes
                c == '!') || (c == '?') || (c == '=');
    }

    /** Includes things you'd want selected as a unit when double clicking in the editor */
    public static boolean isStrictIdentifierChar(char c) {
        return Character.isJavaIdentifierPart(c) ||
                (c == '!') || (c == '?') || (c == '=');
    }

    public static final String unicodedTypeName(String typeName) {
        String unicoded = STD_LIB_TYPE_UNICODE.get(typeName);
        return unicoded != null ? unicoded : typeName;
    }
    public static final Map<String, String> STD_LIB_TYPE_UNICODE = new HashMap<String, String>();


    static {
        STD_LIB_TYPE_UNICODE.put("ZZ8", "\u21248");
        STD_LIB_TYPE_UNICODE.put("ZZ16", "\u212416");
        STD_LIB_TYPE_UNICODE.put("ZZ32", "\u212432");
        STD_LIB_TYPE_UNICODE.put("ZZ64", "\u212464");
        STD_LIB_TYPE_UNICODE.put("ZZ128", "\u2124128");

        STD_LIB_TYPE_UNICODE.put("NN8", "\u21258");
        STD_LIB_TYPE_UNICODE.put("NN16", "\u212516");
        STD_LIB_TYPE_UNICODE.put("NN32", "\u212532");
        STD_LIB_TYPE_UNICODE.put("NN64", "\u212564");
        STD_LIB_TYPE_UNICODE.put("NN128", "\u2125128");

        STD_LIB_TYPE_UNICODE.put("QQ8", "\u212A8");
        STD_LIB_TYPE_UNICODE.put("QQ16", "\u212A16");
        STD_LIB_TYPE_UNICODE.put("QQ32", "\u212A32");
        STD_LIB_TYPE_UNICODE.put("QQ64", "\u212A64");
        STD_LIB_TYPE_UNICODE.put("QQ128", "\u212A128");

        STD_LIB_TYPE_UNICODE.put("CC16", "\u210216");
        STD_LIB_TYPE_UNICODE.put("CC32", "\u210232");
        STD_LIB_TYPE_UNICODE.put("CC64", "\u210264");
        STD_LIB_TYPE_UNICODE.put("CC128", "\u2102128");
        STD_LIB_TYPE_UNICODE.put("CC256", "\u2102256");

        STD_LIB_TYPE_UNICODE.put("RR32", "\u211D16");
        STD_LIB_TYPE_UNICODE.put("RR64", "\u211D32");

    }
    public static final String[] FORTRESS_KEYWORDS = new String[]{
        "BIG SI",
        "unit",
        "absorbs",
        "abstract",
        "also",
        "api",
        "as",
        "asif",
        "at",
        "atomic",
        "bool",
        "case",
        "catch",
        "coerces",
        "coercion",
        "component",
        "comprises",
        "default",
        "dim",
        "do",
        "elif",
        "else",
        "end",
        "ensures",
        "except",
        "excludes",
        "exit",
        "export",
        "extends",
        "finally",
        "fn",
        "for",
        "forbid",
        "from",
        "getter",
        "hidden",
        "ident",
        "if",
        "import",
        "in",
        "int",
        "invariant",
        "io",
        "juxtaposition",
        "label",
        "largest",
        "nat",
        "object",
        "of",
        "opr",
        "or",
        "private",
        "property",
        "provided",
        "requires",
        "self",
        "settable",
        "setter",
        "smallest",
        "spawn",
        "syntax",
        "test",
        "then",
        "throw",
        "throws",
        "trait",
        "transient",
        "try",
        "tryatomic",
        "type",
        "typecase",
        "unit",
        "value",
        "var",
        "where",
        "while",
        "widening",
        "widens",
        "with",
        "wrapped"
    };
    public static final String[] FORTRESS_RESERVED_WORDS = new String[]{
        // The operators on units, namely cubed, cubic, in, inverse, per, square, 
        // and squared, are also reserved words.
        "cubed",
        "cubic",
        "in",
        "inverse",
        "per",
        "square",
        // To avoid confusion, Fortress reserves the following tokens:
        // They do not have any special meanings but they cannot be used as identifiers.
        "goto",
        "idiom",
        "public",
        "pure",
        "reciprocal",
        "static"
    };

    public static int getOffset(scala.tools.nsc.util.Position pos) {
        if (pos.offset().isDefined()) {
            return ((Integer) pos.offset().get()).intValue();
        } else {
            return -1;
        }
    }

    public static boolean isRowWhite(String text, int offset) throws BadLocationException {
        try {
            // Search forwards
            for (int i = offset; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '\n') {
                    break;
                }
                if (!Character.isWhitespace(c)) {
                    return false;
                }
            }
            // Search backwards
            for (int i = offset - 1; i >= 0; i--) {
                char c = text.charAt(i);
                if (c == '\n') {
                    break;
                }
                if (!Character.isWhitespace(c)) {
                    return false;
                }
            }

            return true;
        } catch (Exception ex) {
            BadLocationException ble = new BadLocationException(offset + " out of " + text.length(),
                    offset);
            ble.initCause(ex);
            throw ble;
        }
    }

    public static boolean isRowEmpty(String text, int offset) throws BadLocationException {
        try {
            if (offset < text.length()) {
                char c = text.charAt(offset);
                if (!(c == '\n' || (c == '\r' && (offset == text.length() - 1 || text.charAt(offset + 1) == '\n')))) {
                    return false;
                }
            }

            if (!(offset == 0 || text.charAt(offset - 1) == '\n')) {
                // There's previous stuff on this line
                return false;
            }

            return true;
        } catch (Exception ex) {
            BadLocationException ble = new BadLocationException(offset + " out of " + text.length(),
                    offset);
            ble.initCause(ex);
            throw ble;
        }
    }

    public static int getRowLastNonWhite(String text, int offset) throws BadLocationException {
        try {
            // Find end of line
            int i = offset;
            for (; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '\n' || (c == '\r' && (i == text.length() - 1 || text.charAt(i + 1) == '\n'))) {
                    break;
                }
            }
            // Search backwards to find last nonspace char from offset
            for (i--; i >= 0; i--) {
                char c = text.charAt(i);
                if (c == '\n') {
                    return -1;
                }
                if (!Character.isWhitespace(c)) {
                    return i;
                }
            }

            return -1;
        } catch (Exception ex) {
            BadLocationException ble = new BadLocationException(offset + " out of " + text.length(),
                    offset);
            ble.initCause(ex);
            throw ble;
        }
    }

    public static int getRowFirstNonWhite(String text, int offset) throws BadLocationException {
        try {
            // Find start of line
            int i = offset - 1;
            if (i < text.length()) {
                for (; i >= 0; i--) {
                    char c = text.charAt(i);
                    if (c == '\n') {
                        break;
                    }
                }
                i++;
            }
            // Search forwards to find first nonspace char from offset
            for (; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '\n') {
                    return -1;
                }
                if (!Character.isWhitespace(c)) {
                    return i;
                }
            }

            return -1;
        } catch (Exception ex) {
            BadLocationException ble = new BadLocationException(offset + " out of " + text.length(),
                    offset);
            ble.initCause(ex);
            throw ble;
        }
    }

    public static int getRowStart(String text, int offset) throws BadLocationException {
        try {
            // Search backwards
            for (int i = offset - 1; i >= 0; i--) {
                char c = text.charAt(i);
                if (c == '\n') {
                    return i + 1;
                }
            }

            return 0;
        } catch (Exception ex) {
            BadLocationException ble = new BadLocationException(offset + " out of " + text.length(),
                    offset);
            ble.initCause(ex);
            throw ble;
        }
    }

    public static boolean endsWith(StringBuilder sb, String s) {
        int len = s.length();

        if (sb.length() < len) {
            return false;
        }

        for (int i = sb.length() - len, j = 0; j < len; i++, j++) {
            if (sb.charAt(i) != s.charAt(j)) {
                return false;
            }
        }

        return true;
    }

    public static String truncate(String s, int length) {
        assert length > 3; // Not for short strings

        if (s.length() <= length) {
            return s;
        } else {
            return s.substring(0, length - 3) + "...";
        }
    }
    private static Map<FileObject, Reference<Source>> scalaFileToSource =
            new WeakHashMap<FileObject, Reference<Source>>();
    private static Map<FileObject, Reference<CompilationInfo>> scalaFileToCompilationInfo =
            new WeakHashMap<FileObject, Reference<CompilationInfo>>();

    public static CompilationInfo getCompilationInfoForScalaFile(FileObject fo) {
        Reference<CompilationInfo> infoRef = scalaFileToCompilationInfo.get(fo);
        CompilationInfo info = infoRef != null ? infoRef.get() : null;

        if (info == null) {
            final CompilationInfo[] controllers = new CompilationInfo[1];

            Source source = getSourceForScalaFile(fo);
            try {
                source.runUserActionTask(new CancellableTask<CompilationController>() {

                    public void cancel() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void run(CompilationController controller) throws Exception {
                        controller.toPhase(Phase.ELEMENTS_RESOLVED);
                        controllers[0] = controller;
                    }
                }, true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            info = controllers[0];
            scalaFileToCompilationInfo.put(fo, new WeakReference<CompilationInfo>(info));
        }

        return info;
    }

    /** 
     * @Note: We cannot create javasource via JavaSource.forFileObject(fo) here, which
     * does not support virtual source yet (only ".java" and ".class" files 
     * are supported), but we can create js via JavaSource.create(cpInfo);
     */
    private static Source getSourceForScalaFile(FileObject fo) {
        Reference<Source> sourceRef = scalaFileToSource.get(fo);
        Source source = sourceRef != null ? sourceRef.get() : null;

        if (source == null) {
            ClasspathInfo cpInfo = ClasspathInfo.create(fo);
            source = Source.create(cpInfo, fo);
            scalaFileToSource.put(fo, new WeakReference<Source>(source));

        }

        return source;
    }

    public static List<Template> resolveTemplate(CompilationInfo info, String templateQName) {
        ScalaParserResult pResult = getParserResult(info);
        if (pResult != null) {
            AstScope rootScope = pResult.getRootScope();
            if (rootScope != null) {
                List<Template> templates = new ArrayList<Template>();
                collectTemplatesByName(rootScope, templateQName, templates);
                return templates;
            }
        } else {
            assert false : "Parse result is null : " + info.getFileObject().getName();
        }
        return Collections.<Template>emptyList();
    }

    private static void collectTemplatesByName(AstScope scope, String qName, List<Template> templates) {
        for (AstElement element : scope.getElements()) {
            if (element instanceof Template && ((Template) element).getQualifiedName().toString().equals(qName)) {
                templates.add((Template) element);
            }

        }

        for (AstScope _scope : scope.getScopes()) {
            collectTemplatesByName(_scope, qName, templates);
        }

    }

    public static String getDocComment(CompilationInfo info, AstElement element) {
        ScalaParserResult pResult = getParserResult(info);
        if (pResult == null) {
            return null;
        }

        BaseDocument doc = NbUtilities.getDocument(info.getFileObject(), true);
        if (doc == null) {
            return null;
        }

        TokenHierarchy<Document> th = pResult.getTokenHierarchy();

        doc.readLock(); // Read-lock due to token hierarchy use
        OffsetRange range = ScalaLexUtilities.getDocumentationRange(element, th);
        doc.readUnlock();

        if (range.getEnd() < doc.getLength()) {
            try {
                return doc.getText(range.getStart(), range.getLength());
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return null;
    }

    public static String getDocComment(BaseDocument doc, int symbolOffset) {
        TokenHierarchy th = TokenHierarchy.get(doc);
        if (th == null) {
            return null;
        }

        doc.readLock(); // Read-lock due to token hierarchy use
        OffsetRange range = ScalaLexUtilities.getDocCommentRangeBefore(th, symbolOffset);
        doc.readUnlock();

        if (range != OffsetRange.NONE && range.getEnd() < doc.getLength()) {
            try {
                return doc.getText(range.getStart(), range.getLength());
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return null;
    }

    public static int getOffset(CompilationInfo info, AstElement element) {
        ScalaParserResult pResult = getParserResult(info);
        if (pResult == null) {
            return -1;
        }

        TokenHierarchy<Document> th = pResult.getTokenHierarchy();
        return element.getPickOffset(th);
    }

    public static ScalaParserResult getParserResult(CompilationInfo info) {
        ParserResult result = info.getEmbeddedResult(ScalaMimeResolver.MIME_TYPE, 0);

        if (result == null) {
            return null;
        } else {
            return (ScalaParserResult) result;
        }
    }

    public static FileObject getFileObject(CompilationInfo info, Symbol symbol) {
        String qName = null;
        try {
             qName = symbol.enclClass().fullNameString().replace('.', File.separatorChar);
        } catch (java.lang.Error e) {
            // java.lang.Error: no-symbol does not have owner
            //        at scala.tools.nsc.symtab.Symbols$NoSymbol$.owner(Symbols.scala:1565)
            //        at scala.tools.nsc.symtab.Symbols$Symbol.fullNameString(Symbols.scala:1156)
            //        at scala.tools.nsc.symtab.Symbols$Symbol.fullNameString(Symbols.scala:1166)            
        }
        
        if (qName == null) {
            return null;
        }

        String pkgName = null;
        int lastSep = qName.lastIndexOf(File.separatorChar);
        if (lastSep > 0) {
            pkgName = qName.substring(0, lastSep);
        }

        String clzName = qName + ".class";

        try {
            org.netbeans.api.java.source.CompilationInfo javaInfo = JavaUtilities.getCompilationInfoForScalaFile(info.getFileObject());
            org.netbeans.api.java.source.ClasspathInfo cpInfo = javaInfo.getClasspathInfo();
            ClassPath cp = ClassPathSupport.createProxyClassPath(
                    new ClassPath[]{
                        cpInfo.getClassPath(org.netbeans.api.java.source.ClasspathInfo.PathKind.SOURCE),
                        cpInfo.getClassPath(org.netbeans.api.java.source.ClasspathInfo.PathKind.BOOT),
                        cpInfo.getClassPath(org.netbeans.api.java.source.ClasspathInfo.PathKind.COMPILE)
                    });

            String srcName = null;
            FileObject clzFo = cp.findResource(clzName);
            if (clzFo != null) {
                InputStream in = clzFo.getInputStream();
                try {
                    ClassFile cFile = new ClassFile(in, false);
                    if (cFile != null) {
                        srcName = cFile.getSourceFileName();
                    }
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }

            if (srcName != null) {
                if (pkgName != null) {
                    srcName = pkgName + File.separatorChar + srcName;
                }

                FileObject root = cp.findOwnerRoot(clzFo);
                assert root != null;

                SourceForBinaryQuery.Result result = SourceForBinaryQuery.findSourceRoots(root.getURL());
                FileObject[] srcRoots = result.getRoots();
                ClassPath srcCp = ClassPathSupport.createClassPath(srcRoots);

                return srcCp.findResource(srcName);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
