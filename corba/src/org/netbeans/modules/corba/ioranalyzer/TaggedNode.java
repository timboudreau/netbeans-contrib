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

/*
 * TaggedNode.java
 *
 * Created on November 7, 2000, 3:54 PM
 */

package org.netbeans.modules.corba.ioranalyzer;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.util.*;

/**
 *
 * @author  tzezula
 * @version 
 */
public class TaggedNode extends AbstractNode {
    
    private static final String ICON_BASE = "org/netbeans/modules/corba/ioranalyzer/resources/taggedprofile";
    private static final char[] table = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    private int index;
    private IORTaggedProfile profile;
    

    /** Creates new TaggedNode */
    public TaggedNode(int index, IORTaggedProfile profile) {
        super (Children.LEAF);
        this.index = index;
        this.profile = profile;
        this.setIconBase (ICON_BASE);
    }
    
    public String getName () {
        return this.getDisplayName();
    }
    
    public String getDisplayName () {
        return NbBundle.getBundle(TaggedNode.class).getString("TXT_Profile") + " " + Integer.toString(this.index);
    }
    
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(TaggedNode.class).getString("TXT_ProfileP"), String.class, NbBundle.getBundle(TaggedNode.class).getString("TXT_ProfileP"), NbBundle.getBundle(TaggedNode.class).getString("TIP_ProfileP")) {
            public Object getValue () {
                return Integer.toString (profile.getTag());
            }
        });
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(TaggedNode.class).getString("TXT_ProfileData"), String.class, NbBundle.getBundle(TaggedNode.class).getString("TXT_ProfileData"), NbBundle.getBundle(TaggedNode.class).getString("TIP_ProfileData")) {
            public Object getValue () {
                StringBuffer buffer = new StringBuffer();
                byte[] data = profile.getData();
                for (int i=0; i< data.length; i++) {
                    if (data[i]<0x20) 
                        buffer.append ("\\"+toHexStr(data[i]));
                    else
                        buffer.append((char)data[i]);
                }
                return buffer.toString();
            }
            
            private String toHexStr (byte value) {
                char[] res = new char[4];
                res[0]='0';
                res[1]='x';
                res[2]=table[((value>>4)&0xf)];
                res[3]=table[(value&0xf)];
                return new String (res);
            }
        });
        return s;
    }
    

}
