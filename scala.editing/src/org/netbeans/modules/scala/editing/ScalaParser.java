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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.scala.editing;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.csl.spi.DefaultError;
import org.netbeans.modules.csl.spi.GsfUtilities;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserFactory;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.netbeans.modules.scala.editing.ast.AstRootScope;
import org.netbeans.modules.scala.editing.ast.AstTreeVisitor;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;
import org.netbeans.modules.scala.editing.rats.LexerScala;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import scala.Option;
import scala.tools.nsc.CompilationUnits.CompilationUnit;
import scala.tools.nsc.Global;
import scala.tools.nsc.reporters.Reporter;
import scala.tools.nsc.util.BatchSourceFile;
import scala.tools.nsc.util.Position;
import scala.tools.nsc.util.SourceFile;

/**
 * Wrapper around com.sun.fortress.parser.Fortress to parse a buffer into an AST.
 *
 * 
 * @author Caoyuan Deng
 * @author Tor Norbye
 */
public class ScalaParser extends Parser {

    private ScalaParserResult lastResult;
    private static float[] profile = new float[]{0.0f, 0.0f};
    private Global global;

    private static String asString(CharSequence sequence) {
        if (sequence instanceof String) {
            return (String) sequence;
        } else {
            return sequence.toString();
        }
    }

    public ScalaParser() {
    }

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
        Context context = new Context(snapshot, event);
        lastResult = parseBuffer(context, Sanitize.NONE);
        lastResult.setErrors(context.errors());
    }

    public 
    @Override
    Result getResult(Task task) throws ParseException {
        assert lastResult != null : "getResult() called prior parse()"; //NOI18N
        return lastResult;
    }

    public 
    @Override
    void cancel() {
    }

    public 
    @Override
    void addChangeListener(ChangeListener changeListener) {
        // no-op, we don't support state changes
    }

    public 
    @Override
    void removeChangeListener(ChangeListener changeListener) {
        // no-op, we don't support state changes
    }

    /** Parse the given set of files, and notify the parse listener for each transition
     * (compilation results are attached to the events )
     */
