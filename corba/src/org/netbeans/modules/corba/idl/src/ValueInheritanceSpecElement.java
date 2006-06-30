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

package org.netbeans.modules.corba.idl.src;

import java.util.Vector;

public class ValueInheritanceSpecElement extends IDLElement {

    private Vector supported_interfaces;
    private Vector inherited_values;
    private boolean truncatable;

    public ValueInheritanceSpecElement(int id) {
        super(id);
        truncatable = false;
    }

    public ValueInheritanceSpecElement(IDLParser p, int id) {
        super(p, id);
        truncatable = false;
    }

    public void setTruncatable (boolean value) {
        truncatable = value;
    }

    public boolean getTruncatable () {
        return truncatable;
    }

    public void setInterfaces (Vector value) {
        supported_interfaces = value;
    }

    public Vector getInterfaces () {
        return supported_interfaces;
    }

    public void setValues (Vector value) {
        inherited_values = value;
    }

    public Vector getValues () {
        return inherited_values;
    }



}
