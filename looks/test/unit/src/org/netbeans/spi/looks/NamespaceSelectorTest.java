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
import org.netbeans.spi.looks.*;

import org.netbeans.junit.*;
import java.beans.PropertyChangeListener;

/**
 *
 * @author  Jaroslav Tulach
 */
public class NamespaceSelectorTest extends NbTestCase {

    public NamespaceSelectorTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(new NbTestSuite (NamespaceSelectorTest.class));
    }

    /** For object an object is found.
     */
    public void testObjectIsFound () {
        
        Enumeration en = org.netbeans.modules.looks.TypesSearch.namesForClass( new Object ().getClass() );
        assertClass ("Object is found", Object.class, en.nextElement ());
        assertTrue ("No more items", !en.hasMoreElements());

    }
    
    /** Test that the default namespace look works correctly on a hierarchy 
     * of objects.
     */
    public void testInterfaceBeforeObject () {
        
        class O extends Object implements Runnable {
            public void run () {};
        }
        
        Enumeration en = org.netbeans.modules.looks.TypesSearch.namesForClass( new O ().getClass() );
        assertClass ("Actual class is allways first", O.class, en.nextElement ());
        assertClass ("Interface takes precedence", Runnable.class, en.nextElement());
        assertClass ("Object is the last fallback", Object.class, en.nextElement ());
        assertTrue ("No more items", !en.hasMoreElements());
        
    }
    
    /** All interfaces are introspected.
     */
    public void testAllInterfacesAreChecked () {
        // Action is a interface that extends another interface
        class O extends Object implements javax.swing.Action {
            public void actionPerformed (java.awt.event.ActionEvent ev) {}
            public Object getValue (String s) { return null; }
            public void putValue (String s, Object o) {}
            public void setEnabled (boolean b) {}
            public boolean isEnabled () { return false; }
            public void addPropertyChangeListener (PropertyChangeListener l) {}
            public void removePropertyChangeListener (PropertyChangeListener l) {}
        }
        
        Enumeration en = org.netbeans.modules.looks.TypesSearch.namesForClass( new O ().getClass());
        assertClass ("Actual class is always first", O.class, en.nextElement ());
        assertClass ("It implements Action interface", javax.swing.Action.class, en.nextElement ());
        assertClass ("And the interfaces extends ActionListener", java.awt.event.ActionListener.class, en.nextElement ());
        assertClass ("ActionListener is a listener", java.util.EventListener.class, en.nextElement ());
        assertClass ("Last is as usually Object", Object.class, en.nextElement ());
        assertTrue ("And we are done", !en.hasMoreElements ());
    }
        
    /** Asserts name of class with a name in a namespace
     */
    private static final void assertClass (String txt, Class c, Object obj) {
        assertEquals (txt, c.getName ().replace ('.', '/'), obj);
    }
    
    
    /** Innerclass to get access to namesFor method */
    /*
    private static final class L implements NamespaceLookProvider {
                       
        public Enumeration names (Object obj) {
            return namesFor (obj);
        }
        
        public void addChangeListener(javax.swing.event.ChangeListener listener) throws TooManyListenersException {
        }
        
        public Object getKeyForObject(Object representedObject) {
        }
        
        public Enumeration namesForKey(Object obj) {
        }
        
    }
    */
}
