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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.erlang.editor

import _root_.java.util.{ArrayList,Arrays,Collections,Iterator,List,Map,Set}
import javax.swing.text.{BadLocationException,Document,JTextComponent}
import org.netbeans.api.lexer.{Token,TokenHierarchy,TokenId,TokenSequence}
import org.netbeans.editor.{BaseDocument,Utilities}
import org.netbeans.modules.csl.api.{CodeCompletionContext,CodeCompletionHandler,CodeCompletionResult,CompletionProposal,ElementHandle,ElementKind,HtmlFormatter,ParameterInfo,OffsetRange}
import org.netbeans.modules.csl.api.CodeCompletionHandler.QueryType
import org.netbeans.modules.csl.spi.{DefaultCompletionResult,ParserResult}
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport

import org.netbeans.modules.erlang.editor.ast.{AstDfn,AstItem,AstRootScope,AstScope}
import org.netbeans.modules.erlang.editor.node.ErlSymbols._
import org.netbeans.modules.erlang.editor.rats.LexerErlang
import org.netbeans.modules.erlang.editor.lexer.{LexUtil,ErlangTokenId}

import org.openide.filesystems.FileObject
import org.openide.util.{Exceptions,NbBundle}


/**
 * Code completion handler for JavaScript
 *
 * @todo Do completion on node id's inside $() calls (prototype.js) and $$() calls for CSS rules.
 *   See http://www.sitepoint.com/article/painless-javascript-prototype
 * @todo Track logical classes and inheritance ("extend")
 * @todo Track global variables (these are vars which aren't local). Somehow cooperate work between
 *    semantic highlighter and structure analyzer. I need to only store a single instance of each
 *    global var in the index. The variable visitor should probably be part of the structure analyzer,
 *    since global variables also need to be tracked there. Another possibility is having the
 *    parser track variables - but that's trickier. Perhaps a second pass over the parse tree
 *    (where I set parent pointers) is where I can do this? I can even change node types to be
 *    more obvious...
 * @todo I should NOT include in queries functions that are known to be methods if you're not doing
 *    "unnown type" completion!
 * @todo Today's feature work:
 *    - this.-completion should do something useful
 *    - I need to model prototype inheritance, and then use it in code completion queries
 *    - Skip no-doc'ed methods
 *    - Improve type analysis:
 *        - known types (node, document, ...)
 *        - variable-name guessing (el, doc, etc ...)
 *        - return value tracking
 *    - Improve indexing:
 *        - store @-private, etc.
 *        - more efficient browser-compat flags
 *    - Fix case-sensitivity on index queries such that open type and other forms of completion
 *      work better!
 *  @todo Distinguish properties and globals and functions? Perhaps with attributes in the flags!
 *  @todo Display more information in parameter tooltips, such as type hints (perhaps do smart
 *    filtering Java-style?), and explanations for each parameter
 *  @todo Need preindexing support for unit tests - and separate files
 *
 * @author Tor Norbye
 * @author Caoyuan Deng
 */
class ErlangCodeCompletion extends CodeCompletionHandler {
    import ErlangCodeCompletion._
    protected class CompletionRequest {
        var completionResult:DefaultCompletionResult = _
        var th:TokenHierarchy[_] = _
        var info :ParserResult = _
        var node :AstItem = _
        var root :AstRootScope = _
        var anchor :Int = _
        var lexOffset :Int = _
        var astOffset :Int = _
        var doc :BaseDocument = _
        var prefix :String = _
        var index :ErlangIndex = _
        var kind  :QuerySupport.Kind = _
        var result :ErlangParserResult = _
        var queryType :QueryType = _
        var fileObject :FileObject = _
        //protected MaybeCall call;
        var fqn :String = _
    }

    private var caseSensitive:Boolean = false

    override
    def complete(context:CodeCompletionContext) :CodeCompletionResult = {
        this.caseSensitive = context.isCaseSensitive
        val pResult = context.getParserResult.asInstanceOf[ErlangParserResult]
        val lexOffset = context.getCaretOffset
        val prefix = context.getPrefix match {
            case null => ""
            case x => x
        }

        val kind = if (context.isPrefixMatch) QuerySupport.Kind.PREFIX else QuerySupport.Kind.EXACT
        val queryType = context.getQueryType

        val doc = LexUtil.document(pResult, true) match {
            case None => return CodeCompletionResult.NONE
            case Some(x) => x.asInstanceOf[BaseDocument]
        }

        val proposals = new ArrayList[CompletionProposal]
        val completionResult = new DefaultCompletionResult(proposals, false)

        // Read-lock due to Token hierarchy use
        doc.readLock
        try {
            val astOffset = LexUtil.astOffset(pResult, lexOffset)
            if (astOffset == -1) {
                return CodeCompletionResult.NONE
            }
            val root = pResult.rootScope match {
                case None => return CodeCompletionResult.NONE
                case Some(x) => x
            }
            val th = LexUtil.tokenHierarchy(pResult).get
            val fileObject = LexUtil.fileObject(pResult).get

            // Carry completion context around since this logic is split across lots of methods
            // and I don't want to pass dozens of parameters from method to method; just pass
            // a request context with supporting info needed by the various completion helpers i
            val request = new CompletionRequest
            request.completionResult = completionResult
            request.result = pResult
            request.lexOffset = lexOffset
            request.astOffset = astOffset
            request.index = ErlangIndex.get(pResult)
            request.doc = doc
            request.info = pResult
            request.prefix = prefix
            request.th = th
            request.kind = kind
            request.queryType = queryType
            request.fileObject = fileObject
            request.anchor = lexOffset - prefix.length

            val token = LexUtil.token(doc, lexOffset - 1) match {
                case None => return completionResult
                case Some(x) => x
            }

            val id = token.id match {
                case ErlangTokenId.LineComment =>
                    // TODO - Complete symbols in comments?
                    return completionResult
                case ErlangTokenId.StringLiteral =>
                    //completeStrings(proposals, request);
                    return completionResult
                case _ =>
            }
            val ts = LexUtil.tokenSequence(th, lexOffset - 1).get
            ts.move(lexOffset - 1)
            if (!ts.moveNext && !ts.movePrevious) {
                return completionResult
            }

            val closetToken = LexUtil.findPreviousNonWsNonComment(ts)

            if (root != null) {
                val sanitizedRange = pResult.sanitizedRange
                val offset = if (sanitizedRange != OffsetRange.NONE && sanitizedRange.containsInclusive(astOffset)) {
                    sanitizedRange.getStart
                } else astOffset

                var closest = root.findItemAt(th, offset - 1)
                var closestOffset = offset - 1
                while (closest == None && closestOffset > 0) {
                    closest = root.findItemAt(th, closestOffset)
                    closestOffset -= 1
                }

                request.root = root
                request.node = closest.get

                val call = Call(null, null, false)
                findCall(root, ts, th, call, 0)
                val prefixBak = request.prefix
                call match {
                    case Call(null, _, _) =>
                    case Call(base, _, false) =>
                        // it's not a call, but may be candicate for module name, try to get modules and go-on
                        completeModules(base, proposals, request)
                    case Call(base, select, true) =>
                        if (select != null) {
                            request.prefix = call.select.text.toString
                        } else {
                            request.prefix = ""
                        }
                        completeModuleFunctions(call.base, proposals, request)
                        // Since is after a ":", we won't added other proposals, just return now whatever
                        return completionResult
                }
                request.prefix = prefixBak
                addLocals(proposals, request)
            }

            completeKeywords(proposals, request)
        } finally {
            doc.readUnlock
        }

        completionResult
    }

