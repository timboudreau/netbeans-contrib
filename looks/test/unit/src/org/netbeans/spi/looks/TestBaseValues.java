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

import java.util.*;
import java.io.IOException;
import java.beans.BeanInfo;



import org.openide.nodes.*;
import org.openide.util.Lookup;

import org.netbeans.spi.looks.*;

import org.netbeans.api.nodes2looks.TestUtil;
import org.netbeans.junit.*;

/** This class is a test base for all test for returning right
 * values from a look
 */

public class TestBaseValues extends NbTestCase {

    // The look to be tested
    protected Look look;

    // The node to be tested
    protected Node node;

    // If testing on Node the representedObject has to be set
    protected SampleRepObject representedObject;
    
    // Array with golden items
    protected GoldenValue[] goldenValues;
    
    // Nuber of expected calls to attachTo, rename etc.;
    protected int expectedCallsCount;
    
    // Message for unexpected value 
    protected static final String MSSG_UNEXPECTED_VALUE_RETURNED =
        "Unexpected value returned.";
    
    // Message for unexpected value 
    protected static final String MSSG_METHOD_NOT_CALLED =
        "Method not called.";
    
    // Message for the case when an exception is thrown
    protected static final String MSSG_EXCEPTION_THROWN =
        "Exception thrown.";

    protected static final Lookup NO_LOOKUP = null; // PENDING

    
    

    // Methods of testCase -----------------------------------------------------
    
    public TestBaseValues(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite( TestBaseValues.class );
        
        return suite;
    }    
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    protected void tearDown() throws Exception {
        look = null;        
        super.tearDown();
    }
    
    // Methods for setting up the test case ------------------------------------
    
    protected void setTarget( Look look, SampleRepObject representedObject, int expectedCallsCount ) {
        this.look = look;
        this.node = null;
        this.representedObject = representedObject;
        this.expectedCallsCount = expectedCallsCount;
        org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( look, representedObject, null );        
    }
    
    protected void setTarget( Node node, SampleRepObject representedObject, int expectedCallsCount ) {
        this.look = null;
        this.node = node;
        this.representedObject = representedObject;
        this.expectedCallsCount = expectedCallsCount;
    }
    
    protected void setGoldenValues( GoldenValue[] goldenValues ) {
        this.goldenValues = goldenValues;
    }
    
    protected boolean onNode() {
        return node != null;
    }
    
    // Test methods ------------------------------------------------------------
    
    public void testAttachTo() {
        if ( onNode() ) {
            assertEquals ( MSSG_METHOD_NOT_CALLED, expectedCallsCount,
                representedObject.getAttachCalled() );
        }
        else {
            assertEquals ( MSSG_METHOD_NOT_CALLED, expectedCallsCount,
                ((SampleRepObject)representedObject).getAttachCalled() );
        }
    }
    
    // Methods for FUNCTIONALITY EXTENSIONS ------------------------------------
        
