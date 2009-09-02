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
package org.netbeans.modules.scala.editor.refactoring

import org.netbeans.editor.BaseDocument
import org.netbeans.modules.csl.api.ElementKind
import org.netbeans.modules.csl.spi.GsfUtilities
import org.openide.filesystems.FileObject

import org.netbeans.modules.scala.editor.{ScalaGlobal, ScalaParserResult}
import org.netbeans.modules.scala.editor.lexer.ScalaLexUtil
import scala.tools.nsc.ast.Trees

/**
 * This is a holder class for a Scala element as well as its
 * context - used in various places in the refactoring classes.
 * These need to be able to be mapped from one AST to another,
 * and correspond (roughly) to the TreePath, ScalaElementCtx,
 * Element and ElementHandle classes (plus some friends like CompilationInfo
 * and FileObject) passed around.
 *
 * @author Caoyuan Deng
 */
object ScalaElementCtx {
  
  def apply(root: Trees#Tree, node: Trees#Tree, fo: FileObject, info: ScalaParserResult) =
    new ScalaElementCtx(root, node, fo, info)

  /** Create a new element holder representing the node closest to the given caret offset in the given compilation job */
  def apply(info: ScalaParserResult, caret: Int) = {
    val astOffset = ScalaLexUtil.getAstOffset(info, caret)
    val srcFile = info.srcFile
    val global = info.global
    val pos = global.rangePos(srcFile, astOffset, astOffset, astOffset)
    val root = info.rootScope match {
      case Some(x) => x.unit match {
          case Some(y) => y.body.asInstanceOf[Trees#Tree]
          case None => global.EmptyTree
        }
      case None => global.EmptyTree
    }
    val node = global.locateTree(pos)
    val fo = info.getSnapshot.getSource.getFileObject
      
    val ctx = new ScalaElementCtx(root, node, fo, info)
    ctx.caret = caret
    ctx
  }

  /** Create a new element holder representing the given node in the same context as the given existing context */
  def apply(ctx: ScalaElementCtx, node: Trees#Tree) =
    new ScalaElementCtx(ctx.root, node, ctx.fo, ctx.info)
}

class ScalaElementCtx(val root: Trees#Tree, var node: Trees#Tree, val fo: FileObject, val info: ScalaParserResult) {
  var caret: Int = _
  
  val global = info.global

  val symbol = node.symbol.asInstanceOf[global.Symbol]

  //private Arity arity;
  private var defClass: String = _

  lazy val kind: ElementKind = global.ScalaUtil.getKind(symbol)

  lazy val name = symbol.fullNameString

  lazy val simpleName = symbol.nameString

  lazy val modifiers = global.ScalaUtil.getModifiers(symbol)

//    public Arity getArity() {
//        if (arity == null) {
//            if (node instanceof MethodDefNode) {
//                arity = Arity.getDefArity(node);
//            } else if (AstUtilities.isCall(node)) {
//                arity = Arity.getCallArity(node);
//            } else if (node instanceof ArgumentNode) {
//                AstPath path = getPath();
//
//                if (path.leafParent() instanceof MethodDefNode) {
//                    arity = Arity.getDefArity(path.leafParent());
//                }
//            }
//        }
//
//        return arity;
//    }

// XXX: parsingapi
//    public BaseDocument getDocument() {
//        if (document == null) {
//            document = RetoucheUtils.getDocument(info, info.getFileObject());
//        }
//
//        return document;
//    }

  private def getViewControllerRequire(view: FileObject): String = {
    null
  }

//    /** If the node is a method call, return the class of the method we're looking
//     * for (if any)
//     */
//    public String getDefClass() {
//        if (defClass == null) {
//            if (RubyUtils.isRhtmlFile(fileObject)) {
//                // TODO - look in the Helper class as well to see if the method is coming from there!
//                // In fact that's probably a more likely home!
//                defClass = "ActionView::Base";
//            } else if (AstUtilities.isCall(node)) {
//                // Try to figure out the call type from the call
//                BaseDocument doc = getDocument();
//                TokenHierarchy<Document> th = TokenHierarchy.get((Document)doc);
//                int astOffset = AstUtilities.getCallRange(node).getStart();
//                Call call = Call.getCallType(doc, th, astOffset);
//                int lexOffset = LexUtilities.getLexerOffset(info, astOffset);
//
//                String type = call.getType();
//                String lhs = call.getLhs();
//
//                if ((type == null) && (lhs != null) && (node != null) && call.isSimpleIdentifier()) {
//                    Node method = AstUtilities.findLocalScope(node, getPath());
//
//                    if (method != null) {
//                        // TODO - if the lhs is "foo.bar." I need to split this
//                        // up and do it a bit more cleverly
//                        TypeAnalyzer analyzer =
//                            new TypeAnalyzer(null, method, node, astOffset, lexOffset, doc, null);
//                        type = analyzer.getType(lhs);
//                    }
//                } else if (call == Call.LOCAL) {
//                    // Look in the index to see which method it's coming from...
//                    RubyIndex index = RubyIndex.get(info.getIndex());
//                    String fqn = AstUtilities.getFqnName(getPath());
//
//                    if ((fqn == null) || (fqn.length() == 0)) {
//                        fqn = RubyIndex.OBJECT;
//                    }
//
//                    IndexedMethod method = index.getOverridingMethod(fqn, getName());
//
//                    if (method != null) {
//                        defClass = method.getIn();
//                    } // else: It's some unqualified method call we don't recognize - perhaps an attribute?
//                      // For now just assume it's a method on this class
//                }
//
//                if (defClass == null) {
//                    // Just an inherited method call?
//                    if ((type == null) && (lhs == null)) {
//                        defClass = AstUtilities.getFqnName(getPath());
//                    } else if (type != null) {
//                        defClass = type;
//                    } else {
//                        defClass = RubyIndex.UNKNOWN_CLASS;
//                    }
//                }
//            } else {
//                if (getPath() != null) {
//                    IScopingNode clz = AstUtilities.findClassOrModule(getPath());
//
//                    if (clz != null) {
//                        defClass = AstUtilities.getClassOrModuleName(clz);
//                    }
//                }
//
//                if ((defClass == null) && (element != null)) {
//                    defClass = element.getIn();
//                }
//
//                if (defClass == null) {
//                    defClass = RubyIndex.OBJECT; // NOI18N
//                }
//            }
//        }
//
//        return defClass;
//    }

  override def toString = {
    "node= " + node + ";kind=" + kind 
  }

  /**
   * Get the prefix of the name which should be "stripped" before letting the user edit the variable,
   * and put back in when done. For globals for example, it's "$"
   */
  def getStripPrefix: String = {
//        if (node instanceof GlobalVarNode || node instanceof GlobalAsgnNode) {
//            return "$";
//        } else if (node instanceof InstVarNode || node instanceof InstAsgnNode) {
//            return "@";
//        } else if (node instanceof ClassVarNode || node instanceof ClassVarDeclNode ||
//                node instanceof ClassVarAsgnNode) {
//            return "@@";
//        //} else if (node instanceof SymbolNode) {
//        // Symbols don't include these in their names
//        //    return ":";
//        }
//
//        // TODO: Blocks - "&" ?
//        // Restargs - "*" ?
    null;
  }
}
