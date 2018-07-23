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

package org.netbeans.api.convertor;

import org.netbeans.spi.convertor.Convertor;

/**
 * Convertor descriptor describes basic capability of the convertor,
 * that is the XML namespace and element name which the convertor
 * is capable to read, and (optional) class name which instances the convertor is
 * capable to persist. Only the instances of the particular Java class
 * will be persisted by the convertor. All subclasses must have their own
 * convertors. For more details about semantics of these attributes see
 * {@link org.netbeans.spi.convertor.Convertor} class Javadoc.
 *
 * @author  David Konecny
 */
public final class ConvertorDescriptor extends Object {
    
    private String namespace;
    private String rootElement;
    private String writes;
    private Convertor convertor;
    
    ConvertorDescriptor(Convertor convertor, String namespace, String rootElement, String writes) {
        assert namespace != null && rootElement != null;
        this.namespace = namespace;
        this.rootElement = rootElement;
        this.writes = writes;
        this.convertor = convertor;
    }

    /**
     * Gets the XML namespace which the convertor can read.
     *
     * @return XML namespace; cannot be null
     */    
    public String getNamespace() {
        return namespace;
    }
    
    /**
     * Gets the root element name which the convertor can read.
     *
     * @return root element name; cannot be null
     */    
    public String getElementName() {
        return rootElement;
    }
    
    /**
     * Gets the fully qualified name of the class which instances
     * the convertor can persist. It can be null what means
     * that convertor cannot persist any class.
     *
     * @return fully qualified name of the class or null if
     * this convertor does not persist any class
     */    
    public String getClassName() {
        return writes;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ConvertorDescriptor)) {
            return false;
        }
        ConvertorDescriptor cd = (ConvertorDescriptor)o;
        return  namespace.equals(cd.namespace) &&
                rootElement.equals(cd.rootElement) &&
                (writes != null ? writes.equals(cd.writes) : cd.writes == null);
    }
   
    public int hashCode() {
        int result = namespace.hashCode();
        result += 13 * rootElement.hashCode();
        if (writes != null) {
            result += 17 * writes.hashCode();
        }
        return result;
    }
    
    public String toString() {
        return "ConvertorDescriptor[namespace='"+namespace+"', element='"+rootElement+"', writes='"+writes+"', convertor='"+convertor+"']"+super.toString(); // NOI18N
    }

    Convertor getConvertor() {
        return convertor;
    }
    
}
