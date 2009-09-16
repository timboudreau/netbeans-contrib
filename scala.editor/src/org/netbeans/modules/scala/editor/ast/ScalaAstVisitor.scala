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

import java.io.File
import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy, TokenSequence}
import org.netbeans.modules.csl.api.{ElementKind, Modifier}
import org.netbeans.modules.parsing.api.Snapshot
import org.openide.filesystems.{FileObject, FileUtil}

import org.netbeans.api.language.util.ast.{AstItem, AstScope}
import org.netbeans.modules.scala.editor.ScalaGlobal
import org.netbeans.modules.scala.editor.lexer.{ScalaLexUtil, ScalaTokenId}

import scala.tools.nsc.{CompilationUnits, Global}
import scala.tools.nsc.ast.Trees
import scala.tools.nsc.symtab.{Symbols, SymbolTable, Flags}
import scala.tools.nsc.symtab.Flags._
import scala.tools.nsc.util.{BatchSourceFile, Position, SourceFile}
import scala.collection.mutable.{Stack, HashSet}

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

  val global: ScalaGlobal
  import global._

  val EOL = System.getProperty("line.separator", "\n")

  private var debug: Boolean = _
  private var indentLevel: Int = _
  private var astPath = new Stack[Tree]
  private var visited = new HashSet[Tree]

  private var scopes = new Stack[AstScope]
  private var rootScope: ScalaRootScope = _

  private var fo: Option[FileObject] = _
  private var th: TokenHierarchy[_] = _
  private var srcFile: SourceFile = _
  private var docLength: Int = _

  def reset: Unit = {
    this.scopes.clear
    this.visited.clear
    this.astPath.clear
  }
  
  def visit(unit: CompilationUnit, th: TokenHierarchy[_]): ScalaRootScope = {
    this.th = th
    this.srcFile = unit.source
    this.docLength = srcFile.content.size
    this.fo = if (srcFile ne null) {
      val file = new File(srcFile.path)
      if (file != null && file.exists) { // it's a real file instead of archive file
        FileUtil.toFileObject(file) match {
          case null => None
          case x => Some(x)
        }
      } else None
    } else None

    reset
    rootScope = ScalaRootScope(Some(unit), getBoundsTokens(0, srcFile.length))
    scopes push rootScope
      
    unit match {
      case u: RichCompilationUnit => visitImports(u)
      case _ =>
    }

    (new TreeVisitor) visit unit.body
    rootScope
  }

  def visitImports(unit: RichCompilationUnit) = {
    def visitContextTree(ct: ContextTree): Unit = {
      val c = ct.context
      for (importInfo <- c.imports) {
        importInfo.tree match {
          case me@Import(qual, selectors) =>
            val sym = qual.symbol
            if (sym != null) {
              val idToken = getIdToken(qual)
              val ref = ScalaRef(sym, idToken, if (sym.hasFlag(Flags.PACKAGE)) ElementKind.PACKAGE else ElementKind.OTHER, fo)

              if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
            }
            
            //println("import: qual=" + qual.tpe + ", selectors=" + selectors.mkString("{", ",", "}" ))
            selectors foreach {
              case (null, null) =>
              case (x, y) if x != nme.WILDCARD =>
                val xsym = importedSymbol(me, x)
                if (xsym != null) {
                  val idToken = getIdToken(me, x.decode)
                  val ref = ScalaRef(xsym, idToken, ElementKind.OTHER, fo)
                  if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
                }

                if (y != null) {
                  val ysym = importedSymbol(me, y)
                  if (ysym != null) {
                    val idToken = getIdToken(me, y.decode)
                    val ref = ScalaRef(ysym, idToken, ElementKind.OTHER, fo)
                    if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
                  }
                }
              case _ =>
            }
        }
      }
      ct.children foreach {visitContextTree _}
    }

    unit.contexts foreach {visitContextTree _}
  }

  /**
   * The symbol with name <code>name</code> imported from import clause <code>tree</code>.
   * We'll find class/trait instead of object first.
   * @bug in scala compiler? why name is always TermName? which means it's object instead of class/trait
   */
  def importedSymbol(tree: Import, name: Name): Symbol = {
    var result: List[Symbol] = Nil
    var renamed = false
    val qual = tree.expr

    var selectors = tree.selectors
    while (selectors != Nil && result == Nil) {
      val (x, y) = selectors.head
      if (y == name.toTermName)
        result = qual.tpe.members filter {_.name.toTermName == x.toTermName}
      else if (x == name.toTermName)
        renamed = true
      else if (x == nme.WILDCARD && !renamed)
        result = qual.tpe.members filter {_.name.toTermName == x.toTermName}
    
      selectors = selectors.tail
    }

    def isProperType(x: Symbol) = x.isType && x.tpe != null && x.tpe != NoType
    result find isProperType getOrElse {if (result.isEmpty) null else result.head}
  }

  object InfoLevel extends Enumeration {val Quiet, Normal, Verbose = Value}
  var infolevel = InfoLevel.Quiet

  class TreeVisitor {

    private val buf = new StringBuilder

    private var qualiferMaybeType: Option[Type] = None

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
            try {
              if (!sym.annotations.isEmpty) sym.annotations.map(annotationInfoToString).mkString("[", ",", "]")
              else tree.asInstanceOf[MemberDef].mods.annotations
            } catch {case _ => ""})
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

        def isTupleClass(symbol: Symbol): Boolean = {
          if (symbol ne null) {
            symbol.ownerChain.map{_.rawname.decode} match {
              case List(a, "scala", "<root>") if a.startsWith("Tuple") => true
              case _ => false
            }
          } else false
        }

        // ----- begin visiting

        if (!visited.add(tree)) return // has visited
        
        astPath push tree
        
        tree match {
          case PackageDef(name, stats) =>
            val scope = ScalaScope(getBoundsTokens(tree))
            scopes.top.addScope(scope)

            val dfn = ScalaDfn(tree.symbol, getIdToken(tree), ElementKind.PACKAGE, scope, fo)
            if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

            scopes push scope
            //println("PackageDef("+name+", ")
            for (stat <- stats) traverse(stat, level + 1, false)
            //printcln(")")
            scopes pop

          case ClassDef(mods, name, tparams, impl) =>
            val scope = ScalaScope(getBoundsTokens(tree))
            scopes.top.addScope(scope)

            (if (mods.isTrait) "trait " else "class ")

            val dfn = ScalaDfn(tree.symbol, getIdToken(tree), ElementKind.CLASS, scope, fo)
            if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

            scopes push scope

            //println("ClassDef(" + nodeinfo(tree))
            //println("  " + symflags(tree))
            //println("  \"" + name + "\",")
            if (tparams.isEmpty) {} //println("  List(), // no type parameter")
            else {
              val n = tparams.length
              //println("  List( // " + n + " type parameter(s)")
              for (i <- 0 until n) traverse(tparams(i), level + 2, i < n-1)
              //println("  ),")
            }
            traverse(impl, level + 1, false)
            //printcln(")")
            scopes pop

          case ModuleDef(mods, name, impl) =>
            val scope = ScalaScope(getBoundsTokens(tree))
            scopes.top.addScope(scope)

            val dfn = ScalaDfn(tree.symbol, getIdToken(tree), ElementKind.MODULE, scope, fo)
            if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

            scopes push scope
            traverse(impl, level + 1, false)
            scopes pop

          case DefDef(mods, name, tparams, vparamss, tpt, rhs) =>
            val scope = ScalaScope(getBoundsTokens(tree))
            scopes.top.addScope(scope)

            val kind = if (tree.symbol.isConstructor) ElementKind.CONSTRUCTOR else ElementKind.METHOD

            val dfn = ScalaDfn(tree.symbol, getIdToken(tree), kind, scope, fo)
            if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

            scopes push scope

            //println("DefDef(" + nodeinfo(tree))
            //println("  " + symflags(tree))
            //println("  \"" + name + "\",")
            if (tparams.isEmpty) {}//println("  List(), // no type parameter")
            else {
              val n = tparams.length
              //println("  List( // " + n + " type parameter(s)")
              for (i <- 0 until n) traverse(tparams(i), level + 2, i < n-1)
              //println("  ),")
            }
            val n = vparamss.length
            if (n == 1 && vparamss(0).isEmpty) {}//println("  List(List()), // no parameter")
            else {
              //println("  List(")
              for (i <- 0 until n) {
                val m = vparamss(i).length
                //println("    List( // " + m + " parameter(s)")
                for (j <- 0 until m) traverse(vparamss(i)(j), level + 3, j < m-1)
                //println("    )")
              }
              //println("  ),")
            }
            traverse(tpt, level, false)
            //println("  " + tpt + ",")
            traverse(rhs, level + 1, false)
            //printcln(")")

            scopes pop

          case ValDef(mods, name, tpt, rhs) =>
            val scope = ScalaScope(getBoundsTokens(tree))
            scopes.top.addScope(scope)

            val kind = getCurrentParent match {
              case _: Template => ElementKind.FIELD
              case _: DefDef => ElementKind.PARAMETER
              case _: Function => ElementKind.PARAMETER
              case _ => ElementKind.VARIABLE
            }

            // special case for: val (a, b, c) = (1, 2, 3)
            if (!isTupleClass(tpt.symbol)) {
              val dfn = ScalaDfn(tree.symbol, getIdToken(tree, name.decode.trim), kind, scope, fo)
              if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)
            }

            scopes push scope
            //println("ValDef(" + nodeinfo(tree))
            //println("  " + symflags(tree))
            //println("  \"" + name + "\",")
            traverse(tpt, level, false) // tpe is usually a TypeTree
            traverse(rhs, level + 1, false)
            //printcln(")")
            scopes pop

          case Bind(name, body) =>
            val scope = ScalaScope(getBoundsTokens(tree))
            scopes.top.addScope(scope)

            /**
             * case c => println(c), will define a bind val "c"
             */
            val dfn = ScalaDfn(tree.symbol, getIdToken(tree), ElementKind.VARIABLE, scope, fo)
            if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

            traverse(body, level, false)

          case me@TypeTree() =>
            tree.symbol match {
              case null =>
                // * in case of: <type ?>
                //println("Null symbol found, tree is:" + tree)
              case NoSymbol =>
                // * type tree in case def, for example: case Some(_),
                // * since the symbol is NoSymbol, we should visit its original type
                val original = me.original
                if (original != null && original != tree && !isTupleClass(original.symbol)) {
                  traverse(original, level, false)
                }
              case sym =>
                // * We'll drop tuple type, since all elements in tuple have their own type trees:
                // * for example: val (a, b), where (a, b) as a whole has a type tree, but we only
                // * need binding trees of a and b
                if (!isTupleClass(sym)) {
                  val ref = ScalaRef(sym, getIdToken(tree), ElementKind.CLASS, fo)
                  if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
                }
                
                val orig = me.original
                if (orig != null && orig != tree) {
                  
                  (orig, tree.tpe) match {
                    case (AppliedTypeTree(tpt, args), TypeRef(prex, symx, argTpes)) =>
                      // * special case: type and symbols of args may hide in tree.tpe (sometime also in orig.tpe, but not always)
                      // * example code: Array[String]
                      val argsItr = args.iterator
                      val tpesItr = argTpes.iterator
                      while (argsItr.hasNext && tpesItr.hasNext) {
                        val argTree = argsItr.next
                        val argTpe = tpesItr.next
                        val argSym = argTpe.typeSymbol
                        val argName = argTree match {
                          case Ident(name) => name.decode
                          case _ => ""
                        }
                        val ref = ScalaRef(argSym, getIdToken(argTree, argName), ElementKind.CLASS, fo)
                        if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
                      }
                    case _ => traverse(orig, level, false)
                  }
                }
            }

            //printcln("TypeTree()" + nodeinfo2(tree))

          case TypeDef(mods, name, tparams, rhs)  =>
            val scope = ScalaScope(getBoundsTokens(tree))
            scopes.top.addScope(scope)

            val sym = tree.symbol
            if (sym != null && sym != NoSymbol) {
              if (!sym.hasFlag(Flags.SYNTHETIC)) {
                val dfn = ScalaDfn(sym, getIdToken(tree, name.decode.trim), ElementKind.CLASS, scope, fo)
                if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)
              }
            }

            scopes push scope
            
            if (sym !=null && sym != NoSymbol) {
              (rhs, sym.info) match {
                case (TypeBoundsTree(lo, hi), TypeBounds(loTpe, hiTpe)) =>
                  // * specical case: type of lo, hi are hidden in sym.info (not in sym.tpe)
                  // * example code: Array[_ <: String]
                  val loSym = loTpe.typeSymbol
                  val loRef = ScalaRef(loSym, getIdToken(lo, loSym.nameString), ElementKind.CLASS, fo)
                  if (scopes.top.addRef(loRef)) info("\tAdded: ", loRef)

                  val hiSym = hiTpe.typeSymbol
                  val hiRef = ScalaRef(hiSym, getIdToken(hi, hiSym.nameString), ElementKind.CLASS, fo)
                  if (scopes.top.addRef(hiRef)) info("\tAdded: ", hiRef)
                case _ => traverse(rhs, level + 1, true)
              }           
            } else traverse(rhs, level + 1, true)

            tparams foreach {traverse(_, level + 1, true)}

            scopes pop

          case TypeBoundsTree(lo: Tree, hi: Tree) =>
            traverse(lo, level, true)
            traverse(hi, level, true)

          case Select(qualifier, selector) =>
            /**
             * For error Select tree, for example a.p, the error part's offset will be set to 'p',
             * The tree.qualifier() part's offset will be 'a'
             */
            val sym = tree.symbol
            val kind = if (sym hasFlag IMPLICIT) {
              ElementKind.RULE
            } else if (sym hasFlag METHOD) {
              ElementKind.CALL
            } else if (sym hasFlag MODULE) {
              ElementKind.MODULE
            } else {
              ElementKind.FIELD
            }

            // special case for: val (a, b, c) = (e, e, e), where it may be a `tuple.apple` call
            if (!isTupleClass(qualifier.symbol)) {
              val name = selector.decode.trim
              val idToken = getIdToken(tree, name)
              val ref = ScalaRef(sym, idToken, kind, fo)
              /**
               * @Note: this symbol may has wrong tpe, for example, an error tree,
               * to get the proper resultType, we'll check if the qualierMaybeType isDefined
               */
              if (qualiferMaybeType.isDefined) {
                ref.resultType = qualiferMaybeType.get
              }

              if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
            }

            //* is this tree marked as select type error? if so, the qualifier may be below type
            //* @Note: since `selectTypeErrors` are gathered upon `Select` tree, this detecting should happen here only
            qualiferMaybeType = global.selectTypeErrors.get(tree)

            //println("Select(" + nodeinfo(tree))
            traverse(qualifier, level + 1, true)
            //printcln("  \"" + selector + "\")")

            // * reset qualiferMaybeType
            qualiferMaybeType = None

          case Apply(fun, args) =>
            // * this tree's `fun` part is extractly an `Ident` tree, so add ref at Ident(name) instead here
            //println("Apply(" + nodeinfo(tree))
            traverse(fun, level + 1, true)
            if (args.isEmpty) {} //println("  List() // no argument")
            else {
              val n = args.length
              //println("  List( // " + n + " argument(s)")
              for (i <- 0 until n) traverse(args(i), level + 2, i < n-1)
              //println("  )")
            }
            //printcln(")")

          case Ident(name) =>
            val sym = tree.symbol
            if (sym != null) {
              val idToken = getIdToken(tree, name.decode.trim)
              val ref = ScalaRef(sym, idToken, ElementKind.OTHER, fo)

              /**
               * @Note: this symbol may has wrong tpe, for example, an error tree,
               * to get the proper resultType, we'll check if the qualierMaybeType isDefined
               */
              val tpe = sym.tpe
              if (qualiferMaybeType.isDefined) {
                ref.resultType = qualiferMaybeType.get
              }
              
              if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
            }

            //printcln("Ident(\"" + name + "\")" + nodeinfo2(tree))

          case This(qual) =>
            val sym = tree.symbol
            if (sym != null) {
              val idToken = getIdToken(tree, "this")
              val ref = ScalaRef(sym, idToken, ElementKind.OTHER, fo)

              if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
            }
            //println("This(\"" + qual + "\")" + nodeinfo2(tree))

          case Super(qual, mix) =>
            val sym = tree.symbol
            if (sym != null) {
              val idToken = getIdToken(tree, "super")
              val ref = ScalaRef(sym, idToken, ElementKind.OTHER, fo)

              if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
            }
            //printcln("Super(\"" + qual + "\", \"" + mix + "\")" + nodeinfo2(tree))

          case Import(expr, selectors) =>
            // Import tree has been added into context and replaced by EmptyTree in typer phase
            traverse(expr, level, false)
            selectors foreach {
              case (x:Name, y:Name) =>
              case (x:Name, null) =>
              case _ =>
            }

          case Function(vparams, body) =>
            vparams foreach {traverse(_, level, false)}
            traverse(body, level, false)

          case AppliedTypeTree(tpt, args) =>
            //println("AppliedTypeTree(" + nodeinfo(tree))
            traverse(tpt, level + 1, true)
            if (args.isEmpty) {} //println("  List() // no argument")
            else {
              val n = args.length
              //println("  List( // " + n + " arguments(s)")
              for (i <- 0 until n) traverse(args(i), level + 2, i < n-1)
              //println("  )")
            }
            //printcln(")")

          case ApplyDynamic(fun, args) =>
            //println("ApplyDynamic(" + nodeinfo(tree))
            traverse(fun, level + 1, true)
            if (args.isEmpty) {} //println("  List() // no argument")
            else {
              val n = args.length
              //println("  List( // " + n + " argument(s)")
              for (i <- 0 until n) traverse(args(i), level + 2, i < n-1)
              //println("  )")
            }
            //printcln(")")

          case Block(stats, expr) =>
            //println("Block(" + nodeinfo(tree))
            if (stats.isEmpty) {} //println("  List(), // no statement")
            else {
              val n = stats.length
              //println("  List( // " + n + " statement(s)")
              for (i <- 0 until n) traverse(stats(i), level + 2, i < n-1)
              //println("  ),")
            }
            traverse(expr, level + 1, false)
            //printcln(")")
            
          case EmptyTree =>
            //printcln("EmptyTree")

          case Literal(value) =>
            //printcln("Literal(" + value + ")")

          case New(tpt) =>
            //println("New(" + nodeinfo(tree))
            traverse(tpt, level + 1, false)
            //printcln(")")

          case Template(parents, self, body) =>
            parents foreach {traverse(_, level, false)}
            
            //println("Template(" + nodeinfo(tree))
            //println("  " + parents.map(p =>
            //    if (p.tpe ne null) p.tpe.typeSymbol else "null-" + p
            //  ) + ", // parents")
            traverse(self, level + 1, true)
            if (body.isEmpty) {} //println("  List() // no body")
            else {
              val n = body.length
              //println("  List( // body")
              for (i <- 0 until n) traverse(body(i), level + 2, i < n-1)
              //println("  )")
            }
            //printcln(")")

          case TypeApply(fun, args) =>
            //println("TypeApply(" + nodeinfo(tree))
            traverse(fun, level + 1, true)
            if (args.isEmpty) {} //println("  List() // no argument")
            else {
              val n = args.length
              //println("  List(")
              for (i <- 0 until n) traverse(args(i), level + 1, i < n-1)
              //println("  )")
            }
            //printcln(")")

          case Typed(expr, tpt) =>
            //println("Typed(" + nodeinfo(tree))
            traverse(expr, level + 1, true)
            traverse(tpt, level + 1, false)
            //printcln(")")

          case _ => tree match {
              case p: Product =>
                if (p.productArity != 0) {
                  //println(p.productPrefix+"(")
                  for (elem <- (0 until p.productArity) map p.productElement) {
                    def printElem(elem: Any, level: Int): Unit = elem match {
                      case t: Tree =>
                        traverse(t, level, false)
                      case xs: List[_] =>
                        //print("List(")
                        for (x <- xs) printElem(x, level+1)
                        //printcln(")")
                      case _ =>
                        //println(elem.toString)
                    }
                    printElem(elem, level+1)
                  }
                  //printcln(")")
                } else {} //printcln(p.productPrefix)
            }
        }

        astPath pop
      }
      
      buf setLength 0
      traverse(tree, 0, false)
      if (debug) rootScope.debugPrintTokens(th)
      buf.toString
    }
  }

  // ---- Helper methods
  protected def getCurrentParent: Tree = {
    assert(astPath.size >= 2)
    astPath(astPath.size - 2)
  }

  protected def getAstPathString: String = {
    astPath.map{_.getClass.getSimpleName}.mkString(".")
  }

  protected def enter(tree: Tree): Unit = {
    indentLevel += 1
    astPath.push(tree)

    if (debug) debugPrintAstPath(tree)
  }

  protected def exit(node: Tree): Unit = {
    indentLevel -= 1
    astPath.pop
  }

  protected def getOffset(tree: Tree): Int = {
    tree.pos.startOrPoint
  }

  /**
   * @Note: nameNode may contains preceding void productions, and may also contains
   * following void productions, but nameString has stripped the void productions,
   * so we should adjust nameRange according to name and its length.
   */
  protected def getIdToken(tree: Tree, knownName: String = ""): Option[Token[TokenId]] = {
    val sym = tree.symbol
    if (sym == null) return None

    if (sym.hasFlag(Flags.SYNTHETIC)) {
      // @todo
    }

    /** Do not use symbol.nameString or idString) here, for example, a constructor Dog()'s nameString maybe "this" */
    val name = if (knownName.length > 0) knownName else (if (sym != NoSymbol) sym.rawname.decode.trim else "")
    if (name == "") {
      return None
    }


    val pos = tree.pos
    val offset = if (pos.isDefined) pos.startOrPoint else -1
    val endOffset = if (pos.isDefined) pos.endOrPoint else -1
    if (offset == -1) {
      return None
    }
    
    val ts = ScalaLexUtil.getTokenSequence(th, offset) getOrElse {return None}
    
    ts.move(offset)
    if (!ts.moveNext && !ts.movePrevious) {
      assert(false, "Should not happen!")
    }

    var token = tree match {
      case This(qual)  => ScalaLexUtil.findNext(ts, ScalaTokenId.This)
      case Super(qual, mix) => ScalaLexUtil.findNext(ts, ScalaTokenId.Super)
      case _ if name == "this"  => ScalaLexUtil.findNext(ts, ScalaTokenId.This)
      case _ if name == "super" => ScalaLexUtil.findNext(ts, ScalaTokenId.Super)
      case _ if name == "expected" => Some(ts.token)
      case ValDef(mods, namex, tpt, rhs) if sym hasFlag SYNTHETIC =>
        // * is it a placeholder '_' token ?
        ScalaLexUtil.findNext(ts, ScalaTokenId.Wild) find {_.offset(th) <= endOffset}
        
      case Select(qual, selector) if sym hasFlag IMPLICIT =>
        // * for Select tree that is implicit call, will look forward for the nearest item and change its kind to ElementKind.RULE
        rootScope.findNeastItemsAt(th, offset) foreach {_.kind = ElementKind.RULE}
        None
        
      case Select(qual, selector) if name == "apply" =>
        // * for Select tree that is `apple` call, will look forward for the nearest id token
        //val content = getContent(offset, endOffset)
        ScalaLexUtil.findNextIn(ts, ScalaLexUtil.PotentialIdTokens)

      case Select(qual, selector) if endOffset > 0 =>
        // * for Select tree, will look backward from endOffset
        ts.move(endOffset)
        findIdTokenBackward(ts, name, offset, endOffset) match {
          case None =>
            // * bug in scalac, wrong RangePosition for "list filter {...}", the range only contains "list"
            ts.move(endOffset)
            if (ts.moveNext && ts.movePrevious) {
              val end = Math.min(endOffset + 100, docLength - 1)
              findIdTokenForward(ts, name, endOffset, end)
            } else None
          case x => x
        }

      case Import(qual, selectors) =>
        //println("import tree content=" + getContent(offset, endOffset) + ", name=" + name)
        ts.move(endOffset)
        findIdTokenBackward(ts, name, offset, endOffset)
        
      case _ => findIdTokenForward(ts, name, offset, endOffset)
    }

    token match {
      case Some(x) if x.isFlyweight => Some(ts.offsetToken)
      case x => x
    }
  }

  private def findIdTokenForward(ts: TokenSequence[TokenId], name: String, offset: Int, endOffset: Int): Option[Token[TokenId]] = {
    var token = ScalaLexUtil.findNextIn(ts, ScalaLexUtil.PotentialIdTokens)
    var curr = offset + token.get.length
    while (token.isDefined && !tokenNameEquals(token.get, name) && curr <= endOffset) {
      token = if (ts.moveNext) {
        ScalaLexUtil.findNextIn(ts, ScalaLexUtil.PotentialIdTokens)
      } else None
      if (token.isDefined) curr = ts.offset + token.get.length
    }

    token match {
      case Some(x) if tokenNameEquals(x, name) => token
      case _ => None
    }
  }

  private def findIdTokenBackward(ts: TokenSequence[TokenId], name: String, offset: Int, endOffset: Int): Option[Token[TokenId]] = {
    var token = if (ts.movePrevious) {
      ScalaLexUtil.findPreviousIn(ts, ScalaLexUtil.PotentialIdTokens)
    } else None
    var curr = endOffset
    while (token.isDefined && !tokenNameEquals(token.get, name) && curr >= offset) {
      token = if (ts.movePrevious) {
        ScalaLexUtil.findPreviousIn(ts, ScalaLexUtil.PotentialIdTokens)
      } else None
      if (token.isDefined) curr = ts.offset
    }

    token match {
      case Some(x) if tokenNameEquals(x, name) => token
      case _ => None
    }
  }
  
  private def tokenNameEquals(token: Token[_], name: String): Boolean = {
    val text = token.text.toString.trim
    token.id match {
      case ScalaTokenId.SymbolLiteral => text.substring(1, text.length - 1) == name // strip '`'
      case ScalaTokenId.LArrow if name == "foreach" || name == "map" => true
      case ScalaTokenId.Identifier if name == "apply" || name.startsWith("<error") => true // return the first matched identifier token
      case _ if name.endsWith("_=") => text + "_=" == name
      case _ if name == "Sequence" => text == name || text == "Seq" // Seq may have symbol name "Sequence"
      case _ => text == name
    }
  }

  private def getContent(offset: Int, endOffset: Int): CharSequence = {
    if (endOffset > offset && offset > -1) {
      srcFile.content.subSequence(offset, endOffset)
    } else ""
  }

  protected def getBoundsTokens(offset: Int, endOffset: Int): Array[Token[TokenId]] = {
    Array(getBoundsToken(offset).getOrElse(null), getBoundsEndToken(endOffset).getOrElse(null))
  }

  protected def getBoundsTokens(tree: Tree): Array[Token[TokenId]] = {
    val pos = tree.pos
    val (offset, endOffset) = if (tree.pos.isDefined) {
      (pos.startOrPoint, pos.endOrPoint)
    } else (-1, -1)
    
    getBoundsTokens(offset, endOffset)
  }
  
  protected def getBoundsToken(offset: Int): Option[Token[TokenId]]  = {
    if (offset < 0) {
      return None
    }

    val ts = ScalaLexUtil.getTokenSequence(th, offset).getOrElse(return None)
    ts.move(offset)
    if (!ts.moveNext && !ts.movePrevious) {
      assert(false, "Should not happen!")
    }

    val startToken = ScalaLexUtil.findPreviousNoWsNoComment(ts) match {
      case Some(x) if x.isFlyweight => Some(ts.offsetToken)
      case x => x
    }

    if (startToken == None) {
      println("null start token(" + offset + ")")
    }

    startToken
  }

  protected def getBoundsEndToken(endOffset: Int): Option[Token[TokenId]] = {
    if (endOffset == -1) {
      return None
    }

    val ts = ScalaLexUtil.getTokenSequence(th, endOffset).getOrElse{return None}
    ts.move(endOffset)
    if (!ts.movePrevious && !ts.moveNext) {
      assert(false, "Should not happen!")
    }
    
    val endToken = ScalaLexUtil.findPreviousNoWsNoComment(ts) match {
      case Some(x) if x.isFlyweight => Some(ts.offsetToken)
      case x => x
    }

    endToken
  }

  protected def info(message: String): Unit = {
    if (!debug) {
      return
    }

    println(message)
  }

  protected def info(message: String, item: AstItem): Unit = {
    if (!debug) {
      return
    }

    print(message)
    println(item)
  }

  protected def debugPrintAstPath(tree: Tree): Unit = {
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

    println(getAstPathString + "(" + pos.line + ":" + pos.column + ")" + ", idToken: " + idTokenStr + ", symbol: " + symbolStr)
  }

  /**
   * Used when endOffset of tree is not available.
   * @Note from scala-2.8.x, the endOffset has been added, just keep this method
   * here for reference.
   */
  private def setBoundsEndToken(fromScope: AstScope) {
    assert(fromScope.isScopesSorted == false)

    val children = fromScope.subScopes
    val itr = children.iterator
    var curr = if (itr.hasNext) itr.next else null
    while (curr != null) {
      if (itr.hasNext) {
        val next = itr.next
        val offset = next.boundsOffset(th)
        if (offset != -1) {
          val endToken = getBoundsEndToken(offset - 1)
          curr.boundsEndToken = endToken
        } else {
          println("Scope without start token: " + next)
        }
        curr = next
      } else {
        curr.parent match {
          case Some(x) => curr.boundsEndToken = x.boundsEndToken
          case None =>
        }
        curr = null
      }
    }

    children foreach {setBoundsEndToken(_)}
  }
}
