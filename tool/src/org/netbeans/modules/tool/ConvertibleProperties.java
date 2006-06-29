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
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.tool;

import java.util.Iterator;
import java.util.Properties;

import org.netbeans.spi.convertor.SimplyConvertible;

/**
 * Supports to serialize properties as an xml file via the Registry API.
 *
 * @author John Stuwe
 * @since SP 5
 */
public class ConvertibleProperties
             implements SimplyConvertible
{
    //=======================================================================
    // Private data members

    //
    // The properties
    //
    private Properties myProperties;


    //=======================================================================
    // Public methods
      
    /**
     * Creates new <tt>ConvertibleProperties</tt>. The non-argument
     * constructor is mandatory for the {@link SimplyConvertible}
     * interface.
     */
    public ConvertibleProperties(  )
    {
        myProperties = new Properties(  );
    }

    /**
     * Adds the given property.
     * 
     * @param property The property name.
     * @param value The value
     */
    public void putProperty( String property, String value )
    {
        myProperties.setProperty( property, value );
    }

    /**
     * Returns the value of the given property.
     * 
     * @param property The property name.
     * 
     * @return The value.
     */
    public String getProperty( String property )
    {
        return myProperties.getProperty( property );
    }


    /**
     * @inheritdoc
     */
    public final void read( Properties settings )
    {
        Iterator keys = settings.keySet(  ).iterator(  );

        while( keys.hasNext(  ) )
        {
            String key = (String)keys.next(  );
            String value = settings.getProperty( key );
            putProperty( key, value );
        }
    }


    /**
     * @inheritdoc
     */
    public final void write( Properties settings )
    {
        Iterator keys = myProperties.keySet(  ).iterator(  );

        while( keys.hasNext(  ) )
        {
            String key = (String)keys.next(  );
            String value = myProperties.getProperty( key );
            settings.setProperty( key, value );
        }
    }
}

