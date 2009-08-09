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

import _root_.java.io.{File, IOException, InputStream}
import _root_.java.lang.ref.{Reference, WeakReference}
import javax.swing.text.BadLocationException
import org.netbeans.api.java.classpath.ClassPath
import org.netbeans.api.java.queries.SourceForBinaryQuery
import org.netbeans.api.java.source.ClasspathInfo
import org.netbeans.api.lexer.TokenHierarchy
import org.netbeans.editor.BaseDocument
import org.netbeans.modules.classfile.ClassFile
import org.netbeans.modules.csl.api.{ElementKind, Modifier, OffsetRange}
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.parsing.api.{ParserManager, ResultIterator, Source, UserTask}
import org.netbeans.modules.parsing.spi.{ParseException, Parser}
import org.netbeans.spi.java.classpath.support.ClassPathSupport
import org.openide.filesystems.{FileObject, FileUtil}
import org.openide.util.{Exceptions, NbBundle}

import org.netbeans.api.language.util.ast.{AstDfn, AstScope}
import org.netbeans.modules.scala.editor.ast.{ScalaDfns, ScalaRootScope}
import org.netbeans.modules.scala.editor.element.{JavaElement}
import org.netbeans.modules.scala.editor.lexer.ScalaLexUtil

import _root_.scala.tools.nsc.util.Position
import _root_.scala.tools.nsc.symtab.Symbols
import _root_.scala.collection.mutable.ArrayBuffer

/**
 *
 * @author Caoyuan Deng
 */
object ScalaSourceUtil {

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
          case '\n' => return -1
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

  val scalaFileToSource = new _root_.java.util.WeakHashMap[FileObject, Reference[Source]]
  val scalaFileToCompilationInfo = new _root_.java.util.WeakHashMap[FileObject, Reference[Parser.Result]]

  def getCompilationInfoForScalaFile(fo: FileObject): Parser.Result = {
    var info: Parser.Result = scalaFileToCompilationInfo.get(fo) match {
      case null => null
      case ref => ref.get
    }

    if (info == null) {
      val pResults = new Array[Parser.Result](1)
      val source = getSourceForScalaFile(fo)
      try {
        ParserManager.parse(_root_.java.util.Collections.singleton(source), new UserTask {
            @throws(classOf[Exception])
            override def run(resultIterator: ResultIterator): Unit = {
              pResults(0) = resultIterator.getParserResult
            }
          })
      } catch {
        case ex:ParseException => Exceptions.printStackTrace(ex)
      }

      info = pResults(0)
      scalaFileToCompilationInfo.put(fo, new WeakReference[Parser.Result](info))
    }

    info
  }

  /**
   * @Note: We cannot create javasource via JavaSource.forFileObject(fo) here, which
   * does not support virtual source yet (only ".java" and ".class" files
   * are supported), but we can create js via JavaSource.create(cpInfo);
   */
  private def getSourceForScalaFile(fo: FileObject): Source = {
    var source: Source = scalaFileToSource.get(fo) match {
      case null => null
      case ref => ref.get
    }

    if (source == null) {
      source = Source.create(fo)
      scalaFileToSource.put(fo, new WeakReference[Source](source))
    }

    source
  }

  def getDocComment(info: Parser.Result, element: JavaElement): String = {
    if (info == null) {
      return null
    }

    val doc = info.getSnapshot.getSource.getDocument(true) match {
      case null => return null
      case x: BaseDocument => x
    }

    val th = info.getSnapshot.getTokenHierarchy

    doc.readLock // Read-lock due to token hierarchy use
    val range = ScalaLexUtil.getDocumentationRange(th, element.getBoundsOffset(th))
    doc.readUnlock

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

    doc.readLock // Read-lock due to token hierarchy use
    val range = ScalaLexUtil.getDocCommentRangeBefore(th, symbolOffset)
    doc.readUnlock

    if (range != OffsetRange.NONE && range.getEnd < doc.getLength) {
      try {
        return doc.getText(range.getStart, range.getLength)
      } catch {
        case ex: BadLocationException => Exceptions.printStackTrace(ex)
      }
    }

    ""
  }

