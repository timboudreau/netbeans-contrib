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

import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.util.NbBundle;

public class IORNode extends DataNode implements Node.Cookie {

    public static final String ICON_BASE = "org/netbeans/modules/corba/ioranalyzer/resources/ior";

    public IORNode (IORDataObject dataObject) {
	super (dataObject, new ProfileChildren(dataObject));
	this.setIconBase (ICON_BASE);
        this.getCookieSet().add (this);
    }
    
    
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(IORNode.class).getString("TITLE_Name"),String.class,NbBundle.getBundle(IORNode.class).getString("TITLE_Name"),NbBundle.getBundle(IORNode.class).getString("TIP_Name")) {
            public Object getValue () {
                return IORNode.this.getName();
            }
        });
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(IORNode.class).getString("TITLE_Endian"),String.class,NbBundle.getBundle(IORNode.class).getString("TITLE_Endian"),NbBundle.getBundle(IORNode.class).getString("TIP_Endian")) {
            public Object getValue () {
                ProfileChildren cld = (ProfileChildren) IORNode.this.getChildren();
                Boolean res = cld.isLittleEndian();
                if (res == null)
                    return "";
                else if (res.booleanValue()) 
                    return NbBundle.getBundle(IORNode.class).getString ("TXT_Little");
                else
                    return NbBundle.getBundle(IORNode.class).getString ("TXT_Big");
            }
        });
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(IORNode.class).getString("TITLE_RepositoryId"),String.class,NbBundle.getBundle(IORNode.class).getString("TITLE_RepositoryId"),NbBundle.getBundle(IORNode.class).getString("TIP_RepositoryId")) {
            public Object getValue () {
                String rid =  ((ProfileChildren)IORNode.this.getChildren()).getRepositoryId();
                if (rid == null)
                    return "";
                else 
                    return rid;
            }
        });
        return s;
    }
    

}