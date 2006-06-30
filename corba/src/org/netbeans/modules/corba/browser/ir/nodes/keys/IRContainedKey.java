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

package org.netbeans.modules.corba.browser.ir.nodes.keys;

import org.omg.CORBA.Contained;


public class IRContainedKey extends IRAbstractKey implements Cloneable{

    public Contained contained;
    // To improve the eficiency, by decreasing remote operations
    // cash the RepositoryId in private field;
    private String id;

    /** Creates new IRContainedKey */
    public IRContainedKey(Contained contained) {
        this.contained = contained;
    }

    /** Object.equals()
     */
    public boolean equals (Object other){
        if (other== null || !(other instanceof IRContainedKey))
            return false;
        if (!(this.getId().equals(((IRContainedKey)other).getId())))
            return false;
        return true;
    }

    /** Object.hashCode()
     */
    public int hashCode () {
        return this.getId().hashCode();
    }

    /** Returns the Repository Id of contained
     */
    private String getId () {
        if (this.id == null)
            this.id = contained.id();
        return this.id;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
