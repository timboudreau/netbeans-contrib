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
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

import org.openide.util.Lookup;

import org.netbeans.spi.looks.*;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.actions.NodeAction;


/** Tests whether the DefaultLook returns proper values
 */
public class DefaultLookValuesTest extends TestBaseValues {

    // Methods of testCase -----------------------------------------------------
    
    public DefaultLookValuesTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite(DefaultLookValuesTest.class);
        return suite;
    }    
    
    protected void setUp() throws Exception {       
        
        Look look = createLook();
        setTarget( look, new SampleRepObject(), 1 );
        setGoldenValues( createGoldenValues() );

        super.setUp();    
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    // Test methods ------------------------------------------------------------
    
    /////////////////////////////////////////////////////////////////
    //                                                             //
    //  All methods except one are inherited from: TestBaseValues  //
    //                                                             //
    /////////////////////////////////////////////////////////////////

    public static final class MyAction extends NodeAction {
        protected void performAction(Node[] activatedNodes) {
        }

        protected boolean enable(Node[] activatedNodes) {
            return false;
        }

        public String getName() {
            return getClass().getName();
        }

        public HelpCtx getHelpCtx() {
            return HelpCtx.DEFAULT_HELP;
        }

    }
      
    public void testGetActions() throws Exception {

        Action[] actions = look.getActions( representedObject, NO_LOOKUP );
        
        if ( actions != null ) {
            fail( MSSG_UNEXPECTED_VALUE_RETURNED + actions );
        }
        
        String folderName = "Looks/Actions/" + look.getClass().getName().replace( '.', '/' ) + 
            "/" + MyAction.class.getName().replace('.', '-') + ".instance";
        
        FileObject fo = FileUtil.createData (
            Repository.getDefault ().getDefaultFileSystem().getRoot(),
            folderName
        );
        
        actions = look.getActions ( representedObject, NO_LOOKUP );
        
        assertNotNull ("Not null", actions);
        assertEquals ("One action", actions.length, 1);
        assertTrue ("NewAction", actions[0] instanceof MyAction);
        
        fo.delete ();
    }
    
    public void testIconBase() throws Exception {
        fail( "The test case empty" );
    }
    
    public void testActionBase() throws Exception {
        fail( "The test case empty" );
    }
    
    // Private helper methods --------------------------------------------------
    
    private static GoldenValue[] createGoldenValues() {
        return new GoldenValue[] {
            new GoldenValue( ProxyLook.GET_DISPLAY_NAME, null ),
            new GoldenValue( ProxyLook.GET_NAME, null ),
            // new GoldenValue( ProxyLook.RENAME )
            new GoldenValue( ProxyLook.GET_SHORT_DESCRIPTION, null ),
            new GoldenValue( ProxyLook.GET_ICON, null ),
            new GoldenValue( ProxyLook.GET_OPENED_ICON, null ),
            new GoldenValue( ProxyLook.GET_HELP_CTX, null ),
            new GoldenValue( ProxyLook.GET_CHILD_OBJECTS, null ),
            new GoldenValue( ProxyLook.GET_NEW_TYPES, null ),
            new GoldenValue( ProxyLook.GET_ACTIONS, null ),
            new GoldenValue( ProxyLook.GET_CONTEXT_ACTIONS, null ),
            new GoldenValue( ProxyLook.GET_DEFAULT_ACTION, null ),
            new GoldenValue( ProxyLook.GET_PROPERTY_SETS, null ),
            new GoldenValue( ProxyLook.GET_CUSTOMIZER, null ),
            new GoldenValue( ProxyLook.HAS_CUSTOMIZER, Boolean.FALSE ),
            new GoldenValue( ProxyLook.CAN_RENAME, Boolean.FALSE ),
            new GoldenValue( ProxyLook.CAN_DESTROY, Boolean.FALSE ),
            new GoldenValue( ProxyLook.CAN_COPY, Boolean.FALSE ),
            new GoldenValue( ProxyLook.CAN_CUT, Boolean.FALSE ),
            new GoldenValue( ProxyLook.GET_PASTE_TYPES, null ),
            new GoldenValue( ProxyLook.GET_DROP_TYPE, null ),
            new GoldenValue( ProxyLook.CLIPBOARD_COPY, null ),
            new GoldenValue( ProxyLook.CLIPBOARD_CUT, null ),
            new GoldenValue( ProxyLook.DRAG, null ),
            new GoldenValue( ProxyLook.GET_LOOKUP_ITEMS, null )
            // new GoldenValue( ProxyLook.DESTROY )    
        };
    }
    
    private static Look createLook() {
        return new DefaultLook( "DefaultLookTest" ) {
                        
            public void attachTo( Object representedObject ) {
                super.attachTo( representedObject );                
                
                if ( representedObject instanceof SampleRepObject ) {
                    ((SampleRepObject)representedObject).attach();
                }
                // return new Look.NodeSubstitute( representedObject, this, null / PENDING / );        

            }
            
            public void rename(Object representedObject, String newName, Lookup env ) {
                if ( representedObject instanceof SampleRepObject ) {
                    ((SampleRepObject)representedObject).setName();
                }
            }
                        
            public void destroy(Object representedObject, Lookup env ) throws IOException {
               if ( representedObject instanceof SampleRepObject ) {
                    ((SampleRepObject)representedObject).destroy();
                }
            }            
        };
    }
}
