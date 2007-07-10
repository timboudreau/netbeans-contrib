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

package org.netbeans.api.nodes2looks;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;

import org.openide.nodes.*;
import org.netbeans.junit.*;
import org.openide.util.Lookup;

import org.netbeans.api.nodes2looks.Nodes;
import org.netbeans.spi.looks.*;

/** Tests whether all vales returned from a Node are identical with
 * the values server by associated look
 */
public class LookNodeValuesTest extends TestBaseValues {

    // Methods of testCase -----------------------------------------------------

    public LookNodeValuesTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite(LookNodeValuesTest.class);
        return suite;
    }    
    
    protected void setUp() throws Exception {
        
        Look look = new SampleLook( "LookNodeValesTestLook" );
        LookSelector selector = Selectors.selector( new SampleProvider( look ) );
        GoldenValue[] goldenValues = GoldenValue.createGoldenValues();
        SampleRepObject ro = new SampleRepObject( goldenValues );
        Node node = Nodes.node( ro, null, selector );
        setTarget( node, ro, 1 );
        setGoldenValues( goldenValues );

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
    
    // Helper methods --------------------------------------------------


}


