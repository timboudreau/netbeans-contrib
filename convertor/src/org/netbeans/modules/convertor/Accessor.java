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

package org.netbeans.modules.convertor;

import org.netbeans.api.convertor.ConvertorDescriptor;
import org.netbeans.api.convertor.Convertors;
import org.netbeans.spi.convertor.Convertor;

/**
 *
 * @author  David Konecny
 */
public abstract class Accessor {

    public static Accessor DEFAULT;

    // force loading of ConvertorDescriptor class. That will set DEFAULT varible.
    static {
        Class c = Convertors.class;
        try {
            Class.forName(c.getName(), true, c.getClassLoader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    abstract public void firePropertyChange(String propertyName, Object oldValue, Object newValue);
    
    abstract public ConvertorDescriptor createConvertorDescriptor(Convertor convertor, String namespace, String rootElement, String writes);
}
