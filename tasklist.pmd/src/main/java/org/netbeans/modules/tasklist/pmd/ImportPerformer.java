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

package org.netbeans.modules.tasklist.pmd;

import net.sourceforge.pmd.RuleViolation;
import pmd.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.openide.explorer.view.*;
import org.openide.nodes.*;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.text.DataEditorSupport;
import org.openide.util.NbBundle;

import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.ConfPanel;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;

/**
 * Perform import statement confirmation & removal
 * <p>
 * @author Tor Norbye
 */


public class ImportPerformer implements SuggestionPerformer {
    private Line line;
    private RuleViolation violation;
    private boolean comment;

    ImportPerformer(Line line, RuleViolation violation,
                    boolean comment) {
        this.line = line;
        this.violation = violation;
    }

    public void perform(Suggestion s) {
        // Remove the particular line
        if (comment) {
            TLUtils.commentLine(line, "import "); // NOI18N
        } else {
            TLUtils.deleteLine(line, "import "); // NOI18N
        }
    }
    public boolean hasConfirmation() {
        return true;
    }
    public Object getConfirmation(Suggestion s) {
        DataObject dao = DataEditorSupport.findDataObject(line);
        int linenumber = line.getLineNumber();
        String filename = dao.getPrimaryFile().getNameExt();
        String ruleDesc = violation.getRule().getDescription();
        String ruleExample = violation.getRule().getExample();
        StringBuffer sb = new StringBuffer(200);
        String beforeContents = null;
        String afterContents = null;
        String afterDesc = null;
        String beforeDesc = null;
        if (comment) {
            // TODO - something special if DontImportJavaLang
            beforeDesc = NbBundle.getMessage(ImportPerformer.class,
                                "ImportConfirmationBefore"); // NOI18N
            afterDesc = 
                NbBundle.getMessage(ImportPerformer.class,
                                "ImportConfirmationAfter"); // NOI18N

            Line l = line;
            sb.append("<html>"); // NOI18N
            TLUtils.appendSurroundingLine(sb, l, -1);
            sb.append("<br><b>"); // NOI18N
            sb.append(line.getText());
            sb.append("</b><br>"); // NOI18N
            TLUtils.appendSurroundingLine(sb, l, +1);
            sb.append("</html>"); // NOI18N
            beforeContents = sb.toString();

            sb.setLength(0);
            sb.append("<html>"); // NOI18N
            TLUtils.appendSurroundingLine(sb, l, -1);
            sb.append("<br><b><i>// "); // NOI18N
            sb.append(line.getText());
            sb.append("</i></b><br>"); // NOI18N
            TLUtils.appendSurroundingLine(sb, l, +1);
            sb.append("</html>"); // NOI18N
            afterContents = sb.toString();
        } else {
            String rulename = violation.getRule().getName();
            if (rulename.equals("UnusedImports")) { // NOI18N
                beforeDesc = NbBundle.getMessage(ImportPerformer.class,
                                "ImportConfirmationUnused"); // NOI18N
            } else if (rulename.equals("ImportFromSamePackage")) { // NOI18N
                beforeDesc = NbBundle.getMessage(ImportPerformer.class,
                                "ImportConfirmationSame"); // NOI18N
            } else if (rulename.equals("DontImportJavaLang")) { // NOI18N
                beforeDesc = NbBundle.getMessage(ImportPerformer.class,
                                "ImportConfirmationLang"); // NOI18N
            } else if (rulename.equals("DuplicateImports")) { // NOI18N
                beforeDesc = NbBundle.getMessage(ImportPerformer.class,
                                "ImportConfirmationDuplicate"); // NOI18N
            } else {
                beforeDesc = NbBundle.getMessage(ImportPerformer.class,
                                "ImportConfirmationOther"); // NOI18N
            }

            Line l = line;
            sb.append("<html>"); // NOI18N
            TLUtils.appendSurroundingLine(sb, l, -1);
            sb.append("<br>");
            sb.append("<b><strike>"); // NOI18N
            sb.append(line.getText());
            sb.append("</strike></b>"); // NOI18N
            sb.append("<br>"); // NOI18N
            TLUtils.appendSurroundingLine(sb, l, +1);
            sb.append("</html>"); // NOI18N
            beforeContents = sb.toString();
        }
        
        return new ConfPanel(beforeDesc, 
                             beforeContents, afterDesc, 
                             afterContents,
                             filename, linenumber, 
                             ViolationProvider.getBottomPanel(ruleDesc, 
                                                              ruleExample));
        
    }
}
