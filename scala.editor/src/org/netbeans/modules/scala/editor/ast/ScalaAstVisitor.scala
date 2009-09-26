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
import org.netbeans.modules.csl.api.{ElementKind}
import org.openide.filesystems.{FileObject, FileUtil}

import org.netbeans.api.language.util.ast.{AstItem, AstScope}
import org.netbeans.modules.scala.editor.ScalaGlobal
import org.netbeans.modules.scala.editor.lexer.{ScalaLexUtil, ScalaTokenId}

import scala.tools.nsc.symtab.{Flags}
import scala.tools.nsc.symtab.Flags._
import scala.tools.nsc.util.{SourceFile, OffsetPosition}
import scala.collection.mutable.{Stack, HashSet, HashMap}

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

  private val debug = false
  private val scopes = new Stack[AstScope]
  private var rootScope: ScalaRootScope = _

  private var fo: Option[FileObject] = _
  private var th: TokenHierarchy[_] = _
  private var srcFile: SourceFile = _
  private var docLength: Int = _

  def apply(unit: CompilationUnit, th: TokenHierarchy[_]): ScalaRootScope = {
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

    //println(global.selectTypeErrors)

    scopes.clear
    rootScope = ScalaRootScope(Some(unit), getBoundsTokens(0, docLength))
    scopes push rootScope

    unit match {
      case u: RichCompilationUnit => importingTraverser(u)
      case _ =>
    }

    treeTraverser(unit.body)
    
    rootScope
  }

  private object importingTraverser {
    private val visited = new HashSet[Context]
    
    def visitContextTree(ct: ContextTree): Unit = {
      val c = ct.context
      if (visited.add(c)) {
        for (importInfo <- c.imports;
             me@Import(qual, selectors) = importInfo.tree if me.pos.isDefined
        ) {
          val qualSym = qual.symbol
          if (qualSym != null) {
            val idToken = getIdToken(qual)
            val ref = ScalaRef(qualSym, idToken, if (qualSym.hasFlag(Flags.PACKAGE)) ElementKind.PACKAGE else ElementKind.OTHER, fo)
            if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
          }

          //println("import: qual=" + qual.tpe + ", selectors=" + selectors.mkString("{", ",", "}" ))
          selectors foreach {
            case (null, null) =>

            case (nme.WILDCARD, _) =>
              // * idToken == "_", sym == qualSym
              val idToken = getIdToken(me, nme.WILDCARD.decode)
              val ref = ScalaRef(qualSym, idToken, ElementKind.OTHER, fo)
              if (scopes.top.addRef(ref)) {
                info("\tAdded: ", ref)
                rootScope putImportingItem ref
              }

            case (x, y) =>
              val xsym = importedSymbol(me, x)
              if (xsym != null) {
                val idToken = getIdToken(me, x.decode)
                val ref = ScalaRef(xsym, idToken, ElementKind.OTHER, fo)
                if (scopes.top.addRef(ref)) {
                  info("\tAdded: ", ref)
                  rootScope putImportingItem ref
                }
              }

              if (y != null) {
                val ysym = importedSymbol(me, y)
                if (ysym != null) {
                  val idToken = getIdToken(me, y.decode)
                  val ref = ScalaRef(ysym, idToken, ElementKind.OTHER, fo)
                  if (scopes.top.addRef(ref)) {
                    info("\tAdded: ", ref)
                    rootScope putImportingItem ref
                  }
                }
              }
          }      
        }
      }
      
      ct.children foreach visitContextTree
    }

    /**
     * The symbol with name <code>name</code> imported from import clause <code>tree</code>.
     * We'll find class/trait instead of object first.
     * @bug in scala compiler? why name is always TermName? which means it's object instead of class/trait
     */
    def importedSymbol(tree: Import, name: Name): Symbol = {
      var result: List[Symbol] = Nil
      var renamed = false
      val qual = tree.symbol.tpe match {
        case analyzer.ImportType(expr) => expr
        case _ => tree.expr
      }

      if (qual == null || qual.tpe == null) return null

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

      // * prefer type over object
      result find ScalaUtil.isProperType getOrElse result.headOption.getOrElse(null)
    }

    def apply(unit: RichCompilationUnit) {
      visited.clear
      unit.contexts foreach visitContextTree
    }
  }

  private object treeTraverser {
    private val visited = new HashSet[Tree]
    private var qualiferMaybeType: Option[Type] = None
    private val treeToKnownType = new HashMap[Tree, Type]

    protected var currentOwner: Symbol = definitions.RootClass
    
    def traverse(tree: Tree): Unit = {
      if (!visited.add(tree)) return // has visited

      tree match {
        case EmptyTree =>
          ;
        case PackageDef(pid, stats) =>
          val scope = ScalaScope(getBoundsTokens(tree))
          scopes.top.addScope(scope)

          val sym = tree.symbol
          val dfn = ScalaDfn(sym, getIdToken(tree), ElementKind.PACKAGE, scope, fo)
          if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

          scopes push scope
          atOwner(sym.moduleClass) {
            traverse(pid)
            traverseTrees(stats)
          }
          scopes pop

        case ClassDef(mods, name, tparams, impl) =>
          val scope = ScalaScope(getBoundsTokens(tree))
          scopes.top.addScope(scope)

          (if (mods.isTrait) "trait " else "class ")

          val sym = tree.symbol
          val dfn = ScalaDfn(sym, getIdToken(tree), ElementKind.CLASS, scope, fo)
          if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

          scopes push scope
          atOwner(sym) {
            traverseAnnots(sym)
            traverseTrees(mods.annotations)
            traverseTrees(tparams)
            traverse(impl)
          }
          scopes pop

        case ModuleDef(mods, name, impl) =>
          val scope = ScalaScope(getBoundsTokens(tree))
          scopes.top.addScope(scope)

          val sym = tree.symbol
          val dfn = ScalaDfn(sym, getIdToken(tree), ElementKind.MODULE, scope, fo)
          if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

          scopes push scope
          atOwner(sym.moduleClass) {
            traverseAnnots(sym)
            traverseTrees(mods.annotations)
            traverse(impl)
          }
          scopes pop

        case ValDef(mods, name, tpt, rhs) =>
          val scope = ScalaScope(getBoundsTokens(tree))
          scopes.top.addScope(scope)

          val sym = tree.symbol
          // * special case for: val (a, b, c) = (1, 2, 3)
          if (!isTupleClass(tpt.symbol)) {
            val dfn = ScalaDfn(sym, getIdToken(tree, name.decode.trim), ElementKind.OTHER, scope, fo)
            if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)
          }

          scopes push scope
          atOwner(sym) {
            traverseAnnots(sym)
            traverseTrees(mods.annotations)
            traverse(tpt)
            traverse(rhs)
          }
          scopes pop

        case DefDef(mods, name, tparams, vparamss, tpt, rhs) =>
          val scope = ScalaScope(getBoundsTokens(tree))
          scopes.top.addScope(scope)

          val kind = if (tree.symbol.isConstructor) ElementKind.CONSTRUCTOR else ElementKind.METHOD

          val sym = tree.symbol
          val dfn = ScalaDfn(sym, getIdToken(tree), kind, scope, fo)
          if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

          scopes push scope
          atOwner(sym) {
            traverseAnnots(sym)
            traverseTrees(mods.annotations)
            traverseTrees(tparams)
            traverseTreess(vparamss)
            traverse(tpt)
            traverse(rhs)
          }
          scopes pop

        case TypeDef(mods, name, tparams, rhs) =>
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
          atOwner(sym) {
            traverseAnnots(sym)
            traverseTrees(mods.annotations)
            traverseTrees(tparams)
            if (sym != null && sym != NoSymbol)  {
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
                case _ => traverse(rhs)
              }
            } else traverse(rhs)
          }
          scopes pop

        case LabelDef(name, params, rhs) =>
          traverseTrees(params); traverse(rhs)
        case Import(expr, selectors) =>
          // Import tree has been added into context and replaced by EmptyTree in typer phase
          traverse(expr)
        case Annotated(annot, arg) =>
          traverse(annot); traverse(arg)
        case DocDef(comment, definition) =>
          traverse(definition)
        case Template(parents, self, body) =>
          traverseTrees(parents)
          if (!self.isEmpty) traverse(self)
          traverseStats(body, tree.symbol)
        case Block(stats, expr) =>
          traverseTrees(stats); traverse(expr)
        case CaseDef(pat, guard, body) =>
          traverse(pat); traverse(guard); traverse(body)
        case Sequence(trees) =>
          traverseTrees(trees)
        case Alternative(trees) =>
          traverseTrees(trees)
        case Star(elem) =>
          traverse(elem)
        case Bind(name, body) =>
          val scope = ScalaScope(getBoundsTokens(tree))
          scopes.top.addScope(scope)

          /**
           * case c => println(c), will define a bind val "c"
           */
          val dfn = ScalaDfn(tree.symbol, getIdToken(tree), ElementKind.VARIABLE, scope, fo)
          if (scopes.top.addDfn(dfn)) info("\tAdded: ", dfn)

          traverse(body)

        case UnApply(fun, args) =>
          traverse(fun); traverseTrees(args)
        case ArrayValue(elemtpt, trees) =>
          traverse(elemtpt); traverseTrees(trees)
        case Function(vparams, body) =>
          atOwner(tree.symbol) {
            traverseTrees(vparams); traverse(body)
          }
        case Assign(lhs, rhs) =>
          traverse(lhs); traverse(rhs)
        case AssignOrNamedArg(lhs, rhs) =>
          traverse(lhs); traverse(rhs)
        case If(cond, thenp, elsep) =>
          traverse(cond); traverse(thenp); traverse(elsep)
        case Match(selector, cases) =>
          traverse(selector); traverseTrees(cases)
        case Return(expr) =>
          traverse(expr)
        case Try(block, catches, finalizer) =>
          traverse(block); traverseTrees(catches); traverse(finalizer)
        case Throw(expr) =>
          traverse(expr)
        case New(tpt) =>
          traverse(tpt)
        case Typed(expr, tpt) =>
          traverse(expr); traverse(tpt)
        case TypeApply(fun, args) =>
          traverse(fun); traverseTrees(args)
        case Apply(fun, args) =>
          // * this tree's `fun` part is extractly an `Ident` tree, so add ref at Ident(name) instead here
          traverse(fun); traverseTrees(args)
        case ApplyDynamic(qual, args) =>
          traverse(qual); traverseTrees(args)
        case Super(qual, mix) =>
          val sym = tree.symbol
          if (sym != null) {
            val idToken = getIdToken(tree, "super")
            val ref = ScalaRef(sym, idToken, ElementKind.OTHER, fo)

            if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
          }

        case This(qual) =>
          val sym = tree.symbol
          if (sym != null) {
            val idToken = getIdToken(tree, "this")
            val ref = ScalaRef(sym, idToken, ElementKind.OTHER, fo)

            if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
          }

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

          traverse(qualifier)

          // * reset qualiferMaybeType
          qualiferMaybeType = None

        case Ident(name) =>
          val sym = tree.symbol
          if (sym != null) {
            val idToken = getIdToken(tree, name.decode.trim)
            val sym1 = if (sym == NoSymbol) {
              treeToKnownType.get(tree) match {
                case Some(x) => x.typeSymbol
                case None => sym
              }
            } else sym

            val ref = ScalaRef(sym1, idToken, ElementKind.OTHER, fo)
            /**
             * @Note: this symbol may has wrong tpe, for example, an error tree,
             * to get the proper resultType, we'll check if the qualierMaybeType isDefined
             */
            if (qualiferMaybeType.isDefined) {
              ref.resultType = qualiferMaybeType.get
            }

            // * set ref.resultType before addRef to scope, otherwise, it may not be added if there is same symbol had been added
            if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
          }

        case Literal(value) =>
          value.value match {
            case tpe: Type => addRefForTypeDirectly(tree, tpe)
            case _ =>
          }

        case tt@TypeTree() =>
          tree.symbol match {
            case null =>
              // * in case of: <type ?>
              //println("Null symbol found, tree is:" + tree)
            case NoSymbol =>
              // * type tree in case def, for example: case Some(_),
              // * since the symbol is NoSymbol, we should visit its original type
              val original = tt.original
              if (original != null && original != tree && !isTupleClass(original.symbol)) {
                traverse(original)
              }
            case sym =>
              // * We'll drop tuple type, since all elements in tuple have their own type trees:
              // * for example: val (a, b), where (a, b) as a whole has a type tree, but we only
              // * need binding trees of a and b
              if (!isTupleClass(sym)) {
                tree.tpe match {
                  // special case for `classOf[.....]` etc
                  case TypeRef(pre, sym, argTpes) if sym.fullNameString == "java.lang.Class" =>
                    argTpes foreach {addRefForTypeDirectly(tree, _)}
                  case _ =>
                    val ref = ScalaRef(sym, getIdToken(tree), ElementKind.CLASS, fo)
                    if (scopes.top.addRef(ref)) info("\tAdded: ", ref)
                }
              }

              val orig = tt.original
              if (orig != null && orig != tree) {
                (orig, tree.tpe) match {
                  case (att: AppliedTypeTree, tref: TypeRef) =>
                    // * special case: type and symbols of args may hide in parent's tpe (sometimes also in orig.tpe, but not always)
                    // * example code: Array[String], Option[Array[String]]
                    treeToKnownType += (orig -> tref)
                    traverse(orig)
                  case _ => traverse(orig)
                }
              }
          }

        case SingletonTypeTree(ref) =>
          traverse(ref)
        case SelectFromTypeTree(qualifier, selector) =>
          traverse(qualifier)
        case CompoundTypeTree(templ) =>
          traverse(templ)
        case AppliedTypeTree(tpt, args) =>
          // * special case: type and symbols of args may hide in parent's tpe (sometimes also in orig.tpe, but not always)
          // * example code: Array[String], Option[Array[String]]
          treeToKnownType.get(tree) match {
            // * visit tpt and args with known types
            case Some(TypeRef(pre, sym, argTpes)) =>
              treeToKnownType += (tpt -> sym.tpe)
              traverse(tpt)

              val argsItr = args.iterator
              val tpesItr = argTpes.iterator
              while (argsItr.hasNext && tpesItr.hasNext) {
                val argTree = argsItr.next
                val argTpe = tpesItr.next
                val argSym = argTpe.typeSymbol
                treeToKnownType += (argTree -> argTpe)
                traverse(argTree)
              }
            case _ =>
              traverse(tpt)
              traverseTrees(args)
          }

        case TypeBoundsTree(lo, hi) =>
          traverse(lo); traverse(hi)
        case ExistentialTypeTree(tpt, whereClauses) =>
          traverse(tpt); traverseTrees(whereClauses)
        case SelectFromArray(qualifier, selector, erasure) =>
          traverse(qualifier)
        case Parens(ts) =>
          traverseTrees(ts)
        case tree : StubTree =>
      }
    }

    def traverseTrees(trees: List[Tree]) {
      trees foreach traverse
    }

    def traverseTreess(treess: List[List[Tree]]) {
      treess foreach traverseTrees
    }

    def traverseStats(stats: List[Tree], exprOwner: Symbol) {
      stats foreach (stat =>
        if (exprOwner != currentOwner && stat.isTerm) atOwner(exprOwner)(traverse(stat))
        else traverse(stat))
    }

    def traverseAnnots(sym: Symbol) {
      for (AnnotationInfo(atp, args, assocs) <- sym.annotations) {
        args foreach traverse
      }
    }

    def apply[T <: Tree](tree: T): T = {
      visited.clear
      treeToKnownType.clear
      qualiferMaybeType = None
      currentOwner = definitions.RootClass

      traverse(tree)

      if (debug) rootScope.debugPrintTokens(th)
      
      tree
    }

    def atOwner(owner: Symbol)(traverse: => Unit) {
      val prevOwner = currentOwner
      currentOwner = owner
      traverse
      currentOwner = prevOwner
    }

    private def isTupleClass(symbol: Symbol): Boolean = {
      if (symbol ne null) {
        symbol.ownerChain.map{_.rawname.decode} match {
          case List(a, "scala", "<root>") if a.startsWith("Tuple") => true
          case _ => false
        }
      } else false
    }

    private def addRefForTypeDirectly(onTree: Tree, tpe: Type): Unit = {
      val sym = tpe.typeSymbol

      val idToken = onTree.pos match {
        // tree.pos in case of `classOf[...]` may be set as an OffsetPosition instead of RangePosition,
        // I have to add forward looking char length. @todo get range between "[..., [..],.]"
        case _: OffsetPosition => getIdToken(onTree, sym.name.decode, 20, sym)
        case _ => getIdToken(onTree, sym.name.decode, -1, sym)
      }
      val ref = ScalaRef(sym, idToken, ElementKind.CLASS, fo)
      if (scopes.top.addRef(ref)) info("\tAdded: ", ref)

      // if tpe is TypeRef, we need to add args type
      tpe match {
        case TypeRef(_, _, argTpes) => argTpes foreach {addRefForTypeDirectly(onTree, _)}
        case _ =>
      }
    }
    
  }

  // ---- Helper methods
  
  protected def getOffset(tree: Tree): Int = {
    tree.pos.startOrPoint
  }

  /**
   * @Note: nameNode may contains preceding void productions, and may also contains
   * following void productions, but nameString has stripped the void productions,
   * so we should adjust nameRange according to name and its length.
   */
  protected def getIdToken(tree: Tree, knownName: String = "", forward: Int = -1, asym: Symbol = null): Option[Token[TokenId]] = {
    val sym = if (asym != null) asym else tree.symbol
    if (sym == null) return None

    if (sym.hasFlag(Flags.SYNTHETIC)) {
      // @todo
    }

    /** Do not use symbol.nameString or idString) here, for example, a constructor Dog()'s nameString maybe "this" */
    val name = if (knownName.length > 0) knownName else (if (sym != NoSymbol) sym.rawname.decode.trim else "")
    if (name == "") return None


    val pos = tree.pos
    val offset = if (pos.isDefined) pos.startOrPoint else -1
    if (offset == -1) return None

    var endOffset = if (pos.isDefined) pos.endOrPoint else -1
    if (forward != -1) {
      endOffset = Math.max(endOffset, offset + forward)
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
      case _ if name.endsWith("_=") => text == name || text + "_=" == name
      case _ if name == "Sequence"  => text == name || text == "Seq" // Seq may have symbol name "Sequence"
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

    println("(" + pos.line + ":" + pos.column + ")" + ", idToken: " + idTokenStr + ", symbol: " + symbolStr)
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
