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

import java.io.{File, IOException}
import java.lang.ref.{Reference, WeakReference}
import java.net.{MalformedURLException, URI, URISyntaxException, URL}
import java.util.WeakHashMap
import org.netbeans.api.java.classpath.ClassPath
import org.netbeans.api.java.queries.BinaryForSourceQuery
import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy}
import org.netbeans.api.project.{FileOwnerQuery, Project, ProjectUtils, Sources, SourceGroup}
import org.netbeans.modules.csl.api.ElementKind
import org.netbeans.spi.java.classpath.ClassPathProvider
import org.netbeans.spi.java.queries.BinaryForSourceQueryImplementation
import org.openide.filesystems.{FileChangeAdapter, FileEvent, FileObject, FileRenameEvent,
                                FileStateInvalidException, FileSystem, FileUtil, JarFileSystem}
import org.openide.util.{Exceptions, RequestProcessor}

import org.netbeans.api.language.util.ast.{AstScope}
import org.netbeans.modules.scala.editor.ast.{ScalaDfns, ScalaRefs, ScalaRootScope, ScalaAstVisitor, ScalaUtils}
import org.netbeans.modules.scala.editor.element.{ScalaElements, JavaElements}

import scala.collection.mutable.ArrayBuffer

import scala.tools.nsc.{Phase, Settings}
import scala.collection.mutable.LinkedHashMap

import scala.tools.nsc.interactive.Global
import scala.tools.nsc.symtab.{SymbolTable, Flags}
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.io.PlainFile
import scala.tools.nsc.reporters.{Reporter}
import scala.tools.nsc.util.{Position, SourceFile}

/**
 *
 * @author Caoyuan Deng
 */
object ScalaGlobal {
  
  private class SrcOutDirs {
    var srcOutDirs: Map[FileObject, FileObject] = Map()
    var testSrcOutDirs: Map[FileObject, FileObject] = Map()

    def srcOutDirsPath = toDirPaths(srcOutDirs)
    def testSrcOutDirsPath = toDirPaths(testSrcOutDirs)

    def scalaSrcOutDirs: Map[AbstractFile, AbstractFile] = toScalaDirs(srcOutDirs)
    def scalaTestSrcOutDirs: Map[AbstractFile, AbstractFile] = toScalaDirs(testSrcOutDirs)

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

  // @see org.netbeans.api.java.project.JavaProjectConstants
  private val SOURCES_TYPE_JAVA = "java" // NOI18N
  // * a source group type for separate scala source roots, as seen in maven projects for example.
  private val SOURCES_TYPE_SCALA = "scala" //NOI18N

  private val projectToDirs = new WeakHashMap[Project, SrcOutDirs]

  private val projectToGlobal = new WeakHashMap[Project, ScalaGlobal]
  private val projectToGlobalForTest = new WeakHashMap[Project, ScalaGlobal]

  // * for interative.Global, seems need another global for debug
  private val projectToGlobalForDebug = new WeakHashMap[Project, ScalaGlobal]
  private val projectToGlobalForTestDebug = new WeakHashMap[Project, ScalaGlobal]

  private var globalForStdLib: Option[ScalaGlobal] = None
  
  private var toResetGlobals = Set[ScalaGlobal]()

  val dummyReporter = new Reporter {def info0(pos: Position, msg: String, severity: Severity, force: Boolean) {}}

  def resetAll = synchronized {
    val itr1 = projectToGlobal.values.iterator
    while (itr1.hasNext) {
      itr1.next.askShutdown
    }
    projectToGlobal.clear

    val itr2 = projectToGlobalForTest.values.iterator
    while (itr2.hasNext) {
      itr2.next.askShutdown
    }
    projectToGlobalForTest.clear

    globalForStdLib foreach {_.askShutdown}
    globalForStdLib = None
  }

  def resetLate(global: ScalaGlobal, reason: Throwable) = synchronized {
    println("=== will reset global late due to: \n" + reason.getMessage)

    toResetGlobals += global
    if (globalForStdLib.isDefined && global == globalForStdLib.get) {
      globalForStdLib = None
    } else {
      projectToGlobal.values.remove(global)
      projectToGlobalForTest.values.remove(global)
      projectToGlobalForDebug.values.remove(global)
      projectToGlobalForTestDebug.values.remove(global)
    }
  }