    private def addLocals(proposals:List[CompletionProposal], request:CompletionRequest) :Unit = {
        val prefix = request.prefix
        val kind = request.kind
        val pResult = request.result

        val root = request.root
        val closestScope = root.closestScope(request.th, request.astOffset) match {
            case None => return
            case Some(x) => x
        }
        val localVars = closestScope.visibleDfns(ElementKind.VARIABLE)
        localVars ++= closestScope.visibleDfns(ElementKind.PARAMETER)
        localVars.filter{v => filterKind(kind, prefix, v.name)}.foreach{v =>
            proposals.add(new PlainProposal(v, request.anchor))
        }

        val localFuns = closestScope.visibleDfns(ElementKind.METHOD)
        localFuns.filter{f => filterKind(kind, prefix, f.name)}.foreach{f =>
            proposals.add(new FunctionProposal(f, request.anchor))
        }

        // Add in "arguments" local variable which is available to all functions
        //        String ARGUMENTS = "arguments"; // NOI18N
        //        if (startsWith(ARGUMENTS, prefix)) {
        //            // Make sure we're in a function before adding the arguments property
        //            for (Node n = node; n != null; n = n.getParentNode()) {
        //                if (n.getType() == org.mozilla.javascript.Token.FUNCTION) {
        //                    KeywordElement node = new KeywordElement(ARGUMENTS, ElementKind.VARIABLE);
        //                    proposals.add(new PlainItem(node, request));
        //                    break;
        //                }
        //            }
        //        }
    }

    private def completeKeywords(proposals:List[CompletionProposal], request:CompletionRequest) :Unit = {
        // No keywords possible in the RHS of a call (except for "this"?)
        //        if (request.call.getLhs() != null) {
        //            return;
        //        }

        val prefix = request.prefix
        val itr = LexerErlang.ERLANG_KEYWORDS.iterator
        while (itr.hasNext) {
            val keyword = itr.next
            if (startsWith(keyword, prefix)) {
                proposals.add(new KeywordProposal(keyword, null, request.anchor))
            }
        }
    }

    private def startsWith(theString:String, prefix:String) :Boolean = {
        if (prefix.length == 0) {
            true
        } else {
            if (caseSensitive) theString.startsWith(prefix)
            else theString.toLowerCase.startsWith(prefix.toLowerCase)
        }
    }

    private def filterKind(kind:QuerySupport.Kind, prefix:String, name:String) :Boolean = {
        kind == QuerySupport.Kind.EXACT && prefix.equals(name) ||
        kind != QuerySupport.Kind.EXACT && startsWith(name, prefix)
    }

    @throws(classOf[BadLocationException])
    private def completeComments(proposals:List[CompletionProposal], request:CompletionRequest) :Boolean = {
        var prefix = request.prefix

        val doc = request.doc
        val rowStart = Utilities.getRowFirstNonWhite(doc, request.lexOffset)
        if (rowStart == -1) {
            return false
        }
        val line = doc.getText(rowStart, Utilities.getRowEnd(doc, request.lexOffset) - rowStart)
        val delta = request.lexOffset - rowStart;

        var i = delta - 1
        var continue = true
        while (i >= 0 && continue) {
            val c = line.charAt(i)
            if (Character.isWhitespace(c) || (!Character.isLetterOrDigit(c) && c != '@' && c != '.' && c != '_')) {
                continue = false
            }
            i -= 1
        }
        i += 1
        prefix = line.substring(i, delta)
        request.anchor = rowStart + i

        // Regular expression matching.  {
        for (j <- 0 until DOC_WORDS.length) {
            val word = DOC_WORDS(j)
            if (startsWith(word, prefix)) {
                proposals.add(new KeywordProposal(word, null, request.anchor))
            }
        }

        true
    }

    //    private boolean completeStrings(List<CompletionProposal> proposals, CompletionRequest request) {
    //        String prefix = request.prefix;
    //
    //        // See if we're in prototype js functions, $() and $F(), and if so,
    //        // offer to complete the function ids
    //        TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getPositionedSequence(request.doc, request.lexOffset);
    //        assert ts != null; // or we wouldn't have been called in the first place
    //        //Token<? extends ScalaTokenId> stringToken = ts.token();
    //        int stringOffset = ts.offset();
    //
    //    tokenLoop:
    //        while (ts.movePrevious()) {
    //            Token<? extends ScalaTokenId> token = ts.token();
    //            TokenId id = token.id();
    //            if (id == ScalaTokenId.Identifier) {
    //                String text = token.text().toString();
    //
    //                if (text.startsWith("$") || text.equals("getElementById") ||  // NOI18N
    //                        text.startsWith("getElementsByTagName") || text.equals("getElementsByName") || // NOI18N
    //                        "addClass".equals(text) || "toggleClass".equals(text)) { // NOI18N
    //
    //                    // Compute a custom prefix
    //                    int lexOffset = request.lexOffset;
    //                    if (lexOffset > stringOffset) {
    //                        try {
    //                            prefix = request.doc.getText(stringOffset, lexOffset - stringOffset);
    //                        } catch (BadLocationException ex) {
    //                            Exceptions.printStackTrace(ex);
    //                        }
    //                    } else {
    //                        prefix = "";
    //                    }
    //                    // Update anchor
    //                    request.anchor = stringOffset;
    //
    //                    boolean jQuery = false;
    //                    if (text.equals("$")) {
    //                        for (String imp : request.result.getStructure().getImports()) {
    //                            if (imp.indexOf("jquery") != -1) { // NOI18N
    //                                jQuery = true;
    //                            }
    //                        }
    //                        if (!jQuery) {
    //                            jQuery = request.index.getType("jQuery") != null;
    //                        }
    //                    }
    //
    //                    if ("getElementById".equals(text) || (!jQuery && ("$".equals(text) || "$F".equals(text)))) { // NOI18N
    //                        addElementIds(proposals, request, prefix);
    //
    //                    } else if ("getElementsByName".equals(text)) { // NOI18N
    //                        addElementClasses(proposals, request, prefix);
    //                    } else if ("addClass".equals(text) || "toggleClass".equals(text)) { // NOI18N
    //                        // From jQuery
    //                        addElementClasses(proposals, request, prefix);
    //                    } else if (text.startsWith("getElementsByTagName")) { // NOI18N
    //                        addTagNames(proposals, request, prefix);
    //                    } else if ("$$".equals(text) || (jQuery && "$".equals(text) && jQuery)) { // NOI18N
    //                        // Selectors
    //                        // Determine whether we want to include elements or classes
    //                        // Classes after [ and .
    //
    //                        int showClasses = 1;
    //                        int showElements = 2;
    //                        int showIds = 3;
    //                        int showSpecial = 4;
    //                        int expect = showElements;
    //                        int i = prefix.length()-1;
    //                     findEnd:
    //                        for (; i >= 0; i--) {
    //                            char c = prefix.charAt(i);
    //                            switch (c) {
    //                            case '.':
    //                            case '[':
    //                                expect = showClasses;
    //                                break findEnd;
    //                            case '#':
    //                                expect = showIds;
    //                                break findEnd;
    //                            case ':':
    //                                expect = showSpecial;
    //                                if (i > 0 && prefix.charAt(i-1) == ':') {
    //                                    // Handle ::'s
    //                                    i--;
    //                                }
    //                                break findEnd;
    //                            case ' ':
    //                            case '/':
    //                            case '>':
    //                            case '+':
    //                            case '~':
    //                            case ',':
    //                                expect = showElements;
    //                                break findEnd;
    //                            default:
    //                                if (!Character.isLetter(c)) {
    //                                    expect = showElements;
    //                                    break findEnd;
    //                                }
    //                            }
    //                        }
    //                        if (i >= 0) {
    //                            prefix = prefix.substring(i+1);
    //                        }
    //                        // Update anchor
    //                        request.anchor = stringOffset+i+1;
    //
    //                        if (expect == showElements) {
    //                            addTagNames(proposals, request, prefix);
    //                        } else if (expect == showIds) {
    //                            addElementIds(proposals, request, prefix);
    //                        } else if (expect == showSpecial) {
    //                            // Regular expression matching.  {
    //                            for (int j = 0, n = CSS_WORDS.length; j < n; j += 2) {
    //                                String word = CSS_WORDS[j];
    //                                String desc = CSS_WORDS[j + 1];
    //                                if (word.startsWith(":") && prefix.length() == 0) {
    //                                    // Filter out the double words
    //                                    continue;
    //                                }
    //                                if (startsWith(word, prefix)) {
    //                                    if (word.startsWith(":")) { // NOI18N
    //                                        word = word.substring(1);
    //                                    }
    //                                    //KeywordItem item = new KeywordItem(word, desc, request);
    //                                    TagItem item = new TagItem(word, desc, request, ElementKind.RULE);
    //                                    proposals.add(item);
    //                                }
    //                            }
    //                        } else {
    //                            assert expect == showClasses;
    //                            addElementClasses(proposals, request, prefix);
    //                        }
    //                    }
    //                }
    //
    //                return true;
    //            } else if (id == ScalaTokenId.STRING_BEGIN) {
    //                stringOffset = ts.offset() + token.length();
    //            } else if (!(id == ScalaTokenId.Ws ||
    //                    id == ScalaTokenId.StringLiteral || id == ScalaTokenId.LParen)) {
    //                break tokenLoop;
    //            }
    //        }
    //
    //        for (int i = 0, n = STRING_ESCAPES.length; i < n; i += 2) {
    //            String word = STRING_ESCAPES[i];
    //            String desc = STRING_ESCAPES[i + 1];
    //
    //            if (startsWith(word, prefix)) {
    //                KeywordItem item = new KeywordItem(word, desc, request);
    //                proposals.add(item);
    //            }
    //        }
    //
    //        return true;
    //    }

