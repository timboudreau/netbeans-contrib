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
 *
 * @author Caoyuan Deng
 */
class ErlangCodeCompletion extends CodeCompletionHandler {
    import ErlangCodeCompletion._

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
            request.root = root
            ErlangCodeCompletion.request = request
            
            val token = LexUtil.token(doc, lexOffset - 1) match {
                case None => return completionResult
                case Some(x) => x
            }

            token.id match {
                case ErlangTokenId.LineComment =>
                    // TODO - Complete symbols in comments?
                    return completionResult
                case ErlangTokenId.StringLiteral =>
                    //completeStrings(proposals, request)
                    return completionResult
                case _ =>
            }
            
            val ts = LexUtil.tokenSequence(th, lexOffset - 1) match {
                case None => return completionResult
                case Some(x) =>
                    x.move(lexOffset - 1)
                    if (!x.moveNext && !x.movePrevious) {
                        return completionResult
                    }
                    x
            }
 
            val closetToken = LexUtil.findPreviousNonWsNonComment(ts)

            if (root != null) {
                val sanitizedRange = pResult.sanitizedRange
                val offset = if (sanitizedRange != OffsetRange.NONE && sanitizedRange.containsInclusive(astOffset)) {
                    sanitizedRange.getStart
                } else astOffset

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
                completeLocals(proposals, request)
            }

