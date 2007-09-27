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
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
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