    //    private void addElementClasses(List<CompletionProposal> proposals, CompletionRequest request, String prefix) {
    //        ParserResult result = request.info.getEmbeddedResult(JsUtils.HTML_MIME_TYPE, 0);
    //        if (result != null) {
    //            HtmlParserResult htmlResult = (HtmlParserResult)result;
    //            List<SyntaxElement> elementsList = htmlResult.elementsList();
    //            Set<String> classes = new HashSet<String>();
    //            for (SyntaxElement s : elementsList) {
    //                if (s.type() == SyntaxElement.TYPE_TAG) {
    //                    String node = s.text();
    //                    int classIdx = node.indexOf("class=\""); // NOI18N
    //                    if (classIdx != -1) {
    //                        int classIdxEnd = node.indexOf('"', classIdx+7);
    //                        if (classIdxEnd != -1 && classIdxEnd > classIdx+1) {
    //                            String clz = node.substring(classIdx+7, classIdxEnd);
    //                            classes.add(clz);
    //                        }
    //                    }
    //                }
    //            }
    //
    //            String filename = request.fileObject.getNameExt();
    //            for (String tag : classes) {
    //                if (startsWith(tag, prefix)) {
    //                    TagItem item = new TagItem(tag, filename, request, ElementKind.TAG);
    //                    proposals.add(item);
    //                }
    //            }
    //        }
    //    }
    //
    //    private void addTagNames(List<CompletionProposal> proposals, CompletionRequest request, String prefix) {
    //        ParserResult result = request.info.getEmbeddedResult(JsUtils.HTML_MIME_TYPE, 0);
    //        if (result != null) {
    //            HtmlParserResult htmlResult = (HtmlParserResult)result;
    //            List<SyntaxElement> elementsList = htmlResult.elementsList();
    //            Set<String> tagNames = new HashSet<String>();
    //            for (SyntaxElement s : elementsList) {
    //                if (s.type() == SyntaxElement.TYPE_TAG) {
    //                    String node = s.text();
    //                    int start = 1;
    //                    int end = node.indexOf(' ');
    //                    if (end == -1) {
    //                        end = node.length()-1;
    //                    }
    //                    String tag = node.substring(start, end);
    //                    tagNames.add(tag);
    //                }
    //            }
    //
    //            String filename = request.fileObject.getNameExt();
    //
    //            for (String tag : tagNames) {
    //                if (startsWith(tag, prefix)) {
    //                    TagItem item = new TagItem(tag, filename, request, ElementKind.TAG);
    //                    proposals.add(item);
    //                }
    //            }
    //        }
    //    }
    //    private void addElementIds(List<CompletionProposal> proposals, CompletionRequest request, String prefix) {
    //        ParserResult result = request.info.getEmbeddedResult(JsUtils.HTML_MIME_TYPE, 0);
    //        if (result != null) {
    //            HtmlParserResult htmlResult = (HtmlParserResult)result;
    //            Set<SyntaxElement.TagAttribute> elementIds = htmlResult.elementsIds();
    //            String filename = request.fileObject.getNameExt();
    //            for (SyntaxElement.TagAttribute tag : elementIds) {
    //                String elementId = tag.getValue();
    //                // Strip "'s surrounding value, if any
    //                if (elementId.length() > 2 && elementId.startsWith("\"") && // NOI18N
    //                        elementId.endsWith("\"")) { // NOI18N
    //                    elementId = elementId.substring(1, elementId.length()-1);
    //                }
    //
    //                if (startsWith(elementId, prefix)) {
    //                    TagItem item = new TagItem(elementId, filename, request, ElementKind.TAG);
    //                    proposals.add(item);
    //                }
    //            }
    //        }
    //    }
    
