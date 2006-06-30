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
