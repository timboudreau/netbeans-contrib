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

import org.netbeans.api.registry.BindingEvent;
import org.netbeans.api.registry.ContextException;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;

import java.io.IOException;
import java.util.*;

/**
 * This class manages bindings in the context. All non-primitive and non-simple
 * datas are wrapped into ObjectBinding class.
 */
class ContextBindings {

    // folder of the context
    private FileObject folder;
    
    // context of this ContextBindings
    private BasicContext ctx;
    
    // folders and their FSs.
    // this impl supports resettability if both FSs are provided.
    private FileObject defFO;
    private FileObject custFO;
    private FileSystem defFS;
    private FileSystem custFS;

    // used to distinguish _binding_ changes done in this class and outside of the core/registry    
    static FileSystem.AtomicAction referenceAction = new RegistryAtomicAction () {        
        public void run() throws IOException {
        }
    };    
    
    public static final String PRIMITIVE_BINDING_PREFIX = "BINDING:";

    // map of ObjectBinding instances
    private Map bindings = new HashMap();

    private ContextBindings(FileObject folder, BasicContext ctx, FileSystem defFS, FileSystem custFS) {
        this.folder = folder;
        this.ctx = ctx;
        this.defFS = defFS;
        this.custFS= custFS;
        
        FileObject[] children = this.folder.getChildren();
        for (int i=0; i<children.length; i++) {             
            ObjectBinding ob = ObjectBinding.get(this.ctx, children[i]);
            if (ob != null) {
                bindings.put(ob.getBindingName(),ob);
            }
        }
    }
    
    public static ContextBindings createContextBindings(FileObject folder, BasicContext ctx) {
        return new ContextBindings(folder, ctx, null, null);
    }
    
    public static ContextBindings createResettableContextBindings(FileObject folder, BasicContext ctx, FileSystem defaults, FileSystem customizations) {
        return new ContextBindings(folder, ctx, defaults, customizations);
    }

    private synchronized FileObject getDefFolder() {
        if (defFO != null && defFO.isValid()) {
            return defFO;
        }
        defFO = (defFS != null) ? defFS.findResource(folder.getPath()) : null;
        return defFO;
    }
    
    private synchronized FileObject getCustFolder() {
        if (custFO != null && custFO.isValid()) {
            return custFO;
        }
        custFO = custFS.findResource(folder.getPath());
        return custFO;
    }

    // go through all bindings and do their refresh according to added/removed convertors
    void modulesChanged(Collection added, Collection removed) {
        assert added != null;
        assert removed != null;
        Iterator it = added.iterator();
        while (it.hasNext()) {
            processBindingChange(BindingEvent.BINDING_ADDED, it.next());
        }
        it = removed.iterator();
        while (it.hasNext()) {
            processBindingChange(BindingEvent.BINDING_REMOVED, it.next());
        }
    }
    
