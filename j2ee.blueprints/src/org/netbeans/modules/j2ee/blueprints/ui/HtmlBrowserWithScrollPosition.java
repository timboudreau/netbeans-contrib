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