  /**
   * @Note
   * Tried to use to reset global instead of create a new one, but for current scala Global,
   * it seems reset operation cannot got a clean global?
   */
  def resetBadGlobals = synchronized {
    for (global <- toResetGlobals) {
      println("Reset global: " + global)

      // * this will cause global create a new TypeRun so as to release all unitbuf and filebuf
      global.askReset

      // * try to stop compiler daemon thread, but, does this method work ?
      global.askShutdown

      // * whatever, force global to clear whole unitOfFile
      global.unitOfFile.clear
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
      return globalForStdLib match {
        case None =>
          val g = ScalaHome.getGlobalForStdLib
          globalForStdLib = Some(g)
          g
        case Some(x) => x
      }
    }

    val dirs = projectToDirs.get(project) match {
      case null =>
        val dirsx = findDirResources(project)
        projectToDirs.put(project, dirsx)
        dirsx
      case x => x
    }

    // * is this `fo` under test source?
    val forTest = dirs.testSrcOutDirs.find{case (src, _) => src.equals(fo) || FileUtil.isParentOf(src, fo)}.isDefined

    // * Do not use `srcCp` as the key, different `fo` under same src dir seems returning diff instance of srcCp
    val holder = if (forDebug) {
      if (forTest) projectToGlobalForTestDebug else projectToGlobalForDebug
    } else {
      if (forTest) projectToGlobalForTest else projectToGlobal
    }
    holder.get(project) match {
      case null =>
      case g => return g
    }

    val settings = new Settings
    if (debug) {
      settings.debug.value = true
      settings.verbose.value = true
    } else {
      settings.debug.value = false
      settings.verbose.value = false
    }

    var outPath = ""
    var srcPaths: List[String] = Nil
    for ((src, out) <- if (forTest) dirs.testSrcOutDirsPath else dirs.srcOutDirsPath) {
      srcPaths = src :: srcPaths
      if (outPath == "") {
        outPath = out
      }
    }
    settings.sourcepath.tryToSet(srcPaths.reverse)
    settings.outputDirs.setSingleOutput(outPath)

    /** @Note: settings.outputDirs.add(src, out) seems cannot resolve symbols in other source files, why? */
    /*_
     for ((src, out) <- if (forTest) dirs.scalaTestSrcOutDirs else dirs.scalaSrcOutDirs) {
     settings.outputDirs.add(src, out)
     }
     */

    // * add boot, compile classpath
    val cpp = project.getLookup.lookup(classOf[ClassPathProvider])
    var (bootCp, compCp, srcCp): (ClassPath, ClassPath, ClassPath) = if (cpp != null) {
      (cpp.findClassPath(fo, ClassPath.BOOT), 
       cpp.findClassPath(fo, ClassPath.COMPILE),
       cpp.findClassPath(fo, ClassPath.SOURCE))
    } else (null, null, null)

    if (srcCp != null) println("project's srcDir: [" + srcCp.getRoots.mkString(", ") + "]") else println("project's srcDir is empty !")

    var inStdLib = false
    if (bootCp == null || compCp == null) {
      // * in case of `fo` in standard libaray
      inStdLib = true
      bootCp = ClassPath.getClassPath(fo, ClassPath.BOOT)
      compCp = ClassPath.getClassPath(fo, ClassPath.COMPILE)
    }

    val sb = new StringBuilder
    computeClassPath(project, sb, bootCp)
    settings.bootclasspath.tryToSet(List(sb.toString))

    sb.delete(0, sb.length)
    computeClassPath(project, sb, compCp)
    if (forTest && !inStdLib) {
      var visited = Set[FileObject]()
      for ((src, out) <- dirs.srcOutDirs if !visited.contains(out)) {
        sb.append(File.pathSeparator).append(out)
        visited += out
      }
    }
    settings.classpath.tryToSet(List(sb.toString))

    val global = new ScalaGlobal(settings, dummyReporter)

    if (forTest) {
      (if (forDebug) projectToGlobalForTestDebug else projectToGlobalForTest).put(project, global)
      var visited = Set[FileObject]()
      for ((src, out) <- dirs.testSrcOutDirs if !visited.contains(out)) {
        out.addFileChangeListener(new FileChangeAdapter {

            override def fileChanged(fe: FileEvent): Unit = {
              projectToGlobalForTest.remove(project)
              projectToDirs.remove(project)
            }

            override def fileRenamed(fe: FileRenameEvent): Unit = {
              projectToGlobalForTest.remove(project)
              projectToDirs.remove(project)
            }

            override def fileDeleted(fe: FileEvent): Unit = {
              // * maybe a clean task invoked
              projectToGlobalForTest.remove(project)
              projectToDirs.remove(project)
            }
          })

        visited += out
      }

      visited = Set[FileObject]()
      for ((src, out) <- dirs.srcOutDirs if !visited.contains(out)) {
        // * monitor outDir's changes,
        /** @Todo should reset global for any changes under out dir, including subdirs */
        out.addFileChangeListener(new FileChangeAdapter {

            override def fileChanged(fe: FileEvent): Unit = {
              projectToGlobalForTest.remove(project)
              projectToDirs.remove(project)
            }

            override def fileRenamed(fe: FileRenameEvent): Unit = {
              projectToGlobalForTest.remove(project)
              projectToDirs.remove(project)
            }

            override def fileDeleted(fe: FileEvent): Unit = {
              projectToGlobalForTest.remove(project)
              projectToDirs.remove(project)
            }
          })

        visited += out
      }
    } else {
      (if (forDebug) projectToGlobalForDebug else projectToGlobal).put(project, global)
      var visited = Set[FileObject]()
      for ((src, out) <- dirs.srcOutDirs if !visited.contains(out)) {
        out.addFileChangeListener(new FileChangeAdapter {

            override def fileChanged(fe: FileEvent): Unit = {
              projectToGlobal.remove(project)
              projectToDirs.remove(project)
            }

            override def fileRenamed(fe: FileRenameEvent): Unit = {
              projectToGlobal.remove(project)
              projectToDirs.remove(project)
            }

            override def fileDeleted(fe: FileEvent): Unit = {
              // maybe a clean task invoked
              projectToGlobal.remove(project)
              projectToDirs.remove(project)
            }
          })
        
        visited += out
      }
    }

    if (!forDebug) {
      initialLoadSources(global, project, srcCp)
    }

    global
  }

