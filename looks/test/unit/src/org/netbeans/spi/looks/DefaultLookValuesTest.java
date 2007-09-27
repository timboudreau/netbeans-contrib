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
