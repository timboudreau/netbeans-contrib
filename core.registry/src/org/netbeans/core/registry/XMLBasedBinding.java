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

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.cookies.InstanceCookie;
import org.netbeans.spi.registry.BasicContext;

import java.io.IOException;

/**
 * Provides ObjectBinding.Reader for ObjectBinding implementation.
 *
 * Backward compatibility for XML based instances. Impl. based on
 * datasystems.
 */

final class XMLBasedBinding extends ObjectBinding.Reader {
    static final ObjectBinding.Reader READER = new XMLBasedBinding ();
    
    private XMLBasedBinding () {}
    
    boolean canRead(FileObject fo) {
        boolean retVal = (fo.getExt().equals(getFileExtension()));

        if (retVal) {
            try {
                InstanceCookie ic = getInstanceCookie(fo);
                retVal = (ic != null);
            } catch (DataObjectNotFoundException e) {
                retVal = false;
            }
        }
        return retVal;
    }

    private static InstanceCookie getInstanceCookie(FileObject fo) throws DataObjectNotFoundException {
        DataObject d = DataObject.find(fo);
        return (InstanceCookie)d.getCookie(InstanceCookie.class);
    }

    String getFileExtension() {
        return "xml";//NOI18N
    }

    ObjectBinding getObjectBinding(BasicContext ctx, FileObject fo) {
        return new ObjectBindingImpl (fo);
    }
    
    private class ObjectBindingImpl extends ObjectBinding {
        InstanceCookie ic;
        ObjectBindingImpl(FileObject fo) {
            super(fo);
        }

        public Object createInstance() throws IOException {
            InstanceCookie icLocal = (ic != null) ? ic : getInstanceCookie(getFile());
            try {
                return icLocal.instanceCreate();
            } catch (ClassNotFoundException e) {
                throw new IOException(e.getLocalizedMessage());   
            }
        }

        public boolean isEnabled() {            
            return (getFile() != null && getFile().isValid());
        }
    }
}
