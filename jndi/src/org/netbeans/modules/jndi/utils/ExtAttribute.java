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