  def getOffset(info: Parser.Result, element: JavaElement): Int = {
    if (info == null) {
      return -1
    }

    val th = info.getSnapshot.getTokenHierarchy
    element.getPickOffset(th)
  }

  def getFileObject(info: ParserResult, symbol: Symbols#Symbol): Option[FileObject] = {
    val pos = symbol.pos
    if (pos.isDefined) {
      val srcFile = pos.source
      if (srcFile != null) {
        var srcPath = srcFile.path
        // Check the strange behavior of Scala's compiler, which may omit the beginning File.separator ("/")
        if (!srcPath.startsWith(File.separator)) {
          srcPath = File.separator + srcPath
        }
        val file = new File(srcPath)
        if (file != null && file.exists) {
          // it's a real file and not archive file
          return Some(FileUtil.toFileObject(file))
        }
      }
    }

    val qName: String = try {
      symbol.enclClass.fullNameString.replace('.', File.separatorChar)
    } catch {
      case ex:_root_.java.lang.Error => null
        // java.lang.Error: no-symbol does not have owner
        //        at scala.tools.nsc.symtab.Symbols$NoSymbol$.owner(Symbols.scala:1565)
        //        at scala.tools.nsc.symtab.Symbols$Symbol.fullNameString(Symbols.scala:1156)
        //        at scala.tools.nsc.symtab.Symbols$Symbol.fullNameString(Symbols.scala:1166)
    }

    if (qName == null) {
      return None
    }

    val lastSep = qName.lastIndexOf(File.separatorChar)
    val pkgName: String = if (lastSep > 0) {
      qName.substring(0, lastSep)
    } else null

    val clzName = qName + ".class"

    try {
      val srcFo = info.getSnapshot.getSource.getFileObject
      val cpInfo = ClasspathInfo.create(srcFo)
      val cp = ClassPathSupport.createProxyClassPath(
        Array(cpInfo.getClassPath(ClasspathInfo.PathKind.SOURCE),
              cpInfo.getClassPath(ClasspathInfo.PathKind.BOOT),
              cpInfo.getClassPath(ClasspathInfo.PathKind.COMPILE)): _*)

      val clzFo = cp.findResource(clzName)
      var srcPath: String = null
      if (clzFo != null) {
        val in = clzFo.getInputStream
        try {
          val clzFile = new ClassFile(in, false)
          if (clzFile != null) {
            srcPath = clzFile.getSourceFileName
          }
        } finally {
          if (in != null) {
            in.close
          }
        }
      }

      if (srcPath != null) {
        if (pkgName != null) {
          srcPath = pkgName + File.separatorChar + srcPath
        }

        val root = cp.findOwnerRoot(clzFo)
        assert(root != null)

        val result = SourceForBinaryQuery.findSourceRoots(root.getURL)
        val srcRoots = result.getRoots
        val srcCp = ClassPathSupport.createClassPath(srcRoots: _*)

        srcCp.findResource(srcPath) match {
          case null => None
          case x => return Some(x)
        }
      }
    } catch {
      case ex: IOException => ex.printStackTrace
    }

    None
  }

  private val TMPL_KINDS = Set(ElementKind.CLASS, ElementKind.MODULE)

