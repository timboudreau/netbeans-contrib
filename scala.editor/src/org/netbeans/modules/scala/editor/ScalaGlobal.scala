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

import _root_.java.io.{File, IOException}
import _root_.java.lang.ref.{Reference, WeakReference}
import _root_.java.net.{MalformedURLException, URI, URISyntaxException, URL}
import _root_.java.util.{Iterator, Map, WeakHashMap}
import org.netbeans.api.java.classpath.ClassPath
import org.netbeans.api.java.queries.BinaryForSourceQuery
import org.netbeans.api.lexer.{TokenHierarchy}
import org.netbeans.api.project.{FileOwnerQuery, Project, ProjectUtils, SourceGroup}
import org.netbeans.spi.java.classpath.ClassPathProvider
import org.netbeans.spi.java.queries.BinaryForSourceQueryImplementation
import org.netbeans.modules.scala.editor.ast.{ScalaAstVisitor, ScalaRootScope}
import org.openide.filesystems.{FileChangeAdapter,
                                FileEvent,
                                FileObject,
                                FileRenameEvent,
                                FileStateInvalidException,
                                FileSystem,
                                FileUtil,
                                JarFileSystem}
import org.openide.util.{Exceptions, RequestProcessor}
import _root_.scala.tools.nsc.{Global,Settings}
import _root_.scala.tools.nsc.symtab.{SymbolTable}
import _root_.scala.tools.nsc.util.BatchSourceFile

/**
 *
 * @author Caoyuan Deng
 */
object ScalaGlobal {
  private class SrcOutDirs {
    var srcDir :FileObject = _
    var outDir :FileObject = _
    var testSrcDir :FileObject = _
    var testOutDir :FileObject = _
  }

  private val debug = false

  // @see org.netbeans.api.java.project.JavaProjectConstants
  private val SOURCES_TYPE_JAVA = "java" // NOI18N
  // a source group type for separate scala source roots, as seen in maven projects for example.
  private val SOURCES_TYPE_SCALA = "scala" //NOI18N

  private val ProjectToDirs = new WeakHashMap[Project, Reference[SrcOutDirs]]
  private val ProjectToGlobal = new WeakHashMap[Project, Reference[ScalaGlobal]]
  private val ProjectToGlobalForTest = new WeakHashMap[Project, Reference[ScalaGlobal]]
  private var GlobalForStdLid :Option[ScalaGlobal] = None

  def reset {
    ProjectToGlobal.clear
    GlobalForStdLid = None
  }


