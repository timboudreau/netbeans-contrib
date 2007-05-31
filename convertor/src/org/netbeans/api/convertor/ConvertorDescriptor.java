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
