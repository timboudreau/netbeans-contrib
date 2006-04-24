/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
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
            if (url.getProtocol().equalsIgnoreCase("nbinst")) {
                url = externalizeURL(url); // NOI18N
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
