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
