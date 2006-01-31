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

package org.netbeans.modules.j2ee.sun.ws7.serverresources.loaders;

import org.openide.loaders.DataNode;
import org.openide.nodes.Children;

/** A node to represent this object.
 * Code reused from Appserver common API module 
 * 
 */
public class SunWS70ResourceDataNode extends DataNode {
    
    public SunWS70ResourceDataNode(SunWS70ResourceDataObject obj) {
        this(obj, Children.LEAF);
    }
    
    public SunWS70ResourceDataNode(SunWS70ResourceDataObject obj, Children ch) {
        super(obj, ch);
        setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/ResNodeNodeIcon.gif"); //NOI18N
    }
    
    protected SunWS70ResourceDataObject getWS70SunResourceDataObject() {        
        return (SunWS70ResourceDataObject)getDataObject();
    }
    

}
