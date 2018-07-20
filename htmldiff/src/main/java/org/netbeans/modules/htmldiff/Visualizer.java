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

package org.netbeans.modules.htmldiff;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.HashSet;
import java.util.Set;

/** Implementation of the Diff visualizer.
 *
 * @author  Jaroslav Tulach
 */
public final class Visualizer extends org.netbeans.spi.diff.DiffVisualizer {

    public java.awt.Component createView(
        org.netbeans.api.diff.Difference[] diffs, String name1, String title1, Reader r1,
        String name2, String title2, Reader r2, String MIMEType
    ) throws IOException {
        HtmlDiff[] res = HtmlDiff.diff (r1, r2);
        
        StringWriter w = new StringWriter ();
        for (int i = 0; i < res.length; i++) {
            if (res[i].isDifference()) {
                // put there both
                int oldLen = res[i].getOld ().length ();
                if (oldLen > 0) {
                    w.write ("<strike>");
                    w.write (res[i].getOld());
                    w.write ("</strike>");
                }
                int newLen = res[i].getNew ().length ();
                if (newLen > 0) {
                    w.write ("<font bgcolor=\"#FFFF00\">");
                    w.write (res[i].getNew());
                    w.write ("</font>");
                }
            } else {
                w.write (res[i].getNew ());
            }
        }

        org.openide.awt.HtmlBrowser b = new org.openide.awt.HtmlBrowser ();
        b.setEnableHome(false);
        b.setEnableLocation(false);
        b.setStatusLineVisible(false);
        b.setURL (new URL (null, "file://", new UH (w.toString())));
        b.setName(org.openide.util.NbBundle.getMessage(Visualizer.class, "FMT_NAME", name1, name2));
        return b;
    }
    
    class UH extends java.net.URLStreamHandler {
        private String s;
        
        public UH (String s) {
            this.s = s;
        }
        
        
        protected java.net.URLConnection openConnection(URL u) throws IOException {
            return new UC (u, s);
        }
        
    }
    class UC extends java.net.URLConnection {
        private String s;
        
        public UC (URL u, String s) {
            super (u);
            this.s = s;
        }
        
        public void connect() throws IOException {
        }
        
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream (s.getBytes ());
        }
        
        public String getContentType() {
            return "text/html";
        }
        
    }
}