    /**
     * Compute an appropriate prefix to use for code completion.
     * In Strings, we want to return the -whole- string if you're in a
     * require-statement string, otherwise we want to return simply "" or the previous "\"
     * for quoted strings, and ditto for regular expressions.
     * For non-string contexts, just return null to let the default identifier-computation
     * kick in.
     */
    def  getPrefix(pResult:ParserResult, lexOffset:Int, upToOffset:Boolean) :String = {
        try {
            val doc = LexUtil.document(pResult, true) match {
                case None => return null
                case Some(x) => x
            }
            val th = LexUtil.tokenHierarchy(pResult) match {
                case None => return null
                case Some(x) => x
            }
            val ts = LexUtil.tokenSequence(th, lexOffset) match {
                case None => return null
                case Some(x) => x
            }

            doc.readLock // Read-lock due to token hierarchy use
            try {
                //            int requireStart = ScalaLexUtilities.getRequireStringOffset(lexOffset, th);
                //
                //            if (requireStart != -1) {
                //                // XXX todo - do upToOffset
                //                return doc.getText(requireStart, lexOffset - requireStart);
                //            }

                ts.move(lexOffset)
                if (!ts.moveNext && !ts.movePrevious) {
                    return null
                }

                if (ts.offset == lexOffset) {
                    // We're looking at the offset to the RIGHT of the caret
                    // and here I care about what's on the left
                    ts.movePrevious
                }

                val token = ts.token

                if (token != null) {
                    val id = token.id

                    id match {
                        case ErlangTokenId.StringLiteral if lexOffset > 0 =>
                            val prevChar = doc.getText(lexOffset - 1, 1).charAt(0)
                            if (prevChar == '\\') {
                                return "\\"
                            } else return ""
                        case _ =>
                    }
                    //
                    //                // We're within a String that has embedded Js. Drop into the
                    //                // embedded language and see if we're within a literal string there.
                    //                if (id == ScalaTokenId.EMBEDDED_RUBY) {
                    //                    ts = (TokenSequence)ts.embedded();
                    //                    assert ts != null;
                    //                    ts.move(lexOffset);
                    //
                    //                    if (!ts.moveNext() && !ts.movePrevious()) {
                    //                        return null;
                    //                    }
                    //
                    //                    token = ts.token();
                    //                    id = token.id();
                    //                }
                    //
                    //                String tokenText = token.text().toString();
                    //
                    //                if ((id == ScalaTokenId.STRING_BEGIN) || (id == ScalaTokenId.QUOTED_STRING_BEGIN) ||
                    //                        ((id == ScalaTokenId.ERROR) && tokenText.equals("%"))) {
                    //                    int currOffset = ts.offset();
                    //
                    //                    // Percent completion
                    //                    if ((currOffset == (lexOffset - 1)) && (tokenText.length() > 0) &&
                    //                            (tokenText.charAt(0) == '%')) {
                    //                        return "%";
                    //                    }
                    //                }
                    //            }
                    //
                    //            int doubleQuotedOffset = ScalaLexUtilities.getDoubleQuotedStringOffset(lexOffset, th);
                    //
                    //            if (doubleQuotedOffset != -1) {
                    //                // Tokenize the string and offer the current token portion as the text
                    //                if (doubleQuotedOffset == lexOffset) {
                    //                    return "";
                    //                } else if (doubleQuotedOffset < lexOffset) {
                    //                    String text = doc.getText(doubleQuotedOffset, lexOffset - doubleQuotedOffset);
                    //                    TokenHierarchy hi =
                    //                        TokenHierarchy.create(text, JsStringTokenId.languageDouble());
                    //
                    //                    TokenSequence seq = hi.tokenSequence();
                    //
                    //                    seq.move(lexOffset - doubleQuotedOffset);
                    //
                    //                    if (!seq.moveNext() && !seq.movePrevious()) {
                    //                        return "";
                    //                    }
                    //
                    //                    TokenId id = seq.token().id();
                    //                    String s = seq.token().text().toString();
                    //
                    //                    if ((id == JsStringTokenId.STRING_ESCAPE) ||
                    //                            (id == JsStringTokenId.STRING_INVALID)) {
                    //                        return s;
                    //                    } else if (s.startsWith("\\")) {
                    //                        return s;
                    //                    } else {
                    //                        return "";
                    //                    }
                    //                } else {
                    //                    // The String offset is greater than the caret position.
                    //                    // This means that we're inside the string-begin section,
                    //                    // for example here: %q|(
                    //                    // In this case, report no prefix
                    //                    return "";
                    //                }
                    //            }
                    //
                    //            int singleQuotedOffset = ScalaLexUtilities.getSingleQuotedStringOffset(lexOffset, th);
                    //
                    //            if (singleQuotedOffset != -1) {
                    //                if (singleQuotedOffset == lexOffset) {
                    //                    return "";
                    //                } else if (singleQuotedOffset < lexOffset) {
                    //                    String text = doc.getText(singleQuotedOffset, lexOffset - singleQuotedOffset);
                    //                    TokenHierarchy hi =
                    //                        TokenHierarchy.create(text, JsStringTokenId.languageSingle());
                    //
                    //                    TokenSequence seq = hi.tokenSequence();
                    //
                    //                    seq.move(lexOffset - singleQuotedOffset);
                    //
                    //                    if (!seq.moveNext() && !seq.movePrevious()) {
                    //                        return "";
                    //                    }
                    //
                    //                    TokenId id = seq.token().id();
                    //                    String s = seq.token().text().toString();
                    //
                    //                    if ((id == JsStringTokenId.STRING_ESCAPE) ||
                    //                            (id == JsStringTokenId.STRING_INVALID)) {
                    //                        return s;
                    //                    } else if (s.startsWith("\\")) {
                    //                        return s;
                    //                    } else {
                    //                        return "";
                    //                    }
                    //                } else {
                    //                    // The String offset is greater than the caret position.
                    //                    // This means that we're inside the string-begin section,
                    //                    // for example here: %q|(
                    //                    // In this case, report no prefix
                    //                    return "";
                    //                }
                    //            }
                    //
                    //            // Regular expression
                    //            int regexpOffset = ScalaLexUtilities.getRegexpOffset(lexOffset, th);
                    //
                    //            if ((regexpOffset != -1) && (regexpOffset <= lexOffset)) {
                    //                // This is not right... I need to actually parse the regexp
                    //                // (I should use my Regexp lexer tokens which will be embedded here)
                    //                // such that escaping sequences (/\\\\\/) will work right, or
                    //                // character classes (/[foo\]). In both cases the \ may not mean escape.
                    //                String tokenText = token.text().toString();
                    //                int index = lexOffset - ts.offset();
                    //
                    //                if ((index > 0) && (index <= tokenText.length()) &&
                    //                        (tokenText.charAt(index - 1) == '\\')) {
                    //                    return "\\";
                    //                } else {
                    //                    // No prefix for regexps unless it's \
                    //                    return "";
                    //                }
                    //
                    //                //return doc.getText(regexpOffset, offset-regexpOffset);
                    //            }
                }

                val lineBegin = Utilities.getRowStart(doc, lexOffset)
                if (lineBegin != -1) {
                    val lineEnd = Utilities.getRowEnd(doc, lexOffset)
                    val line = doc.getText(lineBegin, lineEnd - lineBegin)
                    val lineOffset = lexOffset - lineBegin
                    var start = lineOffset
                    if (lineOffset > 0) {
                        var i = lineOffset - 1
                        var continue = true
                        while (i >= 0 && continue) {
                            val c = line.charAt(i)
                            if (!Character.isJavaIdentifierPart(c)) { // @todo erlang id char
                                continue = false
                            } else {
                                start = i
                            }
                            i -= 1
                        }
                    }

                    // Find identifier end
                    var prefix = if (upToOffset) {
                        line.substring(start, lineOffset)
                    } else {
                        if (lineOffset == line.length) {
                            line.substring(start)
                        } else {
                            val n = line.length
                            var end = lineOffset
                            var j = lineOffset
                            var continue = true
                            while (j < n && continue) {
                                val d = line.charAt(j)
                                // Try to accept Foo::Bar as well
                                if (!Character.isJavaIdentifierPart(d)) {
                                    continue = false
                                } else {
                                    end = j + 1
                                }
                                j += 1
                            }
                            line.substring(start, end)
                        }
                    }

                    if (prefix.length > 0) {
                        if (prefix.endsWith("::")) {
                            return ""
                        }

                        if (prefix.endsWith(":") && prefix.length > 1) {
                            return null
                        }

                        // Strip out LHS if it's a qualified method, e.g.  Benchmark::measure -> measure
                        val q = prefix.lastIndexOf("::")

                        if (q != -1) {
                            prefix = prefix.substring(q + 2)
                        }

                        // The identifier chars identified by JsLanguage are a bit too permissive;
                        // they include things like "=", "!" and even "&" such that double-clicks will
                        // pick up the whole "token" the user is after. But "=" is only allowed at the
                        // end of identifiers for example.
                        if (prefix.length == 1) {
                            val c = prefix.charAt(0)
                            if (!(Character.isJavaIdentifierPart(c) || c == '@' || c == '$' || c == ':')) {
                                return null
                            }
                        } else {
                            var i = prefix.length() - 2
                            var continue = true
                            while (i >= 0 && continue) { // -2: the last position (-1) can legally be =, ! or ?
                                val c = prefix.charAt(i)
                                if (i == 0 && c == ':') {
                                    // : is okay at the begining of prefixes
                                } else if (!(Character.isJavaIdentifierPart(c) || c == '@' || c == '$')) {
                                    prefix = prefix.substring(i + 1)
                                    continue = false
                                }
                                i -= 1
                            }
                        }

                        return prefix
                    }
                }
            } finally {
                doc.readUnlock
            }
            // Else: normal identifier: just return null and let the machinery do the rest
        } catch {case ble:BadLocationException => Exceptions.printStackTrace(ble)}

        // Default behavior
        null
    }

