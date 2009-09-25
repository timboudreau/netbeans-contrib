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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.scala.editor

import java.io.{File, IOException}
import java.net.URL
import java.util.logging.Logger
import javax.swing.text.BadLocationException
import javax.swing.text.StyledDocument
import org.netbeans.api.java.classpath.ClassPath
import org.netbeans.api.java.classpath.GlobalPathRegistry
import org.netbeans.api.java.queries.SourceForBinaryQuery
import org.netbeans.api.java.source.ClasspathInfo
import org.netbeans.api.lexer.TokenHierarchy
import org.netbeans.api.project.Project
import org.netbeans.api.project.ProjectUtils
import org.netbeans.api.project.SourceGroup
import org.netbeans.editor.BaseDocument
import org.netbeans.modules.classfile.ClassFile
import org.netbeans.modules.csl.api.{ElementKind, OffsetRange}
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.parsing.api.{ParserManager, ResultIterator, Source, UserTask}
import org.netbeans.modules.parsing.impl.indexing.friendapi.IndexingController
import org.netbeans.modules.parsing.spi.{ParseException, Parser}
import org.netbeans.spi.java.classpath.support.ClassPathSupport
import org.openide.filesystems.{FileObject, FileUtil}
import org.openide.text.NbDocument
import org.openide.util.{Exceptions}

import org.netbeans.api.language.util.ast.{AstDfn, AstScope}
import org.netbeans.modules.scala.editor.ast.{ScalaDfns}
import org.netbeans.modules.scala.editor.element.{JavaElements}
import org.netbeans.modules.scala.editor.lexer.ScalaLexUtil

import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import scala.tools.nsc.symtab.{Flags, Symbols}
import scala.collection.mutable.ArrayBuffer

/**
 *
 * @author Caoyuan Deng
 */
object ScalaSourceUtil {

  val Log = Logger.getLogger(getClass.getName)

  /** @see org.netbeans.api.java.project.JavaProjectConstants */
  val SOURCES_TYPE_JAVA = "java" // NOI18N
  /** a source group type for separate scala source roots, as seen in maven projects for example */
  val SOURCES_TYPE_SCALA = "scala" //NOI18N

  def isScalaFile(f: FileObject): Boolean = {
    ScalaMimeResolver.MIME_TYPE.equals(f.getMIMEType)
  }

  /** Includes things you'd want selected as a unit when double clicking in the editor */
  def isIdentifierChar(c: Char): Boolean = {
    c match {
      case '$' | '@' | '&' | ':' | '!' | '?' | '=' => true // Function name suffixes
      case _ if Character.isJavaIdentifierPart(c) => true // Globals, fields and parameter prefixes (for blocks and symbols)
      case _ => false
    }
  }

  /** Includes things you'd want selected as a unit when double clicking in the editor */
  def isStrictIdentifierChar(c: Char): Boolean = {
    c match {
      case '!' | '?' | '=' => true
      case _ if Character.isJavaIdentifierPart(c) => true
      case _ => false
    }
  }

  @throws(classOf[BadLocationException])
  def isRowWhite(text: String, offset: Int): Boolean = {
    try {
      // Search forwards
      var break = false
      for (i <- offset until text.length if !break) {
        text.charAt(i) match {
          case '\n' => break = true
          case c if !Character.isWhitespace(c) => return false
          case _ =>
        }
      }
    
      // Search backwards
      break = false
      for (i <- offset - 1 to 0 if !break) {
        text.charAt(i) match {
          case '\n' => break = true
          case c if !Character.isWhitespace(c) => return false
          case _ =>
        }
      }

      true
    } catch {
      case ex: Exception =>
        val ble = new BadLocationException(offset + " out of " + text.length, offset)
        ble.initCause(ex)
        throw ble
    }
  }

