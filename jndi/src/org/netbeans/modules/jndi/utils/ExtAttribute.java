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


package org.netbeans.modules.jndi.utils;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.Attribute;
/**
 *
 * @author  Tomas Zezula
 */
public class ExtAttribute extends BasicAttribute {

    /** Creates a new instance of ExtAttribute */
    public ExtAttribute() {
        super (null);
    }

    public ExtAttribute (Attribute ba) throws NamingException {
        super (ba.getID());
        this.ordered = ba.isOrdered();
        for (int i=0; i< ba.size(); i++) {
            this.add (ba.get(i));
        }
    }
    
    public void setID (String id) {
        this.attrID = id;
    }
    
}
