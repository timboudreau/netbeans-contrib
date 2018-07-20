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
