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

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import javax.swing.text.JTextComponent;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.netbeans.modules.editor.NbEditorUtilities;
import javax.swing.text.Document;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.netbeans.editor.BaseAction;
import org.openide.{DialogDescriptor, DialogDisplayer, NotifyDescriptor}
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.scala.editor.ScalaParserResult;
import org.openide.util.Exceptions;

/**
 *
 * @author schmidtm
 */
class FixImportsAction extends BaseAction(NbBundle.getMessage(classOf[FixImportsAction],
                                                              "fix-scala-imports"), 0) with Runnable {

  private val LOG = Logger.getLogger(classOf[FixImportsAction].getName)

  var doc: Option[Document] = None
  private var helper = new FixImportsHelper

  override def isEnabled: Boolean = {
    // here should go all the logic whether there are in fact missing
    // imports we're able to fix.
    true
  }

  def actionPerformed(evt: ActionEvent, comp: JTextComponent) {
    LOG.log(Level.FINEST, "actionPerformed(final JTextComponent comp)")

    assert(comp != null)
    doc = comp.getDocument match {
      case null => None
      case x => Some(x)
    }

    if (doc.isDefined) {
      RequestProcessor.getDefault.post(this)
    }
  }

  def run {
    val dob = NbEditorUtilities.getDataObject(doc.get)
    if (dob == null) {
      LOG.log(Level.FINEST, "Could not get DataObject for document")
      return
    }

    var missingNames: List[String] = Nil

    val fo = dob.getPrimaryFile
    try {
      val source = Source.create(fo)
      // FIXME can we move this out of task (?)
      ParserManager.parse(_root_.java.util.Collections.singleton(source), new UserTask {
          @throws(classOf[Exception])
          override def run(resultIterator: ResultIterator)  {
            val pResult = resultIterator.getParserResult.asInstanceOf[ScalaParserResult]
            if (pResult != null) {
              val errors = pResult.getDiagnostics
              if (errors == null) {
                LOG.log(Level.FINEST, "Could not get list of errors")
                return
              }

              // loop over the list of errors, remove duplicates and
              // populate list of missing imports.
              val itr = errors.iterator
              while (itr.hasNext) {
                val error = itr.next
                FixImportsHelper.checkMissingImport(error.getDescription) match {
                  case Some(missingName) if !missingNames.contains(missingName) =>
                    missingNames = missingName :: missingNames
                  case _ =>
                }
              }
            }
          }
        })
    } catch {case ex: ParseException => Exceptions.printStackTrace(ex)}

    // go over list of missing imports, fix it - if there is only one
    // candidate or populate choosers input list.

    var multipleCandidates: Map[String, List[ImportCandidate]] = Map()
    for (name <- missingNames) {
      helper.getImportCandidate(fo, name) match {
        case Nil =>
        case x :: Nil => helper.doImport(fo, x.fqnName)
        case importCandidates => multipleCandidates += (name -> importCandidates)
      }
    }

    // * do we have multiple candidate? In this case we need to present a chooser
    if (!multipleCandidates.isEmpty) {
      LOG.log(Level.FINEST, "multipleCandidates.size(): " + multipleCandidates.size)
      presentChooser(multipleCandidates) foreach {helper.doImport(fo, _)}
    } 
  }

  private def presentChooser(multipleCandidates: Map[String, List[ImportCandidate]]): Array[String] = {
    LOG.log(Level.FINEST, "presentChooser()")

    val panel = new ImportChooserInnerPanel
    panel.initPanel(multipleCandidates)

    val dd = new DialogDescriptor(panel, NbBundle.getMessage(classOf[FixImportsAction], "FixImportsDialogTitle")) //NOI18N
    val d = DialogDisplayer.getDefault.createDialog(dd)

    d.setVisible(true)
    d.setVisible(false)
    d.dispose

    dd.getValue match {
      case NotifyDescriptor.OK_OPTION => panel.getSelections
      case _ => Array()
    }
  }
}
