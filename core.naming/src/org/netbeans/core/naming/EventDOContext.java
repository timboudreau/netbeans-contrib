/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.core.naming;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.naming.event.EventContext;
import javax.naming.event.NamingListener;
import javax.naming.Name;
import javax.naming.NamingException;

import org.openide.ErrorManager;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;
import org.openide.loaders.OperationEvent;
import org.openide.loaders.OperationListener;
import org.openide.util.Lookup;
import org.openide.util.WeakListener;


/**
 * <code>EventContext</code> over system filesystem.
 *
 * @author Peter Zavadsky
 */
class EventDOContext extends DOContext implements EventContext {
    
    private static final String LISTENER = "listener"; // NOI18N
    private static final String FILESYSTEM_WEAK_LISTENER = "filesystemWeakListener"; // NOI18N
    private static final String OPERATION_WEAK_LISTENER = "operationWeakListener"; // NOI18N

    /** Support for event context listeners. */
    private final EventDOContextSupport support = new EventDOContextSupport();

    
    /** Creates a new instance of EventDOContext */
    EventDOContext(Hashtable env, FileObject folder) throws NamingException {
        super(env, folder);
        init();
    }

    /** Initializes the context, in case this is the "root" context
     * registers filesystem listener on underlying filesystem. */
    private void init() throws NamingException {
        // If this is the "root" context start to listen on the ungerlying
        // filesystem.
        if(folder == env.get(Jndi.ROOT_OBJECT)) {
            try {
                FSListener l = new FSListener(env, folder);

                FileSystem fs = folder.getFileSystem();
                FileChangeListener fsL = WeakListener.fileChange(l, fs);
                fs.addFileChangeListener(fsL);
                addToEnvironment(FILESYSTEM_WEAK_LISTENER, fsL);
                
                OperationListener opL;
                DataLoaderPool pool = (DataLoaderPool)Lookup.getDefault()
                        .lookup(DataLoaderPool.class);
                if(pool != null) {
                    opL = (OperationListener)WeakListener.create (OperationListener.class, l, pool);
                    pool.addOperationListener(opL);
                    addToEnvironment(OPERATION_WEAK_LISTENER, opL);
                } else {
                    ErrorManager.getDefault().notify(new NullPointerException(
                            "There is no data loader pool (from lookup)!")); //NOI18N
                }

                addToEnvironment(LISTENER, l);
            } catch(FileStateInvalidException fsie) {
                NamingException nex = new NamingException(fsie.getMessage());
                nex.setRootCause(fsie);
                throw nex;
            }
        }
    }

    /** Overrides superclass method. Adds removing of filesystem listener. */    
    public void close() throws NamingException {
        // If this is the "root" context, remove listening.
        if(folder == env.get(Jndi.ROOT_OBJECT)) {
            OperationListener opL = (OperationListener)env.get(OPERATION_WEAK_LISTENER);
            if(opL == null) {
                ErrorManager.getDefault().notify(new NullPointerException(
                        "Filesystem listener is null when closing root" // NOI18N
                        + " event context " + this)); // NOI18BN
            } else {
                removeFromEnvironment(OPERATION_WEAK_LISTENER);

                DataLoaderPool pool = (DataLoaderPool)Lookup.getDefault()
                        .lookup(DataLoaderPool.class);
                if(pool != null) {
                    pool.removeOperationListener(opL);
                } else {
                    ErrorManager.getDefault().notify(new NullPointerException(
                            "There is no data loader pool (from lookup)!")); //NOI18N
                }
            }

            FileChangeListener fsL = (FileChangeListener)env.get(FILESYSTEM_WEAK_LISTENER);
            if(fsL == null) {
                ErrorManager.getDefault().notify(new NullPointerException(
                        "Filesystem listener is null when closing root" // NOI18N
                        + " event context " + this)); // NOI18BN
            } else {
                removeFromEnvironment(FILESYSTEM_WEAK_LISTENER);

                try {
                    FileSystem fs = folder.getFileSystem();
                    fs.removeFileChangeListener(fsL);
                } catch(FileStateInvalidException fsie) {
                    // ignore and proceed with context shutdown
                    ErrorManager.getDefault().notify(fsie);
                }
            }
            
            removeFromEnvironment(LISTENER);
        }
        
        super.close();
    }

