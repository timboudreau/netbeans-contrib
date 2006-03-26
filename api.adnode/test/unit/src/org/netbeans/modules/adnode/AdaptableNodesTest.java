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

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Method;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import junit.framework.*;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.Singletonizer;
import org.netbeans.api.adaptable.info.Identity;
import org.netbeans.spi.adaptable.SingletonizerEvent;
import org.netbeans.spi.adaptable.SingletonizerListener;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author Jaroslav Tulach
 */
public class AdaptableNodesTest extends TestCase 
implements org.netbeans.spi.adaptable.Singletonizer, NodeListener {
    private SingletonizerListener l;

    private String name;

    private int cnt;
    
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
        Adaptor a = Adaptors.singletonizer (new Class[] { Identity.class }, this);
        class ToString {
            String name = "Nothing";
            
            public String toString() {
                return name;
            }
        }
        ToString o = new ToString();
        
        Node result = AdaptableNodes.create(a, o);
        
        assertEquals("Name is toString", "Nothing", result.getName ());
        assertNotNull("We have a listener", this.l);
        
        result.addNodeListener(this);
        
        o.name = "New";
        this.l.stateChanged(SingletonizerEvent.anObjectChanged(this, o));
        
        assertEquals("One change in the node", 1, cnt);
        assertEquals("Name changed", Node.PROP_NAME, name);
        
        assertEquals("Name is toString", "New", result.getName ());
        
        
    }
    
    //
    // Singletonizer interface
    // 

    public boolean isEnabled (Object obj, Class c) {
        return true;
    }

    public Object invoke (Object obj, Method method, Object[] args) throws Throwable {
        return obj.toString();
    }

    public synchronized void addSingletonizerListener (SingletonizerListener listener) throws TooManyListenersException {
        assertNull("We support just one listener", this.l);
        this.l = listener;
    }

    public synchronized void removeSingletonizerListener (SingletonizerListener listener) {
        assertEquals("We can remove just the registered listener", this.l, listener);
        this.l = null;
    }

    public void childrenAdded(NodeMemberEvent ev) {
    }

    public void childrenRemoved(NodeMemberEvent ev) {
    }

    public void childrenReordered(NodeReorderEvent ev) {
    }

    public void nodeDestroyed(NodeEvent ev) {
    }

    public void propertyChange(PropertyChangeEvent evt) {
        name = evt.getPropertyName();
        cnt++;
    }
    
}
