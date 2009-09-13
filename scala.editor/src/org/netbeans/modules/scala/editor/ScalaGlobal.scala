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
package org.netbeans.modules.scala.editor

import java.beans.{PropertyChangeEvent, PropertyChangeListener}
import java.io.{File, IOException}
import java.lang.ref.{Reference, WeakReference}
import java.net.{MalformedURLException, URI, URISyntaxException, URL}
import java.util.Date
import java.util.logging.{Logger, Level}
import org.netbeans.api.java.classpath.ClassPath
import org.netbeans.api.java.queries.BinaryForSourceQuery
import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy}
import org.netbeans.api.project.{FileOwnerQuery, Project, ProjectUtils, Sources, SourceGroup}
import org.netbeans.modules.csl.api.ElementKind
import org.netbeans.spi.java.classpath.ClassPathProvider
import org.netbeans.spi.java.queries.BinaryForSourceQueryImplementation
import org.openide.filesystems.{FileChangeAdapter, FileEvent, FileObject, FileRenameEvent,
                                FileStateInvalidException, FileSystem, FileUtil, JarFileSystem, FileChangeListener}
import org.openide.util.{Exceptions, RequestProcessor}

import org.netbeans.api.language.util.ast.{AstScope}
import org.netbeans.modules.scala.editor.ast.{ScalaItems, ScalaDfns, ScalaRefs, ScalaRootScope, ScalaAstVisitor, ScalaUtils}
import org.netbeans.modules.scala.editor.element.{ScalaElements, JavaElements}

import scala.collection.mutable.{ArrayBuffer, LinkedHashMap, WeakHashMap}

import scala.tools.nsc.{Phase, Settings}

import org.netbeans.modules.scala.editor.interactive.Global
//import scala.tools.nsc.interactive.Global
import scala.tools.nsc.symtab.{SymbolTable, Flags}
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.io.PlainFile
import scala.tools.nsc.reporters.{Reporter}
import scala.tools.nsc.util.{Position, SourceFile, NoPosition}

/**
 *
 * @author Caoyuan Deng
 */
object ScalaGlobal {

  private val Log = Logger.getLogger(classOf[ScalaGlobal].getName)
  
  /** index of globals */
  private val Global = 0
  private val GlobalForTest = 1
  private val GlobalForDebug = 2
  private val GlobalForTestDebug = 3

  private class Cache {
    val globals = new Array[ScalaGlobal](4)

    var srcToOut:  Map[FileObject, FileObject] = Map()
    var testToOut: Map[FileObject, FileObject] = Map()

    def srcOutDirsPath = toDirPaths(srcToOut)
    def testSrcOutDirsPath = toDirPaths(testToOut)

    def scalaSrcToOut:  Map[AbstractFile, AbstractFile] = toScalaDirs(srcToOut)
    def scalaTestToOut: Map[AbstractFile, AbstractFile] = toScalaDirs(testToOut)

    private def toDirPaths(dirs: Map[FileObject, FileObject]): Map[String, String] = {
      for ((src, out) <- dirs) yield (toDirPath(src), toDirPath(out))
    }

    private def toScalaDirs(dirs: Map[FileObject, FileObject]): Map[AbstractFile, AbstractFile] = {
      for ((src, out) <- dirs) yield (toScalaDir(src), toScalaDir(out))
    }

    private def toDirPath(fo: FileObject) = FileUtil.toFile(fo).getAbsolutePath
    private def toScalaDir(fo: FileObject) = AbstractFile.getDirectory(FileUtil.toFile(fo))
  }
  
  private val debug = false

  private var globalForStdLib: Option[ScalaGlobal] = None
  
  private val projectToCaches = new WeakHashMap[Project, Cache]
  private var globalToListeners = Map[ScalaGlobal, List[FileChangeListener]]()
  private var toResetGlobals = Set[ScalaGlobal]()

  val dummyReporter = new Reporter {def info0(pos: Position, msg: String, severity: Severity, force: Boolean) {}}

  case class NormalReason(msg: String) extends Throwable(msg)
  case object userRequest extends NormalReason("User's action")
  case object compCpChanged extends NormalReason("Change of compile classpath")

