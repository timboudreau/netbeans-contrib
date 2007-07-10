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

import org.netbeans.spi.looks.*;


/** Tests whether the DefaultLook returns proper values
 */
public class ProxyLookValuesTest extends TestBaseValues {

    // Methods of testCase -----------------------------------------------------

    public ProxyLookValuesTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite(ProxyLookValuesTest.class);
        return suite;
    }    
    
    protected void setUp() throws Exception {       
        
        Look look = createLook();
        setGoldenValues( GoldenValue.createGoldenValues() );
        setTarget( look, new SampleRepObject( goldenValues ), 1 );
        
        super.setUp();    
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    // Test methods ------------------------------------------------------------
    
    //////////////////////////////////////////////////
    //                                              //
    //  All methods inherited from: TestBaseValues  //
    //                                              //
    //////////////////////////////////////////////////
      
    
    // Private helper methods --------------------------------------------------
    
    static Look createLook() {
        
        return new ProxyLook( "ProxyLookValuesTestLook", Selectors.singleton( new SampleLook( "Test" ) ) ) {
                                               
            public String getDisplayName() {
                return getName();
            }
        };
    }
    
    
    
}


