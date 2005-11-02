/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia.
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

