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
