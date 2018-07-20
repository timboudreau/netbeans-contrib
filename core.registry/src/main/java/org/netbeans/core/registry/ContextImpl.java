/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.core.registry;

import org.netbeans.api.registry.*;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.*;

public class ContextImpl implements BasicContext{    
    /** Folder of the context */
    private final FileObject folder;
    
    /** Event listener support */
    private final EventListenerList listeners = new EventListenerList ();
    
    private static final String DEFAULT_SORTING = "default.context.sorting";
    public static final String PRIMITIVE_BINDING_ATTR_PREFIX = "BINDINGATTR:";
    
    

    /** Class wrapping bindings in this context.
     */
    private ContextBindings objectBindings;

    /** Reference to root context of hierarchy to which this context belongs.
     */
    private final ContextImpl rootContext;

    private final ContextCache contextCache;
    /** FileChange listener registered on the Filesystem. It notifies
     * root context and all its subcontexts about changes.
     */
    private FileChangeListener listener;
    
    
    public ContextImpl (FileObject folder) {
        this (folder, null);        
    }

    ContextImpl(FileObject folder, ContextImpl rootContext) {
        this.folder = folder;
        if (rootContext != null) {
            this.rootContext = rootContext;
            contextCache = null;
        } else {
            this.rootContext = this;
            contextCache = new ContextCache();
            initialize();
        }
        // this method must be overriden on root context and cache all
        // its descendant contexts
        getContextCache().cacheContext(folder, this);
    }

    // install listeners
    private void initialize() {
        listener = new FileSystemListener(this);
        try {
            getFolder().getFileSystem().addFileChangeListener(FileUtil.weakFileChangeListener(
                    listener, getFolder()));
        } catch (FileStateInvalidException ex) {
            RuntimeException e = new RuntimeException(ex);
            throw e;
        }

        // register this context for events about module/convertor enable/disable
        StateUpdater.getDefault().registerRootContext(this);
    }
            
    
    public synchronized ContextBindings getContextBindings() {
        if (objectBindings == null) {
            objectBindings = ContextBindings.createContextBindings(folder, this);
        }
        return objectBindings;
    }
    
    public FileObject getFolder() {
        return folder;
    }

    ContextImpl getCtx(FileObject fo) {
        // this method must be overriden on root context and must
        // retrieve context if it already exists
        ContextImpl ctx = getContextCache().retrieveContextFromCache(fo);
        if (ctx == null) {
            if (!fo.isFolder()) {
                throw new RuntimeException("Cannot create context for fileobject "+fo+". It must be folder.");
            }
            ctx = new ContextImpl(fo, getRootContextImpl());
        }
        return ctx;
    }
    
    
    private boolean isRoot() {
        return getRootContextImpl() == this;
    }

    public BasicContext getRootContext() {
        return getRootContextImpl();
    }
    
    public String getContextName() {
        if (isRoot()) {
            return "/"; // NOI18N
        }
        return folder.getName();

    }
    
    public BasicContext getParentContext() {
        if (isRoot()) {
            return null;
        }
        FileObject parent = folder.getParent();
        if (parent != null) {
            return getCtx(parent);
        } else {
            throw new RuntimeException("Cannot happen! File bug against openide/settings!!"); //NOI18N
        }
    }
    
    public BasicContext getSubcontext(String subcontextName) {
        FileObject targetFolder = folder.getFileObject(subcontextName);
        if (targetFolder == null || !targetFolder.isFolder()) {
            return null;
        }
        return getCtx(targetFolder);
    }
    
    public BasicContext createSubcontext(final String subcontextName) throws ContextException {
        if (!isValidName(subcontextName)) {
            throw SpiUtils.createContextException(this, "Cannot create subcontext with name '"+subcontextName+"'. It is invalid name."); //NOI18N
        }
        FileObject fo = folder.getFileObject(subcontextName);
        if (fo != null && fo.isFolder ()) {
            throw SpiUtils.createContextException(this, "Subcontext '"+subcontextName+"' already exist"); //NOI18N
        }

        try {
            fo = folder.createFolder(subcontextName);
        } catch (IOException ex) {
            ContextException ce = SpiUtils.createContextException(this, "Error on underlaying filesystem occured.");
            ce.initCause(ex);
            throw ce;
        }
        return getCtx(fo);
    }
    
