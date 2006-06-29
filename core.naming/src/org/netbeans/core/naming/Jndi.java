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
