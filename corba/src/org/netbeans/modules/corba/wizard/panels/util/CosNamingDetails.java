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

package org.netbeans.modules.corba.wizard.panels.util;

import org.openide.nodes.Node;
/**
 *
 * @author  tzezula
 * @version 
 */
public class CosNamingDetails extends Object {
    
    public Node node;
    public String name;
    public String kind;
    
    
    public CosNamingDetails () {
    }

    /** Creates new CosNamingDetails */
    public CosNamingDetails(Node node, String name, String kind) {
        this.node = node;
        this.name = name;
        this.kind = kind;
    }
    
    public CosNamingDetails (Node node) {
        this.node = node;
    }

}