  def resetLate(global: ScalaGlobal, reason: Throwable) = synchronized {
    reason match {
      case NormalReason(msg) => Log.info("Will reset global late due to: " + msg)
      case _ => Log.log(Level.WARNING, "Will reset global late due to:", reason)
    }

    toResetGlobals += global
    
    if (globalForStdLib.isDefined && global == globalForStdLib.get) {
      globalForStdLib = None
    } else {
      projectToCaches find {case (p, r) =>
          var found = false
          var i = 0
          val size = r.globals.size
          while (i < size && !found) {
            if (r.globals(i) == global) {
              r.globals(i) = null
              globalToListeners.get(global) foreach {xs =>
                xs foreach {x =>
                  p.getProjectDirectory.getFileSystem.removeFileChangeListener(x)
                }
              }
              globalToListeners -= global
              found = true
            }
            i += 1
          }
          found
      }
    }
  }

  /**
   * @Note
   * Tried to use to reset global instead of create a new one, but for current scala Global,
   * it seems reset operation cannot got a clean global?
   */
  def resetBadGlobals = synchronized {
    for (global <- toResetGlobals) {
      Log.info("Reset global: " + global)

      // * this will cause global create a new TypeRun so as to release all unitbuf and filebuf.
      // * But, it seems askReset will only reset current unit, when exception is throw inside
      // * for example, typeCheck, the dependent units may have been damaged, and the symbols in
      // * global may need to be reset too. So the best way is to drop this gloal, use a new
      // * created one instead.
      //global.askReset
      
      //global.analyzer.resetTyper
      //global.firsts = Nil
      //global.unitOfFile.clear

      // * stop compiler daemon thread
      global.askShutdown
    }

    toResetGlobals = Set[ScalaGlobal]()
  }

