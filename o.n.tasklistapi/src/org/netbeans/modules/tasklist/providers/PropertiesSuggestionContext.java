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

package org.netbeans.modules.tasklist.providers;

import org.openide.filesystems.FileObject;
import org.openide.ErrorManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedInputStream;

/**
 * Dedicated java source context that is faster than generic one.
 *
 * @author Petr Kuzel
 */
final class PropertiesSuggestionContext {

    static String getContent(FileObject fo) {
        try {
            char[] buf = new char[1024*64];
            StringBuffer sb = new StringBuffer();
            Reader r = new InputStreamReader(new BufferedInputStream(fo.getInputStream()), "ISO8859-1");  // NOI18N
            int len;
            try {
                while (true) {
                    len = r.read(buf);
                    if (len == -1) break;
                    sb.append(buf, 0, len);
                }
                return sb.toString();
            } finally {
                r.close();
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        return null;
    }


}