  @throws(classOf[BadLocationException])
  def isRowEmpty(text: String, offset: Int): Boolean = {
    try {
      if (offset < text.length) {
        text.charAt(offset) match {
          case '\n' =>
          case '\r' if offset == text.length - 1 || text.charAt(offset + 1) == '\n' =>
          case _ => return false
        }
      }

      if (!(offset == 0 || text.charAt(offset - 1) == '\n')) {
        // There's previous stuff on this line
        return false
      }

      true
    } catch {
      case ex: Exception =>
        val ble = new BadLocationException(offset + " out of " + text.length, offset)
        ble.initCause(ex)
        throw ble
    }
  }

  @throws(classOf[BadLocationException])
  def getRowLastNonWhite(text: String, offset: Int): Int = {
    try {
      // Find end of line
      var i = offset
      var break = false
      while (i < text.length && !break) {
        text.charAt(i) match {
          case '\n' => break = true
          case '\r' if i == text.length() - 1 || text.charAt(i + 1) == '\n' => break = true
          case _ => i += 1
        }
      }
      // Search backwards to find last nonspace char from offset
      i -= 1
      while (i >= 0) {
        text.charAt(i) match {
          case '\n' => return -1
          case c if !Character.isWhitespace(c) => return i
          case _ => i -= 1
        }
      }

      -1
    } catch {
      case ex:Exception =>
        val ble = new BadLocationException(offset + " out of " + text.length, offset)
        ble.initCause(ex)
        throw ble
    }
  }

  @throws(classOf[BadLocationException])
  def getRowFirstNonWhite(text: String, offset: Int): Int = {
    try {
      // Find start of line
      var i = offset - 1
      if (i < text.length) {
        var break = false
        while (i >= 0 && !(text.charAt(i) == '\n')) {
          i -= 1
        }
        i += 1
      }
      // Search forwards to find first nonspace char from offset
      while (i < text.length) {
        text.charAt(i) match {
          case '\n' => return - 1
          case c if !Character.isWhitespace(c) => return i
          case _ => i += 1
        }
      }

      -1
    } catch {
      case ex:Exception =>
        val ble = new BadLocationException(offset + " out of " + text.length, offset)
        ble.initCause(ex)
        throw ble
    }
  }

  @throws(classOf[BadLocationException])
  def getRowStart(text: String, offset: Int): Int = {
    try {
      // Search backwards
      for (i <- offset - 1 to 0) {
        text.charAt(i) match {
          case '\n' => return i + 1
          case _ =>
        }
      }

      0
    } catch {
      case ex: Exception =>
        val ble = new BadLocationException(offset + " out of " + text.length, offset)
        ble.initCause(ex)
        throw ble
    }
  }

  def endsWith(sb: StringBuilder, s: String): Boolean = {
    val len = s.length

    if (sb.length < len) {
      return false
    }

    var i = sb.length - len
    var j = 0
    while (j < len) {
      if (sb.charAt(i) != s.charAt(j)) {
        return false
      } else {
        i += 1
        j += 1
      }
    }

    true
  }

  def truncate(s: String, length: Int): String = {
    assert(length > 3) // Not for short strings

    if (s.length <= length) {
      s
    } else {
      s.substring(0, length - 3) + "..."
    }
  }

  def computeLinesOffset(source: String): Seq[Int] = {
    val length = source.length

    val linesOffset = new ArrayBuffer[Int](length / 25)
    linesOffset += 0

    var line = 0
    for (i <- 0 until length) {
      if (source.charAt(i) == '\n') {
        // \r comes first so are not a problem...
        linesOffset += i
        line += 1
      }
    }

    linesOffset
  }

  /** @todo */
  def getDocComment(pr: Parser.Result, element: JavaElements#JavaElement): String = {
    if (pr == null) {
      return null
    }

    val doc = pr.getSnapshot.getSource.getDocument(true) match {
      case null => return null
      case x: BaseDocument => x
    }

    val th = pr.getSnapshot.getTokenHierarchy

    //doc.readLock // Read-lock due to token hierarchy use
    val offset = 0//element.getBoundsOffset(th)
    val range = ScalaLexUtil.getDocumentationRange(th, offset)
    //doc.readUnlock

    if (range.getEnd < doc.getLength) {
      try {
        return doc.getText(range.getStart, range.getLength)
      } catch {
        case ex:BadLocationException => Exceptions.printStackTrace(ex)
      }
    }

    null
  }

