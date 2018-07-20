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

package org.netbeans.modules.tasklist.editor;

import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.BaseAction;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.tasklist.usertasks.actions.NewTaskAction;
import org.openide.text.Line;
import org.openide.util.NbBundle;



/**
 * Editor action, quite equivalent to NewTaskAction,
 * but plugs into the editor architecture such that it
 * can be embedded in the editor magin popup menu.
 *
 * @author Tor Norbye
 */
public class NewTaskEditorAction extends BaseAction {

    /**
     * Add a new task tied ot the current line
     */
    public static final String NEW_USER_TASK_ACTION = "new-todo-item"; // NOI18N


    public NewTaskEditorAction() {
        super(NEW_USER_TASK_ACTION);
    }

    static final long serialVersionUID = 8870696224845563315L;

    public void actionPerformed(ActionEvent evt, JTextComponent target) {
        if (target == null)
            return;

        BaseDocument doc = (BaseDocument) target.getDocument();
        Caret caret = target.getCaret();

        /*
        // check whether the glyph gutter is visible or not
        if (Utilities.getEditorUI(target) == null || !Utilities.getEditorUI(target).isGlyphGutterVisible()) {
            target.getToolkit().beep();
            return;
        }
        */

        int line = 0;
        try {
            line = Utilities.getLineOffset(doc, caret.getDot());
        } catch (BadLocationException e) {
            target.getToolkit().beep();
            return;
        }

        Line lineObj = NbEditorUtilities.getLine(
                (Document) doc, caret.getDot(), false);
        NewTaskAction.performAction(lineObj);
    }

    public String getString(String str) {
        return NbBundle.getMessage(NewTaskEditorAction.class, str);
    }

    protected Class getShortDescriptionBundleClass() {
        return NewTaskEditorAction.class;
    }

}
