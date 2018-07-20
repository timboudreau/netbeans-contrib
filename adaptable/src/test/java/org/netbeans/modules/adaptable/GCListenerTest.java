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
