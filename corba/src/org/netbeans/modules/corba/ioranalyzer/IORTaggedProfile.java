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

public class IORTaggedProfile {

    private int tag;
    private byte[] data;

    public IORTaggedProfile (int tag, byte[] data) {
	this.tag = tag;
        this.data = data;
    }
    
    public String toString () {
	String res = "Tag= "+tag+"\n";
	res = res + new String (data,0,0,data.length);
	return res;
    }
    
    public int getTag () {
	return tag;
    }
    
    public byte[] getData () {
	return data;
    }
    
    public boolean equals (Object other) {
        if (!(other instanceof IORTaggedProfile))
            return false;
        if (tag != ((IORTaggedProfile)other).tag)
            return false;
        if (this.data.length != ((IORTaggedProfile)other).data.length)
            return false;
        for (int i=0; i<data.length; i++) {
            if (data[i] != ((IORTaggedProfile)other).data[i])
                return false;
        }
        return true;
    }

}