  def getDocComment(doc: BaseDocument, symbolOffset: Int): String = {
    val th = TokenHierarchy.get(doc) match {
      case null => return ""
      case x => x
    }

    //doc.readLock // Read-lock due to token hierarchy use
    val range = ScalaLexUtil.getDocCommentRangeBefore(th, symbolOffset)
    //doc.readUnlock

    if (range != OffsetRange.NONE && range.getEnd < doc.getLength) {
      try {
        return doc.getText(range.getStart, range.getLength)
      } catch {
        case ex: BadLocationException => Exceptions.printStackTrace(ex)
      }
    }

    ""
  }

  /** @todo */
  def getOffset(pr: Parser.Result, element: JavaElements#JavaElement): Int = {
    if (pr == null) {
      return -1
    }

    val th = pr.getSnapshot.getTokenHierarchy
    //element.getPickOffset(th)
    return -1
  }

  def getFileObject(pr: ParserResult, sym: Symbols#Symbol): Option[FileObject] = {
    val pos = sym.pos
    if (pos.isDefined) {
      val srcFile = pos.source
      if (srcFile != null) {
        var srcPath = srcFile.path
        // * Check the strange behavior of Scala's compiler, which may omit the beginning File.separator ("/")
        if (!srcPath.startsWith(File.separator)) {
          srcPath = File.separator + srcPath
        }
        val file = new File(srcPath)
        if (file != null && file.exists) {
          // * it's a real file instead of an archive file
          return Some(FileUtil.toFileObject(file))
        }
      }
    }

    val qName: String = try {
      sym.enclClass.fullNameString.replace('.', File.separatorChar)
    } catch {
      case ex: java.lang.Error => null
        // java.lang.Error: no-symbol does not have owner
        //        at scala.tools.nsc.symtab.Symbols$NoSymbol$.owner(Symbols.scala:1565)
        //        at scala.tools.nsc.symtab.Symbols$Symbol.fullNameString(Symbols.scala:1156)
        //        at scala.tools.nsc.symtab.Symbols$Symbol.fullNameString(Symbols.scala:1166)
    }

    if (qName == null) {
      return None
    }

    val pkgName = qName.lastIndexOf(File.separatorChar) match {
      case -1 => null
      case i => qName.substring(0, i)
    }

    val clzName = qName + ".class"

    try {
      val cp = getClassPath(pr.getSnapshot.getSource.getFileObject)

      val clzFo = cp.findResource(clzName)
      val root = cp.findOwnerRoot(clzFo)
      val ext = if (sym hasFlag Flags.JAVA) ".java" else ".scala"

      // * see if we can find this class's source file straightforward
      findSourceFileObject(cp, root, qName + ext) match {
        case None =>
        case x => return x
      }

      var srcPath = if (clzFo != null) {
        val in = clzFo.getInputStream
        try {
          new ClassFile(in, false) match {
            case null => null
            case clzFile =>
              clzFile.getSourceFileName
          }
        } finally {if (in != null) in.close}
      } else null

      if (srcPath != null) {
        if (pkgName != null) {
          srcPath = pkgName + File.separatorChar + srcPath
        }

        return findSourceFileObject(cp, root, srcPath)
      }
    } catch {case ex: IOException => ex.printStackTrace}

    None
  }

  def findSourceFileObject(cp: ClassPath, root: FileObject, srcPath: String): Option[FileObject] = {
    if (root == null) return None
    
    val srcRoots = SourceForBinaryQuery.findSourceRoots(root.getURL).getRoots
    val srcCp = ClassPathSupport.createClassPath(srcRoots: _*)

    srcCp.findResource(srcPath) match {
      case null => None
      case x => return Some(x)
    }
  }

