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
 * ProfileNode.java
 *
 * Created on November 7, 2000, 1:58 PM
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
public class ProfileNode extends AbstractNode {
    
    private static final String ICON_BASE = "org/netbeans/modules/corba/ioranalyzer/resources/iopprofile";
    private static final char[] table = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    
    private int index;
    private IORProfile profile;

    /** Creates new ProfileNode */
    public ProfileNode(int index, IORProfile profile) {
        super (Children.LEAF);
        this.setIconBase (ICON_BASE);
        this.index = index;
        this.profile = profile;
    }
    
    public String getName() {
        return this.getDisplayName();
    }
    
    public String getDisplayName () {
        return NbBundle.getBundle(ProfileNode.class).getString ("TXT_Profile") + " " + index;
    }
    
    
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(ProfileNode.class).getString("TXT_Hostname"), String.class, NbBundle.getBundle(ProfileNode.class).getString("TXT_Hostname"), NbBundle.getBundle(ProfileNode.class).getString("TIP_Hostname")){
            public Object getValue () {
                return profile.getHostname();
            }
        });
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(ProfileNode.class).getString("TXT_Port"), String.class, NbBundle.getBundle(ProfileNode.class).getString("TXT_Port"), NbBundle.getBundle(ProfileNode.class).getString("TIP_Port")){
            public Object getValue () {
                short port = profile.getPort();
                //Convert to unsigned integer
                int uport = (65535 & (int) port);
                return Integer.toString (uport);
            }
        });
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(ProfileNode.class).getString("TXT_IIOPVer"), String.class, NbBundle.getBundle(ProfileNode.class).getString("TXT_IIOPVer"), NbBundle.getBundle(ProfileNode.class).getString("TIP_IIOPVer")) {
            public Object getValue () {
                return Integer.toString (profile.getMajor())+"."+Integer.toString (profile.getMinor());
            }
        });
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(ProfileNode.class).getString("TXT_ProfileP"), String.class, NbBundle.getBundle(ProfileNode.class).getString("TXT_ProfileP"), NbBundle.getBundle(ProfileNode.class).getString("TIP_ProfileP")){
            public Object getValue () {
                return "INTERNET_IOP";
            }
        });
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(ProfileNode.class).getString("TXT_ObjectKey"), String.class,NbBundle.getBundle(ProfileNode.class).getString("TXT_ObjectKey"),NbBundle.getBundle(ProfileNode.class).getString("TIP_ObjectKey")) {
            
            public Object getValue () {
                StringBuffer buffer = new StringBuffer();
                byte[] objectKey = profile.getObjectKey();
                for (int i=0; i< objectKey.length; i++) {
                    if (objectKey[i]<0x20) 
                        buffer.append ("\\"+toHexStr(objectKey[i]));
                    else if (objectKey[i]=='\\')
                        buffer.append ("\\\\");
                    else
                        buffer.append((char)objectKey[i]);
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
