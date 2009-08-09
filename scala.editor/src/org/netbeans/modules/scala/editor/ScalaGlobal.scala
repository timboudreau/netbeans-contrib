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
import _root_.java.util.WeakHashMap
import org.netbeans.api.java.classpath.ClassPath
import org.netbeans.api.java.queries.BinaryForSourceQuery
import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy}
import org.netbeans.api.project.{FileOwnerQuery, Project, ProjectUtils, SourceGroup}
import org.netbeans.modules.csl.api.ElementKind
import org.netbeans.spi.java.classpath.ClassPathProvider
import org.netbeans.spi.java.queries.BinaryForSourceQueryImplementation
import org.openide.filesystems.{FileChangeAdapter, FileEvent, FileObject, FileRenameEvent,
                                FileStateInvalidException, FileSystem, FileUtil, JarFileSystem}
import org.openide.util.{Exceptions, RequestProcessor}

import org.netbeans.api.language.util.ast.{AstScope}
import org.netbeans.modules.scala.editor.ast.{ScalaDfns, ScalaRefs, ScalaRootScope, ScalaAstVisitor, ScalaUtils}
import org.netbeans.modules.scala.editor.element.{ScalaElements}

import scala.tools.nsc.{Phase, Settings}
import scala.tools.nsc.interactive.Global
import scala.tools.nsc.symtab.{SymbolTable}
import scala.tools.nsc.util.BatchSourceFile
import scala.tools.nsc.io.AbstractFile

/**
 *
 * @author Caoyuan Deng
 */
object ScalaGlobal {
  private class SrcOutDirs {
    var srcOutDirs: Map[FileObject, FileObject] = Map()
    var testSrcOutDirs: Map[FileObject, FileObject] = Map()

    def scalaSrcOutDirs = toScalaDirs(srcOutDirs)
    def scalaTestSrcOutDirs = toScalaDirs(testSrcOutDirs)

    private def toScalaDirs(dirs: Map[FileObject, FileObject]): Map[AbstractFile, AbstractFile] = {
      for ((src, out) <- dirs) yield (toScalaDir(src), toScalaDir(out))
    }

    private def toScalaDir(fo: FileObject) = AbstractFile.getDirectory(FileUtil.toFile(fo))
  }

  private val debug = false

  // @see org.netbeans.api.java.project.JavaProjectConstants
  private val SOURCES_TYPE_JAVA = "java" // NOI18N
  // a source group type for separate scala source roots, as seen in maven projects for example.
  private val SOURCES_TYPE_SCALA = "scala" //NOI18N

  private val ProjectToDirs = new WeakHashMap[Project, Reference[SrcOutDirs]]
  private val ProjectToGlobal = new WeakHashMap[Project, Reference[ScalaGlobal]]
  private val ProjectToGlobalForTest = new WeakHashMap[Project, Reference[ScalaGlobal]]
  private var GlobalForStdLib: Option[ScalaGlobal] = None

  def resetAll {
    ProjectToGlobal.clear
    GlobalForStdLib = None
  }

  def reset(global: Global) {
    ProjectToGlobal.remove(global)
    if (global == GlobalForStdLib) GlobalForStdLib = None
  }

