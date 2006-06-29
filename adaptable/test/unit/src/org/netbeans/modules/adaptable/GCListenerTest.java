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

package org.netbeans.modules.adaptable;

import java.lang.ref.WeakReference;
import java.util.TooManyListenersException;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adaptable.AdaptableEvent;
import org.netbeans.api.adaptable.AdaptableListener;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.Singletonizer;
import org.netbeans.spi.adaptable.SingletonizerListener;

/**
 *
 * @author jarda
 */
public class GCListenerTest extends NbTestCase {
    
    /** Creates a new instance of GCListenerTest */
    public GCListenerTest(String s) {
        super(s);
    }


    public void testListenerIsKeptEvenIfItIsWeak() {
        Object rep = new Object();

        Implementation i = new Implementation();
        Adaptor adaptor = Adaptors.singletonizer(new Class[] { Runnable.class }, i);

        Adaptable a = adaptor.getAdaptable(rep);

        class L implements AdaptableListener {
            public void stateChanged(AdaptableEvent e) {
            }
        }
        L l = new L();
        WeakReference w = new WeakReference(l);

        a.addAdaptableListener(l);
        l = null;

        try {
            assertGC("Cannot gc at all", w);
        } catch (Throwable t) {
            // ok
            return;
        }

        fail("Cannot gc the listener: " + w);
    }

    private static class Implementation implements Singletonizer {
        public SingletonizerListener listener;

        public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) {
            return null;
        }

        public void addSingletonizerListener(SingletonizerListener listener) throws TooManyListenersException {
            if (this.listener != null) throw new TooManyListenersException ();
            this.listener = listener;
        }

        public void removeSingletonizerListener(SingletonizerListener listener) {
            if (this.listener == listener) {
                this.listener = null;
            }
        }

        public boolean isEnabled(Object obj, Class c) {
            return true;
        }
    } // end of Implementation

}
