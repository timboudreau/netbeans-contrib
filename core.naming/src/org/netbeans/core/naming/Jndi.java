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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.Lookup;


/** A context over system file system.
 *
 * @author  Jaroslav Tulach
 */
public final class Jndi implements InitialContextFactory, InitialContextFactoryBuilder {
    
    /** Name of property in environment keeping root file object. */
    static final String ROOT_OBJECT = "rootObject"; //NOI18N

    
    /** Creates new <code>Jndi</code> instance. */
    public Jndi() {
    }
    
    
    /** Implements <code>InitialContextFacory</code> interface. */
    public Context getInitialContext(Hashtable hashtable) throws NamingException {
        if(hashtable == null) {
            hashtable = new Hashtable();
        }

        // Initialize environment.
        FileObject root = (FileObject) hashtable.get (ROOT_OBJECT);
        if (root == null) {
            root = Repository.getDefault ().getDefaultFileSystem().getRoot();
            hashtable.put (ROOT_OBJECT, root);
        }

        if(hashtable.get(DOContext.CONTEXTS) == null) {
//            hashtable.put (DOContext.CONTEXTS, new WeakHashMap (37));
            hashtable.put (DOContext.CONTEXTS, new HashMap(40));
        }

        if(hashtable.get(DOContext.CREATE_ATOMIC_ACTIONS) == null) {
            hashtable.put(DOContext.CREATE_ATOMIC_ACTIONS, new HashSet(10));
        }

        if(hashtable.get(DOContext.RENAME_ATOMIC_ACTIONS) == null) {
            hashtable.put(DOContext.RENAME_ATOMIC_ACTIONS, new HashSet(10));
        }

        return  DOContext.find(hashtable, root);
    }

    /** Implements <code>InitialContextFactoryBuilder</code> interface. */
    public InitialContextFactory createInitialContextFactory(Hashtable hashtable) 
    throws NamingException {
        String className = (String) hashtable.get(Context.INITIAL_CONTEXT_FACTORY);
        if (className == null || className.equals (Jndi.class.getName())) {
            // the default is to use our own context factory
            return this;
        }

        try {
            Object classloader = hashtable.get (ClassLoader.class);
            if (! (classloader instanceof ClassLoader) ) {
                // use default
                classloader = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
            }

            Class factoryClass = Class.forName(
                className,true, (ClassLoader)classloader
            );
            return (InitialContextFactory) factoryClass.newInstance();
        } catch (ClassNotFoundException classNotFoundException) {
            NamingException ne = new NamingException ();
            ne.setRootCause (classNotFoundException);
            throw ne;
        } catch (InstantiationException instantiationException) {
            NamingException ne = new NamingException ();
            ne.setRootCause (instantiationException);
            throw ne;
        } catch (IllegalAccessException illegalAccessException) {
            NamingException ne = new NamingException ();
            ne.setRootCause (illegalAccessException);
            throw ne;
        }
    }
}
