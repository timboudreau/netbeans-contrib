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

import _root_.java.io.{File,IOException}
import _root_.java.net.MalformedURLException
import _root_.java.util.{Collection}
import javax.swing.text.Document
import org.netbeans.editor.BaseDocument;
import org.netbeans.api.lexer.{TokenHierarchy}
import org.netbeans.modules.csl.api.{ElementKind,Modifier}
import org.netbeans.modules.parsing.api.Snapshot
import org.netbeans.modules.parsing.spi.Parser.Result
import org.netbeans.modules.parsing.spi.indexing.{Context,EmbeddingIndexer,EmbeddingIndexerFactory,Indexable}
import org.netbeans.modules.parsing.spi.indexing.support.{IndexDocument,IndexingSupport}
import org.netbeans.modules.erlang.editor.node.ErlSymbols._
import org.netbeans.modules.erlang.editor.lexer.LexUtil
import org.openide.filesystems.{FileObject,FileStateInvalidException,FileUtil}
import org.openide.util.Exceptions
import org.openide.windows.{IOProvider,InputOutput}

import scala.collection.mutable.ArrayBuffer

/**
 * GSF Indexex will index project resources and resources of boot classess
 * Where boot classes usally will be get from org.netbeans.spi.gsfpath.classpath.ClassPathProvider
 * Project manager should provide an implementation of ClassPathProvider under META-INFO.services
 * for example:
 *     org.netbeans.modules.erlang.project.ProjectClassPathProvider
 *     org.netbeans.modules.erlang.project.BootClassPathProvider
 * The later one will init BootClassPathImplementation, which implemented infterface:
 *     org.netbeans.modules.erlang.project.classpath.BootClassPathImplementation#getResources
 * Since each project may use different platform, so the BootClassPathImplementation will be
 * put in project module, and will get active.platform from project.properties, thus know which
 * boot classes will be used.
 *
 * @author Caoyuan Deng
 */
class ErlangIndexer extends EmbeddingIndexer {

    //private static final boolean INDEX_UNDOCUMENTED = Boolean.getBoolean("ruby.index.undocumented");
    private val INDEX_UNDOCUMENTED = true
    private val PREINDEXING = true//Boolean.getBoolean("gsf.preindexing");

    private val io :InputOutput = IOProvider.getDefault.getIO("Info", false)

    def getPersistentUrl(file:File) :String = {
        try {
            file.toURI.toURL.toExternalForm
            // Make relative URLs for urls in the libraries
            //return RubyIndex.getPreindexUrl(url);
        } catch {
            case ex:MalformedURLException =>
                Exceptions.printStackTrace(ex)
                file.getPath
        }
    }

    override
    protected def index(indexable:Indexable, parserResult:Result, context:Context) :Unit = {
	val start = System.currentTimeMillis
        //if (file.isPlatform())
        io.getOut().print("Indexing: " + parserResult.getSnapshot.getSource.getFileObject + " ")

        val r = parserResult match {
            case null => return
            case x:ErlangParserResult => x
        }

        r.rootScope match {
            case None => return
            case Some(x) => x
        }

        val support = try {
            IndexingSupport.getInstance(context)
        } catch {
            case ioe:IOException => return
        }


        // I used to suppress indexing files that have had automatic cleanup to
        // remove in-process editing. However, that makes code completion not
        // work for local classes etc. that are being queried. I used to handle
        // that by doing local AST searches but this had a lot of problems
        // (not handling scoping and inheritance well etc.) so now I'm using the
        // index for everything.
        //  if (r.getSanitizedRange() != OffsetRange.NONE) {
        //     return;
        //  }

        val analyzer = new TreeAnalyzer(r, support, indexable)
        analyzer.analyze

        for (doc <- analyzer.getDocuments) {
            support.addDocument(doc)
        }

	//if (file.isPlatform())
        io.getOut().println((System.currentTimeMillis() - start) + "ms");
    }

    /** Travel through parsed result, and index meta-data */
    class TreeAnalyzer(pResult:ErlangParserResult, support:IndexingSupport, indexable:Indexable) {
        val MODULE = 1
        val HEADER = 2

        private var imports:String = _
        private val documents = new ArrayBuffer[IndexDocument]

        private val fo :FileObject = pResult.getSnapshot.getSource.getFileObject
        private val tpe = fo.getExt match {
            case "hrl" => HEADER
            case _ => MODULE
        }

        private val url = try {
            fo.getURL.toExternalForm
            if (PREINDEXING) {
                // Make relative URLs for preindexed data structures
                //url = ErlangIndex.getPreindexUrl(url);
            }
        } catch {
            case ex:FileStateInvalidException => null
        }

        private val th:TokenHierarchy[_] = LexUtil.tokenHierarchy(pResult) match {
            case None => null
            case Some(x) => x
        }

        def getDocuments : List[IndexDocument] = documents.toList

