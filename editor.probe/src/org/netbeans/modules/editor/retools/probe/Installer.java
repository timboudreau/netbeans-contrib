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
package org.netbeans.modules.editor.retools.probe;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.FocusManager;
import javax.swing.SwingUtilities;
import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    public static final Logger FLOG = Logger.getLogger("nbeditor.focus"); //NOI18N
    private static final Logger LOG = Logger.getLogger(Installer.class.getName());
    
    private static HttpServer server = null;
    private static FocusLog focusLog = null;
    
    @Override
    public void restored() {
        assert server == null;
        
        for(int i = 9000; i <= 9010; i++) {
            try {
                server = new HttpServer(i);
                LOG.log(Level.INFO, "Editor Probe listening on port " + i); //NOI18N
                // port works, quit the loop
                break;
            } catch (IOException ioe) {
                // try another port
            }
        }
        
        assert focusLog == null;
        focusLog = new FocusLog();
        FocusManager.getCurrentManager().addPropertyChangeListener(focusLog);
    }

    @Override
    public void close() {
        shutdownServer();
        shutdownLogging();
    }

    @Override
    public void uninstalled() {
        shutdownServer();
        shutdownLogging();
    }

    private void shutdownServer() {
        if (server != null) {
            server.kill();
            server = null;
        }
    }

    private void shutdownLogging() {
        if (focusLog != null) {
            FocusManager.getCurrentManager().removePropertyChangeListener(focusLog);
            focusLog = null;
        }
    }

    private static final class HttpServer extends NanoHTTPD {

        private static final String REFRESH_MESSAGE = "<html>" //NOI18N
            + "<head>" //NOI18N
            + "<META HTTP-EQUIV=\"Refresh\" CONTENT=\"5; URL=/\" />" //NOI18N
            + "</head>" //NOI18N
            + "<body>" //NOI18N
            + "<p>Editor Probe " + Probe.getVersion() + " is running...</p>" //NOI18N
            + "<p>Please Alt+Tab back to Netbeans and wait for at least 5 seconds, you can than come back here.</p>" //NOI18N
            + "</body>" //NOI18N
            + "</html>"; //NOI18N
        
        public HttpServer(int port) throws IOException {
            super(port);
        }

        @Override
        public NanoHTTPD.Response serve(String uri, String method, Properties header, Properties parms) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Exception Probe serving request: '" + uri + "'"); //NOI18N
            }
            
            final String [] message = new String [1];
            
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        boolean isFocused = WindowManager.getDefault().getMainWindow().isFocused();
                        if (isFocused) {
                            message[0] = new Probe().getStatus();
                        }
                    }
                });
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Exception in Editor Probe", e); //NOI18N
                message[0] = "Exception in Editor Probe: " + e.getMessage(); //NOI18N
            }
            
            if (message[0] != null) {
                return new NanoHTTPD.Response(HTTP_OK, "text/plain", message[0]); //NOI18N
            } else {
                return new NanoHTTPD.Response(HTTP_OK, "text/html", REFRESH_MESSAGE); //NOI18N
            }
        }
    } // End of HttpServer class
    
    private static final class FocusLog implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            FLOG.info("FM pchng: '" + evt.getPropertyName()
                    + "' old=[" + Probe.s2s(evt.getOldValue())
                    + "] new=[" + Probe.s2s(evt.getNewValue()) + "]"); //NOI18N
        }
    } // End of FocusLog class
}