    // >> EventContext implementation
    /** Adds a listener for receiving naming events fired
     * when the object(s) identified by a target and scope changes.
     * Implements <code>javax.namning.event.EventContext</code>
     * interface method. */
    public void addNamingListener(Name target, int scope, NamingListener l)
    throws NamingException {
        support.addNamingListener(target, scope, l);
    }
    
    /** Adds a listener for receiving naming events fired
     * when the object named by the string target name and scope changes.
     * Implements <code>javax.namning.event.EventContext</code>
     * interface method.*/
    public void addNamingListener(String target, int scope, NamingListener l)
    throws NamingException {
        addNamingListener(getNameParser(target).parse(target), scope, l);
    }
    
    /** Removes a listener from receiving naming events fired
     * by this <tt>EventContext</tt>.
     * Implements <code>javax.namning.event.EventContext</code>
     * interface method. */
    public void removeNamingListener(NamingListener l)
    throws NamingException {
        support.removeNamingListener(l);
    }

    /** Determines whether a listener can register interest in a target
     * that does not exist.
     * Implements <code>javax.namning.event.EventContext</code>
     * interface method.
     * @return <code>false</code> */
    public boolean targetMustExist() throws NamingException {
        return false;
    }
    // << EventContext implementation

    
    private void fireTargetAdded(Name target, FileObject f, Object info) {
        support.fireTargetAdded(target, this, env, f, info);
    }
    
    private void fireTargetRemoved(Name target, FileObject f, Object info) {
        support.fireTargetRemoved(target, this, env, f, info);
    }
    
    private void fireTargetRenamed(Name target, FileObject f, Object info,
    Name oldName) {
        support.fireTargetRenamed(target, this, env, f, info, oldName);
    }
    
    private void fireTargetChanged(Name target, FileObject f, Object info) {
        support.fireTargetChanged(target, this, env, f, info);
    }


