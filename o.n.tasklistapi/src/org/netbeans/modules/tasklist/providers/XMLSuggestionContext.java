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

package org.netbeans.modules.tasklist.providers;

import org.openide.filesystems.FileObject;
import org.openide.ErrorManager;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedInputStream;

/**
 * Dedicated XML document context that is faster than generic one.
 *
 * @author Petr Kuzel
 */
final class XMLSuggestionContext {

    static  String getContent(FileObject fo) {
        try {
            char[] buf = new char[1024*64];
            StringBuffer sb = new StringBuffer();
            String encoding = XMLEncodingHelper.detectEncoding(new BufferedInputStream(fo.getInputStream(), 2157));
            if (encoding == null) return null;
            Reader r = new InputStreamReader(new BufferedInputStream(fo.getInputStream()), encoding);
            int len = -1;
            while (true) {
                len = r.read(buf);
                if (len == -1) break;
                sb.append(buf, 0, len);
            }
            return sb.toString();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        return null;
    }

}
