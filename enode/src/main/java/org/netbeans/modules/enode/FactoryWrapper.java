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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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

package org.netbeans.modules.enode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Template;

import org.netbeans.spi.enode.LookupContentFactory;

/** Wrapper object for delaying of loading classes of the attached
 * objects. Reads the file attributes needed for this optimalization.
 * @author David Strupl
 */
public class FactoryWrapper implements LookupContentFactory {
    
    private static final Logger log = Logger.getLogger(FactoryWrapper.class.getName());
    private static boolean LOGGABLE = log.isLoggable(Level.FINE);

    /**
     * File object on the system filesystem. The attributes of this
     * file object are used to determine the class of the result.
     */
    private FileObject f;
    
    /**
     * The result of call to the <code> instantiate </code> method.
     */
    private Object obj;
    
    /**
     * Stores the classes that we were asked for and returned that
     * the resulting object does not implement them. The collection
     * is used in method @see #checkImplementsClause(Object).
     */
    private Set<Class> implementsQueries = new HashSet<Class>();
    
    /** Just remembers the parameter.*/
    public FactoryWrapper(FileObject f) {
        this.f = f;
    }
    
    /**
     * Method from the LookupContentFactory interface. This method
     * passes the <code>target</code> argument to the delegated
     * LookupContentFactory.
     */
    public Object create(Node target) {
        if (obj == null) {
            obj = instantiate();
        }
        if (obj instanceof LookupContentFactory) {
            LookupContentFactory lcf = (LookupContentFactory)obj;
            Object result = lcf.create(target);
            checkImplementsClause(result);
            return result;
        }
        checkImplementsClause(obj);
        return obj;
    }
    
    /**
     * 
     */
    private void checkImplementsClause(Object toBeReturned) {
        for (Class clazz : implementsQueries) {
            if (clazz.isInstance(toBeReturned)) {
                throw new IllegalStateException("Registration under " + f.getPath() + // NOI18N
                        " \n is missing implements entries for " + toBeReturned + // NOI18N
                        " \n namelly " + clazz.getName() + // NOI18N
                        " \n the implements attribute is " + // NOI18N
                        f.getAttribute("implements")); // NOI18N
            }
        }
    }
    
    /**
     * Method from the LookupContentFactory interface. This method
     * passes the <code>target</code> argument to the delegated
     * LookupContentFactory.
     */
    public Lookup createLookup(Node target) {
        if (obj == null) {
            obj = instantiate();
        }
        if (obj instanceof LookupContentFactory) {
            LookupContentFactory lcf = (LookupContentFactory)obj;
            return lcf.createLookup(target);
        }
        return null;
    }
    
    /**
     * Checks whether we can match the template. If the resulting object
     * has been computed we just use its class or if it has not the file
     * object is examined for the "implements" attribute.
     */
    boolean matches(Template template) {
        if (template.getType() != null) {
            if (obj != null) {
                // after the factory object was created we cannot determine
                // which interfaces it implements and do not want to go
                // through the expensive check bellow we simply return
                // true here and the lookup implementation will handle
                // correct return value from the lookup
                return true;
            }
            if (! resultImplements().contains(template.getType().getName())) {
                if (LOGGABLE) {
                    log.fine("implementsQueries adding " + 
                        template.getType().getName() +
                        " while the attribute is " +
                        f.getAttribute("implements"));
                }
                implementsQueries.add(template.getType());
                return false;
            }
        }
        return true;
    }
    
    /**
     * Parses the value of attribute "implements"
     * @return List of String with names of classes/interfaces
     *      implemented by the resulting object
     */
    private List resultImplements() {
        String classAttr = (String)f.getAttribute("implements"); // NOI18N
        if (LOGGABLE) {
            log.fine("resultImplements the attribute is " +
            f.getAttribute("implements"));
        }
        ArrayList res = new ArrayList();
        if (classAttr == null) {
            return res;
        }
        StringTokenizer t = new StringTokenizer(classAttr, ",");
        while (t.hasMoreElements()) {
            res.add(t.nextElement());
        }
        return res;
    }
    
    /**
     * We use the system classloader for resolving the class specified
     * in the file attribute.
     * @return Class of the resulting object. If the object has not
     *      been created yet the attribute "factoryClass" is consulted
     * @throws IllegalStateException if something went wrong
     */
    private Class clazz() {
        if (obj != null) {
            return obj.getClass();
        }
        try {
            String classAttr = (String)f.getAttribute("factoryClass"); // NOI18N
            ClassLoader cl = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
            if (classAttr != null) {
                Class c = Class.forName(classAttr, true, cl);
                return c;
            } else {
                throw new IllegalStateException("Attribute factoryClass not specified for " + f); // NOI18N
            }
        } catch (ClassNotFoundException cnfe) {
            IllegalStateException ise = new IllegalStateException(cnfe);
            throw ise;
        }
    }
    
    /**
     * After calling the clazz method newInstance of the resulting
     * class is returned.
     * @throws IllegalStateException if something went wrong
     */
    private Object instantiate() {
        try {
            return clazz().newInstance();
        } catch (InstantiationException is) {
            IllegalStateException ise = new IllegalStateException(is);
            throw ise;
        } catch (IllegalAccessException iae) {
            IllegalStateException ise = new IllegalStateException(iae);
            throw ise;
        }
    }
    
    /**
     * @return Human readable description of the wrapper object.
     */
    public String toString() {
        if (obj != null) {
            return "FactoryWrapper[" + clazz().getName() + "]"; // NOI18N
        }
        return "FactoryWrapper[" + f.getAttribute("factoryClass") + "]"; // NOI18N
    }
    
}
