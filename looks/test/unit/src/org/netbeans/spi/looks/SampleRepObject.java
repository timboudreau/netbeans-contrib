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

 