  /**
   * Scala's global is not thread safed
   */
  def getGlobal(fo: FileObject, forDebug: Boolean = false): ScalaGlobal = synchronized {
    resetBadGlobals
    
    val project = FileOwnerQuery.getOwner(fo)
    if (project == null) {
      // * it may be a standalone file, or file in standard lib
      return globalForStdLib getOrElse {
        val g = ScalaHome.getGlobalForStdLib
        globalForStdLib = Some(g)
        g
      }
    }

    val cache = projectToCaches.get(project) getOrElse {
      val cachex = findDirResources(project)
      projectToCaches.put(project, cachex)
      cachex
    }

    // * is this `fo` under test source?
    val forTest = cache.testToOut find {case (src, _) =>
        src.equals(fo) || FileUtil.isParentOf(src, fo)
    } isDefined

    // * Do not use `srcCp` as the key, different `fo` under same src dir seems returning diff instance of srcCp
    val idx = if (forDebug) {
      if (forTest) GlobalForTestDebug else GlobalForDebug
    } else {
      if (forTest) GlobalForTest else Global
    }

    val g = cache.globals(idx)
    if (g != null) {
      return g
    }

    // ----- need to create a new global:
    
    val settings = new Settings
    if (debug) {
      settings.debug.value = true
      settings.verbose.value = true
    } else {
      settings.debug.value = false
      settings.verbose.value = false
    }

    var bootCp = ClassPath.getClassPath(fo, ClassPath.BOOT)
    var compCp = ClassPath.getClassPath(fo, ClassPath.COMPILE)
    val srcCp  = ClassPath.getClassPath(fo, ClassPath.SOURCE)

    val inStdLib =
      if (bootCp == null || compCp == null) {
        true // * in case of `fo` in standard libaray
      } else false

    // ----- set sourcepath, outpath
    
    var outPath = ""
    var srcPaths: List[String] = Nil
    for ((src, out) <- if (forTest) cache.testSrcOutDirsPath else cache.srcOutDirsPath) {
      srcPaths = src :: srcPaths

      // * we only need one out path
      if (outPath == "") {
        outPath = out

        // * Had out path dir been deleted? (a clean task etc), if so, create it, since scalac
        // * can't parse anything correctly without an exist out dir (sounds a bit strange)
        try {
          val file = new File(outPath)
          if (!file.exists) file.mkdirs
        } catch {case _ =>}
      }
    }

    // * @Note: do not add src path to global for test, since the corresponding build/classes has been added to compCp

    settings.sourcepath.tryToSet(srcPaths.reverse)
    settings.outputDirs.setSingleOutput(outPath)

    Log.info("project's source paths set for global: " + srcPaths)
    if (srcCp != null){
      Log.info(srcCp.getRoots.mkString("project's srcCp: [", ", ", "]"))
    } else {
      Log.info("project's srcCp is empty !")
    }
    
    // * @Note: settings.outputDirs.add(src, out) seems cannot resolve symbols in other source files, why?
    /*_
     for ((src, out) <- if (forTest) dirs.scalaTestSrcOutDirs else dirs.scalaSrcOutDirs) {
     settings.outputDirs.add(src, out)
     }
     */

    // ----- set bootclasspath, classpath

    val sb = new StringBuilder
    concatClassPath(project, sb, bootCp)
    settings.bootclasspath.tryToSet(List(sb.toString))

    sb.delete(0, sb.length)
    concatClassPath(project, sb, compCp)
    settings.classpath.tryToSet(List(sb.toString))

    // ----- now, the new global

    val global = new ScalaGlobal(settings, dummyReporter)
    cache.globals(idx) = global

    // * listen to compCp's change
    if (compCp != null) {
      val compCpListener = new CompCpListener(global, compCp)
      globalToListeners += (global -> (compCpListener :: globalToListeners.getOrElse(global, Nil)))
      project.getProjectDirectory.getFileSystem.addFileChangeListener(compCpListener)
    }
   
    if (!forDebug) {
      // * we have to do following step to get mixed java sources visible to scala sources
      if (srcCp != null) {
        val srcCpListener = new SrcCpListener(global, srcCp)
        globalToListeners += (global -> (srcCpListener :: globalToListeners.getOrElse(global, Nil)))
        project.getProjectDirectory.getFileSystem.addFileChangeListener(srcCpListener)

        // * should push java srcs before scala srcs
        val javaSrcs = new ArrayBuffer[FileObject]
        srcCp.getRoots foreach {x => findAllSourcesOf("text/x-java", x, javaSrcs)}

        val scalaSrcs = new ArrayBuffer[FileObject]
        // * it seems only java src files need to be pushed explicitly ?
        //srcCp.getRoots foreach {x => findAllSourcesOf("text/x-scala", x, scalaSrcs)}

        // * the reporter should be set, otherwise, no java source is resolved, maybe throws exception already.
        global.reporter = dummyReporter
        global askForReLoad (javaSrcs ++= scalaSrcs).toList
      }
    }

    global
  }

  private def findAllSourcesOf(mimeType: String, dirFo: FileObject, result: ArrayBuffer[FileObject]): Unit = {
    dirFo.getChildren foreach {
      case x if x.isFolder => findAllSourcesOf(mimeType, x, result)
      case x if x.getMIMEType == mimeType => result += x
      case _ =>
    }
  }

  private def findDirResources(project: Project): Cache = {
    val cache = new Cache

    val sources = ProjectUtils.getSources(project)
    val scalaSgs = sources.getSourceGroups(ScalaSourceUtil.SOURCES_TYPE_SCALA)
    val javaSgs  = sources.getSourceGroups(ScalaSourceUtil.SOURCES_TYPE_JAVA)

    Log.info((scalaSgs map (_.getRootFolder)).mkString("project's src group[ScalaType] dir: [", ", ", "]"))
    Log.info((javaSgs  map (_.getRootFolder)).mkString("project's src group[JavaType]  dir: [", ", ", "]"))

    List(scalaSgs, javaSgs) foreach {
      case Array(srcSg) =>
        val src = srcSg.getRootFolder
        val out = findOutDir(project, src)
        cache.srcToOut += (src -> out)

      case Array(srcSg, testSg, _*) =>
        val src = srcSg.getRootFolder
        val out = findOutDir(project, src)
        cache.srcToOut += (src -> out)

        val test = testSg.getRootFolder
        val testOut = findOutDir(project, test)
        cache.testToOut += (test -> testOut)

      case x =>
        // @todo add other srcs
    }
    
    cache
  }

