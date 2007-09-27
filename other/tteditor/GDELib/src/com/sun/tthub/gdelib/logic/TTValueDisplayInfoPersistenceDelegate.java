
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */

package com.sun.tthub.gdelib.logic;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @author choonyin
 */
public class TTValueDisplayInfoPersistenceDelegate extends PersistenceDelegate {
    
    /** Creates a new instance of TTValueDisplayInfoPersistenceDelegate */
    public TTValueDisplayInfoPersistenceDelegate() {
    }
      protected boolean mutatesTo( Object oldInstance, Object newInstance ) {
        return oldInstance == newInstance;
    }

    protected Expression instantiate( Object oldInstance, Encoder out ) {
        Class type = oldInstance.getClass();
        if ( !Modifier.isPublic( type.getModifiers() ) )
            throw new IllegalArgumentException( "Could not instantiate instance of non-public class: " + oldInstance );

        for ( Field field : type.getFields() ) {
            int mod = field.getModifiers();
            if ( Modifier.isPublic( mod ) && Modifier.isStatic( mod ) && Modifier.isFinal( mod ) && ( type == field.getDeclaringClass() ) ) {
                try {
                    if ( oldInstance == field.get( null ) )
                        return new Expression( oldInstance, field, "get", new Object[]{null} );
                } catch ( IllegalAccessException exception ) {
                    throw new IllegalArgumentException( "Could not get value of the field: " + field, exception );
                }
            }
        }
        throw new IllegalArgumentException( "Could not instantiate value: " + oldInstance );
    }

}
