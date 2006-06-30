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

import org.omg.CORBA.IDLType;
import org.omg.CORBA.Any;


public class IRTypeCodeKey extends IRAbstractKey implements Cloneable {

    public String name;
    public IDLType type;
    public Any label;



    /** Creates new IRTypeCodeKey */
    public IRTypeCodeKey(String name, IDLType type, Any label){
        this.name = name;
        this.type = type;
        this.label = label;
    }

    public IRTypeCodeKey(String name, IDLType type) {
        this (name,type,null);
    }

    public IRTypeCodeKey (String name){
        this (name,null,null);
    }


    public boolean equals (Object other){
        if (! (other instanceof IRTypeCodeKey))
            return false;
        if (! name.equals(((IRTypeCodeKey)other).name))
            return false;
        return true;
    }

    public int hashCode(){
        return this.name.hashCode();
    }

    public Object clone () throws CloneNotSupportedException {
        return super.clone();
    }


}
