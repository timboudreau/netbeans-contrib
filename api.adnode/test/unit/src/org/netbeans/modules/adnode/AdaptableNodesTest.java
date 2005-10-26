/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.adnode;

import java.lang.reflect.Method;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeListener;
import junit.framework.*;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.Singletonizer;
import org.netbeans.spi.adnode.Name;
import org.openide.nodes.Node;

/**
 *
 * @author Jaroslav Tulach
 */
public class AdaptableNodesTest extends TestCase 
implements org.netbeans.spi.adaptable.Singletonizer {
    
    public AdaptableNodesTest (String testName) {
        super (testName);
    }

    protected void setUp () throws Exception {
    }

    protected void tearDown () throws Exception {
    }

    public static Test suite () {
        TestSuite suite = new TestSuite(AdaptableNodesTest.class);
        
        return suite;
    }

    public void testCreate () {
        Adaptor a = Adaptors.singletonizer (new Class[] { Name.class }, this);
        Object o = "Nothing";
        
        Node result = AdaptableNodes.create(a, o);
        
        assertEquals("Name is toString", "Nothing", result.getName ());
    }
    
    //
    // Singletonizer interface
    // 

    public boolean isEnabled (Class c) {
        return true;
    }

    public Object invoke (Object obj, Method method, Object[] args) throws Throwable {
        return obj.toString();
    }

    public void addChangeListener (ChangeListener listener) throws TooManyListenersException {
    }

    public void removeChangeListener (ChangeListener listener) {
    }
    
}
