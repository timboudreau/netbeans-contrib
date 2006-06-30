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
 * IRInitializerKey.java
 *
 * Created on August 28, 2000, 8:01 PM
 */

package org.netbeans.modules.corba.browser.ir.nodes.keys;

import org.omg.CORBA.*;
/**
 *
 * @author  tzezula
 * @version
 */
public class IRInitializerKey extends IRAbstractKey {

    public Initializer initializer;

    /** Creates new IRInitializerKey */
    public IRInitializerKey(Initializer initializer) {
        this.initializer = initializer;
    }


    public boolean equals (java.lang.Object other) {
        if ( ! (other instanceof IRInitializerKey))
            return false;
        IRInitializerKey otherInit = (IRInitializerKey) other;
        if (! this.initializer.name.equals(otherInit.initializer.name))
            return false;
        if (this.initializer.members.length != otherInit.initializer.members.length)
            return false;
        for (int i=0; i< this.initializer.members.length; i++) {
            if (!this.initializer.members[i].name.equals (otherInit.initializer.members[i].name))
                return false;
            if (!this.initializer.members[i].type.equal (otherInit.initializer.members[i].type))
                return false;
        }
        return true;
    }
    
    public int hashCode () {
        return ( this.initializer.name.hashCode() ^ this.initializer.members.length);
    }

}
