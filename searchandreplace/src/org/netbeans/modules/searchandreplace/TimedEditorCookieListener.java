/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.searchandreplace;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JEditorPane;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * If the user double clicks an item in the table, we want to open the file
 * using its node's open cookie or edit cookie.  That operation is asynch -
 * it won't immediately have an EditorCookie we can use to scroll the
 * editor to the position of the item that was clicked.
 * <p>
 * So instead, we need to attach a listener to the Node's lookup, which will
 * wait for the EditorCookie to appear.  But it's possible that the
 * EditorCookie will never appear, so we don't want to be listening for one
 * forever, or randomly scrolling at some late date.
 * <p>
 * This class will listen for an EditorCookie to appear for 7 seconds, and
 * if one does, will scroll to the correct location;  at the end of that, it
 * will detach itself and disappear.
 *
 * @author Tim Boudreau
 */
class TimedEditorCookieListener implements LookupListener, ActionListener, Runnable {
    private final Timer timer;
    private final int loc;
    private final Lookup.Result result;

    private final int end;
    public TimedEditorCookieListener(Lookup lkp, int loc, int end) {
        this.loc = loc;
        this.end = end;
        result = lkp.lookup(new Lookup.Template(EditorCookie.class));
        if (!position()) {
            result.addLookupListener(this);
            timer = new Timer (7000, this);
            timer.start();
        } else {
            timer = null;
        }
    }

    public void resultChanged(LookupEvent ev) {
        position();
    }

    public void cancel() {
        timer.stop();
    }

    public void actionPerformed(ActionEvent e) {
        shutdown();
        //one last try
        position();
    }

    private void shutdown() {
        result.removeLookupListener(this);
        timer.stop();
    }

    private boolean position() {
        Collection c = result.allInstances();
        if (!c.isEmpty()) {
            EditorCookie ck = (EditorCookie) c.iterator().next();
            scroll (ck);
            return true;
        } else {
            return false;
        }
    }

    private JEditorPane[] panes;
    private void scroll(EditorCookie editorCookie) {
        panes = editorCookie.getOpenedPanes();
        if (panes != null && panes.length > 0) {
            EventQueue.invokeLater(this);
        }
    }

    public void run() {
        if (panes != null && panes.length > 0) {
            try {
                Rectangle r = panes[0].modelToView(loc);
                panes[0].scrollRectToVisible(r);
                panes[0].setSelectionStart(loc);
                panes[0].setSelectionEnd(end);
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify (
                        ErrorManager.INFORMATIONAL, ex);
                return;
            }
        }
    }
}