        def analyze {
            if (th == null) return

            val rootScope = pResult.rootScope match {
                case None => return
                case Some(x) => x
            }

            val fqn = tpe match {
                case MODULE => rootScope.findAllDfnSyms(classOf[ErlModule]) match {
                        case x :: _ => x.name
                        case _ => null
                    }
                case _ => null
                    // @todo getHeaderFqn(fo)
            }
            if (fqn == null) {
                return
            }

            /** we will index exported functions and, defined macros etc */
            val includes = rootScope.findAllDfnSyms(classOf[ErlInclude])
            val exports  = rootScope.findAllDfnSyms(classOf[ErlExport])
            val records  = rootScope.findAllDfnSyms(classOf[ErlRecord])
            val macros   = rootScope.findAllDfnSyms(classOf[ErlMacro])

            /** @ReferenceOnly used by sqlIndexEngine
             * if (isSqlIndexAvaialble(index)) {
             * long moduleId = sqlIndexEngine.storeModule(module.getName(), url);
             * if (moduleId != -1) {
             * for (ErlExport export : exports) {
             * sqlIndexEngine.storeFunctions(export.getFunctions(), moduleId);
             * }
             * }
             * }
             */
             
            /** The following code is currently for updating the timestamp only */
            analyzeModule(fqn, includes, exports, records, macros)
        }

        /**
         * @NOTE Add "lib;" before header file fqn of lib, it also contains its ext (such as ".hrl")
         */
        private def getHeaderFqn(fo:FileObject) :String = {
            val libFo :FileObject = null//RubyPlatformManager.getDefaultPlatform().getLibFO();
            assert(libFo != null)
            val relativePath = FileUtil.getRelativePath(libFo, fo)
            /**
             * @NOTE: we can not rely on file.isPlatform here: when a platform file
             * is opened in editor, the file.isPlatform seems always return false;
             */
            if (relativePath == null) {
                // not a platform lib file
                return fo.getNameExt
            }

            val groups = relativePath.split(File.separator)
            val pkgNameWithVersion = if (groups.length >= 1) groups(0) else relativePath
            // Remove version number:
            val dashIdx = pkgNameWithVersion.lastIndexOf('-')
            val pkgName = if (dashIdx != -1) pkgNameWithVersion.substring(0, dashIdx) else pkgNameWithVersion
            val sb = new StringBuilder(30);
            sb.append("lib;").append(pkgName)
            for (i <- 1 until groups.length) {
                sb.append("/").append(groups(i))
            }
            sb.toString
        }

        private def analyzeModule(fqn:String, includes:List[ErlInclude], exports:List[ErlExport], records:List[ErlRecord], macros:List[ErlMacro]) :Unit = {
            /** Add a Lucene document */
            val document = support.createDocument(indexable)
            documents + document

            val typeAttr = if ( tpe == MODULE) "m" else "h"
            val attrs = typeAttr

            /** @TODO */
            //boolean isDocumented = isDocumented(node);
            //int documentSize = getDocumentSize(node);
            //
            //if (documentSize > 0) {
            //    attributes = attributes + "d(" + documentSize + ")";
            //}
            document.addPair(ErlangIndexer.FIELD_ATTRS, attrs, false, true)

            document.addPair(ErlangIndexer.FIELD_FQN_NAME, fqn, true, true)

            includes.foreach(indexInclude(_, document))
            /** we only index exported functions */
            exports.foreach(_.functions.foreach{function => indexFunction(function, document)})
            records.foreach{indexRecord(_, document)}
            macros.foreach(indexMacro(_, document))
        }

        private def indexFunction(function:ErlFunction, document:IndexDocument) :Unit = {
            val sb = new StringBuilder
            sb.append(function.name)

            val isDocumented = false; // @TODO isDocumented(childNode);
            if (isDocumented) {
                sb.append(";").append("d")
            } else {
                sb.append(";").append("")
            }

            sb.append(";").append(function.arity)

            sb.append(";").append(function.offset(th))
            sb.append(";").append(function.endOffset(th))

            //function.args.foreach{sb.append(";").append(_)}

            document.addPair(ErlangIndexer.FIELD_FUNCTION, sb.toString, true, true)
        }

        private def indexInclude(include:ErlInclude, document:IndexDocument) :Unit = {
            val sb = new StringBuilder
            sb.append(include.path)

            val isDocumented = false // @TODO isDocumented(childNode);
            if (isDocumented) {
                sb.append(";").append("d")
            } else {
                sb.append(";").append("")
            }

            sb.append(";").append(include.isLib)

            sb.append(";").append(include.offset(th))
            sb.append(";").append(include.endOffset(th))

            document.addPair(ErlangIndexer.FIELD_INCLUDE, sb.toString, true, true)
        }

        private def indexRecord(record:ErlRecord, document:IndexDocument) :Unit = {
            val sb = new StringBuilder
            sb.append(record.name)

            val isDocumented = false // @TODO isDocumented(childNode);
            if (isDocumented) {
                sb.append(";").append("d")
            } else {
                sb.append(";").append("")
            }

            sb.append(";").append(record.fields.size)

            sb.append(";").append(record.offset(th))
            sb.append(";").append(record.endOffset(th))
            record.fields.foreach{sb.append(";").append(_)}

            document.addPair(ErlangIndexer.FIELD_RECORD, sb.toString, true, true)
        }

