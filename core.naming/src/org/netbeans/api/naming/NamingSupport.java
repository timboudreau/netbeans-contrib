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

package org.netbeans.api.naming;


import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openide.ErrorManager;


/**
 * Provides factory of <code>InitialContext</code>
 * working over system filesystem (SFS).
 *
 * @author  Peter Zavadsky
 *
 * @deprecated use protocol nbres:/ e.g. like this: (Context)new InitialContext().lookup("nbres:/");
 *      or better yet use protocol nbres:/ in your JNDI lookup query
 */
public final class NamingSupport {

    private NamingSupport() {
    }

    /** Creates instance of <code>IntialContext</code> working over
     * system filesystem, with default (empty) environment.
     * @throws NamingException when problem while instantiation of
     *          <code>InitialContext</code> is encountered 
     * @deprecated use protocol nbres:/ e.g. like this: (Context)new InitialContext().lookup("nbres:/");
     */
    public static InitialContext createSFSInitialContext()
    throws NamingException {
        return createSFSInitialContext(null);
    }
    
    /** Creates instance of <code>IntialContext</code> working over
     * system filesystem, with specified environment.
     * @param env environent to be used for <code>InitialContext</code>
     *              It is possible to use <code>null</code> value
     * @throws NamingException when problem while instantiation of
     *          <code>InitialContext</code> is encountered 
     * @deprecated use protocol nbres:/ e.g. like this: (Context)new InitialContext().lookup("nbres:/");
     */
    public static InitialContext createSFSInitialContext(Hashtable env)
    throws NamingException {
        if(env == null) {
            env = new Hashtable();
        } else {
            // Check the environment, if already contains the key.
            Object icf = env.get(Context.INITIAL_CONTEXT_FACTORY);
            if(icf != null) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING,
                    new IllegalArgumentException("There was set " // NOI18N
                        + Context.INITIAL_CONTEXT_FACTORY + "=" + icf // NOI18N
                        + ". Ignoring")); // NOI18N
            }
        }

        InitialContext ret = createSFSInitialContextImpl(env);
        return ret;
    }

    
    private static InitialContext createSFSInitialContextImpl(Hashtable env)
    throws NamingException {
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.netbeans.core.naming.Jndi"); // NOI18N
        return new InitialContext(env);
    }
    
}
