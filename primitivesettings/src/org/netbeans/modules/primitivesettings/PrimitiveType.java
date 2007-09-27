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
