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
package org.netbeans.modules.scala.editor.ast

import _root_.java.io.File
import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy, TokenSequence}
import org.netbeans.api.language.util.ast.{AstItem, AstScope}
import org.netbeans.modules.csl.api.ElementKind
import org.netbeans.modules.scala.editor.lexer.{ScalaLexUtil, ScalaTokenId}
import org.openide.filesystems.{FileObject, FileUtil}
import _root_.scala.tools.nsc.{CompilationUnits, Global}
import _root_.scala.tools.nsc.ast.Trees
//import scala.tools.nsc.ast.Trees.Annotated;
////import scala.tools.nsc.ast.Trees.Annotation;
//import scala.tools.nsc.ast.Trees.AppliedTypeTree;
//import scala.tools.nsc.ast.Trees.Apply;
//import scala.tools.nsc.ast.Trees.ApplyDynamic;
//import scala.tools.nsc.ast.Trees.ArrayValue;
//import scala.tools.nsc.ast.Trees.Assign;
//import scala.tools.nsc.ast.Trees.Bind;
//import scala.tools.nsc.ast.Trees.Block;
//import scala.tools.nsc.ast.Trees.CaseDef;
//import scala.tools.nsc.ast.Trees.ClassDef;
//import scala.tools.nsc.ast.Trees.CompoundTypeTree;
//import scala.tools.nsc.ast.Trees.DefDef;
//import scala.tools.nsc.ast.Trees.DocDef;
//import scala.tools.nsc.ast.Trees.ExistentialTypeTree;
//import scala.tools.nsc.ast.Trees.Function;
//import scala.tools.nsc.ast.Trees.Ident;
//import scala.tools.nsc.ast.Trees.If;
//import scala.tools.nsc.ast.Trees.Import;
//import scala.tools.nsc.ast.Trees.LabelDef;
//import scala.tools.nsc.ast.Trees.Literal;
//import scala.tools.nsc.ast.Trees.Match;
//import scala.tools.nsc.ast.Trees.ModuleDef;
//import scala.tools.nsc.ast.Trees.New;
//import scala.tools.nsc.ast.Trees.PackageDef;
//import scala.tools.nsc.ast.Trees.Return;
//import scala.tools.nsc.ast.Trees.Select;
//import scala.tools.nsc.ast.Trees.SelectFromTypeTree;
//import scala.tools.nsc.ast.Trees.Sequence;
//import scala.tools.nsc.ast.Trees.SingletonTypeTree;
//import scala.tools.nsc.ast.Trees.Star;
//import scala.tools.nsc.ast.Trees.StubTree;
//import scala.tools.nsc.ast.Trees.Super;
//import scala.tools.nsc.ast.Trees.Template;
//import scala.tools.nsc.ast.Trees.This;
//import scala.tools.nsc.ast.Trees.Throw;
//import scala.tools.nsc.ast.Trees.Tree;
//import scala.tools.nsc.ast.Trees.Try;
//import scala.tools.nsc.ast.Trees.TypeApply;
//import scala.tools.nsc.ast.Trees.TypeBoundsTree;
//import scala.tools.nsc.ast.Trees.TypeDef;
//import scala.tools.nsc.ast.Trees.TypeTree;
//import scala.tools.nsc.ast.Trees.Typed;
//import scala.tools.nsc.ast.Trees.UnApply;
//import scala.tools.nsc.ast.Trees.ValDef;
import _root_.scala.tools.nsc.symtab.{Symbols, SymbolTable}
import _root_.scala.tools.nsc.symtab.Flags._
import _root_.scala.tools.nsc.util.{BatchSourceFile, Position}
import _root_.scala.collection.mutable.{Stack, HashSet}

/**
 *
 * Usage: in global
 *   object scalaAstVisitor extends {
 *     val global: Global.this.type = Global.this
 *   } with ScalaAstVisitor
 * 
 * @author Caoyuan Deng
 */
abstract class ScalaAstVisitor {

  val global: Global
  import global._

  val EOL = System.getProperty("line.separator", "\n")

  private var debug :Boolean = _
  private var indentLevel :Int = _
  private var astPath :Stack[Tree] = _
  //private var exprs :Stack[AstExpr] = new Stack
  private var visited :Set[Tree] = _

