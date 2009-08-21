/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.scala.editor.actions

import java.util.MissingResourceException;
import javax.swing.text.BadLocationException;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.openide.filesystems.FileObject;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.NameKind;
import java.util.Set;
import java.util.EnumSet;
import java.util.regex.Pattern
import javax.lang.model.element.TypeElement;
import javax.swing.Icon;
import org.netbeans.editor.BaseDocument;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.Utilities;
import org.netbeans.api.java.source.ui.ElementIcons
import org.netbeans.modules.csl.api.EditList;
import org.netbeans.modules.scala.editor.{ScalaSourceUtil}
import org.netbeans.modules.scala.editor.lexer.{ScalaLexUtil, ScalaTokenId}

/**
 *
 * @author schmidtm
 */
object FixImportsHelper{

  private val LOG = Logger.getLogger(classOf[FixImportsHelper].getName)

  def getImportanceLevel(fqn: String): Int = {
    var weight = 50
    if (fqn.startsWith("scala") || fqn.startsWith("scala.util")) {
      weight -= 20
    } else if (fqn.startsWith("java.lang") || fqn.startsWith("java.util")) {
      weight -= 10
    } else if (fqn.startsWith("org.omg") || fqn.startsWith("org.apache")) {
      weight += 10
    } else if (fqn.startsWith("com.sun") || fqn.startsWith("com.ibm") || fqn.startsWith("com.apple")) {
      weight += 20
    } else if (fqn.startsWith("sun") || fqn.startsWith("sunw") || fqn.startsWith("netscape")) {
      weight += 30
    }
    weight
  }
  
  val NotFoundValue = Pattern.compile("not found: value (.*)") // NOI18N
  val NotFoundType = Pattern.compile("not found: type (.*)")  // NOI18N

  def checkMissingImport(desc: String): Option[String] = {
    NotFoundValue.matcher(desc) match {
      case x if x.matches => Some(x.group(1))
      case _ => NotFoundType.matcher(desc) match {
          case x if x.matches => Some(x.group(1))
          case _ => None
        }
    }
  }
}

class FixImportsHelper {
  import FixImportsHelper._
    
  def getImportCandidate(fo: FileObject, missingClass: String): List[ImportCandidate] = {
    LOG.log(Level.FINEST, "Looking for class: " + missingClass)

    var result: List[ImportCandidate] = Nil

    //        ScalaIndex index = ScalaIndex.get(pResult.getSnapshot().getSource().getFileObject());
    //        Set<GsfElement> cslElements = index.getDeclaredTypes(missingClass, QuerySupport.Kind.PREFIX, pResult);
    //        for (GsfElement cslElement : cslElements) {
    //            javax.lang.model.element.Element element = cslElement.getElement();
    //            javax.lang.model.element.ElementKind ekind = element.getKind();
    //            if (ekind == javax.lang.model.element.ElementKind.CLASS ||
    //                    ekind == javax.lang.model.element.ElementKind.INTERFACE) {
    //                String fqnName = element.asType();
    //                //Icon icon = ElementIcons.getElementIcon(ek, null);
    //                int level = getImportanceLevel(fqnName);
    //
    //                ImportCandidate candidate = new ImportCandidate(missingClass, fqnName, icon, level);
    //                result.add(candidate);
    //
    //            }
    //        }

    val cpInfo = ScalaSourceUtil.getClasspathInfo(fo).getOrElse(return result)
    val typeNames = cpInfo.getClassIndex.getDeclaredTypes(missingClass, NameKind.SIMPLE_NAME,
                                                          EnumSet.allOf(classOf[ClassIndex.SearchScope]))
    val itr = typeNames.iterator
    while (itr.hasNext) {
      val typeName = itr.next
      typeName.getKind match {
        case ek@(javax.lang.model.element.ElementKind.CLASS | javax.lang.model.element.ElementKind.INTERFACE) =>
          val fqnName = typeName.getQualifiedName
          LOG.log(Level.FINEST, "Found     : " + fqnName)

          val icon = ElementIcons.getElementIcon(ek, null)
          val level = getImportanceLevel(fqnName)

          val candidate = new ImportCandidate(missingClass, fqnName, icon, level)
          result = candidate :: result
        case _ =>
      }

    }

    result
  }

  def getImportPosition(doc: BaseDocument): Int = {
    val ts = ScalaLexUtil.getTokenSequence(doc, 1).getOrElse(return -1)

    var importEnd = -1
    var packageOffset = -1

    while (ts.moveNext) {
      val t = ts.token
      val offset = ts.offset
      t.id match {
        case ScalaTokenId.Import =>
          LOG.log(Level.FINEST, "ScalaTokenId.Import found");
          importEnd = offset
        case ScalaTokenId.Package =>
          LOG.log(Level.FINEST, "ScalaTokenId.Package found");
          packageOffset = offset
        case _ =>
      }
    }

    var useOffset = 0
    // sanity check: package *before* import
    if (importEnd != -1 && packageOffset > importEnd) {
      LOG.log(Level.FINEST, "packageOffset > importEnd")
      return -1;
    }

    // nothing set:
    if (importEnd == -1 && packageOffset == -1) {
      // place imports in the first line
      LOG.log(Level.FINEST, "importEnd == -1 && packageOffset == -1")
      return 0
    } else if (importEnd == -1 && packageOffset != -1) { // only package set:
      // place imports behind package statement
      LOG.log(Level.FINEST, "importEnd == -1 && packageOffset != -1")
      useOffset = packageOffset
    } else if (importEnd != -1 && packageOffset == -1) { // only imports set:
      // place imports after the last import statement
      LOG.log(Level.FINEST, "importEnd != -1 && packageOffset == -1")
      useOffset = importEnd
    } // both package & import set:
    else if (importEnd != -1 && packageOffset != -1) {
      // place imports right after the last import statement
      LOG.log(Level.FINEST, "importEnd != -1 && packageOffset != -1")
      useOffset = importEnd

    }

    var lineOffset = 0
    try {
      lineOffset = Utilities.getLineOffset(doc, useOffset)
    } catch {case ex: BadLocationException =>
        LOG.log(Level.FINEST, "BadLocationException for offset : {0}", useOffset)
        LOG.log(Level.FINEST, "BadLocationException : {0}", ex.getMessage)
        return -1
    }

    Utilities.getRowStartFromLineOffset(doc, lineOffset + 1)

  }

  @throws(classOf[MissingResourceException])
  def doImport(fo: FileObject, fqnName: String) {
    val baseDoc = ScalaLexUtil.getDocument(fo, true).getOrElse(return)
    var firstFreePosition = getImportPosition(baseDoc)

    if (firstFreePosition != -1) {
      if (baseDoc == null) {
        return
      }

      val edits = new EditList(baseDoc)
      LOG.log(Level.FINEST, "Importing here: " + firstFreePosition)

      edits.replace(firstFreePosition, 0, "import " + fqnName + "\n", false, 0)
      edits.apply
    }
  }
}
