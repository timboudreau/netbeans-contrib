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
import org.netbeans.api.adaptable.Facets.Identity;
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
        this.l.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, o, Identity.class));
        
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