  def getBinaryClassName(pResult: ScalaParserResult, offset: Int): String = {
    val th = pResult.getSnapshot.getTokenHierarchy
    val rootScope = pResult.getRootScopeForDebugger match {
      case None => return null
      case Some(x) => x
    }
    
    var clzName = ""

    rootScope.enclosingDfn(TMPL_KINDS, th, offset) foreach {enclDfn =>
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
      ParserManager.parse(_root_.java.util.Collections.singleton(source), new UserTask {
          @throws(classOf[Exception])
          override def run(resultIterator: ResultIterator): Unit = {
            val pResult = resultIterator.getParserResult.asInstanceOf[ScalaParserResult]
            val rootScope = pResult.rootScope match {
              case None => return
              case Some(x) => x
            }
            // Get all defs will return all visible packages from the root and down
            getAllDfns(rootScope, ElementKind.PACKAGE) foreach {
              // Only go through the defs for each package scope.
              // Sub-packages are handled by the fact that
              // getAllDefs will find them.
              packaging => packaging.bindingScope.dfns foreach {dfn =>
                if (isMainMethodExists(dfn.asInstanceOf[ScalaDfns#ScalaDfn])) result += dfn.asInstanceOf[ScalaDfns#ScalaDfn]
              }
            }
            
            rootScope.visibleDfns(ElementKind.MODULE) foreach {dfn =>
              if (isMainMethodExists(dfn.asInstanceOf[ScalaDfns#ScalaDfn])) result += dfn.asInstanceOf[ScalaDfns#ScalaDfn]
            }
          }

          def getAllDfns(rootScope: AstScope, kind: ElementKind): Seq[ScalaDfns#ScalaDfn] = {
            getAllDfns(rootScope, kind, new ArrayBuffer[ScalaDfns#ScalaDfn])
          }

          def getAllDfns(astScope: AstScope, kind: ElementKind, result: ArrayBuffer[ScalaDfns#ScalaDfn]): Seq[ScalaDfns#ScalaDfn] = {
            astScope.dfns foreach {dfn =>
              if (dfn.getKind == kind)  result += dfn.asInstanceOf[ScalaDfns#ScalaDfn]
            }
            astScope.subScopes foreach {
              childScope => getAllDfns(childScope, kind, result)
            }
            result
          }
        })

      result
    } catch {
      case ex: ParseException => Exceptions.printStackTrace(ex); Nil
    }
  }

  def getMainClassesAsJavaCollection(fo: FileObject): _root_.java.util.Collection[AstDfn] = {
    val result = new _root_.java.util.ArrayList[AstDfn]
    getMainClasses(fo) foreach {result.add(_)}
    result
  }

  /**
   * Returns classes declared under the given source roots which have the main method.
   * @param sourceRoots the source roots
   * @return the classes containing the main methods
   * Currently this method is not optimized and may be slow
   */
  def getMainClassesAsJavaCollection(sourceRoots: Array[FileObject]): _root_.java.util.Collection[AstDfn] = {
    val result = new _root_.java.util.ArrayList[AstDfn]
    for (root <- sourceRoots) {
      result.addAll(getMainClassesAsJavaCollection(root))
      try {
        val bootPath = ClassPath.getClassPath(root, ClassPath.BOOT)
        val compilePath = ClassPath.getClassPath(root, ClassPath.COMPILE)
        val srcPath = ClassPathSupport.createClassPath(Array(root): _*)
        val cpInfo = ClasspathInfo.create(bootPath, compilePath, srcPath)
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
          return _root_.java.util.Collections.emptySet[AstDfn]
      }
    }
    result
  }

  
  def isMainMethodExists(obj: ScalaDfns#ScalaDfn): Boolean = {
    obj.symbol.tpe.members exists {
      member => member.isMethod && isMainMethod(member)
    }
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
        val bootPath = ClassPath.getClassPath(root, ClassPath.BOOT)
        val compilePath = ClassPath.getClassPath(root, ClassPath.COMPILE)
        val srcPath = ClassPathSupport.createClassPath(Array(root): _*)
        val cpInfo = ClasspathInfo.create(bootPath, compilePath, srcPath)
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
      } catch {
        case ioe: Exception => Exceptions.printStackTrace(ioe); Nil
      }
    }

    result
  }

  def getClasspathInfoForFileObject(fo: FileObject): Option[ClasspathInfo] = {
    val bootPath = ClassPath.getClassPath(fo, ClassPath.BOOT)
    val compilePath = ClassPath.getClassPath(fo, ClassPath.COMPILE)
    val srcPath = ClassPath.getClassPath(fo, ClassPath.SOURCE)

    if (bootPath == null || compilePath == null || srcPath == null) {
      /** @todo why? at least I saw this happens on "Scala project created from existing sources" */
      println("No classpath for " + fo)
      None
    } else {
      ClasspathInfo.create(bootPath, compilePath, srcPath) match {
        case null => None
        case x => Some(x)
      }
    }
  }
}
