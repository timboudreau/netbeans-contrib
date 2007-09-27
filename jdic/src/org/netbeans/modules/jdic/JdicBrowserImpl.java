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

package org.netbeans.modules.jdic;

import java.beans.*;
import java.net.*;

import org.jdesktop.jdic.browser.*;
import org.openide.awt.HtmlBrowser.Impl;
import org.openide.util.NbBundle;

/**
 * JDIC browser support
 *
 * @author Martin Grebac
 */
public class JdicBrowserImpl extends org.openide.awt.HtmlBrowser.Impl {

    private WebBrowser webBrowser = null;

    /** standard helper variable */
    private PropertyChangeSupport pcs;

    /** requested URL */
    private URL url;
    private String title = "";      // NOI18N
    private String statusMsg = "";      // NOI18N
    private boolean isForward = false;
    private boolean isBackward = false;
    
    /** Default constructor. 
      * <p>Builds PropertyChangeSupport. 
      */
    public JdicBrowserImpl() {
        pcs = new PropertyChangeSupport (this);
    }
    
    public boolean isHistory() { return false; }
    public void showHistory() {}

    public boolean isBackward() { 
        return isBackward;
    }
    
    public boolean isForward() { 
        return isForward;
    }

    /** Returns status message representing status of html browser.
     *
     * @return status message.
     */
    public String getStatusMessage() {
        return statusMsg;
    }
        
    public String getTitle() {
        return title;
    }

    protected void setTitle (String title) {
        this.title = title;
    }

    /** Returns current URL.
     *
     * @return current URL.
     */
    public URL getURL() {
        return url;
    }

    public void stopLoading() { 
        webBrowser.stop();
    }

    public void backward() {
        if (webBrowser == null) {
            init();
        }
        webBrowser.back();
    }
    
    public void forward() {
        if (webBrowser == null) {
            init();
        }
        webBrowser.forward();
    }

    /** Call setURL again to force reloading.
     * Browser must be set to reload document and do not cache them.
     */
    public void reloadDocument() {
        if (webBrowser == null) {
            init();
        }
        webBrowser.refresh();
    }
            
    /** Sets current URL.
     *
     * @param url URL to show in the browser.
     */
    public synchronized void setURL(final URL url) {
        this.url = url;
        if (webBrowser == null) {
            init();
        }
        webBrowser.setURL(url);
    }
    
    /** Returns visual component of html browser.
     *
     * @return visual component of html browser.
     */
    public final java.awt.Component getComponent() {
        if (webBrowser == null) {
            init();
        }
        return webBrowser;
    }
    
    private synchronized void init() {
        if (webBrowser == null) {
            try {
                webBrowser = new WebBrowser();
                //webBrowser.setDebug(true); // debugging info to console
            } catch (Exception e) {
                // TODO
                return;
            }

            webBrowser.addWebBrowserListener(new WebBrowserListener() {
                public void downloadStarted(WebBrowserEvent event) {
                    String oldMsg = statusMsg;
                    statusMsg = NbBundle.getMessage(JdicBrowserImpl.class, "MSG_Download_started"); //NOI18N
                    pcs.firePropertyChange(PROP_STATUS_MESSAGE, oldMsg, statusMsg);
                }

                public void downloadCompleted(WebBrowserEvent event) {
                    //update status message
                    String oldMsg = statusMsg;
                    statusMsg = NbBundle.getMessage(JdicBrowserImpl.class, "MSG_Download_completed"); //NOI18N
                    pcs.firePropertyChange(PROP_STATUS_MESSAGE, oldMsg, statusMsg);

                    //update fwd and back buttons' state
                    boolean oldFwd = isForward;
                    boolean oldBack = isBackward;
                    isForward = webBrowser.getStatus().isForwardEnabled();
                    isBackward = webBrowser.getStatus().isBackEnabled();

                    System.err.println("back: " + isBackward);
                    if (isForward != oldFwd) {
                        pcs.firePropertyChange(PROP_FORWARD, oldFwd, isForward);
                    }
                    if (isBackward != oldBack) {
                        pcs.firePropertyChange(PROP_BACKWARD, oldBack, isBackward);
                    }

                    //update url
                    URL old = url;
                    URL url = webBrowser.getURL();
                    if (old != url) {
                        pcs.firePropertyChange(PROP_URL, old, url);
                    }
                }

                public void downloadProgress(WebBrowserEvent event) {
                }

                public void downloadError(WebBrowserEvent event) {
                    String oldMsg = statusMsg;
                    statusMsg = event.getData();
                    pcs.firePropertyChange(PROP_STATUS_MESSAGE, oldMsg, statusMsg);
                }

                public void titleChange(WebBrowserEvent event) {
                    String old = title;
                    title = event.getData();
                    pcs.firePropertyChange(Impl.PROP_TITLE, old, title);
                }  

                public void statusTextChange(WebBrowserEvent event) {
                    String oldMsg = statusMsg;
                    statusMsg = event.getData();
                    pcs.firePropertyChange(PROP_STATUS_MESSAGE, oldMsg, statusMsg);
                }  
            });
        }
    }

    /** Adds PropertyChangeListener to this browser.
     *
     * @param l Listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener (l);
    }
    
    /** Removes PropertyChangeListener from this browser.
     *
     * @param l Listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener (l);
    }
    
}
