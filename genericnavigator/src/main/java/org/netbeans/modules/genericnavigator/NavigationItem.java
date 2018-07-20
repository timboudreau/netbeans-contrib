/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s): */
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