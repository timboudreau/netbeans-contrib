/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jndi.spi;

import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.Context;
import javax.naming.NamingException;
import org.openide.TopManager;
/**
 *
 * @author  Tomas Zezula
 */
public class NbInitialContextFactoryBuilder implements InitialContextFactoryBuilder {
    
    /** Creates a new instance of NbInitialContextFactoryBuilder */
    public NbInitialContextFactoryBuilder() {
    }
    
    public javax.naming.spi.InitialContextFactory createInitialContextFactory(java.util.Hashtable hashtable) throws javax.naming.NamingException {
        String className = (String) hashtable.get(Context.INITIAL_CONTEXT_FACTORY);
        if (className == null)
            throw new IllegalArgumentException();
        try {
            Class factoryClass = Class.forName(className,true,TopManager.getDefault().currentClassLoader());
            return (javax.naming.spi.InitialContextFactory) factoryClass.newInstance();
        }catch (ClassNotFoundException classNotFoundException) {
            NamingException ne = new NamingException ();
            ne.setRootCause (classNotFoundException);
            throw ne;
        }
        catch (InstantiationException instantiationException) {
            NamingException ne = new NamingException ();
            ne.setRootCause (instantiationException);
            throw ne;
        }
        catch (IllegalAccessException illegalAccessException) {
            NamingException ne = new NamingException ();
            ne.setRootCause (illegalAccessException);
            throw ne;
        }
    }
    
}
