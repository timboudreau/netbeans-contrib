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
package org.netbeans.modules.erlang.editor


import _root_.java.io.{IOException, StringReader}
import _root_.java.util.ArrayList
import _root_.java.util.Collection
import _root_.java.util.List
import _root_.java.util.ListIterator
import _root_.javax.swing.event.ChangeListener
import _root_.javax.swing.text.BadLocationException

import org.netbeans.modules.csl.api.ElementHandle
import org.netbeans.modules.csl.api.Error
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.csl.api.Severity
import org.netbeans.modules.csl.spi.DefaultError
import org.netbeans.modules.parsing.api.Snapshot
import org.netbeans.modules.parsing.api.Task
import org.netbeans.modules.parsing.spi.ParseException
import org.netbeans.modules.csl.api.EditHistory
import org.netbeans.modules.csl.spi.GsfUtilities
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.parsing.api.Source
import org.netbeans.modules.parsing.spi.Parser
import org.netbeans.modules.parsing.spi.Parser.Result
import org.netbeans.modules.parsing.spi.ParserFactory
import org.netbeans.modules.parsing.spi.SourceModificationEvent
import org.openide.filesystems.FileObject
import org.openide.util.Exceptions

import org.netbeans.api.editor.EditorRegistry
import org.netbeans.api.lexer.{TokenHierarchy, TokenId}
import org.netbeans.editor.BaseDocument
import org.netbeans.modules.editor.NbEditorUtilities

import xtc.parser.{ParseError, SemanticValue}
import xtc.tree.{GNode, Location}

import org.netbeans.modules.erlang.editor.ast.AstRootScope
import org.netbeans.modules.erlang.editor.lexer.{ErlangTokenId, LexUtil}
import org.netbeans.modules.erlang.editor.lexer.ErlangTokenId._
import org.netbeans.modules.erlang.editor.rats.ParserErlang
import org.netbeans.modules.erlang.editor.node.AstNodeVisitor

/**
 *
 * @author Caoyuan Deng
 */
class ErlangParser extends Parser {

    private var lastResult :ErlangParserResult = _

    @throws(classOf[ParseException])
    override
    def parse(snapshot:Snapshot, task:Task, event:SourceModificationEvent) :Unit = {
        val context = new Context(snapshot, event)
        lastResult = parseBuffer(context, NONE)
        lastResult.errors = context.errors
    }

    @throws(classOf[ParseException])
    override
    def getResult(task:Task) :Result = {
        assert(lastResult != null, "getResult() called prior parse()") //NOI18N
        lastResult
    }

    override
    def cancel :Unit = {}

    override
    def addChangeListener(changeListener:ChangeListener) :Unit = {
        // no-op, we don't support state changes
    }

    override
    def removeChangeListener(changeListener:ChangeListener) :Unit = {
        // no-op, we don't support state changes
    }

    private def lexToAst(source:Snapshot, offset:Int) :Int = source match {
        case null => offset
        case _ => source.getEmbeddedOffset(offset)
    }

    private def astToLex(source:Snapshot, offset:Int) :Int = source match {
        case null => offset
        case _ => source.getOriginalOffset(offset)
    }

    private def sanitizeSource(context:Context, sanitizing:Sanitize) :Boolean = {
        false
    }

    private def sanitize(context:Context, sanitizing:Sanitize) :ErlangParserResult = {
        sanitizing match {
            case NEVER =>
                createParseResult(context)
            case NONE =>
                createParseResult(context)
            case _ =>
                // we are out of trick, just return as it
                createParseResult(context)
        }
    }

    protected def notifyError(context:Context, message:String, sourceName:String,
                              start:Int, lineSource:String, end:Int,
                              sanitizing:Sanitize, severity:Severity,
                              key:String, params:Object) :Unit = {

        val error = new DefaultError(key, message, null, context.fo.getOrElse(null), start, end, severity)

        params match {
            case null =>
            case x:Array[Object] => error.setParameters(x)
            case _ => error.setParameters(Array(params))
        }

        context.notifyError(error)

        if (sanitizing == NONE) {
            context.errorOffset = start
        }
    }

