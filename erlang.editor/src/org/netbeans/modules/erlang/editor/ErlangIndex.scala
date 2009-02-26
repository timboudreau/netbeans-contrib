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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.erlang.editor

import _root_.java.io.IOException;
import _root_.java.net.{MalformedURLException,URL}
import _root_.java.util.{Collection,Collections}
import org.netbeans.modules.csl.api.CompletionProposal
import org.netbeans.modules.csl.spi.{GsfUtilities,ParserResult}
import org.netbeans.modules.parsing.spi.{Parser}
import org.netbeans.modules.parsing.spi.indexing.support.{IndexResult,QuerySupport}
import org.netbeans.modules.erlang.editor.lexer.LexUtil
import org.netbeans.modules.erlang.editor.node.ErlSymbols._
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions

import scala.collection.mutable.{ArrayBuffer,HashSet,HashMap}

/**
 *
 * @author Caoyuan Deng
 */
class ErlangIndex(querySupport:QuerySupport) {
    import ErlangIndex._

    //    public ErlangIndexProvider.I get(FileObject fo) {
    //        Index indexEngine = ClasspathInfo.create(fo).getClassIndex(Erlang.MIME_TYPE);
    //        return new ErlangIndex(indexEngine);
    //    }

    private def search(key:String, name:String, kind:QuerySupport.Kind) :Boolean = {
        try {
            assert(querySupport != null, "index is null, are you using ErlangIndexPrivider.getDefault() ?, if so, use ErlangIndexPrivider.getDefault().get(fo) instead!")
            querySupport.query(key, name, kind, null)
            true
        } catch {
            case ex:IOException => Exceptions.printStackTrace(ex); false
        }
    }

    private def search(key:String, name:String, kind:QuerySupport.Kind, scope:Object) :Boolean = {
        try {
            assert(querySupport != null, "index is null, are you using ErlangIndexPrivider.getDefault() ?, if so, use ErlangIndexPrivider.getDefault().get(fo) instead!")
            querySupport.query(key, name, kind, null)
            true
        } catch {
            case ex:IOException => Exceptions.printStackTrace(ex); false
        }
    }

    private def searchFile(name:String, kind:QuerySupport.Kind) :List[IndexResult] = {
        import QuerySupport.Kind._
        val result = new ArrayBuffer[IndexResult]

        val field = ErlangIndexer.FIELD_FQN_NAME
        kind match {
            // No point in doing case insensitive searches on method names because
            // method names in Ruby are always case insensitive anyway
            //            case CASE_INSENSITIVE_PREFIX:
            //            case CASE_INSENSITIVE_REGEXP:
            //                field = RubyIndexer.FIELD_CASE_INSENSITIVE_METHOD_NAME;
            //                break;
            case EXACT |  PREFIX  | CAMEL_CASE | REGEXP | CASE_INSENSITIVE_PREFIX | CASE_INSENSITIVE_REGEXP =>
                // I can't do exact searches on methods because the method
                // entries include signatures etc. So turn this into a prefix
                // search and then compare chopped off signatures with the name
                //kind = NameKind.PREFIX;
            case _ => throw new UnsupportedOperationException(kind.toString)
        }

        search(field, name, kind, result)

        result.toList
    }

    def getFunction(fqn:String, functionName:String, arity:Int) :ErlFunction = {
        for (r <- searchFile(fqn, QuerySupport.Kind.EXACT)) {
            val signatures = r.getValues(ErlangIndexer.FIELD_FUNCTION)
            if (signatures != null) {
                val url = r.getUrl
                for (signature <- signatures) {
                    val function = createFuntion(url, signature);
                    if (function.name.equals(functionName) && function.arity == arity) {
                        return function;
                    }
                }
            }
        }
        null
    }

    def getMacro(includes:List[ErlInclude], macroName:String) :ErlMacro = {
        /** search including headfiles */
        for (include <- includes) {
            for (r <- searchFile(include.path, QuerySupport.Kind.EXACT)) {
                val signatures = r.getValues(ErlangIndexer.FIELD_MACRO)
                if (signatures != null) {
                    val url = r.getUrl
                    for (signature <- signatures) {
                        val macro = createMacro(url, signature);
                        if (macro.name.equals(macroName)) {
                            return macro
                        }
                    }
                }
            }
        }
        null
    }

