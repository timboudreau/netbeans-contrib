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

import java.lang.reflect.Array;
import java.util.*;

import org.openide.nodes.*;
import org.netbeans.junit.*;

import org.netbeans.spi.looks.*;
import org.netbeans.api.nodes2looks.LookNodeValuesTest;

/** Tests whether all vales returned from a Node are identical with
 * the values server by associated look
 */
public class CompositeLookValuesTest extends TestBaseValues {

    // Golden values for the three looks which will be composed

    private GoldenValue goldenValues[][];
    private static GoldenValue gvForTypes[] = GoldenValue.createGoldenValues();

    // Methods of testCase -----------------------------------------------------

    public CompositeLookValuesTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite( CompositeLookValuesTest.class );
        return suite;
    }

    protected void setUp() throws Exception {

        goldenValues = new GoldenValue[][] {
            createGoldenValues( 1 ),
            createGoldenValues( 2 ),
            createGoldenValues( 3 )
        };

        GoldenValue[] resultValues = mergeGoldenValues( goldenValues );

        Look look1 = new SampleLook( "CL1", goldenValues[0] );
        Look look2 = new SampleLook( "CL2", goldenValues[1] );
        Look look3 = new SampleLook( "CL3", goldenValues[2] );
        Look compositeLook = Looks.composite( "Composite", new Look[] { look1, look2, look3 } );

        // LookSelector selector = new SampleSelector( look );
        // SampleRepObject ro = new SampleRepObject( null );
        // LookNode node = new LookNode( ro, composite, selector );

        setTarget( compositeLook, new SampleRepObject(), 3 );
        setGoldenValues( resultValues );

        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    // Test methods ------------------------------------------------------------

    ///////////////////////////////////////////////////////
    //                                                   //
    //  Most methods are inherited from: TestBaseValues  //
    //                                                   //
    ///////////////////////////////////////////////////////


    // Private methods ---------------------------------------------------------

    public static GoldenValue[] createGoldenValues( int index ) {

        GoldenValue goldenValues[] = null;

        switch ( index ) {
            case 1:
                goldenValues = new GoldenValue[] {

                    new GoldenValue( ProxyLook.GET_DISPLAY_NAME,
                        "DisplayName_1" ),

                    new GoldenValue( ProxyLook.GET_NAME, null ),

                    new GoldenValue( ProxyLook.GET_SHORT_DESCRIPTION, "ShortDescription_1" ),

                    new GoldenValue( ProxyLook.GET_ICON,
                        new java.awt.image.BufferedImage( 16, 16, java.awt.image.BufferedImage.TYPE_INT_RGB ) ),

                    new GoldenValue( ProxyLook.GET_OPENED_ICON, null ),

                    new GoldenValue( ProxyLook.GET_HELP_CTX, null ),

                    new GoldenValue( ProxyLook.GET_CHILD_OBJECTS,
                        Arrays.asList( new String[] {
                            "Child 1",
                            "Child 2"
                        } ) ),

                    new GoldenValue( ProxyLook.GET_NEW_TYPES,
                        new org.openide.util.datatransfer.NewType[] {
                            new org.openide.util.datatransfer.NewType() {
                                public void create() {  }
                            },

                            new org.openide.util.datatransfer.NewType() {
                                public void create() {  }
                            }
                        } ),

                    new GoldenValue( ProxyLook.GET_ACTIONS, null ),


                    new GoldenValue( ProxyLook.GET_CONTEXT_ACTIONS,
                        new org.openide.util.actions.SystemAction[] {
                            (org.openide.util.actions.SystemAction)org.openide.util.SharedClassObject.findObject( GoldenValue.TestingAction3.class )
                        } ),

                    new GoldenValue( ProxyLook.GET_DEFAULT_ACTION,
                        org.openide.util.actions.SystemAction.get( GoldenValue.TestingAction5.class  ) ),

                    new GoldenValue( ProxyLook.GET_PROPERTY_SETS,
                        new Node.PropertySet[] {
                            new Sheet.Set(),
                            new Sheet.Set()
                        }),

                    new GoldenValue( ProxyLook.GET_CUSTOMIZER,
                        null ),

                    new GoldenValue( ProxyLook.HAS_CUSTOMIZER,
                        Boolean.FALSE ),

                    new GoldenValue( ProxyLook.CAN_RENAME,
                        Boolean.TRUE ),

                    new GoldenValue( ProxyLook.CAN_DESTROY,
                        Boolean.FALSE ),

                    new GoldenValue( ProxyLook.CAN_COPY,
                        Boolean.FALSE ),

                    new GoldenValue( ProxyLook.CAN_CUT,
                        Boolean.TRUE ),

                    new GoldenValue( ProxyLook.GET_PASTE_TYPES, null ),

                    new GoldenValue( ProxyLook.GET_DROP_TYPE, null ),

                    new GoldenValue( ProxyLook.CLIPBOARD_COPY, null ),

                    new GoldenValue( ProxyLook.CLIPBOARD_CUT,
                        new java.awt.datatransfer.StringSelection( "ClipboardCut" ) ),

                    new GoldenValue( ProxyLook.DRAG, null ),

                    new GoldenValue( ProxyLook.GET_LOOKUP_ITEMS, createGoldenLookupItems( index ) )

                };

                // We need to patch PropertySets
                Node.PropertySet[] sets = (Node.PropertySet[])GoldenValue.get( ProxyLook.GET_PROPERTY_SETS, goldenValues );
                sets[0].setName( "Set_1" );
                sets[1].setName( "Set_2" );

                break;
            case 2:
                goldenValues = new GoldenValue[] {
                    new GoldenValue( ProxyLook.GET_DISPLAY_NAME, null ),

                    new GoldenValue( ProxyLook.GET_NAME,
                        "Name 2" ),

                    new GoldenValue( ProxyLook.GET_SHORT_DESCRIPTION, null ),

                    new GoldenValue( ProxyLook.GET_ICON, null ),

                    new GoldenValue( ProxyLook.GET_OPENED_ICON,
                        new java.awt.image.BufferedImage( 16, 16, java.awt.image.BufferedImage.TYPE_INT_RGB ) ),

                    new GoldenValue( ProxyLook.GET_HELP_CTX, null ),

                    new GoldenValue( ProxyLook.GET_CHILD_OBJECTS,
                        Arrays.asList( new String[] {
                            "Child 3",
                            "Child 4"
                        } ) ),

                    new GoldenValue( ProxyLook.GET_NEW_TYPES, null ),

                    new GoldenValue( ProxyLook.GET_ACTIONS,
                        new org.openide.util.actions.SystemAction[] {
                            (org.openide.util.actions.SystemAction)org.openide.util.SharedClassObject.findObject( GoldenValue.TestingAction1.class )
                        } ),

                    new GoldenValue( ProxyLook.GET_CONTEXT_ACTIONS,
                        new org.openide.util.actions.SystemAction[] {
                            (org.openide.util.actions.SystemAction)org.openide.util.SharedClassObject.findObject( GoldenValue.TestingAction4.class )
                        } ),

                    new GoldenValue( ProxyLook.GET_DEFAULT_ACTION, null ),

                    new GoldenValue( ProxyLook.GET_PROPERTY_SETS,
                        new Node.PropertySet[] {
                            new Sheet.Set(),
                            new Sheet.Set()
                        }),

                    new GoldenValue( ProxyLook.GET_CUSTOMIZER,
                        new javax.swing.JPanel() ),

                    new GoldenValue( ProxyLook.HAS_CUSTOMIZER,
                        Boolean.FALSE ),

                    new GoldenValue( ProxyLook.CAN_RENAME,
                        Boolean.FALSE ),

                    new GoldenValue( ProxyLook.CAN_DESTROY,
                        Boolean.TRUE ),

                    new GoldenValue( ProxyLook.CAN_COPY,
                        Boolean.FALSE ),

                    new GoldenValue( ProxyLook.CAN_CUT,
                        Boolean.FALSE ),

                    new GoldenValue( ProxyLook.GET_PASTE_TYPES,
                        new org.openide.util.datatransfer.PasteType[] {
                            new org.openide.util.datatransfer.PasteType() {
                                public java.awt.datatransfer.Transferable paste() { return null; }
                            },

                            new org.openide.util.datatransfer.PasteType() {
                                public java.awt.datatransfer.Transferable paste() { return null; }
                            }
                        }),

                    new GoldenValue( ProxyLook.GET_DROP_TYPE,
                        new org.openide.util.datatransfer.PasteType() {
                            public java.awt.datatransfer.Transferable paste() { return null; }
                        }),

                    new GoldenValue( ProxyLook.CLIPBOARD_COPY,  null ),

                    new GoldenValue( ProxyLook.CLIPBOARD_CUT, null ),

                    new GoldenValue( ProxyLook.DRAG,
                        new java.awt.datatransfer.StringSelection( "Drag" ) ),

                    new GoldenValue( ProxyLook.GET_LOOKUP_ITEMS, createGoldenLookupItems( index ) )
                    };

                // We need to patch PropertySets
                sets = (Node.PropertySet[])GoldenValue.get( ProxyLook.GET_PROPERTY_SETS, goldenValues );
                sets[0].setName( "Set_3" );
                sets[1].setName( "Set_4" );
                break;
            default:
                goldenValues = new GoldenValue[] {
                    new GoldenValue( ProxyLook.GET_DISPLAY_NAME, null ),

                    new GoldenValue( ProxyLook.GET_NAME, null ),

                    new GoldenValue( ProxyLook.GET_SHORT_DESCRIPTION,
                        "ShortDescription_3" ),

                    new GoldenValue( ProxyLook.GET_ICON, null ),

                    new GoldenValue( ProxyLook.GET_OPENED_ICON, null ),

                    new GoldenValue( ProxyLook.GET_HELP_CTX,
                        new org.openide.util.HelpCtx( LookNodeValuesTest.class ) ),

                    new GoldenValue( ProxyLook.GET_CHILD_OBJECTS, null ),

                    new GoldenValue( ProxyLook.GET_NEW_TYPES,
                        new org.openide.util.datatransfer.NewType[] {
                            new org.openide.util.datatransfer.NewType() {
                                public void create() {  }
                            },

                            new org.openide.util.datatransfer.NewType() {
                                public void create() {  }
                            }
                        } ),

                    new GoldenValue( ProxyLook.GET_ACTIONS,
                        new org.openide.util.actions.SystemAction[] {
                            (org.openide.util.actions.SystemAction)org.openide.util.SharedClassObject.findObject( GoldenValue.TestingAction2.class )
                        } ),

                    new GoldenValue( ProxyLook.GET_CONTEXT_ACTIONS, null ),

                    new GoldenValue( ProxyLook.GET_DEFAULT_ACTION, null ),

                    new GoldenValue( ProxyLook.GET_PROPERTY_SETS, null ),

                    new GoldenValue( ProxyLook.GET_CUSTOMIZER, null ),

                    new GoldenValue( ProxyLook.HAS_CUSTOMIZER,
                        Boolean.TRUE ),

                    new GoldenValue( ProxyLook.CAN_RENAME,
                        Boolean.FALSE ),

                    new GoldenValue( ProxyLook.CAN_DESTROY,
                        Boolean.FALSE ),

                    new GoldenValue( ProxyLook.CAN_COPY,
                        Boolean.TRUE ),

                    new GoldenValue( ProxyLook.CAN_CUT,
                        Boolean.FALSE ),

                    new GoldenValue( ProxyLook.GET_PASTE_TYPES,
                        new org.openide.util.datatransfer.PasteType[] {
                            new org.openide.util.datatransfer.PasteType() {
                                public java.awt.datatransfer.Transferable paste() { return null; }
                            },

                            new org.openide.util.datatransfer.PasteType() {
                                public java.awt.datatransfer.Transferable paste() { return null; }
                            }
                        }),

                    new GoldenValue( ProxyLook.GET_DROP_TYPE,
                        new org.openide.util.datatransfer.PasteType() {
                            public java.awt.datatransfer.Transferable paste() { return null; }
                        }),

                    new GoldenValue( ProxyLook.CLIPBOARD_COPY,
                        new java.awt.datatransfer.StringSelection( "ClipboardCopy" ) ),

                    new GoldenValue( ProxyLook.CLIPBOARD_CUT,
                        new java.awt.datatransfer.StringSelection( "ClipboardCut" ) ),

                    new GoldenValue( ProxyLook.DRAG,
                        new java.awt.datatransfer.StringSelection( "Drag" ) ),

                    new GoldenValue( ProxyLook.GET_LOOKUP_ITEMS, createGoldenLookupItems( index ) )
                };
            break;
        }
        return goldenValues;
    }

    /** Creates lookup for testing
     */
    public static Collection createGoldenLookupItems( int index ) {

        ArrayList ic;

        switch ( index ) {
            case 1:
                ic = new ArrayList();
                ic.add(
                    new GoldenValue.TestLookupItem(
                        new org.openide.cookies.SaveCookie() {
                            public void save() {}
                        }
                    )
                );
                ic.add( new GoldenValue.TestLookupItem( new javax.swing.JPanel() ) );
                return ic;
            case 2:
                ic = new ArrayList();
                ic.add(
                    new GoldenValue.TestLookupItem(
                        new org.openide.cookies.CloseCookie() {
                            public boolean close() { return false; }
                        }
                    )
                );
                ic.add( new GoldenValue.TestLookupItem( "HoHo" ) );
                return ic;
            default:
                return null;
        }
    }


    /** Merges golden values together.
     *
     */
    public static GoldenValue[] mergeGoldenValues( GoldenValue gv[][] ) {

        GoldenValue result[] = new GoldenValue[ gv[0].length ];

        for( int i = 0; i < gv[0].length; i++ ) {

            Class clazz = GoldenValue.get( gv[0][i].key, gvForTypes ).getClass();
            boolean isArray = clazz.isArray();


            if ( List.class.isAssignableFrom( clazz ) ) {  // Merge lists
                ArrayList list = new ArrayList();
                for( int j = 0; j < gv.length; j++ ) {
                    List gvList = (List)GoldenValue.get( gv[0][i].key, gv[j] );

                    if ( gvList != null ) {
                        list.addAll( gvList );
                    }
                }

                result[i] = new GoldenValue( gv[0][i].key, list );

            }
            else if ( isArray ) {
                // We need to merge the arrays
                ArrayList list = new ArrayList();
                for( int j = 0; j < gv.length; j++ ) {
                    Object gvArray[] = (Object[])GoldenValue.get( gv[0][i].key, gv[j] );

                    if ( gvArray != null ) {
                        list.addAll( Arrays.asList(gvArray) );
                    }
                }

                result[i] = new GoldenValue( gv[0][i].key,
                        list.toArray( (Object[])Array.newInstance( clazz.getComponentType(), 0 ) ) );

            }
            else {  // Well, single valued property we only need to find first
                Object resultValue =  null;
                for( int j = 0; j < gv.length; j++ ) {
                    Object goldenValue = GoldenValue.get( gv[0][i].key, gv[j] );

                    if ( goldenValue instanceof Boolean ) {
                        if ( Boolean.TRUE.equals( goldenValue ) ) {
                            resultValue = Boolean.TRUE;
                            break;
                        }
                        else {
                            resultValue = Boolean.FALSE;
                        }
                    }
                    else if ( goldenValue != null ) {
                        resultValue = goldenValue;
                        break;
                    }
                }

                result[i] = new GoldenValue( gv[0][i].key, resultValue );

            }
        }


        return result;
    }

}