  /**
   * Scala's global is not thread safed
   *
   * @Todo: it seems scala's Settings only support one source path, i.e.
   * "/scalaproject/src" only, does not support "/scalaproject/src:/scalaproject/src2"
   * since we can not gaurantee the srcCp returns only one entry, we have to use
   * following guessing method:
   */
  def getGlobal(fo:FileObject) :ScalaGlobal = synchronized {
    val project = FileOwnerQuery.getOwner(fo)
    if (project == null) {
      // it may be a standalone file, or file in standard lib
      return GlobalForStdLid match {
        case None =>
          val g = ScalaHome.getGlobalForStdLib
          GlobalForStdLid = Some(g)
          g
        case Some(x) => x
      }
    }

    val dirs = ProjectToDirs.get(project) match {
      case null =>
        val dirsx = findDirsInfo(project);
        ProjectToDirs.put(project, new WeakReference(dirsx))
        dirsx
      case ref => ref.get
    }

    // is fo under test source?
    val forTest = if (dirs.testSrcDir != null && (dirs.testSrcDir.equals(fo) ||
                                                  FileUtil.isParentOf(dirs.testSrcDir, fo))) {
      true
    } else {
      false
    }

    // Do not use srcCp as the key, different fo under same src dir seems returning diff instance of srcCp
    val globalRef = if (forTest) ProjectToGlobalForTest.get(project) else ProjectToGlobal.get(project)
    if (globalRef != null) {
      globalRef.get match {
        case null =>
        case global => return global
      }
    }

    val (srcPath, outPath) = if (forTest) {
      (if (dirs.testSrcDir == null) "" else FileUtil.toFile(dirs.testSrcDir).getAbsolutePath,
       if (dirs.testOutDir == null) "" else FileUtil.toFile(dirs.testOutDir).getAbsolutePath)
    } else {
      (if (dirs.srcDir == null) "" else FileUtil.toFile(dirs.srcDir).getAbsolutePath,
       if (dirs.outDir == null) "" else FileUtil.toFile(dirs.outDir).getAbsolutePath)
    }

    val settings = new Settings
    if (debug) {
      settings.debug.value = true
      settings.verbose.value = true
    } else {
      settings.verbose.value = false
    }

    settings.sourcepath.tryToSet(List(srcPath))
    //settings.outdir().tryToSet(scala.netbeans.Wrapper$.MODULE$.stringList(new String[]{"-d", outPath}));
    settings.outputDirs.setSingleOutput(outPath)

    // add boot, compile classpath
    val cpp = project.getLookup.lookup(classOf[ClassPathProvider])
    var (bootCp, compCp) :(ClassPath, ClassPath) = if (cpp != null) {
      (cpp.findClassPath(fo, ClassPath.BOOT), cpp.findClassPath(fo, ClassPath.COMPILE))
    } else (null, null)

    var inStdLib = false
    if (bootCp == null || compCp == null) {
      // in case of fo in standard libaray
      inStdLib = true
      bootCp = ClassPath.getClassPath(fo, ClassPath.BOOT)
      compCp = ClassPath.getClassPath(fo, ClassPath.COMPILE)
    }

    val sb = new StringBuilder
    computeClassPath(project, sb, bootCp)
    settings.bootclasspath.tryToSet(List(sb.toString))

    sb.delete(0, sb.length)
    computeClassPath(project, sb, compCp)
    if (forTest && !inStdLib && dirs.outDir != null) {
      sb.append(File.pathSeparator).append(dirs.outDir)
    }
    settings.classpath.tryToSet(List(sb.toString))

    val global = new ScalaGlobal(settings)

    if (forTest) {
      ProjectToGlobalForTest.put(project, new WeakReference[ScalaGlobal](global))
      if (dirs.testOutDir != null) {
        dirs.testOutDir.addFileChangeListener(new FileChangeAdapter {

            override def fileChanged(fe:FileEvent) :Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileRenamed(fe:FileRenameEvent) :Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileDeleted(fe:FileEvent) :Unit = {
              // maybe a clean task invoked
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
            }
          })
      }

      if (dirs.outDir != null) {
        // monitor outDir's changes,
        /** @Todo should reset global for any changes under out dir, including subdirs */
        dirs.outDir.addFileChangeListener(new FileChangeAdapter {

            override def fileChanged(fe:FileEvent) :Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileRenamed(fe:FileRenameEvent) :Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileDeleted(fe:FileEvent) :Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
            }
          })
      }
    } else {
      ProjectToGlobal.put(project, new WeakReference(global))
      if (dirs.outDir != null) {
        dirs.outDir.addFileChangeListener(new FileChangeAdapter {

            override def fileChanged(fe:FileEvent) :Unit = {
              ProjectToGlobal.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileRenamed(fe:FileRenameEvent) :Unit = {
              ProjectToGlobal.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileDeleted(fe:FileEvent) :Unit = {
              // maybe a clean task invoked
              ProjectToGlobal.remove(project)
              ProjectToDirs.remove(project)
            }
          })
      }
    }
    
    global
  }

  private def findDirsInfo(project:Project) :SrcOutDirs = {
    val dirs = new SrcOutDirs

    val sgs = ProjectUtils.getSources(project).getSourceGroups(SOURCES_TYPE_SCALA) match {
      case Array() =>
        // as a fallback use java ones..
        ProjectUtils.getSources(project).getSourceGroups(SOURCES_TYPE_JAVA)
      case x => x
    }

    sgs match {
      case Array(src) =>
        dirs.srcDir = src.getRootFolder
        dirs.outDir = findOutDir(project, dirs.srcDir)
      case Array(src, test, _*) =>
        dirs.srcDir = src.getRootFolder
        dirs.outDir = findOutDir(project, dirs.srcDir)
        dirs.testSrcDir = test.getRootFolder
        dirs.testOutDir = findOutDir(project, dirs.testSrcDir)
      case _ =>
    }

    dirs
  }

  private def findOutDir(project:Project, srcRoot:FileObject) :FileObject = {
    val srcRootUrl:URL = try {
      // make sure the url is in same form of BinaryForSourceQueryImplementation
      FileUtil.toFile(srcRoot).toURI.toURL
    } catch {
      case ex:MalformedURLException => Exceptions.printStackTrace(ex); null
    }

    var out :FileObject = null
    val query = project.getLookup.lookup(classOf[BinaryForSourceQueryImplementation])
    if (query != null && srcRootUrl != null) {
      val result = query.findBinaryRoots(srcRootUrl)
      if (result != null) {
        var break = false
        for (url <- result.getRoots if !break && !FileUtil.isArchiveFile(url)) {
          val uri :URI = try {
            url.toURI
          } catch {
            case ex:URISyntaxException => Exceptions.printStackTrace(ex); null
          }

          if (url != null) {
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
        } catch {
          case ex:IOException => Exceptions.printStackTrace(ex)
        }
      }
    }

    out
  }

  private def computeClassPath(project:Project, sb:StringBuilder, cp:ClassPath) :Unit = {
    if (cp == null) {
      return
    }

    val itr = cp.entries.iterator
    while (itr.hasNext) {
      val rootFile :File = try {
        val entryRoot = itr.next.getRoot
        if (entryRoot != null) {
          entryRoot.getFileSystem match {
            case jfs:JarFileSystem => jfs.getJarFile
            case _ => FileUtil.toFile(entryRoot)
          }
        } else null
      } catch {
        case ex:FileStateInvalidException => Exceptions.printStackTrace(ex); null
      }

      if (rootFile != null) {
        FileUtil.toFileObject(rootFile).addFileChangeListener(new FileChangeAdapter {

            override def fileChanged(fe:FileEvent) :Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToGlobal.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileRenamed(fe:FileRenameEvent) :Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToGlobal.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileDeleted(fe:FileEvent) :Unit = {
              // maybe a clean task invoked
              ProjectToGlobalForTest.remove(project)
              ProjectToGlobal.remove(project)
              ProjectToDirs.remove(project)
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

class ScalaGlobal(settings:Settings) extends Global(settings) {

  // * Inner object inside a class is not singleton, so it's safe for each instance of ScalaGlobal,
  // * but, is it thread safe? http://lampsvn.epfl.ch/trac/scala/ticket/1591
  private object scalaAstVisitor extends {
    val trees :ScalaGlobal.this.type = ScalaGlobal.this
  } with ScalaAstVisitor

  override def onlyPresentation = false

  override def logError(msg:String, t:Throwable) :Unit = {}

  def compileSourceForPresentation(srcFile:BatchSourceFile, th:TokenHierarchy[_]) :ScalaRootScope = {
    compileSource(srcFile, Phase.superaccessors, th)
  }

  // * @Note Should pass phase "lambdalift" to get anonfun's class symbol built, the following setting exlcudes 'stopPhase'
  def compileSourceForDebugger(srcFile:BatchSourceFile, th:TokenHierarchy[_]) :ScalaRootScope = {
    compileSource(srcFile, Phase.constructors, th)
  }

  def compileSource(srcFile:BatchSourceFile, stopPhase:Phase, th:TokenHierarchy[_]) :ScalaRootScope = synchronized {
    settings.stop.value = Nil
    settings.stop.tryToSetColon(List(stopPhase.name))
    resetSelectTypeErrors
    val run = new this.Run

    val srcFiles = List(srcFile)
    try {
      run.compileSources(srcFiles)
    } catch {
      case ex:AssertionError =>
        /**@Note: avoid scala nsc's assert error. Since global's
         * symbol table may have been broken, we have to reset ScalaGlobal
         * to clean this global
         */
        ScalaGlobal.reset
      case ex:_root_.java.lang.Error => // avoid scala nsc's Error error
      case ex:Throwable => // just ignore all ex
    }

    if (ScalaGlobal.debug) {
      println("selectTypeErrors:" + selectTypeErrors)
    }

    val units = run.units
    while (units.hasNext) {
      units.next match {
        case unit if (unit.source == srcFile) =>
          if (ScalaGlobal.debug) {
            RequestProcessor.getDefault.post(new Runnable {
                def run :Unit = {
                  treeBrowser.browse(unit.body)
                }
              })
          }
          
          return scalaAstVisitor.visit(unit, th)
        case _ =>
      }
    }

    ScalaRootScope(Array())
  }
}

abstract class Phase(val name:String)
object Phase {
  case object parser extends Phase("parser")
  case object namer extends Phase("name")
  case object typer extends Phase("typer")
  case object superaccessors extends Phase("superaccessors")
  case object pickler extends Phase("pickler")
  case object refchecks extends Phase("refchecks")
  case object liftcode extends Phase("liftcode")
  case object uncurry extends Phase("uncurry")
  case object tailcalls extends Phase("tailcalls")
  case object explicitouter extends Phase("explicitouter")
  case object erasure extends Phase("erasure")
  case object lazyvals extends Phase("lazyvals")
  case object lambdalift extends Phase("lambdalift")
  case object constructors extends Phase("constructors")
  case object flatten extends Phase("flatten")
  case object mixin extends Phase("mixin")
  case object cleanup extends Phase("cleanup")
  case object icode extends Phase("icode")
  case object inliner extends Phase("inliner")
  case object closelim extends Phase("closelim")
  case object dce extends Phase("dce")
  case object jvm extends Phase("jvm")
}
