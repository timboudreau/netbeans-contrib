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
