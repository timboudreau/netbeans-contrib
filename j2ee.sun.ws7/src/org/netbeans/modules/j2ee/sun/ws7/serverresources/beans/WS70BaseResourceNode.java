/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
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
