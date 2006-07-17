/* The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.

You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.

When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */
package org.netbeans.modules.genericnavigator;
import java.io.File;
/**
 * Object used in the list model in the JList shown in Navigator.
 * @author Tim Boudreau
 */
final class NavigationItem {
    public final File file;
    public final int offset;
    public final String txt;
    public final int end;
    public final boolean strip;
    NavigationItem(File f, int offset, int end, String txt, boolean strip) {
        this.offset = offset;
        this.file = f;
        this.txt = txt;
        this.end = end;
        this.strip = strip;
    }

    public String toString() {
        return strip ? stripHtml(txt) : txt;
    }

    static String stripHtml(String s) {
        StringBuffer result = new StringBuffer(s.length());
        char[] c = s.toCharArray();
        boolean inTag = false;
        int lastTagStart = -1;
        boolean lastWasWhitespace = false;
        boolean notAtag = false;
        for (int i = 0; i < c.length; i++) {
            //XXX need to handle entity includes
            boolean wasInTag = inTag;
            if (!inTag) {
                if (c[i] == '<') {
                    inTag = true;
                    lastTagStart = i;
                }
            } else {
                if (c[i] == '<') {
                    boolean localLastWasWhitespace = false;
                    for (int j = lastTagStart; j <= i; j++) {
                        boolean localIsWhitespace = Character.isWhitespace(c[j]);
                        if (!localIsWhitespace || (localIsWhitespace != localLastWasWhitespace)) {
                            result.append (c[j]);
                        }
                        localLastWasWhitespace = localIsWhitespace;
                    }
                    lastTagStart = i;
                    inTag = false;
                    lastWasWhitespace = false;
                } else if (c[i] == '>') {
                    inTag = false;
                    lastTagStart = -1;
                }
            }
            if (!inTag && wasInTag == inTag) {
                boolean isWhitespace = Character.isWhitespace(c[i]);
                if (!isWhitespace || (isWhitespace != lastWasWhitespace)) {
                    result.append(c[i]);
                }
                lastWasWhitespace = isWhitespace;
            }
        }
        if (inTag && lastTagStart != -1) {
            result.append (new String (c, lastTagStart, c.length - 
                    lastTagStart));
        }
        return result.toString();
    }
}