        private def indexMacro(macro:ErlMacro, document:IndexDocument) :Unit = {
            val sb = new StringBuilder
            sb.append(macro.name)

            val isDocumented = false // @TODO isDocumented(childNode);
            if (isDocumented) {
                sb.append(";").append("d")
            } else {
                sb.append(";").append("")
            }

            sb.append(";").append(macro.params.size)

            sb.append(";").append(macro.offset(th))
            sb.append(";").append(macro.endOffset(th))

            macro.params.foreach{sb.append(";").append(_)}

            sb.append(";").append(macro.body)

            document.addPair(ErlangIndexer.FIELD_MACRO, sb.toString, true, true)
        }
    } // end of inner class TreeAnalyzer

    def getPreindexedDb :FileObject = {
        null
        //        if (preindexedDb == null) {
        //            File preindexed = InstalledFileLocator.getDefault().locate(
        //                    "preindexed", "org.netbeans.modules.ruby", false); // NOI18N
        //            if (preindexed == null || !preindexed.isDirectory()) {
        //                throw new RuntimeException("Can't locate preindexed directory. Installation might be damaged"); // NOI18N
        //            }
        //            preindexedDb = FileUtil.toFileObject(preindexed);
        //        }
        //        return preindexedDb;
    }
}

object ErlangIndexer {
    /** Fields of Module Document for Lucene */
    /** Fully Qualified Name */
    val FIELD_FQN_NAME = "fqn" //NOI18N
    val FIELD_FILEURL  = "source" // NOI18N
    val FIELD_EXPORT   = "export" //NOI18N
    val FIELD_EXPORTS  = "exports" //NOI18N
    val FIELD_IMPORT   = "import" //NOI18N
    val FIELD_IMPORTS  = "imports" //NOI18N
    /** Attributes: "i" -> private, "o" -> protected, ", "s" - static/notinstance, "d" - documented */
    val FIELD_INCLUDE  = "include" //NOI18N
    val FIELD_FUNCTION = "function" //NOI18N
    val FIELD_RECORD   = "record" //NOI18N
    val FIELD_MACRO    = "macro" //NOI18N
    /** Attributes: "m" -> module, "d" -> documented, "d(nnn)" documented with n characters */
    val FIELD_ATTRS    = "attrs" //NOI18N
    
    val NAME = "erlang" // NOI18N
    val VERSION = 9
    
    class Factory extends EmbeddingIndexerFactory {

        val INDEXABLE_FOLDERS = Array("src", "include", "test")

        override
        def createIndexer(indexable:Indexable, snapshot:Snapshot) :EmbeddingIndexer = {
            if (isIndexable(indexable, snapshot)) {
                new ErlangIndexer
            } else null
        }

        override
        def getIndexerName = NAME

        override
        def getIndexVersion = VERSION

        private def isIndexable(indexable:Indexable, snapshot:Snapshot) :Boolean = {
            val fo :FileObject = snapshot.getSource.getFileObject
            if (fo == null) {
                /**
                 * Not each kind of MIME files hava FileObject, for instance:
                 * ParserFile with name as ".LCKxxxxx.erl~" etc will have none FileObject.
                 */
                return false;
            }

            val maxMemoryInMBs = Runtime.getRuntime.maxMemory / (1024.0 * 1024.0)
            val path = fo.getPath
            fo.getExt match {
                case "erl" | "hrl" =>
                    for (indexableFolder <- INDEXABLE_FOLDERS if path.contains(indexableFolder)) {
                        /**
                         * @TODO: a bad hacking:
                         * try to ignore these big files according to max memory size */
                        val fileSizeInKBs = fo.getSize() / 1024.0;
                        /**
                         * 250M:  < 200KB
                         * 500M:  < 400KB
                         * 1500M: < 1200KB
                         */
                        val factor = (maxMemoryInMBs / 250.0) * 200;
                        if (fileSizeInKBs > factor) {
                            //if (file.isPlatform()) io.getErr().println("Indexing: " + fo.getPath() + " (skipped due to too big!)");
                            return false
                        }
                        return true
                    }
                    false
                case _ => false
            }
        }

        override
        def filesDeleted(deleted:Collection[_ <: Indexable], context:Context) :Unit = {
            try {
                val support = IndexingSupport.getInstance(context)
                val itr = deleted.iterator
                while (itr.hasNext) {
                    support.removeDocuments(itr.next)
                }
            } catch {
                case ex:IOException => Exceptions.printStackTrace(ex)
            }
        }
    }


    private var preindexedDb :FileObject = _

    /** For testing only */
    def setPreindexedDb(preindexedDb:FileObject) :Unit = {
        ErlangIndexer.preindexedDb = preindexedDb
    }
}
