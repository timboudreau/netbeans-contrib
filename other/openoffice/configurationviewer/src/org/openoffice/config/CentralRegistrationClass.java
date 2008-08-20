/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
package org.openoffice.config;

import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 *
 * @author S. Aubrecht
 */
public class CentralRegistrationClass {
    
    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        String regClassesList = getRegistrationClasses();
        StringTokenizer t = new StringTokenizer(regClassesList, " ");
        while (t.hasMoreTokens()) {
            String className = t.nextToken();
            if (className != null && className.length() != 0) {
                try {
                    Class regClass = Class.forName(className);
                    Method writeRegInfo = regClass.getDeclaredMethod("__getComponentFactory", new Class[]{String.class});
                    Object result = writeRegInfo.invoke(regClass, sImplementationName);
                    if (result != null) {
                       return (XSingleComponentFactory)result;
                    }
                }
                catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (ClassCastException ex) {
                    ex.printStackTrace();
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                } catch (NoSuchMethodException ex) {
                    ex.printStackTrace();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        boolean bResult = true;
        String regClassesList = getRegistrationClasses();
        StringTokenizer t = new StringTokenizer(regClassesList, " ");
        while (t.hasMoreTokens()) {
            String className = t.nextToken();
            if (className != null && className.length() != 0) {
                try {
                    Class regClass = Class.forName(className);
                    Method writeRegInfo = regClass.getDeclaredMethod("__writeRegistryServiceInfo", new Class[]{XRegistryKey.class});
                    Object result = writeRegInfo.invoke(regClass, xRegistryKey);
                    bResult &= ((Boolean)result).booleanValue();
                }
                catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (ClassCastException ex) {
                    ex.printStackTrace();
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                } catch (NoSuchMethodException ex) {
                    ex.printStackTrace();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return bResult;
    }

    private static String getRegistrationClasses() {
        CentralRegistrationClass c = new CentralRegistrationClass();
        String name = c.getClass().getCanonicalName().replace('.', '/').concat(".class");
        try {
            Enumeration<URL> urlEnum = c.getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (urlEnum.hasMoreElements()) {
                URL url = urlEnum.nextElement();
                String file = url.getFile();
                JarURLConnection jarConnection =
                    (JarURLConnection) url.openConnection();
                Manifest mf = jarConnection.getManifest();

                Attributes attrs = (Attributes) mf.getAttributes(name);
                if ( attrs != null ) {
                    String classes = attrs.getValue( "RegistrationClasses" );
                    return classes;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
            
        return "";
    }
    
    /** Creates a new instance of CentralRegistrationClass */
    private CentralRegistrationClass() {
    }
}
