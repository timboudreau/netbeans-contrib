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

import org.openide.nodes.*;
import org.netbeans.junit.*;

import org.netbeans.api.nodes2looks.Nodes;
import org.netbeans.spi.looks.*;

public class ProxyLookEventsWithDelegateObjectTest extends TestBaseEvents {

    // Methods of testCase -----------------------------------------------------

    public ProxyLookEventsWithDelegateObjectTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite( ProxyLookEventsWithDelegateObjectTest.class );
        return suite;
    }
    
    protected void setUp() throws Exception {
                
        Look sampleLook = createObjectDelegatingLook();
        LookSelector sampleSelector = Selectors.selector( new SampleProvider( sampleLook ) );
        GoldenValue[] goldenValues = GoldenValue.createGoldenValues();
        SampleDelegatingRepObject representedObject = new SampleDelegatingRepObject( goldenValues );
        Node lookNode = Nodes.node( representedObject, sampleLook, sampleSelector );
        
        setTarget( lookNode, (SampleRepObject)representedObject.delegate );
        setGoldenValues( goldenValues );
        
        super.setUp();
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
        
    
    // Test methods ------------------------------------------------------------        
    
    ///////////////////////////////////////////////////////
    //                                                   //
    //  Most methods are inherited from: TestBaseEvents  //
    //                                                   //
    ///////////////////////////////////////////////////////
    
    
    // Helper methods and classes ----------------------------------------------
    
    static Look createObjectDelegatingLook() {
        
        return new DelegatingProxyLook( "ProxyLookValuesTestLook", Selectors.singleton( new SampleLook( "Test" ) ) );
       
    }
    
    private static class DelegatingProxyLook extends ProxyLook {
        
        public DelegatingProxyLook( String name, LookSelector selector ) {
            super( name, selector );
        }
        
        protected Object delegateObject (Object representedObject) {
            return ((SampleDelegatingRepObject)representedObject).delegate;
        }
        
        protected Object undelegateObject( Object delegate ) {
            return ((SampleRepObjectDelegate)delegate).original;
        }
        
    }
    
    private static class SampleDelegatingRepObject extends SampleRepObject {
        
        final Object delegate;
        
        SampleDelegatingRepObject( GoldenValue[] goldenValues ) {
            super( goldenValues );
            delegate = new SampleRepObjectDelegate( this, GoldenValue.createGoldenValues() );
        }
    }
    
    private static class SampleRepObjectDelegate extends SampleRepObject {
        
        final Object original;
        
        SampleRepObjectDelegate( Object original, GoldenValue[] goldenValues ) {            
            super( goldenValues );
            this.original = original;
        }
        
    }
    
}