    /** Determine if we're trying to complete the name of a method on another object rather
     * than an inherited or local one. These should list ALL known methods, unless of course
     * we know the type of the method we're operating on (such as strings or regexps),
     * or types inferred through data flow analysis
     *
     * @todo Look for self or this or super; these should be limited to inherited.
     */
    //    private boolean completeTemplateMembers(List<CompletionProposal> proposals, CompletionRequest request) {
    //
    //        ScalaIndex index = request.index;
    //        String prefix = request.prefix;
    //        int astOffset = request.astOffset;
    //        int lexOffset = request.lexOffset;
    //        AstScope root = request.root;
    //        TokenHierarchy<Document> th = request.th;
    //        BaseDocument doc = request.doc;
    //        NameKind kind = request.kind;
    //        FileObject fileObject = request.fileObject;
    //        AstNode closest = request.node;
    //        ScalaParserResult result = request.result;
    //        CompilationInfo info = request.info;
    //
    //        String fqn = request.fqn;
    //        MaybeCall call = request.call;
    //
    //        TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(th, lexOffset);
    //
    //        // Look in the token stream for constructs of the type
    //        //   foo.x^
    //        // or
    //        //   foo.^
    //        // and if found, add all methods
    //        // (no keywords etc. are possible matches)
    //        if ((index != null) && (ts != null)) {
    //            boolean skipPrivate = true;
    //
    //            if ((call == MaybeCall.LOCAL) || (call == MaybeCall.NONE)) {
    //                return false;
    //            }
    //
    //            // If we're not sure we're only looking for a method, don't abort after this
    //            boolean done = call.isMethodExpected();
    //
    ////            boolean skipInstanceMethods = call.isStatic();
    //
    //            Set<GsfElement> elements = Collections.emptySet();
    //
    //            String typeQName = call.getType();
    //            String lhs = call.getLhs();
    //
    //            if (typeQName == null) {
    //                if (closest != null) {
    //                    TypeMirror type = null;
    //                    if (closest instanceof FieldCall) {
    //                        // dog.tal|
    //                        type = closest.asType();
    //                    } else if (closest instanceof FunctionCall) {
    //                        // dog.talk().
    //                        type = closest.asType();
    //                    } else if (closest instanceof IdCall) {
    //                        // dog.|
    //                        type = closest.asType();
    //                    } else {
    //                        type = closest.asType();
    //                    }
    //
    //                    if (type != null) {
    //                        typeQName = Type.qualifiedNameOf(type);
    //                    }
    //                }
    //            //Node method = AstUtilities.findLocalScope(node, path);
    //            //if (method != null) {
    //            //    List<Node> nodes = new ArrayList<Node>();
    //            //    AstUtilities.addNodesByType(method, new int[] { org.mozilla.javascript.Token.MISSING_DOT }, nodes);
    //            //    if (nodes.size() > 0) {
    //            //        Node exprNode = nodes.get(0);
    //            //        JsTypeAnalyzer analyzer = new JsTypeAnalyzer(info, /*request.info.getParserResult(),*/ index, method, node, astOffset, lexOffset, doc, fileObject);
    //            //        type = analyzer.getType(exprNode.getParentNode());
    //            //    }
    //            //}
    //            }
    //
    //            if (typeQName == null && call.getPrevCallParenPos() != -1) {
    //                // It's some sort of call
    //                assert call.getType() == null;
    //                assert call.getLhs() == null;
    //
    //                // Try to figure out the call in question
    //                int callEndAstOffset = AstUtilities.getAstOffset(info, call.getPrevCallParenPos());
    //                if (callEndAstOffset != -1) {
    ////                    AstPath callPath = new AstPath(root, callEndAstOffset);
    ////                    Iterator<Node> it = callPath.leafToRoot();
    ////                    while (it.hasNext()) {
    ////                        Node callNode = it.next();
    ////                        if (callNode.getType() == org.mozilla.javascript.Token.FUNCTION) {
    ////                            break;
    ////                        } else if (callNode.getType() == org.mozilla.javascript.Token.CALL) {
    ////                            Node method = AstUtilities.findLocalScope(node, path);
    ////
    ////                            if (method != null) {
    ////                                JsTypeAnalyzer analyzer = new JsTypeAnalyzer(info, /*request.info.getParserResult(),*/ index, method, node, astOffset, lexOffset, doc, fileObject);
    ////                                type = analyzer.getType(callNode);
    ////                            }
    ////                            break;
    ////                        } else if (callNode.getType() == org.mozilla.javascript.Token.GETELEM) {
    ////                            Node method = AstUtilities.findLocalScope(node, path);
    ////
    ////                            if (method != null) {
    ////                                JsTypeAnalyzer analyzer = new JsTypeAnalyzer(info, /*request.info.getParserResult(),*/ index, method, node, astOffset, lexOffset, doc, fileObject);
    ////                                type = analyzer.getType(callNode);
    ////                            }
    ////                            break;
    ////                        }
    ////                    }
    //                }
    //            } else if (typeQName == null && lhs != null && closest != null) {
    ////                Node method = AstUtilities.findLocalScope(node, path);
    ////
    ////                if (method != null) {
    ////                    JsTypeAnalyzer analyzer = new JsTypeAnalyzer(info, /*request.info.getParserResult(),*/ index, method, node, astOffset, lexOffset, doc, fileObject);
    ////                    type = analyzer.getType(node);
    ////                }
    //            }
    //
    //            if ((typeQName == null) && (lhs != null) && (closest != null) && call.isSimpleIdentifier()) {
    ////                Node method = AstUtilities.findLocalScope(node, path);
    ////
    ////                if (method != null) {
    ////                    // TODO - if the lhs is "foo.bar." I need to split this
    ////                    // up and do it a bit more cleverly
    ////                    JsTypeAnalyzer analyzer = new JsTypeAnalyzer(info, /*request.info.getParserResult(),*/ index, method, node, astOffset, lexOffset, doc, fileObject);
    ////                    type = analyzer.getType(lhs);
    ////                }
    //            }
    //
    //            // I'm not doing any data flow analysis at this point, so
    //            // I can't do anything with a LHS like "foo.". Only actual types.
    //            if (typeQName != null && typeQName.length() > 0) {
    //                if ("this".equals(lhs)) {
    //                    typeQName = fqn;
    //                    skipPrivate = false;
    ////                } else if ("super".equals(lhs)) {
    ////                    skipPrivate = false;
    ////
    ////                    IndexedClass sc = index.getSuperclass(fqn);
    ////
    ////                    if (sc != null) {
    ////                        type = sc.getFqn();
    ////                    } else {
    ////                        ClassNode cls = AstUtilities.findClass(path);
    ////
    ////                        if (cls != null) {
    ////                            type = AstUtilities.getSuperclass(cls);
    ////                        }
    ////                    }
    ////
    ////                    if (type == null) {
    ////                        type = "Object"; // NOI18N
    ////                    }
    //                }
    //
    //                if (typeQName != null && typeQName.length() > 0) {
    //                    // Possibly a class on the left hand side: try searching with the class as a qualifier.
    //                    // Try with the LHS + current FQN recursively. E.g. if we're in
    //                    // Test::Unit when there's a call to Foo.x, we'll try
    //                    // Test::Unit::Foo, and Test::Foo
    //                    while (elements.size() == 0 && fqn != null && !fqn.equals(typeQName)) {
    //                        elements = index.getMembers(prefix, fqn + "." + typeQName, kind, ScalaIndex.ALL_SCOPE, result, false);
    //
    //                        int f = fqn.lastIndexOf("::");
    //
    //                        if (f == -1) {
    //                            break;
    //                        } else {
    //                            fqn = fqn.substring(0, f);
    //                        }
    //                    }
    //
    //                    // Add methods in the class (without an FQN)
    //                    Set<GsfElement> m = index.getMembers(prefix, typeQName, kind, ScalaIndex.ALL_SCOPE, result, false);
    //
    //                    if (m.size() > 0) {
    //                        elements = m;
    //                    }
    //                }
    //            } else if (lhs != null && lhs.length() > 0) {
    //                // No type but an LHS - perhaps it's a type?
    //                Set<GsfElement> m = index.getMembers(prefix, lhs, kind, ScalaIndex.ALL_SCOPE, result, false);
    //
    //                if (m.size() > 0) {
    //                    elements = m;
    //                }
    //            }
    //
    //            // Try just the method call (e.g. across all classes). This is ignoring the
    //            // left hand side because we can't resolve it.
    //            if ((elements.size() == 0) && (prefix.length() > 0 || typeQName == null)) {
    ////                if (prefix.length() == 0) {
    ////                    proposals.clear();
    ////                    proposals.add(new KeywordItem("", "Type more characters to see matches", request));
    ////                    return true;
    ////                } else {
    //                //elements = index.getAllNames(prefix, kind, ScalaIndex.ALL_SCOPE, result);
    ////                }
    //            }
    //
    //            for (GsfElement gsfElement : elements) {
    //                Element element = gsfElement.getElement();
    //                // Skip constructors - you don't want to call
    //                //   x.Foo !
    //                if (element.getKind() == ElementKind.CONSTRUCTOR) {
    //                    continue;
    //                }
    //
    //                // Don't include private or protected methods on other objects
    //                if (skipPrivate && element.getModifiers().contains(Modifier.PRIVATE)) {
    //                    continue;
    //                }
    //
    //
    //
    ////                // We can only call static methods
    ////                if (skipInstanceMethods && !method.isStatic()) {
    ////                    continue;
    ////                }
    //
    ////                if (element.isNoDoc()) {
    ////                    continue;
    ////                }
    //
    //                if (element instanceof ExecutableElement) {
    //                    FunctionItem item = new FunctionItem(gsfElement, request);
    //                    proposals.add(item);
    //                } else if (element instanceof VariableElement) {
    //                    PlainItem item = new PlainItem(gsfElement, request);
    //                    proposals.add(item);
    //                }
    //            }
    //
    //            return done;
    //        }
    //
    //        return false;
    //    }
    /** Determine if we're trying to complete the name for a "new" (in which case
     * we show available constructors.
     */
    //    private boolean completeNew(List<CompletionProposal> proposals, CompletionRequest request) {
    //        ScalaIndex index = request.index;
    //        String prefix = request.prefix;
    //        int lexOffset = request.lexOffset;
    //        TokenHierarchy<Document> th = request.th;
    //        NameKind kind = request.kind;
    //
    //        TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(th, lexOffset);
    //
    //        if ((index != null) && (ts != null)) {
    //            ts.move(lexOffset);
    //
    //            if (!ts.moveNext() && !ts.movePrevious()) {
    //                return false;
    //            }
    //
    //            if (ts.offset() == lexOffset) {
    //                // We're looking at the offset to the RIGHT of the caret
    //                // position, which could be whitespace, e.g.
    //                //  "def fo| " <-- looking at the whitespace
    //                ts.movePrevious();
    //            }
    //
    //            Token<? extends ScalaTokenId> token = ts.token();
    //
    //            if (token != null) {
    //                TokenId id = token.id();
    //
    //                // See if we're in the identifier - "foo" in "def foo"
    //                // I could also be a keyword in case the prefix happens to currently
    //                // match a keyword, such as "next"
    //                if ((id == ScalaTokenId.Identifier) || (id == ScalaTokenId.CONSTANT) || id.primaryCategory().equals("keyword")) {
    //                    if (!ts.movePrevious()) {
    //                        return false;
    //                    }
    //
    //                    token = ts.token();
    //                    id = token.id();
    //                }
    //
    //                // If we're not in the identifier we need to be in the whitespace after "def"
    //                if (id != ScalaTokenId.Ws && id != ScalaTokenId.Nl) {
    //                    // Do something about http://www.netbeans.org/issues/show_bug.cgi?id=100452 here
    //                    // In addition to checking for whitespace I should look for "Foo." here
    //                    return false;
    //                }
    //
    //                // There may be more than one whitespace; skip them
    //                while (ts.movePrevious()) {
    //                    token = ts.token();
    //
    //                    if (token.id() != ScalaTokenId.Ws) {
    //                        break;
    //                    }
    //                }
    //
    //                if (token.id() == ScalaTokenId.New) {
    //                    if (prefix.length() < 2) {
    //                        /** @todo return imported types */
    //                        return false;
    //                    }
    //
    //                    /**
    //                     * @Todo : we should implement completion for "new" in two phase:
    //                     * 1. get Type name
    //                     * 2. get constructors of this type when use pressed enter
    //                     */
    //                    Set<GsfElement> gsdElements = index.getDeclaredTypes(prefix, kind, ScalaIndex.ALL_SCOPE, request.result);
    //                    String lhs = request.call == null ? null : request.call.getLhs();
    //                    /**
    //                     if (lhs != null && lhs.length() > 0) {
    //                     Set<IndexedElement> m = index.getElements(prefix, lhs, kind, ScalaIndex.ALL_SCOPE, null, true);
    //                     if (m.size() > 0) {
    //                     if (gsdElements.size() == 0) {
    //                     gsdElements = new HashSet<GsfElement>();
    //                     }
    //                     for (IndexedElement f : m) {
    //                     if (f.getKind() == ElementKind.CONSTRUCTOR) {
    //                     gsdElements.add(f);
    //                     }
    //                     }
    //                     }
    //                     } else if (prefix.length() > 0) {
    //                     Set<IndexedElement> m = index.getElements(prefix, null, kind, ScalaIndex.ALL_SCOPE, null, true);
    //                     if (m.size() > 0) {
    //                     if (gsdElements.size() == 0) {
    //                     gsdElements = new HashSet<GsfElement>();
    //                     }
    //                     for (IndexedElement f : m) {
    //                     if (f.getKind() == ElementKind.CONSTRUCTOR) {
    //                     gsdElements.add(f);
    //                     }
    //                     }
    //                     }
    //                     } */
    //                    for (GsfElement gsfElement : gsdElements) {
    //                        // Hmmm, is this necessary? Filtering should happen in the getInheritedMEthods call
    //                        if (!gsfElement.getName().startsWith(prefix)) {
    //                            continue;
    //                        }
    //
    //
    //                        //                        // For def completion, skip local methods, only include superclass and included
    //                        //                        if ((fqn != null) && fqn.equals(method.getClz())) {
    //                        //                            continue;
    //                        //                        }
    //
    //                        // If a method is an "initialize" method I should do something special so that
    //                        // it shows up as a "constructor" (in a new() statement) but not as a directly
    //                        // callable initialize method (it should already be culled because it's private)
    //                        //                        ScalaCompletionItem item;
    //                        //                        if (gsfElement instanceof IndexedFunction) {
    //                        //                            item = new FunctionItem((IndexedFunction) gsfElement, request);
    //                        //                        } else {
    //                        //                            item = new PlainItem(request, gsfElement);
    //                        //                        }
    //                        ScalaCompletionItem item = new PlainItem(gsfElement, request);
    //                        // Exact matches
    //                        //                        item.setSmart(method.isSmart());
    //                        proposals.add(item);
    //                    }
    //
    //                    return true;
    //                    //                } else if (token.id() == ScalaTokenId.IDENTIFIER && "include".equals(token.text().toString())) {
    //                    //                    // Module completion
    //                    //                    Set<IndexedClass> classes = index.getClasses(prefix, kind, false, true, false);
    //                    //                    for (IndexedClass clz : classes) {
    //                    //                        if (clz.isNoDoc()) {
    //                    //                            continue;
    //                    //                        }
    //                    //
    //                    //                        ClassItem item = new ClassItem(clz, anchor, request);
    //                    //                        item.setSmart(true);
    //                    //                        proposals.add(item);
    //                    //                    }
    //                    //
    //                    //                    return true;
    //                }
    //            }
    //        }
    //
    //        return false;
    //    }


