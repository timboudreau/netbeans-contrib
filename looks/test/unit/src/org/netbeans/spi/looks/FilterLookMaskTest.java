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

import java.beans.BeanInfo;

import org.openide.util.Lookup;
import org.netbeans.spi.looks.*;

import org.netbeans.junit.*;

public class FilterLookMaskTest extends NbTestCase {

    private static final String MSSG_UNEXPECTED_EXCEPTION =  "Unexpected exception caught : ";

    // Filter look to test on
    private Look filterLook;

    // Look which will be filtered
    private SampleLook sampleLook;

    // Represented object
    private SampleRepObject representedObject;

    // Golden values
    private GoldenValue[] goldenValues;
    
    // Results
    private GoldenValue[] results;
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite(FilterLookMaskTest.class);
        
        return suite;
    }
    
    // Methods of testCase -----------------------------------------------------
    
    public FilterLookMaskTest( String name ) {
        super( name );
    }
    
    protected void setUp() throws Exception {
        super.setUp();

        sampleLook = new SampleLook( "FilterLookTest" );
        //filter = Looks.filter( sampleLook, ProxyLook.ALL_METHODS );
        goldenValues = GoldenValue.createGoldenValues();
        representedObject = new SampleRepObject( goldenValues );
        
        
        // LookNode n = new LookNode (new Object (), sampleLook);
    }
    
    protected void tearDown() throws Exception {

        filterLook = null; 
        sampleLook = null;
        
        super.tearDown();
    }
      
    // Test methods ------------------------------------------------------------
    
    // Tests whether the constants defined in the right order and whether
    // there is no owerflow.
    /*
    public void testMaskOrder() {
        System.out.println("testMaskOrder");
        
        // Add your test code below by replacing the default call to fail.
        
        for ( int i = 0, j = 1; i < testingItems.length; i++ ) {
            if ( testingItems[i].mask != j ) {
                fail("Ordering failed : " + i );
            }
            j *= 2;
        }               
    }
     */
