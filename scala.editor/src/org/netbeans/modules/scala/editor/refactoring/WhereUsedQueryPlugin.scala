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
package org.netbeans.modules.scala.editor.refactoring;

import javax.swing.Icon;
import javax.swing.text.Document;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.lexer.TokenUtilities;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.core.UiUtils;
import org.netbeans.modules.csl.spi.GsfUtilities;
import org.netbeans.modules.csl.spi.support.ModificationResult;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.ProgressEvent;
import org.netbeans.modules.refactoring.api.WhereUsedQuery;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;

import org.netbeans.modules.scala.editor.ScalaParserResult
import org.netbeans.modules.scala.editor.ast.{ScalaItems, ScalaRootScope}
import org.netbeans.modules.scala.editor.lexer.ScalaLexUtil
import org.openide.filesystems.FileObject
import org.openide.util.NbBundle;
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet
import scala.tools.nsc.ast.Trees
import scala.tools.nsc.symtab.Flags

/**
 * Actual implementation of Find Usages query search for Ruby
 *
 * @todo Perform index lookups to determine the set of files to be checked!
 * @todo Scan comments!
 * @todo Do more prechecks of the elements we're trying to find usages for
 *
 * @author  Jan Becicka
 * @author Tor Norbye
 */
class WhereUsedQueryPlugin(refactoring: WhereUsedQuery) extends ScalaRefactoringPlugin {
  private val searchHandle = refactoring.getRefactoringSource.lookup(classOf[ScalaItems#ScalaItem])
  private val targetName =  searchHandle.symbol.fullNameString

  def preCheck: Problem = {
    searchHandle.fo match {
      case Some(x) if x.isValid => null
      case _ => return new Problem(true, NbBundle.getMessage(classOf[WhereUsedQueryPlugin], "DSC_ElNotAvail")); // NOI18N
    }
  }

  private def getRelevantFiles(item: ScalaItems#ScalaItem): Set[FileObject] = {
    val set = new HashSet[FileObject]

    item.fo match {
      case Some(fo) =>
        set.add(fo)

        // @todo
        var isLocal = false
        if (item.symbol.hasFlag(Flags.PARAM)) {
          isLocal = true
        }

        if (!isLocal) {
          set ++= (RetoucheUtils.getScalaFilesInProject(fo))
        }
      case _ =>
    }

    set.toSet


//        final ClasspathInfo cpInfo = getClasspathInfo(refactoring);
//        //final ClassIndex idx = cpInfo.getClassIndex();
//        final Set<FileObject> set = new HashSet<FileObject>();
//
//        final FileObject file = tph.getFileObject();
//        Source source;
//        if (file!=null) {
//           set.add(file);
//            source = RetoucheUtils.createSource(cpInfo, tph.getFileObject());
//        } else {
//            source = Source.create(cpInfo);
//        }
//        //XXX: This is slow!
//        UserTask task = new UserTask() {
//            public void run(ResultIterator resultIterator) {
//                //System.out.println("TODO - compute a full set of files to be checked... for now just lamely using the project files");
//                //set.add(info.getFileObject());
//                // (This currently doesn't need to run in a compilation controller since I'm not using parse results at all...)
//
//
////                if (isFindSubclasses() || isFindDirectSubclassesOnly()) {
////                    // No need to do any parsing, we'll be using the index to find these files!
////                    set.add(info.getFileObject());
////
////                    String name = tph.getName();
////
////                    // Find overrides of the class
////                    RubyIndex index = RubyIndex.get(info.getIndex());
////                    String fqn = AstUtilities.getFqnName(tph.getPath());
////                    Set<IndexedClass> classes = index.getSubClasses(null, fqn, name, isFindDirectSubclassesOnly());
////
////                    if (classes.size() > 0) {
////                        subclasses = classes;
//////                        for (IndexedClass clz : classes) {
//////                            FileObject fo = clz.getFileObject();
//////                            if (fo != null) {
//////                                set.add(fo);
//////                            }
//////                        }
////                        // For now just parse this particular file!
////                        set.add(info.getFileObject());
////                        return;
////                    }
////                }
//
//                boolean isLocal = false;
//                if (tph.getKind() == ElementKind.PARAMETER) {
//                    isLocal = true;
//                } else if (tph.getKind() == ElementKind.VARIABLE) {
//                    Node n = tph.getNode();
//                    while (n != null) {
//                        if (n.getType() == org.mozilla.nb.javascript.Token.FUNCTION) {
//                            isLocal = true;
//                            break;
//                        }
//                        n = n.getParentNode();
//                    }
//                }
//                if (isLocal) {
//                    // For local variables, only look in the current file!
//                    set.add(info.getFileObject());
//                }  else {
//                    set.addAll(RetoucheUtils.getJsFilesInProject(info.getFileObject()));
//                }
//
////                final Element el = tph.resolveElement(info);
////                if (el.getKind().isField()) {
////                    //get field references from index
////                    set.addAll(idx.getResources(ElementHandle.create((TypeElement)el.getEnclosingElement()), EnumSet.of(ClassIndex.SearchKind.FIELD_REFERENCES), EnumSet.of(ClassIndex.SearchScope.SOURCE)));
////                } else if (el.getKind().isClass() || el.getKind().isInterface()) {
////                    if (isFindSubclasses()||isFindDirectSubclassesOnly()) {
////                        if (isFindDirectSubclassesOnly()) {
////                            //get direct implementors from index
////                            EnumSet searchKind = EnumSet.of(ClassIndex.SearchKind.IMPLEMENTORS);
////                            set.addAll(idx.getResources(ElementHandle.create((TypeElement)el), searchKind,EnumSet.of(ClassIndex.SearchScope.SOURCE)));
////                        } else {
////                            //itererate implementors recursively
////                            set.addAll(getImplementorsRecursive(idx, cpInfo, (TypeElement)el));
////                        }
////                    } else {
////                        //get type references from index
////                        set.addAll(idx.getResources(ElementHandle.create((TypeElement) el), EnumSet.of(ClassIndex.SearchKind.TYPE_REFERENCES, ClassIndex.SearchKind.IMPLEMENTORS),EnumSet.of(ClassIndex.SearchScope.SOURCE)));
////                    }
////                } else if (el.getKind() == ElementKind.METHOD && isFindOverridingMethods()) {
////                    //Find overriding methods
////                    TypeElement type = (TypeElement) el.getEnclosingElement();
////                    set.addAll(getImplementorsRecursive(idx, cpInfo, type));
////                }
////                if (el.getKind() == ElementKind.METHOD && isFindUsages()) {
////                    //get method references for method and for all it's overriders
////                    Set<ElementHandle<TypeElement>> s = idx.getElements(ElementHandle.create((TypeElement) el.getEnclosingElement()), EnumSet.of(ClassIndex.SearchKind.IMPLEMENTORS),EnumSet.of(ClassIndex.SearchScope.SOURCE));
////                    for (ElementHandle<TypeElement> eh:s) {
////                        TypeElement te = eh.resolve(info);
////                        if (te==null) {
////                            continue;
////                        }
////                        for (Element e:te.getEnclosedElements()) {
////                            if (e instanceof ExecutableElement) {
////                                if (info.getElements().overrides((ExecutableElement)e, (ExecutableElement)el, te)) {
////                                    set.addAll(idx.getResources(ElementHandle.create(te), EnumSet.of(ClassIndex.SearchKind.METHOD_REFERENCES),EnumSet.of(ClassIndex.SearchScope.SOURCE)));
////                                }
////                            }
////                        }
////                    }
////                    set.addAll(idx.getResources(ElementHandle.create((TypeElement) el.getEnclosingElement()), EnumSet.of(ClassIndex.SearchKind.METHOD_REFERENCES),EnumSet.of(ClassIndex.SearchScope.SOURCE))); //?????
////                } else if (el.getKind() == ElementKind.CONSTRUCTOR) {
////                        set.addAll(idx.getResources(ElementHandle.create((TypeElement) el.getEnclosingElement()), EnumSet.of(ClassIndex.SearchKind.TYPE_REFERENCES, ClassIndex.SearchKind.IMPLEMENTORS),EnumSet.of(ClassIndex.SearchScope.SOURCE)));
////                }
////
//            }
//        };
//        try {
//            source.runUserActionTask(task, true);
//        } catch (IOException ioe) {
//            throw (RuntimeException) new RuntimeException().initCause(ioe);
//        }
//        return set;
  }

//    private Set<FileObject> getImplementorsRecursive(ClassIndex idx, ClasspathInfo cpInfo, TypeElement el) {
//        Set<FileObject> set = new HashSet<FileObject>();
//        LinkedList<ElementHandle<TypeElement>> elements = new LinkedList(idx.getElements(ElementHandle.create(el),
//                EnumSet.of(ClassIndex.SearchKind.IMPLEMENTORS),
//                EnumSet.of(ClassIndex.SearchScope.SOURCE)));
//        HashSet<ElementHandle> result = new HashSet();
//        while(!elements.isEmpty()) {
//            ElementHandle<TypeElement> next = elements.removeFirst();
//            result.add(next);
//            elements.addAll(idx.getElements(next,
//                    EnumSet.of(ClassIndex.SearchKind.IMPLEMENTORS),
//                    EnumSet.of(ClassIndex.SearchScope.SOURCE)));
//        }
//        for (ElementHandle<TypeElement> e : result) {
//            FileObject fo = SourceUtils.getFile(e, cpInfo);
//            assert fo != null: "issue 90196, Cannot find file for " + e + ". cpInfo=" + cpInfo ;
//            set.add(fo);
//        }
//        return set;
//    }

  //@Override
  def prepare(elements: RefactoringElementsBag): Problem = {
    val a = getRelevantFiles(searchHandle)
    fireProgressListenerStart(ProgressEvent.START, a.size);
    processFiles(a, new FindTask(elements))
    fireProgressListenerStop
    null
  }

  def fastCheckParameters: Problem = {
    if (targetName == null) {
      return new Problem(true, "Cannot determine target name. Please file a bug with detailed information on how to reproduce (preferably including the current source file and the cursor position)");
    }
    if (searchHandle.kind == ElementKind.METHOD) {
      return checkParametersForMethod(isFindOverridingMethods, isFindUsages)
    }
    null
  }

  def checkParameters: Problem = {
    null
  }

  private def checkParametersForMethod(overriders: Boolean, usages: Boolean): Problem = {
    if (!(usages || overriders)) {
      new Problem(true, NbBundle.getMessage(classOf[WhereUsedQueryPlugin], "MSG_NothingToFind"));
    } else null
  }

  private def isFindSubclasses: Boolean = {
    refactoring.getBooleanValue(WhereUsedQueryConstants.FIND_SUBCLASSES)
  }
  private def isFindUsages: Boolean = {
    refactoring.getBooleanValue(WhereUsedQuery.FIND_REFERENCES)
  }
  private def isFindDirectSubclassesOnly: Boolean = {
    refactoring.getBooleanValue(WhereUsedQueryConstants.FIND_DIRECT_SUBCLASSES)
  }
  private def isFindOverridingMethods: Boolean = {
    refactoring.getBooleanValue(WhereUsedQueryConstants.FIND_OVERRIDING_METHODS)
  }
  private def isSearchInComments: Boolean = {
    refactoring.getBooleanValue(WhereUsedQuery.SEARCH_IN_COMMENTS)
  }
  private def isSearchFromBaseClass: Boolean = {
    false
  }

  private class FindTask(elements: RefactoringElementsBag) extends TransformTask() {

    protected def process(pr: ScalaParserResult): Seq[ModificationResult] = {
      if (isCancelled) {
        return null
      }

      var error: Error = null

      val searchCtx = searchHandle

      val th = pr.getSnapshot.getTokenHierarchy
      val root = pr.rootScope.get

      if (root == ScalaRootScope.EMPTY) {
        val sourceText = pr.getSnapshot.getText.toString
        //System.out.println("Skipping file " + workingCopy.getFileObject());
        // See if the document contains references to this symbol and if so, put a warning in
        if (sourceText != null && sourceText.indexOf(targetName) != -1) {
          var start = 0;
          var end = 0;
          var desc = "Parse error in file which contains " + targetName + " reference - skipping it";
          val errors = pr.getDiagnostics
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
              errorMsg = errorMsg.substring(0, 77) + "..." // NOI18N
            }

            desc = desc + "; " + errorMsg
            start = error.getStartPosition
            start = ScalaLexUtil.getLexerOffset(pr, start)
            if (start == -1) {
              start = 0
            }
            end = start
          }

          val modifiers = java.util.Collections.emptySet[Modifier]
          val icon = UiUtils.getElementIcon(ElementKind.ERROR, modifiers)
          val range = new OffsetRange(start, end)
          val element = WhereUsedElement(pr, targetName, desc, range, icon)
          elements.add(refactoring, element)
        }
      }

      // @todo Caoyuan
      /* if (error == null && isSearchInComments) {
       val doc = RetoucheUtils.getDocument(pResult)
       if (doc != null) {
       //force open
       val th = TokenHierarchy.get(doc)
       val ts = th.tokenSequence.asInstanceOf[TokenSequence[TokenId]]

       ts.move(0)

       searchTokenSequence(pResult, ts)
       }
       } */

      if (root == null) {
        // TODO - warn that this file isn't compileable and is skipped?
        return Nil
      }

      // If it's a local search, use a simpler search routine
      // TODO: ArgumentNode - look to see if we're in a parameter list, and if so its a localvar
      // (if not, it's a method)

      /*            if (isFindSubclasses() || isFindDirectSubclassesOnly()) {
       // I'm only looking for the specific classes
       assert subclasses != null;
       // Look in these files for the given classes
       //findSubClass(root);
       for (IndexedClass clz : subclasses) {
       ScalaItems#ScalaItem matchCtx = new ScalaItems#ScalaItem(clz, compiler);
       elements.add(refactoring, WhereUsedElement.create(matchCtx));
       }
       } else*/

      if (isFindUsages) {
        val matched =
          for ((token, items) <- root.idTokenToItems(th);
               item <- items;
               sym = item.asInstanceOf[ScalaItems#ScalaItem].symbol
               if sym == searchHandle.symbol && token.text.toString == sym.nameString
          ) {
            elements.add(refactoring, WhereUsedElement(pr, item.asInstanceOf[ScalaItems#ScalaItem]))
          }
      } else if (isFindOverridingMethods) {
        // TODO

      } else if (isSearchFromBaseClass) {
        // TODO
      }

      Nil
    }

    private def searchTokenSequence(info: ScalaParserResult, ts: TokenSequence[TokenId]) {
      if (ts.moveNext) {
        do {
          val token = ts.token
          val id = token.id

          val primaryCategory = id.primaryCategory
          if ("comment".equals(primaryCategory) || "block-comment".equals(primaryCategory)) { // NOI18N
            // search this comment
            assert(targetName != null)
            val tokenText = token.text
            if (tokenText != null && targetName != null) {
              val index = TokenUtilities.indexOf(tokenText, targetName)
              if (index != -1) {
                val text = tokenText.toString
                // TODO make sure it's its own word. Technically I could
                // look at identifier chars like "_" here but since they are
                // used for other purposes in comments, consider letters
                // and numbers as enough
                if ((index == 0 || !Character.isLetterOrDigit(text.charAt(index-1))) &&
                    (index+targetName.length >= text.length ||
                     !Character.isLetterOrDigit(text.charAt(index+targetName.length)))) {
                  val start = ts.offset + index
                  val end = start + targetName.length

                  // TODO - get a comment-reference icon? For now, just use the icon type
                  // of the search target
                  val modifiers = if (searchHandle.symbol != null) {
                    searchHandle.getModifiers
                  } else java.util.Collections.emptySet[Modifier]
                  val icon = UiUtils.getElementIcon(searchHandle.kind, modifiers)
                  val range = new OffsetRange(start, end)
                  val element = WhereUsedElement(info, targetName, range, icon)
                  elements.add(refactoring, element)
                }
              }
            }
          } else {
            val embedded = ts.embedded.asInstanceOf[TokenSequence[TokenId]]
            if (embedded != null) {
              searchTokenSequence(info, embedded)
            }
          }
        } while (ts.moveNext)
      }
    }

  }
}