  private def initialLoadSources(global: ScalaGlobal, project: Project, srcCp: ClassPath) {
    if (srcCp != null) {
      // * we have to do following step to get mixed java sources visible to scala sources

      project.getProjectDirectory.getFileSystem.addFileChangeListener(new FileChangeAdapter {
          val srcRoots = srcCp.getRoots

          private def isUnderSrcDir(fo: FileObject) = {
            srcRoots.find{x => FileUtil.isParentOf(x, fo)}.isDefined
          }

          override def fileDataCreated(fe: FileEvent): Unit = {
            val fo = fe.getFile
            if (fo.getMIMEType == "text/x-java" && isUnderSrcDir(fo)) {
              global.reporter = dummyReporter
              global.askForReLoad(List(fo))
              //global.compileSourcesForPresentation(List(fo))
            }
          }

          override def fileChanged(fe: FileEvent): Unit = {
            val fo = fe.getFile
            if (fo.getMIMEType == "text/x-java" && isUnderSrcDir(fo)) {
              global.reporter = dummyReporter
              global.askForReLoad(List(fo))
              //global.compileSourcesForPresentation(List(fo))
            }
          }

          override def fileRenamed(fe: FileRenameEvent): Unit = {
            val fo = fe.getFile
            if (fo.getMIMEType == "text/x-java" && isUnderSrcDir(fo)) {
              global.reporter = dummyReporter
              global.askForReLoad(List(fo))
              //global.compileSourcesForPresentation(List(fo))
            }
          }

          override def fileDeleted(fe: FileEvent): Unit = {
            // @todo recompile all?
          }
        })


      // * should push java srcs before scala srcs
      val javaSrcs = new ArrayBuffer[FileObject]
      srcCp.getRoots foreach {x => findAllSourcesOf("text/x-java", x, javaSrcs)}

      val scalaSrcs = new ArrayBuffer[FileObject]
      // * it seems only java src files need to be pushed explicitly ?
      //srcCp.getRoots foreach {x => findAllSourcesOf("text/x-scala", x, scalaSrcs)}

      // * the reporter should be set, otherwise, no java source is resolved, maybe throws exception already.
      global.reporter = dummyReporter
      global.askForReLoad((javaSrcs ++ scalaSrcs).toList)
      //global.compileSourcesForPresentation((javaSrcs ++ scalaSrcs).toList)
    }
  }

  private def findAllSourcesOf(mimeType: String, dirFo: FileObject, result: ArrayBuffer[FileObject]): Unit = {
    dirFo.getChildren foreach {x =>
      if (x.isFolder) {
        findAllSourcesOf(mimeType, x, result)
      } else if (x.getMIMEType == mimeType) {
        result += x
      }
    }
  }