  private def findOutDir(project: Project, srcRoot: FileObject): FileObject = {
    val srcRootUrl: URL =
      try {
        // * make sure the url is in same form of BinaryForSourceQueryImplementation
        FileUtil.toFile(srcRoot).toURI.toURL
      } catch {case ex: MalformedURLException => Exceptions.printStackTrace(ex); null}

    var out: FileObject = null
    val query = project.getLookup.lookup(classOf[BinaryForSourceQueryImplementation])
    if (query != null && srcRootUrl != null) {
      val result = query.findBinaryRoots(srcRootUrl)
      if (result != null) {
        var break = false
        val itr = result.getRoots.iterator
        while (itr.hasNext && !break) {
          val url = itr.next
          if (!FileUtil.isArchiveFile(url)) {
            val uri = try {
              url.toURI
            } catch {case ex: URISyntaxException => Exceptions.printStackTrace(ex); null}

            if (uri != null) {
              val file = new File(uri)
              break =
                if (file != null) {
                  if (file.isDirectory) {
                    out = FileUtil.toFileObject(file)
                    true
                  } else if (file.exists) {
                    false
                  } else {
                    // * global requires an exist out path, so we should create
                    if (file.mkdirs) {
                      out = FileUtil.toFileObject(file)
                      true
                    } else false
                  }
                } else false
            }
          }
        }
      }
    }

    // * global requires an exist out path, so we have to create a tmp folder
    if (out == null) {
      val projectDir = project.getProjectDirectory
      if (projectDir != null && projectDir.isFolder) {
        try {
          val tmpClasses = "tmpClasses"
          out = projectDir.getFileObject(tmpClasses) match {
            case null => projectDir.createFolder(tmpClasses)
            case x => x
          }
        } catch {case ex: IOException => Exceptions.printStackTrace(ex)}
      }
    }

    out
  }

  private def concatClassPath(project: Project, sb: StringBuilder, cp: ClassPath): Unit = {
    if (cp == null) {
      return
    }

    val itr = cp.entries.iterator
    while (itr.hasNext) {
      val rootFile = try {
        val entryRoot = itr.next.getRoot
        if (entryRoot != null) {
          entryRoot.getFileSystem match {
            case jfs: JarFileSystem => jfs.getJarFile
            case _ => FileUtil.toFile(entryRoot)
          }
        } else null
      } catch {case ex:FileStateInvalidException => Exceptions.printStackTrace(ex); null}

      if (rootFile != null) {
        val path = rootFile.getAbsolutePath
        sb.append(path)
        if (itr.hasNext) {
          sb.append(File.pathSeparator)
        }
      }
    }
  }

  private class SrcCpListener(global: ScalaGlobal, srcCp: ClassPath) extends FileChangeAdapter {
    val javaMimeType = "text/x-java"
    val srcRoots = srcCp.getRoots

    private def isUnderSrcDir(fo: FileObject) = {
      srcRoots find {x => FileUtil.isParentOf(x, fo)} isDefined
    }

    override def fileDataCreated(fe: FileEvent): Unit = {
      val fo = fe.getFile
      if (fo.getMIMEType == javaMimeType && isUnderSrcDir(fo) && global != null) {
        global.reporter = dummyReporter
        global askForReLoad List(fo)
      }
    }

    override def fileChanged(fe: FileEvent): Unit = {
      val fo = fe.getFile
      if (fo.getMIMEType == javaMimeType && isUnderSrcDir(fo) && global != null) {
        global.reporter = dummyReporter
        global askForReLoad List(fo)
      }
    }

    override def fileRenamed(fe: FileRenameEvent): Unit = {
      val fo = fe.getFile
      if (fo.getMIMEType == javaMimeType && isUnderSrcDir(fo) && global != null) {
        global.reporter = dummyReporter
        global askForReLoad List(fo)
      }
    }

    override def fileDeleted(fe: FileEvent): Unit = {
      // @todo get the dependency ot just recompile all?
    }
  }

