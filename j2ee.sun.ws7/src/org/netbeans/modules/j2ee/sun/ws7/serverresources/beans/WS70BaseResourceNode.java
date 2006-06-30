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
/*
 * WS70BaseResourceNode.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.beans;

import org.openide.nodes.Children;
import org.openide.loaders.DataNode;
import org.openide.actions.PropertiesAction;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.loaders.SunWS70ResourceDataObject;

import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70Resources;

/**
 * Code reused from Appserver common API module
 */
public abstract class WS70BaseResourceNode extends DataNode implements java.beans.PropertyChangeListener {
    
                
    /**
     * Creates a new instance of WS70BaseResourceNode
     */
    public WS70BaseResourceNode(SunWS70ResourceDataObject obj) {
        super(obj, Children.LEAF);
    }
    
    public javax.swing.Action getPreferredAction(){
        return SystemAction.get(PropertiesAction.class);
    }
    
    protected SunWS70ResourceDataObject getSunWS70ResourceDataObject() {
        return (SunWS70ResourceDataObject)getDataObject();
    }
    
    abstract public WS70Resources getBeanGraph();
    
    abstract public void propertyChange(java.beans.PropertyChangeEvent evt);
    
}
