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

package org.netbeans.bluej.welcome;

import java.awt.Component;
import java.awt.Cursor;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.openide.ErrorManager;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
//import org.netbeans.api.javahelp.Help;

/**
 * Handles welcome screen buttons. It should be loaded
 * into memory only if a button pressed saving few ms.
 *
 * @author Petr Kuzel
 */
final class BusinessLogic {
    
    private static final int THREE_SECONDS = 3 * 1000;
    
    /**
     *
     * @param opcode 1-6 for button set. 101 top log, 102 bottom logo
     */
    static void perform(int opcode, JComponent client) {
        switch (opcode) {
            case 1:
                showURL("HTML_NEWS", client); // NOI18N
                break;                
            case 2:
                showURL("HTML_QUICKSTART", client); // NOI18N
                break;
            case 3:
                showURL("HTML_modules", client);  // NOI18N
                break;                
            case 4:
                showURL("HTML_Collab", client);  // NOI18N
                break;                
            case 5:
                showURL("HTML_Profiler", client);  // NOI18N
                break;                
            case 6:
                showURL("HTML_J2ME", client);  // NOI18N
                break;                
            case 101:
                showURL("HTML_JAVA", client);  // NOI18N
                break;
            case 102:
                showURL("HTML_SUN", client);  // NOI18N
                break;                
            case 103:
                showURL("HTML_NB", client);  // NOI18N
                break;                                
            default:
                assert false : "Unexpected operation code: " + opcode;  // NOI18N
        }
    }
    
    private static void showURL(String bundleKey, JComponent client) {
        try {
            start(client);
            URL url = new URL(NbBundle.getMessage(BusinessLogic.class, bundleKey));
            if (url.getProtocol().equalsIgnoreCase("nbinst")) { // NOI18N
                url = externalizeURL(url);
            }
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);            
        } catch (java.net.MalformedURLException ex) {
            ErrorManager.getDefault().notify(ex);
        } finally {
            // waiting for #63987
            // done(client);
        }
    }    

//    private static void showHelp(String id, JComponent client) {
//        Help help = (Help) Lookup.getDefault().lookup(Help.class);
//        if (help != null) {
//            try {
//                start(client);
//                help.showHelp(new HelpCtx(id));
//            } finally {
//                done(client);
//            }
//        }
//    }

    /**
     * Enables glass pane for max 3sec. It disallows
     * user to select same slow action by accident two times.
     */
    private static void start(final JComponent client) {
            Component root = SwingUtilities.getRoot(client);
            final Component glasspane = (root instanceof JFrame) ? ((JFrame)root).getGlassPane() : null;
            
            // do the timeouting waiting cursor
            if (glasspane != null) {
                glasspane.setVisible(true);
                glasspane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }
            
            org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
                public void run() { 
                    done(client);
                }
            }, THREE_SECONDS);        
    }
    
    /**
     * Hides the glass pane.
     */
    private static void done(JComponent client) {
        Component root = SwingUtilities.getRoot(client);
        final Component glasspane = (root instanceof JFrame) ? ((JFrame)root).getGlassPane() : null;
        if (glasspane!=null) {
            glasspane.setCursor(Cursor.getDefaultCursor()); 
            glasspane.setVisible(false);
        }
    }
    
    private static URL externalizeURL(URL url) {       
        FileObject fo = URLMapper.findFileObject(url);
        if (fo == null) {
            try {
                return new URL("http://www.netbeans.org");  // NOI18N
            } catch (MalformedURLException ex) {
                // ignore
                return null;
            }
        } else {
            return URLMapper.findURL(fo,URLMapper.EXTERNAL);
        }
    }
    
}
