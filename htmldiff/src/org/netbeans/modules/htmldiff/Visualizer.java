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
