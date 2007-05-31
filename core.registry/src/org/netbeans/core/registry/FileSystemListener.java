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

import org.netbeans.api.registry.AttributeEvent;
import org.netbeans.api.registry.BindingEvent;
import org.netbeans.api.registry.ContextEvent;
import org.netbeans.api.registry.SubcontextEvent;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import java.util.Collection;
import java.util.Iterator;

class FileSystemListener implements FileChangeListener {
    
    private ContextImpl rootContext;
    

    public FileSystemListener(ContextImpl rootContext) {
        this.rootContext =rootContext;
    }
    
    public void fileAttributeChanged(org.openide.filesystems.FileAttributeEvent fe) {
        FileObject fo = fe.getFile();
        if (!acceptFileObject(rootContext.getFolder(), fo)) {
            return;
        }
        String attrName = fe.getName();
        if (attrName != null && attrName.startsWith(ContextBindings.PRIMITIVE_BINDING_PREFIX)) {
            BindingEvent be = null;
            BasicContext ctx1 = rootContext.getCtx(fo);
            String bindingName = attrName.substring(ContextBindings.PRIMITIVE_BINDING_PREFIX.length());
            if (fe.getNewValue() == null) {
                be = SpiUtils.createBindingEvent(ctx1, bindingName, BindingEvent.BINDING_REMOVED);
            } else if (fe.getOldValue() == null) {
                be = SpiUtils.createBindingEvent(ctx1, bindingName, BindingEvent.BINDING_ADDED);
            } else {
                be = SpiUtils.createBindingEvent(ctx1, bindingName, BindingEvent.BINDING_MODIFIED);
            }
            fireContextEvent(fo, be);
            return;
        }
        
        BasicContext ctx;
        String bindingName = null;
        if (!fo.isFolder()) {
            bindingName = fo.getName();
            ctx = rootContext.getCtx(fo.getParent());
        } else {
            // #34156 - attribute name can be null
            if (attrName != null && attrName.startsWith(ContextImpl.PRIMITIVE_BINDING_ATTR_PREFIX)) {
               int index = attrName.indexOf('/');
               if (index != -1) {
                   bindingName = attrName.substring(ContextImpl.PRIMITIVE_BINDING_ATTR_PREFIX.length(), index);
                   attrName = attrName.substring(index+1);
               }
            }
            ctx = rootContext.getCtx(fo);
        }
        Object v = fe.getNewValue();
        AttributeEvent ae = null;
        if (fe.getNewValue() == null) {
            ae = SpiUtils.createAttributeEvent(ctx, bindingName, attrName, AttributeEvent.ATTRIBUTE_REMOVED);
        } else if (fe.getOldValue() == null) {
            ae = SpiUtils.createAttributeEvent(ctx, bindingName, attrName, AttributeEvent.ATTRIBUTE_ADDED);
        } else {
            ae = SpiUtils.createAttributeEvent(ctx, bindingName, attrName, AttributeEvent.ATTRIBUTE_MODIFIED);
        }
        fireContextEvent(fo, ae);
    }
    
    public void fileChanged(org.openide.filesystems.FileEvent fe) {
        FileObject fo = fe.getFile();
        if (!acceptFileObject(rootContext.getFolder(), fo)) {
            return;
        }
        boolean contextExisted = (rootContext.getContextCache().retrieveContextFromCache(fo.getParent()) != null);
        ContextImpl ctx = rootContext.getCtx(fo.getParent());
        if(fe.firedFrom(ContextBindings.referenceAction)) {
            if (ctx.getContextBindings().isBindingFile(fo)) {
                fireContextEvent(fo, SpiUtils.createBindingEvent(ctx, fo.getName(), BindingEvent.BINDING_MODIFIED));
            }
        } else {
            // change from outside of core/registry:
            if (ctx.getContextBindings().isRelevantChange(fo, false)) {
                fireContextEvent(fo, SpiUtils.createBindingEvent(ctx, fo.getName(), BindingEvent.BINDING_MODIFIED));
            }
        }
    }
    
