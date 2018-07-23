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

package org.netbeans.modules.jndi.spi;

import java.security.AllPermission;
import java.security.PermissionCollection;
import java.security.Permissions;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.Context;
import javax.naming.NamingException;
import org.openide.execution.NbClassLoader;
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
            NbClassLoader nbcl = new NbClassLoader();
            nbcl.setDefaultPermissions(getAllPermissions());
            Class factoryClass = Class.forName(className,true,nbcl);
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
    
    private static PermissionCollection allPerimssions;
    static synchronized PermissionCollection getAllPermissions() {
        if (allPerimssions == null) {
            allPerimssions = new Permissions();
            allPerimssions.add(new AllPermission());
        }
        return allPerimssions;
    }
    
}
