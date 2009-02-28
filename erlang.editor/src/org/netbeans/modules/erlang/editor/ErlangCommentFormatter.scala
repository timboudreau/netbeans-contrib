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

import org.netbeans.api.lexer.{Token,TokenHierarchy,TokenSequence}
import org.netbeans.modules.erlang.editor.lexer.ErlangTokenId
import org.openide.util.NbBundle

import scala.collection.mutable.ArrayBuffer

/**
 *
 * @author Caoyuan Deng
 */
object ErlangCommentFormatter {
    val Doc_TAG = "@doc" //NOI18N
    val SPEC_TAG = "@spec" //NOI18N
    val TYPE_TAG = "@type" //NOI18N
    val PARAM_TAG = "@param" //NOI18N
    val THROWS_TAG = "@throws" //NOI18N
    val RETURN_TAG = "@return" //NOI18N
    val EXAMPLE_TAG = "@example" //NOI18N
    val DEPRECATED_TAG = "@deprecated" //NOI18N
}

class ErlangCommentFormatter(comment:String) {
    import ErlangCommentFormatter._

    private val th = TokenHierarchy.create(comment, ErlangTokenId.language)
    private val ts = th.tokenSequence(ErlangTokenId.language)
    val summary = new StringBuilder
    val rest = new StringBuilder
    val params = new ArrayBuffer[String]
    val exceptions = new ArrayBuffer[String]
    var returnTag :String = _
    var returnType :String = _
    var spec :String = _
    private var deprecation :String = _
    private var example :String = _
    // flag to see if this is already formatted comment with all html stuff
    private var formattedComment :Boolean = _

    process

    def setSeqName(name:String) :Unit = {
    }

    def toHtml :String = {
        val sb = new StringBuilder

        if (!formattedComment && summary.length > 0) {
            val summaryText = summary.toString.trim
            if (summaryText.length > 0) {
                sb.append("<b>")
                sb.append(NbBundle.getMessage(classOf[ErlangCommentFormatter], "Summary"))
                sb.append("</b><blockquote>").append(summaryText).append("</blockquote>") //NOI18N
            }
        } else {
            sb.append(summary)
        }

        if (deprecation != null) {
            val hasDescription = deprecation.trim.length > 0
            sb.append("<b")
            if (!hasDescription) {
                sb.append(" style=\"background:#ffcccc\"")
            }
            sb.append(">")
            sb.append(NbBundle.getMessage(classOf[ErlangCommentFormatter], "Deprecated"))
            sb.append("</b>")
            sb.append("<blockquote")
            if (hasDescription) {
                sb.append(" style=\"background:#ffcccc\">")
                sb.append(deprecation)
            } else {
                sb.append(">")
            }
            sb.append("</blockquote>") //NOI18N
        }

        if (params.size > 0) {
            sb.append("<b>")
            sb.append(NbBundle.getMessage(classOf[ErlangCommentFormatter], "Parameters"))
            sb.append("</b><blockquote>") //NOI18N
            for (i <- 0 until params.size) {
                if (i > 0) {
                    sb.append("<br><br>") // NOI18N
                }
                val param = params(i)
                sb.append(param)
            }
            sb.append("</blockquote>") // NOI18N
        }

        if (returnTag != null || returnType != null) {
            sb.append("<b>") // NOI18N
            sb.append(NbBundle.getMessage(classOf[ErlangCommentFormatter], "Returns"))
            sb.append("</b><blockquote>") //NOI18N
            if (returnTag != null) {
                sb.append(returnTag)
                if (returnType != null) {
                    sb.append("<br>") // NOI18N
                }
            }
            if (returnType != null) {
                sb.append(NbBundle.getMessage(classOf[ErlangCommentFormatter], "ReturnType"))
                sb.append("<i>") // NOI18N
                sb.append(returnType)
                sb.append("</i>") // NOI18N
            }
            sb.append("</blockquote>") //NOI18N
        }

        if (exceptions.size > 0) {
            sb.append("<b>")
            sb.append(NbBundle.getMessage(classOf[ErlangCommentFormatter], "Throws"));
            sb.append("</b><blockquote>") //NOI18N
            for (tag <- exceptions) {
                sb.append(tag)
                sb.append("<br>") // NOI18N
            }
            sb.append("</blockquote>") // NOI18N
        }

        if (example != null) {
            sb.append("<b>");
            sb.append(NbBundle.getMessage(classOf[ErlangCommentFormatter], "CodeExample"))
            sb.append("</b><blockquote>") //NOI18N
            sb.append("<pre>").append(example).append("</pre></blockquote>") //NOI18N
        }

        if (spec != null) {
            sb.append("<b>");
            sb.append(NbBundle.getMessage(classOf[ErlangCommentFormatter], "Spec"))
            sb.append("</b><blockquote>") //NOI18N
            sb.append("<pre>").append(spec).append("</pre></blockquote>") //NOI18N
        }

        if (rest.length > 0) {
            sb.append("<b>")
            sb.append(NbBundle.getMessage(classOf[ErlangCommentFormatter], "Miscellaneous"))
            sb.append("</b><blockquote>") //NOI18N
            sb.append(rest)
            sb.append("</blockquote>") // NOI18N
        }

        sb.toString
    }

