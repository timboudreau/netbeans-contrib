/*
 * ExtAttribute.java
 *
 * Created on May 29, 2002, 2:44 PM
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
