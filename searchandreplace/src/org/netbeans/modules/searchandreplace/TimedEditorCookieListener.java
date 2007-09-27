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