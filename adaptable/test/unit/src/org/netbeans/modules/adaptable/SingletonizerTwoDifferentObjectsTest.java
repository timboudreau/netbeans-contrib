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

package org.netbeans.modules.adaptable;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.TooManyListenersException;

import org.netbeans.api.adaptable.*;
import org.netbeans.spi.adaptable.*;


/** Another Singletonizer behaviour test.`
 *
 * @author Jaroslav Tulach
 */
public class SingletonizerTwoDifferentObjectsTest extends org.netbeans.junit.NbTestCase {
    public SingletonizerTwoDifferentObjectsTest(java.lang.String testName) {
        super(testName);
    }
    
    protected void setUp () throws Exception {
        super.setUp ();
    }
    
    /** Subclassable method to create an Adaptors.singletonizer
     */
    protected Adaptor createSingletonizer (Class[] supported, Singletonizer impl, Initializer listenerInit) {
        return Adaptors.singletonizer (supported, impl, null, listenerInit, null, null);
    }

    
    public void testButtonBehaviour() throws Exception {
        final int cnt = 5;
        
        
        Class[] supported = { Runnable.class };
        
        Implementation runImpl = new Implementation ();
        Adaptor provider = createSingletonizer (supported, runImpl, runImpl);

        Button b1 = new Button();
        Button b2 = new Button();
        b2.setFocusable(false);
        
        Adaptable adaptable1 = provider.getAdaptable (b1);
        Adaptable adaptable2 = provider.getAdaptable (b2);


        SingletonizerLifeCycleTest.Listener l1 = new SingletonizerLifeCycleTest.Listener(adaptable1);
        SingletonizerLifeCycleTest.Listener l2 = new SingletonizerLifeCycleTest.Listener(adaptable2);

        Runnable r;

        r = adaptable1.lookup(Runnable.class);
        assertNotNull("1st is enabled", r);
        r.run();


        r = adaptable2.lookup(Runnable.class);
        assertNull("2nd is not enabled", r);

        b2.setFocusable(true);

        l2.assertCount("One change in listener", 1);

        r = adaptable1.lookup(Runnable.class);
        assertNotNull("2nd is now enabled", r);
        r.run();

    }
    
    /** Implementation of singletonizer */
    protected static final class Implementation
    implements Singletonizer,  Initializer, PropertyChangeListener {
        public SingletonizerListener listener;

        public boolean isEnabled (Object obj, Class c) {
            Button b = (Button)obj;
            return b.isFocusable();
        }

        public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) {
            Button b = (Button)obj;
            if (b.isEnabled()) {
                for (ActionListener l : b.getActionListeners()) {
                    l.actionPerformed(new ActionEvent(b, 0, b.getActionCommand()));
                }
            }
            return null;
        }

        public void addSingletonizerListener (SingletonizerListener listener) throws TooManyListenersException {
            if (this.listener != null) throw new TooManyListenersException ();
            this.listener = listener;
        }

        public void removeSingletonizerListener (SingletonizerListener listener) {
            if (this.listener == listener) {
                this.listener = null;
            }
        }

        public void initialize(Object representedObject) {
            Button b = (Button)representedObject;
            b.addPropertyChangeListener(this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            Button b = (Button)evt.getSource();
            if ("focusable".equals(evt.getPropertyName())) {
                listener.stateChanged(SingletonizerEvent.anObjectChanged(this, b));
            }
        }
        
    } // end of Implementation
}