/*    
    public void test_ATTACH_TO() {
        System.out.println("test_ATTACH_TO");
        
        // Add your test code below by replacing the default call to fail.
        int failIndex = doTestingWithMask( ProxyLook.ATTACH_TO );
        if ( failIndex > -1 ) {
            fail( getFailMessage( failIndex) );
        }
    }
*/    

    public void test_GET_DISPLAY_NAME() {
        String message = doTestingWithMask( ProxyLook.GET_DISPLAY_NAME );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_NAME() {
        String message = doTestingWithMask( ProxyLook.GET_NAME );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_RENAME() {
        String message = doTestingWithMask( ProxyLook.RENAME );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_SHORT_DESCRIPTION () {
        String message = doTestingWithMask( ProxyLook.GET_SHORT_DESCRIPTION );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_ICON() {
        String message = doTestingWithMask( ProxyLook.GET_ICON );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_OPENED_ICON () {
        String message = doTestingWithMask( ProxyLook.GET_OPENED_ICON );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_HELP_CTX () {
        String message = doTestingWithMask( ProxyLook.GET_HELP_CTX );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_CHILD_OBJECTS () {
        String message = doTestingWithMask( ProxyLook.GET_CHILD_OBJECTS );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_NEW_TYPES () {
        String message = doTestingWithMask( ProxyLook.GET_NEW_TYPES );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_ACTIONS () {
        String message = doTestingWithMask( ProxyLook.GET_ACTIONS );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_CONTEXT_ACTIONS () {
        String message = doTestingWithMask( ProxyLook.GET_CONTEXT_ACTIONS );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_DEFAULT_ACTION () {
        String message = doTestingWithMask( ProxyLook.GET_DEFAULT_ACTION );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_PROPERTY_SETS () {
        String message = doTestingWithMask( ProxyLook.GET_PROPERTY_SETS );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_CUSTOMIZER () {
        String message = doTestingWithMask( ProxyLook.GET_CUSTOMIZER );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_CAN_RENAME () {
        String message = doTestingWithMask( ProxyLook.CAN_RENAME );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_CAN_DESTROY () {
        String message = doTestingWithMask( ProxyLook.CAN_DESTROY );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_CAN_COPY () {
        String message = doTestingWithMask( ProxyLook.CAN_COPY );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_CAN_CUT () {
        String message = doTestingWithMask( ProxyLook.CAN_CUT );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_PASTE_TYPES () {
        String message = doTestingWithMask( ProxyLook.GET_PASTE_TYPES );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_GET_DROP_TYPE () {
        String message = doTestingWithMask( ProxyLook.GET_DROP_TYPE );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_CLIPBOARD_COPY () {
        String message = doTestingWithMask( ProxyLook.CLIPBOARD_COPY );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_CLIPBOARD_CUT () {
        String message = doTestingWithMask( ProxyLook.CLIPBOARD_CUT );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_DRAG () {
        String message = doTestingWithMask( ProxyLook.DRAG );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    public void test_DESTROY () {
        String message = doTestingWithMask( ProxyLook.DESTROY );
        assertTrue( message == null ? "" : message, message == null );
    }
    
    // Private methods ---------------------------------------------------------    
    
    private String doTestingWithMask( long mask ) {
        filterLook = Looks.filter( "TestFilter", sampleLook, mask );
        
        try {
            results = readResults();
        }
        catch ( Exception e ) {
            return MSSG_UNEXPECTED_EXCEPTION;
        }
        
        for( int i = 0; i < goldenValues.length; i ++ ) {
            
            if ( goldenValues[i].key == ProxyLook.GET_LOOKUP_ITEMS ) {
                continue;
            }
            
            Object result = GoldenValue.get( goldenValues[i].key, results );
            
            // Wee need to replace Boolean.FALSE with null
            // to test properly. Notice that because of this the golden
            // values in SampleRepObject should return true from all
            // methods of return type boolean            
            if ( result instanceof Boolean && Boolean.FALSE.equals( result ) ) {
                result = null;
            } 
            
            if ( ( ( goldenValues[i].key & mask ) > 0 && result == null ) || 
                 ( ( goldenValues[i].key & mask ) == 0 && result != null )) {
                return getFailMessage( i );
            }
        }
        
        return null;
    }

    private static final Lookup NO_LOOKUP = null; // PENDING

    private GoldenValue[] readResults() throws Exception {
         
        return new GoldenValue[] {

            new GoldenValue(
                ProxyLook.GET_DISPLAY_NAME,
                filterLook.getDisplayName( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_NAME,
                filterLook.getName( representedObject, NO_LOOKUP ) ),

            // Set name                

            new GoldenValue(
                ProxyLook.GET_SHORT_DESCRIPTION,
                filterLook.getShortDescription( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_ICON,
                filterLook.getIcon( representedObject, BeanInfo.ICON_COLOR_16x16, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_OPENED_ICON,
                filterLook.getOpenedIcon( representedObject, BeanInfo.ICON_COLOR_16x16, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_HELP_CTX,
                filterLook.getHelpCtx( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_CHILD_OBJECTS,
                filterLook.getChildObjects( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_NEW_TYPES,
                filterLook.getNewTypes( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_ACTIONS,
                filterLook.getActions( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_CONTEXT_ACTIONS,
                filterLook.getContextActions( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_DEFAULT_ACTION,
                filterLook.getDefaultAction( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_PROPERTY_SETS,
                filterLook.getPropertySets( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_CUSTOMIZER,
                filterLook.getCustomizer( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.HAS_CUSTOMIZER,
                new Boolean( filterLook.hasCustomizer( representedObject, NO_LOOKUP ) ) ),

            new GoldenValue(
                ProxyLook.CAN_RENAME,
                new Boolean( filterLook.canRename( representedObject, NO_LOOKUP ) ) ),

            new GoldenValue(
                ProxyLook.CAN_DESTROY,
                new Boolean( filterLook.canDestroy( representedObject, NO_LOOKUP ) ) ),

            new GoldenValue(
                ProxyLook.CAN_COPY,
                new Boolean( filterLook.canCopy( representedObject, NO_LOOKUP ) ) ),

            new GoldenValue(
                ProxyLook.CAN_CUT,
                new Boolean( filterLook.canCut( representedObject, NO_LOOKUP ) ) ),

            new GoldenValue(
                ProxyLook.GET_PASTE_TYPES,
                filterLook.getPasteTypes( representedObject, null, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.GET_DROP_TYPE,
                filterLook.getDropType( representedObject, null, 0, 0, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.CLIPBOARD_COPY,
                filterLook.clipboardCopy( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.CLIPBOARD_CUT,
                filterLook.clipboardCut( representedObject, NO_LOOKUP ) ),

            new GoldenValue(
                ProxyLook.DRAG,
                filterLook.drag( representedObject, NO_LOOKUP ) )

             // destroy               
        };
    }
    
    private String getFailMessage( int failIndex ) {
        return "Failed on method index : " + failIndex + " :  " +
            results[ failIndex ].key +  " = " +
            results[ failIndex ].result;
    }
           
}