    def getPersistentUrl(fqn:String) :URL = {
        var url = moduleToUrlBuf.get(fqn) match {
            case Some(x) => return x
            case None => null
        }

        for (map <- searchFile(fqn, QuerySupport.Kind.EXACT); url = map.getUrl if url != null) {
            try {
                moduleToUrlBuf.put(fqn, url)
                return url
            } catch {case ex:MalformedURLException => ex.printStackTrace}
        }
        
        url
    }

    private def getFunctions(fqn:String) :List[ErlFunction] = {
        functionsBuf.clear
        for (map <- searchFile(fqn, QuerySupport.Kind.EXACT)) {
            val signatures = map.getValues(ErlangIndexer.FIELD_FUNCTION)
            if (signatures != null) {
                val url = map.getUrl
                for (signature <- signatures) {
                    val function = createFuntion(url, signature)
                    functionsBuf + function
                }
            }
        }
        functionsBuf.toList
    }

    def getIncludes(fqn:String) : List[ErlInclude] = {
        includesBuf.clear
        for (map <- searchFile(fqn, QuerySupport.Kind.EXACT)) {
            val signatures = map.getValues(ErlangIndexer.FIELD_INCLUDE)
            if (signatures != null) {
                for (signature <- signatures) {
                    val include = createInclude(signature)
                    includesBuf + include
                }
            }
        }
        includesBuf.toList
    }

    private def getRecords(fqn:String) :List[ErlRecord] = {
        recordsBuf.clear
        /** search my module first */
        for (map <- searchFile(fqn, QuerySupport.Kind.EXACT)) {
            val signatures = map.getValues(ErlangIndexer.FIELD_RECORD)
            if (signatures != null) {
                val url = map.getUrl
                for (signature <- signatures) {
                    val record = createRecord(url, signature)
                    recordsBuf + record
                }
            }
        }
        /** search including headfiles */
        for (include <- getIncludes(fqn)) {
            for (map <- searchFile(include.path, QuerySupport.Kind.EXACT)) {
                val signatures = map.getValues(ErlangIndexer.FIELD_RECORD)
                if (signatures != null) {
                    val url = map.getUrl
                    for (signature <- signatures) {
                        val record = createRecord(url, signature)
                        recordsBuf + record
                    }
                }
            }
        }
        recordsBuf.toList
    }

    private def getMacros(fqn:String) :List[ErlMacro] = {
        definesBuf.clear
        /** search my module first */
        for (map <- searchFile(fqn, QuerySupport.Kind.EXACT)) {
            val signatures = map.getValues(ErlangIndexer.FIELD_MACRO)
            if (signatures != null) {
                val url = map.getUrl
                for (signature <- signatures) {
                    val define = createMacro(url, signature)
                    definesBuf + define
                }
            }
        }

        /** search including headfiles */
        for (include <- getIncludes(fqn)) {
            for (map <- searchFile(include.path, QuerySupport.Kind.EXACT)) {
                val signatures = map.getValues(ErlangIndexer.FIELD_MACRO)
                if (signatures != null) {
                    val url = map.getUrl
                    for (signature <- signatures) {
                        val define = createMacro(url, signature)
                        ErlangIndex.definesBuf + define
                    }
                }
            }
        }

        definesBuf.toList
    }

    def getModuleCompletionItems(_fqnPrefix:String) :List[ErlSymbol] = {
        completionSymsBuf.clear
        val fqnPrefix =
        if (_fqnPrefix.endsWith("'")) {
            /** remove last "'" of no-complete quoted atom */
            _fqnPrefix.substring(0, _fqnPrefix.length - 1)
        } else _fqnPrefix

        for (map <- searchFile(fqnPrefix, QuerySupport.Kind.PREFIX)) {
            val fqns = map.getValues(ErlangIndexer.FIELD_FQN_NAME)
            if (fqns != null) {
                for (fqn <- fqns) {
                    completionSymsBuf + ErlModule(fqn)
                }
            }
        }
        completionSymsBuf.toList
    }

    def getFunctionCompletionItems(fqn:String) :List[ErlSymbol] = {
        completionSymsBuf.clear
        for (function <- getFunctions(fqn)) {
            //            val argumentsOpts = function.getArgumentsOpts();
            //            if (argumentsOpts.size() == 0) {
            //                completionSymsBuf.add(CompletionProposal.create(function.getName() + "()", "/" + function.getArity(), "", CompletionItem.Type.METHOD, 1));
            //            } else {
            //                for (argumentsOpt <- argumentsOpts) {
            //                    completionSymsBuf.add(CompletionProposal.create(function.getName() + "(" + argumentsOpt + ")", "/" + function.getArity(), "", CompletionItem.Type.METHOD, 1));
            //                }
            //            }
        }
        completionSymsBuf.toList
    }

