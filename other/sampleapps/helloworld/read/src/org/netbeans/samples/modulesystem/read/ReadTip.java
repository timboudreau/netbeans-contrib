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
package org.netbeans.samples.modulesystem.read;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import org.netbeans.samples.modulesystem.sayhello.api.HelloProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

public class ReadTip implements HelloProvider {
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
