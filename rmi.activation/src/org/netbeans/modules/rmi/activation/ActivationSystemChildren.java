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

package org.netbeans.modules.rmi.activation;

import java.beans.*;
import java.util.*;

import org.openide.nodes.*;
import org.openide.util.*;

/** Children of ActivationSystemNode.
 *
 * @author  mryzl, Jan Pokorsky
 * @version
 */
public class ActivationSystemChildren extends org.openide.nodes.Children.Keys implements PropertyChangeListener {

    private ActivationSystemItem item;
    /** Weak reference to ActivationSystemChildren. */
    private PropertyChangeListener listener;

    /** Creates new ActivationSystemChildren */
    public ActivationSystemChildren(ActivationSystemItem item) {
        this.item = item;
    }

    public void addNotify() {
        setKeysImpl(item.getActivationGroupItems());
        listener = WeakListener.propertyChange(this, item);
        item.addPropertyChangeListener(listener);
    }
    
    public void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
        item.removePropertyChangeListener(listener);
        listener = null;
    }
    
    private void setKeysImpl(Collection col) {
        if (col == null) col = Collections.EMPTY_LIST;
        List l = new ArrayList(col);
        Collections.sort(l);
        setKeys(l);
    }
    
    
    
    /** Create nodes for a given key.
     * @param key the key
     * @return child nodes for this key
     */
    protected Node[] createNodes(Object key) {
        if (key instanceof ActivationObjectItem) {
            return new Node[] { new ActivationObjectNode((ActivationObjectItem) key) };
        }
        if (key instanceof ActivationGroupItem) {
            return new Node[] { new ActivationGroupNode((ActivationGroupItem) key) };
        }
        return new Node[] { new ActivationNode(Children.LEAF, (ActivationItem) key) };
    }
    
    /** Activation items changed.
     * @param pce event
     */
    public void propertyChange(java.beans.PropertyChangeEvent pce) {
        if (ActivationSystemItem.PROP_ACTIVATION_ITEMS.equals(pce.getPropertyName())) {
            setKeysImpl(item.getActivationGroupItems());
        }
    }
    
}
