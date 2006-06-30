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