    private void processBindingChange(int eventType, Object moduleDescriptor) {
        Iterator it = bindings.values().iterator();
        while (it.hasNext()) {
            ObjectBinding ob = (ObjectBinding)it.next();
            if (ob.getModuleDescriptor().equals(moduleDescriptor)) {
                fireBindingEvent(ob.getBindingName(), eventType);
            }
        }
    }

    
    private void fireBindingEvent(String bindingName, int eventType) {
        BindingEvent be = SpiUtils.createBindingEvent(ctx, bindingName, eventType);
        // fires this change on all relevant contexts
        FileSystemListener.fireContextEvent(((ContextImpl)ctx).getRootContextImpl(), folder, be);                       
    }
    
    
    /** Helper method for FileSystemListener to check whether the file
     * which was added/modified or removed in core/external operation
     * is relevant to this context and must be fired as binding change.
     *
     * This method must not only properly answer, but also updates its
     * internal binding cache.
     */
    boolean isRelevantChange(FileObject fo, boolean removed) {
        if (!removed) {
            ObjectBinding ob = findObjectBinding(fo.getName());
            if (ob != null) {
                return true;
            } else {
                ob = ObjectBinding.get(ctx, fo);
                if (ob != null) {
                    bindings.put(ob.getBindingName(), ob); 
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            ObjectBinding ob = findObjectBinding(fo.getName());
            if (ob != null) {
                bindings.remove(fo.getName());
                return true;
            } else {
                return false;
            }
        }
    }

    
    // couple of methods for managing of primitive and simple data types:


/*
    The process of binding removal might include also removal of its attributes
    and so it must be done in atomic action to group all events. Otherwise removal
    of binding's attribute can notify client's listener which in turn can execute its code
    in the middle of binding removal.
        
    also lastAtomicAction variable is set to this atomic action so that
    FS listener can distinguish _binding_ changes done in core/registry and outside
*/    
    public void bindObject(final String bindingName, final Object object) throws ContextException {        
        try {
            folder.getFileSystem().runAtomicAction(new RegistryAtomicAction () {
                public void run() throws IOException {
                    doBindObject(bindingName, object);
                }
            });
        } catch (IOException ex) {
            ContextException ce = SpiUtils.createContextException(ctx, "Error on underlaying filesystem occured.");
            ce.initCause(ex);
            throw ce;
        } 
    }

    private void doBindObject(String bindingName, Object object) throws IOException {
        if (object == null) {            
            ObjectBinding[] ob = new ObjectBinding[] {findObjectBinding(bindingName), PrimitiveBinding.get(folder, bindingName)};
            for (int i = 0; i < ob.length; i++) {
                ObjectBinding objectBinding = ob[i];
                if (objectBinding != null ) {
                    objectBinding.delete();                                        
                }
            }
            bindings.remove(bindingName);            
        } else {
            boolean isPrimitive = PrimitiveBinding.isPrimitive(object);
            ObjectBinding toDelete = isPrimitive ? findObjectBinding(bindingName) : PrimitiveBinding.get(folder, bindingName);
            if (toDelete != null) toDelete.delete();
            bindings.remove(bindingName);
            
            ObjectBinding toCreate = PrimitiveBinding.create(folder, bindingName, object);
            if (toCreate == null) {
                toCreate = ObjectBinding.write (ctx, folder, bindingName, object);
                bindings.put(bindingName, toCreate);
            }                        
        }
    }
    
    public Object lookupObject(String bindingName) throws ContextException {        
        Object o = getBindingFromCache(bindingName);
        if (o != null) return o;
        
        try {
            ObjectBinding[] ob = new ObjectBinding[] {findObjectBinding(bindingName), PrimitiveBinding.get(folder, bindingName)};
            for (int i = 0; i < ob.length; i++) {
                ObjectBinding objectBinding = ob[i];
                if (objectBinding != null && objectBinding.isEnabled() ) {
                    o = objectBinding.getObject();                                        
                }
            }
            
            return o;
        } catch (IOException e) {
            ContextException ce = SpiUtils.createContextException(ctx, e.getLocalizedMessage());
            ce.initCause(e);
            throw ce;
        } 
    }

    private Object getBindingFromCache(String bindingName) throws ContextException {
        Object o = null;
        ObjectBinding ob1 = (ObjectBinding)bindings.get(bindingName);
        if (ob1 != null && ob1.isEnabled()) {
            try {
                o = ob1.getObject();
            } catch (IOException e) {
                ContextException ce = SpiUtils.createContextException(ctx,e.getLocalizedMessage());
                ce.initCause(e);
                throw ce;
            }
        }
        return o;
    }
    
    private ObjectBinding findObjectBinding(String bindingName) {
        Iterator it = bindings.values().iterator();
        while (it.hasNext()) {
            ObjectBinding ob = (ObjectBinding)it.next();
            if (ob.getBindingName().equals(bindingName)) {
                return ob;
            }
        }
        return null;
    }

    public Collection getNames() {
        Set retVal = new HashSet();
        
        Set obMerged = new HashSet ();
        obMerged.addAll(bindings.values());
        obMerged.addAll (PrimitiveBinding.getAll(folder));
        
        for (Iterator iterator = obMerged.iterator(); iterator.hasNext();) {
            ObjectBinding ob = (ObjectBinding) iterator.next();
            if (ob != null && ob.isEnabled()) retVal.add(ob.getBindingName());             
        }
        
        return retVal;
    }
    
    public boolean hasDefault(String bindingName) {
        assert bindingName != null;
        boolean retVal = false;
        
        FileObject defF = getDefFolder();
        if (defF != null) {
            retVal = (findBindingFile(defF, bindingName) != null || PrimitiveBinding.get(defF, bindingName) != null);            
        }
        
        return retVal;
    }

    private static FileObject findBindingFile(FileObject folder, String bindingName) {
        FileObject fo = null;
        Collection exts = ObjectBinding.getFileExtensions();
        for (Iterator iterator = exts.iterator(); iterator.hasNext();) {
            String ext = (String) iterator.next();
            fo = folder.getFileObject(bindingName, ext);
            if (fo != null) break;
        }
        return fo;
    }
  
    
    public boolean isModified(String bindingName) {
        assert bindingName != null;
        if (custFS == null) {
            return false;
        }
        FileObject custF = getCustFolder();
        if (custF == null) {           
            // MultiFilesystem stores attributes of folders which do not exist
            // on writable layer yet on root file object of the writable filesystem
            // in the format of "folder\folder\attribute". Check that here:
            return PrimitiveBinding.isCustomizedAndAttachedToRoot(custFS, folder, bindingName) ? true : false;
        }
        
        return (findBindingFile(custF, bindingName) != null || PrimitiveBinding.get(custF, bindingName) != null);
    }
    
    public void revert(final String bindingName) throws ContextException {
        // execute revert in atomic action to group all events.
        try {
            folder.getFileSystem().runAtomicAction(new RegistryAtomicAction () {
                public void run() throws IOException {
                    doRevert(bindingName);
                }
            });
        } catch (IOException ex) {
            ContextException ce = SpiUtils.createContextException(ctx, "Error on underlaying filesystem occured.");
            ce.initCause(ex);
            throw ce;
        } 
    }
    
    private void doRevert(String bindingName) throws IOException {
        assert bindingName != null;
        if (isModified(bindingName)) {
            FileObject custF = getCustFolder();
            if (custF != null) {             
                ObjectBinding ob = PrimitiveBinding.get(custF, bindingName);
                if (ob == null) ob  = ObjectBinding.get(ctx, findBindingFile(custF, bindingName));
                if (ob != null) ob.delete();
                bindings.remove(bindingName);            
                removeMaskFile(custF, bindingName);
                
                // Binding from customization layer was removed. Check
                // binding on the default layer. If it is binding file create ObjectBinding info            
                ob = ObjectBinding.get(ctx, findBindingFile(folder, bindingName));
                if (ob != null) bindings.put(ob.getBindingName(), ob);
            } else {
                //special case
                // MultiFilesystem stores attributes of folders which do not exist
                // on writable layer yet on root file object of the writable filesystem
                // in the format of "folder\folder\attribute". Check that here:                        
                PrimitiveBinding.deleteIfCustomizedAndAttachedToRoot(custFS, folder, bindingName);                
            }
        }
    }

    private static void removeMaskFile(FileObject folder, String name) throws IOException {
        Enumeration en = folder.getChildren(false);
        while (en.hasMoreElements()) {
            FileObject fo = (FileObject)en.nextElement();
            if (fo.getName().equals(name) && fo.getExt().endsWith("_hidden")) {
                fo.delete();
                break;
            }
        }
    }
    
    public boolean existBinding(String bindingName) {
        return (findObjectBinding(bindingName) != null || PrimitiveBinding.get(folder, bindingName) != null); 
    }

    
    public FileObject getBindingFile(String bindingName) {
        ObjectBinding ob = findObjectBinding(bindingName);
        if (ob == null) {
            return null;
        }
        if (!ob.isEnabled()) {
            return null;
        }
        return ob.getFile();
    }
    
    public boolean isBindingFile(FileObject fo) {
        Iterator it = bindings.values().iterator();
        while (it.hasNext()) {
            ObjectBinding ob = (ObjectBinding)it.next();
            if (fo.equals(ob.getFile())) {
                return true;
            }
        }
        return false;
    }
    private static abstract class RegistryAtomicAction implements FileSystem.AtomicAction {
        public boolean equals(Object obj) {
            return (obj instanceof RegistryAtomicAction);
        }

        public int hashCode() {
            return RegistryAtomicAction.class.hashCode();
        }
    }    
}
