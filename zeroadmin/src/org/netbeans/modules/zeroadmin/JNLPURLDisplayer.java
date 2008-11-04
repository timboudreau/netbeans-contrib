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
 * Software is Nokia. Portions Copyright 2004 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.zeroadmin;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;


/**
 * External browser that was used to invoke the program using JNLP
 * is used also for displaying all other HTML content. This class is
 * instantiated using META-INF/services/org.openide.awt.HtmlBrowser$URLDisplayer
 * file.
 * @author David Strupl
 */
@org.openide.util.lookup.ServiceProvider(service=org.openide.awt.HtmlBrowser.URLDisplayer.class, supersedes="org.netbeans.core.NbTopManager$NbURLDisplayer")
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
            Logger.getLogger(JNLPURLDisplayer.class.getName()).log(
                Level.SEVERE, "Cannot show URL : " + u, e); // NOI18N
        }
    }
}