    public void destroySubcontext(String subcontextName) throws ContextException {
        final FileObject fo = folder.getFileObject(subcontextName);
        if (fo == null || !fo.isFolder()) {
            throw SpiUtils.createContextException(this, "Subcontext '"+subcontextName+"' does not exist."); //NOI18N
        }

        try {
            fo.delete();
        } catch (IOException ex) {
            ContextException ce = SpiUtils.createContextException(this, "Error on underlaying filesystem occured.");
            ce.initCause(ex);
            throw ce;
        }
    }
    
    
    // operation is blocking now
    public void bindObject(final String name, final Object object) throws ContextException {
        
        // check name validity
        if (!isValidName(name)) {
            throw SpiUtils.createContextException(this, "Cannot bind object with name '"+name+"'. It is invalid binding name."); //NOI18N
        }

        getContextBindings().bindObject(name, object);
    }
    
    public Object lookupObject(String name) throws ContextException {        
        Object value = getContextBindings().lookupObject(name);
        return value;
    }
    
    public String getAttribute(String bindingName, String attributeName) throws ContextException {
        Object attr = null;
        if (bindingName == null) {
            attr = folder.getAttribute(attributeName);
        } else {
            FileObject fo = getContextBindings().getBindingFile(bindingName);
            if (fo == null) {
                attr = folder.getAttribute(PRIMITIVE_BINDING_ATTR_PREFIX + bindingName + '/' + attributeName);
            } else {
                attr = fo.getAttribute(attributeName);    
            }                        
        }
        
        if (attr != null) {
            if (attr instanceof String) {
                return (String)attr;
            } else if (attr instanceof Long || attr instanceof Boolean || 
                    attr instanceof Float || attr instanceof Integer) {
                return attr.toString();
            } else if (attr instanceof URL) {
                return ((URL)attr).toExternalForm();
            } else {
                throw SpiUtils.createContextException(this, "Type of attribute value is not not supported - "+attr.getClass().getName());
            }
        } else {
            return null;
        }
    }
    
    public void setAttribute(String bindingName, String attributeName, final String value) throws ContextException {
        if (bindingName != null && !isValidName(bindingName)) {
            throw SpiUtils.createContextException(this, "Cannot set attribute for binding with name '"+bindingName+"'. It is invalid name."); //NOI18N
        }
        if (!isValidName(attributeName)) {
            throw SpiUtils.createContextException(this, "Cannot set attribute with name '"+attributeName+"'. It is invalid name."); //NOI18N
        }
        FileObject fo;
        if (bindingName == null) {
            fo = folder;
        } else {
            if (!getContextBindings().existBinding(bindingName)) {
                throw SpiUtils.createContextException(this, "Cannot set attribute for non-existing binding (binding="+bindingName+" attribute="+attributeName+").");
            }
            // if the binding is primitive binding then its attribute have to
            // be attached to folder, but is prefixed with binding name.
            fo = getContextBindings().getBindingFile(bindingName);
            if (fo == null) {
                fo = folder;
                attributeName = PRIMITIVE_BINDING_ATTR_PREFIX + bindingName + '/' + attributeName;
            }
        }
        try {
            fo.setAttribute(attributeName, value);
        } catch (IOException ex) {
            ContextException ce = SpiUtils.createContextException(this, "Error on underlaying filesystem occured.");
            ce.initCause(ex);
            throw ce;
        }
    }
    
    public final synchronized void addContextListener(ContextListener listener) {
        listeners.add(ContextListener.class, listener);
    }
    
    public final synchronized void removeContextListener(ContextListener listener) {
        listeners.remove(ContextListener.class, listener);
    }
    
    void fireAttributeEvent(final AttributeEvent ae) {
        final Object[] l;
        
        synchronized (this) {
            if (listeners.getListenerCount() == 0) {
                return;
            }
            l = listeners.getListenerList();
        }
        // this method must be override on root context and must
        // return one mutex shared by all its descendant contexts
        Context.getMutex().readAccess(new Runnable() {
            public void run() {
                for (int i = l.length-2; i>=0; i-=2) {
                    ((ContextListener)l[i+1]).attributeChanged(ae);
                }
            }
        });
    }
    
    void fireBindingEvent(final BindingEvent be) {
        final Object[] l;

        synchronized (this) {
            if (listeners.getListenerCount() == 0) {
                return;
            }
            l = listeners.getListenerList();
        }
        // this method must be override on root context and must
        // return one mutex shared by all its descendant contexts
        Context.getMutex().readAccess(new Runnable() {
            public void run() {
                for (int i = l.length-2; i>=0; i-=2) {
                    ((ContextListener)l[i+1]).bindingChanged(be);
                }
            }
        });
    }
    
