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
        BufferedReader buf = new BufferedReader (r);
        
        ArrayList arr = new ArrayList ();
        int state = 0;
        StringBuffer parsing = null;
        for (;;) {
            int read = buf.read ();
            if (read == -1) break;
            char ch = (char)read;
            
            switch (state) {
            case 0: // looking for <
                if (ch == '<') {
                    state = 1;
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
            case 3: // wait till end "
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
                    } else {
                        // some garbge
                        state = 99;
                    }
                } else {
                    // end of URL
                    try {
                        URL u = new URL (url, parsing.toString());
                        arr.add (u);
                    } catch (java.net.MalformedURLException ex) {
                        org.openide.ErrorManager.getDefault ().log ("Wrong URL: " + parsing);
                    }
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
        
        return (URL[])arr.toArray (new URL[0]);
    }
}
