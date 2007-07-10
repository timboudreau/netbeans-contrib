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

import org.netbeans.junit.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.looks.LookListener;

import org.openide.util.Lookup;

import org.netbeans.spi.looks.*;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;


/** Tests whether the DefaultLook returns proper values
 */
public class LookSizeTest extends NbTestCase {
    
    // Methods of testCase -----------------------------------------------------
    
    public LookSizeTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite(LookSizeTest.class);
        return suite;
    }    
    
    protected void setUp() throws Exception {               
        super.setUp();    
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    // Test methods ------------------------------------------------------------
    
    /** Test the size of a look when large amount of objects is registered
     */    
    public void testLotOfObjectSize() {
        
        String bigArray[] = createLotOfStrings( 10, 1000 );
        Look look = new SimpleStringLook();
        LookListener listener = new SimpleStringLookListener();
        LookListener listener2 = new SimpleStringLookListener(); 
        
        for ( int i = 0; i < bigArray.length; i++ ) {
            org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( look, bigArray[i], listener );
            //org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( look, bigArray[i], listener2 );
        }
        
        Object[] subtract = bigArray; // Ignore the user data
        assertSize( "Size of the node " , Collections.singleton( look ), 131248, subtract );
                
    }
    
    
    public void testLotOfObjectSizeComposite() {
        
        String bigArray[] = createLotOfStrings( 10, 1000 );
        Look look1 = new SimpleStringLook();
        Look look2 = new SimpleStringLook();
        Look look3 = new SimpleStringLook();
        Look look = Looks.composite( "KAREL", new Look[] { look1, look2, look3 } );
        LookListener listener = new SimpleStringLookListener();
        LookListener listener2 = new SimpleStringLookListener(); 
        
        for ( int i = 0; i < bigArray.length; i++ ) {
            org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( look, bigArray[i], listener );
            //org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( look, bigArray[i], listener2 );
        }
        
        Object[] subtract = bigArray; // Ignore the user data
        // 150000 is just a guess from the test above however it should not 
        // be those 500000 as it is now. To fix the problem we have to
        // separate attachTo calls and addListener calls.
        assertSize( "Size of the node " , Collections.singleton( look ), 150000, subtract );
                
    }
    
    // Helper methods and classes ----------------------------------------------
    
    private static String[] createLotOfStrings( int classes, int count ) {
        
        String[] bigArray = new String[ classes * count ];
        
        for( int i = 0; i < count; i++ ) {
            for( int j = 0; j < classes; j++ ) {
                bigArray[ i * classes + j] =  (char)('A' + j)  + " " + ( i );
            }
        }
        
        return bigArray;
    }
    
    private static class SimpleStringLookListener implements LookListener {
        
        public void change(org.netbeans.modules.looks.LookEvent evt) {
        }
        
        public void propertyChange(org.netbeans.modules.looks.LookEvent evt) {
        }
        
    }
    
    private static class SimpleStringLook extends Look {
        
        SimpleStringLook() {
            super( "SIMPLE_STRING_LOOK" );
        }
        
        public String getName( Object representedObject, Lookup env ) {
            return representedObject.toString();
        }
                
    }
    
    
}
