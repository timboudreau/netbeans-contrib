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


    /** Creates new CosNamingDetails */
    public CosNamingDetails () {
    }

    public CosNamingDetails(Node node, String name, String kind) {
        this.node = node;
        this.name = name;
        this.kind = kind;
    }

}