    protected def parseBuffer(context:Context, sanitizing:Sanitize) :ErlangParserResult = {
        var sanitizedSource = false
        var source = context.source

        sanitizing match {
            case NONE | NEVER =>
            case _ =>
                val ok = sanitizeSource(context, sanitizing)
                if (ok) {
                    assert(context.sanitizedSource != null)
                    sanitizedSource = true
                    source = context.sanitizedSource
                } else {
                    // Try next trick
                    return sanitize(context, sanitizing)
                }
        }

        if (sanitizing == NONE) {
            context.errorOffset = -1
        }

        val parser = createParser(context)

        val ignoreErrors = sanitizedSource
        var root :Option[GNode] = None
        try {
            var error :Option[ParseError] = None
            val r = parser.pS(0)
            if (r.hasValue) {
                val v = r.asInstanceOf[SemanticValue]
                root = Some(v.value.asInstanceOf[GNode])
            } else {
                error = Some(r.parseError)
            }

            if (!ignoreErrors) {
                def syntaxError(err:ParseError) = {
                    val start = err.index match {
                        case -1 => 0
                        case i  => i
                    }
                    notifyError(context, err.msg, "Syntax error",
                                start, "", start,
                                sanitizing, Severity.ERROR,
                                "SYNTAX_ERROR", Array(err))
                }
                
                // --- recovered errors
                for (err <- parser.errors) {
                    syntaxError(err)
                }

                // --- No-recoverable error
                for (err <- error) {
                    syntaxError(err)
                    //System.err.println(err.msg)
                }
            }
        } catch {
            case e:IOException => e.printStackTrace
            case e:IllegalArgumentException =>
                // An internal exception thrown by parser, just catch it and notify
                notifyError(context, e.getMessage, "",
                            0, "", 0,
                            sanitizing, Severity.ERROR,
                            "SYNTAX_ERROR", Array(e))
        }

        root match {
            case Some(_) =>
                context.sanitized = sanitizing
                context.root = root

                analyze(context)

                val r = createParseResult(context)
                r.setSanitized(context.sanitized, context.sanitizedRange, context.sanitizedContents)
                r.source = source
                r
            case None =>
                sanitize(context, sanitizing)
        }
    }

    private def analyze(context:Context) :Unit = {
        val doc = LexUtil.document(context.snapshot, false)

        // * we need TokenHierarchy to do anaylzing task
        for (root <- context.root;
             th <- LexUtil.tokenHierarchy(context.snapshot)) {
            // * Due to Token hierarchy will be used in analyzing, should do it in an Read-lock atomic task
            for (x <- doc) {x.readLock}
            try {
                val visitor = new AstNodeVisitor(root, th, context.fo)
                visitor.visit(root)
                context.rootScope = Some(visitor.rootScope)
            } catch {
                case ex:Throwable => ex.printStackTrace
            } finally {
                for (x <- doc) {x.readUnlock}
            }
        }
    }

    protected def createParser(context:Context) :ParserErlang = {
        val in = new StringReader(context.source)
        val fileName = context.fo match {
            case None => "<current>"
            case Some(x) => x.getNameExt
        }

        val parser = new ParserErlang(in, fileName)

        parser
    }

    private def createParseResult(context:Context) :ErlangParserResult = {
        new ErlangParserResult(context.snapshot, context.root, context.rootScope)
    }

    /** Parsing context */
    class Context(val snapshot:Snapshot, event:SourceModificationEvent) {
        val errors :List[Error] = new ArrayList[Error]

        var source :String = ErlangParser.asString(snapshot.getText)
        var caretOffset :Int = GsfUtilities.getLastKnownCaretOffset(snapshot, event)

        var root :Option[GNode] = None
        var rootScope :Option[AstRootScope] = None
        var errorOffset :Int = _
        var sanitizedSource :String = _
        var sanitizedRange :OffsetRange = OffsetRange.NONE
        var sanitizedContents :String = _
        var sanitized :Sanitize = NONE

        def notifyError(error:Error) = errors.add(error)

        def fo :Option[FileObject] = snapshot.getSource.getFileObject match {
            case null => None
            case x => Some(x)
        }

        override
        def toString = "ErlangParser.Context(" + fo + ")" // NOI18N

    }
}

object ErlangParser {
    def asString(sequence:CharSequence) :String = sequence match {
        case s:String => s
        case _ => sequence.toString
    }

    def sourceUri(source:Source) :String = source.getFileObject match {
        case null => "fileless" //NOI18N
        case f => f.getNameExt
    }
}

/** Attempts to sanitize the input buffer */
sealed case class Sanitize
/** Only parse the current file accurately, don't try heuristics */
case object NEVER extends Sanitize
/** Perform no sanitization */
case object NONE extends Sanitize