  private class CompCpListener(global: ScalaGlobal, compCp: ClassPath) extends FileChangeAdapter {
    val compRoots = compCp.getRoots

    private def isUnderCompCp(fo: FileObject) = {
      // * when there are series of folder/file created, only top created folder can be listener
      val found = compRoots find {x => FileUtil.isParentOf(fo, x) || x == fo}
      if (found.isDefined) Log.finest("under compCp: fo=" + fo + ", found=" + found)
      found isDefined
    }

    override def fileFolderCreated(fe: FileEvent) {
      val fo = fe.getFile
      if (isUnderCompCp(fo) && global != null) {
        Log.finest("folder created: " + fo)
        resetLate(global, compCpChanged)
      }
    }

    override def fileDataCreated(fe: FileEvent): Unit = {
      val fo = fe.getFile
      if (isUnderCompCp(fo) && global != null) {
        Log.finest("data created: " + fo)
        resetLate(global, compCpChanged)
      }
    }

    override def fileChanged(fe: FileEvent): Unit = {
      val fo = fe.getFile
      if (isUnderCompCp(fo) && global != null) {
        Log.finest("file changed: " + fo)
        resetLate(global, compCpChanged)
      }
    }

    override def fileRenamed(fe: FileRenameEvent): Unit = {
      val fo = fe.getFile
      if (isUnderCompCp(fo) && global != null) {
        Log.finest("file renamed: " + fo)
        resetLate(global, compCpChanged)
      }
    }

    override def fileDeleted(fe: FileEvent): Unit = {
      val fo = fe.getFile
      if (isUnderCompCp(fo) && global != null) {
        Log.finest("file deleted: " + fo)
        resetLate(global, compCpChanged)
      }
    }
  }
}

