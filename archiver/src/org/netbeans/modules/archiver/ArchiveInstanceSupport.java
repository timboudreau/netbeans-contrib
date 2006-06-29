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

package org.netbeans.modules.archiver;

import java.beans.*;
import java.io.*;
import org.netbeans.api.java.classpath.*;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;

public class ArchiveInstanceSupport implements InstanceCookie {

    private final FileObject fo;

    public ArchiveInstanceSupport(FileObject fo) {
        this.fo = fo;
    }

    public Object instanceCreate() throws IOException, ClassNotFoundException {
        InputStream is = fo.getInputStream();
        ClassLoader origL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(ClassPath.getClassPath(fo, ClassPath.EXECUTE).getClassLoader(true));
        try {
            final Exception[] x = new Exception[1];
            ExceptionListener l = new ExceptionListener() {
                public void exceptionThrown(Exception e) {
                    if (x[0] == null) {
                        x[0] = e;
                    }
                }
            };
            XMLDecoder dec = new XMLDecoder(new BufferedInputStream(is), null, l);
            Object o = dec.readObject();
            if (x[0] != null) {
                throw new IOException(x[0].toString());
            }
            return o;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException(e.toString());
        } finally {
            Thread.currentThread().setContextClassLoader(origL);
            is.close();
        }
    }
    
    public Class instanceClass() throws IOException, ClassNotFoundException {
        return instanceCreate().getClass();
    }
    
    public String instanceName() {
        try {
            return instanceClass().getName();
        } catch (Exception e) {
            return null;
        }
    }
    
}
