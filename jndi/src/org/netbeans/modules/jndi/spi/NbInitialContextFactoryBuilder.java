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
