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

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.PrintWriter
import java.io.Reader
import java.io.StringWriter
import java.io.Writer
import java.util.logging.Logger
import javax.swing.event.ChangeListener
import org.netbeans.modules.csl.api.ElementKind
//import org.netbeans.modules.java.preprocessorbridge.spi.JavaSourceProvider
import org.netbeans.modules.java.preprocessorbridge.spi.VirtualSourceProvider
import org.netbeans.modules.parsing.api.ParserManager
import org.netbeans.modules.parsing.api.ResultIterator
import org.netbeans.modules.parsing.api.Source
import org.netbeans.modules.parsing.api.UserTask
import org.netbeans.modules.parsing.spi.ParseException
import org.openide.filesystems.{FileObject, FileUtil}
import org.openide.util.Exceptions

import org.netbeans.api.language.util.ast.{AstRootScope, AstScope}
import org.netbeans.modules.scala.editor.ast.ScalaDfns
import scala.collection.mutable.ArrayBuffer

import scala.util.NameTransformer
import scala.collection.mutable.HashSet
import scala.tools.nsc.symtab.Flags
import scala.tools.nsc.symtab.Symbols
import scala.tools.nsc.symtab.Types


/**
 * Virtual java source
 *
 * @author Caoyuan Deng
 */

/* @org.openide.util.lookup.ServiceProviders(
 Array(new org.openide.util.lookup.ServiceProvider(service = classOf[JavaSourceProvider]),
 new org.openide.util.lookup.ServiceProvider(service = classOf[VirtualSourceProvider]))
 ) */

/**
 * This requires also a Java Indexer to be enabled for scala mimetype
 * @see layer.xml:
 *      <file name="JavaIndexer.shadow">
 *          <attr name="originalFile" stringvalue="Editors/text/x-java/JavaIndexer.instance"/>
 *      </file>
 *
 * @Note: don't use full class name `classOf[org.netbeans.modules.java.preprocessorbridge.spi.VirtualSourceProvider]`, here
 * instead, should use `classOf[VirtualSourceProvider]`, otherwise, lookup cannot find it. Why? don't know ...
 */
@org.openide.util.lookup.ServiceProvider(service = classOf[VirtualSourceProvider])
class ScalaVirtualSourceProvider extends VirtualSourceProvider {
  import ScalaVirtualSourceProvider._

  Log.info("Instance of " + this.getClass.getSimpleName + " is created")

  /** @Todo
   * The only reason to implement JavaSourceProvider is to get a none-null JavaSource#forFileObject,
   * the JavaSource instance is a must currently when eval expression under debugging. see issue #150903
   */
  /* def forFileObject(fo: FileObject): JavaSourceProvider.PositionTranslatingJavaFileFilterImplementation = {
   if (!"text/x-scala".equals(FileUtil.getMIMEType(fo)) && !"scala".equals(fo.getExt)) {  //NOI18N
   return null
   } else {
   new JavaSourceProvider.PositionTranslatingJavaFileFilterImplementation {
   def getOriginalPosition(javaSourcePosition: Int): Int = -1
   def getJavaSourcePosition(originalPosition: Int): Int = -1
   def filterReader(r: Reader): Reader = r
   def filterCharSequence(charSequence: CharSequence): CharSequence = ""
   def filterWriter(w: Writer): Writer = w
   def addChangeListener(listener: ChangeListener) {}
   def removeChangeListener(listener: ChangeListener) {}
   }
   }
   } */

  override def getSupportedExtensions: java.util.Set[String] = {
    java.util.Collections.singleton("scala") // NOI18N
  }

  override def index: Boolean = true

  override def translate(files: java.lang.Iterable[File], sourceRoot: File, result: VirtualSourceProvider.Result) {
    Log.info("Translating " + files)
    
    val itr = files.iterator
    while (itr.hasNext) {
      translate(itr.next, sourceRoot, result)
    }
  }

