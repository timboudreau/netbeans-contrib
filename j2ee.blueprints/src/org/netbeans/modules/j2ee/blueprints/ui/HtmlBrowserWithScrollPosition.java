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

package org.netbeans.modules.j2ee.blueprints.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import org.openide.awt.HtmlBrowser;

/**
 * Html Browser component that allows the user to get and set the
 * scroll position.  Useful for implementing a forward/back button
 * mechanism.
 *
 * @author Mark Roth
 */
public class HtmlBrowserWithScrollPosition 
    extends HtmlBrowser
{
    /** Timer to use for scrolling this tab */
    private Timer scrollTimer = null;
    
    /** Creates a new instance of HtmlBrowserWithScrollPosition */
    public HtmlBrowserWithScrollPosition(boolean toolbar, boolean statusLine) {
        super(toolbar, statusLine);
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
