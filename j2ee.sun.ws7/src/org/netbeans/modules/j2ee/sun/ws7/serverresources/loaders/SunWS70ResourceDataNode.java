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
