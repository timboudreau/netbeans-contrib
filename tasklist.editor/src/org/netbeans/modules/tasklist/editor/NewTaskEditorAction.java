/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.editor;

import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.BaseAction;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.LocaleSupport;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.LocaleSupport.Localizer;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.tasklist.usertasks.actions.NewTaskAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.text.DataEditorSupport;
import org.openide.text.Line;
import org.openide.util.NbBundle;



/**
 * Editor action, quite equivalent to NewTaskAction,
 * but plugs into the editor architecture such that it
 * can be embedded in the editor magin popup menu.
 *
 * @author Tor Norbye
 */
public class NewTaskEditorAction extends BaseAction implements Localizer {

    /**
     * Add a new task tied ot the current line
     */
    public static final String newTodoItemAction = "new-todo-item"; // NOI18N


    public NewTaskEditorAction() {
        super(newTodoItemAction);
        LocaleSupport.addLocalizer(this); // XXX is this too late?
    }

    static final long serialVersionUID = 8870696224845563315L;

    public void actionPerformed(ActionEvent evt, JTextComponent target) {
        if (target == null)
            return;

        // TODO test shows the componnet far from caret
        //EditorView.show(new JLabel("HOHOHOHOH"));

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

        Line lineObj = NbEditorUtilities.getLine(doc, caret.getDot(), false);
        DataObject dob = DataEditorSupport.findDataObject(lineObj);
        if (dob == null)
            return;
        
        FileObject fo = dob.getPrimaryFile();
        URL url = URLMapper.findURL(fo, URLMapper.EXTERNAL);
        if (url == null)
            return;

        NewTaskAction.performAction(null, null, url, line, true);
    }

    public String getString(String str) {
        return NbBundle.getMessage(NewTaskEditorAction.class, str);
    }

}