    def getSummary = summary.toString.trim

    private def process :Unit = {
        while (ts.moveNext && ts.token.id != ErlangTokenId.CommentTag) {
            val token = ts.token
            val line = token.text.toString.trim
            summary.append(removeCommentPrefix(line)).append(' ')
        }

        ts.movePrevious
        var sb:StringBuilder = null
        while (ts.moveNext) {
            val token = ts.token
            if (token.id == ErlangTokenId.CommentTag) {
                if (sb != null) {
                    processTag(sb.toString.trim)
                }
                sb = new StringBuilder
            }
            if (sb != null) { // we have some tags
                val line = token.text.toString.trim
                sb.append(removeCommentPrefix(line)).append(' ')
            }
        }

        if (sb != null) {
            processTag(sb.toString.trim)
        }
    }

    private def processTag(tag:String) :Unit = {
        if (tag.startsWith(PARAM_TAG)) {
            // Try to make the parameter name bold, and the type italic
            val s = tag.substring(PARAM_TAG.length).trim
            if (s.length == 0) {
                return
            }
            val sb = new StringBuilder
            var index = 0
            if (s.charAt(0) == '{') {
                // We have a type
                var end = s.indexOf('}')
                if (end != -1) {
                    end += 1
                    sb.append("<i>") // NOI18N
                    sb.append(s.substring(0, end))
                    sb.append("</i>") // NOI18N
                }
                index = end
                var cont = true
                while (index < s.length && cont) {
                    if (!Character.isWhitespace((s.charAt(index)))) {
                        cont = false
                    }
                    index += 1
                }
            }
            if (index < s.length) {
                var end = index
                var cont = true
                while (end < s.length && cont) {
                    if (Character.isWhitespace((s.charAt(end)))) {
                        cont = false
                    }
                    end += 1
                }
                if (end < s.length) {
                    sb.append(" <b>") // NOI18N
                    sb.append(s.substring(index, end))
                    sb.append("</b>") // NOI18N
                    sb.append(s.substring(end))
                    params + sb.toString
                    return
                }
            }
            params + s
        } else if (tag.startsWith(RETURN_TAG)) {
            returnTag = tag.substring(RETURN_TAG.length).trim
        } else if (tag.startsWith(TYPE_TAG)) {
            returnType = tag.substring(TYPE_TAG.length).trim
        } else if (tag.startsWith(THROWS_TAG)) {
            exceptions + tag.substring(THROWS_TAG.length).trim
        } else if (tag.startsWith(DEPRECATED_TAG)) {
            deprecation = tag.substring(DEPRECATED_TAG.length).trim
        } else if (tag.startsWith(EXAMPLE_TAG)) {
            example = tag.substring(EXAMPLE_TAG.length).trim
            example = escapeHtml(example)
        } else if (tag.startsWith(SPEC_TAG)) {
            spec = tag.substring(SPEC_TAG.length).trim
            spec = escapeHtml(spec)
        } else {
            // Store up the rest of the stuff so we don't miss unexpected tags,
            // like @private, @config, etc.
            if (!tag.startsWith("@id ") && !tag.startsWith("@name ") && // NOI18N
                !tag.startsWith("@attribute") && // NOI18N
                !tag.startsWith("@method") && !tag.startsWith("@property")) { // NOI18N
                rest.append(tag)
                rest.append("<br>") // NOI18N
            }
        }
    }

    private def escapeHtml(_code:String) :String = {
        var code = _code.replace("&", "&amp;") // NOI18N
        code = code.replace("<", "&lt;") // NOI18N
        code = code.replace(">", "&gt;") // NOI18N
        code
    }

    private def removeCommentPrefix(_line:String) :String = {
        var line = _line.trim
        if (line.startsWith("%")) {
            line.substring(1)
        } else {
            line
        }
    }
}