  private var scopes :Stack[AstScope[Symbols#Symbol]] = _
  private var rootScope :ScalaRootScope = _

  private var fo :Option[FileObject] = _
  private var th :TokenHierarchy[_] = _

  def reset :Unit = {
    this.scopes = new Stack
    this.visited = Set()
    this.astPath = new Stack
  }
  
  def visit(unit:CompilationUnit, th:TokenHierarchy[_]) :ScalaRootScope = {
    this.th = th
    val srcFile = unit.source
    this.fo = if (srcFile ne null) {
      val file = new File(srcFile.path)
      if (file != null && file.exists) { // it's a real file and not archive file
        FileUtil.toFileObject(file) match {
          case null => None
          case x => Some(x)
        }
      } else None
    } else None
    
    if (unit.body ne null) {
      reset
      val rootTree = unit.body
      this.rootScope = ScalaRootScope(getBoundsTokens(offset(rootTree), srcFile.length))
      scopes push rootScope
      //exprs.push(rootScope.getExprContainer());
      //visit(rootTree)
      if (debug) {
        //rootScope.getExprContainer().print();
      }
      
      (new TreeVisitor) visit unit.body
      rootScope
    } else ScalaRootScope.EMPTY
  }

  object InfoLevel extends Enumeration {val Quiet, Normal, Verbose = Value}
  var infolevel = InfoLevel.Quiet

  class TreeVisitor {

    private val buf = new StringBuilder

    private var maybeType :Option[Type] = None

    def visit(tree: Tree): String = {
      def traverse(tree: Tree, level: Int, comma: Boolean) {
        def println(s: String) {
          for (i <- 0 until level) buf.append("  ")
          buf.append(s)
          buf.append(EOL)
        }
        def printcln(s: String) {
          for (i <- 0 until level) buf.append("  ")
          buf.append(s)
          if (comma) buf.append(",")
          buf.append(EOL)
        }
        def annotationInfoToString(annot: AnnotationInfo): String = {
          val str = new StringBuilder
          str.append(annot.atp.toString())
          if (!annot.args.isEmpty)
          str.append(annot.args.mkString("(", ",", ")"))
          if (!annot.assocs.isEmpty)
          for (((name, value), index) <- annot.assocs.zipWithIndex) {
            if (index > 0)
            str.append(", ")
            str.append(name).append(" = ").append(value)
          }
          str.toString
        }
        def symflags(tree: Tree): String = {
          val sym = tree.symbol
          val buf = new StringBuffer
          if (sym hasFlag IMPLICIT     ) buf.append(" | IMPLICIT")
          if (sym hasFlag FINAL        ) buf.append(" | FINAL")
          if (sym hasFlag PRIVATE      ) buf.append(" | PRIVATE")
          if (sym hasFlag PROTECTED    ) buf.append(" | PROTECTED")

          if (sym hasFlag SEALED       ) buf.append(" | SEALED")
          if (sym hasFlag OVERRIDE     ) buf.append(" | OVERRIDE")
          if (sym hasFlag CASE         ) buf.append(" | CASE")
          if (sym hasFlag ABSTRACT     ) buf.append(" | ABSTRACT")

          if (sym hasFlag DEFERRED     ) buf.append(" | DEFERRED")
          if (sym hasFlag METHOD       ) buf.append(" | METHOD")
          if (sym hasFlag MODULE       ) buf.append(" | MODULE")
          if (sym hasFlag INTERFACE    ) buf.append(" | INTERFACE")

          if (sym hasFlag MUTABLE      ) buf.append(" | MUTABLE")
          if (sym hasFlag PARAM        ) buf.append(" | PARAM")
          if (sym hasFlag PACKAGE      ) buf.append(" | PACKAGE")
          if (sym hasFlag DEPRECATED   ) buf.append(" | DEPRECATED")

          if (sym hasFlag COVARIANT    ) buf.append(" | COVARIANT")
          if (sym hasFlag CAPTURED     ) buf.append(" | CAPTURED")
          if (sym hasFlag BYNAMEPARAM  ) buf.append(" | BYNAMEPARAM")
          if (sym hasFlag CONTRAVARIANT) buf.append(" | CONTRVARIANT")
          if (sym hasFlag LABEL        ) buf.append(" | LABEL")
          if (sym hasFlag INCONSTRUCTOR) buf.append(" | INCONSTRUCTOR")
          if (sym hasFlag ABSOVERRIDE  ) buf.append(" | ABSOVERRIDE")
          if (sym hasFlag LOCAL        ) buf.append(" | LOCAL")
          if (sym hasFlag JAVA         ) buf.append(" | JAVA")
          if (sym hasFlag SYNTHETIC    ) buf.append(" | SYNTHETIC")
          if (sym hasFlag STABLE       ) buf.append(" | STABLE")
          if (sym hasFlag STATIC       ) buf.append(" | STATIC")

          if (sym hasFlag CASEACCESSOR ) buf.append(" | CASEACCESSOR")
          if (sym hasFlag TRAIT        ) buf.append(" | TRAIT")
          if (sym hasFlag DEFAULTPARAM ) buf.append(" | DEFAULTPARAM")
          if (sym hasFlag BRIDGE       ) buf.append(" | BRIDGE")
          if (sym hasFlag ACCESSOR     ) buf.append(" | ACCESSOR")

          if (sym hasFlag SUPERACCESSOR) buf.append(" | SUPERACCESSOR")
          if (sym hasFlag PARAMACCESSOR) buf.append(" | PARAMACCESSOR")
          if (sym hasFlag MODULEVAR    ) buf.append(" | MODULEVAR")
          if (sym hasFlag SYNTHETICMETH) buf.append(" | SYNTHETICMETH")
          if (sym hasFlag MONOMORPHIC  ) buf.append(" | MONOMORPHIC")
          if (sym hasFlag LAZY         ) buf.append(" | LAZY")

          if (sym hasFlag IS_ERROR     ) buf.append(" | IS_ERROR")
          if (sym hasFlag OVERLOADED   ) buf.append(" | OVERLOADED")
          if (sym hasFlag LIFTED       ) buf.append(" | LIFTED")

          if (sym hasFlag MIXEDIN      ) buf.append(" | MIXEDIN")
          if (sym hasFlag EXISTENTIAL  ) buf.append(" | EXISTENTIAL")

          if (sym hasFlag EXPANDEDNAME ) buf.append(" | EXPANDEDNAME")
          if (sym hasFlag IMPLCLASS    ) buf.append(" | IMPLCLASS")
          if (sym hasFlag PRESUPER     ) buf.append(" | PRESUPER")
          if (sym hasFlag TRANS_FLAG   ) buf.append(" | TRANS_FLAG")
          if (sym hasFlag LOCKED       ) buf.append(" | LOCKED")

          val annots = ", annots=" + (
            if (!sym.annotations.isEmpty)
            sym.annotations.map(annotationInfoToString).mkString("[", ",", "]")
            else
            tree.asInstanceOf[MemberDef].mods.annotations)
          (if (buf.length() > 2) buf.substring(3)
           else "0") + ", // flags=" + flagsToString(sym.flags) + annots
        }
        
        def nodeinfo(tree: Tree): String =
        if (infolevel == InfoLevel.Quiet) ""
        else {
          val buf = new StringBuilder(" // sym=" + tree.symbol)
          if (tree.hasSymbol) {
            if (tree.symbol.isPrimaryConstructor)
            buf.append(", isPrimaryConstructor")
            else if (tree.symbol.isConstructor)
            buf.append(", isConstructor")
            if (tree.symbol != NoSymbol)
            buf.append(", sym.owner=" + tree.symbol.owner)
            buf.append(", sym.tpe=" + tree.symbol.tpe)
          }
          buf.append(", tpe=" + tree.tpe)
          if (tree.tpe != null) {
            var sym = tree.tpe.termSymbol
            if (sym == NoSymbol) sym = tree.tpe.typeSymbol
            buf.append(", tpe.sym=" + sym)
            if (sym != NoSymbol) {
              buf.append(", tpe.sym.owner=" + sym.owner)
              if ((infolevel > InfoLevel.Normal) &&
                  !(sym.owner eq definitions.ScalaPackageClass) &&
                  !sym.isModuleClass && !sym.isPackageClass &&
                  !sym.hasFlag(JAVA)) {
                val members = for (m <- tree.tpe.decls.toList)
                yield m.toString() + ": " + m.tpe + ", "
                buf.append(", tpe.decls=" + members)
              }
            }
          }
          buf.toString
        }
        
        def nodeinfo2(tree: Tree): String = {if (comma) "," else ""} + nodeinfo(tree)

        def isTupleClass(symbol:Symbol) :Boolean = {
          if (symbol ne null) {
            symbol.ownerChain.map{_.rawname.decode} match {
              case List(a, "scala", "<root>") if a.startsWith("Tuple") => true
              case _ => false
            }
          } else false
        }

        if (visited.contains(tree)) {
          return
        } else {
          visited += tree
        }

        astPath push tree
        
        tree match {
          case AppliedTypeTree(tpt, args) =>
            println("AppliedTypeTree(" + nodeinfo(tree))
            traverse(tpt, level + 1, true)
            if (args.isEmpty) println("  List() // no argument")
            else {
              val n = args.length
              println("  List( // " + n + " arguments(s)")
              for (i <- 0 until n) traverse(args(i), level + 2, i < n-1)
              println("  )")
            }
            printcln(")")

          case Apply(fun, args) =>
            println("Apply(" + nodeinfo(tree))
            traverse(fun, level + 1, true)
            if (args.isEmpty) println("  List() // no argument")
            else {
              val n = args.length
              println("  List( // " + n + " argument(s)")
              for (i <- 0 until n) traverse(args(i), level + 2, i < n-1)
              println("  )")
            }
            printcln(")")

          case ApplyDynamic(fun, args) =>
            println("ApplyDynamic(" + nodeinfo(tree))
            traverse(fun, level + 1, true)
            if (args.isEmpty) println("  List() // no argument")
            else {
              val n = args.length
              println("  List( // " + n + " argument(s)")
              for (i <- 0 until n) traverse(args(i), level + 2, i < n-1)
              println("  )")
            }
            printcln(")")

          case Block(stats, expr) =>
            println("Block(" + nodeinfo(tree))
            if (stats.isEmpty) println("  List(), // no statement")
            else {
              val n = stats.length
              println("  List( // " + n + " statement(s)")
              for (i <- 0 until n) traverse(stats(i), level + 2, i < n-1)
              println("  ),")
            }
            traverse(expr, level + 1, false)
            printcln(")")
            
          case ModuleDef(mods, name, impl) =>
            val scope = ScalaScope(getBoundsToken(offset(tree)))
            scopes.top.addScope(scope)

            val dfn = ScalaDfn(ScalaSymbol(tree.symbol), getIdToken(tree), ElementKind.MODULE, scope, fo)
            if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

            scopes push scope
            traverse(impl, level + 1, false)
            scopes pop
            
          case ClassDef(mods, name, tparams, impl) =>
            val scope = ScalaScope(getBoundsToken(offset(tree)))
            scopes.top.addScope(scope)

            (if (mods.isTrait) "trait " else "class ")

            val dfn = ScalaDfn(ScalaSymbol(tree.symbol), getIdToken(tree), ElementKind.CLASS, scope, fo)
            if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

            scopes push scope

            println("ClassDef(" + nodeinfo(tree))
            println("  " + symflags(tree))
            println("  \"" + name + "\",")
            if (tparams.isEmpty) println("  List(), // no type parameter")
            else {
              val n = tparams.length
              println("  List( // " + n + " type parameter(s)")
              for (i <- 0 until n) traverse(tparams(i), level + 2, i < n-1)
              println("  ),")
            }
            traverse(impl, level + 1, false)
            printcln(")")
            scopes pop
            
          case DefDef(mods, name, tparams, vparamss, tpt, rhs) =>
            val scope = ScalaScope(getBoundsToken(offset(tree)))
            scopes.top.addScope(scope)

            val kind = if (tree.symbol.isConstructor) ElementKind.CONSTRUCTOR else ElementKind.METHOD

            val dfn = ScalaDfn(ScalaSymbol(tree.symbol), getIdToken(tree), kind, scope, fo)
            if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

            scopes push scope
 
            println("DefDef(" + nodeinfo(tree))
            println("  " + symflags(tree))
            println("  \"" + name + "\",")
            if (tparams.isEmpty) println("  List(), // no type parameter")
            else {
              val n = tparams.length
              println("  List( // " + n + " type parameter(s)")
              for (i <- 0 until n)
              traverse(tparams(i), level + 2, i < n-1)
              println("  ),")
            }
            val n = vparamss.length
            if (n == 1 && vparamss(0).isEmpty) println("  List(List()), // no parameter")
            else {
              println("  List(")
              for (i <- 0 until n) {
                val m = vparamss(i).length
                println("    List( // " + m + " parameter(s)")
                for (j <- 0 until m) traverse(vparamss(i)(j), level + 3, j < m-1)
                println("    )")
              }
              println("  ),")
            }
            println("  " + tpt + ",")
            traverse(rhs, level + 1, false)
            printcln(")")

            scopes pop

          case EmptyTree =>
            printcln("EmptyTree")

          case Ident(name) =>
            printcln("Ident(\"" + name + "\")" + nodeinfo2(tree))

          case Literal(value) =>
            printcln("Literal(" + value + ")")

          case New(tpt) =>
            println("New(" + nodeinfo(tree))
            traverse(tpt, level + 1, false)
            printcln(")")

          case Select(qualifier, selector) =>
            /**
             * For error Select tree, for example a.p, the error part's offset will be set to 'p',
             * The tree.qualifier() part's offset will be 'a'
             */
            val symbol = tree.symbol
            val ref = ScalaRef(ScalaSymbol(symbol), getIdToken(tree), ElementKind.METHOD)
            if (symbol != null && symbol == NoSymbol && maybeType != None) {
              //ref.setResultType(maybeType)
            }
            if (scopes.top.addRef(ref)) info("\tAdded: ", ref)

            /**
             * For error Select tree, the qual type may stored, try to fetch it now
             */
            def guessMaybeType {
              val qualSym = qualifier.symbol
              if (qualSym != null && qualSym == NoSymbol && global != null) {
                maybeType = global.selectTypeErrors.get(tree)
              }
            }
            
            qualifier match {
              case Ident(name) => guessMaybeType
              case Apply(fun, args) => guessMaybeType
              case _ =>
            }

            println("Select(" + nodeinfo(tree))
            traverse(qualifier, level + 1, true)
            printcln("  \"" + selector + "\")")

            maybeType = None

          case Super(qual, mix) =>
            printcln("Super(\"" + qual + "\", \"" + mix + "\")" + nodeinfo2(tree))

          case Template(parents, self, body) =>
            println("Template(" + nodeinfo(tree))
            println("  " + parents.map(p =>
                if (p.tpe ne null) p.tpe.typeSymbol else "null-" + p
              ) + ", // parents")
            traverse(self, level + 1, true)
            if (body.isEmpty) println("  List() // no body")
            else {
              val n = body.length
              println("  List( // body")
              for (i <- 0 until n) traverse(body(i), level + 2, i < n-1)
              println("  )")
            }
            printcln(")")

          case This(qual) =>
            println("This(\"" + qual + "\")" + nodeinfo2(tree))

          case TypeApply(fun, args) =>
            println("TypeApply(" + nodeinfo(tree))
            traverse(fun, level + 1, true)
            if (args.isEmpty) println("  List() // no argument")
            else {
              val n = args.length
              println("  List(")
              for (i <- 0 until n)
              traverse(args(i), level + 1, i < n-1)
              println("  )")
            }
            printcln(")")

          case TypeTree() =>
            tree.symbol match {
              case null =>
                // * in case of: <type ?>
                //println("Null symbol found, tree is:" + tree)
              case NoSymbol =>
                // * type tree in case def, for example: case Some(_),
                // * since the symbol is NoSymbol, we should visit its original type
                val original = tree.asInstanceOf[TypeTree].original
                if (original != null && original != tree && !isTupleClass(original.symbol)) {
                  traverse(original, level, false)
                }
              case symbol =>
                // * We'll drop tuple type, since all elements in tuple have their own type trees:
                // * for example: val (a, b), where (a, b) as a whole has a type tree, but we only
                // * need binding trees of a and b
                if (!isTupleClass(symbol)) {
                  val ref = ScalaRef(ScalaSymbol(symbol), getIdToken(tree), ElementKind.CLASS)
                  if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
                  //if (original != tree) visit(tree.original));
                }
            }
            
            printcln("TypeTree()" + nodeinfo2(tree))

          case Typed(expr, tpt) =>
            println("Typed(" + nodeinfo(tree))
            traverse(expr, level + 1, true)
            traverse(tpt, level + 1, false)
            printcln(")")

          case ValDef(mods, name, tpt, rhs) =>
            val scope = ScalaScope(getBoundsToken(offset(tree)))
            scopes.top.addScope(scope)

            val kind = getCurrentParent match {
              case _:Template => ElementKind.FIELD
              case _:DefDef => ElementKind.PARAMETER
              case _ => ElementKind.VARIABLE
            }

            // special case for: val (a, b, c) = (1, 2, 3)
            if (!isTupleClass(tpt.symbol)) {
              val dfn = ScalaDfn(ScalaSymbol(tree.symbol), getIdToken(tree), kind, scope, fo)
              if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)
            }

            scopes push scope
            println("ValDef(" + nodeinfo(tree))
            println("  " + symflags(tree))
            println("  \"" + name + "\",")
            traverse(tpt, level + 1, true) // tpe is usually a TypeTree
            traverse(rhs, level + 1, false)
            printcln(")")
            scopes.pop

          case PackageDef(name, stats) =>
            val scope = ScalaScope(getBoundsToken(offset(tree)))
            scopes.top.addScope(scope)

            val dfn = ScalaDfn(ScalaSymbol(tree.symbol), getIdToken(tree), ElementKind.PACKAGE, scope, fo)
            if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

            scopes push scope
            println("PackageDef("+name+", ")
            for (stat <- stats) traverse(stat, level + 1, false)
            printcln(")")
            scopes pop
            
          case _ => tree match {
              case p: Product =>
                if (p.productArity != 0) {
                  println(p.productPrefix+"(")
                  for (elem <- (0 until p.productArity) map p.productElement) {
                    def printElem(elem: Any, level: Int): Unit = elem match {
                      case t: Tree =>
                        traverse(t, level, false)
                      case xs: List[_] =>
                        print("List(")
                        for (x <- xs) printElem(x, level+1)
                        printcln(")")
                      case _ =>
                        println(elem.toString)
                    }
                    printElem(elem, level+1)
                  }
                  printcln(")")
                } else printcln(p.productPrefix)
            }
        }

        astPath pop
      }
      
      buf setLength 0
      traverse(tree, 0, false)
      rootScope.debugPrintTokens(th)
      buf.toString
    }
  }

  /*_
   protected def visit(trees:List[Tree]) :Unit = {
   trees.foreach {
   case x:Tree => visit(x)
   case x:Tree => visit(x)
   case x:List[_] => visit(x)
   case x:Tuple2[_, _] =>
   /*
    System.out.println("Visit Tuple: " + tree + " class=" + tree.getClass().getCanonicalName());

    Object o1 = ((Tuple2) tree)._1();
    if (o1 != null) {
    System.out.println("Visit Tuple: " + o1 + " class=" + o1.getClass().getCanonicalName());
    }
    Object o2 = ((Tuple2) tree)._2();
    if (o2 != null) {
    System.out.println("Visit Tuple: " + o2 + " class=" + o2.getClass().getCanonicalName());
    }
    */
   case x => println("Try to visit unknown: " + x + " class=" + x.getClass.getCanonicalName);
   }
   }

   protected def visit(tree:global.Tree) :Unit = {
   if (tree == null) {
   return
   }

   if (offset(tree) == -1) {
   /** It may be EmptyTree, emptyValDef$, or remote TypeTree which presents an inferred Type etc */
   return
   }

   /**
    * @Note: For some reason, or bug in Scala's native compiler, the tree will
    * be recursively linked to itself via childern. Which causes infinite loop,
    * We have to avoid this happens:
    */
   if (visited.contains(tree)) {
   //System.out.println("Detected a possible infinite loop of visiting: " + tree);
   return
   } else {
   visited += tree
   }

   enter(tree)
   try {
   if (tree instanceof PackageDef) {
   visitPackageDef((PackageDef) tree);
   } else if (tree instanceof ClassDef) {
   visitClassDef((ClassDef) tree);
   } else if (tree instanceof ModuleDef) {
   visitModuleDef((ModuleDef) tree);
   } else if (tree instanceof ValDef) {
   visitValDef((ValDef) tree);
   } else if (tree instanceof DefDef) {
   visitDefDef((DefDef) tree);
   } else if (tree instanceof TypeDef) {
   visitTypeDef((TypeDef) tree);
   } else if (tree instanceof LabelDef) {
   visitLabelDef((LabelDef) tree);
   } else if (tree instanceof Import) {
   visitImport((Import) tree);
   //            } else if (tree instanceof Annotation) {
   //                visitAnnotation((Annotation) tree);
   } else if (tree instanceof Template) {
   visitTemplate((Template) tree);
   } else if (tree instanceof Block) {
   visitBlock((Block) tree);
   } else if (tree instanceof Match) {
   visitMatch((Match) tree);
   } else if (tree instanceof CaseDef) {
   visitCaseDef((CaseDef) tree);
   } else if (tree instanceof Sequence) {
   visitSequence((Sequence) tree);
   } else if (tree instanceof Alternative) {
   visitAlternative((Alternative) tree);
   } else if (tree instanceof Star) {
   visitStar((Star) tree);
   } else if (tree instanceof Bind) {
   visitBind((Bind) tree);
   } else if (tree instanceof UnApply) {
   visitUnApply((UnApply) tree);
   } else if (tree instanceof ArrayValue) {
   visitArrayValue((ArrayValue) tree);
   } else if (tree instanceof Function) {
   visitFunction((Function) tree);
   } else if (tree instanceof Assign) {
   visitAssign((Assign) tree);
   } else if (tree instanceof If) {
   visitIf((If) tree);
   } else if (tree instanceof Return) {
   visitReturn((Return) tree);
   } else if (tree instanceof Try) {
   visitTry((Try) tree);
   } else if (tree instanceof Throw) {
   visitThrow((Throw) tree);
   } else if (tree instanceof New) {
   visitNew((New) tree);
   } else if (tree instanceof Typed) {
   visitTyped((Typed) tree);
   } else if (tree instanceof TypeApply) {
   visitTypeApply((TypeApply) tree);
   } else if (tree instanceof Apply) {
   visitApply((Apply) tree);
   } else if (tree instanceof ApplyDynamic) {
   visitApplyDynamic((ApplyDynamic) tree);
   } else if (tree instanceof Super) {
   visitSuper((Super) tree);
   } else if (tree instanceof This) {
   visitThis((This) tree);
   } else if (tree instanceof Select) {
   visitSelect((Select) tree);
   } else if (tree instanceof Ident) {
   visitIdent((Ident) tree);
   } else if (tree instanceof Literal) {
   visitLiteral((Literal) tree);
   } else if (tree instanceof TypeTree) {
   visitTypeTree((TypeTree) tree);
   } else if (tree instanceof Annotated) {
   visitAnnotated((Annotated) tree);
   } else if (tree instanceof SingletonTypeTree) {
   visitSingletonTypeTree((SingletonTypeTree) tree);
   } else if (tree instanceof SelectFromTypeTree) {
   visitSelectFromTypeTree((SelectFromTypeTree) tree);
   } else if (tree instanceof CompoundTypeTree) {
   visitCompoundTypeTree((CompoundTypeTree) tree);
   } else if (tree instanceof AppliedTypeTree) {
   visitAppliedTypeTree((AppliedTypeTree) tree);
   } else if (tree instanceof TypeBoundsTree) {
   visitTypeBoundsTree((TypeBoundsTree) tree);
   } else if (tree instanceof ExistentialTypeTree) {
   visitExistentialTypeTree((ExistentialTypeTree) tree);
   } else if (tree instanceof StubTree) {
   visitStubTree((StubTree) tree);
   } else if (tree instanceof DocDef) {
   visitDocDef((DocDef) tree)
   } else {
   println("Visit Unknow tree: " + tree + " class=" + tree.getClass.getCanonicalName)
   }
   } catch {
   case ex:Throwable => println("Exception when visit tree: " + tree + "\n" + ex.getMessage)
   }
   exit(tree)
   }
   */
  
  // ---- Helper methods
  protected def getCurrentParent :Tree = {
    assert(astPath.size >= 2)
    astPath(astPath.size - 2)
  }

  protected def getAstPathString :String = {
    val sb = new StringBuilder
    val itr = astPath.iterator
    while (itr.hasNext) {
      sb.append(itr.next.getClass.getSimpleName)
      if (itr.hasNext) {
        sb.append(".")
      }
    }

    sb.toString
  }

  protected def enter(tree:Tree) :Unit = {
    indentLevel += 1
    astPath.push(tree)

    if (debug) {
      debugPrintAstPath(tree)
    }
  }

  protected def exit(node:Tree) :Unit = {
    indentLevel -= 1
    astPath.pop
  }

  protected def offset(tree:Tree) :Int = {
    offset(tree.pos.offset)
  }

  protected def offset(symbol:Symbol) :Unit = {
    offset(symbol.pos.offset)
  }

  protected def offset(intOption:Option[Int]) :Int = {
    intOption match {
      case None => -1
      case Some(i) => i
    }
  }

  /**
   * @Note: nameNode may contains preceding void productions, and may also contains
   * following void productions, but nameString has stripped the void productions,
   * so we should adjust nameRange according to name and its length.
   */
  protected def getIdToken(tree:Tree) :Option[Token[TokenId]] = {
    val symbol = tree.symbol
    if (symbol == null) {
      return None
    }

    /** Do not use symbol.nameString() here, for example, a constructor Dog()'s nameString maybe "this" */
    //String name = symbol.idString();
    val name = symbol.rawname.decode.trim
    val offset1 = offset(tree)
    val ts = ScalaLexUtil.getTokenSequence(th, offset1)
    ts.move(offset1)
    if (!ts.moveNext && !ts.movePrevious) {
      assert(false, "Should not happen!")
    }

    var altToken :Token[TokenId] = null
    var token = (tree, name) match {
      case (x:This, _)     => ScalaLexUtil.findNext(ts, ScalaTokenId.This)
      case (_, "this")     => ScalaLexUtil.findNext(ts, ScalaTokenId.This)
      case (x:Super, _)    => ScalaLexUtil.findNext(ts, ScalaTokenId.Super)
      case (_, "super")    => ScalaLexUtil.findNext(ts, ScalaTokenId.Super)
      case (_, "expected") => ts.token
      case (_, "foreach")  =>
        val tk = ScalaLexUtil.findNext(ts, ScalaTokenId.Identifier)
        altToken = tk
        if (tk != null && tk.text.toString.equals("foreach")) {
          ScalaLexUtil.findNext(ts, ScalaTokenId.LArrow)
        } else {
          tk
        }
      case (_, "_") => ScalaLexUtil.findNext(ts, ScalaTokenId.Wild)
      case (_, _) if name.startsWith("<error") => ts.token.id match {
          case ScalaTokenId.Dot =>
            // a. where, offset is at .
            ScalaLexUtil.findPrevious(ts, ScalaTokenId.Identifier)
          case _ =>
            // a.p where, offset is at p
            ScalaLexUtil.findNextIn(ts, ScalaLexUtil.PotentialIdTokens)
        }
      case _ => 
        ScalaLexUtil.findNextIn(ts, ScalaLexUtil.PotentialIdTokens) match {
          case x:Token[_] if x.text.toString.trim == name => x
          case _ => null
        }
    }

    if (token != null && token.isFlyweight) {
      token = ts.offsetToken
    }

    // root expr is just a container
    //    if (!exprs.peek().isRoot()) {
    //      exprs.peek().addToken(token);
    //    }
    if (token == null) None else Some(token)
  }

  protected def getBoundsTokens(offset:Int, endOffset:Int) :Array[Token[TokenId]] = {
    Array(getBoundsToken(offset), getBoundsEndToken(endOffset))
  }

  protected def getBoundsToken(offset:Int) :Token[TokenId]  = {
    if (offset == -1) {
      return null
    }

    val ts = ScalaLexUtil.getTokenSequence(th, offset)

    ts.move(offset)
    if (!ts.moveNext && !ts.movePrevious) {
      assert(false, "Should not happen!")
    }

    val startToken = ScalaLexUtil.findPreviousNonWsNonComment(ts) match {
      case x if x.isFlyweight => ts.offsetToken
      case x => x
    }

    if (startToken == null) {
      println("null start token(" + offset + ")")
    }

    startToken
  }

  protected def getBoundsEndToken(endOffset:Int) :Token[TokenId] = {
    if (endOffset == -1) {
      return null
    }

    val ts = ScalaLexUtil.getTokenSequence(th, endOffset)

    ts.move(endOffset)
    if (!ts.movePrevious && !ts.moveNext) {
      assert(false, "Should not happen!")
    }
    
    val endToken = ScalaLexUtil.findPreviousNonWsNonComment(ts) match {
      case x if x.isFlyweight => ts.offsetToken
      case x => x
    }

    endToken
  }

  protected def info(message:String) :Unit = {
    if (!debug) {
      return
    }

    println(message)
  }

  protected def info(message:String, item:AstItem[Symbols#Symbol]) :Unit = {
    if (!debug) {
      return
    }

    print(message)
    println(item)
  }

  protected def debugPrintAstPath(tree:Tree) :Unit = {
    if (!debug) {
      return
    }

    val idTokenStr = getIdToken(tree) match {
      case None => "<null>"
      case Some(x) => x.text.toString
    }

    val symbol = tree.symbol
    val symbolStr = if (symbol == null) "<null>" else symbol.toString

    val pos = tree.pos

    println(getAstPathString + "(" + offset(pos.line) + ":" + offset(pos.column) + ")" + ", idToken: " + idTokenStr + ", symbol: " + symbolStr);
  }
}
