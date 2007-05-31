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

import org.netbeans.modules.convertor.Accessor;
import org.netbeans.spi.convertor.Convertor;

/**
 *
 * @author  David Konecny
 */

// this class is constructed and assigned to Accessor.DEFAULT
// variable in Convertors static initializer. see there for more.
final class AccessorImpl extends Accessor {

    AccessorImpl() {
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        Convertors.firePropertyChange(propertyName, oldValue, newValue);
    }

    public ConvertorDescriptor createConvertorDescriptor(Convertor convertor, String namespace, String rootElement, String writes) {
        return new ConvertorDescriptor(convertor, namespace, rootElement, writes);
    }
    
}
