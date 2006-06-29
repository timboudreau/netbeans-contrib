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
 * Software is Nokia. Portions Copyright 2004 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.zeroadmin;

import org.openide.ErrorManager;
import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;


/**
 * External browser that was used to invoke the program using JNLP
 * is used also for displaying all other HTML content. This class is
 * instantiated using META-INF/services/org.openide.awt.HtmlBrowser$URLDisplayer
 * file.
 * @author David Strupl
 */
public class JNLPURLDisplayer extends org.openide.awt.HtmlBrowser.URLDisplayer {

    /** Creates a new instance of JNLPURLDisplayer */
    public JNLPURLDisplayer() {
    }

    /**
   * The only method that has to be implemented. The implementation
     * here simply calls to JNLP API.
     */
    public void showURL(java.net.URL u) {
        try {
            BasicService bs = (BasicService)
                ServiceManager.lookup("javax.jnlp.BasicService"); // NOI18N
            
            bs.showDocument(u);
            
        } catch (Exception e) {
            ErrorManager.getDefault().getInstance("org.netbeans.modules.zeroadmin").notify(e); // NOI18N
        }
    }
}