  def getScalaJavaSourceGroups(p: Project): Array[SourceGroup] = {
    val sources = ProjectUtils.getSources(p)
    val scalaSgs = sources.getSourceGroups(ScalaSourceUtil.SOURCES_TYPE_SCALA)
    val javaSgs  = sources.getSourceGroups(ScalaSourceUtil.SOURCES_TYPE_JAVA)
    scalaSgs ++ javaSgs
  }

  /** @see org.netbeans.api.java.source.SourceUtils#getDependentRoots */
  def getDependentRoots(root: URL): Set[URL] = {
    val deps = IndexingController.getDefault.getRootDependencies
    getDependentRootsImpl(root, deps)
  }

  private def getDependentRootsImpl(root: URL, deps: java.util.Map[URL, java.util.List[URL]]): Set[URL] = {
    // * create inverse dependencies
    val inverseDeps = new HashMap[URL, ArrayBuffer[URL]]
    val entries = deps.entrySet.iterator
    while (entries.hasNext) {
      val entry = entries.next
      val u1 = entry.getKey
      val l1 = entry.getValue.iterator
      while (l1.hasNext) {
        val u2 = l1.next
        val l2 = inverseDeps.get(u2) getOrElse {
          val x = new ArrayBuffer[URL]
          inverseDeps += (u2 -> x)
          x
        }
        
        l2 += u1
      }
    }

    // * collect dependencies
    val result = new HashSet[URL]
    var todo = List(root)
    while (!todo.isEmpty) {
      todo match {
        case Nil =>
        case url :: xs =>
          todo = xs
          if (result.add(url)) {
            inverseDeps.get(url) foreach {x => todo = x.toList ::: todo}
          }
      }
    }

    // * filter non opened projects
    val cps = GlobalPathRegistry.getDefault.getPaths(ClassPath.SOURCE).iterator
    val toRetain = new HashSet[URL]
    while (cps.hasNext) {
      val cp = cps.next
      val entries = cp.entries.iterator
      while (entries.hasNext) {
        val e = entries.next
        toRetain += e.getURL
      }
    }

    result retain toRetain
    result.toSet
  }


  private val TMPL_KINDS = Set(ElementKind.CLASS, ElementKind.MODULE)

  def getBinaryClassName(pr: ScalaParserResult, lineNumber: Int): String = {
    val global = pr.global
    import global._

    val root = pr.rootScope.getOrElse(return null)
    val fo = pr.getSnapshot.getSource.getFileObject
    val doc = pr.getSnapshot.getSource.getDocument(false).asInstanceOf[StyledDocument]
    val th = pr.getSnapshot.getTokenHierarchy
    val offset = NbDocument.findLineOffset(doc, lineNumber - 1)

    var clazzName = ""
    root.enclosingDfn(TMPL_KINDS, th, offset) foreach {case enclDfn: ScalaDfn =>
        val sym = enclDfn.symbol
        // "scalarun.Dog.$talk$1"
        val fqn = new StringBuilder(sym.fullNameString('.'))

        // * getTopLevelClassName "scalarun.Dog"
        val topSym = sym.toplevelClass
        val topClzName = topSym.fullNameString('.')

        // "scalarun.Dog$$talk$1"
        for (i <- topClzName.length until fqn.length if fqn.charAt(i) == '.') {
          fqn.setCharAt(i, '$')
        }

        // * According to Symbol#kindString, an object template isModuleClass()
        // * trait's symbol name has been added "$class" by compiler
        if (topSym.isModuleClass) {
          fqn.append("$")
        }
        clazzName = fqn.toString
    }

    if (clazzName.length == 0) return null

    val out = ScalaGlobal.getOutFileObject(fo).getOrElse(return clazzName)

    def findAllClassFilesWith(prefix: String, dirFo: FileObject, result: ArrayBuffer[FileObject]): Unit = {
      dirFo.getChildren foreach {
        case x if x.isFolder => findAllClassFilesWith(prefix, x, result)
        case x if x.getExt == "class" && FileUtil.getRelativePath(out, x).startsWith(prefix) => result += x
        case _ =>
      }
    }

    val pathPrefix = clazzName.replace('.', File.separatorChar)
    Log.info("Class prefix: " + pathPrefix + ", out dir: " + out)
    val potentialClasses = new ArrayBuffer[FileObject]
    findAllClassFilesWith(pathPrefix, out, potentialClasses)
    for (clazzFo <- potentialClasses) {
      val in = clazzFo.getInputStream
      try {
        val clazzBin = new ClassFile(in, true)
        if (clazzBin != null) {
          val itr = clazzBin.getMethods.iterator
          while (itr.hasNext) {
            val method = itr.next
            val code = method.getCode
            if (code != null) {
              Log.info("LineNumbers: " + code.getLineNumberTable.mkString("[", ",", "]"))
              if (code.getLineNumberTable find {_ == lineNumber} isDefined) {
                clazzName = FileUtil.getRelativePath(out, clazzFo).replace(File.separatorChar, '.')
                clazzName = clazzName.lastIndexOf(".class") match {
                  case -1 => clazzName
                  case i => clazzName.substring(0, i)
                }
                Log.info("Found binary class name: " + clazzName)
                return clazzName
              }
            }
          }
        }
      } finally {if (in != null) in.close}
    }

    clazzName
  }

