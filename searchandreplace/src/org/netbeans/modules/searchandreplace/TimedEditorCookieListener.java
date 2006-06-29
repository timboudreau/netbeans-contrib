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
 * If the user double clicks an item in the table, we wantto open the file
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

    static int TIMEOUT = 7000;

    private final int end;
    public TimedEditorCookieListener(Lookup lkp, int loc, int end) {
        this.loc = loc;
        this.end = end;
        result = lkp.lookup(new Lookup.Template(EditorCookie.class));
        if (!position()) {
            result.addLookupListener(this);
            timer = new Timer (TIMEOUT, this);
            timer.start();
        } else {
            timer = null;
        }
    }

    public void resultChanged(LookupEvent ev) {
        if (position()) {
            shutdown();
        }
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
                if (r != null) {
                    panes[0].scrollRectToVisible(r);
                    panes[0].setSelectionStart(loc);
                    panes[0].setSelectionEnd(end);
                }
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify (
                        ErrorManager.INFORMATIONAL, ex);
                return;
            }
        }
    }
}