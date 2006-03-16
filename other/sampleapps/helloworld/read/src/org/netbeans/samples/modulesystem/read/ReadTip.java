/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.samples.modulesystem.read;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import org.netbeans.samples.modulesystem.sayhello.api.HelloTip;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

public class ReadTip implements HelloTip {
    public String giveMeATip() {
        try {
            FileObject root = Repository.getDefault().getDefaultFileSystem().getRoot();
            FileObject hello = FileUtil.createFolder(root, "HelloWorld");
            hello.refresh();
            FileObject[] arr = hello.getChildren();
            if (arr.length == 0) {
                return "Cannot read what to say";
            }
            Collections.shuffle(Arrays.asList(arr));
            return read(arr[0].getInputStream());
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    private static String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        for(;;) {
            int ch = is.read();
            if (ch == -1) return sb.toString();
            sb.append((char)ch);
        }
    }
}
