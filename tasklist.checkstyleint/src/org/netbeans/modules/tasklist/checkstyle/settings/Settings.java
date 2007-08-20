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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.tasklist.checkstyle.settings;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.prefs.Preferences;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 *
 * @author S. Aubrecht
 */
final public class Settings {
    
    public static final String PROP_CONFIG_URL = "configUrl"; //NOI18N

    private static Settings theInstance;

    private PropertyChangeSupport propertySupport;
    
    /** Creates a new instance of Settings */
    private Settings() {
    }
    
    public static final Settings getDefault() {
        if( null == theInstance )
            theInstance = new Settings();
        return theInstance;
    }
    
    public boolean isExtensionSupported( String fileExtension ) {
        return "JAVA".compareToIgnoreCase( fileExtension ) == 0;
    }
    
    public void addPropertyChangeListener( PropertyChangeListener l ) {
        if( null == propertySupport )
            propertySupport = new PropertyChangeSupport( this );
        propertySupport.addPropertyChangeListener( l );
    }
    
    public void removePropertyChangeListener( PropertyChangeListener l ) {
        if( null != propertySupport )
            propertySupport.removePropertyChangeListener( l );
    }
    
    private Preferences getPreferences() {
        return NbPreferences.forModule( Settings.class );
    }
    
    public String getConfigurationUrl() {
        return getPreferences().get("configUrl", getDefaultConfigurationUrl() ); //NOI18N
    }
    
    private String getDefaultConfigurationUrl() {
        ClassLoader cl = Lookup.getDefault().lookup( ClassLoader.class );
        URL url = cl.getResource("org/netbeans/modules/tasklist/checkstyle/sun_checks.xml"); //NOI18N
        return url.toExternalForm();
    }
    
    public void setConfigurationUrl( String newUrl ) {
        String oldVal = getConfigurationUrl();
        getPreferences().put( "configUrl", newUrl ); //NOI18N
        if( null != propertySupport )
            propertySupport.firePropertyChange( PROP_CONFIG_URL, oldVal, newUrl );
    }
}
