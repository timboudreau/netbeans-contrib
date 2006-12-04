/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