  private def translate(file: File, sourceRoot: File, result: VirtualSourceProvider.Result) {
    val fo = FileUtil.toFileObject(file)
    
    if (fo == null) return

    val rootFo = FileUtil.toFileObject(sourceRoot)
    try {
      val source = Source.create(fo)
      /** @Note: do not use UserTask to parse it? which may cause "refershing workspace" */
      // FIXME can we move this out of task (?)
      ParserManager.parse(java.util.Collections.singleton(source), new UserTask {
            
          @throws(classOf[ParseException])
          override def run(ri: ResultIterator) {
            val pr = ri.getParserResult.asInstanceOf[ScalaParserResult]
            val global = pr.global
            val rootScope = pr.rootScope getOrElse {assert(false, "Parse result is null : " + fo.getName); return}
            val tmpls = new ArrayBuffer[ScalaDfns#ScalaDfn]
            visit(rootScope, tmpls)
            process(global, tmpls.toList)
          }

          private def visit(scope: AstScope, tmpls: ArrayBuffer[ScalaDfns#ScalaDfn]): Unit = {
            for (dfn <- scope.dfns if (dfn.getKind == ElementKind.CLASS || dfn.getKind == ElementKind.MODULE)) {
              tmpls += dfn.asInstanceOf[ScalaDfns#ScalaDfn]
            }

            for (_scope <- scope.subScopes) {
              visit(_scope, tmpls)
            }
          }

          private def process(global: ScalaGlobal, tmpls: List[ScalaDfns#ScalaDfn]) = {
            tmpls match {
              case Nil =>
                // * source is probably broken and there is no AST
                // * let's generate empty Java stub with simple name equal to file name
                var pkg = FileUtil.getRelativePath(rootFo, fo.getParent)
                if (pkg != null) {
                  pkg = pkg.replace('/', '.')
                  val sb = new StringBuilder
                  if (!pkg.equals("")) { // NOI18N
                    sb.append("package " + pkg + ";") // NOI18N
                  }
                  val name = fo.getName
                  sb.append("public class ").append(name).append(" implements scala.ScalaObject {public int $tag() throws java.rmi.RemoteException {return 0;}}"); // NOI18N
                  //@Todo diable result add till we get everything ok
                  //result.add(file, pkg, file.getName(), sb.toString());
                }
              case _ =>
                val generator = new JavaStubGenerator(global)
                for (tmpl <- tmpls;
                     sym = tmpl.symbol if sym != global.NoSymbol; // avoid strange file name, for example: <error: class ActorProxy>.java
                     symSName = sym.nameString if symSName.length > 0 && symSName.charAt(0) != '<' // @todo <any>
                ) {
                  try {
                    val javaStub = generator.generateClass(sym.asInstanceOf[generator.global.Symbol])
                    val pkgQName = sym.enclosingPackage match {
                      case null => ""
                      case packaging => packaging.fullNameString match {
                          case "<empty>" => ""
                          case x => x
                        }
                    }
                    
                    result.add(file, pkgQName, symSName, javaStub)
                  } catch {case ex: FileNotFoundException => Exceptions.printStackTrace(ex)}
                }
            }
          }
        })
    } catch {case ex: ParseException => Exceptions.printStackTrace(ex)}
  }

  private class JavaStubGenerator(val global: ScalaGlobal) {
    import global._

    private val toCompile = new ArrayBuffer[String]

    @throws(classOf[FileNotFoundException])
    def generateClass(sym: Symbol): CharSequence = {
      val qName = sym.fullNameString
      val fileName = encodeName(qName).replace('.', '/')
      toCompile += fileName

      val sw = new StringWriter
      val pw = new PrintWriter(sw)

      try {
        val packaging = sym.enclosingPackage
        if (packaging != null) {
          val pkgName = sym.enclosingPackage match {
            case null => ""
            case packaging => packaging.fullNameString match {
                case x if x.length > 0 && x.charAt(0) != '<' => x
                case _ => ""
              }
          }

          if (pkgName.length > 0) {
            pw.print("package ")
            pw.print(pkgName)
            pw.println(";")
          }
        }

        //pw.println("@NetBeansVirtualSource(11, 12)");

        pw.print(modifiers(sym))
        if (sym.isTrait) { // isTrait also isClass, so determine trait before class
          // @Todo has two classes;
          pw.print(" interface ")
        } else if (sym.isModule) { // object
          // @Todo has two classes;
          pw.print(" final class ")
        } else if (sym.isClass) {
          pw.print(" class ")
        } 

        val clzName = sym.nameString
        pw.print(encodeName(clzName))

        val superQName = sym.superClass match {
          case null => ""
          case x => x.fullNameString
        }

        if (superQName.length > 0) {
          pw.print(" extends ")
          pw.print(encodeQName(superQName))
        }

        val tpe = try {
          sym.tpe
        } catch {case _=> null}

        if (tpe != null) {
          val itr = tpe.baseClasses.tail.iterator // head is always `java.lang.Object`
          var i = 0
          while (itr.hasNext) {
            val base = itr.next
            base.fullNameString  match {
              case `superQName` =>
              case `qName` =>
              case "java.lang.Object" =>
              case x =>
                if (i == 0) {
                  pw.print(" implements ")
                } else {
                  pw.print(",")
                }
                pw.print(encodeQName(x))
                i += 1
            }
          }

          pw.println(" {")

          for (member <- tpe.members if !member.hasFlag(Flags.PRIVATE)) {
            val mTpe = try {
              member.tpe
            } catch {case ex => ScalaGlobal.resetLate(global, ex); null}

            if (mTpe != null && !ScalaUtil.isInherited(sym, member)) {
              val mSName = member.nameString
              if (member.isMethod && mSName != "$init$" && mSName != "synchronized") {
                pw.print(modifiers(member))
                pw.print(" ")
                if (member.isConstructor) {
                  pw.print(encodeName(sym.nameString))
                  // * parameters
                  pw.print(params(mTpe.params))
                  pw.println(" {}")
                } else {
                  val mResTpe = try {
                    mTpe.resultType
                  } catch {case ex => ScalaGlobal.resetLate(global, ex); null}

                  if (mResTpe != null) {
                    val mResQName = encodeType(mResTpe.typeSymbol.fullNameString)
                    pw.print(mResQName)
                    pw.print(" ")
                    // method name
                    pw.print(encodeName(mSName))
                    // method parameters
                    pw.print(params(mTpe.params))
                    pw.print(" ")

                    // method body
                    pw.print("{")
                    pw.print(returnStrOfType(mResQName))
                    pw.println("}")
                  }
                }
              } else if (member.isVariable) {
                // do nothing
              } else if (member.isValue) {
                pw.print(modifiers(member))
                pw.print(" ")
                val mResTpe = mTpe.resultType
                val mResQName = encodeType(mResTpe.typeSymbol.fullNameString)
                pw.print(mResQName)
                pw.print(" ")
                pw.print(mSName)
                pw.println(";")
              }
            }
          }

          pw.println(dollarTagMethod) // * should implement scala.ScalaObject

          pw.println("}")
        } else {
          pw.println(" {")
          
          pw.println(dollarTagMethod) // * should implement scala.ScalaObject
          pw.println("}")
        }
      } finally {
        try {
          pw.close
        } catch {case ex: Exception =>}
        
        try {
          sw.close
        } catch {case ex: IOException =>}
      }

      //println(sw.toString)

      sw.toString
    }

    private val dollarTagMethod = "public int $tag() throws java.rmi.RemoteException {return 0;}"

    private def modifiers(sym: Symbol): String = {
      if (sym hasFlag Flags.PRIVATE) {
        "private"
      } else if (sym hasFlag Flags.PROTECTED) {
        "protected"
      } else {
        "public"
      }
    }

    private def params(params: List[Symbol]): String = {
      val sb = new StringBuffer
      sb.append("(")

      val paramNames = new HashSet[String]
      var i = 0
      val itr = params.iterator
      while (itr.hasNext) {
        val param = itr.next

        val tpe = try {
          param.tpe
        } catch {case ex => ScalaGlobal.resetLate(global, ex); null}
        
        if (tpe != null) {
          sb.append(encodeQName(tpe.typeSymbol.fullNameString))
        } else {
          sb.append("Object")
        }
        sb.append(" ")

        val name = param.nameString
        if (name.length > 0 && paramNames.add(name)) {
          sb.append(name)
        } else {
          sb.append("a")
          sb.append(i)
        }
        
        if (itr.hasNext) {
          sb.append(",")
        }
        
        i += 1
      }

      sb.append(")")
      sb.toString
    }

    /*
     * to java name
     */
    private def encodeName(scalaTermName: String): String = {
      NameTransformer.encode(scalaTermName)
    }

    /*
     * to java type name
     */
    private def encodeType(scalaTypeQName: String): String = {
      scalaTypeQName match {
        case "scala.Unit" => "void"
        case _ => encodeQName(scalaTypeQName)
      }
    }

    private def encodeQName(qName: String): String = {
      qName.lastIndexOf('.') match {
        case -1 => encodeName(qName)
        case i =>
          val pkgName = qName.substring(0, i + 1) // with last '.'
          val sName = qName.substring(i + 1, qName.length)
          pkgName + encodeName(sName)
      }
    }
  }
}

object ScalaVirtualSourceProvider {
  val Log = Logger.getLogger(classOf[ScalaVirtualSourceProvider].getName)

  private def returnStrOfType(tpe: String) = tpe match {
    case "scala.Unit" => "return;"
    case "void"    => "return;"
    case "double"  => "return 0.0;"
    case "float"   => "return 0.0f;"
    case "long"    => "return 0L;"
    case "int"     => "return 0;"
    case "short"   => "return 0;"
    case "byte"    => "return 0;"
    case "boolean" => "return false;"
    case "char"    => "return 0;"
    case _ => "return null"
  }
}