class ScalaGlobal(settings: Settings, reporter: Reporter) extends Global(settings, reporter)
                                                             with ScalaItems
                                                             with ScalaDfns
                                                             with ScalaRefs
                                                             with ScalaElements
                                                             with JavaElements
                                                             with ScalaCompletionProposals
                                                             with ScalaUtils {
  import ScalaGlobal._
  
  // * Inner object inside a class is not singleton, so it's safe for each instance of ScalaGlobal,
  // * but, is it thread safe? http://lampsvn.epfl.ch/trac/scala/ticket/1591
  private object scalaAstVisitor extends {
    val global: ScalaGlobal.this.type = ScalaGlobal.this
  } with ScalaAstVisitor

  override def onlyPresentation = true

  override def logError(msg: String, t: Throwable): Unit = {}

  def askForReLoad(srcFos: List[FileObject]) : Unit = {
    val srcFiles = srcFos map {fo => getSourceFile(new PlainFile(FileUtil.toFile(fo)))}

    try {
      val resp = new Response[Unit]
      askReload(srcFiles, resp)
      resp.get
    } catch {
      case ex: AssertionError =>
        /**
         * @Note: avoid scala nsc's assert error. Since global's
         * symbol table may have been broken, we have to reset ScalaGlobal
         * to clean this global
         */
        ScalaGlobal.resetLate(this, ex)
      case ex: java.lang.Error => // avoid scala nsc's Error error
      case ex: Throwable => // just ignore all ex
    }
  }

  def askForPresentation(srcFile: SourceFile, th: TokenHierarchy[_]): ScalaRootScope = {
    resetSelectTypeErrors

    val resp = new Response[Tree]
    try {
      askType(srcFile, true, resp)
    } catch {
      case ex: AssertionError =>
        /**
         * @Note: avoid scala nsc's assert error. Since global's
         * symbol table may have been broken, we have to reset ScalaGlobal
         * to clean this global
         */
        ScalaGlobal.resetLate(this, ex)
      case ex: java.lang.Error => // avoid scala nsc's Error error
      case ex: Throwable => // just ignore all ex
    }

    resp.get.left.toOption map {tree =>
      scalaAstVisitor.visit(unitOf(srcFile), th)
    } getOrElse ScalaRootScope.EMPTY
  }

  def askForDebug(srcFile: SourceFile, th: TokenHierarchy[_]): ScalaRootScope = {
    resetSelectTypeErrors

    val resp = new Response[Tree]
    try {
      askLambdaLift(srcFile, true, resp)
    } catch {
      case ex: AssertionError =>
        /**
         * @Note: avoid scala nsc's assert error. Since global's
         * symbol table may have been broken, we have to reset ScalaGlobal
         * to clean this global
         */
        ScalaGlobal.resetLate(this, ex)
      case ex: java.lang.Error => // avoid scala nsc's Error error
      case ex: Throwable => // just ignore all ex
    }

    resp.get.left.toOption map {tree =>
      scalaAstVisitor.visit(unitOf(srcFile), th)
    } getOrElse ScalaRootScope.EMPTY
  }

  /** batch complie */
  def compileSourcesForPresentation(srcFiles: List[FileObject]): Unit = {
    settings.stop.value = Nil
    settings.stop.tryToSetColon(List(superAccessors.phaseName))
    try {
      new this.Run compile (srcFiles map (FileUtil.toFile(_).getAbsolutePath))
    } catch {
      case ex: AssertionError =>
        /**
         * @Note: avoid scala nsc's assert error. Since global's
         * symbol table may have been broken, we have to reset ScalaGlobal
         * to clean this global
         */
        ScalaGlobal.resetLate(this, ex)
      case ex: java.lang.Error => // avoid scala nsc's Error error
      case ex: Throwable => // just ignore all ex
    }
  }

  def compileSourceForPresentation(srcFile: SourceFile, th: TokenHierarchy[_]): ScalaRootScope = {
    compileSource(srcFile, superAccessors.phaseName, th)
  }

  // * @Note Should pass phase "lambdalift" to get anonfun's class symbol built
  def compileSourceForDebug(srcFile: SourceFile, th: TokenHierarchy[_]): ScalaRootScope = {
    compileSource(srcFile, constructors.phaseName, th)
  }

  // * @Note the following setting exlcudes 'stopPhase' itself
  def compileSource(srcFile: SourceFile, stopPhaseName: String, th: TokenHierarchy[_]): ScalaRootScope = synchronized {
    settings.stop.value = Nil
    settings.stop.tryToSetColon(List(stopPhaseName))
    resetSelectTypeErrors

    val run = new this.Run

    val srcFiles = List(srcFile)
    try {
      run.compileSources(srcFiles)
    } catch {
      case ex: AssertionError =>
        /**
         * @Note: avoid scala nsc's assert error. Since global's
         * symbol table may have been broken, we have to reset ScalaGlobal
         * to clean this global
         */
        ScalaGlobal.resetLate(this, ex)
      case ex: java.lang.Error => // avoid scala nsc's Error error
      case ex: Throwable => // just ignore all ex
    }

    //println("selectTypeErrors:" + selectTypeErrors)

    run.units find {_.source == srcFile} map {unit =>
      if (ScalaGlobal.debug) {
        RequestProcessor.getDefault.post(new Runnable {
            def run {
              treeBrowser.browse(unit.body)
            }
          })
      }

      scalaAstVisitor.visit(unit, th)
    } getOrElse ScalaRootScope.EMPTY
  }

  // <editor-fold defaultstate="collapsed" desc="lambdaLift">
  // ----- @lambdaLift Some test code to detect if lambdaLift can apply just after typer, but no success:

  /** A fully lambdaLifted tree corresponding to the entire compilation unit  */
  def lambdaLiftedTree(source: SourceFile, forceReload: Boolean): Tree = {
    val unit = unitOf(source)
    val sources = List(source)
    if (unit.status == NotLoaded || forceReload) reloadSources(sources)
    moveToFront(sources)
    currentTyperRun.typedTree(unitOf(source))
    currentTyperRun.lambdaLiftedTree(unitOf(source))
  }

  /** Set sync var `result` to a fully attributed tree corresponding to the entire compilation unit  */
  def getLambdaLiftedTree(source : SourceFile, forceReload: Boolean, result: Response[Tree]) {
    respond(result)(lambdaLiftedTree(source, forceReload))
  }

  def askLambdaLift(source: SourceFile, forceReload: Boolean, result: Response[Tree]) =
    scheduler postWorkItem new WorkItem {
      def apply() = getLambdaLiftedTree(source, forceReload, result)
      override def toString = "lambdaLift"
    }

  // ----- end @lambdaLift Some test code to detect if lambdaLift can apply just after typer, but no success:
  // </editor-fold>

  // ----- helper methods, patched version from interactive.Global and CompilerControl

  import analyzer.{SearchResult, ImplicitSearch}

  def askTypeCompletion(pos: Position, alternatePos: Position, resultTpe: Type, result: Response[List[Member]]) =
    scheduler postWorkItem new WorkItem {
      def apply() = getTypeCompletion(pos, alternatePos, resultTpe, result)
      override def toString = "type completion "+pos.source+" "+pos.show
    }

  def getTypeCompletion(pos: Position, alternatePos: Position, resultTpe: Type, result: Response[List[Member]]) {
    respond(result) { typeMembers(pos, alternatePos, resultTpe) }
    if (debugIDE) scopeMembers(pos)
  }

  /**
   * When we are doing completion, what we need is an `Ident` or `qualifier` of `Select` tree
   * we'll collect the members of result type of this `Ident` or `qualifier`
   */
  def completionTypeAt(pos: Position, alternatePos: Position): Tree = {
    try {
      typedTreeAt(pos) match {
        case me@Ident(name) => me
        case Select(qualifier, name) => qualifier
        case x =>
          alternatePos match {
            case NoPosition => Log.warning("Got a suspicious completion tree: " + x.getClass.getSimpleName); x
            case _ => completionTypeAt(alternatePos, NoPosition)
          }
      }
    } catch {
      case ex =>
        alternatePos match {
          case NoPosition => EmptyTree
          case _ => completionTypeAt(alternatePos, NoPosition)
        }
    }
  }

  /**
   * @todo: doLocateContext may return none, should fix it
   * from interative.Global#typeMembers
   */
  def typeMembers(apos: Position, alternatePos: Position, resultTpe: Type): List[TypeMember] = {
    // @Note typedTreeAt throws exceptions sometimes, which damages global, we actually has
    // typed unit when askForPresentation, this step is useless, so, just locateTree(alternatePos)
    /* val (pos, tree) = completionTypeAt(apos, alternatePos) match {
     case EmptyTree => return Nil
     case x => (x.pos, x)
     } */

    val (pos, tree) = (alternatePos, locateTree(alternatePos))

    val treeSym = tree.symbol
    val isPackage = treeSym != null && treeSym.isPackage

    val resTpe = resultTpe
    tree.tpe match {
      case null | ErrorType | NoType =>
        println("==== Tree type is null or error, will replace resultTpe with " + resultTpe)
      case x =>
    }
    
    println("typeMembers at " + tree + ", tree class=" + tree.getClass.getSimpleName + ", tpe=" + tree.tpe + ", resultTpe=" + resTpe)
    val context = try {
      doLocateContext(pos)
    } catch {case ex => println(ex.getMessage); NoContext}
    
    val superAccess = tree.isInstanceOf[Super]
    val scope = newScope
    val members = new LinkedHashMap[Symbol, TypeMember]

    def addTypeMember1(sym: Symbol, pre: Type, inherited: Boolean, viaView: Symbol) {
      val symtpe = pre.memberType(sym)
      if (scope.lookupAll(sym.name) forall (sym => !(members(sym).tpe matches symtpe))) {
        scope enter sym
        members(sym) = new TypeMember(
          sym,
          symtpe,
          context.isAccessible(sym, pre, superAccess && (viaView == NoSymbol)),
          inherited,
          viaView)
      }
    }

    def addPackageMember1(sym: Symbol, pre: Type, inherited: Boolean, viaView: Symbol) {
      // * don't ask symtpe here via pre.memberType(sym) or sym.tpe, which may throw "no-symbol does not have owner"
      members(sym) = new TypeMember(
        sym,
        NoPrefix,
        context.isAccessible(sym, pre, false),
        inherited,
        viaView)
    }

    def viewApply1(view: SearchResult): Tree = {
      if (context == null) EmptyTree
      assert(view.tree != EmptyTree)
      try {
        analyzer.newTyper(context.makeImplicit(false)).typed(Apply(view.tree, List(tree)) setPos tree.pos)
      } catch {
        case ex: TypeError => EmptyTree
      }
    }

    val pre = try {
      stabilizedType(tree) match {
        case null => NoPrefix
        case x => x
      }
    } catch {case ex => println(ex.getMessage); NoPrefix}

    if (!isPackage){
      try {
        for (sym <- resTpe.decls)
          addTypeMember1(sym, pre, false, NoSymbol)
      } catch {case ex => println(ex.getMessage)}
    }

    try {
      for (sym <- resTpe.members)
        if (isPackage) {
          addPackageMember1(sym, pre, true, NoSymbol)
        } else {
          addTypeMember1(sym, pre, true, NoSymbol)
        }
    } catch {case ex => println(ex.getMessage)}

    if (!isPackage) {
      try {
        val applicableViews: List[SearchResult] =
          //if (context != NoContext) {
        new ImplicitSearch(tree, definitions.functionType(List(resTpe), definitions.AnyClass.tpe), true, context.makeImplicit(false)).allImplicits
        //} else Nil
        
        for (view <- applicableViews) {
          val vtree = viewApply1(view)
          val vpre = stabilizedType(vtree)
          for (sym <- vtree.tpe.members) {
            addTypeMember1(sym, vpre, false, view.tree.symbol)
          }
        }
      } catch {case ex => println(ex.getMessage)}
    }
    
    members.valuesIterator.toList
  }

  /** Return all members visible without prefix in context enclosing `pos`. */
  override def scopeMembers(pos: Position): List[ScopeMember] = {
    typedTreeAt(pos) // to make sure context is entered
    val context = try {
      doLocateContext(pos)
    } catch {case ex => ex.printStackTrace; NoContext}
    
    val locals = new LinkedHashMap[Name, ScopeMember]

    def addScopeMember1(sym: Symbol, pre: Type, viaImport: Tree) =
      if (!sym.name.decode.containsName(Dollar) &&
          !sym.hasFlag(Flags.SYNTHETIC) &&
          !locals.contains(sym.name)) {
        //println("adding scope member: "+pre+" "+sym)
        val tpe = try {
          pre.memberType(sym)
        } catch {case ex => ex.printStackTrace; NoPrefix}
        
        locals(sym.name) = new ScopeMember(
          sym,
          tpe,
          context.isAccessible(sym, pre, false),
          viaImport)
      }

    var cx = context
    while (cx != NoContext) {
      for (sym <- cx.scope)
        addScopeMember1(sym, NoPrefix, EmptyTree)

      cx = cx.enclMethod
      if (cx != NoContext) {
        for (sym <- cx.scope)
          addScopeMember1(sym, NoPrefix, EmptyTree)
      }

      cx = cx.enclClass
      val pre = cx.prefix
      for (sym <- pre.members)
        addScopeMember1(sym, pre, EmptyTree)
      cx = cx.outer
    }
    for (imp <- context.imports) {
      val pre = imp.qual.tpe
      for (sym <- imp.allImportedSymbols) {
        addScopeMember1(sym, pre, imp.qual)
      }
    }
    val result = locals.valuesIterator.toList
    if (debugIDE) for (m <- result) println(m)
    result
  }

  /**
   * In interactive.Global, the `newRunnerThread` always waits for `scheduler.waitForMoreWork()`
   * before `pollForWork()`, which may cause raised `except`s never have chance to be polled, if
   * there is no more `WorkItem` in `todo` queue, so I have to post another Action to awake it.
   * @Ticket #2289
   */
  override def askShutdown() = {
    scheduler.raise(new ShutdownReq)
    scheduler postWorkItem {() => println("A action to awake scheduler to process shutdown except")}
  }

  override def askReset() = {
    scheduler.raise(new FreshRunReq)
    scheduler postWorkItem {() => println("A action to awake scheduler to process reset except")}
  }

}
