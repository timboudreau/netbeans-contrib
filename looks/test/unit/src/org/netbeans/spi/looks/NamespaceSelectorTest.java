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