            completeKeywords(proposals, request)
        } finally {
            doc.readUnlock
        }

        completionResult
    }

    private def completeLocals(proposals:List[CompletionProposal], request:CompletionRequest) :Unit = {
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
    }

    private def completeKeywords(proposals:List[CompletionProposal], request:CompletionRequest) :Unit = {
        val prefix = request.prefix
        val itr = LexerErlang.ERLANG_KEYWORDS.iterator
        while (itr.hasNext) {
            val keyword = itr.next
            if (startsWith(keyword, prefix)) {
                proposals.add(new KeywordProposal(keyword, null, request.anchor))
            }
        }
    }

    private def findClosestItem(root:AstRootScope, th:TokenHierarchy[_], offset:Int) :Option[AstItem] = {
        var closest = root.findItemAt(th, offset - 1)
        var closestOffset = offset - 1
        while (closest == None && closestOffset > 0) {
            closest = root.findItemAt(th, closestOffset)
            closestOffset -= 1
        }
        closest
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

    /**
     * Compute an appropriate prefix to use for code completion.
     * In Strings, we want to return the -whole- string if you're in a
     * require-statement string, otherwise we want to return simply "" or the previous "\"
     * for quoted strings, and ditto for regular expressions.
     * For non-string contexts, just return null to let the default identifier-computation
     * kick in.
     */
    override
    def getPrefix(pResult:ParserResult, lexOffset:Int, upToOffset:Boolean) :String = {
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
                        if (prefix.endsWith("?")) {
                            return ""
                        }

                        if (prefix.endsWith(":") && prefix.length > 1) {
                            return null
                        }

                        return prefix
                    }
                }
            } finally {
                doc.readUnlock
            }
            // Else: normal identifier: just return null and let the machinery do the rest
        } catch {case ble:BadLocationException => Exceptions.printStackTrace(ble)}

        null
    }

    override
    def getAutoQuery(component:JTextComponent, typedText:String) :QueryType = {
        typedText.charAt(0) match {
            case '\n' | '(' | '[' | '{' | ';' => return QueryType.STOP
            case ':' => // go on
            case _ => return QueryType.NONE
        }

        val offset = component.getCaretPosition
        val doc = component.getDocument.asInstanceOf[BaseDocument]

        if (":".equals(typedText)) { // NOI18N
            val ts = LexUtil.tokenSequence(doc, offset) match {
                case None => return QueryType.NONE
                case Some(x) => 
                    x.move(offset)
                    if (!x.moveNext && !x.movePrevious) {
                        return QueryType.NONE
                    }
                    x
            }

            if (ts.offset == offset && !ts.movePrevious) {
                return QueryType.NONE
            }
            val token = ts.token
            val id = token.id

            // TODO - handle embedded Erlang
            id.primaryCategory match {
                case "comment" | "string" | "regexp" => return QueryType.NONE // NOI18N
                case _ => return QueryType.COMPLETION
            }
        }
        
        QueryType.NONE
    }

    private def isErlangContext(doc:BaseDocument, offset:int) :Boolean = {
        val ts = LexUtil.tokenSequence(doc, offset) match {
            case None => return false
            case Some(x) =>
                x.move(offset)
                if (!x.moveNext && !x.movePrevious) {
                    return true
                }
                x
        }

        ts.token.id.primaryCategory match {
            case "comment" | "string" | "regexp" => false // NOI18N
            case _ => true
        }
    }

    override
    def resolveTemplateVariable(variable:String, info:ParserResult, caretOffset:Int, name:String, parameters:Map[_, _]) :String = {
        throw new UnsupportedOperationException("Not supported yet.")
    }

    override
    def document(info:ParserResult, element:ElementHandle) :String = {
        val sigFormatter = new SignatureHtmlFormatter
        val comment = element match {
            case x:AstDfn => x.docComment
            case _ => null
        }

        val html = new StringBuilder
        if (comment == null) {
            element match {
                case x:AstDfn => x.htmlFormat(sigFormatter)
                case _ =>
            }
            html.append(sigFormatter).append("\n<hr>\n<i>").append(NbBundle.getMessage(classOf[ErlangCodeCompletion], "NoCommentFound")).append("</i>")
        } else {
            //val formatter = new ScalaCommentFormatter(comment);
            val name = element.getName
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

    override
    def getApplicableTemplates(info:ParserResult, selectionBegin:Int, selectionEnd:Int) :Set[String] = {
        Collections.emptySet[String]
    }

    /** Todo
     * Compute parameter info for the given offset - parameters surrounding the given
     * offset, which particular parameter in that list we're currently on, and so on.
     * @param info The compilation info to pick an AST from
     * @param caretOffset The caret offset for the completion request
     * @param proposal May be null, but if not, provide the specific completion proposal
     *   that the parameter list is requested for
     * @return A ParameterInfo object, or ParameterInfo.NONE if parameter completion is not supported.
     */
    override
    def parameters(info:ParserResult, lexOffset:Int, proposal:CompletionProposal) :ParameterInfo = {
        ParameterInfo.NONE
    }

    /**
     * Resolve a link that was written into the HTML returned by {@link #document}.
     *
     * @param link The link, which can be in any format chosen by the {@link #document} method.
     *   However, links starting with www or standard URL format (http://, etc.)
     *   will automatically be handled by the browser, so avoid this format.
     * @param originalHandle The handle to the documentation item where the link was generated.
     * @return An ElementHandle that will be passed in to {@link #document} to
     *   compute the new documentation to be warped to.
     */
    override
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
                val funDfns = request.index.queryFunctions(baseName)
                funDfns.filter{f => filterKind(kind, prefix, f.getName)}.foreach{f =>
                    proposals.add(new FunctionProposal(f, request.anchor))
                }
                !funDfns.isEmpty
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
        val idToken = closest.id match {
            case ErlangTokenId.Colon =>
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
            case id if LexUtil.CALL_IDs.contains(id) => closest
            case _ => null
        }

        if (idToken != null) {
            times match {
                case 0 if call.caretAfterColon => call.base = idToken
                case 0 if ts.movePrevious => LexUtil.findPreviousNonWsNonComment(ts) match {
                        case null => call.base = idToken
                        case prev if prev.id == ErlangTokenId.Colon =>
                            call.caretAfterColon = true
                            call.select = idToken
                            findCall(rootScope, ts, th, call, times + 1)
                        case _ => call.base = idToken
                    }
                case _ => call.base = idToken
            }
        }
    }

    case class Call(var base:Token[TokenId], var select:Token[TokenId], var caretAfterColon:Boolean)
}

object ErlangCodeCompletion {
    class CompletionRequest {
        var completionResult:DefaultCompletionResult = _
        var th:TokenHierarchy[_] = _
        var info :ParserResult = _
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
        var fqn :String = _
    }

    var request:CompletionRequest = _
    var callLineStart = -1
    var callMethod :ErlFunction = _

    def setCallConext(callMethod:ErlFunction) = {
        if (request != null) {
            try {
                callLineStart = Utilities.getRowStart(request.doc, request.anchor)
            } catch {case ble:BadLocationException => Exceptions.printStackTrace(ble)}
            this.callMethod = callMethod
        }
    }
}