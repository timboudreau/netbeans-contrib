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
public class TaggedProfileKey extends ProfileKey {

    public IORTaggedProfile value;
    /** Creates new TaggedProfileKey */
    public TaggedProfileKey(int index, IORTaggedProfile profile) {
        super (index);
        this.value = profile;
    }
    
    public boolean equals (Object other) {
        if (!(other instanceof TaggedProfileKey))
            return false;
        return value.equals(((TaggedProfileKey)other).value);
    }
    
    public int hashCode () {
        return index;
    }

}