    public void fileDataCreated(org.openide.filesystems.FileEvent fe) {
        FileObject fo = fe.getFile();
        if (!acceptFileObject(rootContext.getFolder(), fo)) {
            return;
        }
        boolean contextExisted = (rootContext.getContextCache().retrieveContextFromCache(fo.getParent()) != null);
        ContextImpl ctx = rootContext.getCtx(fo.getParent());
        if (fo.isFolder()) {
            fireContextEvent(fo, SpiUtils.createSubcontextEvent(ctx, fo.getName(), SubcontextEvent.SUBCONTEXT_ADDED));
        } else {
            if(fe.firedFrom(ContextBindings.referenceAction)) {
                if (ctx.getContextBindings().isBindingFile(fo)) {
                    fireContextEvent(fo, SpiUtils.createBindingEvent(ctx, fo.getName(), BindingEvent.BINDING_ADDED));
                }
            } else {
                // change from outside of core/registry:
                if (ctx.getContextBindings().isRelevantChange(fo, false)) {
                    fireContextEvent(fo, SpiUtils.createBindingEvent(ctx, fo.getName(), BindingEvent.BINDING_ADDED));
                }
            }
        }
    }
    
    public void fileDeleted(org.openide.filesystems.FileEvent fe) {
        FileObject fo = fe.getFile();
        if (!acceptFileObject(rootContext.getFolder(), fo)) {
            return;
        }
        boolean contextExisted = (rootContext.getContextCache().retrieveContextFromCache(fo.getParent()) != null);
        ContextImpl ctx = rootContext.getCtx(fo.getParent());
        if (fo.isFolder()) {
            // folder fo was deleted, so start firing events from its parent
            fireContextEvent(fo.getParent(), SpiUtils.createSubcontextEvent(ctx, fo.getName(), SubcontextEvent.SUBCONTEXT_REMOVED));
        } else {
            if(fe.firedFrom(ContextBindings.referenceAction)) {
                if (isBindingFile(fo)) {
                    fireContextEvent(fo, SpiUtils.createBindingEvent(ctx, fo.getName(), BindingEvent.BINDING_REMOVED));
                }
            } else {
                // change from outside of core/registry:
                if (ctx.getContextBindings().isRelevantChange(fo, true)) {
                    fireContextEvent(fo, SpiUtils.createBindingEvent(ctx, fo.getName(), BindingEvent.BINDING_REMOVED));
                }
            }
        }
    }
    
    public static boolean isBindingFile(FileObject fo) {
        String fileExt = fo.getExt();
        Collection exts = ObjectBinding.getFileExtensions();
        for (Iterator iterator = exts.iterator(); iterator.hasNext();) {
            String ext = (String) iterator.next();
            if (fileExt.equals(ext)) return true;
        }        
        return false;
    }
    
    public void fileFolderCreated(org.openide.filesystems.FileEvent fe) {
        FileObject fo = fe.getFile();
        if (!acceptFileObject(rootContext.getFolder(), fo)) {
            return;
        }
        BasicContext ctx = rootContext.getCtx(fo.getParent());
        fireContextEvent(fo, SpiUtils.createSubcontextEvent(ctx, fo.getName(), SubcontextEvent.SUBCONTEXT_ADDED));
    }
    
    public void fileRenamed(org.openide.filesystems.FileRenameEvent fe) {
        //TODO: does it need to be handled?
    }

    private void fireContextEvent(FileObject fo, ContextEvent ce) {
        fireContextEvent(rootContext, fo, ce);
    }
    
    private static boolean acceptFileObject(FileObject root, FileObject child) {
        return (FileUtil.isParentOf(root, child) || root.equals(child));
    }
    
    public static void fireContextEvent(ContextImpl rootContext, FileObject fo, ContextEvent ce) {
        // go throught the hierarchy and check whether the context exist for
        // the fileobject. if yes then fire events on the context
        while (fo != null && acceptFileObject(rootContext.getFolder(), fo)) {
            ContextImpl ctx = rootContext.getContextCache().retrieveContextFromCache(fo);
            if (ctx != null) {
                if (ce instanceof AttributeEvent) {
                    ctx.fireAttributeEvent((AttributeEvent)ce);
                } else if (ce instanceof BindingEvent) {
                    ctx.fireBindingEvent((BindingEvent)ce);
                } else if (ce instanceof SubcontextEvent) {
                    ctx.fireSubcontextEvent((SubcontextEvent)ce);
                }
            }
            fo = fo.getParent();
        }
    }

    
}
    
