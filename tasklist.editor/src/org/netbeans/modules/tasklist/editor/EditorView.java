/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.tasklist.editor;

import org.netbeans.editor.PopupManager;
import org.netbeans.editor.BaseTextUI;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.ext.ExtEditorUI;
import org.openide.windows.WindowManager;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.nodes.Node;
import org.openide.cookies.EditorCookie;

import javax.swing.text.JTextComponent;
import javax.swing.*;

/**
 * Presents suggestions right in editor view.
 *
 * @author Petr Kuzel
 */
public final class EditorView {

    private EditorView() {

    }

    /** Show selected component if currently vissible editor. */
    public static void show(JComponent component) {
        Mode mode = WindowManager.getDefault().findMode(CloneableEditorSupport.EDITOR_MODE);
        if (mode == null) return;
        TopComponent tc = mode.getSelectedTopComponent();
        Node[] nodes = tc.getActivatedNodes();
        if (nodes == null) return;

        EditorCookie cake = (EditorCookie) nodes[0].getCookie(EditorCookie.class);
        JEditorPane[] panes = cake.getOpenedPanes();

        PopupManager pop = getPopup(panes[0]);
        pop.install(component);
    }

    private static PopupManager getPopup(JEditorPane pane) {
        ExtEditorUI ui = getEditorUI(pane);
        return ui.getPopupManager();
    }

    private static BaseTextUI getBaseTextUI(JEditorPane pane){
        return (pane!=null)?(BaseTextUI)pane.getUI():null;
    }

    private static ExtEditorUI getEditorUI(JEditorPane pane){
        BaseTextUI btui = getBaseTextUI(pane);
        return (btui!=null) ? (ExtEditorUI)btui.getEditorUI() : null;
    }

}
