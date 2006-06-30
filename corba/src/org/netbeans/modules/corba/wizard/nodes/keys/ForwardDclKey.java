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

package org.netbeans.modules.corba.wizard.nodes.keys;

/**
 *
 * @author  tzezula
 * @version
 */
public class ForwardDclKey extends NamedKey {

    private boolean intf;

    /** Creates new FrowardDclKey */
    public ForwardDclKey(int kind, String name, boolean intf) {
        super (kind, name);
        this.intf = intf;
    }

    public void setInterface (boolean intf) {
        this.intf = intf;
    }

    public boolean isInterface () {
        return intf;
    }

    public int hashCode () {
        return this.kind();
    }
    
    public boolean equals (Object other) {
        if (!(other instanceof ForwardDclKey))
            return false;
        if (((ForwardDclKey)other).kind() != this.kind())
            return false;
        if (!((ForwardDclKey)other).getName().equals(this.getName()))
            return false;
        if (((ForwardDclKey)other).isInterface() != this.isInterface())
            return false;
        return true;
    }

}
