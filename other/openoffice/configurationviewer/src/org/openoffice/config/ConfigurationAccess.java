/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.openoffice.config;

import com.sun.star.beans.PropertyState;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertyState;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XHierarchicalName;
import com.sun.star.container.XNameAccess;
import com.sun.star.lang.NoSupportException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.XInterface;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Access and iterate all configuration values in user's and shared settings.
 *
 * @author S. Aubrecht
 */
class ConfigurationAccess {
    
    private XComponentContext context;
    private XMultiServiceFactory userProvider;
    private XMultiServiceFactory sharedProvider;
    
    /** Creates a new instance of ConfigurationAccess */
    public ConfigurationAccess( XComponentContext context ) {
        this.context = context;
    }
    
    protected XMultiServiceFactory getUserProvider() throws com.sun.star.uno.Exception {
        if( null == userProvider ) {
            // create the provider for user settings
            userProvider = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class,
                    context.getServiceManager().createInstanceWithContext("com.sun.star.configuration.ConfigurationProvider", context));
        }
        return userProvider;
    }
    
    protected XMultiServiceFactory getSharedProvider() throws com.sun.star.uno.Exception {
        if( null == sharedProvider ) {
            // create the provider for shared settings
            sharedProvider = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class,
                    context.getServiceManager().createInstanceWithContext("com.sun.star.configuration.AdministrationProvider", context));
        }
        return sharedProvider;
    }
    
    protected XInterface createConfigView( String path, boolean userConfig ) throws com.sun.star.uno.Exception {
        PropertyValue prop = new PropertyValue();
        
        prop.Name = "nodepath";
        prop.Value = path;
        
        PropertyValue prop2 = new PropertyValue();
        
        prop2.Name = "nocache";
        prop2.Value = Boolean.TRUE;
        
        XMultiServiceFactory provider = userConfig ? getUserProvider() : getSharedProvider();
        
        return (XInterface)provider.createInstanceWithArguments( "com.sun.star.configuration.ConfigurationAccess", new Object[] {prop, prop2} );
    }
    
    protected XInterface createUpdateView( String path ) throws com.sun.star.uno.Exception {
        PropertyValue prop = new PropertyValue();
        
        prop.Name = "nodepath";
        prop.Value = path;
        
        PropertyValue prop2 = new PropertyValue();
        
        prop2.Name = "enableasync";
        prop2.Value = Boolean.FALSE;
        
        XMultiServiceFactory provider = getUserProvider();
        
        return (XInterface)provider.createInstanceWithArguments( "com.sun.star.configuration.ConfigurationUpdateAccess", new Object[] {prop, prop2} );
    }
    
    /**
     * @param path Full path to configuration root node.
     * @return True if the given configuration root path is valid (i.e. contains accessible settings)
     */
    public boolean isValidRootPath( String path ) {
        try {
            createConfigView( path, true );
            return true;
        } catch( Exception e ) {
            //the requested config path probably doesn't exist
            try {
                createConfigView( path, false );
                return true;
            } catch( Exception e2 ) {
                //the requested config path probably doesn't exist
            }
        }
        return false;
    }
    
    /**
     * Iterate all configuration values under the given path with the given processor.
     * @param configurationPath Full path to configuration root node
     * @param processor Configuration processor that will display the value in some format.
     */
    public void browse( String configurationPath, ConfigurationProcessor processor ) {
        
        XInterface userRoot = null;
        try {
            userRoot = createConfigView( configurationPath, true );
        } catch( com.sun.star.uno.Exception ex ) {
            //the configuration path probably doesn't exist
        }
        
        XInterface sharedRoot = null;
        try {
            sharedRoot = createConfigView( configurationPath, false );
        } catch( com.sun.star.uno.Exception ex ) {
            //the configuration path probably doesn't exist
        }
        
        if( null == userRoot && null == sharedRoot )
            return; //nothing to browse here
        
        browse( userRoot, sharedRoot, processor );
        
        if( null != userRoot ) {
            ((XComponent) UnoRuntime.queryInterface(XComponent.class,userRoot)).dispose();
            userRoot = null;
        }
        
        if( null != sharedRoot ) {
            ((XComponent) UnoRuntime.queryInterface(XComponent.class,sharedRoot)).dispose();
            sharedRoot = null;
        }
    }
    
    protected void browse( XInterface userNode, XInterface sharedNode, ConfigurationProcessor processor ) {
        XHierarchicalName elementPath = null;
        
        if( null != userNode )
            elementPath = (XHierarchicalName) UnoRuntime.queryInterface(XHierarchicalName.class, userNode);
        else if( null != sharedNode )
            elementPath = (XHierarchicalName) UnoRuntime.queryInterface(XHierarchicalName.class, sharedNode);
        
        assert null != elementPath;
        
        String path = elementPath.getHierarchicalName();
        
        //call configuration processor object
        processor.processStructuralElement( path, userNode, sharedNode );
        
        //process both user and shared configuration trees in paralel
        XNameAccess userChildAccess = null;
        XNameAccess sharedChildAccess = null;
        XPropertyState userPropertyState = null;
        XPropertyState sharedPropertyState = null;
        String[] userElementNames = null;
        String[] sharedElementNames = null;

        if( null != userNode ) {
            userChildAccess = (XNameAccess) UnoRuntime.queryInterface( XNameAccess.class, userNode );
            userPropertyState = (XPropertyState) UnoRuntime.queryInterface( XPropertyState.class, userNode );
            userElementNames = userChildAccess.getElementNames();
        }

        if( null != sharedNode ) {
            sharedChildAccess = (XNameAccess) UnoRuntime.queryInterface( XNameAccess.class, sharedNode );
            sharedPropertyState = (XPropertyState) UnoRuntime.queryInterface( XPropertyState.class, sharedNode );
            sharedElementNames = sharedChildAccess.getElementNames();
        }
        
        //merge children elements into a single array
        String[] elementNames = null;
        if( null != sharedElementNames && null != userElementNames ) {
            ArrayList<String> mergedNames = new ArrayList<String>( sharedElementNames.length + userElementNames.length );
            for( String n : sharedElementNames ) {
                if( !mergedNames.contains( n ) )
                    mergedNames.add( n );
            }
            for( String n : userElementNames ) {
                if( !mergedNames.contains( n ) )
                    mergedNames.add( n );
            }
            Collections.sort( mergedNames );
            elementNames = mergedNames.toArray( new String[0] );
        } else if( null != sharedElementNames ) {
            elementNames = sharedElementNames;
        } else if( null != userElementNames ) {
            elementNames = userElementNames;
        }
        
        
        // and process them one by one
        for( String childName : elementNames ) {
            Object userChild = null;
            Object sharedChild = null;
            try {
                if( null != userChildAccess && userChildAccess.hasByName( childName ) ) {
                    userChild = userChildAccess.getByName( childName );
                }
                if( null != sharedChildAccess && sharedChildAccess.hasByName( childName ) ) {
                    sharedChild = sharedChildAccess.getByName( childName );
                }
                if( userChild instanceof XInterface && sharedChild instanceof XInterface ) {

                    browse( (XInterface)userChild, (XInterface)sharedChild, processor );

                } else if( userChild instanceof XInterface ) {

                    browse( (XInterface)userChild, null, processor );
                    processValueElements( processor, childName, elementPath, null, null, sharedChild, sharedPropertyState );

                } else if( sharedChild instanceof XInterface ) {

                    browse( null, (XInterface)sharedChild, processor );
                    processValueElements( processor, childName, elementPath, userChild, userPropertyState, null, null );

                } else {

                    processValueElements( processor, childName, elementPath, userChild, userPropertyState, sharedChild, sharedPropertyState );
                }
            } catch( NoSuchElementException nseE ) {
                nseE.printStackTrace();
            } catch( WrappedTargetException wtrE ) {
                wtrE.printStackTrace();
            } catch( com.sun.star.lang.IllegalArgumentException iaE ) {
                iaE.printStackTrace();
            } catch( NoSupportException nsE ) {
                nsE.printStackTrace();
            } catch( UnknownPropertyException upE ) {
                upE.printStackTrace();
            }
        }
    }
    
    private void processValueElements( ConfigurationProcessor processor, String childName, XHierarchicalName elementPath, 
            Object userChild, XPropertyState userPropertyState, 
            Object sharedChild, XPropertyState sharedPropertyState ) throws com.sun.star.lang.IllegalArgumentException, NoSupportException, UnknownPropertyException {
        
        // Build the path to it from the path of
        // the element and the name of the child
        String childPath = elementPath.composeHierarchicalName( childName );

        boolean isUserDefaultValue = true;
        boolean isSharedDefaultValue = true;
        if( null != userPropertyState ) {
            PropertyState state = userPropertyState.getPropertyState( childName );
            isUserDefaultValue = PropertyState.DEFAULT_VALUE.equals( state );
        }
        if( null != sharedPropertyState ) {
            PropertyState state = sharedPropertyState.getPropertyState( childName );
            isSharedDefaultValue = PropertyState.DEFAULT_VALUE.equals( state );
        }
        // and process the value
        processor.processValueElement( childPath, userChild, isUserDefaultValue, sharedChild, isSharedDefaultValue );
    }
}
