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

public class ConstElement extends IDLElement {

    String exp;
    String type;

    static final long serialVersionUID =2501064395128035802L;
    public ConstElement(int id) {
        super(id);
    }

    public ConstElement(IDLParser p, int id) {
        super(p, id);
    }

    public void setExpression (String e) {
        exp = e;
    }

    public String getExpression () {
        return exp;
    }

    public void setType (String t) {
        type = t;
    }

    public String getType () {
        return type;
    }

}

