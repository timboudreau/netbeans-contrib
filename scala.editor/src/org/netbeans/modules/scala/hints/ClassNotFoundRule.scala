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

import org.netbeans.modules.csl.api.Error
import org.netbeans.modules.csl.api.Hint
import org.netbeans.modules.csl.api.HintFix
import org.netbeans.modules.csl.api.HintSeverity
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.csl.api.RuleContext
import org.netbeans.modules.scala.editor.util.NbBundler
import java.util.prefs.Preferences;
import javax.swing.JComponent;


class ClassNotFoundRule extends ScalaErrorRule with NbBundler {

    println("created ClassNotFDoundRule")

    override def appliesTo(context : RuleContext) : Boolean = true

    override def getDisplayName : String = locMessage("LBL_ClassNotFound")

    override def showInTasklist : Boolean = false

    override def getDefaultSeverity : HintSeverity = HintSeverity.ERROR


//    override def getKinds : java.util.Set[_] = new java.util.HashSet()
    override def getCodes : java.util.Set[String] = {
        val codes = new java.util.HashSet[String]()
        codes.add(ScalaErrorRule.SYNTAX_ERROR)
        codes
    }

        /** Gets unique ID of the rule
         */
//    override def getId : String = "ID"

        /** Gets longer description of the rule
         */
//    override def getDescription() : String = "desc"

        /** Finds out whether the rule is currently enabled.
         * @return true if enabled false otherwise.
         */
//    override def getDefaultEnabled() : Boolean = true

        /** Gets the UI description for this rule. It is fine to return null
         * to get the default behavior. Notice that the Preferences node is a copy
         * of the node returned from {link:getPreferences()}. This is in oder to permit
         * canceling changes done in the options dialog.<BR>
         * Default implementation return null, which results in no customizer.
         * It is fine to return null (as default implementation does)
         * @param node Preferences node the customizer should work on.
         * @return Component which will be shown in the options dialog.
         */
//     override def getCustomizer(node: Preferences) : JComponent = null
    
    override def createHints(context : ScalaRuleContext, error : Error) : List[Hint] =  {
        val desc = error.getDescription
        println("desc=" + desc)
        List()
    }
}