    def getRecordCompletionItems(fqn:String) :List[ErlSymbol] = {
        completionSymsBuf.clear
        for (record <- getRecords(fqn)) {
            completionSymsBuf + record
        }
        completionSymsBuf.toList
    }

    def getMacroCompletionItems(fqn:String) :List[ErlSymbol] = {
        completionSymsBuf.clear
        for (macro <- getMacros(fqn)) {
            completionSymsBuf + macro
        }
        completionSymsBuf.toList
    }

    def getRecordFieldsCompletionItems(fqn:String, recordName:String) :List[ErlSymbol] = {
        completionSymsBuf.clear
        getRecords(fqn).find(_.name.equals(recordName)) match {
            case None => null
            case Some(x) =>
                for (fieldName <- x.fields) {
                    completionSymsBuf + ErlTerm(fieldName)
                }
        }
        completionSymsBuf.toList
    }

    private def createFuntion(url:URL, signature:String) :ErlFunction = {
        val groups = signature.split(";")
        val (name, arity, offset, endOffset) = groups match {
            case Array(nameX, _, arityX, offsetX, endOffsetX, _*) => (nameX, arityX.toInt, offsetX.toInt, endOffsetX.toInt)
            case Array(nameX, _*) => (nameX, 0, 0, 0)
        }
        val function = ErlFunction(None, name, arity)
        //function.setSourceFileUrl(url)
        val args = for (i <- 5 until groups.length) yield groups(i)
        function
    }

    private def createInclude(signature:String) :ErlInclude = {
        val groups = signature.split(";")
        val (path, isLib, offset, endOffset) = groups match {
            case Array(pathX, _, isLibX, offsetX, endOffsetX, _*) => (pathX, isLibX.toBoolean, offsetX.toInt, endOffsetX.toInt)
            case Array(pathX, _*) => (pathX, false, 0, 0)
        }
        val include = ErlInclude(isLib, path)
        val url = getPersistentUrl(path)
        //include.setSourceFileUrl(url)
        include
    }

    private def createRecord(url:URL, signature:String) :ErlRecord = {
        val groups = signature.split(";")
        val (name, arity, offset, endOffset) = groups match {
            case Array(nameX, _, arityX, offsetX, endOffsetX, _*) => (nameX, arityX.toInt, offsetX.toInt, endOffsetX.toInt)
            case Array(nameX, _*) => (nameX, 0, 0, 0)
        }
        val fields = for (i <- 5 until groups.length) yield groups(i)
        val record = ErlRecord(name, fields.toList)
        //record.setSourceFileUrl(url)
        record
    }

    private def createMacro(url:URL, signature:String) :ErlMacro = {
        val groups = signature.split(";")
        val (name, arity, offset, endOffset) = groups match {
            case Array(nameX, _, arityX, offsetX, endOffsetX, _*) => (nameX, arityX.toInt, offsetX.toInt, endOffsetX.toInt)
            case Array(nameX, _*) => (nameX, 0, 0, 0)
        }
        val params = for (i <- 5 until groups.length - 1) yield groups(i)
        val body = groups(groups.length - 1)
        val macro = ErlMacro(name, params.toList, body)
        //macro.setSourceFileUrl(url)
        macro
    }
}

object ErlangIndex {
    val moduleToUrlBuf = new HashMap[String, URL]
    val completionSymsBuf = new ArrayBuffer[ErlSymbol]
    val definesBuf   = new ArrayBuffer[ErlMacro]
    val functionsBuf = new ArrayBuffer[ErlFunction]
    val includesBuf  = new ArrayBuffer[ErlInclude]
    val recordsBuf   = new ArrayBuffer[ErlRecord]
    private val EMPTY = new ErlangIndex(null)

    def get(roots:Collection[FileObject]) :ErlangIndex = {
        try {
            new ErlangIndex(QuerySupport.forRoots(ErlangIndexer.NAME,
                                                  ErlangIndexer.VERSION,
                                                  roots.toArray(new Array[FileObject](roots.size)):_*))
        } catch {case ioe:IOException => EMPTY}
    }
    
    def get(result:ParserResult) :ErlangIndex = {
        LexUtil.fileObject(result) match {
            case None => null
            case Some(fo) =>
                get(GsfUtilities.getRoots(fo,
                                          Collections.singleton(ErlangLanguage.SOURCE),
                                          Collections.singleton(ErlangLanguage.BOOT),
                                          Collections.emptySet[String]))
        }
    }
}
