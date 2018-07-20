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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Parses an HTML page in order to find all <a href=".." /> tags there.
 *
 * @author  Jaroslav Tulach
 */
final class ParseURLs extends Object {
    /** No instances.
     */
    private ParseURLs() {
    }

    /** Reads a content of a reader and finds all <a href="" /> tags there
     * @param r stream to read
     * @param url base url of the document
     * @return array of urls that were found in the document
     */
    public static URL[] parse (Reader r, URL url) throws IOException {
        Section only = parseWithSections (r, false, url)[0];
        return only.getURLs ();
    }

    /** Scans the document for sections.
     * @param r stream to read
     * @param url base url of the document
     * @return array of sections
     */
    public static Section[] sections (Reader r, URL url) throws IOException {
        return parseWithSections (r, true, url);
    }
    
    /** Reads a content of a reader and finds all <a name="" /> tags and
     * their positions and URLs in each section.
     *
     * @param r stream to read
     * @param url base url of the document
     * @return array of urls that were found in the document
     */
    private static Section[] parseWithSections (Reader r, boolean doSections, URL url) throws IOException {
        BufferedReader buf = new BufferedReader (r);
        
        int state = 0;
        int pos = 0;
        int lastPos = 0;
        ArrayList sections = new ArrayList ();
        Section lastSection = new Section (null, 0);
        sections.add (lastSection);
        
        StringBuffer parsing = null;
        for (;;) {
            int read = buf.read ();
            if (read == -1) break;
            char ch = (char)read;
            pos++;
            
            switch (state) {
            case 0: // looking for <
                if (ch == '<') {
                    state = 1;
                    lastPos = pos - 1;
                }
                break;
            case 1: // looking for a
                if (Character.isSpaceChar(ch)) {
                    break;
                }
                if (ch == 'a' || ch == 'A') {
                    state = 2;
                    parsing = new StringBuffer ();
                    break;
                }
                state = 99;
                break;
            case 2: // wait till "
                if (Character.isSpaceChar (ch) || ch == '\n') {
                    break;
                }
            case 3: // wait till end " in HREF mode
            case 4: // wait till end " in NAME mode
                if (ch != '"') {
                    parsing.append (ch);
                    break;
                }
                // end reached now decide
                if (state == 2) {
                    // href= stuff
                    if (parsing.toString ().toUpperCase().equals ("HREF=")) {
                        state = 3;
                        parsing = new StringBuffer ();
                        break;
                    };
                    if (doSections && parsing.toString ().toUpperCase().equals ("NAME=")) {
                        state = 4;
                        parsing = new StringBuffer ();
                        break;
                    }
                    // some garbge
                    state = 99;
                } else {
                    if (state == 4) {
                        // new section started
                        lastSection = new Section (parsing.toString (), lastPos);
                        sections.add (lastSection);
                    } else {
                        // end of URL
                        try {
                            URL u = new URL (url, parsing.toString());
                            lastSection.arr.add (u);
                        } catch (java.net.MalformedURLException ex) {
                            org.openide.ErrorManager.getDefault ().log ("Wrong URL: " + parsing);
                        }
                    }
                    // initialize for the rest
                    parsing = null;
                    state = 99;
                }
                break;
            case 99: // looking for end >
                if (ch == '>') {
                    state = 0;
                }
                break;
            default:
                throw new IllegalStateException ();
            }
        }
        
        return (Section[])sections.toArray (new Section[0]);
    }

    /** Describes one NAME section in the document.
     */
    public static final class Section extends Object {
        final String name;
        final int pos;
        final ArrayList arr = new ArrayList ();
        
        Section (String name, int pos) {
            this.name = name;
            this.pos = pos;
        }
        
        /** @return name or null if this is the initial section
         */
        public String getName () {
            return name;
        }
        
        /** @return position where the section starts
         */
        public int getStart () {
            return pos;
        }
        
        /** List of referenced URLs.
         */
        public URL[] getURLs () {
            return (URL[])arr.toArray (new URL[0]);
        }
        
        /** Computes the percentage of changes for this section, this
         * depends on the provided changes of ContentDiff and 
         * links leading out from the section.
         *
         * @param base location of the file the Section is in
         * @param diff the content diff to resolve the change for
         * @return 0 - 100
         */
        public int getChanged (URL base, ContentDiff diff) {
            URL[] arr = getURLs ();
            if (arr.length == 0) {
                return 0;
            }
            
            String b = base.toExternalForm();

            int sum = 0;
            int pages = 0;
            for (int i = 0; i < arr.length; i++) {
                String s = arr[i].toExternalForm();
                int hash = s.indexOf ('#');
                if (hash != -1) {
                    s = s.substring (0, hash);
                }
                if (s.startsWith (b)) {
                    s = s.substring (b.length());
                }
                
                ContentDiff.Page page = diff.findPage(s);
                if (page != null) {
                    ContentDiff.Cluster c = diff.findCluster(page);
                    sum += c.getChanged ();
                    pages++;
                }
            }
            return pages == 0 ? 0 : sum / pages;
        }
    }
}
