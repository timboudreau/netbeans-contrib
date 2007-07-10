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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;

import javax.swing.JPanel;


import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;

import org.netbeans.spi.looks.ProxyLook;
import org.netbeans.api.nodes2looks.LookNodeValuesTest;

/** Class for comparing desirable data against real results. It also contains 
 * some testing values which can be used as test values.
 */    
public class GoldenValue {
    
    long key;
    Object result;

    public GoldenValue(long key, Object result) {
        this.key = key;
        this.result = result;
    }

    public static boolean isOK( long key, Object result, GoldenValue[] items) {
        for( int i = 0; i < items.length; i++ ) {
            if ( key == items[i].key ) {
                if ( result == items[i].result ) {
                    return true;
                }
                else if ( result == null ) {
                    return false;
                }
                else if ( result.equals( items[i].result ) ){
                    return true;
                }
                else if ( key == ProxyLook.GET_LOOKUP_ITEMS ) {
                    return compareLookupItems( (Collection)result, (Collection)items[i].result );
                }
                else if ( result.getClass().isArray() && 
                          items[i].result.getClass().isArray() &&
                          Arrays.equals( (Object[])result, (Object[])items[i].result ) ) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        return false;
    }
    
    public static Object get( long key, GoldenValue[] items) {
        for( int i = 0; i < items.length; i++ ) {
            if ( key == items[i].key ) {
                return items[i].result;
            }
        }

        return null;
    }
    
    // Private methods ---------------------------------------------------------
    
    private static boolean compareLookupItems( Collection c1, Collection c2 ) {
        if ( c1.size() != c2.size() ) {
            return false;
        }
        
        Iterator it1 = c1.iterator();
        Iterator it2 = c2.iterator();
        
        while( it1.hasNext() ) {
            Lookup.Item li1 = (Lookup.Item)it1.next();
            Lookup.Item li2 = (Lookup.Item)it2.next();
            
            if ( !li1.getDisplayName().equals( li2.getDisplayName() ) ) {
                return false;
            }
            
            if ( !li1.getId().equals( li2.getId() ) ) {
                return false;
            }
            
            if ( !li1.getInstance().equals( li2.getInstance() ) ) {
                return false;
            }
            
            if ( !li1.getType().equals( li2.getType() ) ) {
                return false;
            }
            
        }
        
        return true;
    }

    // This method can be used in other tests as well.
    public static GoldenValue[] createGoldenValues() {

        return new GoldenValue[] {
            new GoldenValue( ProxyLook.GET_DISPLAY_NAME,
                "DisplayName" ),

            new GoldenValue( ProxyLook.GET_NAME,
                "Name" ),

            new GoldenValue( ProxyLook.GET_SHORT_DESCRIPTION,
                "ShortDescription" ),

            new GoldenValue( ProxyLook.GET_ICON,
                new BufferedImage( 16, 16, BufferedImage.TYPE_INT_RGB ) ),

            new GoldenValue( ProxyLook.GET_OPENED_ICON,
                new BufferedImage( 16, 16, BufferedImage.TYPE_INT_RGB ) ),

            new GoldenValue( ProxyLook.GET_HELP_CTX,
                new HelpCtx( LookNodeValuesTest.class ) ),

            new GoldenValue( ProxyLook.GET_CHILD_OBJECTS,
                Arrays.asList( new String[] {
                    "Child 1",
                    "Child 2"
                }) ),

            new GoldenValue( ProxyLook.GET_NEW_TYPES,
                new NewType[] {
                    new NewType() {
                        public void create() {  }
                    },

                    new NewType() {
                        public void create() {  }
                    }
                } ),

            new GoldenValue( ProxyLook.GET_ACTIONS,
                new SystemAction[] {
                    (SystemAction)org.openide.util.SharedClassObject.findObject( TestingAction1.class ),
                    (SystemAction)org.openide.util.SharedClassObject.findObject( TestingAction2.class )
                } ),

            new GoldenValue( ProxyLook.GET_CONTEXT_ACTIONS,
                new SystemAction[] {
                    (SystemAction)org.openide.util.SharedClassObject.findObject( TestingAction3.class ),
                    (SystemAction)org.openide.util.SharedClassObject.findObject( TestingAction4.class )
                } ),

            new GoldenValue( ProxyLook.GET_DEFAULT_ACTION,
                SystemAction.get( TestingAction5.class  ) ),

            new GoldenValue( ProxyLook.GET_PROPERTY_SETS,
                new Node.PropertySet[] {
                    new Sheet.Set(),
                    new Sheet.Set()
                }),

            new GoldenValue( ProxyLook.GET_CUSTOMIZER,
                new JPanel() ),

            new GoldenValue( ProxyLook.HAS_CUSTOMIZER,
                Boolean.TRUE ),

            new GoldenValue( ProxyLook.CAN_RENAME,
                Boolean.TRUE ),

            new GoldenValue( ProxyLook.CAN_DESTROY,
                Boolean.TRUE ),

            new GoldenValue( ProxyLook.CAN_COPY,
                Boolean.TRUE ),

            new GoldenValue( ProxyLook.CAN_CUT,
                Boolean.TRUE ),

            new GoldenValue( ProxyLook.GET_PASTE_TYPES,
                new PasteType[] {
                    new PasteType() {
                        public Transferable paste() { return null; }
                    },

                    new PasteType() {
                        public Transferable paste() { return null; }
                    }
                }),

            new GoldenValue( ProxyLook.GET_DROP_TYPE,
                new PasteType() {
                    public Transferable paste() { return null; }
                }),

            new GoldenValue( ProxyLook.CLIPBOARD_COPY,
                new StringSelection( "ClipboardCopy" ) ),

            new GoldenValue( ProxyLook.CLIPBOARD_CUT,
                new StringSelection( "ClipboardCut" ) ),

            new GoldenValue( ProxyLook.DRAG,
                new StringSelection( "Drag" ) ),

            new GoldenValue( ProxyLook.GET_LOOKUP_ITEMS,
                createGoldenLookupItems() )


        };
    }

    public static Collection createGoldenLookupItems() {

        ArrayList lookupItems = new ArrayList();

        lookupItems.add(
            new TestLookupItem(
                new org.openide.cookies.SaveCookie() {
                    public void save() {}
                    
                    public String toString() {
                        return "Tetst SaveCookie";
                    }
                }
            )
        );

        lookupItems.add(
            new TestLookupItem(
                new org.openide.cookies.CloseCookie() {
                    public boolean close() { return false; }
                    
                    public String toString() {
                        return "Tetst CloseCookie";
                    }
                }
            )
        );

        lookupItems.add( new TestLookupItem( "HoHo" ) );

        return lookupItems;
    }
    
    // Sample data -------------------------------------------------------------
                        
    public static class TestingAction extends CallableSystemAction {

        public HelpCtx getHelpCtx() {
            return null;
        }
        
        public String getName() {
            return this.getClass().getName();
        }
        
        public void performAction() {
        }
        
    }
    
    public static class TestingAction1 extends TestingAction {
    }
    
    public static class TestingAction2 extends TestingAction {
    }
    
    public static class TestingAction3 extends TestingAction {
    }
    
    public static class TestingAction4 extends TestingAction {
    }
    
    public static class TestingAction5 extends TestingAction {
    }
    
    public static class TestingAction6 extends TestingAction {
    }

    public static class TestLookupItem extends Lookup.Item {

        private String id;
        private Object instance;

        public TestLookupItem( Object instance ) {
            this( instance, null );
        }

        public TestLookupItem( Object instance, String id ) {
            this.id = id;
            this.instance = instance;
        }

        public String getDisplayName() {
            return getId();
        }

        public String getId() {
            return id == null ? instance.toString() : id;
        }

        public Object getInstance() {
            return instance;
        }

        public Class getType() {
            return instance.getClass();
        }
        
        public boolean equals( Object object ) {
            if ( object instanceof Lookup.Item ) {
                return instance == ((Lookup.Item)object).getInstance();
            }
            return false;
        }
        
        public int hashCode() {
            return instance.hashCode();
        }

    }

}
 