  private def findDirResources(project: Project): SrcOutDirs = {
    val dirs = new SrcOutDirs

    val sources = ProjectUtils.getSources(project)
    val scalaSgs = sources.getSourceGroups(SOURCES_TYPE_SCALA)
    val javaSgs = sources.getSourceGroups(SOURCES_TYPE_JAVA)

    println("project's src group[Scala] dir: [" + scalaSgs.map{_.getRootFolder}.mkString(", ") + "]")
    println("project's src group[Java] dir: [" + javaSgs.map{_.getRootFolder}.mkString(", ") + "]")

    List(scalaSgs, javaSgs) foreach {sgs =>
      if (sgs.size > 0) {
        val src = sgs(0).getRootFolder
        val out = findOutDir(project, src)
        dirs.srcOutDirs += (src -> out)
      }

      if (sgs.size > 1) { // the 2nd one is test src
        val src = sgs(1).getRootFolder
        val out = findOutDir(project, src)
        dirs.testSrcOutDirs += (src -> out)
      }

      // @todo add other srcs
    }
    
    dirs
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
            val uri: URI = try {
              url.toURI
            } catch {case ex: URISyntaxException => Exceptions.printStackTrace(ex); null}

            if (uri != null) {
              val file = new File(uri)
              break = if (file != null) {
                if (file.isDirectory) {
                  out = FileUtil.toFileObject(file)
                  true
                } else if (file.exists) {
                  false
                } else {
                  // global requires an exist out path, so we should create
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

    // global requires an exist out path, so we have to create a tmp folder
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

  private def computeClassPath(project: Project, sb: StringBuilder, cp: ClassPath): Unit = {
    if (cp == null) {
      return
    }

    val itr = cp.entries.iterator
    while (itr.hasNext) {
      val rootFile: File =
        try {
          val entryRoot = itr.next.getRoot
          if (entryRoot != null) {
            entryRoot.getFileSystem match {
              case jfs: JarFileSystem => jfs.getJarFile
              case _ => FileUtil.toFile(entryRoot)
            }
          } else null
        } catch {case ex:FileStateInvalidException => Exceptions.printStackTrace(ex); null}

      if (rootFile != null) {
        FileUtil.toFileObject(rootFile).addFileChangeListener(new FileChangeAdapter {

            override def fileChanged(fe: FileEvent): Unit = {
              projectToGlobalForTest.remove(project)
              projectToGlobal.remove(project)
              projectToDirs.remove(project)
            }

            override def fileRenamed(fe: FileRenameEvent): Unit = {
              projectToGlobalForTest.remove(project)
              projectToGlobal.remove(project)
              projectToDirs.remove(project)
            }

            override def fileDeleted(fe: FileEvent): Unit = {
              // maybe a clean task invoked
              projectToGlobalForTest.remove(project)
              projectToGlobal.remove(project)
              projectToDirs.remove(project)
            }
          })

        val path = rootFile.getAbsolutePath
        sb.append(path)
        if (itr.hasNext) {
          sb.append(File.pathSeparator)
        }
      }
    }
  }

}

class ScalaGlobal(settings: Settings, reporter: Reporter) extends Global(settings, reporter)
                                                             with ScalaDfns
                                                             with ScalaRefs
                                                             with ScalaElements
                                                             with JavaElements
                                                             with ScalaCompletionProposals
                                                             with ScalaUtils {

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

  def askForPresentation(srcFile: SourceFile, th: TokenHierarchy[_]) : ScalaRootScope = {
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
      scalaAstVisitor.visit(tree, srcFile, th)
    } getOrElse ScalaRootScope.EMPTY
  }

  /** batch complie */
  def compileSourcesForPresentation(srcFiles: List[FileObject]): Unit = {
    settings.stop.value = Nil
    settings.stop.tryToSetColon(List(superAccessors.phaseName))
    try {
      new this.Run compile (srcFiles map {FileUtil.toFile(_).getAbsolutePath})
    } catch {
      case ex: AssertionError =>
        /**
         * @Note: avoid scala nsc's assert error. Since global's
         * symbol table may have been broken, we have to reset ScalaGlobal
         * to clean this global
         */
        ScalaGlobal.resetLate(this, ex)
      case ex: _root_.java.lang.Error => // avoid scala nsc's Error error
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

    run.units.find{_.source == srcFile} map {unit =>
      if (ScalaGlobal.debug) {
        RequestProcessor.getDefault.post(new Runnable {
            def run {
              treeBrowser.browse(unit.body)
            }
          })
      }

      scalaAstVisitor.visit(unit.body, unit.source, th)
    } getOrElse ScalaRootScope.EMPTY
  }


  // --- helper methods, patched version from interactive.Global and CompilerControl

  import analyzer.{SearchResult, ImplicitSearch}

  def askTypeCompletion(pos: Position, resultTpe: Type, result: Response[List[Member]]) =
    scheduler postWorkItem new WorkItem {
      def apply() = getTypeCompletion(pos, resultTpe, result)
      override def toString = "type completion "+pos.source+" "+pos.show
    }

  def getTypeCompletion(pos: Position, resultTpe: Type, result: Response[List[Member]]) {
    respond(result) { typeMembers(pos, resultTpe) }
    if (debugIDE) scopeMembers(pos)
  }

  /** 
   * @todo: doLocateContext may return none, should fix it
   * from interative.Global#typeMembers
   */
  def typeMembers(apos: Position, resultTpe: Type): List[TypeMember] = {
    val (pos, tree) = typedTreeAt(apos) match {
      case Select(qualifier, name) => (qualifier.pos, qualifier)
      case treex => (apos, treex)
    }

    val treeSym = tree.symbol
    val isPackage = treeSym != null && treeSym.isPackage

    val resTpe = resultTpe
    /* val resTpe = tree.tpe match {
     case null | ErrorType | NoType =>
     println("will replace resultTpe " + resultTpe)
     resultTpe
     case x => x.resultType
     } */
    
    println("typeMembers at " + tree + ", tree class=" + tree.getClass.getSimpleName + ", tpe=" + tree.tpe + ", resType=" + resTpe)
    val context = try {
      doLocateContext(pos)
    } catch {case ex => println(ex.getMessage); null}
    
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
          if (context == null) true else context.isAccessible(sym, pre, superAccess && (viaView == NoSymbol)),
          inherited,
          viaView)
      }
    }

    def addPackageMember1(sym: Symbol, pre: Type, inherited: Boolean, viaView: Symbol) {
      // * don't ask symtpe here via pre.memberType(sym) or sym.tpe, which may throw "no-symbol does not have owner"
      members(sym) = new TypeMember(
        sym,
        null,
        true,
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

    val pre = stabilizedType(tree) match {
      case null => resTpe
      case x => x
    }

    if (!isPackage){
      try {
        for (sym <- resTpe.decls)
          addTypeMember1(sym, pre, false, NoSymbol)
      } catch {case ex => ex.printStackTrace}
    }

    try {
      for (sym <- resTpe.members)
        if (isPackage) {
          addPackageMember1(sym, pre, true, NoSymbol)
        } else {
          addTypeMember1(sym, pre, true, NoSymbol)
        }
    } catch {case ex => ex.printStackTrace}

    if (!isPackage) {
      try {
        val applicableViews: List[SearchResult] =
          if (context != null) {
            new ImplicitSearch(tree, definitions.functionType(List(resTpe), definitions.AnyClass.tpe), true, context.makeImplicit(false)).allImplicits
          } else Nil
        
        for (view <- applicableViews) {
          val vtree = viewApply1(view)
          val vpre = stabilizedType(vtree)
          for (sym <- vtree.tpe.members) {
            addTypeMember1(sym, vpre, false, view.tree.symbol)
          }
        }
      } catch {case ex => ex.printStackTrace}
    }
    
    members.valuesIterator.toList
  }

  /** Return all members visible without prefix in context enclosing `pos`. */
  override def scopeMembers(pos: Position): List[ScopeMember] = {
    typedTreeAt(pos) // to make sure context is entered
    val context = try {
      doLocateContext(pos)
    } catch {case ex => ex.printStackTrace; null}
    
    if (context == null) {
      return Nil
    }

    val locals = new LinkedHashMap[Name, ScopeMember]

    def addScopeMember1(sym: Symbol, pre: Type, viaImport: Tree) =
      if (!sym.name.decode.containsName(Dollar) &&
          !sym.hasFlag(Flags.SYNTHETIC) &&
          !locals.contains(sym.name)) {
        //println("adding scope member: "+pre+" "+sym)
        val tpe = try {
          pre.memberType(sym)
        } catch {case ex => ex.printStackTrace; null}
        
        locals(sym.name) = new ScopeMember(
          sym,
          tpe,
          if (context == null) true else context.isAccessible(sym, pre, false),
          viaImport)
      }

    var cx = context
    while (cx != NoContext) {
      for (sym <- cx.scope)
        addScopeMember1(sym, NoPrefix, EmptyTree)
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
    scheduler postWorkItem {() => println("A action to awake scheduler to process raised except")}
  }

  override def askReset() = {
    scheduler.raise(new FreshRunReq)
    scheduler postWorkItem {() => println("A action to awake scheduler to process raised except")}
  }

}
