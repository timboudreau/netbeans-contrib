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
@org.openide.util.lookup.ServiceProvider(service=javax.naming.spi.InitialContextFactoryBuilder.class)
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
