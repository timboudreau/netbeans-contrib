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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.scala.editor.refactoring

import java.util.logging.Level;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.csl.spi.GsfUtilities;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.UserTask;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position.Bias;
import org.netbeans.api.language.util.ast.{AstDfn, AstScope}
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.lexer.TokenUtilities;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.support.ModificationResult;
import org.netbeans.modules.csl.spi.support.ModificationResult.Difference;
import org.netbeans.modules.scala.editor.{ScalaMimeResolver, ScalaParserResult}
import org.netbeans.modules.scala.editor.ast.ScalaItems
import org.netbeans.modules.scala.editor.lexer.{ScalaTokenId, ScalaLexUtil}
import org.netbeans.modules.parsing.spi.Parser
import org.netbeans.modules.refactoring.api._
import org.openide.filesystems.FileObject
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.spi.ParseException;
import org.openide.text.PositionRef;
import org.openide.util.NbBundle;
import scala.collection.mutable.ArrayBuffer
import scala.tools.nsc.ast.Trees

import org.openide.text.CloneableEditorSupport

/**
 * The actual Renaming refactoring work for Python.
 *
 * @author Tor Norbye
 * 
 * @todo Perform index lookups to determine the set of files to be checked!
 * @todo Check that the new name doesn't conflict with an existing name
 * @todo Check unknown files!
 * @todo More prechecks
 * @todo When invoking refactoring on a file object, I also rename the file. I should (a) list the
 *   name it's going to change the file to, and (b) definitely "filenamize" it - e.g. for class FooBar the
 *   filename should be foo_bar.
 * @todo If you rename a Model, I should add a corresponding rename_table entry in the migrations...
 *
 * @todo Complete this. Most of the prechecks are not implemented - and the refactorings themselves need a lot of work.
 */
class RenameRefactoringPlugin(rename: RenameRefactoring) extends ScalaRefactoringPlugin {
  
  private val refactoring = rename
  private var searchHandle: ScalaItems#ScalaItem = _
  private var overriddenByMethods: Collection[_] = null // methods that override the method to be renamed
  private var overridesMethods: Collection[_] = null // methods that are overridden by the method to be renamed
  private var doCheckName: Boolean = true;

  init
  
