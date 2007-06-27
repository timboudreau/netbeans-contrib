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
package org.netbeans.modules.sfsexplorer;

import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
class ServiceImplNode extends AbstractNode {
    private Action[] actions;

    /**
     * 
     * @param serviceImpl 
     */
    ServiceImplNode(String serviceImpl) {
        super(Children.LEAF);
        setDisplayName(serviceImpl);
        setIconBaseWithExtension("org/netbeans/modules/sfsexplorer/provider.gif");
    }

    /**
     * 
     * @param context 
     * @return 
     */
    public Action[] getActions(boolean context) {
        if (!context) {
            if (actions == null) {
                actions = new Action[]{};
            }
            return actions;
        }
        return SFSBrowserTopComponent.EMPTY_ACTIONS;
    }
}