    def getAutoQuery(component:JTextComponent, typedText:String) :QueryType = {
        val c = typedText.charAt(0)

        // TODO - auto query on ' and " when you're in $() or $F()
        c match {
            case '\n' | '(' | '[' | '{' | ';' => return QueryType.STOP
            case _ if c != '.' => return QueryType.NONE
            case _ =>
        }

        val offset = component.getCaretPosition
        val doc = component.getDocument.asInstanceOf[BaseDocument]

        if (".".equals(typedText)) { // NOI18N
            // See if we're in Js context

            val ts = LexUtil.tokenSequence(doc, offset) match {
                case None => return QueryType.NONE
                case Some(x) => x
            }
            ts.move(offset)
            if (!ts.moveNext && !ts.movePrevious) {
                return QueryType.NONE
            }
            if (ts.offset == offset && !ts.movePrevious) {
                return QueryType.NONE
            }
            val token = ts.token
            val id = token.id

            //            // ".." is a range, not dot completion
            //            if (id == ScalaTokenId.RANGE) {
            //                return QueryType.NONE;
            //            }

            // TODO - handle embedded JavaScript
            id.primaryCategory match {
                case "comment" | "string" | "regexp" => return QueryType.NONE // NOI18N
                case _ => return QueryType.COMPLETION
            }
        }

        //        if (":".equals(typedText)) { // NOI18N
        //            // See if it was "::" and we're in ruby context
        //            int dot = component.getSelectionStart();
        //            try {
        //                if ((dot > 1 && component.getText(dot-2, 1).charAt(0) == ':') && // NOI18N
        //                        isJsContext(doc, dot-1)) {
        //                    return QueryType.COMPLETION;
        //                }
        //            } catch (BadLocationException ble) {
        //                Exceptions.printStackTrace(ble);
        //            }
        //        }
        //
        QueryType.NONE
    }

