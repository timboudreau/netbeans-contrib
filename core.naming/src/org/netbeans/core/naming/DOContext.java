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

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

import javax.naming.*;
import javax.naming.spi.*;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.*;


/** A context over system file system.
 *
 * @author  Jaroslav Tulach
 */
/*final*/ class DOContext extends Object implements Context/*, NameParser*/ {
    
    /** Name in environment which keeps map of contexts. */
    static final String CONTEXTS = "contexts"; // NOI18N

    /** Name for keeping map of "create" atomic actions. */
    static final String CREATE_ATOMIC_ACTIONS = "createAtomicActions"; // NOI18N
    /** Name for keeping map of "rename" atomic actions. */
    static final String RENAME_ATOMIC_ACTIONS = "renameAtomicActions"; // NOI18N
    
    
//    private static final CreateAtomicAction createAtomicAction
//            = new CreateAtomicAction();
//    
//    private static final RenameAtomicAction renameAtomicAction 
//            = new RenameAtomicAction(); 
    
    
    /** a file object we are attached to */
    FileObject folder;
    /** Environment associated with context */
    Hashtable env;
    
    /** Context.
     * @param fo file to be assigned to
     */
    DOContext (Hashtable env, FileObject folder) {
//        this.env = new Hashtable (env); 
        this.env = env;
        this.folder = folder;
    }
    
    /** Finds the context for given file.
     * @param fo file object
     * @return the context for it
     * @exception NamingException thrown if not file object
     */
    public static synchronized DOContext find (Hashtable env, FileObject fo) throws NamingException {
        if(!fo.isFolder()) {
            throw new NamingException ("Not a folder " + fo); // NOI18N
        }
        
        Map contexts = (Map)env.get(CONTEXTS);
        if(contexts == null) {
            throw new NamingException ("Not a valid environment." // NOI18N
                    + " Missing contexts."); // NOI18N
        }
        
        if(env.get(CREATE_ATOMIC_ACTIONS) == null) {
            throw new NamingException ("Not a valid environment." // NOI18N
                    + " Missing create atomic acions map."); // NOI18N
        }
        
        if(env.get(RENAME_ATOMIC_ACTIONS) == null) {
            throw new NamingException ("Not a valid environment." // NOI18N
                    + " Missing rename atomic acions map."); // NOI18N
        }


//        java.lang.ref.Reference ref;
        DOContext c;
        synchronized(contexts) {
//            ref = (java.lang.ref.Reference)contexts.get (fo); 
            c = (DOContext)contexts.get(fo);
        }
//        DOContext c = ref == null ? null : (DOContext)ref.get ();
        if (c == null) {
            // XXX If not supporting event context, just switch the lines.
            //c = new DOContext (env, fo);
            c = new EventDOContext(env, fo);
            
            synchronized(contexts) {
                // XXX no soft refs, otherwise the listeners could gone.
                // Needed to make another "release" policy. E.g. when
                // deleted folder and its context has no listeners.
//                contexts.put (fo, new SoftReference (c));
                contexts.put(fo, c);
            }
        }
        return c;
    }
    
    //
    // Lookup
    //
    
    public Object lookupLink(String str) throws javax.naming.NamingException {
        return lookupLink (getNameParser().parse (str));
    }
    
    public Object lookupLink(javax.naming.Name name) throws javax.naming.NamingException {
        return lookupImpl (name, false);
    }

    public String getNameInNamespace() throws javax.naming.NamingException {
        return folder.toString ();
    }
    
    public Object lookup(String str) throws javax.naming.NamingException {
        return lookup (getNameParser().parse (str));
    }
    
    public Object lookup(Name name) throws javax.naming.NamingException {
        return lookupImpl (name, true);
    }

    private Object lookupImpl (Name name, boolean followLinks) throws javax.naming.NamingException {
        FileObject f = Utils.findFileForName(folder, name, followLinks);
        return Utils.getObjectForFile(env, f, followLinks);
    }
    
    
    //
    // Listing
    //
    
    public javax.naming.NamingEnumeration list(Name name) throws javax.naming.NamingException {
        try {
            return DOEnum.create (lookup (name));
        } catch (javax.naming.NameNotFoundException ex) {
            return DOEnum.EMPTY;
        }
    }
    
    public javax.naming.NamingEnumeration list(String str) throws javax.naming.NamingException {
        try {
            return DOEnum.create (lookup (str));
        } catch (javax.naming.NameNotFoundException ex) {
            return DOEnum.EMPTY;
        }
    }
    
    public javax.naming.NamingEnumeration listBindings(String str) throws javax.naming.NamingException {
        try {
            return DOEnum.create (lookup (str));
        } catch (javax.naming.NameNotFoundException ex) {
            return DOEnum.EMPTY;
        }
    }
    
    public javax.naming.NamingEnumeration listBindings(Name name) throws javax.naming.NamingException {
        try {
            return DOEnum.create (lookup (name));
        } catch (javax.naming.NameNotFoundException ex) {
            return DOEnum.EMPTY;
        }
    }    
    
    
    //
    // Modification methods
    //
    
    /** Convenience method to find a target context. */
    Context findTargetContext (Name name) throws NamingException {
        int size = name.size ();
        if (size == 0) throw new InvalidNameException ("Empty name"); // NOI18N

        if (size == 1) {
            return this;
        }
        
        Object o = lookup (name.getPrefix (size - 1));
        if (o instanceof Context) {
            return (Context)o;
        }
        
        throw new NotContextException ("Target context required"); // NOI18N
    }

    public javax.naming.Context createSubcontext(String str) throws javax.naming.NamingException {
        return createSubcontext (getNameParser().parse (str));
    }

    public javax.naming.Context createSubcontext(Name name) throws javax.naming.NamingException {
        Context ctx = findTargetContext (name);
        if (ctx == this) {
            String n = name.get (0);
            FileObject f = folder.getFileObject (n);
            if (f != null && f.isFolder ()) throw new NameAlreadyBoundException (n);

            try {
                return find (env, folder.createFolder (n));
            } catch (IOException ex) {
                NamingException nex = new NamingException(ex.getMessage());
                nex.setRootCause(ex);
                throw nex;
            }
        } else {
            return ctx.createSubcontext (name.get (name.size () - 1));
        }
    }
    
    public void destroySubcontext(String str) throws javax.naming.NamingException {
        destroySubcontext (getNameParser().parse (str));
    }

    public void destroySubcontext(Name name) throws javax.naming.NamingException {
        Context ctx = findTargetContext (name);
        if (ctx == this) {
            String n = name.get (0);
            FileObject f = folder.getFileObject (n);
            if (f != null) {
                if (!f.isFolder ()) throw new NotContextException (n);
                if (f.getChildren ().length > 0) throw new ContextNotEmptyException ();

                try {
                    f.delete ();
                    Map contexts = (Map)env.get(CONTEXTS);
                    synchronized(contexts) {
                        contexts.remove (f);
                    }
                } catch (IOException ex) {
                    NamingException nex = new NamingException(ex.getMessage());
                    nex.setRootCause(ex);
                    throw nex;
                }
            }
        } else {
            ctx.destroySubcontext (name.get (name.size () - 1));
        }
    }
    
    public void rename(String oldName, String newName) throws javax.naming.NamingException {
        rename (getNameParser().parse (oldName),
                getNameParser().parse (newName));
    }
    
    public void rename(Name oldName, Name newName) throws javax.naming.NamingException {
        Object o = lookup (oldName);

        Set renameActions = (Set)env.get(RENAME_ATOMIC_ACTIONS);
        RenameAtomicAction renameAtomicAction = new RenameAtomicAction();
        renameAtomicAction.set(this, o, oldName, newName);
        synchronized(renameActions) {
            renameActions.add(renameAtomicAction);
        }

        try {
            folder.getFileSystem().runAtomicAction(renameAtomicAction);
            NamingException ne = renameAtomicAction.getException();
            if(ne != null) {
                throw ne;
            }
        } catch(IOException ioe) {
            NamingException ne = new NamingException();
            ne.setRootCause(ioe);
            throw ne;
        } finally {
            synchronized(renameActions) {
                renameActions.remove(renameAtomicAction);
            }
        }
//        unbind (oldName);
//        bind (newName, o);
    }
    
    public void unbind(String str) throws javax.naming.NamingException {
        unbind (getNameParser().parse (str));
    }
    
    public void unbind(Name name) throws javax.naming.NamingException {
        Context ctx = findTargetContext (name);
        if (ctx == this) {
            String n = name.get (0);
            FileObject f = Utils.findInstanceFile (folder, n);
            if (f != null) {
                if (f.isFolder ()) throw new InvalidNameException ("Name denotes context, use destroySubcontext() method"); //NOI18N
                
                try {
                    f.delete ();
                } catch (IOException ex) {
                    NamingException nex = new NamingException(ex.getMessage());
                    nex.setRootCause(ex);
                    throw nex;
                }
            }
        } else {
            ctx.unbind (name.get (name.size () - 1));
        }
    }
    
    public void rebind(String str, Object obj) throws javax.naming.NamingException {
        rebind (getNameParser().parse (str), obj);
    }
    
    public void rebind(Name name, Object obj) throws javax.naming.NamingException {
        Context ctx = findTargetContext (name);
        if (ctx == this) {
            doBind (name, obj);
        } else {
            ctx.bind (name.get (name.size () - 1), obj);
        }
    }
    
    public void bind(String str, Object obj) throws javax.naming.NamingException {
        bind (getNameParser().parse (str), obj);
    }

    public void bind(Name name, Object obj) throws javax.naming.NamingException {
        Context ctx = findTargetContext (name);
        if (ctx == this) {
            String n = name.get (0);
            FileObject f = Utils.findInstanceFile (folder, n);
            if (f != null) throw new NameAlreadyBoundException (n);
            doBind (name, obj);
        } else {
            ctx.bind (name.get (name.size () - 1), obj);
        }
    }
    
    public void close() throws javax.naming.NamingException {
        Map contexts = (Map)env.get(CONTEXTS);
        synchronized(contexts) {
            contexts.remove (folder);
        }
    }

    private void doBind (final Name n, Object o)
    throws javax.naming.NamingException {
        o = NamingManager.getStateToBind (o, n, this, env);

        if (o instanceof Referenceable) {
            o = ((Referenceable) o).getReference ();
        }

        Set createActions = (Set)env.get(CREATE_ATOMIC_ACTIONS);
        CreateAtomicAction createAtomicAction = new CreateAtomicAction();
        createAtomicAction.set(
            DataFolder.findFolder(folder),
            n.get(0),
            o,
            this
        );
        synchronized(createActions) {
            createActions.add(createAtomicAction);
        }
        try {
            folder.getFileSystem().runAtomicAction(createAtomicAction);
        } catch (IOException ex) {
            NamingException nex = new NamingException(ex.getMessage());
            nex.setRootCause(ex);
            throw nex;
        } finally {
            synchronized(createActions) {
                createActions.remove(createAtomicAction);
            }
        }
    }

    //
    // Environment
    //
    
    public Object addToEnvironment(String str, Object obj) throws javax.naming.NamingException {
        return env.put (str, obj);
    }
    

    public Object removeFromEnvironment(String str) throws javax.naming.NamingException {
        return env.remove (str);
    }
    
    public java.util.Hashtable getEnvironment() throws javax.naming.NamingException {
        return new Hashtable(env);
    }
    
    // 
    // Name parser
    //

    public javax.naming.NameParser getNameParser(javax.naming.Name name) throws javax.naming.NamingException {
        return getNameParser();
    }

    public javax.naming.NameParser getNameParser(String str) throws javax.naming.NamingException {
        return getNameParser();
    }
    
    public javax.naming.Name composeName(javax.naming.Name name, javax.naming.Name name1) throws javax.naming.NamingException {
        Name name2 = (Name)name1.clone();
        name2.addAll(name);
        return name2;
    }
    
    public String composeName(String str, String str1) throws javax.naming.NamingException {
        Name name = composeName(new CompositeName(str), new CompositeName(str1));
        return name.toString();
    }
    
    
    //
    // Name parsing stuff
    //
    public static NameParser getNameParser() {
        return defaultParser;
    }
    
    private static final NameParser defaultParser = new FileNameParser();
    
    private static class FileNameParser implements NameParser {
        FileNameParser() {}
        
        public Name parse(String s) throws NamingException {
            int start = s.startsWith ("/") ? 1 : 0;
            int end = s.endsWith ("/") ? s.length () - 1 : s.length ();

            if (start < end) {
                s = s.substring (start, end);
            } else {
                s = "";
            }

            return new CompositeName (s);
        }
    } // End of class FileNameParser.

    
//    static CreateAtomicAction getCreateAtomicAction() {
//        return createAtomicAction;
//    }
//    
//    static RenameAtomicAction getRenameAtomicAction() {
//        return renameAtomicAction;
//    }

//    // XXX Ugly trick, missing the possibility to get
//    // the atomic action in FileEvent.
//    /** Atomic action. Hacks ability to pass info about context
//     * causing the atomic action. It is then compared in Event context
//     * and retrieved as additional info. */
//    static class CreateAtomicAction implements FileSystem.AtomicAction {
//        private final ThreadLocal dataFolder = new ThreadLocal();
//        private final ThreadLocal name = new ThreadLocal();
//        private final ThreadLocal instance = new ThreadLocal();
//        private final ThreadLocal info = new ThreadLocal();
//
//        /** Implements <code>FileSystem.AtomicAction</code> interface. */
//        public void run() throws IOException {
//            InstanceDataObject.create(
//                (DataFolder)dataFolder.get(),
//                (String)name.get(),
//                instance.get(),
//                null);
//        }
//        
//        public Object getInfo() {
//            return info.get();
//        }
//        
//        public void set(DataFolder dataFolder, String name, Object instance,
//        Object info) {
//            this.dataFolder.set(dataFolder);
//            this.name.set(name);
//            this.instance.set(instance);
//            this.info.set(info);
//        }
//        
//        public void clear() {
//            dataFolder.set(null);
//            name.set(null);
//            instance.set(null);
//            info.set(null);
//        }
//    } // End of DOContextAtomicAction.
//    
//    // XXX Ugly trick, missing the possibility to handle rename action.
//    /** Atomic action. Hacks ability to pass info about context
//     * causing the atomic action. It is then compared in Event context
//     * and retrieved as additional info. */
//    static class RenameAtomicAction implements FileSystem.AtomicAction {
//        private final ThreadLocal context = new ThreadLocal();
//        private final ThreadLocal object = new ThreadLocal();
//        private final ThreadLocal oldName = new ThreadLocal();
//        private final ThreadLocal newName = new ThreadLocal();
//        private final ThreadLocal exception = new ThreadLocal();
//
//        /** Implements <code>FileSystem.AtomicAction</code> interface. */
//        public void run() throws IOException {
//            Context ctx = (Context)context.get();
//            try {
//                ctx.unbind((Name)oldName.get());
//                ctx.bind((Name)newName.get(), object.get());
//            } catch(NamingException ne) {
//                exception.set(ne);
//            }
//        }
//        
//        public Name getOldName() {
//            return (Name)oldName.get();
//        }
//        
//        public NamingException getException() {
//            return (NamingException)exception.get();
//        }
//        
//        public void set(Context ctx, Object object, Name oldName, Name newName) {
//            this.context.set(ctx);
//            this.object.set(object);
//            this.oldName.set(oldName);
//            this.newName.set(newName);
//        }
//        
//        public void clear() {
//            context.set(null);
//            object.set(null);
//            oldName.set(null);
//            newName.set(null);
//        }
//    } // End of RenameAtomicAction.
    
    // XXX Ugly trick, missing the possibility to get
    // the atomic action in FileEvent.
    /** Atomic action. Hacks ability to pass info about context
     * causing the atomic action. It is then compared in Event context
     * and retrieved as additional info. */
    static class CreateAtomicAction implements FileSystem.AtomicAction {
        private DataFolder dataFolder;
        private String name;
        private Object instance;
        private Object info;

        /** Implements <code>FileSystem.AtomicAction</code> interface. */
        public void run() throws IOException {
            InstanceDataObject.create(dataFolder, name, instance, null);
        }
        
        public Object getInfo() {
            return info;
        }
        
        public void set(DataFolder dataFolder, String name, Object instance,
        Object info) {
            this.dataFolder = dataFolder;
            this.name = name;
            this.instance = instance;
            this.info = info;
        }
    } // End of DOContextAtomicAction.
    
    // XXX Ugly trick, missing the possibility to handle rename action.
    /** Atomic action. Hacks ability to pass info about context
     * causing the atomic action. It is then compared in Event context
     * and retrieved as additional info. */
    static class RenameAtomicAction implements FileSystem.AtomicAction {
        private Context context;
        private Object object;
        private Name oldName;
        private Name newName;
        private NamingException exception;

        /** Implements <code>FileSystem.AtomicAction</code> interface. */
        public void run() throws IOException {
            try {
                context.unbind(oldName);
                context.bind(newName, object);
            } catch(NamingException ne) {
                exception = ne;
            }
        }
        
        public Name getOldName() {
            return oldName;
        }
        
        public NamingException getException() {
            return exception;
        }
        
        public void set(Context ctx, Object object, Name oldName, Name newName) {
            this.context = ctx;
            this.object = object;
            this.oldName = oldName;
            this.newName = newName;
        }
    } // End of RenameAtomicAction.
    
}
