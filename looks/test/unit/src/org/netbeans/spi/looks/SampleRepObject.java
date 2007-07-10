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

package org.netbeans.spi.looks;

import org.netbeans.spi.looks.GoldenValue;

import java.beans.*;
import java.util.Hashtable;

/** Sample represented object which can tell whether some methods
 * were called
 */
public class SampleRepObject {

    private int attachCalled;
    private int detachCalled;
    private int setNameCalled;
    private int destroyCalled;

    private Hashtable properties = new Hashtable();

    // Special propert for testing destroy events
    // see SampleLook.TestSubstitute
    public static final String DESTROY = "Destroy";
    
    private GoldenValue[] goldenValues;
    
    private PropertyChangeSupport propertyChangeSupport;
    
    public SampleRepObject() {}
    
    public SampleRepObject( GoldenValue[] goldenValues ) {
        this.goldenValues = goldenValues;
    }
    
    // Methods for testin calls to void methods --------------------------------
    
    public void attach() {
        attachCalled++;
    }
    
    public void detach() {
        detachCalled++;
    }
    
    public void setName() {
        setNameCalled++;
    }
    
    public void destroy() {
        destroyCalled++;
    }
   
    public int getAttachCalled() {
        int r = attachCalled;
        attachCalled = 0;
        return r;
    }
    
    public int getDetachCalled() {
        int r = detachCalled;
        detachCalled = 0;
        return r;
    }
    
    public int getSetNameCalled() {
        int r = setNameCalled;
        setNameCalled = 0;
        return r;
    }
                        
    public int getDestroyCalled() {
        int r = destroyCalled;
        destroyCalled = 0;
        return r;
    }
    
    // Method for properties ---------------------------------------------------
    
    public Object getProperty( String key ) {
        return properties.get( key );
    }
    
    public void setProperty( String key, Object o ) {
        Object oldValue = getProperty( key );
        properties.put( key, o );
        
        if ( propertyChangeSupport != null ) {
            propertyChangeSupport.firePropertyChange( key, oldValue, o );
        }
    }

    // Golden value methods ----------------------------------------------------
    
    public Object getValue( long key ) {
        
        if ( goldenValues == null ) {
            return null;
        }
        
        for( int i = 0; i < goldenValues.length; i++ ) {
            if ( key == goldenValues[i].key ) {
                return goldenValues[i].result;
            }
        }
        return null;
    }
    
    public void setValue( long key, Object value ) {
        
        if ( goldenValues == null ) {
            goldenValues = new GoldenValue[] {
                new GoldenValue( key, value  )
            };
            return;
        }
        
        // Try to find the key
        int index = -1;
        
        for( int i = 0; i < goldenValues.length; i++ ) {
            if ( key == goldenValues[i].key ) {
                index = i;
            }
        }
        
        Object oldValue = null;
        
        // If not found create new array
        if ( index == -1 ) {
            GoldenValue[] newGvs = new GoldenValue[ goldenValues.length + 1 ];
            index = goldenValues.length;
            System.arraycopy( goldenValues, 0, newGvs, 0, goldenValues.length );
            goldenValues = newGvs;
        }
        else {
            oldValue = goldenValues[index].result;
        }
        
        goldenValues[index] = new GoldenValue( key, value );
        
        if ( propertyChangeSupport != null ) {
            propertyChangeSupport.firePropertyChange( 
                Long.toString( goldenValues[index].key ),
                oldValue, 
                value );
            
            
        }
        
        
    }    
    
    // Regisatration of listeners ----------------------------------------------
    
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        if ( propertyChangeSupport == null ) {
            propertyChangeSupport = new PropertyChangeSupport( this );
        }
        
        propertyChangeSupport.addPropertyChangeListener( listener );
    }
    
    
    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        if ( propertyChangeSupport == null ) {
            return;
        }
        
        propertyChangeSupport.removePropertyChangeListener( listener );
    }
    
}

 