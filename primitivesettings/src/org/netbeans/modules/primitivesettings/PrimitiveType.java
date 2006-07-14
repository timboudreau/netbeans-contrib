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

package org.netbeans.modules.primitivesettings;

import java.lang.reflect.Method;
import org.openide.util.Utilities;

/**
 *
 * @author vita
 */
public enum PrimitiveType {
    NULL("nullValue", null), //NOI18N
    BOOLEAN("boolValue", Boolean.class), //NOI18N
    BYTE("byteValue", Byte.class), //NOI18N
    CHAR("charValue", Character.class), //NOI18N
    DOUBLE("doubleValue", Double.class), //NOI18N
    FLOAT("floatValue", Float.class), //NOI18N
    INT("intValue", Integer.class), //NOI18N
    LONG("longValue", Long.class), //NOI18N
    SHORT("shortValue", Short.class), //NOI18N
    STRING("stringValue", String.class); //NOI18N
    
    private String typeName;
    private Class typeClass;
    
    private PrimitiveType(String typeName, Class typeClass) {
        this.typeName = typeName;
        this.typeClass = typeClass;
    }

    public String getTypeName() {
        return typeName;
    }
    
    public Class getTypeClass() {
        return typeClass;
    }

    public Object createFromString(String s) {
        if (typeClass == null) {
            return null;
        } else if (typeClass == Character.class) {
            return s.length() == 0 ? null : s.charAt(0);
        } else if (typeClass == String.class) {
            return s;
        } else {
            try {
                Method valueOf = typeClass.getDeclaredMethod("valueOf", String.class); //NOI18N
                return valueOf.invoke(null, s);
            } catch (Exception e) {
                throw new IllegalStateException("Can't convert string '" + s + 
                        "' to " + typeClass.getName(), e); //NOI18N
            }
        }
    }
    
    public static PrimitiveType findPrimitiveType(String typeName) {
        assert typeName != null : "The typeName parameter must not be null"; //NOI18N
        
        for (PrimitiveType t : PrimitiveType.values()) {
            if (typeName.equalsIgnoreCase(t.typeName)) {
                return t;
            }
        }
        
        return null;
    }
    
    public static PrimitiveType findPrimitiveType(Class typeClass) {
        for (PrimitiveType t : PrimitiveType.values()) {
            if (Utilities.compareObjects(typeClass, t.typeClass)) {
                return t;
            }
        }
        
        return null;
    }
}