  /** Creates a new instance of RenameRefactoring */
  private def init = {
    val tph = rename.getRefactoringSource.lookup(classOf[ScalaItems#ScalaItem])
    if (tph != null) {
      searchHandle = tph
    } else {
      val source = Source.create(rename.getRefactoringSource.lookup(classOf[FileObject]))
      try {
        ParserManager.parse(java.util.Collections.singleton(source), new UserTask {
            @throws(classOf[Exception])
            override def run(ri: ResultIterator) {
              if (ri.getSnapshot.getMimeType == ScalaMimeResolver.MIME_TYPE) {
                val pr = ri.getParserResult.asInstanceOf[ScalaParserResult]
                val root = pr.rootScope.get
                val tmpls = new ArrayBuffer[AstDfn]
                RetoucheUtils.getTopTemplates(List(root), tmpls)
                if (!tmpls.isEmpty) {
                  // @todo multiple tmpls
                  searchHandle = tmpls(0).asInstanceOf[ScalaItems#ScalaItem]
                  refactoring.getContext().add(ri)
                }
              }
            }
          })
      } catch {case ex: ParseException => Logger.getLogger(classOf[RenameRefactoringPlugin].getName).log(Level.WARNING, null, ex)}
    }
  }



  def fastCheckParameters: Problem = {
    var fastCheckProblem: Problem = null
    if (searchHandle == null) {
      return null; //no refactoring, not params check
    }

    val kind = searchHandle.kind
    val newName = refactoring.getNewName
    val oldName = searchHandle.symbol.fullNameString
    if (oldName == null) {
      return new Problem(true, "Cannot determine target name. Please file a bug with detailed information on how to reproduce (preferably including the current source file and the cursor position)");
    }

    if (oldName.equals(newName)) {
      val nameNotChanged = true
      //if (kind == ElementKind.CLASS || kind == ElementKind.MODULE) {
      //    if (!((TypeElement) element).getNestingKind().isNested()) {
      //        nameNotChanged = info.getFileObject().getName().equals(element);
      //    }
      //}
      if (nameNotChanged) {
        return ScalaRefactoringPlugin.createProblem(fastCheckProblem, true, getString("ERR_NameNotChanged"))
      }

    }

    // TODO - get a better Js name picker - and check for invalid Js symbol names etc.
    // TODO - call JsUtils.isValidLocalVariableName if we're renaming a local symbol!
    /*if (kind == ElementKind.CLASS && !JsUtils.isValidJsClassName(newName)) {
     String s = getString("ERR_InvalidClassName"); //NOI18N
     String msg = new MessageFormat(s).format(Array(newName), new StringBuffer, new FieldPosition(0)).toString
     fastCheckProblem = createProblem(fastCheckProblem, true, msg);
     return fastCheckProblem;
     } else*/


    // by Caoyuan
    /* if (kind == ElementKind.METHOD && !JsUtils.isValidJsMethodName(newName)) {
      val s = getString("ERR_InvalidMethodName"); //NOI18N
      val msg = new MessageFormat(s).format(Array(newName), new StringBuffer, new FieldPosition(0)).toString
      return ScalaRefactoringPlugin.createProblem(fastCheckProblem, true, msg)
    } else if (!JsUtils.isValidJsIdentifier(newName)) {
      val s = getString("ERR_InvalidIdentifier"); //NOI18N
      val msg = new MessageFormat(s).format(Array(newName), new StringBuffer, new FieldPosition(0)).toString
      return ScalaRefactoringPlugin.createProblem(fastCheckProblem, true, msg)
    }


    val msg = JsUtils.getIdentifierWarning(newName, 0);
    if (msg != null) {
      fastCheckProblem = ScalaRefactoringPlugin.createProblem(fastCheckProblem, false, msg);
    } */
    // ----- by Caoyuan
    
    // TODO
//        System.out.println("TODO - look for variable clashes etc");


//        if (kind.isClass() && !((TypeElement) element).getNestingKind().isNested()) {
//            if (doCheckName) {
//                String oldfqn = RetoucheUtils.getQualifiedName(treePathHandle);
//                String newFqn = oldfqn.substring(0, oldfqn.lastIndexOf(oldName));
//
//                String pkgname = oldfqn;
//                int i = pkgname.indexOf('.');
//                if (i>=0)
//                    pkgname = pkgname.substring(0,i);
//                else
//                    pkgname = "";
//
//                String fqn = "".equals(pkgname) ? newName : pkgname + '.' + newName;
//                FileObject fo = treePathHandle.getFileObject();
//                ClassPath cp = ClassPath.getClassPath(fo, ClassPath.SOURCE);
//                if (RetoucheUtils.typeExist(treePathHandle, newFqn)) {
//                    String msg = new MessageFormat(getString("ERR_ClassClash")).format(
//                            Array(newName, pkgname)
//                    , new StringBuffer, new FieldPosition(0)).toString
//                    fastCheckProblem = createProblem(fastCheckProblem, true, msg);
//                    return fastCheckProblem;
//                }
//            }
//            FileObject primFile = treePathHandle.getFileObject();
//            FileObject folder = primFile.getParent();
//            FileObject[] children = folder.getChildren();
//            for (int x = 0; x < children.length; x++) {
//                if (children[x] != primFile && !children[x].isVirtual() && children[x].getName().equals(newName) && "java".equals(children[x].getExt())) { //NOI18N
//                    String msg = new MessageFormat(getString("ERR_ClassClash")).format(
//                            Array(newName, folder.getPath(), new StringBuffer, new FieldPosition(0)).toString
//                    );
//                    fastCheckProblem = createProblem(fastCheckProblem, true, msg);
//                    break;
//                }
//            } // for
//        } else if (kind == ElementKind.LOCAL_VARIABLE || kind == ElementKind.PARAMETER) {
//            String msg = variableClashes(newName,treePath, info);
//            if (msg != null) {
//                fastCheckProblem = createProblem(fastCheckProblem, true, msg);
//                return fastCheckProblem;
//            }
//        } else {
//            String msg = clashes(element, newName, info);
//            if (msg != null) {
//                fastCheckProblem = createProblem(fastCheckProblem, true, msg);
//                return fastCheckProblem;
//            }
//        }
    return fastCheckProblem;
  }

  def checkParameters: Problem = {

    var checkProblem: Problem = null
    var steps = 0
    if (overriddenByMethods != null) {
      steps += overriddenByMethods.size;
    }
    if (overridesMethods != null) {
      steps += overridesMethods.size
    }

    fireProgressListenerStart(AbstractRefactoring.PARAMETERS_CHECK, 8 + 3*steps);

//        Element element = treePathHandle.resolveElement(info);

    fireProgressListenerStep
    fireProgressListenerStep

    // TODO - check more parameters
    //System.out.println("TODO - need to check parameters for hiding etc.");


//        if (treePathHandle.getKind() == ElementKind.METHOD) {
//            checkProblem = checkMethodForOverriding((ExecutableElement)element, refactoring.getNewName(), checkProblem, info);
//            fireProgressListenerStep();
//            fireProgressListenerStep();
//        } else if (element.getKind().isField()) {
//            fireProgressListenerStep();
//            fireProgressListenerStep();
//            Element hiddenField = hides(element, refactoring.getNewName(), info);
//            fireProgressListenerStep();
//            fireProgressListenerStep();
//            fireProgressListenerStep();
//            if (hiddenField != null) {
//                msg = new MessageFormat(getString("ERR_WillHide")).format(
//                        new Object[] {SourceUtils.getEnclosingTypeElement(hiddenField).toString()}
//                );
//                checkProblem = createProblem(checkProblem, false, msg);
//            }
//        }
    fireProgressListenerStop
    checkProblem
  }

//        private Problem checkMethodForOverriding(ExecutableElement m, String newName, Problem problem, CompilationInfo info) {
//            ElementUtilities ut = info.getElementUtilities();
//            //problem = willBeOverridden(m, newName, argTypes, problem);
//            fireProgressListenerStep();
//            problem = willOverride(m, newName, problem, info);
//            fireProgressListenerStep();
//            return problem;
//        }
//
//    private Set<searchHandle<ExecutableElement>> allMethods;

  override def preCheck: Problem = {
    if (searchHandle == null) {
      return null
    }
    searchHandle.fo match {
      case Some(x) if x.isValid => return null
      case _ => return new Problem(true, NbBundle.getMessage(classOf[RenameRefactoringPlugin], "DSC_ElNotAvail")) // NOI18N
    }
  }

  private def getRelevantFiles: Set[FileObject] = {
    if (searchHandle.kind == ElementKind.VARIABLE || searchHandle.kind == ElementKind.PARAMETER) {
      // For local variables, only look in the current file!
      Set(searchHandle.fo.get)
    }  else {
      RetoucheUtils.getScalaFilesInProject(searchHandle.fo.get, true)
    }
  }

//    private void addMethods(ExecutableElement e, Set set, CompilationInfo info, ClassIndex idx) {
//        set.add(SourceUtils.getFile(e, info.getClasspathInfo()));
//        searchHandle<TypeElement> encl = searchHandle.create(SourceUtils.getEnclosingTypeElement(e));
//        set.addAll(idx.getResources(encl, EnumSet.of(ClassIndex.SearchKind.METHOD_REFERENCES),EnumSet.of(ClassIndex.SearchScope.SOURCE)));
//        allMethods.add(searchHandle.create(e));
//    }

  private var allMethods: Set[ScalaItems#ScalaItem] = _

  def prepare(elements: RefactoringElementsBag): Problem = {
    if (searchHandle == null) {
      return null
    }
    val files = getRelevantFiles
    fireProgressListenerStart(ProgressEvent.START, files.size)
    if (!files.isEmpty) {
      val transform = new TransformTask {

        override protected def process(pr: ScalaParserResult): Seq[ModificationResult] = {
          val rt = new RenameTransformer(refactoring.getNewName, allMethods)
          rt.workingCopy_=(pr)
          rt.scan
          if(rt.diffs.isEmpty) {
            return Nil
          } else {
            val mr = new ModificationResult
            mr.addDifferences(pr.getSnapshot.getSource.getFileObject, java.util.Arrays.asList(rt.diffs.toArray: _*))
            return List(mr)
          }
        }
      }

      val results = processFiles(files, transform);
      elements.registerTransaction(new ScalaTransaction(results))
      for (result <- results) {
        val fItr = result.getModifiedFileObjects.iterator
        while (fItr.hasNext) {
          val fo = fItr.next
          val dItr = result.getDifferences(fo).iterator
          while (dItr.hasNext) {
            val diff = dItr.next
            val old = diff.getOldText
            if (old!=null) {
              //TODO: workaround
              //generator issue?
              elements.add(refactoring, DiffElement(diff, fo, result))
            }
          }
        }
      }
    }
    fireProgressListenerStop
    return null;
  }


  private def getString(key: String): String = {
    return NbBundle.getMessage(classOf[RenameRefactoringPlugin], key);
  }

  /**
   *
   * @author Jan Becicka
   */
  class RenameTransformer(newName: String, allMethods: Set[ScalaItems#ScalaItem]) extends SearchVisitor {

    private val oldName = searchHandle.symbol.nameString
    private var ces: CloneableEditorSupport = _
    var diffs: ArrayBuffer[Difference] = _

    override def workingCopy_=(workingCopy: ScalaParserResult) {
      // Cached per working copy
      this.ces = null
      this.diffs = null
      super.workingCopy = workingCopy
    }

    override def scan {
      diffs = new ArrayBuffer[Difference]
      val searchCtx = searchHandle
      var error: Error = null
      val root = workingCopy.rootScope
      val workingCopyFo = workingCopy.getSnapshot.getSource.getFileObject
      if (root != None) {
        val doc = GsfUtilities.getDocument(workingCopyFo, true)
        try {
          if (doc != null) {
            doc.readLock
          }

          /* ===== @Caoyuan temp commented
          Element element = AstElement.getElement(workingCopy, root);
          Node node = searchCtx.getNode();

          val fileCtx = new ScalaItems#ScalaItem(root, node, element, workingCopyFileObject, workingCopy);

          Node scopeNode = null;
          if (workingCopyFo == searchCtx.fo) {
            if (node.getType() == org.mozilla.nb.javascript.Token.NAME ||
                node.getType() == org.mozilla.nb.javascript.Token.BINDNAME ||
                node.getType() == org.mozilla.nb.javascript.Token.PARAMETER) {


              // TODO - map this node to our new tree.
              // In the mean time, just search in the old seach tree.
              Node searchRoot = node;
              while (searchRoot.getParentNode() != null) {
                searchRoot = searchRoot.getParentNode();
              }

              VariableVisitor v = new VariableVisitor();
              new ParseTreeWalker(v).walk(searchRoot);
              scopeNode = v.getDefiningScope(node);
            }
          }

          if (scopeNode != null) {
            findLocal(searchCtx, fileCtx, scopeNode, oldName);
          } else {
            // Full AST search
            AstPath path = new AstPath();
            path.descend(root);
            find(path, searchCtx, fileCtx, root, oldName);
            path.ascend();
          } */
        } finally {
          if (doc != null) {
            doc.readUnlock();
          }
        }
      } else {
        //System.out.println("Skipping file " + workingCopy.getFileObject());
        // See if the document contains references to this symbol and if so, put a warning in
        val workingCopyText = workingCopy.getSnapshot.getText.toString
        if (workingCopyText.indexOf(oldName) != -1) {
          // TODO - icon??
          if (ces == null) {
            ces = RetoucheUtils.findCloneableEditorSupport(workingCopy);
          }
          var start = 0
          var end = 0
          var desc = NbBundle.getMessage(classOf[RenameRefactoringPlugin], "ParseErrorFile", oldName);
          val errors = workingCopy.getDiagnostics
          if (errors.size > 0) {
            var break = false
            val itr = errors.iterator
            while (itr.hasNext && !break) {
              val e = itr.next
              if (e.getSeverity == Severity.ERROR) {
                error = e
                break = true
              }
            }
            if (error == null) {
              error = errors.get(0)
            }

            var errorMsg = error.getDisplayName
            if (errorMsg.length > 80) {
              errorMsg = errorMsg.substring(0, 77) + "..."; // NOI18N
            }

            desc = desc + "; " + errorMsg
            start = error.getStartPosition
            start = ScalaLexUtil.getLexerOffset(workingCopy, start)
            if (start == -1) {
              start = 0
            }
            end = start
          }
          val startPos = ces.createPositionRef(start, Bias.Forward);
          val endPos = ces.createPositionRef(end, Bias.Forward);
          val diff = new Difference(Difference.Kind.CHANGE, startPos, endPos, "", "", desc); // NOI18N
          diffs += diff
        }
      }

      if (error == null && refactoring.isSearchInComments) {
        val doc = RetoucheUtils.getDocument(workingCopy);
        if (doc != null) {
          //force open
          val th = TokenHierarchy.get(doc)
          val ts = th.tokenSequence.asInstanceOf[TokenSequence[TokenId]]

          ts.move(0)

          searchTokenSequence(ts)
        }
      }

      ces = null;
    }

    private def searchTokenSequence(ts: TokenSequence[TokenId]) {
      if (ts.moveNext) {
        do {
          val token = ts.token
          val id = token.id

          val primaryCategory = id.primaryCategory
          if ("comment".equals(primaryCategory) || "block-comment".equals(primaryCategory)) { // NOI18N
            // search this comment
            val tokenText = token.text
            if (tokenText != null && oldName != null) {
              val index = TokenUtilities.indexOf(tokenText, oldName)
              if (index != -1) {
                val text = tokenText.toString
                // TODO make sure it's its own word. Technically I could
                // look at identifier chars like "_" here but since they are
                // used for other purposes in comments, consider letters
                // and numbers as enough
                if ((index == 0 || !Character.isLetterOrDigit(text.charAt(index-1))) &&
                    (index+oldName.length >= text.length ||
                     !Character.isLetterOrDigit(text.charAt(index+oldName.length)))) {
                  val start = ts.offset() + index
                  val end = start + oldName.length
                  if (ces == null) {
                    ces = RetoucheUtils.findCloneableEditorSupport(workingCopy);
                  }
                  val startPos = ces.createPositionRef(start, Bias.Forward);
                  val endPos = ces.createPositionRef(end, Bias.Forward);
                  val desc = getString("ChangeComment");
                  val diff = new Difference(Difference.Kind.CHANGE, startPos, endPos, oldName, newName, desc);
                  diffs += diff
                }
              }
            }
          } else {
            val embedded = ts.embedded.asInstanceOf[TokenSequence[TokenId]]
            if (embedded != null) {
              searchTokenSequence(embedded)
            }
          }
        } while (ts.moveNext)
      }
    }

/* @Caoyuan temp commented

    private def rename(node: Trees#Tree, oldCode: String, anewCode: String, desc: String) {
      var newCode = anewCode
      val range = AstUtilities.getNameRange(node);
      assert(range != OffsetRange.NONE)
      val pos = range.getStart();

      if (desc == null) {
        // TODO - insert "method call", "method definition", "class definition", "symbol", "attribute" etc. and from and too?
        switch (node.getType()) {
          case Token.OBJLITNAME: {
              if (!AstUtilities.isLabelledFunction(node)) {
                desc = NbBundle.getMessage(RenameRefactoringPlugin.class, "UpdateRef", oldCode);
                break;
              } else {
                // Fall through
              }
            }
          case Token.FUNCNAME:
          case Token.FUNCTION:
            desc = getString("UpdateMethodDef");
            break;
          case Token.NEW:
          case Token.CALL:
            desc = getString("UpdateCall");
            break;
          case Token.NAME:
            if (node.getParentNode() != null &&
                (node.getParentNode().getType() == Token.CALL ||
                 node.getParentNode().getType() == Token.NEW)) {
              // Ignore
              desc = getString("UpdateCall");
              break;
            }
            // Fallthrough
          case Token.BINDNAME:
            if (oldCode != null && oldCode.length() > 0 && Character.isUpperCase(oldCode.charAt(0))) {
              desc = getString("UpdateClass");
              break;
            }
            desc = getString("UpdateLocalvar");
            break;
          case Token.PARAMETER:
            desc = getString("UpdateParameter");
            break;
//                case Token.GLOBAL:
//                    desc = getString("UpdateGlobal");
//                case Token.PROPERTY:
//                    desc = getString("UpdateProperty");
            default:
            desc = NbBundle.getMessage(RenameRefactoringPlugin.class, "UpdateRef", oldCode);
            break;
        }
      }

      if (ces == null) {
        ces = RetoucheUtils.findCloneableEditorSupport(workingCopy);
      }

      // Convert from AST to lexer offsets if necessary
      pos = LexUtilities.getLexerOffset(workingCopy, pos);
      if (pos == -1) {
        // Translation failed
        return;
      }

      var start = pos
      var end = pos + oldCode.length
      // TODO if a SymbolNode, +=1 since the symbolnode includes the ":"
      var doc: BaseDocument = null;
      try {
        doc = ces.openDocument.asInstanceOf[BaseDocument]
        doc.readLock();

        if (start > doc.getLength) {
          start = end = doc.getLength
        }

        if (end > doc.getLength) {
          end = doc.getLength
        }

        // Look in the document and search around a bit to detect the exact method reference
        // (and adjust position accordingly). Thus, if I have off by one errors in the AST (which
        // occasionally happens) the user's source won't get munged
        if (!oldCode.equals(doc.getText(start, end-start))) {
          // Look back and forwards by 1 at first
          val lineStart = Utilities.getRowFirstNonWhite(doc, start);
          val lineEnd = Utilities.getRowLastNonWhite(doc, start)+1; // +1: after last char
          if (lineStart == -1 || lineEnd == -1) { // We're really on the wrong line!
            val f = workingCopy.getSnapshot().getSource().getFileObject();
            println("Empty line entry in " + FileUtil.getFileDisplayName(f) +
                    "; no match for " + oldCode + " in line " + start + " referenced by node " +
                    node + " of type " + node.getClass().getName());
            return;
          }

          if (lineStart < 0 || lineEnd-lineStart < 0) {
            return; // Can't process this one
          }

          val line = doc.getText(lineStart, lineEnd-lineStart);
          if (line.indexOf(oldCode) == -1) {
            val f = workingCopy.getSnapshot().getSource().getFileObject();
            println("Skipping entry in " + FileUtil.getFileDisplayName(f) +
                    "; no match for " + oldCode + " in line " + line + " referenced by node " +
                    node + " of type " + node.getClass().getName());
          } else {
            val lineOffset = start-lineStart;
            var newOffset = -1;
            // Search up and down by one
            for (distance <- 1 until line.length) {
              // Ahead first
              if (lineOffset+distance+oldCode.length() <= line.length() &&
                  oldCode.equals(line.substring(lineOffset+distance, lineOffset+distance+oldCode.length()))) {
                newOffset = lineOffset+distance;
                break;
              }
              if (lineOffset-distance >= 0 && lineOffset-distance+oldCode.length() <= line.length() &&
                  oldCode.equals(line.substring(lineOffset-distance, lineOffset-distance+oldCode.length()))) {
                newOffset = lineOffset-distance;
                break;
              }
            }

            if (newOffset != -1) {
              start = newOffset+lineStart;
              end = start+oldCode.length();
            }
          }
        }
      } catch {
        case ie: IOException => Exceptions.printStackTrace(ie)
        case ble: BadLocationException => Exceptions.printStackTrace(ble)
      } finally {
        if (doc != null) {
          doc.readUnlock
        }
      }

      if (newCode == null) {
        // Usually it's the new name so allow client code to refer to it as just null
        newCode = refactoring.getNewName // XXX isn't this == our field "newName"?
      }

      val startPos = ces.createPositionRef(start, Bias.Forward);
      val endPos = ces.createPositionRef(end, Bias.Forward);
      val diff = new Difference(Difference.Kind.CHANGE, startPos, endPos, oldCode, newCode, desc);
      diffs += diff;
    }

    /** Search for local variables in local scope */
    private def findLocal(searchCtx: ScalaItems#ScalaItem, fileCtx: ScalaItems#ScalaItem, node: Trees#Tree, name: String) {
      switch (node.getType()) {
        case Token.PARAMETER:
          if (node.getString().equals(name)) {
            rename(node, name, null, getString("RenameParam"));
          }
          break;
        case Token.NAME:
          if ((node.getParentNode() != null && node.getParentNode().getType() == Token.CALL ||
               node.getParentNode() != null && node.getParentNode().getType() == Token.NEW) &&
              node.getParentNode().getFirstChild() == node) {
            // Ignore calls
            break;
          }
          // Fallthrough
        case Token.BINDNAME:
          if (node.getString().equals(name)) {
            rename(node, name, null, Character.isUpperCase(name.charAt(0)) ? getString("UpdateClass") : getString("UpdateLocalvar"));
          }
      }

      if (node.hasChildren()) {
        var child = node.getFirstChild();

        while (child != null) {
          findLocal(searchCtx, fileCtx, child, name);
          child = child.getNext()
        }
      }
    }

    /**
     * @todo P1: This is matching method names on classes that have nothing to do with the class we're searching for
     *   - I've gotta filter fields, methods etc. that are not in the current class
     *  (but I also have to search for methods that are OVERRIDING the class... so I've gotta work a little harder!)
     * @todo Arity matching on the methods to preclude methods that aren't overriding or aliasing!
     */
    private def find(path: AstPath, searchCtx: ScalaItems#ScalaItem, fileCtx: ScalaItems#ScalaItem, node: Trees#Tree, name: String) {
      switch (node.getType()) {
        case org.mozilla.nb.javascript.Token.OBJLITNAME: {
            if (node.getString().equals(name) && AstUtilities.isLabelledFunction(node)) {
              // TODO - implement skip semantics here, as is done for functions!
              // AstUtilities.getLabelledFunction(node);
              rename(node, name, null, getString("UpdateMethodDef"));
            }

            // No children to consider
            return;
          }

        case org.mozilla.nb.javascript.Token.FUNCNAME: {
            if (node.getString().equals(name)) {
              boolean skip = false;
//
//                        // Check that we're in a class or module we're interested in
//                        String fqn = AstUtilities.getFqnName(path);
//                        if (fqn == null || fqn.length() == 0) {
//                            fqn = JsIndex.OBJECT;
//                        }
//
//                        if (!fqn.equals(searchCtx.getDefClass())) {
//                            // XXX THE ABOVE IS NOT RIGHT - I shouldn't
//                            // use equals on the class names, I should use the
//                            // index and see if one derives fromor includes the other
//                            skip = true;
//                        }
//
//                        // Check arity
//                        if (!skip && AstUtilities.isCall(searchCtx.getNode())) {
//                            // The reference is a call and this is a definition; see if
//                            // this looks like a match
//                            // TODO - enforce that this method is also in the desired
//                            // target class!!!
//                            if (!AstUtilities.isCallFor(searchCtx.getNode(), searchCtx.getArity(), node)) {
//                                skip = true;
//                            }
//                        } else {
//                            // The search handle is a method def, as is this, with the same name.
//                            // Now I need to go and see if this is an override (e.g. compatible
//                            // arglist...)
//                            // XXX TODO
//                        }

              if (!skip) {
                // Found a method match
                // TODO - check arity - see OccurrencesFinder
                //node = AstUtilities.getDefNameNode((MethodDefNode)node);
                rename(node, name, null, getString("UpdateMethodDef"));
                return;
              }
            }
            break;
          }
        case org.mozilla.nb.javascript.Token.NEW:
        case org.mozilla.nb.javascript.Token.CALL: {
            String s = AstUtilities.getCallName(node, false);
            if (s.equals(name)) {
              // TODO - if it's a call without a lhs (e.g. Call.LOCAL),
              // make sure that we're referring to the same method call
              // Found a method call match
              // TODO - make a node on the same line
              // TODO - check arity - see OccurrencesFinder
              rename(node, name, null, null);
              return;
            }
            break;
          }
        case org.mozilla.nb.javascript.Token.NAME:
          if (node.getParentNode().getType() == org.mozilla.nb.javascript.Token.CALL ||
              node.getParentNode().getType() == org.mozilla.nb.javascript.Token.NEW) {
            // Skip - call name is already handled as part of parent
            break;
          }
          // Fallthrough
        case org.mozilla.nb.javascript.Token.STRING: {
            int parentType = node.getParentNode().getType();
            if (!(parentType == org.mozilla.nb.javascript.Token.GETPROP ||
                  parentType == org.mozilla.nb.javascript.Token.SETPROP)) {
              break;
            }
            // Fallthrough
          }
        case org.mozilla.nb.javascript.Token.BINDNAME: {
            // Global vars
            if (node.getString().equals(name)) {
              rename(node, name, null, null);
              return;
            }
            break;
          }
      }

      if (node.hasChildren()) {
        Node child = node.getFirstChild();

        for (; child != null; child = child.getNext()) {
          find(path, searchCtx, fileCtx, child, name);
        }
      }
    } */
  } 
}
