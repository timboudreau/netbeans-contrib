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
 * IRUnionMemberKey.java
 *
 * Created on September 19, 2000, 9:50 AM
 */

package org.netbeans.modules.corba.browser.ir.nodes.keys;

import org.omg.CORBA.UnionMember;

/**
 *
 * @author  tzezula
 * @version
 */
public class IRUnionMemberKey extends IRAbstractKey implements Cloneable {

    private UnionMember _mbr;

    /** Creates new IRUnionMemberKey */
    public IRUnionMemberKey(UnionMember mbr) {
        this._mbr = mbr;
    }

    public boolean equals (Object other){
        if (! (other instanceof IRUnionMemberKey))
            return false;
        if (! _mbr.name.equals(((IRUnionMemberKey)other)._mbr.name))
            return false;
        return true;
    }

    public int hashCode(){
        return this._mbr.name.hashCode();
    }

    public Object clone () throws CloneNotSupportedException {
        return super.clone();
    }
    
    public String getName () {
        return this._mbr.name;
    }
    
    public UnionMember getValue () {
        return this._mbr;
    }

}
