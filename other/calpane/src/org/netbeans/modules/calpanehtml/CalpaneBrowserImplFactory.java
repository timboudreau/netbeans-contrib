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
