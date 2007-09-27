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

package org.netbeans.modules.rmi.registry;

import java.rmi.server.*;
import java.text.MessageFormat;

import org.openide.nodes.*;

/**
 *
 * @author  mryzl
 */

public class ServiceItem extends Object implements Comparable, Node.Cookie {

    /** A format for toString() method. */
    public static final String FMT_NAME = "{0}[class={1}]"; // NOI18N

    /** Name of the service. */
    private String name;

    /** Class of the service. */
    private Class clazz;

    /** Creates new ServiceItem. */
    public ServiceItem(String name, Class clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    /** Getter for name.
    * @return name
    */
    public String getName() {
        return name;
    }

    /** Getter for class.
    * @return class
    */
    public Class getServiceClass() {
        return clazz;
    }

    /** Get class annotation - codebase where the class was downloaded from.
    * @return annotation
    */
    public String getClassAnnotation() {
        Class clazz;
        if ((clazz = getServiceClass()) != null) {
            String ca = RMIClassLoader.getClassAnnotation(clazz);
            if (ca == null) {
                try {
                    java.security.ProtectionDomain pd = clazz.getProtectionDomain();
                    ca = pd.getCodeSource().getLocation().toString();
                } catch (SecurityException ex) {
                    // prohibited by SM
                }
            }
            return ca;
        }
        return null;
    }

    /** Equals.
    * @return true if names and classes are equal.
    */
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof ServiceItem)) {
            ServiceItem item = (ServiceItem) obj;
            if (item.getName().equals(name)) {
                return (clazz == null) ? item.getServiceClass() == null : clazz.equals(item.getServiceClass());
            }
        }
        return false;
    }

    /** toString
    */
    public String toString() {
        return MessageFormat.format(FMT_NAME, new Object[] { getName(), getServiceClass()});
    }

    public int compareTo(final java.lang.Object p1) {
        return ((ServiceItem)p1).getName().compareTo(getName());
    }
}

/*
* <<Log>>
*  5    Gandalf-post-FCS1.3.1.0     3/20/00  Martin Ryzl     localization
*  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  3    Gandalf   1.2         8/30/99  Martin Ryzl     saving corrected
*  2    Gandalf   1.1         8/27/99  Martin Ryzl     equals changed
*  1    Gandalf   1.0         8/27/99  Martin Ryzl     
* $ 
*/ 
