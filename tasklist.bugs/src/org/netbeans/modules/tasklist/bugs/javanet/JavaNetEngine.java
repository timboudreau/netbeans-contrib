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

package org.netbeans.modules.tasklist.bugs.javanet;

import org.netbeans.modules.tasklist.bugs.issuezilla.IZBugEngine;
import org.netbeans.modules.tasklist.bugs.BugQuery;
import org.netbeans.modules.tasklist.bugs.BugList;
import org.netbeans.modules.tasklist.bugs.bugzilla.SourcePanel;

import javax.swing.*;

/**
 * Java.net uses IZ derivate.
 * 
 * @author Petr Kuzel
 */
public class JavaNetEngine extends IZBugEngine {

    public JComponent getQueryCustomizer(BugQuery query, boolean edit) {
        return new SourcePanel(false);
    }

    public void refresh(BugQuery query, BugList list) {

        // Java.net uses HTTPS that is not properly set up by default

        String httpProxyHost = System.getProperty("http.proxyHost");  // NOI18N
        String httpProxyPort = System.getProperty("http.proxyPort");  // NOI18N

        String httpsProxyHost = System.getProperty("https.proxyHost", null);  // NOI18N

        if (httpsProxyHost == null && httpProxyHost != null) {
            System.out.println("Inheriting HTTPS proxy settings from HTTP proxy settings...");
            System.setProperty("https.proxyHost", httpProxyHost);   // NOI18N
            System.out.print("-J-Dhttps.proxyHost=" + httpProxyHost);
            if (httpProxyPort != null) {
                System.setProperty("https.proxyPort", httpProxyPort);  // NOI18N
                System.out.print(" -J-Dhttps.proxyPort=" + httpProxyPort);
            }
            System.out.println();
        }

        super.refresh(query, list);
    }

}
