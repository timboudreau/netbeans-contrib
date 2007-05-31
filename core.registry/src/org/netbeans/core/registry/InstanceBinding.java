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

package org.netbeans.core.registry;

import org.netbeans.core.registry.oldformats.InstanceUtils;
import org.netbeans.spi.registry.BasicContext;
import org.openide.filesystems.FileObject;

import java.io.IOException;


/**
 * Provides ObjectBinding.Reader for ObjectBinding implementation.
 * (copy & pasted & refactored from original org.netbeans.core.registry.ObjectBinding)
 */
final class InstanceBinding {
    private static final String INSTANCE_EXTENSION = "instance";//NOI18N
    static final ObjectBinding.Reader READER = new ObjectBinding.Reader() {
        boolean canRead(FileObject fo) {
            return fo.getExt().equalsIgnoreCase(INSTANCE_EXTENSION);
        }

        String getFileExtension() {
            return INSTANCE_EXTENSION;
        }

        ObjectBinding getObjectBinding(BasicContext ctx, FileObject fo) {
            return new ObjectBindingImpl(fo);
        }
    };

    private InstanceBinding() {
    }

    private static class ObjectBindingImpl extends ObjectBinding {
        protected ObjectBindingImpl(FileObject fo) {
            super(fo);
        }

        public Object createInstance() throws IOException {
            try {
                return read(getFile());
            } catch (ClassNotFoundException e) {
                throw new IOException(e.getLocalizedMessage());
            } catch (UnsupportedOperationException e) {
                throw new IOException(e.getLocalizedMessage());
            }
        }

        public boolean isEnabled() {
            return (getFile() != null && getFile().isValid());
        }
    }

    private static Object read(FileObject fo)
            throws ClassNotFoundException, UnsupportedOperationException {
        Object o = fo.getAttribute("instanceCreate");
        if (o != null) {
            return o;
        }

        o = fo.getAttribute("instanceClass");
        if (o != null) {
            if (o instanceof String) {
                return InstanceUtils.newValue((String) o);
            } else {
                return o;
            }
        }

        String s = fo.getName();
        if (s.indexOf('[') >= 0) {
            s = s.substring(s.indexOf('[')+1, s.indexOf(']'));
        }
        s = s.replace('-', '.');
        return InstanceUtils.newValue(s);
    }

}