    /** Listener on FS associated with all event contexts operating over
     * tree specified by root folder. */
    private static class FSListener
    implements FileChangeListener, OperationListener, PropertyChangeListener {
        private Hashtable env;
        
        private FileObject root;
        
        public FSListener(Hashtable env, FileObject root) {
            this.env = env;
            this.root = root;
        }
        
        
        // FileSystemListener>>
        /** Implements <code>FileChangeListener</code> interface method. */
        public void fileFolderCreated(FileEvent fe) {
            FileObject f = fe.getFile();
            if(!validateFile(f)) {
                return;
            }

            // Ignore renaming.
            if(isRenaming(env, fe)) {
                return;
            }
            
            Object info = getInfo(env, fe);
            Map fireMap = getFireMap(env, f);
            
            for(Iterator it = fireMap.keySet().iterator(); it.hasNext(); ) {
                EventDOContext ctx = (EventDOContext)it.next();
                Name name = (Name)fireMap.get(ctx);
                ctx.fireTargetAdded(name, f, info);
            }
        }

        /** Fired when a new file is created. This action can only be
         * Implements <code>FileChangeListener</code> interface method. */
        public void fileDataCreated (FileEvent fe) {
            FileObject f = fe.getFile();
            if(!validateFile(f)) {
                return;
            }

            Object info = getInfo(env, fe);
            Map fireMap = getFireMap(env, f);
            
            
            Name oldName = getOldName(env, fe);
            
            for(Iterator it = fireMap.keySet().iterator(); it.hasNext(); ) {
                EventDOContext ctx = (EventDOContext)it.next();
                Name name = (Name)fireMap.get(ctx);
                
                if(oldName != null) {
                    ctx.fireTargetRenamed(name, f, info, oldName);
                } else {
                    ctx.fireTargetAdded(name, f, info);
                }
            }
        }

        /** Fired when a file is changed.
         * Implements <code>FileChangeListener</code> interface method. */
        public void fileChanged (FileEvent fe) {
            FileObject f = fe.getFile();
            if(!validateFile(f)) {
                return;
            }

            // Ignore renaming.
            if(isRenaming(env, fe)) {
                return;
            }
            
            Object info = getInfo(env, fe);
            Map fireMap = getFireMap(env, f);
            
            for(Iterator it = fireMap.keySet().iterator(); it.hasNext(); ) {
                EventDOContext ctx = (EventDOContext)it.next();
                Name name = (Name)fireMap.get(ctx);
                ctx.fireTargetChanged(name, f, info);
            }
        }

        /** Fired when a file is deleted.
         * Implements <code>FileChangeListener</code> interface method. */
        public void fileDeleted (FileEvent fe) {
            FileObject f = fe.getFile();
            if(!validateFile(f)) {
                return;
            }

            // Ignore renaming.
            if(isRenaming(env, fe)) {
                return;
            }
            
            Object info = getInfo(env, fe);
            Map fireMap = getFireMap(env, f);
            
            for(Iterator it = fireMap.keySet().iterator(); it.hasNext(); ) {
                EventDOContext ctx = (EventDOContext)it.next();
                Name name = (Name)fireMap.get(ctx);
                ctx.fireTargetRemoved(name, f, info);
            }
        }

        /** Fired when a file is renamed.
         * Implements <code>FileChangeListener</code> interface method. */
        public void fileRenamed (FileRenameEvent fe) {
            FileObject f = fe.getFile();
            if(!validateFile(f)) {
                return;
            }
            
            Object info = getInfo(env, fe);
            Map fireMap = getFireMap(env, f);

            // Ignore renaming !!, i.e. binding renaming.
            if(isRenaming(env, fe)) {
                return;
            }
            
            for(Iterator it = fireMap.keySet().iterator(); it.hasNext(); ) {
                EventDOContext ctx = (EventDOContext)it.next();
                Name name = (Name)fireMap.get(ctx);
//                ctx.fireTargetRenamed(name, f, info);
                ctx.fireTargetChanged(name, f, info);
            }
        }

        /** Fired when a file attribute is changed.
         * Implements <code>FileChangeListener</code> interface method. */
        public void fileAttributeChanged (FileAttributeEvent fe) {
            // PENDING fire or not fire?
             FileObject f = fe.getFile();
             if(!validateFile(f)) {
                 return;
             }

             Object info = getInfo(env, fe);
             Map fireMap = getFireMap(env, f);

             // Ignore renaming !!, i.e. binding renaming.
             if(isRenaming(env, fe)) {
                 return;
             }

             for(Iterator it = fireMap.keySet().iterator(); it.hasNext(); ) {
                 EventDOContext ctx = (EventDOContext)it.next();
                 Name name = (Name)fireMap.get(ctx);
                 ctx.fireTargetChanged(name, f, info);
             }
        }

        
        private boolean validateFile(FileObject f) {
            if(FileUtil.isParentOf(root, f)
            && (f.isFolder()
                    || Utils.getInstanceExtensions().contains(f.getExt())) ) {
                return true;
            }
            
            return false;
        }

// not used anymore, could be cleaned up perhaps        
//        private Context findRootContext(Hashtable env) {
//            Map contexts = (Map)env.get(DOContext.CONTEXTS);
//            if(contexts == null) {
//                return null;
//            }
//            
//            Reference ref;
//            synchronized(contexts) {
//                ref = (Reference)contexts.get(root);
//            }
//            
//            if(ref == null) {
//                return null;
//            }
//            
//            return (Context)ref.get();
//        }

        /** Gets change info from file event. */
        private static Object getInfo(Hashtable env, FileEvent fe) {
            Set createActions = (Set)env.get(DOContext.CREATE_ATOMIC_ACTIONS);
            Set set;
            synchronized(createActions) {
                set = new HashSet(createActions);
            }
            
            for(Iterator it = set.iterator(); it.hasNext(); ) {
                DOContext.CreateAtomicAction caa
                        = (DOContext.CreateAtomicAction)it.next();
                if(fe.firedFrom(caa)) {
                    return caa.getInfo();
                }
            }

            return null;
        }
        
        private static boolean isRenaming(Hashtable env, FileEvent fe) {
            return getOldName(env, fe) != null;
        }
        
        private static Name getOldName(Hashtable env, FileEvent fe) {
            Set renameActions = (Set)env.get(DOContext.RENAME_ATOMIC_ACTIONS);
            Set set;
            synchronized(renameActions) {
                set = new HashSet(renameActions);
            }
            
            for(Iterator it = set.iterator(); it.hasNext(); ) {
                DOContext.RenameAtomicAction raa
                        = (DOContext.RenameAtomicAction)it.next();
                if(fe.firedFrom(raa)) {
                    return raa.getOldName();
                }
            }

            return null;
        }
        
        private static Map getFireMap(Hashtable env, FileObject f) {
            Map contexts = (Map)env.get(DOContext.CONTEXTS);
            Map ctxMap;
            synchronized(contexts) {
                ctxMap = new HashMap(contexts);
            }
            
            Set folders = new HashSet();
            for(Iterator it = ctxMap.keySet().iterator(); it.hasNext(); ) {
                FileObject folder = (FileObject)it.next();
                if(FileUtil.isParentOf(folder, f)) {
                    folders.add(folder);
                }
            }

            Map fireMap = new HashMap(folders.size());
            for(Iterator it = folders.iterator(); it.hasNext(); ) {
                FileObject folder = (FileObject)it.next();

                String fPath = folder.getPath();
                String path = f.getPath();

                // Trim extension.
                if(!f.isFolder()) {
                    int lastDot = path.lastIndexOf('.');
                    int lastSlash = path.lastIndexOf('/');
                    if(lastDot > lastSlash) {
                        path = path.substring(0, lastDot);
                    }
                }
                
                Name name;
                try {
                    name = DOContext.getNameParser().parse(
                            path.substring(fPath.length()));
                } catch(NamingException ne) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL,
                            ne);
                    continue;
                }
                
//                Reference ref = (Reference)ctxMap.get(folder);
//                if(ref != null) {
//                    EventContext ctx = (EventContext)ref.get();
                EventContext ctx = (EventContext)ctxMap.get(folder);
                if(ctx != null) {
                    fireMap.put(ctx, name);
                }
//                }
            }
            
