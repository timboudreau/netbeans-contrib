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

import java.io.IOException;

/**
 * Dedicated java source context that is faster than generic one.
 *
 * @author Petr Kuzel
 */
final class JavaSuggestionContext {

    static  String getContent(FileObject fo) {
        String encoding = org.netbeans.modules.java.Util.getFileEncoding(fo);
        char[] source = null;
        try {
            source = org.netbeans.modules.java.Util.getContent(fo, false, true, encoding);
            return new String(source);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        return null;
    }
}
