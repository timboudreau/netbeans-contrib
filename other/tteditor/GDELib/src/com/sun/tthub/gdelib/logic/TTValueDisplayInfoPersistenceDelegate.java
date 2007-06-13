
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
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
