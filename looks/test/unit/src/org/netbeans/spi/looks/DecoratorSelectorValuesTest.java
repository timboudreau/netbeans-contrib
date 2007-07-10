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

import java.util.*;

import org.netbeans.junit.*;

import org.netbeans.spi.looks.*;

/** Tests whether all vales returned from a Node are identical with
 * the values server by associated look
 */
public class DecoratorSelectorValuesTest extends TestBaseValues {

    // Golden values for the three looks which will be composed

    private GoldenValue goldenValues[][];
    private static GoldenValue gvForTypes[] = GoldenValue.createGoldenValues();

    // Methods of testCase -----------------------------------------------------
    
    public DecoratorSelectorValuesTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite( DecoratorSelectorValuesTest.class );
        return suite;
    }    
    
    protected void setUp() throws Exception {
        
        goldenValues = new GoldenValue[][] {
            CompositeLookValuesTest.createGoldenValues( 1 ),
            CompositeLookValuesTest.createGoldenValues( 2 ),
            CompositeLookValuesTest.createGoldenValues( 3 )
        };
        
        GoldenValue[] resultValues = CompositeLookValuesTest.mergeGoldenValues( goldenValues );
        
        Look look1 = new SampleLook( "CL1", goldenValues[0] );
        Look look2 = new SampleLook( "CL2", goldenValues[1] );
        Look look3 = new SampleLook( "CL3", goldenValues[2] );
        
        // Create look to decorate
        Look primaryLook = Looks.composite( "Composite", new Look[] { look1, look2 } );
        LookSelector primarySelector = Selectors.selector( new SampleProvider( primaryLook ) );
        
        // Create selector which decorates
        LookSelector decoratingSelector = Selectors.decorator( primarySelector, look3 );
        
        // Create the represented object
        SampleRepObject representedObject = new SampleRepObject();
        
        // Find the decorated descriptor in the decorating.selector
        Enumeration de = decoratingSelector.getLooks( representedObject );
        Look decoratingLook = (Look)de.nextElement();
                
        setTarget( decoratingLook, representedObject, 3 );
        setGoldenValues( resultValues );
        
        super.setUp();    
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    // Test methods ------------------------------------------------------------
    
    //////////////////////////////////////////////////////
    //                                                  //
    //  All methods are inherited from: TestBaseValues  //
    //                                                  //
    //////////////////////////////////////////////////////
    
    
    
}


