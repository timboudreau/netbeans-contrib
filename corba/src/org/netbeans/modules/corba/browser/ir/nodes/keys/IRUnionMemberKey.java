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