//    public void parseFiles(Parser.Job job) {
//        ParseListener listener = job.listener;
//        SourceFileReader reader = job.reader;
//
//        for (ParserFile file : job.files) {
//            long start = System.currentTimeMillis();
//
//            ParseEvent beginEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, null);
//            listener.started(beginEvent);
//
//            ParserResult pResult = null;
//
//            try {
//                CharSequence buffer = reader.read(file);
//                String source = asString(buffer);
//                int caretOffset = reader.getCaretOffset(file);
//                if (caretOffset != -1 && job.translatedSource != null) {
//                    caretOffset = job.translatedSource.getAstOffset(caretOffset);
//                }
//                Context context = new Context(file, listener, source, caretOffset, job.translatedSource);
//                pResult = parseBuffer(context, Sanitize.NONE);
//            } catch (IOException ioe) {
//                listener.exception(ioe);
//                pResult = createParserResult(file, null, null, TokenHierarchy.create("", ScalaTokenId.language()), Collections.<DefaultError>emptyList());
//            }
//
//            ParseEvent doneEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, pResult);
//            listener.finished(doneEvent);
//
//            long time = System.currentTimeMillis() - start;
//            profile[0] += time / 1000.0f;
//            profile[1] += 1.0f;
//        //System.out.println("Parsing time: " + time / 1000.0f + "s");
//        //System.out.println("Average parsing time: " + profile[0] / profile[1] + "s");
//        }
//    }
    private static final class Factory extends ParserFactory {

        @Override
        public Parser createParser(Collection<Snapshot> snapshots) {
            return new ScalaParser();
        }
    } // End of Factory class

    /**
     * Try cleaning up the source buffer around the current offset to increase
     * likelihood of parse success. Initially this method had a lot of
     * logic to determine whether a parse was likely to fail (e.g. invoking
     * the isEndMissing method from bracket completion etc.).
     * However, I am now trying a parse with the real source first, and then
     * only if that fails do I try parsing with sanitized source. Therefore,
     * this method has to be less conservative in ripping out code since it
     * will only be used when the regular source is failing.
     * 
     * @todo Automatically close current statement by inserting ";"
     * @todo Handle sanitizing "new ^" from parse errors
     * @todo Replace "end" insertion fix with "}" insertion
     */
    private boolean sanitizeSource(Context context, Sanitize sanitizing) {

        if (sanitizing == Sanitize.MISSING_END) {
            context.sanitizedSource = context.source + "end";
            int start = context.source.length();
            context.sanitizedRange = new OffsetRange(start, start + 4);
            context.sanitizedContents = "";
            return true;
        }

        int offset = context.caretOffset;

        // Let caretOffset represent the offset of the portion of the buffer we'll be operating on
        if ((sanitizing == Sanitize.ERROR_DOT) || (sanitizing == Sanitize.ERROR_LINE)) {
            offset = context.errorOffset;
        }

        // Don't attempt cleaning up the source if we don't have the buffer position we need
        if (offset == -1) {
            return false;
        }

        // The user might be editing around the given caretOffset.
        // See if it looks modified
        // Insert an end statement? Insert a } marker?
        String source = context.source;
        if (offset > source.length()) {
            return false;
        }

        try {
            // Sometimes the offset shows up on the next line
            if (ScalaUtils.isRowEmpty(source, offset) || ScalaUtils.isRowWhite(source, offset)) {
                offset = ScalaUtils.getRowStart(source, offset) - 1;
                if (offset < 0) {
                    offset = 0;
                }
            }

            if (!(ScalaUtils.isRowEmpty(source, offset) || ScalaUtils.isRowWhite(source, offset))) {
                if ((sanitizing == Sanitize.EDITED_LINE) || (sanitizing == Sanitize.ERROR_LINE)) {
                    // See if I should try to remove the current line, since it has text on it.
                    int lineEnd = ScalaUtils.getRowLastNonWhite(source, offset);

                    if (lineEnd != -1) {
                        StringBuilder sb = new StringBuilder(source.length());
                        int lineStart = ScalaUtils.getRowStart(source, offset);
                        int rest = lineStart + 1;

                        sb.append(source.substring(0, lineStart));
                        sb.append('#');

                        if (rest < source.length()) {
                            sb.append(source.substring(rest, source.length()));
                        }
                        assert sb.length() == source.length();

                        context.sanitizedRange = new OffsetRange(lineStart, lineEnd);
                        context.sanitizedSource = sb.toString();
                        context.sanitizedContents = source.substring(lineStart, lineEnd);
                        return true;
                    }
                } else {
                    assert sanitizing == Sanitize.ERROR_DOT || sanitizing == Sanitize.EDITED_DOT;
                    // Try nuking dots/colons from this line
                    // See if I should try to remove the current line, since it has text on it.
                    int lineStart = ScalaUtils.getRowStart(source, offset);
                    int lineEnd = offset - 1;
                    while (lineEnd >= lineStart && lineEnd < source.length()) {
                        if (!Character.isWhitespace(source.charAt(lineEnd))) {
                            break;
                        }
                        lineEnd--;
                    }
                    if (lineEnd > lineStart) {
                        StringBuilder sb = new StringBuilder(source.length());
                        String line = source.substring(lineStart, lineEnd + 1);
                        int removeChars = 0;
                        int removeEnd = lineEnd + 1;

                        if (line.endsWith(".") || line.endsWith("(")) { // NOI18N

                            removeChars = 1;
                        } else if (line.endsWith(",")) { // NOI18N                            removeChars = 1;

                            removeChars = 1;
                        } else if (line.endsWith(", ")) { // NOI18N

                            removeChars = 2;
                        } else if (line.endsWith(",)")) { // NOI18N
                            // Handle lone comma in parameter list - e.g.
                            // type "foo(a," -> you end up with "foo(a,|)" which doesn't parse - but
                            // the line ends with ")", not "," !
                            // Just remove the comma

                            removeChars = 1;
                            removeEnd--;
                        } else if (line.endsWith(", )")) { // NOI18N
                            // Just remove the comma

                            removeChars = 1;
                            removeEnd -= 2;
                        } else {
                            // Make sure the line doesn't end with one of the JavaScript keywords
                            // (new, do, etc) - we can't handle that!
                            for (String keyword : LexerScala.SCALA_KEYWORDS) { // reserved words are okay

                                if (line.endsWith(keyword)) {
                                    removeChars = 1;
                                    break;
                                }
                            }
                        }

                        if (removeChars == 0) {
                            return false;
                        }

                        int removeStart = removeEnd - removeChars;

                        sb.append(source.substring(0, removeStart));

                        for (int i = 0; i < removeChars; i++) {
                            sb.append(' ');
                        }

                        if (removeEnd < source.length()) {
                            sb.append(source.substring(removeEnd, source.length()));
                        }
                        assert sb.length() == source.length();

                        context.sanitizedRange = new OffsetRange(removeStart, removeEnd);
                        context.sanitizedSource = sb.toString();
                        context.sanitizedContents = source.substring(removeStart, removeEnd);
                        return true;
                    }
                }
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }

        return false;
    }

    @SuppressWarnings("fallthrough")
    private ScalaParserResult sanitize(final Context context, final Sanitize sanitizing) {

        switch (sanitizing) {
            case NEVER:
                return createParserResult(context);

            case NONE:

                // We've currently tried with no sanitization: try first level
                // of sanitization - removing dots/colons at the edited offset.
                // First try removing the dots or double colons around the failing position
                if (context.caretOffset != -1) {
                    return parseBuffer(context, Sanitize.EDITED_DOT);
                }

            // Fall through to try the next trick
            case EDITED_DOT:

                // We've tried editing the caret location - now try editing the error location
                // (Don't bother doing this if errorOffset==caretOffset since that would try the same
                // source as EDITED_DOT which has no better chance of succeeding...)
                if (context.errorOffset != -1 && context.errorOffset != context.caretOffset) {
                    return parseBuffer(context, Sanitize.ERROR_DOT);
                }

            // Fall through to try the next trick
            case ERROR_DOT:

                // We've tried removing dots - now try removing the whole line at the error position
                if (context.errorOffset != -1) {
                    return parseBuffer(context, Sanitize.ERROR_LINE);
                }

            // Fall through to try the next trick
            case ERROR_LINE:

                // Messing with the error line didn't work - we could try "around" the error line
                // but I'm not attempting that now.
                // Finally try removing the whole line around the user editing position
                // (which could be far from where the error is showing up - but if you're typing
                // say a new "def" statement in a class, this will show up as an error on a mismatched
                // "end" statement rather than here
                if (context.caretOffset != -1) {
                    return parseBuffer(context, Sanitize.EDITED_LINE);
                }

            // Fall through to try the next trick
            case EDITED_LINE:
                return parseBuffer(context, Sanitize.MISSING_END);

            // Fall through for default handling
            case MISSING_END:
            default:
                // We're out of tricks - just return the failed parse result
                return createParserResult(context);
        }
    }

//    protected ScalaParserResult parseBuffer_old(final Context context, final Sanitize sanitizing) {
//        boolean sanitizedSource = false;
//        String source = context.source;
//
//        if (!((sanitizing == Sanitize.NONE) || (sanitizing == Sanitize.NEVER))) {
//            boolean ok = sanitizeSource(context, sanitizing);
//
//            if (ok) {
//                assert context.sanitizedSource != null;
//                sanitizedSource = true;
//                source = context.sanitizedSource;
//            } else {
//                // Try next trick
//                return sanitize(context, sanitizing);
//            }
//        }
//
//        TokenHierarchy th = null;
//
//        BaseDocument doc = null;
//        /** If this file is under editing, always get th from incrementally lexed th via opened document */
//        JTextComponent target = EditorRegistry.lastFocusedComponent();
//        if (target != null) {
//            doc = (BaseDocument) target.getDocument();
//            if (doc != null) {
//                FileObject fileObject = NbEditorUtilities.getFileObject(doc);
//                if (fileObject == context.file.getFileObject()) {
//                    th = TokenHierarchy.get(doc);
//                }
//            }
//        }
//
//        if (th == null) {
//            th = TokenHierarchy.create(source, ScalaTokenId.language());
//        }
//
//        context.th = th;
//
//        final boolean ignoreErrors = sanitizedSource;
//
//        Reader in = new StringReader(source);
//        String fileName = context.file != null ? context.file.getNameExt() : "<current>";
//        ParserScala parser = new ParserScala(in, fileName);
//        context.parser = parser;
//
//        if (sanitizing == Sanitize.NONE) {
//            context.errorOffset = -1;
//        }
//
//        AstScope rootScope = null;
//        List<GNode> errors = null;
//        if (doc != null) {
//            // Read-lock due to Token hierarchy use
//            doc.readLock();
//        }
//        try {
//            ParseError error = null;
//            Result r = parser.pCompilationUnit(0);
//            if (r.hasValue()) {
//                SemanticValue v = (SemanticValue) r;
//                GNode node = (GNode) v.value;
//
//                AstNodeVisitor visitor = new AstNodeVisitor(node, th);
//                visitor.visit(node);
//                rootScope = visitor.getRootScope();
//
//                ScalaTypeInferencer inferencer = new ScalaTypeInferencer(rootScope, th);
//                inferencer.infer();
//
//                errors = visitor.getErrors();
//                for (GNode errorNode : errors) {
//                    String msg = errorNode.getString(0);
//                    Location loc = errorNode.getLocation();
//                    notifyError(context, "SYNTAX_ERROR", msg,
//                            loc.offset, loc.endOffset, sanitizing, Severity.ERROR, new Object[]{loc.offset, errorNode});
//                }
//            } else {
//                error = r.parseError();
//            }
//
//            if (error != null) {
//                if (!ignoreErrors) {
//                    int start = 0;
//                    if (error.index != -1) {
//                        start = error.index;
//                    }
//                    notifyError(context, "SYNTAX_ERROR", "Syntax error",
//                            start, start, sanitizing, Severity.ERROR, new Object[]{error.index, error});
//                }
//
//                System.err.println(error.msg);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            // An internal exception thrown by ParserScala, just catch it and notify
//            notifyError(context, "SYNTAX_ERROR", e.getMessage(),
//                    0, 0, sanitizing, Severity.ERROR, new Object[]{e});
//        } finally {
//            if (doc != null) {
//                doc.readUnlock();
//            }
//        }
//
//
//        if (rootScope != null) {
//            context.sanitized = sanitizing;
//            ScalaParserResult pResult = createParseResult(context.file, rootScope, null, context.th, null);
//            pResult.setSanitized(context.sanitized, context.sanitizedRange, context.sanitizedContents);
//            pResult.setSource(source);
//            return pResult;
//        } else {
//            return sanitize(context, sanitizing);
//        }
//    }
    protected ScalaParserResult parseBuffer(final Context context, final Sanitize sanitizing) {
        boolean sanitizedSource = false;
        String source = context.source;

        if (!(sanitizing == Sanitize.NONE || sanitizing == Sanitize.NEVER)) {
            boolean ok = sanitizeSource(context, sanitizing);

            if (ok) {
                assert context.sanitizedSource != null;
                sanitizedSource = true;
                source = context.sanitizedSource;
            } else {
                // Try next trick
                return sanitize(context, sanitizing);
            }
        }

        if (sanitizing == Sanitize.NONE) {
            context.errorOffset = -1;
        }

        TokenHierarchy th = context.snapshot().getTokenHierarchy();

        final boolean ignoreErrors = sanitizedSource;

        File file = context.fileObject() != null ? FileUtil.toFile(context.fileObject()) : null;
        // We should use absolutionPath here for real file, otherwise, symbol.sourcefile.path won't be abs path
        String filePath = file != null ? file.getAbsolutePath() : "<current>";

        AstRootScope rootScope = null;

        // Scala global parser
        Reporter reporter = new ErrorReporter(context, sanitizing);
        global = ScalaGlobal.getGlobal(context.fileObject());
        global.reporter_$eq(reporter);

        BatchSourceFile srcFile = new BatchSourceFile(filePath, source.toCharArray());
        try {
            CompilationUnit unit = ScalaGlobal.compileSource(global, srcFile);
            rootScope = new AstTreeVisitor(global, unit, th, srcFile).getRootScope();
        } catch (AssertionError ex) {
            // avoid scala nsc's assert error
            ScalaGlobal.reset();
        } catch (java.lang.Error ex) {
            // avoid scala nsc's exceptions
        } catch (IllegalArgumentException ex) {
            // An internal exception thrown by ParserScala, just catch it and notify
            notifyError(context, "SYNTAX_ERROR", ex.getMessage(),
                    0, 0, sanitizing, Severity.ERROR, new Object[]{ex});
        } catch (Exception ex) {
            ex.printStackTrace();
            // Scala's global throws too many exceptions
        }
        
        if (rootScope != null) {
            context.setRootScope(rootScope);
            context.sanitized = sanitizing;
            ScalaParserResult pResult = createParserResult(context);
            pResult.setSanitized(context.sanitized, context.sanitizedRange, context.sanitizedContents);
            return pResult;
        } else {
            // Don't do sanitize trying:
            //return sanitize(context, sanitizing);
            ScalaParserResult pResult = createParserResult(context);
            pResult.setSanitized(context.sanitized, context.sanitizedRange, context.sanitizedContents);
            return pResult;
        }
    }
    private static long version;

    private ScalaParserResult createParserResult(Context context) {
        if (!context.errors().isEmpty()) {
            FileObject fo = context.fileObject();
            if (fo != null) {
                try {
                    Set<URL> inError = Collections.singleton(fo.getURL());
//                        ErrorAnnotator eAnnot = ErrorAnnotator.getAnnotator();
//                        if (eAnnot != null) {
//                            eAnnot.updateInError(inError);
//                        }
                } catch (FileStateInvalidException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        return new ScalaParserResult(this, context.snapshot(), context.rootScope());
    }

    private Sanitize processObjectSymbolError(Context context, AstRootScope root) {
        List<Error> errors = context.errors();
        TokenHierarchy th = context.snapshot().getTokenHierarchy();
        if (errors.isEmpty() || th == null) {
            return Sanitize.NONE;
        }

        for (Error error : errors) {
            String msg = error.getDescription();
            if (msg.startsWith("identifier expected but")) {
                int start = error.getStartPosition();

                TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(th, start - 1);
                ts.move(start - 1);
                if (!ts.moveNext() && !ts.movePrevious()) {
                    continue;
                }

                Token token = ScalaLexUtilities.findPreviousNonWsNonComment(ts);
                if (token != null && token.id() == ScalaTokenId.Dot) {
                    if (context.caretOffset == token.offset(th) + 1) {
                        if (ts.movePrevious()) {
                            token = ScalaLexUtilities.findPreviousNonWsNonComment(ts);
                            if (token != null && token.id() == ScalaTokenId.Identifier) {
                                return Sanitize.EDITED_DOT;
                            }
                        }
                    }
                }
            }
        }

        return Sanitize.NONE;
    }

    private List<Integer> computeLinesOffset(String source) {
        int length = source.length();

        List<Integer> linesOffset = new ArrayList<Integer>(length / 25);
        linesOffset.add(0);

        int line = 0;
        for (int i = 0; i < length; i++) {
            if (source.charAt(i) == '\n') {
                // \r comes first so are not a problem...
                linesOffset.add(i);
                line++;
            }
        }

        return linesOffset;
    }

    protected void notifyError(Context context, String key, String msg,
            int start, int end, Sanitize sanitizing, Severity severity, Object params) {

        DefaultError error = new DefaultError(key, msg, msg, context.fileObject(), start, end, severity);
        if (params != null) {
            if (params instanceof Object[]) {
                error.setParameters((Object[]) params);
            } else {
                error.setParameters(new Object[]{params});
            }
        }

        context.notifyError(error);

        if (sanitizing == Sanitize.NONE) {
            context.errorOffset = end;
        }
    }

    public Global global() {
        return global;
    }

    /** Attempts to sanitize the input buffer */
    public static enum Sanitize {

        /** Only parse the current file accurately, don't try heuristics */
        NEVER,
        /** Perform no sanitization */
        NONE,
        /** Try to remove the trailing . at the caret line */
        EDITED_DOT,
        /** Try to remove the trailing . at the error position, or the prior
         * line, or the caret line */
        ERROR_DOT,
        /** Try to cut out the error line */
        ERROR_LINE,
        /** Try to cut out the current edited line, if known */
        EDITED_LINE,
        /** Attempt to add an "end" to the end of the buffer to make it compile */
        MISSING_END,
    }

    /** Parsing context */
    public static class Context {

        private FileObject fileObject;
        private int errorOffset;
        private String source;
        private String sanitizedSource;
        private OffsetRange sanitizedRange = OffsetRange.NONE;
        private String sanitizedContents;
        private int caretOffset;
        private Sanitize sanitized = Sanitize.NONE;
        private List<Error> errors;
        private Snapshot snapshot;
        private AstRootScope rootScope;

        public Context(Snapshot snapshot, SourceModificationEvent event) {
            this.snapshot = snapshot;
            this.source = ScalaParser.asString(snapshot.getText());
            this.fileObject = snapshot.getSource().getFileObject();
            this.caretOffset = GsfUtilities.getLastKnownCaretOffset(snapshot, event);
        }

        public Snapshot snapshot() {
            return snapshot;
        }

        public void setRootScope(AstRootScope rootScope) {
            this.rootScope = rootScope;
        }

        public AstRootScope rootScope() {
            return rootScope;
        }

        public FileObject fileObject() {
            return fileObject;
        }

        @Override
        public String toString() {
            return "ScalaParser.Context(" + fileObject.toString() + ")"; // NOI18N

        }

        public OffsetRange getSanitizedRange() {
            return sanitizedRange;
        }

        public Sanitize getSanitized() {
            return sanitized;
        }

        public String getSanitizedSource() {
            return sanitizedSource;
        }

        public int getErrorOffset() {
            return errorOffset;
        }

        protected void notifyError(Error error) {
            if (errors == null) {
                errors = new ArrayList<Error>();
            }
            errors.add(error);
        }
        
        public void cleanErrors() {
            if (errors != null) {
                errors.clear();
            }
        }

        public List<Error> errors() {
            return errors == null ? Collections.<Error>emptyList() : errors;
        }
    }

    private class ErrorReporter extends Reporter {

        private Context context;
        private Sanitize sanitizing;

        public ErrorReporter(Context context, Sanitize sanitizing) {
            this.context = context;
            this.sanitizing = sanitizing;
        }

        @Override
        public void info0(Position pos, String msg, Severity severity, boolean force) {
            boolean ignoreError = context.sanitizedSource != null;
            if (!ignoreError) {
                // It seems scalac's errors may contain those from other source files that are deep referred, try to filter them here
                Option source = pos.source();
                if (source.isDefined()) {
                    SourceFile sf = (SourceFile) source.get();
                    if (!context.fileObject().getPath().equals(sf.file().path())) {
                        return;
                    }
                }
                //System.out.println("Error in source: " + pos.source());
                int offset = ScalaUtils.getOffset(pos);
                org.netbeans.modules.csl.api.Severity sev = org.netbeans.modules.csl.api.Severity.ERROR;
                switch (severity.id()) {
                    case 0:
                        return;
                    case 1:
                        sev = org.netbeans.modules.csl.api.Severity.WARNING;
                        break;
                    case 2:
                        sev = org.netbeans.modules.csl.api.Severity.ERROR;
                        break;
                    default:
                        return;
                }

                notifyError(context, "SYNTAX_ERROR", msg,
                        offset, -1, sanitizing, sev, new Object[]{offset, msg});
            }
        }
    }
}
