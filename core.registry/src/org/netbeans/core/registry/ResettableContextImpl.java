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

import org.netbeans.core.startup.layers.SessionManager;
import org.netbeans.spi.registry.ResettableContext;
import org.netbeans.spi.registry.SpiUtils;
import org.netbeans.api.registry.ContextException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.ErrorManager;

import java.io.IOException;

public class ResettableContextImpl extends ContextImpl implements ResettableContext {

    private ContextBindings resettableContextBindings;
    
    private FileSystem defaults;
    private FileSystem customizations;
    
    public ResettableContextImpl () {
        this (Repository.getDefault().getDefaultFileSystem().getRoot());        
    }
    
    public ResettableContextImpl (FileObject folder) {
        this (folder, null, SessionManager.getDefault().getLayer(SessionManager.LAYER_INSTALL), SessionManager.getDefault().getLayer(SessionManager.LAYER_SESSION));
    }
    
    public ResettableContextImpl(FileObject folder, ContextImpl rootContext, FileSystem defaults, FileSystem customizations) {
        super(folder, rootContext);
        this.defaults = defaults;
        this.customizations = customizations;
    }
    
    public synchronized ContextBindings getContextBindings() {
        if (resettableContextBindings == null) {
            resettableContextBindings = ContextBindings.createResettableContextBindings(getFolder(), this, defaults, customizations);
        }
        return resettableContextBindings;
    }
    
    public ContextImpl getCtx(FileObject fo) {
        // this method must be overriden on root context and must
        // retrieve context if it already exists
        ContextImpl ctx = getRootContextImpl().getContextCache().retrieveContextFromCache(fo);
        if (ctx == null) {
            ctx = new ResettableContextImpl(fo, getRootContextImpl(), defaults, customizations);
        }
        return ctx;
    }

    /*ResettableContext.hasDefault */
    public boolean hasDefault(String bindingName) {
        if (bindingName != null) {
            return getContextBindings().hasDefault(bindingName);
        } else {
            FileSystem fs = defaults;
            FileObject defaults = fs.findResource(getFolder ().getPath());
            return (defaults != null);
        }
    }

    /*ResettableContext.isModified */    
    public boolean isModified(String bindingName) {
        if (!(this instanceof ResettableContext)) {
            // assert - should never happen!!
            // these methods can be called only from ResettableContextImpl
            return false;
        }
        if (bindingName != null) {
            if (getContextBindings().isModified(bindingName)) {
                return true;
            }
            return false;
        } else {
            FileSystem fs = customizations;
            FileObject custs = fs.findResource(getFolder ().getPath());
            return (custs != null);
        }
    }

    /*ResettableContext.revert */        
    public void revert(String bindingName) throws ContextException {
        if (!isModified(bindingName)) {
            return;
        }
        
        if (bindingName != null) {
            if (getContextBindings().isModified(bindingName)) {
                getContextBindings().revert(bindingName);
                return;
            }
        } else {
            FileSystem fs = customizations;
            FileObject custs = fs.findResource(getFolder ().getPath());
            if (custs != null) {
                deleteFolderContent(custs);
            }
        }
    }

    private void deleteFolderContent(FileObject fo) throws ContextException {
        try {
// #16761 - the following code does not properly clean attributes. 
// Use delete folder + create folder instead
//            FileObject fos[] = fo.getChildren();
//            for (int i=0; i<fos.length; i++) {
//                fos[i].delete();
//            }
//            Enumeration en = fo.getAttributes();
//            while (en.hasMoreElements()) {
//                String attr = (String)en.nextElement();
//                fo.setAttribute(attr, null);
//                Object oo = fo.getAttribute(attr);
//            }
            FileObject f = fo.getParent();
            String name = fo.getNameExt();
            fo.delete();
            f.createFolder(name);
        } catch (IOException ex) {
            ContextException ce = SpiUtils.createContextException(this, "Error on underlaying filesystem occured during the revert.");
            ErrorManager.getDefault().annotate(ce, ex);
            throw ce;
        }
    }

}
