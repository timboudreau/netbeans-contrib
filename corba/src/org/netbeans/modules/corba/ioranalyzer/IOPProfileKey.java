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

package org.netbeans.modules.corba.ioranalyzer;

/**
 *
 * @author  tzezula
 * @version 
 */
public class IOPProfileKey extends ProfileKey {

    public IORProfile value;
    
    /** Creates new IOPProfileKey */
    public IOPProfileKey(int index, IORProfile profile) {
        super (index);
        this.value = profile;
    }
    
    
    public boolean equals (Object other) {
        if (!(other instanceof IOPProfileKey))
            return false;
        return value.equals (((IOPProfileKey)other).value);
    }
    
    public int hashCode () {
        return index;
    }

}
