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

package org.netbeans.modules.j2ee.blueprints.ui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.Timer;

import org.openide.ErrorManager;
import org.openide.awt.HtmlBrowser;

public class HtmlBrowserWithScrollPosition extends JPanel implements HyperlinkListener {

    // for displaying in the external browser
    private HtmlBrowser.URLDisplayer displayer = HtmlBrowser.URLDisplayer.getDefault();
    /** Timer to use for scrolling this tab */
    private Timer scrollTimer = null;
    private JLabel statusBar;
    // html rendering pane
    protected JEditorPane html;
    
    public HtmlBrowserWithScrollPosition() {
        setLayout(new BorderLayout());

        // construct html pane
        html = new JEditorPane();
        html.setEditable(false);
        html.addHyperlinkListener(this);

        add(new JScrollPane(html), BorderLayout.CENTER);
        
        statusBar = new JLabel(" "); // NOI18N
        statusBar.setBackground(new java.awt.Color(80, 80, 80));
        add(statusBar, BorderLayout.SOUTH);

	}

    // override the method in HyperLinkListener
    public void hyperlinkUpdate(HyperlinkEvent e) {
        HyperlinkEvent.EventType type = e.getEventType();
        if (type == HyperlinkEvent.EventType.ACTIVATED) {
            setURL(e.getURL());
        } else if (type == HyperlinkEvent.EventType.ENTERED) {
            statusBar.setText(e.getURL().toString());
        } else if (type == HyperlinkEvent.EventType.EXITED) {
            statusBar.setText(" "); // NOI18N
        }
    }
 
    protected void setURL(URL u) {
        if (u!=null) {
            String protocol = u.getProtocol();
            // spawn the external browser for these types of links
            if (protocol != null && (protocol.equals("http") || protocol.equals("https"))) {
                displayer.showURL(u);
            } else {
                Cursor currentC = html.getCursor();
                Cursor busyC = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
                html.setCursor(busyC);
                try {
                    html.setPage(u);
                } catch (IOException e) {
                    statusBar.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/blueprints/ui/Bundle").getString("doc_not_found"));
                } finally {
                    html.setCursor(currentC);
                }
            }
        }
    }
    
    /**
     * Returns the current position within this document, suitable for
     * passing to setBrowserScrollPosition.
     *
     * @return the scroll position
     */
    public int getScrollPosition() {
        int result = 0;
        // If this browser has a scroll pane, use it to determine
        // the current position.
        Component c = getComponent(0);
        if(c instanceof JScrollPane) {
            JScrollPane pane = (JScrollPane)c;
            result = pane.getVerticalScrollBar().getValue();
        }
        return result;
    }
    
    /**
     * Sets the current scroll position in this browser.
     * If the document hasn't fully loaded, this method will retry
     * for five seconds, once every 0.1 seconds.
     *
     * @param position The new scroll position, as returned from a 
     *     previous call to getScrollPosition().
     */
    public void setScrollPosition(final int position) {
        if(this.scrollTimer != null) {
            this.scrollTimer.stop();
        }
        this.scrollTimer = new Timer(100,
            new ActionListener() {
                int timeout = 50;
                public void actionPerformed(ActionEvent e) {
                    boolean done = true;
                    // If this browser has a scroll pane, use it to set
                    // the current position.
                    Component c = getComponent(0);
                    if(c instanceof JScrollPane) {
                        JScrollPane pane = (JScrollPane)c;
                        JScrollBar bar = pane.getVerticalScrollBar();
                        if(position <= bar.getMaximum()) {
                            bar.setValue(position);
                        }
                        else {
                            // Couldn't set yet.  Retry later.
                            done = false;
                        }
                    }
                    timeout--;
                    if(timeout <= 0) done = true;
                    if(done) {
                        scrollTimer.stop();
                        scrollTimer = null;
                    }
                }
            }
        );
        this.scrollTimer.start();
    }
} 