            return fireMap;
        }
    
        
        // OperationListener implementation >>
        public void operationCopy(OperationEvent.Copy evt) {}
        public void operationCreateFromTemplate(OperationEvent.Copy evt) {}
        public void operationCreateShadow(OperationEvent.Copy evt) {}
        public void operationDelete(OperationEvent evt) {}
        public void operationMove(OperationEvent.Move evt) {}
        public void operationRename(OperationEvent.Rename evt) {}
        
        /** Object has been recognized by
         * {@link DataLoaderPool#findDataObject}. */
        public void operationPostCreate(OperationEvent evt) {
            DataObject dobj = evt.getObject();
            FileObject primary = dobj.getPrimaryFile();
            if(!FileUtil.isParentOf(root, primary)
            || !Utils.isInstanceFile(primary)) {
                return;
            }
            
            dobj.addPropertyChangeListener(
                    WeakListener.propertyChange(this, dobj));
        }
        
        
        public void propertyChange(PropertyChangeEvent evt) {
            if(DataObject.PROP_COOKIE.equals(evt.getPropertyName())) {
                DataObject dobj = (DataObject)evt.getSource();
                if(!dobj.isValid()) {
                    return;
                }
                
                FileObject fo = dobj.getPrimaryFile();
                
                if(!validateFile(fo)) {
                    return;
                }
//
//                // Ignore renaming.
//                if(isRenaming(env, fe)) {
//                    return;
//                }
//                Object info = getInfo(env, fe);
                Map fireMap = getFireMap(env, fo);

                for(Iterator it = fireMap.keySet().iterator(); it.hasNext(); ) {
                    EventDOContext ctx = (EventDOContext)it.next();
                    Name name = (Name)fireMap.get(ctx);
                    ctx.fireTargetChanged(name, fo, null);
                }
            }
        }
    }
    // FSListener<<
    
}

