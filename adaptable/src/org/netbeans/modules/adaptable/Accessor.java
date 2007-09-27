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

package org.netbeans.modules.adaptable;

import java.util.Set;
import org.netbeans.api.adaptable.*;
import org.netbeans.spi.adaptable.SingletonizerEvent;

/** Class that allows "friend" calls to api package.
 *
 * @author Jaroslav Tulach
 */
public abstract class Accessor {
    /** instance to make calls to api package */
    public static Accessor API;
    /** spi part of the accessor */
    public static Accessor SPI;

    static {
        // forces initialization of class Adaptor that initializes
        // field API
        Class c = Adaptor.class;
        try {
            Class.forName (c.getName (), true, c.getClassLoader ());
        } catch (Exception ex) {
            // swallow
        }
        //org.netbeans.api.adaptable.Adaptor.init ();
        assert API != null : "We have to initilialize the API field"; // NOI18N

        c = SingletonizerEvent.class;
        try {
            Class.forName (c.getName (), true, c.getClassLoader ());
        } catch (Exception ex) {
            // swallow
        }
        //org.netbeans.api.adaptable.Adaptor.init ();
        assert SPI != null : "We have to initilialize the SPI field"; // NOI18N
    }
 
    /**
     * Creates new instance of Adaptor
     * @param impl the impl to pass to the provider
     */
    public abstract Adaptor createAspectProvider (ProviderImpl impl, Object data);

    /** creates the AdaptableEvent.
     */
    public abstract AdaptableEvent createEvent(Adaptable source, Set<Class> affected);
    
    /** Gets the associated data */
    public abstract Object getData (Adaptor adaptor);
    /** Gets associated provider */
    public abstract ProviderImpl getProviderImpl (Adaptor adaptor);

    /** Gets affected object from an event.
     */
    public abstract Object getAffectedObject(SingletonizerEvent ev);

    /** Gets set of affected classes from the event.
     */
    public abstract Set<Class> getAffectedClasses(SingletonizerEvent ev);
}
