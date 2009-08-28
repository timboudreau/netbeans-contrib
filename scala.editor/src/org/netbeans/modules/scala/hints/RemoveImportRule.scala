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

package org.netbeans.modules.scala.hints

import scala.collection.JavaConversions._

import scala.collection.mutable
import scala.collection.immutable

import org.netbeans.modules.csl.api.Error
import org.netbeans.modules.csl.api.Hint
import org.netbeans.modules.csl.api.HintFix
import org.netbeans.modules.csl.api.HintSeverity
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.csl.api.RuleContext
import org.netbeans.modules.scala.editor.util.NbBundler
import java.{util=>ju}
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import java.util.regex.{Pattern, Matcher}
import org.netbeans.api.java.source.ElementHandle
import javax.swing.JPanel
import org.netbeans.api.java.source.ClassIndex
import javax.lang.model.element.ElementKind
import org.openide.filesystems.FileObject
import javax.lang.model.element.TypeElement
import org.netbeans.modules.scala.editor.lexer.ScalaLexUtil
import org.netbeans.api.language.util.ast.{AstRef, AstDfn, AstItem, AstRootScope}
import org.netbeans.api.lexer.{Language, Token, TokenHierarchy, TokenId, TokenSequence}

import org.netbeans.modules.csl.api.EditList
import org.netbeans.modules.scala.editor.lexer.ScalaTokenId
import org.netbeans.editor.BaseDocument

import org.netbeans.modules.scala.editor.actions.FixImportsHelper
import org.netbeans.modules.scala.editor.ScalaParserResult
import org.netbeans.api.language.util.ast._
import scala.tools.nsc.symtab._



class RemoveImportRule() extends ScalaAstRule with NbBundler {
    val DEFAULT_PRIORITY = 293

    /** Gets unique ID of the rule
     */
    def getId() : String = "RemoveImportRule"

    /** Gets longer description of the rule
     */
    def getDescription() : String = "Desc"

    /** Finds out whether the rule is currently enabled.
     * @return true if enabled false otherwise.
     */
    def getDefaultEnabled() : Boolean = true

    /** Gets the UI description for this rule. It is fine to return null
     * to get the default behavior. Notice that the Preferences node is a copy
     * of the node returned from {link:getPreferences()}. This is in oder to permit
     * canceling changes done in the options dialog.<BR>
     * Default implementation return null, which results in no customizer.
     * It is fine to return null (as default implementation does)
     * @param node Preferences node the customizer should work on.
     * @return Component which will be shown in the options dialog.
     */
    def getCustomizer(node : Preferences) : JComponent = new JPanel()

    /**
     * Return true iff this hint applies to the given file
     */
    def appliesTo(context : RuleContext) : Boolean = true

    /** Get's UI usable name of the rule
     */
    def getDisplayName : String = "Remove import"

    /**
     * Whether this task should be shown in the tasklist
     */
    def showInTasklist : Boolean = false

    /** Gets current severiry of the hint.
     * @return Hints severity in current profile.
     */
    def getDefaultSeverity : HintSeverity = HintSeverity.WARNING


    def getKinds() : java.util.Set[_] = java.util.Collections.singleton(ScalaAstRule.ROOT)

    def createHints(context : ScalaRuleContext, scope : AstScope) : List[Hint] = {
        println("creating rmeove import hint")
        val defs = findDefinitions(scope)
        defs.foreach(a => println(a))

        val imports = FixImportsHelper.allGlobalImports(context.doc)

        //TODO very simplistic, need radical refinement!!
        val candidates = for (i <- imports 
                              if !(i._3.contains("_") || i._3.contains("{")) &&
                                   !defs.exists(a => a == i._3))
                                  yield i

//        candidates.foreach( a => println("candidate" + a._3))
        val toRet = mutable.ListBuffer[Hint]()
        for ((start, end, text) <- candidates) {
              val rangeOpt = context.calcOffsetRange(start, end)
              toRet +=  new Hint(this, "Remove Unused Import " + text, context.getFileObject, rangeOpt.get,
                             new ju.ArrayList() /**new RemoveImportFix(context, start, end, text)) */, DEFAULT_PRIORITY)
        }

        toRet.toList
    }

    private def findDefinitions(scope : AstScope) : List[String] = {
        val buf = mutable.HashSet[String]()
//        val defs = scope.dfns.filter(a => a.getKind == ElementKind.CLASS || a.getKind == ElementKind.INTERFACE || a.getKind == ElementKind.TYPE_PARAMETER)
//        println("defs size=" + defs.size)
        for (d <- scope.refs) {
            val sym = d.symbol.asInstanceOf[scala.tools.nsc.symtab.Symbols#Symbol]
            if (sym.isClass || sym.isTrait) {
                println("symbol type=" + sym.tpe)
                println("    type? " + sym.isType)
                println("    module? " + sym.isModule)
                println("    class? " + sym.isClass)
                println("    trait? " + sym.isTrait)
                buf.add(sym.tpe.toString)
            }
        }
        for (sc <- scope.subScopes) {
           buf.addAll(findDefinitions(sc))
        }
        buf.toList
    }

}