    def isErlangContext(doc:BaseDocument, offset:int) :Boolean = {
        val ts = LexUtil.tokenSequence(doc, offset) match {
            case None => return false
            case Some(x) => x
        }
        ts.move(offset)
        if (!ts.moveNext && !ts.movePrevious) {
            return true
        }

        ts.token.id.primaryCategory match {
            case "comment" | "string" | "regexp" => false // NOI18N
            case _ => true
        }
    }

    def resolveTemplateVariable(variable:String, info:ParserResult, caretOffset:Int, name:String, parameters:Map[_, _]) :String = {
        throw new UnsupportedOperationException("Not supported yet.")
    }

    def document(info:ParserResult, element:ElementHandle) :String = {
        val sigFormatter = new SignatureHtmlFormatter
        val comment = element match {
            case x:AstDfn => x.docComment
            case _ => null
        }

        val html = new StringBuilder

        //String htmlSignature = IndexedElement.getHtmlSignature((IndexedElement) element);
        if (comment == null) {
            html.append(sigFormatter).append("\n<hr>\n<i>").append(NbBundle.getMessage(classOf[ErlangCodeCompletion], "NoCommentFound")).append("</i>")
        } else {
            //val formatter = new ScalaCommentFormatter(comment);
            val name = element.getName;
            if (name != null && name.length > 0) {
                //formatter.setSeqName(name)
            }

            val fo = element.getFileObject
            if (fo != null) {
                html.append("<b>").append(fo.getNameExt).append("</b><br>")
            }

            html.append(sigFormatter).append("\n<hr>\n")//.append(formatter.toHtml)
        }
        html.toString
    }

    def getApplicableTemplates(info:ParserResult, selectionBegin:Int, selectionEnd:Int) :Set[String] = {
        Collections.emptySet[String]
    }

    def parameters(info:ParserResult, lexOffset:Int, proposal:CompletionProposal) :ParameterInfo = {
        val methodHolder = Array[ErlFunction](null)
        val paramIndexHolder = Array(0)
        val anchorOffsetHolder = Array(0)
        val astOffset = LexUtil.astOffset(info, lexOffset)
        if (!computeMethodCall(info, lexOffset, astOffset,
                               methodHolder, paramIndexHolder, anchorOffsetHolder, null)) {

            return ParameterInfo.NONE;
        }

        val method = methodHolder(0)
        if (method == null) {
            return ParameterInfo.NONE
        }
        val index = paramIndexHolder(0)
        val anchorOffset = anchorOffsetHolder(0)


        // TODO: Make sure the caret offset is inside the arguments portion
        // (parameter hints shouldn't work on the method call name itself
        // See if we can find the method corresponding to this call
        //        if (proposal != null) {
        //            Element node = proposal.getElement();
        //            if (node instanceof IndexedFunction) {
        //                method = ((IndexedFunction)node);
        //            }
        //        }
        val arity = method.arity
        val paramsInStr = if (arity == 0) {
            Collections.emptyList[String]
        } else {
            val ps = new ArrayList[String]
            for (i <- 0 until arity) {
                ps.add("a" + arity)
            }
            ps
        }

        if (paramsInStr.size > 0) {
            return new ParameterInfo(paramsInStr, index, anchorOffset)
        }

        return ParameterInfo.NONE
    }

    /** Compute the current method call at the given offset. Returns false if we're not in a method call.
     * The argument index is returned in parameterIndexHolder[0] and the method being
     * called in methodHolder[0].
     */
    def computeMethodCall(info:ParserResult, lexOffset:Int, _astOffset:Int,
                          methodHolder: Array[ErlFunction], parameterIndexHolder:Array[Int], anchorOffsetHolder:Array[Int],
                          alternativesHolder:Array[Set[ErlFunction]]) :Boolean = {
        try {
            var astOffset = _astOffset
            val pResult = info match {
                case x:ErlangParserResult => x
                case _ => return false
            }
            for (root <- pResult.rootScope;
                 doc <- LexUtil.document(pResult, true);
                 th <- LexUtil.tokenHierarchy(pResult);
                 ts <- LexUtil.tokenSequence(th, lexOffset)
            ) {
                var targetMethod :ErlFunction = null
                var index = -1

                // Account for input sanitation
                // TODO - also back up over whitespace, and if I hit the method
                // I'm parameter number 0
                val originalAstOffset = astOffset

                // Adjust offset to the left
                val newLexOffset = LexUtil.findSpaceBegin(doc, lexOffset)
                if (newLexOffset < lexOffset) {
                    astOffset -= (lexOffset - newLexOffset);
                }

                val range = pResult.sanitizedRange
                if (range != OffsetRange.NONE && range.containsInclusive(astOffset)) {
                    if (astOffset != range.getStart) {
                        astOffset = range.getStart - 1
                        if (astOffset < 0) {
                            astOffset = 0
                        }
                    }
                }

                ts.move(lexOffset);
                if (!ts.moveNext() && !ts.movePrevious()) {
                    return false
                }

                var closest = root.findItemAt(th, astOffset)
                var closestOffset = astOffset - 1
                while (closest == None && closestOffset > 0) {
                    closest = root.findItemAt(th, closestOffset)
                    closestOffset -= 1
                }

                //val call = findCallSymbol(visitor, ts, th, request, true)
                val call = closest.get

                val currentLineStart = Utilities.getRowStart(doc, lexOffset)
                if (callLineStart != -1 && currentLineStart == callLineStart) {
                    // We know the method call
                    targetMethod = callMethod;
                    if (targetMethod != null) {
                        // Somehow figure out the argument index
                        // Perhaps I can keep the node tree around and look in it
                        // (This is all trying to deal with temporarily broken
                        // or ambiguous calls.
                    }
                }
                // Compute the argument index

                var anchorOffset = -1;

                //            if (targetMethod != null) {
                //                Iterator<Node> it = path.leafToRoot();
                //                String name = targetMethod.getName();
                //                while (it.hasNext()) {
                //                    Node node = it.next();
                //                }
                //            }
                val haveSanitizedComma = false
                //            val haveSanitizedComma = pResult.sanitized == Sanitize.EDITED_DOT || pResult.getSanitized() == Sanitize.ERROR_DOT;
                //            if (haveSanitizedComma) {
                //                // We only care about removed commas since that
                //                // affects the parameter count
                //                if (pResult.getSanitizedContents().indexOf(',') == -1) {
                //                    haveSanitizedComma = false;
                //                }
                //            }

                if (call == null) {
                    // Find the call in around the caret. Beware of
                    // input sanitization which could have completely
                    // removed the current parameter (e.g. with just
                    // a comma, or something like ", @" or ", :")
                    // where we accidentally end up in the previous
                    // parameter.
                    //                ListIterator<Node> it = path.leafToRoot();
                    //             nodesearch:
                    //                while (it.hasNext()) {
                    //                    Node node = it.next();
                    //
                    //                    if (node.getType() == org.mozilla.javascript.Token.CALL) {
                    //                        call = node;
                    //                        index = AstUtilities.findArgumentIndex(call, astOffset, path);
                    //                        break;
                    //                    }
                    //
                    //                }
                }

                if (index != -1 && haveSanitizedComma && call != null) {
                    //                if (call.nodeId == NodeTypes.FCALLNODE) {
                    //                    an = ((FCallNode)call).getArgsNode();
                    //                } else if (call.nodeId == NodeTypes.CALLNODE) {
                    //                    an = ((CallNode)call).getArgsNode();
                    //                }
                    //                if (an != null && index < an.childNodes().size() &&
                    //                        ((Node)an.childNodes().get(index)).nodeId == NodeTypes.HASHNODE) {
                    //                    // We should stay within the hashnode, so counteract the
                    //                    // index++ which follows this if-block
                    //                    index--;
                    //                }

                    // Adjust the index to account for our removed
                    // comma
                    index += 1
                }

                if (call == null || index == -1) {
                    callLineStart = -1
                    callMethod = null
                    return false
                } else if (targetMethod == null) {
                    // Look up the
                    // See if we can find the method corresponding to this call

                    //targetMethod = new ScalaDeclarationFinder().findMethodDeclaration(info, call, alternativesHolder);
                    if (targetMethod == null) {
                        return false
                    }
                }

                callLineStart = currentLineStart
                callMethod = targetMethod

                methodHolder(0) = callMethod
                parameterIndexHolder(0) = index

                if (anchorOffset == -1) {
                    anchorOffset = call.idOffset(th) // TODO - compute

                }
                anchorOffsetHolder(0) = anchorOffset
            }
        } catch {case ble:BadLocationException => Exceptions.printStackTrace(ble); return false}

        true
    }