  /**
   * Scala's global is not thread safed
   *
   * @Todo: it seems scala's Settings only support one source path, i.e.
   * "/scalaproject/src" only, does not support "/scalaproject/src:/scalaproject/src2"
   * since we can not gaurantee the srcCp returns only one entry, we have to use
   * following guessing method:
   */
  def getGlobal(fo: FileObject): ScalaGlobal = synchronized {
    val project = FileOwnerQuery.getOwner(fo)
    if (project == null) {
      // it may be a standalone file, or file in standard lib
      return GlobalForStdLib match {
        case None =>
          val g = ScalaHome.getGlobalForStdLib
          GlobalForStdLib = Some(g)
          g
        case Some(x) => x
      }
    }

    val dirs = ProjectToDirs.get(project) match {
      case null =>
        val dirsx = findDirResouces(project)
        ProjectToDirs.put(project, new WeakReference(dirsx))
        dirsx
      case ref =>
        ref.get match {
          case null =>
            val dirsx = findDirResouces(project)
            ProjectToDirs.put(project, new WeakReference(dirsx))
            dirsx
          case x => x
        }
    }

    // * is this `fo` under test source?
    val forTest = dirs.testSrcOutDirs.find{case (src, _) => src.equals(fo) || FileUtil.isParentOf(src, fo)}.isDefined

    // * Do not use srcCp as the key, different fo under same src dir seems returning diff instance of srcCp
    val globalRef = if (forTest) ProjectToGlobalForTest.get(project) else ProjectToGlobal.get(project)
    if (globalRef != null) {
      globalRef.get match {
        case null =>
        case global => return global
      }
    }

    val settings = new Settings
    if (debug) {
      settings.debug.value = true
      settings.verbose.value = true
    } else {
      settings.verbose.value = false
    }

    for ((src, out) <- if (forTest) dirs.scalaTestSrcOutDirs else dirs.scalaSrcOutDirs) {
      settings.outputDirs.add(src, out)
    }

    // * add boot, compile classpath
    val cpp = project.getLookup.lookup(classOf[ClassPathProvider])
    var (bootCp, compCp): (ClassPath, ClassPath) = if (cpp != null) {
      (cpp.findClassPath(fo, ClassPath.BOOT), cpp.findClassPath(fo, ClassPath.COMPILE))
    } else (null, null)

    var inStdLib = false
    if (bootCp == null || compCp == null) {
      // * in case of fo in standard libaray
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

    val global = new ScalaGlobal(settings)

    if (forTest) {
      ProjectToGlobalForTest.put(project, new WeakReference(global))
      var visited = Set[FileObject]()
      for ((src, out) <- dirs.testSrcOutDirs if !visited.contains(out)) {
        out.addFileChangeListener(new FileChangeAdapter {

            override def fileChanged(fe: FileEvent): Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileRenamed(fe: FileRenameEvent): Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileDeleted(fe: FileEvent): Unit = {
              // * maybe a clean task invoked
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
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
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileRenamed(fe: FileRenameEvent): Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileDeleted(fe: FileEvent): Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToDirs.remove(project)
            }
          })

        visited += out
      }
    } else {
      ProjectToGlobal.put(project, new WeakReference(global))
      var visited = Set[FileObject]()
      for ((src, out) <- dirs.srcOutDirs if !visited.contains(out)) {
        out.addFileChangeListener(new FileChangeAdapter {

            override def fileChanged(fe: FileEvent): Unit = {
              ProjectToGlobal.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileRenamed(fe: FileRenameEvent): Unit = {
              ProjectToGlobal.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileDeleted(fe: FileEvent): Unit = {
              // maybe a clean task invoked
              ProjectToGlobal.remove(project)
              ProjectToDirs.remove(project)
            }
          })
        visited += out
      }
    }
    
    global
  }

  private def findDirResouces(project: Project): SrcOutDirs = {
    val dirs = new SrcOutDirs

    val sgs = ProjectUtils.getSources(project).getSourceGroups(SOURCES_TYPE_SCALA) match {
      case Array() =>
        // * found none, as a fallback use java ones ..
        ProjectUtils.getSources(project).getSourceGroups(SOURCES_TYPE_JAVA)
      case x => x
    }

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
    dirs
  }

  private def findOutDir(project: Project, srcRoot: FileObject): FileObject = {
    val srcRootUrl: URL = try {
      // make sure the url is in same form of BinaryForSourceQueryImplementation
      FileUtil.toFile(srcRoot).toURI.toURL
    } catch {
      case ex: MalformedURLException => Exceptions.printStackTrace(ex); null
    }

    var out: FileObject = null
    val query = project.getLookup.lookup(classOf[BinaryForSourceQueryImplementation])
    if (query != null && srcRootUrl != null) {
      val result = query.findBinaryRoots(srcRootUrl)
      if (result != null) {
        var break = false
        for (url <- result.getRoots if !break && !FileUtil.isArchiveFile(url)) {
          val uri: URI = try {
            url.toURI
          } catch {
            case ex: URISyntaxException => Exceptions.printStackTrace(ex); null
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
          case ex: IOException => Exceptions.printStackTrace(ex)
        }
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
      val rootFile: File = try {
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

            override def fileChanged(fe: FileEvent): Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToGlobal.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileRenamed(fe: FileRenameEvent): Unit = {
              ProjectToGlobalForTest.remove(project)
              ProjectToGlobal.remove(project)
              ProjectToDirs.remove(project)
            }

            override def fileDeleted(fe: FileEvent): Unit = {
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

class ScalaGlobal(settings: Settings) extends Global(settings, null)
with ScalaDfns
with ScalaRefs
with ScalaElements
with ScalaCompletionProposals
with ScalaUtils {

  // * Inner object inside a class is not singleton, so it's safe for each instance of ScalaGlobal,
  // * but, is it thread safe? http://lampsvn.epfl.ch/trac/scala/ticket/1591
  private object scalaAstVisitor extends {
    val global: ScalaGlobal.this.type = ScalaGlobal.this
  } with ScalaAstVisitor

  override def onlyPresentation = false

  override def logError(msg: String, t: Throwable): Unit = {}

  def compileSourceForPresentation(srcFile: BatchSourceFile, th: TokenHierarchy[_]): ScalaRootScope = {
    compileSource(srcFile, superAccessors.phaseName, th)
  }

  // * @Note Should pass phase "lambdalift" to get anonfun's class symbol built
  def compileSourceForDebugger(srcFile: BatchSourceFile, th: TokenHierarchy[_]): ScalaRootScope = {
    compileSource(srcFile, constructors.phaseName, th)
  }

  // * @Note the following setting exlcudes 'stopPhase' itself
  def compileSource(srcFile: BatchSourceFile, stopPhaseName: String, th: TokenHierarchy[_]): ScalaRootScope = synchronized {
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
        ScalaGlobal.reset(this)
      case ex: _root_.java.lang.Error => // avoid scala nsc's Error error
      case ex: Throwable => // just ignore all ex
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
                def run: Unit = {
                  treeBrowser.browse(unit.body)
                }
              })
          }
          
          return scalaAstVisitor.visit(unit, th)
        case _ =>
      }
    }

    ScalaRootScope.EMPTY
  }
}
