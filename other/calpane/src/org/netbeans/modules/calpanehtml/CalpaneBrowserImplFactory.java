/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.calpanehtml;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;

import org.openide.awt.HtmlBrowser;

import calpa.html.*;

/**
 * @author Jesse Glick
 */
public class CalpaneBrowserImplFactory implements HtmlBrowser.Factory {
    
    public HtmlBrowser.Impl createHtmlBrowserImpl() {
        return new CalpaneBrowserImpl();
    }
    
    private static class CalpaneBrowserImpl extends HtmlBrowser.Impl implements CalHTMLObserver {
        
        private PropertyChangeSupport pcs;
        private final CalHTMLPane pane;
        private URL url;
        private String message;
        
        public CalpaneBrowserImpl() {
            CalHTMLPreferences prefs = new CalHTMLPreferences();
            prefs.setHandleNewFrames(false);
            // XXX what is the name of the top-level frame supposed to be??
            pane = new CalHTMLPane(prefs, this, "Calpane");
        }
        
        public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
            if (pcs == null) {
                pcs = new PropertyChangeSupport(this);
            }
            pcs.addPropertyChangeListener(l);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener l) {
            if (pcs != null) {
                pcs.removePropertyChangeListener(l);
            }
        }
        
        public void backward() {
            pane.goBack();
        }
        
        public void forward() {
            pane.goForward();
        }
        
        public Component getComponent() {
            return pane;
        }
        
        public String getStatusMessage() {
            return message;
        }
        
        public String getTitle() {
            return ""; // XXX
        }
        
        public URL getURL() {
            return url;
        }
        
        public boolean isBackward() {
            return false; // XXX
        }
        
        public boolean isForward() {
            return false; // XXX
        }
        
        public boolean isHistory() {
            return false;
        }
        
        public void reloadDocument() {
            pane.showHTMLDocument(url, null, true);
        }
        
        public void setURL(URL url) {
            this.url = url;
            pane.showHTMLDocument(url);
            pcs.firePropertyChange(PROP_URL, null, null);
        }
        
        public void showHistory() {
            // XXX
        }
        
        public void stopLoading() {
            // XXX
        }
        
        public void historyUpdate(CalHTMLPane calHTMLPane, int posn) {
            pcs.firePropertyChange(PROP_BACKWARD, null, null);
            pcs.firePropertyChange(PROP_FORWARD, null, null);
            pcs.firePropertyChange(PROP_HISTORY, null, null);
        }
        
        public void linkActivatedUpdate(CalHTMLPane calHTMLPane, URL uRL, String targetFrame, String jName) {
            url = uRL;
            pcs.firePropertyChange(PROP_URL, null, null);
        }
        
        public void formSubmitUpdate(CalHTMLPane calHTMLPane, URL uRL, int method, String action, String data) {
            url = uRL;
            pcs.firePropertyChange(PROP_URL, null, null);
        }
        
        public void linkFocusedUpdate(CalHTMLPane calHTMLPane, URL uRL) {
        }
        
        public void showNewFrameRequest(CalHTMLPane calHTMLPane, String str, URL uRL) {
            // XXX
        }
        
        public void statusUpdate(CalHTMLPane calHTMLPane, int status, URL uRL, int value, String message) {
            this.message = message;
            pcs.firePropertyChange(PROP_STATUS_MESSAGE, null, null);
        }
        
    }
    
}
