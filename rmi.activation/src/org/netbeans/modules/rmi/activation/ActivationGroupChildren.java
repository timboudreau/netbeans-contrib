/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi.activation;

import java.util.*;

import org.openide.nodes.*;

/**
 * Children of ActivationGroupNode.
 * @author  Jan Pokorsky
 * @version 
 */
public class ActivationGroupChildren extends org.openide.nodes.Children.Keys
                        implements java.beans.PropertyChangeListener {

    private ActivationGroupItem aiGroup;

    public ActivationGroupChildren (ActivationGroupItem aiGroup) {
        this.aiGroup = aiGroup;
    }

    protected void addNotify() {
        setKeysImpl(aiGroup.getActivatables());
        aiGroup.addPropertyChangeListener(this);
    }

    protected void removeNotify() {
        aiGroup.removePropertyChangeListener(this);
        setKeys(java.util.Collections.EMPTY_SET);
    }

    private void setKeysImpl(Collection c) {
        List l = new ArrayList(c);
        Collections.sort(l);
        setKeys(l);
    }

    protected org.openide.nodes.Node[] createNodes(java.lang.Object key) {
        return new Node[] { new ActivationObjectNode((ActivationObjectItem) key) };
    }

    public void propertyChange(java.beans.PropertyChangeEvent pce) {
        if (ActivationGroupItem.PROP_ACTIVATABLES.equals(pce.getPropertyName()))
            setKeysImpl(aiGroup.getActivatables());
    }
    
}