    public void testGetLookupValues() {
        if ( onNode() ) {
            Lookup lookup = node.getLookup();
            Lookup.Result result = lookup.lookup( new Lookup.Template( Object.class ) );
            Collection items = new ArrayList( result.allItems() ); // Make it modifyable
            
            // We need to remove the node itself
            
            Object nodeItself = null;
            
            for( Iterator it = items.iterator(); it.hasNext(); ) {
                Lookup.Item item = (Lookup.Item)it.next();
                if ( item.getInstance() == node ) {
                    nodeItself = item;
                    break;
                }
            }
            
            assertNotNull( "Lookup should contain the node itself ", nodeItself );
            items.remove( nodeItself );            
            
            assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                            GoldenValue.isOK(
                                ProxyLook.GET_LOOKUP_ITEMS,
                                items,
                                goldenValues ) );
        }
        else {
            assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                            GoldenValue.isOK(
                                ProxyLook.GET_LOOKUP_ITEMS,
                                look.getLookupItems( representedObject, Lookup.EMPTY ),
                                goldenValues ) );
        }
    }
    
    // Methods for STYLE -------------------------------------------------------
    
    public void testGetDisplayName() {        
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_DISPLAY_NAME,
                            onNode() ? node.getDisplayName() : 
                                       look.getDisplayName( representedObject, NO_LOOKUP ),
                            goldenValues ) );
        
    }
    
    public void testGetName() {        
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_NAME,
                            onNode() ? node.getName() : 
                                       look.getName( representedObject, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    public void testSetName() throws IOException  {
        if ( onNode() ) {
           node.setName( "New name" );
           if ( onNode() ) {
            assertEquals( MSSG_METHOD_NOT_CALLED, expectedCallsCount,
                representedObject.getSetNameCalled() );
        }
        }
        else {
           look.rename( representedObject, "New name", NO_LOOKUP );
           assertEquals ( MSSG_METHOD_NOT_CALLED, expectedCallsCount,
               ((SampleRepObject)representedObject).getSetNameCalled() );
        }
        
    }

    public void testGetShortDescription() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_SHORT_DESCRIPTION,
                            onNode() ? node.getShortDescription() : 
                                       look.getShortDescription( representedObject, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    public void testGetIcon() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_ICON,
                            onNode() ? node.getIcon( BeanInfo.ICON_COLOR_16x16 ) :
                                       look.getIcon( representedObject, BeanInfo.ICON_COLOR_16x16, NO_LOOKUP ),
                            goldenValues ) );
    
    }
    
    public void testGetOpenedIcon() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_OPENED_ICON,
                            onNode() ? node.getOpenedIcon( BeanInfo.ICON_COLOR_16x16 ) :
                                       look.getOpenedIcon( representedObject, BeanInfo.ICON_COLOR_16x16, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    public void testGetHelpCtx() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_HELP_CTX,
                            onNode() ? node.getHelpCtx() : 
                                       look.getHelpCtx( representedObject, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    // Methods for CHILDREN ----------------------------------------------------
        
    public void testGetChildObjects() {
        
        if ( onNode() ) {

            Node[] nodes = node.getChildren().getNodes();
            List gv = (List)GoldenValue.get( ProxyLook.GET_CHILD_OBJECTS, goldenValues );
            
            if ( gv == null ) {
                fail( "Golden value is invalid" );
            }
            if ( gv.size() != nodes.length ) {
                fail( MSSG_UNEXPECTED_VALUE_RETURNED );
            }
            
            
            for( int i = 0; i < nodes.length; i++ ) {
                
                Node n = nodes[i];
                if ( TestUtil.getRepresentedObject( n ) != gv.get(i) &&
                    !TestUtil.getRepresentedObject( n ).equals( gv.get(i) ) ) {
                    fail( MSSG_UNEXPECTED_VALUE_RETURNED + "on index : " + i );
                }
            }            
            
        }
        else {
            assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                            GoldenValue.isOK(
                                ProxyLook.GET_CHILD_OBJECTS,
                                look.getChildObjects( representedObject, NO_LOOKUP ),
                                goldenValues ) );
        }
    }
    
    // Methods for ACTIONS & NEW TYPES -----------------------------------------
    
    public void testGetNewTypes() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_NEW_TYPES,
                            onNode() ? node.getNewTypes() : 
                                       look.getNewTypes( representedObject, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    public void testGetActions() throws Exception {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_ACTIONS,
                            onNode() ? node.getActions() : 
                                       look.getActions( representedObject, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    public void testGetContextActions() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_CONTEXT_ACTIONS,
                            onNode() ? node.getContextActions() : 
                                       look.getContextActions( representedObject, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    public void testGetDefaultAction() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_DEFAULT_ACTION,
                            onNode() ? node.getDefaultAction() : 
                                       look.getDefaultAction( representedObject, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    // Methods for PROPERTIES AND CUSTOMIZER -----------------------------------
    
    public void testGetPropertySets() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_PROPERTY_SETS,
                            onNode() ? node.getPropertySets() : 
                                       look.getPropertySets( representedObject, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    public void testGetCustomizer() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_CUSTOMIZER,
                            onNode() ? node.getCustomizer() : 
                                       look.getCustomizer( representedObject, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    public void testHasCustomizer() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.HAS_CUSTOMIZER,
                            onNode() ? new Boolean( node.hasCustomizer() ) :
                                       new Boolean( look.hasCustomizer( representedObject, NO_LOOKUP ) ),
                            goldenValues ) );
    }
    
    
    // Methods for CLIPBOARD OPERATIONS ----------------------------------------
    
    public void testCanRename() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.CAN_RENAME,
                            onNode() ? new Boolean( node.canRename() ) :
                                       new Boolean( look.canRename( representedObject, NO_LOOKUP ) ),
                            goldenValues ) );
    }
    
    public void testCanDestroy() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.CAN_DESTROY,
                            onNode() ? new Boolean( node.canDestroy() ) :
                                       new Boolean( look.canDestroy( representedObject, NO_LOOKUP ) ),
                            goldenValues ) );
    }
    
    public void testCanCopy() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.CAN_COPY,
                            onNode() ? new Boolean( node.canCopy() ) :
                                       new Boolean( look.canCopy( representedObject, NO_LOOKUP ) ),
                            goldenValues ) );
    }
    
    public void testCanCut() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.CAN_CUT,
                            onNode() ? new Boolean( node.canCut() ) :
                                       new Boolean( look.canCut( representedObject, NO_LOOKUP ) ),
                            goldenValues ) );
    }
    
    public void testGetPasteTypes() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_PASTE_TYPES,
                            onNode() ? node.getPasteTypes( null ) : 
                                       look.getPasteTypes( representedObject, null, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    public void testGetDropType() {
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_DROP_TYPE,
                            onNode() ? node.getDropType( null, 0, 0 ) : 
                                       look.getDropType( representedObject, null, 0, 0, NO_LOOKUP ),
                            goldenValues ) );
    }
    
    public void testClipboardCopy() {
        try {
            assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.CLIPBOARD_COPY,
                            onNode() ? node.clipboardCopy() : 
                                       look.clipboardCopy( representedObject, NO_LOOKUP ),
                            goldenValues ) );
        }
        catch ( java.io.IOException e ) {
            fail ( MSSG_EXCEPTION_THROWN );
        }
    }
    
    public void testClipboardCut() {
        try {
            assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.CLIPBOARD_CUT,
                            onNode() ? node.clipboardCut() : 
                                       look.clipboardCut( representedObject, NO_LOOKUP ),
                            goldenValues ) );
        }
        catch ( java.io.IOException e ) {
            fail ( MSSG_EXCEPTION_THROWN );
        }
        
    }
    
    public void testDrag() {
        try {
            assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.DRAG,
                            onNode() ? node.drag() : 
                                       look.drag( representedObject, NO_LOOKUP ),
                            goldenValues ) );
        }
        catch ( java.io.IOException e ) {
            fail ( MSSG_EXCEPTION_THROWN );
        }
    }
    
    public void testDestroy() {
        try {
            if ( onNode() ) {
                node.destroy();
                assertEquals ( MSSG_METHOD_NOT_CALLED, expectedCallsCount,
                    representedObject.getDestroyCalled() );
            }
            else {
                look.destroy( representedObject, NO_LOOKUP );
                assertEquals ( MSSG_METHOD_NOT_CALLED, expectedCallsCount,
                    ((SampleRepObject)representedObject).getDestroyCalled() );
            }
                        
        }
        catch ( java.io.IOException e ) {
            fail ( MSSG_EXCEPTION_THROWN );
        }
    }
    
    
    
}
