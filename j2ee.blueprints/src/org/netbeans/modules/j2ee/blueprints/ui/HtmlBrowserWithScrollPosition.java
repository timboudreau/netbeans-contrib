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

import java.io.*; 
import java.awt.*; 
import java.awt.event.*;
import java.net.*;
import javax.swing.*; 
import javax.swing.event.*; 
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.Timer;
import org.openide.awt.HtmlBrowser;

public class HtmlBrowserWithScrollPosition extends JPanel implements HyperlinkListener { 

    // for displaying in the external browser
    private HtmlBrowser.URLDisplayer displayer = HtmlBrowser.URLDisplayer.getDefault();
    /** Timer to use for scrolling this tab */
    private Timer scrollTimer = null;
    // html rendering pane
    protected JEditorPane html;
    // URL input field - not necessary for being used in the component
    protected JTextField  inputURL;
    
    public HtmlBrowserWithScrollPosition() {
        setLayout(new BorderLayout());

        // construct html pane
        html = new JEditorPane();
        html.setEditorKitForContentType("text/html", new HTMLEditorKit()); //NOI18N
        html.setEditable(false);
        html.addHyperlinkListener(this);

        // construct URL input field - not necessary for the component
        inputURL = new JTextField();
        inputURL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionevent) {      
                 try {
                     setURL(new URL(inputURL.getText()));
                 } catch (MalformedURLException e) {
                     System.out.println("malformed url"); // NOI18N
                     //ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                 }
                 
            }
        });
        // only for testing with main()
        // add(inputURL, BorderLayout.NORTH);
        add(new JScrollPane(html), BorderLayout.CENTER); 
		
	}

    // override the method in HyperLinkListener
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            setURL(e.getURL());
        }
    }
 
    protected void setURL(URL u) {
        if (u!=null) {
        String protocol = u.getProtocol();
        // spawn the external browser for these types of links
        if (protocol.equals("http") || protocol.equals("https")) {
            displayer.showURL(u);
        } else {
            inputURL.setText(u.toString());
            Cursor c = html.getCursor();
            Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
            html.setCursor(waitCursor);
            SwingUtilities.invokeLater(new HtmlLoader(u, c));
        }
        }
    } 

    // Mostly copied from swing sample - this class may need to be synchronized
    class HtmlLoader implements Runnable {
        URL url;
        Cursor cursor; 

        HtmlLoader(URL u, Cursor c) {
            url = u;
            cursor = c;
        } 

        public void run() {
            if (url == null) {
                html.setCursor(cursor);
                Container parent = html.getParent();
                parent.repaint();
            } else {
                Document doc = html.getDocument();
                try {
                    html.setPage(url);
                } catch (IOException e) {
                    html.setDocument(doc);
                    System.out.println("IOException" + e); // NOI18N
                } finally {
                    url = null;
                    SwingUtilities.invokeLater(this);
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