    void fireSubcontextEvent(final SubcontextEvent se) {
        final Object[] l;

        synchronized (this) {
            if (listeners.getListenerCount() == 0) {
                return;
            }
            l = listeners.getListenerList();
        }
        // this method must be override on root context and must
        // return one mutex shared by all its descendant contexts
        Context.getMutex().readAccess(new Runnable() {
            public void run() {
                for (int i = l.length-2; i>=0; i-=2) {
                    ((ContextListener)l[i+1]).subcontextChanged(se);
                }
            }
        });
    }
    
    
    
    private static boolean isValidName(String name) {
        if (name.length() == 0 || name.indexOf('/') != -1) {
            return false;
        }
        return true;
    }

    public String toString() {
        return super.toString() + "[ctx="+getContextName()+", folder="+folder+"]";
    }
    
    public java.util.Collection getAttributeNames(String bindingName) {
        ArrayList list = new ArrayList();
        if (bindingName == null) {
            addAttrs(list, folder, null, PRIMITIVE_BINDING_ATTR_PREFIX);
            if (!(list.contains(DEFAULT_SORTING))) {
                // #36156 - report DEFAULT_SORTING attribute always and
                // not only for non-empty contexts
                list.add(DEFAULT_SORTING);
            }
        } else {
            FileObject fo = getContextBindings().getBindingFile(bindingName);
            String attributePrefix = null;
            if (fo == null) {
                fo = folder;
                attributePrefix = PRIMITIVE_BINDING_ATTR_PREFIX + bindingName + '/';
            }
            if (fo != null) {
                addAttrs(list, fo, attributePrefix, null);
            }
        }
        return list;
    }
    
    private static void addAttrs(ArrayList list, FileObject fo, String prefix, String ignore) {
        Enumeration en = fo.getAttributes();
        while (en.hasMoreElements()) {
            String attrName = (String)en.nextElement();
            
            if (attrName.startsWith(ContextBindings.PRIMITIVE_BINDING_PREFIX)) {
                continue;
            }
            
            if (ignore != null && attrName.startsWith(ignore)) {
                continue;
            }

/*
                next condition was added as 
                workaround for #16761, and this fo.getAttribute call  
                brings performance problem and should be deleted.                
*/
            if (fo.getAttribute(attrName) == null) continue;
            
            if (prefix != null) {
                if (!(attrName.startsWith(prefix))) {
                    continue;
                } else {
                    attrName = attrName.substring(prefix.length());
                }
            }

            list.add(attrName);
        }
    }

    public java.util.Collection getBindingNames() {
        return new HashSet(getContextBindings().getNames());
    }
    
    public java.util.Collection getSubcontextNames() {
        FileObject[] children = folder.getChildren();
        ArrayList list = new ArrayList(children.length);
        for (int i=0; i<children.length; i++) {
            if (children[i].isFolder()) {
                list.add(children[i].getNameExt());
            }
        }
        return list;
    }


    ContextCache getContextCache() {
        return getRootContextImpl().contextCache;
    }


    ContextImpl getRootContextImpl() {
        return rootContext;
    }

    static final class ContextCache  {

        /** Cache of the contexts. This property is initialized in root context
         * and is just referenced from all descendant contexts.
         */
        WeakHashMap contextsCache;
    
    
        private ContextCache() {
        }
    
        // here are four methods defined on ContextImpl which 
        // must be properly override on rootcontext. all what ContextImpl
        // impl does is that it delagates calls to its rootContext.
    
        void cacheContext(FileObject folder, ContextImpl context) {
            getContextsCache().put(folder, new WeakReference(context));
        }
    
        public ContextImpl retrieveContextFromCache(FileObject folder) {
            WeakReference ref = (WeakReference)getContextsCache().get(folder);
            if (ref != null) {
                return (ContextImpl)ref.get();
            } else {
                return null;
            }
        }
        
        // iterate all existing contexts and give them chance to update list of 
        // available bindings after convertors list has changed
        void modulesChanged(Collection added, Collection removed) {
            Iterator it = getContextsCache().values().iterator();
            while (it.hasNext()) {
                WeakReference ref = (WeakReference)it.next();
                ContextImpl ctx = (ContextImpl)ref.get();
                if (ctx == null) {
                    continue;
                }
                ctx.getContextBindings().modulesChanged(added, removed);
            }
        }


        private synchronized WeakHashMap getContextsCache() {
            if (contextsCache == null) {
                contextsCache = new WeakHashMap();
            }
            return contextsCache;
        }    
    }
    
}
