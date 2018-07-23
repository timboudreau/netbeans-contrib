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

package org.netbeans.modules.looks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import org.netbeans.spi.looks.Look;
import org.openide.util.Enumerations;

/** Utility class for converting beween types and names.
 *
 * @author Jaroslav Tulach, Petr Hrebejk
 */
public class TypesSearch  {

    /** Static utility class
     */
    private TypesSearch() {};

    /** Enumeration for a java class.
     * @param c class 
     * @return enumeration of names of implemented/extended classes in form suitable
     *      for findLook
     */
    public static Enumeration namesForClass (Class c) {
        Enumeration en;
        if (c != Object.class) {
            en = Enumerations.queue(Enumerations.singleton(c), new Enumerations.Processor() {
                public Object process(Object o, Collection coll) {
                    Class x = (Class) o;
                    Class s = x.getSuperclass();
                    coll.addAll(Arrays.asList(x.getInterfaces()));
                    if (s != null && s != Object.class) {
                        // if not object process all interfaces and super classes
                        coll.add(s);
                    }
                    return o;
                }
            });
        } else {
            en = Enumerations.empty();
        }
        
        Enumeration alter = Enumerations.convert(en, new Enumerations.Processor() {
            public Object process(Object clazz, Collection ignore) {
                Class c = (Class) clazz;
                return c.getName().replace('.', '/');
            }
        });
        
        return Enumerations.concat(alter, Enumerations.singleton("java/lang/Object")); // NOI18N
    }
    
    /** Finds look(s) for a class.
     * @param prefix prefix string to add to each name or null
     * @param names list of Strings to check their names (c1/c2/name)
     * @param looks list where to add all found looks or null if just find one
     * @return look found or null 
     */
     static Enumeration findLooks( String prefix, Enumeration names, RegistryBridge rb ) {
         ArrayList list = new ArrayList ();        
         findLook( prefix, names, list, null, rb );
         return Collections.enumeration( list );
     }
    
     /** Original version of the method from namespace look */
     private static Look findLook (String prefix, Enumeration names, List looks, String preferredName, RegistryBridge registryBridge ) {
        
        Collection checks = new HashSet();
        Look preferredLook = null;
        
        while (names.hasMoreElements ()) {
            String check = (String)names.nextElement ();
            if (!checks.add(check))
                continue;
            if (prefix != null) {
                check = prefix + check;
            }

            Enumeration en = registryBridge.getObjects (check, Look.class);
            while (en.hasMoreElements ()) {                    
                Object o = en.nextElement ();

                if (o != null) {
                    if ( preferredName != null && 
                         preferredName.equals( ((Look)o).getName() ) ) {
                        preferredLook = (Look)o;
                    }
                    if (looks != null) {
                        // we are searching for all looks
                        looks.add (o);
                    } else {
                        // we need just one
                        return (Look)o;
                    }
                }
            }            
        }
        return preferredLook == null ? 
                   ( looks == null || looks.size() == 0 ?  null : (Look)looks.get( 0 ) ) :
                   preferredLook;
    }
    
}