  /** @deprecated */
  def getBinaryClassName_old(pr: ScalaParserResult, offset: Int): String = {
    val root = pr.getRootScopeForDebug.getOrElse(return null)
    val th = pr.getSnapshot.getTokenHierarchy
    
    var clzName = ""
    root.enclosingDfn(TMPL_KINDS, th, offset) foreach {enclDfn =>
      val sym = enclDfn.asInstanceOf[ScalaDfns#ScalaDfn].symbol
      if (sym != null) {
        // "scalarun.Dog.$talk$1"
        val fqn = new StringBuilder(sym.fullNameString('.'))

        // * getTopLevelClassName "scalarun.Dog"
        val topSym = sym.toplevelClass
        val topClzName = topSym.fullNameString('.')

        // "scalarun.Dog$$talk$1"
        for (i <- topClzName.length until fqn.length) {
          if (fqn.charAt(i) == '.') {
            fqn.setCharAt(i, '$')
          }
        }

        // * According to Symbol#kindString, an object template isModuleClass()
        // * trait's symbol name has been added "$class" by compiler
        if (topSym.isModuleClass) {
          fqn.append("$")
        }
        clzName = fqn.toString
      }
    }

    if (clzName.length == 0) {
      clzName = null
    }

    //        AstDfn tmpl = rootScope.getEnclosinDef(ElementKind.CLASS, th, offset);
    //        if (tmpl == null) {
    //            tmpl = rootScope.getEnclosinDef(ElementKind.MODULE, th, offset);
    //        }
    //        if (tmpl == null) {
    //            ErrorManager.getDefault().log(ErrorManager.WARNING,
    //                    "No enclosing class for " + pResult.getSnapshot().getSource().getFileObject() + ", offset = " + offset);
    //        }
    //
    //        String className = tmpl.getBinaryName();
    //
    //        String enclosingPackage = tmpl.getPackageName();
    //        if (enclosingPackage == null || enclosingPackage != null && enclosingPackage.length() == 0) {
    //            result[0] = className;
    //        } else {
    //            result[0] = enclosingPackage + "." + className;
    //        }
    clzName
  }

  /**
   * Returns classes declared in the given source file which have the main method.
   * @param fo source file
   * @return the classes containing main method
   * @throws IllegalArgumentException when file does not exist or is not a java source file.
   */
  def getMainClasses(fo: FileObject): Seq[ScalaDfns#ScalaDfn] = {
    if (fo == null || !fo.isValid || fo.isVirtual) {
      throw new IllegalArgumentException
    }
    val source = Source.create(fo) match {
      case null => throw new IllegalArgumentException
      case x => x
    }
    try {
      val result = new ArrayBuffer[ScalaDfns#ScalaDfn]
      ParserManager.parse(java.util.Collections.singleton(source), new UserTask {
          @throws(classOf[Exception])
          override def run(ri: ResultIterator): Unit = {
            val pr = ri.getParserResult.asInstanceOf[ScalaParserResult]
            val root = pr.rootScope match {
              case None => return
              case Some(x) => x
            }
            val global = pr.global
            import global._
            
            def getAllDfns(scope: AstScope, kind: ElementKind, result: ArrayBuffer[ScalaDfn]): Seq[ScalaDfn] = {
              scope.dfns foreach {dfn =>
                if (dfn.getKind == kind)  result += dfn.asInstanceOf[ScalaDfn]
              }
              scope.subScopes foreach {
                childScope => getAllDfns(childScope, kind, result)
              }
              result
            }

            // * get all dfns will return all visible packages from the root and down
            getAllDfns(root, ElementKind.PACKAGE, new ArrayBuffer[ScalaDfn]) foreach {
              // * only go through the defs for each package scope.
              // * Sub-packages are handled by the fact that
              // * getAllDefs will find them.
              packaging => packaging.bindingScope.dfns foreach {dfn =>
                if (isMainMethodExists(dfn.asInstanceOf[ScalaDfn])) result += dfn.asInstanceOf[ScalaDfn]
              }
            }
            
            root.visibleDfns(ElementKind.MODULE) foreach {dfn =>
              if (isMainMethodExists(dfn.asInstanceOf[ScalaDfn])) result += dfn.asInstanceOf[ScalaDfn]
            }
          }

        })

      result
    } catch {
      case ex: ParseException => Exceptions.printStackTrace(ex); Nil
    }
  }

  def getMainClassesAsJavaCollection(fo: FileObject): java.util.Collection[AstDfn] = {
    val result = new java.util.ArrayList[AstDfn]
    getMainClasses(fo) foreach {result.add(_)}
    result
  }

  /**
   * Returns classes declared under the given source roots which have the main method.
   * @param sourceRoots the source roots
   * @return the classes containing the main methods
   * Currently this method is not optimized and may be slow
   */
  def getMainClassesAsJavaCollection(sourceRoots: Array[FileObject]): java.util.Collection[AstDfn] = {
    val result = new java.util.ArrayList[AstDfn]
    for (root <- sourceRoots) {
      result.addAll(getMainClassesAsJavaCollection(root))
      try {
        val bootCp = ClassPath.getClassPath(root, ClassPath.BOOT)
        val compCp = ClassPath.getClassPath(root, ClassPath.COMPILE)
        val srcCp = ClassPathSupport.createClassPath(Array(root): _*)
        val cpInfo = ClasspathInfo.create(bootCp, compCp, srcCp)
        //                final Set<AstElement> classes = cpInfo.getClassIndex().getDeclaredTypes("", ClassIndex.NameKind.PREFIX, EnumSet.of(ClassIndex.SearchScope.SOURCE));
        //                Source js = Source.create(cpInfo);
        //                js.runUserActionTask(new CancellableTask<CompilationController>() {
        //
        //                    public void cancel() {
        //                    }
        //
        //                    public void run(CompilationController control) throws Exception {
        //                        for (AstElement cls : classes) {
        //                            TypeElement te = cls.resolve(control);
        //                            if (te != null) {
        //                                Iterable<? extends ExecutableElement> methods = ElementFilter.methodsIn(te.getEnclosedElements());
        //                                for (ExecutableElement method : methods) {
        //                                    if (isMainMethod(method)) {
        //                                        if (isIncluded(cls, control.getClasspathInfo())) {
        //                                            result.add(cls);
        //                                        }
        //                                        break;
        //                                    }
        //                                }
        //                            }
        //                        }
        //                    }
        //                }, false);
        result
      } catch {case ioe: Exception =>
          Exceptions.printStackTrace(ioe)
          return java.util.Collections.emptySet[AstDfn]
      }
    }
    result
  }

  
  def isMainMethodExists(obj: ScalaDfns#ScalaDfn): Boolean = {
    obj.symbol.tpe.members exists {member => member.isMethod && isMainMethod(member)}
  }

  /**
   * Returns true if the method is a main method
   * @param method to be checked
   * @return true when the method is a main method
   */
  def isMainMethod(method: Symbols#Symbol): Boolean = {
    (method.nameString, method.tpe.paramTypes) match {
      case ("main", List(x)) => true  //NOI18N
      case _ => false
    }
  }

  /**
   * Returns classes declared under the given source roots which have the main method.
   * @param sourceRoots the source roots
   * @return the classes containing the main methods
   * Currently this method is not optimized and may be slow
   */
  def getMainClasses(sourceRoots: Array[FileObject]): Seq[ScalaDfns#ScalaDfn] = {
    val result = new ArrayBuffer[ScalaDfns#ScalaDfn]
    for (root <- sourceRoots) {
      result ++= getMainClasses(root)
      try {
        val bootCp = ClassPath.getClassPath(root, ClassPath.BOOT)
        val compCp = ClassPath.getClassPath(root, ClassPath.COMPILE)
        val srcCp = ClassPathSupport.createClassPath(Array(root): _*)
        val cpInfo = ClasspathInfo.create(bootCp, compCp, srcCp)
        //                final Set<JavaElement> classes = cpInfo.getClassIndex().getDeclaredTypes("", ClassIndex.NameKind.PREFIX, EnumSet.of(ClassIndex.SearchScope.SOURCE));
        //                Source js = Source.create(cpInfo);
        //                js.runUserActionTask(new CancellableTask<CompilationController>() {
        //
        //                    public void cancel() {
        //                    }
        //
        //                    public void run(CompilationController control) throws Exception {
        //                        for (JavaElement cls:  classes) {
        //                            TypeElement te = cls.resolve(control);
        //                            if (te != null) {
        //                                Iterable<? extends ExecutableElement> methods = ElementFilter.methodsIn(te.getEnclosedElements());
        //                                for (ExecutableElement method:  methods) {
        //                                    if (isMainMethod(method)) {
        //                                        if (isIncluded(cls, control.getClasspathInfo())) {
        //                                            result.add(cls);
        //                                        }
        //                                        break;
        //                                    }
        //                                }
        //                            }
        //                        }
        //                    }
        //                }, false);
        cpInfo
      } catch {case ioe: Exception => Exceptions.printStackTrace(ioe); Nil}
    }

    result
  }

  def getClasspathInfo(fo: FileObject): Option[ClasspathInfo] = {
    val bootCp = ClassPath.getClassPath(fo, ClassPath.BOOT)
    val compCp = ClassPath.getClassPath(fo, ClassPath.COMPILE)
    val srcCp = ClassPath.getClassPath(fo, ClassPath.SOURCE)

    if (bootCp == null || compCp == null || srcCp == null) {
      /** @todo why? at least I saw this happens on "Scala project created from existing sources" */
      println("No classpath for " + fo)
      None
    } else {
      ClasspathInfo.create(bootCp, compCp, srcCp) match {
        case null => None
        case x => Some(x)
      }
    }
  }

  def getClassPath(fo: FileObject) = {
    val bootCp = ClassPath.getClassPath(fo, ClassPath.BOOT)
    val compCp = ClassPath.getClassPath(fo, ClassPath.COMPILE)
    val srcCp = ClassPath.getClassPath(fo, ClassPath.SOURCE)
    ClassPathSupport.createProxyClassPath(Array(bootCp, compCp, srcCp): _*)
  }

  /** What's difference from getClassPath(fo: FileObject) ? */
  def getClassPath2(fo: FileObject) = {
    val cpInfo = ClasspathInfo.create(fo)
    val bootCp = cpInfo.getClassPath(ClasspathInfo.PathKind.BOOT)
    val compCp = cpInfo.getClassPath(ClasspathInfo.PathKind.COMPILE)
    val srcCp = cpInfo.getClassPath(ClasspathInfo.PathKind.SOURCE)
    ClassPathSupport.createProxyClassPath(Array(bootCp, compCp, srcCp): _*)
  }
}
