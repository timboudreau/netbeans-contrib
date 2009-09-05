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
import org.netbeans.modules.java.preprocessorbridge.spi.{JavaSourceProvider, VirtualSourceProvider}
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

  override def translate(files: java.lang.Iterable[File], sourceRoot: File, result: VirtualSourceProvider.Result): Unit = {
    Log.info("Translating " + files)
    
    val rootFo = FileUtil.toFileObject(sourceRoot)
    val it = files.iterator
    while (it.hasNext) {
      val file = it.next
      getTemplates(file) match {
        case (global, Nil) =>
          // * source is probably broken and there is no AST
          // * let's generate empty Java stub with simple name equal to file name
          val fo = FileUtil.toFileObject(file)
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
        case (globalx, tmpls) =>
          val fo = FileUtil.toFileObject(file)
          val generator = new JavaStubGenerator {override val global = globalx}
          for (tmpl <- tmpls;
               sym = tmpl.symbol if sym != globalx.NoSymbol; // avoid strange file name, for example: <error: class ActorProxy>.java
               name = sym.nameString if name.length > 0 && name.charAt(0) != '<' // @todo <any>
          ) {
            try {
              val javaStub = generator.generateClass(tmpl.asInstanceOf[generator.global.ScalaDfn])
              val packaging = sym.enclosingPackage
              val pkgName = (if (packaging == null) "" else packaging.fullNameString) match {
                case "<empty>" => ""
                case x => x
              }
              result.add(file, pkgName, name, javaStub)
            } catch {case ex: FileNotFoundException => Exceptions.printStackTrace(ex)}
          }
      }
    }
  }

  private def getTemplates(file: File): (ScalaGlobal, List[ScalaDfns#ScalaDfn]) = {
    val global = Array[ScalaGlobal](null)
    val tmpls = new ArrayBuffer[ScalaDfns#ScalaDfn]
    val fo = FileUtil.toFileObject(file)
    if (fo != null) {
      try {
        val source = Source.create(fo)
        /** @Note: do not use UserTask to parse it? which may cause "refershing workspace" */
        // FIXME can we move this out of task (?)
        ParserManager.parse(java.util.Collections.singleton(source), new UserTask {
            
            @throws(classOf[ParseException])
            override def run(ri: ResultIterator) {
              val pr = ri.getParserResult.asInstanceOf[ScalaParserResult]
              global(0) = pr.global
              val rootScope = pr.rootScope getOrElse {assert(false, "Parse result is null : " + fo.getName); return}
              scan(rootScope, tmpls)
            }
          })
      } catch {case ex: ParseException => Exceptions.printStackTrace(ex)}
    }

    (global(0), tmpls.toList)
  }

  private def scan(scope: AstScope, tmpls: ArrayBuffer[ScalaDfns#ScalaDfn]): Unit = {
    for (dfn <- scope.dfns if (dfn.getKind == ElementKind.CLASS || dfn.getKind == ElementKind.MODULE)) {
      tmpls += dfn.asInstanceOf[ScalaDfns#ScalaDfn]
    }

    for (_scope <- scope.subScopes) {
      scan(_scope, tmpls)
    }
  }

  private abstract class JavaStubGenerator {
    val global: ScalaGlobal
    import global._

    private val toCompile = new ArrayBuffer[String]

    @throws(classOf[FileNotFoundException])
    def generateClass(tmpl: ScalaDfn): CharSequence = {
      val sym = tmpl.symbol
      val fileName = toJavaName(sym.fullNameString).replace('.', '/')
      toCompile += fileName

      val sw = new StringWriter
      val pw = new PrintWriter(sw)

      try {
        val packaging = sym.enclosingPackage
        if (packaging != null) {
          val pkgName = packaging.fullNameString
          if (pkgName.length > 0 && pkgName.charAt(0) != '<') {
            pw.print("package ")
            pw.print(packaging.fullNameString)
            pw.println(";")
          }
        }

        //out.println("@NetBeansVirtualSource(11, 12)");

        printModifiers(pw, sym)
        if (sym.isClass) {
          pw.print(" class ")
        } else if (sym.isModule) {
          // @Todo has two classes;
          pw.print(" class ")
        } else if (sym.isTrait) {
          // @Todo has two classes;
          pw.print(" interface ")
        }

        // class name
        val clzName = toJavaName(sym.nameString)
        pw.print(clzName)

//                Symbol superClass = symbol.superClass();
//                if (superClass != null) {
//                    String superQName = ScalaElement.symbolQualifiedName(superClass);
//                    out.print(" extends ");
//                    out.print(superQName);
//                }
//
//                scala.List parents = symbol.tpe().parents();
//                int n = 0;
//                for (int i = 0; i < parents.size(); i++) {
//                    Type parent = (Type) parents.apply(i);
//                    if (ScalaElement.typeQualifiedName(parent, false).equals("java.lang.Object")) {
//                        continue;
//                    }
//
//                    if (n == 0) {
//                        out.print(" implements ");
//                    } else {
//                        out.print(",");
//                    }
//                    printType(out, parent);
//                    n++;
//                }

        pw.println(" {")

        /*
         scala.List members = null;
         try {
         // scalac will throw exceptions here, we have to catch it
         members = symbol.tpe().members();
         } catch (Throwable e) {
         ScalaGlobal.reset();
         }
         if (members != null) {
         int size = members.size();
         for (int i = 0; i < size; i++) {
         Symbol member = (Symbol) members.apply(i);

         if (member.isPublic() || member.isProtectedLocal()) {
         if (ScalaElement.isInherited(symbol, member)) {
         continue;
         }

         if (member.isMethod()) {
         if (member.nameString().equals("$init$") || member.nameString().equals("synchronized")) {
         continue;
         }

         printModifiers(out, member);
         out.print(" ");
         if (member.isConstructor()) {
         out.print(toJavaName(symbol.nameString()));
         // parameters
         printParams(out, member.tpe().paramTypes());
         out.print(" ");
         out.println("{}");
         } else {
         Type resType = null;
         try {
         resType = member.tpe().resultType();
         } catch (Throwable ex) {
         ScalaGlobal.reset();
         }
         if (resType != null) {
         String resQName = toJavaType(ScalaElement.typeQualifiedName(resType, false));
         out.print(resQName);
         out.print(" ");
         // method name
         out.print(toJavaName(member.nameString()));
         // method parameters
         printParams(out, member.tpe().paramTypes());
         out.print(" ");

         // method body
         out.print("{");
         printReturn(out, resQName);
         out.println("}");
         }
         }
         } else if (member.isVariable()) {
         // do nothing
         } else if (member.isValue()) {
         printModifiers(out, member);
         out.print(" ");
         Type resType = member.tpe().resultType();
         String resQName = toJavaType(ScalaElement.typeQualifiedName(resType, false));
         out.print(resQName);
         out.print(" ");
         out.print(member.nameString());
         out.println(";");
         }
         }

         // implements scala.ScalaObject
         out.println("public int $tag() throws java.rmi.RemoteException {return 0;}");
         }
         }
         */
        pw.println("}")
      } finally {
        try {
          pw.close
        } catch {case ex: Exception =>}
        
        try {
          sw.close
        } catch {case ex: IOException =>}
      }
      sw.toString
    }

    private def printModifiers(pw: PrintWriter, symbol: Symbol) {
      if (symbol hasFlag Flags.PRIVATE) {
        pw.print("private")
      } else if (symbol hasFlag Flags.PROTECTED) {
        pw.print("protected")
      } else {
        pw.print("public")
      }
    }

    private def printType(pw: PrintWriter, tpe: Type) {
      //out.print(JavaScalaMapping.toJavaType(ScalaElement.typeQualifiedName(type, false)));
    }

    private def printParams(pw: PrintWriter, params: List[Type]) {
      pw.print("(")

      var i = 0
      val itr = params.iterator
      while (itr.hasNext) {
        val tpe = itr.next
        printType(pw, tpe)
        pw.print(" a")
        pw.print(i)
        if (itr.hasNext) {
          pw.print(",")
        }
        i += 1
      }

      pw.print(")")
    }

    private def printReturn(pw: PrintWriter, typeName: String) {
      pw.print(returnStrOfType(typeName))
    }

    private def toJavaName(scalaName: String): String = {
      scalaName//JavaScalaMapping.toJavaOpName(scalaName);
    }

    private def toJavaType(scalaTypeName: String): String = {
      scalaTypeName //JavaScalaMapping.toJavaType(scalaTypeName);
    }
  }
}

object ScalaVirtualSourceProvider {
  val Log = Logger.getLogger(classOf[ScalaVirtualSourceProvider].getName)

  private def returnStrOfType(tpe: String) = tpe match {
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