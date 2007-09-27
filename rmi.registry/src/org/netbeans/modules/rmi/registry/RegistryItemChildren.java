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

package org.netbeans.modules.rmi.registry;

import java.beans.*;
import java.util.*;

import org.openide.nodes.*;
import org.openide.util.WeakListeners;

/**
 * [PENDING] removing listener from item needs improving
 * it should be rather called when node is discarded.
 * @author  mryzl
 */

public class RegistryItemChildren extends Children.Keys {

    private RegistryItem item;

    /** Keep this reference because it is weak listener. */
    private PropertyChangeListener listener;

    /** Creates new RegistryItemChildren. */
    public RegistryItemChildren(RegistryItem item) {
        this.item = item;
        
        // weak listener, it is not necessary to unregister
    }
    
    protected void addNotify() {
        Collection keys = item.getServices();
        if (keys != null) {
            setKeys(keys);
            if (listener == null) {
                listener = new ChildrenListener();
                item.addPropertyChangeListener(WeakListeners.propertyChange(listener, item));
            }
        } else {
            RMIRegistryNode node = (RMIRegistryNode) getNode().getParentNode().getCookie(RMIRegistryNode.class);
            node.refreshItem(item);
        }
    }
    
    protected void removeNotify() {
        if (listener != null) {
            item.removePropertyChangeListener(listener);
            listener = null;
        }
    }

    /** Proxy to setKeys.
    */
    protected void setKeys2(Collection keysSet) {
        super.setKeys(keysSet);
    }

    protected Node[] createNodes(Object key) {
        Class clazz = ((ServiceItem) key).getServiceClass();
        Node node;
        if (clazz != null) {
            node = new ServiceNode((ServiceItem) key, new ServiceChildren(clazz.getInterfaces()));
        } else {
            node = new ServiceNode((ServiceItem) key);
        }
        return new Node[] { node };
    }

    private class ChildrenListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            // huh, zmena
            // jestli je nova hodnota null, tak se odregistruj, jinak se updatni
            Collection services;
            if ((services = item.getServices()) != null) setKeys2(services);
            else {
                RMIRegistryNode node = (RMIRegistryNode) getNode().getParentNode().getCookie(RMIRegistryNode.class);
                node.refreshItem(item);
            }
        }
    }
}