    def resolveLink(_link:String, elementHandle:ElementHandle) :ElementHandle = {
        var link = _link
        if (link.indexOf(':') != -1) {
            link = link.replace(':', '.')
            new ElementHandle.UrlHandle(link)
        } else null
    }

    private def completeModuleFunctions(baseToken:Token[TokenId], proposals:List[CompletionProposal], request:CompletionRequest) :Boolean = {
        val kind = request.kind
        (baseToken.text.toString, baseToken.id, request.prefix) match {
            case (baseName, ErlangTokenId.Atom, prefix) =>
                val functions = request.index.queryFunctions(baseName)
                functions.filter{f => filterKind(kind, prefix, f.name)}.foreach{f =>
                    proposals.add(new PlainProposal(PseudoElement(f.name, ElementKind.METHOD), request.anchor))
                }
                !functions.isEmpty
            case _ => false
        }
        false
    }

    private def completeModules(baseToken:Token[TokenId], proposals:List[CompletionProposal], request:CompletionRequest) :Boolean = {
        (baseToken.text.toString, baseToken.id) match {
            case (baseName, ErlangTokenId.Atom) =>
                val modules = request.index.queryModules(baseName)
                modules.foreach{module =>
                    proposals.add(new PlainProposal(PseudoElement(module, ElementKind.MODULE), request.anchor))
                }
                !modules.isEmpty
            case _ => false
        }
        false
    }

    private def findCall(rootScope:AstRootScope, ts:TokenSequence[TokenId], th:TokenHierarchy[_], call:Call, times:Int) :Unit = {
        assert(rootScope != null)

        val closest = LexUtil.findPreviousNonWsNonComment(ts)
        val idToken = if (closest.id == ErlangTokenId.Colon) {
            call.caretAfterColon = true
            // skip RParen if it's the previous
            if (ts.movePrevious) {
                val prev = LexUtil.findPreviousNonWs(ts)
                if (prev != null) {
                    prev.id match {
                        case ErlangTokenId.RParen   => LexUtil.skipPair(ts, ErlangTokenId.LParen,   ErlangTokenId.RParen,   true)
                        case ErlangTokenId.RBrace   => LexUtil.skipPair(ts, ErlangTokenId.LBrace,   ErlangTokenId.RBrace,   true)
                        case ErlangTokenId.RBracket => LexUtil.skipPair(ts, ErlangTokenId.LBracket, ErlangTokenId.RBracket, true)
                        case _ =>
                    }
                }
            }
            LexUtil.findPrevIncluding(ts, LexUtil.CALL_IDs)
        } else if (LexUtil.CALL_IDs.contains(closest.id)) {
            closest
        } else null

        if (idToken != null) {
            if (times == 0) {
                if (call.caretAfterColon) {
                    call.base = idToken
                    return
                }

                val prev = if (ts.movePrevious) {
                    LexUtil.findPreviousNonWsNonComment(ts)
                } else null

                if (prev != null && prev.id == ErlangTokenId.Colon) {
                    call.caretAfterColon = true
                    call.select = idToken
                    findCall(rootScope, ts, th, call, times + 1)
                } else {
                    call.base = idToken
                    return
                }
            } else {
                call.base = idToken
                return
            }
        }

        return
    }

    case class Call(var base:Token[TokenId], var select:Token[TokenId], var caretAfterColon:Boolean)
}

object ErlangCodeCompletion {
    var callLineStart = -1
    var callMethod :ErlFunction = _

    private val REGEXP_WORDS = Array(
        // Dbl-space lines to keep formatter from collapsing pairs into a block

        // Literals

        "\\0", "The NUL character (\\u0000)",
          "\\t", "Tab (\\u0009)",
          "\\n", "Newline (\\u000A)",
          "\\v", "Vertical tab (\\u000B)",
          "\\f", "Form feed (\\u000C)",
          "\\r", "Carriage return (\\u000D)",
          "\\x", "\\x<i>nn</i>: The latin character in hex <i>nn</i>",
          "\\u", "\\u<i>xxxx</i>: The Unicode character in hex <i>xxxx</i>",
          "\\c", "\\c<i>X</i>: The control character ^<i>X</i>",
          // Character classes
        "[]", "Any one character between the brackets",
          "[^]", "Any one character not between the brackets",
          "\\w", "Any ASCII word character; same as [0-9A-Za-z_]",
          "\\W", "Not a word character; same as [^0-9A-Za-z_]",
          "\\s", "Unicode space character",
          "\\S", "Non-space character",
          "\\d", "Digit character; same as [0-9]",
          "\\D", "Non-digit character; same as [^0-9]",
          "[\\b]", "Literal backspace",
          // Match positions
        "^", "Start of line",
          "$", "End of line",
          "\\b", "Word boundary (if not in a range specification)",
          "\\B", "Non-word boundary",
          // According to JavaScript The Definitive Guide, the following are not supported
        // in JavaScript:
        // \\a, \\e, \\l, \\u, \\L, \\U, \\E, \\Q, \\A, \\Z, \\z, and \\G
        //
        //"\\A", "Beginning of string",
        //"\\z", "End of string",
        //"\\Z", "End of string (except \\n)",

        "*", "Zero or more repetitions of the preceding",
          "+", "One or more repetitions of the preceding",
          "{m,n}", "At least m and at most n repetitions of the preceding",
          "?", "At most one repetition of the preceding; same as {0,1}",
          "|", "Either preceding or next expression may match",
          "()", "Grouping", //"[:alnum:]", "Alphanumeric character class",
          //"[:alpha:]", "Uppercase or lowercase letter",
        //"[:blank:]", "Blank and tab",
        //"[:cntrl:]", "Control characters (at least 0x00-0x1f,0x7f)",
        //"[:digit:]", "Digit",
        //"[:graph:]", "Printable character excluding space",
        //"[:lower:]", "Lowecase letter",
        //"[:print:]", "Any printable letter (including space)",
        //"[:punct:]", "Printable character excluding space and alphanumeric",
        //"[:space:]", "Whitespace (same as \\s)",
        //"[:upper:]", "Uppercase letter",
        //"[:xdigit:]", "Hex digit (0-9, a-f, A-F)",
    )    // Strings section 7.8

    private val STRING_ESCAPES = Array(
        "\\0", "The NUL character (\\u0000)",
          "\\b", "Backspace (0x08)",
          "\\t", "Tab (\\u0009)",
          "\\n", "Newline (\\u000A)",
          "\\v", "Vertical tab (\\u000B)",
          "\\f", "Form feed (\\u000C)",
          "\\r", "Carriage return (\\u000D)",
          "\\\"", "Double Quote (\\u0022)",
          "\\'", "Single Quote (\\u0027)",
          "\\\\", "Backslash (\\u005C)",
          "\\x", "\\x<i>nn</i>: The latin character in hex <i>nn</i>",
          "\\u", "\\u<i>xxxx</i>: The Unicode character in hex <i>xxxx</i>",
          "\\", "\\<i>ooo</i>: The latin character in octal <i>ooo</i>",
          // PENDING: Is this supported?
        "\\c", "\\c<i>X</i>: The control character ^<i>X</i>"
    )

    private val DOC_WORDS = Array("@augments",
                                  "@class",
                                  "@config",
                                  "@constructor",
                                  "@deprecated",
                                  "@description",
                                  "@event",
                                  "@example",
                                  "@exception",
                                  "@fileOverview",
                                  "@function",
                                  "@ignore",
                                  "@inherits",
                                  "@memberOf",
                                  "@name",
                                  "@namespace",
                                  "@param",
                                  "@param",
                                  "@private",
                                  "@property",
                                  "@return",
                                  "@scope",
                                  "@scope",
                                  "@static",
                                  "@type"
    )


}
